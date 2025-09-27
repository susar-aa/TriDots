package com.example.tridots.adapters; // Adjust package as needed

import android.content.Context;
import android.text.format.DateFormat;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.tridots.R; // Make sure this path is correct
import com.example.tridots.models.Message; // Make sure this path is correct

import java.util.List;
import java.util.Calendar;
import java.util.Locale;

public class MessageAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {

    private static final int MSG_TYPE_SENT = 0;
    private static final int MSG_TYPE_RECEIVED = 1;

    private Context mContext;
    private List<Message> mMessageList;
    private int mCurrentUserId; // Current logged-in user's ID

    public MessageAdapter(Context context, List<Message> messageList, int currentUserId) {
        mContext = context;
        mMessageList = messageList;
        mCurrentUserId = currentUserId;
    }

    @Override
    public int getItemViewType(int position) {
        Message message = mMessageList.get(position);
        if (message.getSenderId() == mCurrentUserId) {
            return MSG_TYPE_SENT;
        } else {
            return MSG_TYPE_RECEIVED;
        }
    }

    @NonNull
    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        if (viewType == MSG_TYPE_SENT) {
            View view = LayoutInflater.from(mContext).inflate(R.layout.item_message_sent, parent, false);
            return new SentMessageViewHolder(view);
        } else {
            View view = LayoutInflater.from(mContext).inflate(R.layout.item_message_received, parent, false);
            return new ReceivedMessageViewHolder(view);
        }
    }

    @Override
    public void onBindViewHolder(@NonNull RecyclerView.ViewHolder holder, int position) {
        Message message = mMessageList.get(position);

        Calendar cal = Calendar.getInstance(Locale.ENGLISH);
        cal.setTimeInMillis(message.getTimestamp());
        String date = DateFormat.format("hh:mm a", cal).toString(); // e.g., 04:30 PM

        if (holder.getItemViewType() == MSG_TYPE_SENT) {
            SentMessageViewHolder sentHolder = (SentMessageViewHolder) holder;
            sentHolder.messageText.setText(message.getText());
            sentHolder.messageTime.setText(date);
        } else {
            ReceivedMessageViewHolder receivedHolder = (ReceivedMessageViewHolder) holder;
            receivedHolder.messageText.setText(message.getText());
            receivedHolder.messageTime.setText(date);
        }
    }

    @Override
    public int getItemCount() {
        return mMessageList.size();
    }

    // ViewHolder for sent messages
    static class SentMessageViewHolder extends RecyclerView.ViewHolder {
        TextView messageText, messageTime;

        SentMessageViewHolder(@NonNull View itemView) {
            super(itemView);
            messageText = itemView.findViewById(R.id.textViewMessageText);
            messageTime = itemView.findViewById(R.id.textViewMessageTime);
        }
    }

    // ViewHolder for received messages
    static class ReceivedMessageViewHolder extends RecyclerView.ViewHolder {
        TextView messageText, messageTime;

        ReceivedMessageViewHolder(@NonNull View itemView) {
            super(itemView);
            messageText = itemView.findViewById(R.id.textViewMessageText);
            messageTime = itemView.findViewById(R.id.textViewMessageTime);
        }
    }
}