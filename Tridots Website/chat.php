<?php
session_start();
// For demonstration purposes, let's assume the user is logged in.
if (!isset($_SESSION['user_id'])) {
    $_SESSION['user_id'] = 1; // Example: logged-in user ID
}

// These would be dynamic based on the ad being viewed
$user_id = $_SESSION['user_id'];
$seller_id = isset($_GET['seller']) ? intval($_GET['seller']) : 2; 
$ad_id = isset($_GET['ad']) ? intval($_GET['ad']) : 101; 

// --- Mock Seller Data (fetch from your DB in a real app) ---
$seller_info = [
    'name' => 'John Doe',
    'profile_picture' => 'https://i.pravatar.cc/100?u=seller2',
];
?>
<!DOCTYPE html>
<html lang="en">
<head>
    <meta charset="UTF-8">
    <meta name="viewport" content="width=device-width, initial-scale=1.0">
    <title>Marketplace - Viewing Ad</title>
    <script src="https://www.gstatic.com/firebasejs/8.10.0/firebase-app.js"></script>
    <script src="https://www.gstatic.com/firebasejs/8.10.0/firebase-database.js"></script>
    <style>
        /* --- Page Styles (for context) --- */
        body {
            font-family: -apple-system, BlinkMacSystemFont, "Segoe UI", Roboto, Helvetica, Arial, sans-serif;
            background-color: #f0f2f5;
            color: #333;
            line-height: 1.6;
            margin: 0;
            padding: 20px;
            padding-bottom: 100px; /* Space for chat widget */
        }
        .container { max-width: 960px; margin: auto; }
        .product-card { background: #fff; padding: 2em; border-radius: 10px; box-shadow: 0 4px 15px rgba(0,0,0,0.05); }
        h1, h2 { color: #1d1d1f; }
        .button { background-color: #007bff; color: white; padding: 10px 20px; text-decoration: none; border-radius: 8px; display: inline-block; }

        /* --- Chat Widget Styles --- */
        .chat-widget {
            position: fixed;
            bottom: 20px;
            right: 20px;
            width: 370px;
            max-width: 90vw;
            height: 500px;
            max-height: 80vh;
            background: #ffffff;
            border-radius: 15px;
            box-shadow: 0 10px 30px rgba(0,0,0,0.15);
            display: flex;
            flex-direction: column;
            overflow: hidden;
            transform: translateY(20px);
            opacity: 0;
            animation: slide-up 0.5s ease-out forwards;
            z-index: 1000;
        }

        @keyframes slide-up {
            to {
                transform: translateY(0);
                opacity: 1;
            }
        }

        .chat-widget-header {
            padding: 15px 20px;
            background: #007bff;
            color: white;
            display: flex;
            align-items: center;
            justify-content: space-between;
        }
        .chat-widget-header .seller-info {
            display: flex;
            align-items: center;
        }
        .chat-widget-header img {
            width: 40px;
            height: 40px;
            border-radius: 50%;
            margin-right: 15px;
            border: 2px solid rgba(255,255,255,0.5);
        }
        .chat-widget-header .seller-name {
            font-weight: 600;
        }
        .chat-widget-header .close-btn {
            background: none; border: none; color: white; font-size: 24px; cursor: pointer; opacity: 0.8;
        }
        .chat-widget-header .close-btn:hover { opacity: 1; }

        .chatbox {
            flex-grow: 1;
            padding: 20px;
            overflow-y: auto;
            display: flex;
            flex-direction: column;
        }

        /* --- Empty Chat State --- */
        .empty-chat-state {
            display: flex;
            flex-direction: column;
            justify-content: center;
            align-items: center;
            height: 100%;
            text-align: center;
            color: #888;
            padding: 20px;
        }
        .empty-chat-state svg {
            width: 50px;
            height: 50px;
            margin-bottom: 15px;
            stroke: #ccc;
        }
        .empty-chat-state h4 { margin: 0 0 5px 0; color: #555; }

        /* --- Message Bubbles --- */
        .message { display: flex; margin-bottom: 15px; max-width: 80%; }
        .message-bubble { padding: 10px 15px; border-radius: 18px; line-height: 1.5; }
        .message-time { font-size: 0.75em; color: #999; margin-top: 4px; }
        
        .message.received { align-self: flex-start; }
        .message.received .message-bubble { background-color: #f1f0f0; border-top-left-radius: 5px; }
        
        .message.sent { align-self: flex-end; }
        .message.sent .message-bubble { background-color: #007bff; color: white; border-top-right-radius: 5px; }
        .message.sent .message-time { text-align: right; color: #e0e0e0; }

        /* --- Chat Input --- */
        .chat-input-form {
            display: flex;
            padding: 10px 15px;
            border-top: 1px solid #e0e0e0;
        }
        #chatInput {
            flex-grow: 1;
            border: none;
            background: #f1f0f0;
            border-radius: 20px;
            padding: 10px 15px;
            font-size: 1em;
        }
        #chatInput:focus { outline: none; }
        #sendButton {
            background: none; border: none; color: #007bff; font-size: 24px; padding: 0 10px; margin-left: 5px; cursor: pointer;
        }
    </style>
</head>
<body>

    <div class="container">
        <h1>Amazing Product</h1>
        <div class="product-card">
            <p>This is a great product you might want to buy. If you have questions, feel free to chat with the seller.</p>
            <p>The chat window will automatically open for you on the bottom right.</p>
            <a href="#" class="button">Buy Now</a>
        </div>
    </div>

    <div class="chat-widget" id="chatWidget">
        <header class="chat-widget-header">
            <div class="seller-info">
                <img src="<?php echo htmlspecialchars($seller_info['profile_picture']); ?>" alt="Seller">
                <span class="seller-name"><?php echo htmlspecialchars($seller_info['name']); ?></span>
            </div>
            <button class="close-btn" onclick="closeChat()">&times;</button>
        </header>

        <div class="chatbox" id="chatbox">
            </div>
        
        <form class="chat-input-form" id="chatForm">
            <input type="text" id="chatInput" required autocomplete="off" placeholder="Type a message...">
            <button type="submit" id="sendButton">
                <svg xmlns="http://www.w3.org/2000/svg" width="24" height="24" viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="2" stroke-linecap="round" stroke-linejoin="round"><line x1="22" y1="2" x2="11" y2="13"></line><polygon points="22 2 15 22 11 13 2 9 22 2"></polygon></svg>
            </button>
        </form>
    </div>

<script>
// --- Firebase Configuration ---
var firebaseConfig = {
    // IMPORTANT: Replace with your actual Firebase config values
    apiKey: "YOUR_API_KEY",
    authDomain: "YOUR_AUTH_DOMAIN",
    databaseURL: "https://tridots-49c37-default-rtdb.firebaseio.com/", // Keep your DB URL
    projectId: "YOUR_PROJECT_ID",
    storageBucket: "YOUR_STORAGE_BUCKET",
    messagingSenderId: "YOUR_MESSAGING_SENDER_ID",
    appId: "YOUR_APP_ID"
};

// --- Initialize Firebase ---
if (!firebase.apps.length) {
    firebase.initializeApp(firebaseConfig);
}

// --- PHP to JS Variables ---
const userId = "<?php echo $user_id; ?>";
const sellerId = "<?php echo $seller_id; ?>";
const adId = "<?php echo $ad_id; ?>";

// --- Database References ---
const chatId = `ad${adId}_user${userId}_seller${sellerId}`;
const db = firebase.database();
const chatRef = db.ref('chats/' + chatId);

// --- DOM Elements ---
const chatbox = document.getElementById('chatbox');
const chatForm = document.getElementById('chatForm');
const chatInput = document.getElementById('chatInput');

// --- Functions ---
const formatTimestamp = (timestamp) => {
    if (!timestamp) return '';
    const date = new Date(timestamp);
    return date.toLocaleTimeString([], { hour: '2-digit', minute: '2-digit' });
};

const displayMessage = (msg) => {
    const isSent = msg.sender == userId;
    const messageDiv = document.createElement('div');
    messageDiv.classList.add('message', isSent ? 'sent' : 'received');
    
    messageDiv.innerHTML = `
        <div class="message-bubble">
            ${msg.text}
            <div class="message-time">${formatTimestamp(msg.timestamp)}</div>
        </div>
    `;
    
    chatbox.appendChild(messageDiv);
    chatbox.scrollTop = chatbox.scrollHeight;
};

const showEmptyState = () => {
    chatbox.innerHTML = `
        <div class="empty-chat-state">
            <svg xmlns="http://www.w3.org/2000/svg" width="24" height="24" viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="1.5" stroke-linecap="round" stroke-linejoin="round"><path d="M21 15a2 2 0 0 1-2 2H7l-4 4V5a2 2 0 0 1 2-2h14a2 2 0 0 1 2 2z"></path></svg>
            <h4>No messages yet</h4>
            <p>Start a conversation through the marketplace to get answers about this ad.</p>
        </div>
    `;
};

const loadChat = () => {
    // First, check if any messages exist for this chat
    chatRef.once('value', (snapshot) => {
        if (snapshot.exists()) {
            // Chat history exists, load all messages
            chatRef.orderByChild('timestamp').on('child_added', (msgSnapshot) => {
                displayMessage(msgSnapshot.val());
            });
        } else {
            // No chat history, show the empty state
            showEmptyState();
            // Also listen for the very first message to clear the empty state
            chatRef.orderByChild('timestamp').on('child_added', (msgSnapshot) => {
                chatbox.innerHTML = ''; // Clear the empty state message
                displayMessage(msgSnapshot.val());
                // Detach the listener after the first message to avoid re-clearing
                chatRef.orderByChild('timestamp').off('child_added'); 
                // Re-attach a normal listener
                chatRef.orderByChild('timestamp').on('child_added', (newMsgSnapshot) => {
                    displayMessage(newMsgSnapshot.val());
                });
            });
        }
    });
};

// --- Event Listeners & Initial Load ---
chatForm.onsubmit = (e) => {
    e.preventDefault();
    const text = chatInput.value.trim();
    if (text) {
        chatRef.push({
            sender: userId,
            text: text,
            timestamp: firebase.database.ServerValue.TIMESTAMP
        });
        chatInput.value = "";
    }
};

const closeChat = () => {
    const widget = document.getElementById('chatWidget');
    widget.style.display = 'none';
    // You could also add a "Open Chat" button on the page to make it reappear
}

// Start the chat loading process when the page loads
loadChat();

</script>
</body>
</html>