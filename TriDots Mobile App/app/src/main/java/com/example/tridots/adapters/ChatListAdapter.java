package com.example.tridots.adapters;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.tridots.R;
import com.squareup.picasso.Picasso; // For loading profile pictures

import java.util.List;

public class ChatListAdapter extends RecyclerView.Adapter<ChatListAdapter.ChatUserViewHolder> {

    private Context mContext;
    private List<com.example.tridots.models.User> mChatUsers;
    private OnItemClickListener mListener;

    public interface OnItemClickListener {
        void onItemClick(com.example.tridots.models.User user);
    }

    public ChatListAdapter(Context context, List<com.example.tridots.models.User> chatUsers, OnItemClickListener listener) {
        this.mContext = context;
        this.mChatUsers = chatUsers;
        this.mListener = listener;
    }

    @NonNull
    @Override
    public ChatUserViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(mContext).inflate(R.layout.item_chat_user, parent, false);
        return new ChatUserViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ChatUserViewHolder holder, int position) {
        com.example.tridots.models.User user = mChatUsers.get(position);
        holder.userName.setText(user.getUsername());

        if (user.getProfilePictureUrl() != null && !user.getProfilePictureUrl().isEmpty()) {
            Picasso.get().load(user.getProfilePictureUrl())
                    .placeholder(R.drawable.profile_placeholder) // Your placeholder
                    .error(R.drawable.profile_placeholder) // Your error image
                    .into(holder.userProfileImage);
        } else {
            holder.userProfileImage.setImageResource(R.drawable.profile_placeholder); // Default image
        }

        holder.itemView.setOnClickListener(v -> {
            if (mListener != null) {
                mListener.onItemClick(user);
            }
        });
    }

    @Override
    public int getItemCount() {
        return mChatUsers.size();
    }

    static class ChatUserViewHolder extends RecyclerView.ViewHolder {
        ImageView userProfileImage;
        TextView userName;

        ChatUserViewHolder(@NonNull View itemView) {
            super(itemView);
            userProfileImage = itemView.findViewById(R.id.imageViewUserProfile);
            userName = itemView.findViewById(R.id.textViewUserName);
        }
    }
}