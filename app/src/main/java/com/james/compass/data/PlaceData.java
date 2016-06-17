package com.james.compass.data;

import android.net.Uri;
import android.os.Bundle;
import android.os.Parcel;
import android.os.Parcelable;

import com.google.android.gms.location.places.Place;
import com.google.android.gms.maps.model.LatLng;

public class PlaceData implements Parcelable {

    public String id, name, description, number, address;
    public double latitude, longitude;
    public Uri website;

    public PlaceData(Place place) {
        id = place.getId();
        name = place.getName().toString();
        description = place.getAddress().toString();
        number = place.getPhoneNumber().toString();
        address = place.getAddress().toString();

        LatLng latLng = place.getLatLng();
        latitude = latLng.latitude;
        longitude = latLng.longitude;

        website = place.getWebsiteUri();
    }

    protected PlaceData(Parcel in) {
        id = in.readString();
        name = in.readString();
        description = in.readString();
        number = in.readString();
        address = in.readString();
        latitude = in.readDouble();
        longitude = in.readDouble();
        website = in.readParcelable(Uri.class.getClassLoader());
    }

    public static final Creator<PlaceData> CREATOR = new Creator<PlaceData>() {
        @Override
        public PlaceData createFromParcel(Parcel in) {
            return new PlaceData(in);
        }

        @Override
        public PlaceData[] newArray(int size) {
            return new PlaceData[size];
        }
    };

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(id);
        dest.writeString(name);
        dest.writeString(description);
        dest.writeString(number);
        dest.writeString(address);
        dest.writeDouble(latitude);
        dest.writeDouble(longitude);
        dest.writeParcelable(website, flags);
    }
}
