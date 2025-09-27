package com.example.tridots.models;

import android.os.Parcel;
import android.os.Parcelable;

public class Ad implements Parcelable {
    private int id;
    private String title;
    private String imageUrl;
    private String description;
    private String category; // "Renting", "Vehicle", or "Service"
    private String approvalStatus; // "Approved", "Not Approved", "Under Review", "Blocked"

    // Constructor
    public Ad(int id, String title, String imageUrl, String description, String category, String approvalStatus) {
        this.id = id;
        this.title = title;
        this.imageUrl = imageUrl;
        this.description = description;
        this.category = category;
        this.approvalStatus = approvalStatus;
    }

    protected Ad(Parcel in) {
        id = in.readInt();
        title = in.readString();
        imageUrl = in.readString();
        description = in.readString();
        category = in.readString();
        approvalStatus = in.readString();
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeInt(id);
        dest.writeString(title);
        dest.writeString(imageUrl);
        dest.writeString(description);
        dest.writeString(category);
        dest.writeString(approvalStatus);
    }

    @Override
    public int describeContents() {
        return 0;
    }

    public static final Creator<Ad> CREATOR = new Creator<Ad>() {
        @Override
        public Ad createFromParcel(Parcel in) {
            return new Ad(in);
        }

        @Override
        public Ad[] newArray(int size) {
            return new Ad[size];
        }
    };

    public int getId() {
        return id;
    }

    public String getTitle() {
        return title;
    }

    public String getImageUrl() {
        return imageUrl;
    }

    public String getDescription() {
        return description;
    }

    public String getCategory() {
        return category;
    }

    public String getApprovalStatus() {
        return approvalStatus;
    }

    // Method to get the ad type (which is the category in this model)
    public String getAdType() {
        return category;
    }
}
