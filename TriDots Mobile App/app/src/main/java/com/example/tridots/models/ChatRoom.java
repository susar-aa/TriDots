package com.example.tridots.models;

import com.google.firebase.firestore.ServerTimestamp;
import java.util.Date;
import java.util.List;
import java.util.Map;

public class ChatRoom {
    private String chatRoomId;
    private List<String> participants;
    private Map<String, String> participantNames;
    private Map<String, String> participantImages;
    private String lastMessage;
    @ServerTimestamp // Annotation for Firestore to automatically set the server time
    private Date lastMessageTimestamp;
    private String lastMessageSenderId;

    // Required empty constructor for Firestore deserialization
    public ChatRoom() {}

    public ChatRoom(String chatRoomId, List<String> participants, Map<String, String> participantNames, Map<String, String> participantImages, String lastMessage, String lastMessageSenderId) {
        this.chatRoomId = chatRoomId;
        this.participants = participants;
        this.participantNames = participantNames;
        this.participantImages = participantImages;
        this.lastMessage = lastMessage;
        this.lastMessageSenderId = lastMessageSenderId;
        // lastMessageTimestamp will be set by Firestore using @ServerTimestamp
    }

    // Getters and Setters
    public String getChatRoomId() { return chatRoomId; }
    public void setChatRoomId(String chatRoomId) { this.chatRoomId = chatRoomId; }

    public List<String> getParticipants() { return participants; }
    public void setParticipants(List<String> participants) { this.participants = participants; }

    public Map<String, String> getParticipantNames() { return participantNames; }
    public void setParticipantNames(Map<String, String> participantNames) { this.participantNames = participantNames; }

    public Map<String, String> getParticipantImages() { return participantImages; }
    public void setParticipantImages(Map<String, String> participantImages) { this.participantImages = participantImages; }

    public String getLastMessage() { return lastMessage; }
    public void setLastMessage(String lastMessage) { this.lastMessage = lastMessage; }

    public Date getLastMessageTimestamp() { return lastMessageTimestamp; }
    public void setLastMessageTimestamp(Date lastMessageTimestamp) { this.lastMessageTimestamp = lastMessageTimestamp; }

    public String getLastMessageSenderId() { return lastMessageSenderId; }
    public void setLastMessageSenderId(String lastMessageSenderId) { this.lastMessageSenderId = lastMessageSenderId; }

    // Helper method to get the other participant's ID
    public String getOtherUserId(String currentUserId) {
        if (participants != null) {
            for (String userId : participants) {
                if (!userId.equals(currentUserId)) {
                    return userId;
                }
            }
        }
        return null;
    }
}