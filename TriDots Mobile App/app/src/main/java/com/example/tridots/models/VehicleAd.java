package com.example.tridots.models;

import android.os.Parcel;
import android.os.Parcelable;

public class VehicleAd implements Parcelable {
    private int vehicleId;
    private String vehicleName;
    private String brand;
    private String model;
    private String priceType;
    private double amount;
    private String vehicleImages;
    private String location;
    private String description;
    private String capacity;
    private String fuelType;
    private String transmissionType;

    public VehicleAd(int vehicleId, String vehicleName, String brand, String model, String priceType, double amount, String vehicleImages, String location, String description, String capacity, String fuelType, String transmissionType) {
        this.vehicleId = vehicleId;
        this.vehicleName = vehicleName;
        this.brand = brand;
        this.model = model;
        this.priceType = priceType;
        this.amount = amount;
        this.vehicleImages = vehicleImages;
        this.location = location;
        this.description = description;
        this.capacity = capacity;
        this.fuelType = fuelType;
        this.transmissionType = transmissionType;
    }

    protected VehicleAd(Parcel in) {
        vehicleId = in.readInt();
        vehicleName = in.readString();
        brand = in.readString();
        model = in.readString();
        priceType = in.readString();
        amount = in.readDouble();
        vehicleImages = in.readString();
        location = in.readString();
        description = in.readString();
        capacity = in.readString();
        fuelType = in.readString();
        transmissionType = in.readString();
    }

    public static final Creator<VehicleAd> CREATOR = new Creator<VehicleAd>() {
        @Override
        public VehicleAd createFromParcel(Parcel in) {
            return new VehicleAd(in);
        }

        @Override
        public VehicleAd[] newArray(int size) {
            return new VehicleAd[size];
        }
    };

    public int getVehicleId() {
        return vehicleId;
    }

    public String getVehicleName() {
        return vehicleName;
    }

    public String getBrand() {
        return brand;
    }

    public String getModel() {
        return model;
    }

    public String getPriceType() {
        return priceType;
    }

    public double getAmount() {
        return amount;
    }

    public String getVehicleImages() {
        return vehicleImages;
    }

    public String getLocation() {
        return location;
    }

    public String getDescription() {
        return description;
    }

    public String getCapacity() {
        return capacity;
    }

    public String getFuelType() {
        return fuelType;
    }

    public String getTransmissionType() {
        return transmissionType;
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeInt(vehicleId);
        dest.writeString(vehicleName);
        dest.writeString(brand);
        dest.writeString(model);
        dest.writeString(priceType);
        dest.writeDouble(amount);
        dest.writeString(vehicleImages);
        dest.writeString(location);
        dest.writeString(description);
        dest.writeString(capacity);
        dest.writeString(fuelType);
        dest.writeString(transmissionType);
    }
}