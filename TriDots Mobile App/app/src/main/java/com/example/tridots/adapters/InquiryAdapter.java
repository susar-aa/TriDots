package com.example.tridots.adapters;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.example.tridots.R;
import com.example.tridots.models.Inquiry;

import java.util.List;

public class InquiryAdapter extends RecyclerView.Adapter<InquiryAdapter.InquiryViewHolder> {
    private List<Inquiry> inquiryList;
    private OnInquiryActionListener mListener;

    public interface OnInquiryActionListener {
        void onCancelClicked(Inquiry inquiry);
        void onConfirmClicked(Inquiry inquiry);
    }

    public void setOnInquiryActionListener(OnInquiryActionListener listener) {
        this.mListener = listener;
    }

    public InquiryAdapter(List<Inquiry> inquiryList) {
        this.inquiryList = inquiryList;
    }

    @NonNull
    @Override
    public InquiryViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View itemView = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.item_inquiry, parent, false);
        return new InquiryViewHolder(itemView);
    }

    @Override
    public void onBindViewHolder(@NonNull InquiryViewHolder holder, int position) {
        Inquiry inquiry = inquiryList.get(position);

        // Bind ad name
        holder.textViewAdName.setText(
                inquiry.getAdName() != null && !inquiry.getAdName().isEmpty() ?
                        inquiry.getAdName() : "Ad Name");

        // Bind ad image (Glide or placeholder)
        if (inquiry.getAdImageUrl() != null && !inquiry.getAdImageUrl().isEmpty()) {
            Glide.with(holder.itemView.getContext())
                    .load(inquiry.getAdImageUrl())
                    .placeholder(R.drawable.ad_placeholder)
                    .error(R.drawable.ad_placeholder)
                    .into(holder.imageViewAd);
        } else {
            holder.imageViewAd.setImageResource(R.drawable.ad_placeholder);
        }

        holder.textViewInquirerName.setText(inquiry.getInquirerName());
        holder.textViewInquiryMessage.setText(inquiry.getInquiryMessage());

        // Bind inquiry date (already formatted by backend)
        if (inquiry.getInquiryDate() != null && !inquiry.getInquiryDate().isEmpty()) {
            holder.textViewInquiryDate.setText(inquiry.getInquiryDate());
        } else {
            holder.textViewInquiryDate.setText("-");
        }

        holder.textViewStatus.setText(inquiry.getStatus());

        // Existing logic for showing/hiding actions, seller reply, etc.
        holder.textViewSellerReplyLabel.setVisibility(View.GONE);
        holder.textViewSellerReply.setVisibility(View.GONE);
        holder.textViewEstimatedCostLabel.setVisibility(View.GONE);
        holder.textViewEstimatedCost.setVisibility(View.GONE);
        holder.layoutActions.setVisibility(View.GONE);

        if ("Replied".equals(inquiry.getStatus())) {
            holder.textViewSellerReplyLabel.setVisibility(View.VISIBLE);
            holder.textViewSellerReply.setVisibility(View.VISIBLE);
            holder.textViewEstimatedCostLabel.setVisibility(View.VISIBLE);
            holder.textViewEstimatedCost.setVisibility(View.VISIBLE);
            holder.layoutActions.setVisibility(View.VISIBLE);
            holder.buttonCancel.setVisibility(View.VISIBLE);
            holder.buttonConfirm.setVisibility(View.VISIBLE);

            holder.textViewSellerReply.setText(inquiry.getSellerReply());
            holder.textViewEstimatedCost.setText(String.format("%.2f", inquiry.getEstimatedCost()));

            holder.buttonCancel.setOnClickListener(v -> {
                if (mListener != null) {
                    mListener.onCancelClicked(inquiry);
                }
            });

            holder.buttonConfirm.setOnClickListener(v -> {
                if (mListener != null) {
                    mListener.onConfirmClicked(inquiry);
                }
            });
        }
    }

    @Override
    public int getItemCount() {
        return inquiryList.size();
    }

    public static class InquiryViewHolder extends RecyclerView.ViewHolder {
        public ImageView imageViewAd;
        public TextView textViewAdName;
        public TextView textViewInquirerName, textViewInquiryMessage, textViewInquiryDate, textViewStatus;
        public TextView textViewSellerReplyLabel, textViewSellerReply, textViewEstimatedCostLabel, textViewEstimatedCost;
        public LinearLayout layoutActions;
        public Button buttonCancel, buttonConfirm;

        public InquiryViewHolder(@NonNull View itemView) {
            super(itemView);
            imageViewAd = itemView.findViewById(R.id.imageViewAd);
            textViewAdName = itemView.findViewById(R.id.textViewAdName);
            textViewInquirerName = itemView.findViewById(R.id.textViewInquirerName);
            textViewInquiryMessage = itemView.findViewById(R.id.textViewInquiryMessage);
            textViewInquiryDate = itemView.findViewById(R.id.textViewInquiryDate);
            textViewStatus = itemView.findViewById(R.id.textViewStatus);
            textViewSellerReplyLabel = itemView.findViewById(R.id.textViewSellerReplyLabel);
            textViewSellerReply = itemView.findViewById(R.id.textViewSellerReply);
            textViewEstimatedCostLabel = itemView.findViewById(R.id.textViewEstimatedCostLabel);
            textViewEstimatedCost = itemView.findViewById(R.id.textViewEstimatedCost);
            layoutActions = itemView.findViewById(R.id.layoutActions);
            buttonCancel = itemView.findViewById(R.id.buttonCancel);
            buttonConfirm = itemView.findViewById(R.id.buttonConfirm);
        }
    }
}