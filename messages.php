<!DOCTYPE html>
<html lang="en">
<head>
    <meta charset="UTF-8">
    <meta name="viewport" content="width=device-width, initial-scale=1.0">
    <title>Mobile Chat Guide</title>
    <!-- Tailwind CSS CDN -->
    <script src="https://cdn.tailwindcss.com"></script>
    <link href="https://fonts.googleapis.com/css2?family=Inter:wght@300;400;500;600;700&display=swap" rel="stylesheet">
    <style>
        body {
            font-family: 'Inter', sans-serif;
            background-color: #e2e8f0; /* Soft gray-blue background */
            display: flex;
            justify-content: center;
            align-items: center;
            min-height: 100vh; /* Ensure body takes full viewport height */
            margin: 0;
            padding: 10px; /* Reduced padding */
            box-sizing: border-box;
            overflow-y: auto; /* Allow vertical scrolling if content exceeds height, but try to avoid */
        }
        .container {
            background-color: #f8fafc; /* Off-white card background */
            border-radius: 1.5rem; /* Slightly less pronounced rounded corners */
            box-shadow: 0 10px 25px rgba(0, 0, 0, 0.1); /* Slightly softer shadow */
            padding: 1.5rem; /* Reduced padding */
            max-width: 500px; /* Reduced max-width */
            width: 100%;
            text-align: center;
            display: flex;
            flex-direction: column;
            align-items: center;
            gap: 1rem; /* Reduced space between elements */
            border: 1px solid #cbd5e0; /* Subtle border */
            max-height: 98vh; /* Limit container height to almost full viewport height */
            overflow-y: auto; /* Allow scrolling within the container if necessary */
            position: relative; /* Needed for absolute positioning of back button */
        }
        .qr-placeholder {
            width: 150px; /* Smaller QR code area */
            height: 150px;
            background-color: #e9ecef; /* Lighter background for QR */
            border-radius: 0.75rem; /* More rounded corners for QR */
            display: flex;
            justify-content: center;
            align-items: center;
            font-size: 0.8rem; /* Smaller text */
            color: #64748b; /* Muted gray text */
            margin-bottom: 1rem;
            object-fit: contain;
            border: 2px solid #a7d9b9; /* Border for QR placeholder */
        }
        .button-primary {
            background-color: #2ecc71; /* Vibrant green button */
            color: white;
            padding: 0.7rem 1.5rem; /* Reduced padding for button */
            border-radius: 0.75rem; /* More rounded corners */
            font-weight: 700; /* Bolder text */
            transition: background-color 0.3s ease, transform 0.2s ease;
            box-shadow: 0 5px 15px rgba(46, 204, 113, 0.3); /* Softer green shadow */
            letter-spacing: 0.03em; /* Slightly less letter spacing */
            text-transform: uppercase; /* Uppercase text */
            font-size: 0.9rem; /* Smaller font size */
        }
        .button-primary:hover {
            background-color: #27ae60; /* Darker green on hover */
            transform: translateY(-1px); /* Slight lift effect */
        }
        .message-box {
            background-color: #d1fae5; /* Light green background for messages */
            color: #047857; /* Dark green text */
            padding: 0.8rem; /* Reduced padding */
            border-radius: 0.75rem;
            margin-top: 1rem;
            display: none;
            font-weight: 600;
            width: 100%;
            border: 1px solid #34d399; /* Green border */
            box-shadow: 0 2px 8px rgba(0, 0, 0, 0.05); /* Subtle shadow for message box */
            font-size: 0.9rem; /* Smaller font size */
        }
        .logo {
            max-width: 120px; /* Smaller logo */
            height: auto;
            margin-bottom: 1.5rem; /* Reduced margin below logo */
            border-radius: 0.75rem; /* Rounded corners for logo */
            box-shadow: 0 3px 10px rgba(0, 0, 0, 0.05); /* Subtle shadow for logo */
        }
        h1 {
            color: #2d3748; /* Darker text for main heading */
            font-size: 2.5rem; /* Smaller main heading */
            margin-bottom: 0.5rem;
        }
        p {
            color: #4a5568; /* Muted text for paragraphs */
            font-size: 1rem; /* Smaller paragraph text */
            line-height: 1.5;
        }
        h2 {
            color: #2d3748; /* Darker text for subheadings */
            font-size: 1.75rem; /* Smaller subheadings */
            margin-bottom: 1rem;
        }
        ol li {
            color: #4a5568; /* Muted text for list items */
            font-size: 0.95rem; /* Smaller list item text */
            margin-bottom: 0.5rem;
        }
        ol li strong {
            color: #1a202c; /* Stronger color for bold text in lists */
        }
        a {
            color: #3b82f6; /* Blue for links */
            font-weight: 500;
            font-size: 0.85rem; /* Smaller link text */
        }
        a:hover {
            color: #2563eb; /* Darker blue on link hover */
        }
        /* Style for the back button */
        .back-button {
            position: absolute;
            top: 1.5rem;
            left: 1.5rem;
            background-color: #607d8b; /* A subtle gray for the back button */
            color: white;
            padding: 0.5rem 1rem;
            border-radius: 0.75rem;
            font-weight: 600;
            transition: background-color 0.3s ease;
            box-shadow: 0 2px 5px rgba(0, 0, 0, 0.2);
            font-size: 0.85rem;
            display: flex;
            align-items: center;
            gap: 0.25rem;
        }
        .back-button:hover {
            background-color: #455a64; /* Darker gray on hover */
        }
    </style>
</head>
<body>
    <div class="container">
        <!-- Back Button Added -->
        <button class="back-button" onclick="goToMarketplaceDashboard()">
            <svg xmlns="http://www.w3.org/2000/svg" width="16" height="16" fill="currentColor" viewBox="0 0 16 16">
                <path fill-rule="evenodd" d="M11.354 1.646a.5.5 0 0 1 0 .708L5.707 8l5.647 5.646a.5.5 0 0 1-.708.708l-6-6a.5.5 0 0 1 0-.708l6-6a.5.5 0 0 1 .708 0z"/>
            </svg>
            Back
        </button>

        <!-- Added Logo -->
        <img src="/Tridots/images/logo1.png" alt="Tridots Logo" class="logo">

        <h1 class="text-4xl font-bold text-gray-800 mb-2">Welcome to Our Chat!</h1>
        <p class="text-lg text-gray-600 mb-4">
            Follow these simple steps to start chatting in our mobile application.
        </p>

        <div class="text-left w-full">
            <h2 class="text-2xl font-semibold text-gray-700 mb-3">Using Chats in the Mobile App:</h2>
            <ol class="list-decimal list-inside text-gray-600 space-y-2">
                <li>
                    <strong>Download & Install:</strong> Ensure you have the latest version of our mobile app installed on your smartphone.
                </li>
                <li>
                    <strong>Log In/Sign Up:</strong> Open the app and log in with your credentials, or create a new account if you haven't already.
                </li>
                <li>
                    <strong>Navigate to Chats:</strong> Look for a "Chats," "Messages," or "Inbox" icon (often a speech bubble) in the navigation bar or main menu.
                </li>
                <li>
                    <strong>Start a New Chat:</strong> Tap on the "+" or "New Message" button to start a conversation with a contact or group.
                </li>
                <li>
                    <strong>Send Messages:</strong> Type your message in the text field and tap the "Send" button. You can also attach photos, videos, or files.
                </li>
            </ol>
        </div>

        <div class="mt-6 w-full flex flex-col items-center">
            <h2 class="text-2xl font-semibold text-gray-700 mb-3">Access Important Resources:</h2>
            <p class="text-gray-600 mb-4">
                Scan the QR code below or copy the link to access our shared Google Drive folder with additional resources.
            </p>
            <div class="qr-placeholder flex-shrink-0">
                <!-- Updated QR Code Image Source -->
                <img id="qrCodeImage" src="/Tridots/images/QR.png" alt="QR Code for Google Drive" class="rounded-lg object-contain w-full h-full" onerror="this.onerror=null;this.src='https://placehold.co/150x150/FF0000/FFFFFF?text=QR+Image+Missing';">
            </div>
            <a id="driveLink" href="https://drive.google.com/drive/folders/1nOxV2pj807bEvW5zmi-2at-wGs1riYsK?usp=sharing" target="_blank" class="text-blue-500 hover:underline text-sm mb-4">
                Direct Link to Google Drive Folder
            </a>
            <button id="copyLinkBtn" class="button-primary">
                Copy Link to Clipboard
            </button>
            <div id="messageBox" class="message-box"></div>
        </div>
    </div>

    <script>
        document.addEventListener('DOMContentLoaded', () => {
            const googleDriveLink = "https://drive.google.com/drive/folders/1nOxV2pj807bEvW5zmi-2at-wGs1riYsK?usp=sharing";
            const copyLinkBtn = document.getElementById('copyLinkBtn');
            const messageBox = document.getElementById('messageBox');
            const qrCodeImage = document.getElementById('qrCodeImage');

            copyLinkBtn.addEventListener('click', () => {
                const tempInput = document.createElement('input');
                tempInput.value = googleDriveLink;
                document.body.appendChild(tempInput);
                tempInput.select();
                tempInput.setSelectionRange(0, 99999);

                try {
                    document.execCommand('copy');
                    showMessage('Link copied to clipboard!', 'success');
                } catch (err) {
                    showMessage('Failed to copy link. Please copy it manually from the "Direct Link" below.', 'error');
                } finally {
                    document.body.removeChild(tempInput);
                }
            });

            /**
             * Displays a message in the message box.
             * @param {string} message - The message to display.
             * @param {string} type - The type of message ('success' or 'error') to apply styling.
             */
            function showMessage(message, type) {
                messageBox.textContent = message;
                messageBox.style.display = 'block';
                if (type === 'success') {
                    messageBox.style.backgroundColor = '#d1fae5'; /* Light green */
                    messageBox.style.color = '#047857'; /* Dark green */
                    messageBox.style.borderColor = '#34d399'; /* Green border */
                } else if (type === 'error') {
                    messageBox.style.backgroundColor = '#fee2e2'; /* Light red */
                    messageBox.style.color = '#991b1b'; /* Dark red */
                    messageBox.style.borderColor = '#ef4444'; /* Red border */
                }
                setTimeout(() => {
                    messageBox.style.display = 'none';
                }, 3000);
            }
        });

        /**
         * Navigates to the marketplace dashboard page.
         */
        function goToMarketplaceDashboard() {
            window.location.href = 'marketplacedashboard.php';
        }
    </script>
</body>
</html>
