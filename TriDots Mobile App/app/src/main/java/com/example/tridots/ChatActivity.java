package com.example.tridots; // Adjust package as needed

import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.tridots.adapters.MessageAdapter; // We'll create this
import com.example.tridots.models.Message;     // We'll create this
import com.google.firebase.database.ChildEventListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID; // For generating unique message IDs

public class ChatActivity extends AppCompatActivity {

    private RecyclerView recyclerViewMessages;
    private EditText editTextMessage;
    private ImageButton buttonSendMessage;
    private TextView textViewReceiverUsername;

    private MessageAdapter messageAdapter;
    private List<Message> messageList;

    private SharedPreferences sharedPreferences;
    private int currentUserId;
    private String currentUsername; // Assuming you'll fetch or pass this
    private int receiverId;
    private String receiverUsername;
    private String chatRoomId;

    private DatabaseReference mChatsRef;
    private DatabaseReference mUserChatsRef;

    private static final String PREF_NAME = "user_session";
    private static final String KEY_USER_ID = "user_id";
    private static final String KEY_USERNAME = "username"; // Assuming you store username in SharedPreferences

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_chat); // Create this layout

        recyclerViewMessages = findViewById(R.id.recyclerViewMessages);
        editTextMessage = findViewById(R.id.editTextMessage);
        buttonSendMessage = findViewById(R.id.buttonSendMessage);
        textViewReceiverUsername = findViewById(R.id.textViewReceiverUsername);

        sharedPreferences = getSharedPreferences(PREF_NAME, MODE_PRIVATE);
        currentUserId = sharedPreferences.getInt(KEY_USER_ID, -1);
        currentUsername = sharedPreferences.getString(KEY_USERNAME, "You"); // Get current user's username

        receiverId = getIntent().getIntExtra("receiver_id", -1);
        receiverUsername = getIntent().getStringExtra("receiver_username");

        if (currentUserId == -1 || receiverId == -1) {
            Toast.makeText(this, "Error: User or receiver ID not found.", Toast.LENGTH_SHORT).show();
            finish();
            return;
        }

        textViewReceiverUsername.setText(receiverUsername);

        // Determine the chat room ID (always consistent for two users)
        chatRoomId = getChatRoomId(currentUserId, receiverId);

        FirebaseDatabase database = FirebaseDatabase.getInstance();
        mChatsRef = database.getReference("chats").child(chatRoomId).child("messages");
        mUserChatsRef = database.getReference("user_chats");

        messageList = new ArrayList<>();
        messageAdapter = new MessageAdapter(this, messageList, currentUserId);
        recyclerViewMessages.setLayoutManager(new LinearLayoutManager(this));
        recyclerViewMessages.setAdapter(messageAdapter);

        listenForMessages();

        buttonSendMessage.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                sendMessage();
            }
        });

        // Ensure the correct bottom navigation item is selected (if you have one in ChatActivity)
        // If ChatActivity is not part of the main bottom nav flow, you might remove this.
        // bottomNavigationView = findViewById(R.id.bottomNavigationView);
        // if (bottomNavigationView != null) {
        //     bottomNavigationView.setSelectedItemId(R.id.navigation_inbox);
        // }
    }

    private String getChatRoomId(int user1Id, int user2Id) {
        // Always create a consistent chat ID by sorting the user IDs
        if (user1Id < user2Id) {
            return user1Id + "_" + user2Id;
        } else {
            return user2Id + "_" + user1Id;
        }
    }

    private void listenForMessages() {
        mChatsRef.addChildEventListener(new ChildEventListener() {
            @Override
            public void onChildAdded(@NonNull DataSnapshot snapshot, String previousChildName) {
                Message message = snapshot.getValue(Message.class);
                if (message != null) {
                    messageList.add(message);
                    messageAdapter.notifyItemInserted(messageList.size() - 1);
                    recyclerViewMessages.scrollToPosition(messageList.size() - 1); // Scroll to last message
                }
            }

            @Override
            public void onChildChanged(@NonNull DataSnapshot snapshot, String previousChildName) {
                // Handle message changes if needed (e.g., editing messages)
            }

            @Override
            public void onChildRemoved(@NonNull DataSnapshot snapshot) {
                // Handle message deletion if needed
            }

            @Override
            public void onChildMoved(@NonNull DataSnapshot snapshot, String previousChildName) {
                // Handle message reordering if needed
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                Toast.makeText(ChatActivity.this, "Failed to load messages: " + error.getMessage(), Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void sendMessage() {
        String messageText = editTextMessage.getText().toString().trim();
        if (messageText.isEmpty()) {
            Toast.makeText(this, "Message cannot be empty", Toast.LENGTH_SHORT).show();
            return;
        }

        String messageId = UUID.randomUUID().toString(); // Generate unique message ID
        long timestamp = System.currentTimeMillis();

        Message message = new Message(messageId, currentUserId, receiverId, messageText, timestamp);

        mChatsRef.child(messageId).setValue(message)
                .addOnSuccessListener(aVoid -> {
                    editTextMessage.setText(""); // Clear input field
                    // Also update user_chats node to mark that a chat exists
                    mUserChatsRef.child(String.valueOf(currentUserId)).child(String.valueOf(receiverId)).setValue(true);
                    mUserChatsRef.child(String.valueOf(receiverId)).child(String.valueOf(currentUserId)).setValue(true);
                })
                .addOnFailureListener(e -> {
                    Toast.makeText(ChatActivity.this, "Failed to send message: " + e.getMessage(), Toast.LENGTH_SHORT).show();
                });
    }
}