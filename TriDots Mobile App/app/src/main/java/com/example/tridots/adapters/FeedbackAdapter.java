package com.example.tridots.adapters;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.RatingBar;
import android.widget.TextView;
import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;
import com.example.tridots.R;
import com.example.tridots.models.Feedback;
import java.util.List;

public class FeedbackAdapter extends RecyclerView.Adapter<FeedbackAdapter.FeedbackViewHolder> {
    private List<Feedback> feedbacks;

    public FeedbackAdapter(List<Feedback> feedbacks) {
        this.feedbacks = feedbacks;
    }

    @NonNull
    @Override
    public FeedbackViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        // Inflate the new redesigned layout
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_feedback, parent, false);
        return new FeedbackViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull FeedbackViewHolder holder, int position) {
        Feedback feedback = feedbacks.get(position);

        holder.userName.setText(feedback.getUserName());
        holder.ratingBar.setRating(feedback.getRating());

        // Hide comment view if the comment is empty or null
        if (feedback.getComment() != null && !feedback.getComment().trim().isEmpty()) {
            holder.comment.setText(feedback.getComment());
            holder.comment.setVisibility(View.VISIBLE);
        } else {
            holder.comment.setVisibility(View.GONE);
        }

        // You might want to format the date for a cleaner look
        // For now, we just set the text as is.
        holder.createdAt.setText(feedback.getCreatedAt());
    }

    @Override
    public int getItemCount() {
        return feedbacks.size();
    }

    // The ViewHolder now references the same IDs from the new layout
    public static class FeedbackViewHolder extends RecyclerView.ViewHolder {
        TextView userName, comment, createdAt;
        RatingBar ratingBar;
        public FeedbackViewHolder(@NonNull View itemView) {
            super(itemView);
            userName = itemView.findViewById(R.id.feedbackUserName);
            ratingBar = itemView.findViewById(R.id.feedbackRatingBar);
            comment = itemView.findViewById(R.id.feedbackComment);
            createdAt = itemView.findViewById(R.id.feedbackCreatedAt);
        }
    }
}
