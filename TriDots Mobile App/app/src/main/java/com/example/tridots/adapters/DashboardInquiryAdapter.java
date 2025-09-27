package com.example.tridots.adapters;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;
import com.bumptech.glide.Glide;
import com.example.tridots.R;
import com.example.tridots.models.Inquiry;
import java.util.List;

public class DashboardInquiryAdapter extends RecyclerView.Adapter<DashboardInquiryAdapter.ViewHolder> {

    private List<Inquiry> inquiryList;
    private OnFeedbackClickListener feedbackClickListener;

    public interface OnFeedbackClickListener {
        void onFeedbackClick(int position, Inquiry inquiry);
    }

    public DashboardInquiryAdapter(List<Inquiry> inquiryList) {
        this.inquiryList = inquiryList;
    }

    public void setOnFeedbackClickListener(OnFeedbackClickListener listener) {
        this.feedbackClickListener = listener;
    }

    public void updateInquiry(int inquiryId, String adName, String adImageUrl) {
        for (int i = 0; i < inquiryList.size(); i++) {
            if (inquiryList.get(i).getInquiryId() == inquiryId) {
                inquiryList.get(i).setAdName(adName);
                inquiryList.get(i).setAdImageUrl(adImageUrl);
                notifyItemChanged(i);
                return;
            }
        }
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.item_dashboard_inquiry, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        Inquiry inquiry = inquiryList.get(position);
        holder.textViewAdName.setText(inquiry.getAdName() != null ? inquiry.getAdName() : "Loading...");
        holder.textViewInquirerName.setText(inquiry.getInquirerName());
        if (inquiry.getAdImageUrl() != null && !inquiry.getAdImageUrl().isEmpty()) {
            Glide.with(holder.itemView.getContext())
                    .load(inquiry.getAdImageUrl())
                    .placeholder(R.drawable.ad_placeholder)
                    .error(R.drawable.image_error_placeholder)
                    .into(holder.imageViewAd);
        } else {
            holder.imageViewAd.setImageResource(R.drawable.ad_placeholder);
        }

        holder.buttonSendFeedback.setOnClickListener(v -> {
            if (feedbackClickListener != null) {
                feedbackClickListener.onFeedbackClick(position, inquiry);
            }
        });

        // Also allow clicking the item for feedback
        holder.itemView.setOnClickListener(v -> {
            if (feedbackClickListener != null) {
                feedbackClickListener.onFeedbackClick(position, inquiry);
            }
        });
    }

    @Override
    public int getItemCount() {
        return inquiryList.size();
    }

    public static class ViewHolder extends RecyclerView.ViewHolder {
        ImageView imageViewAd;
        TextView textViewAdName, textViewInquirerName;
        Button buttonSendFeedback;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            imageViewAd = itemView.findViewById(R.id.imageViewAd);
            textViewAdName = itemView.findViewById(R.id.textViewAdName);
            textViewInquirerName = itemView.findViewById(R.id.textViewInquirerName);
            buttonSendFeedback = itemView.findViewById(R.id.buttonSendFeedback);
        }
    }
}