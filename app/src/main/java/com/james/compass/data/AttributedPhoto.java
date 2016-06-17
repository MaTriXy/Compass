package com.james.compass.data;

import android.graphics.Bitmap;
import android.os.Parcel;
import android.os.Parcelable;

public class AttributedPhoto implements Parcelable {

    public String attribution;
    public Bitmap bitmap;

    public AttributedPhoto(CharSequence attribution, Bitmap bitmap) {
        this.attribution = attribution.toString();
        this.bitmap = bitmap;
    }

    protected AttributedPhoto(Parcel in) {
        attribution = in.readString();
        bitmap = in.readParcelable(Bitmap.class.getClassLoader());
    }

    public static final Creator<AttributedPhoto> CREATOR = new Creator<AttributedPhoto>() {
        @Override
        public AttributedPhoto createFromParcel(Parcel in) {
            return new AttributedPhoto(in);
        }

        @Override
        public AttributedPhoto[] newArray(int size) {
            return new AttributedPhoto[size];
        }
    };

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(attribution);
        dest.writeParcelable(bitmap, flags);
    }
}
