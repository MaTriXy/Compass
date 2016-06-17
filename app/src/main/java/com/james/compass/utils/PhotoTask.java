package com.james.compass.utils;

import android.content.Context;
import android.graphics.Bitmap;
import android.os.AsyncTask;

import com.google.android.gms.location.places.PlacePhotoMetadata;
import com.google.android.gms.location.places.PlacePhotoMetadataBuffer;
import com.google.android.gms.location.places.PlacePhotoMetadataResult;
import com.google.android.gms.location.places.Places;
import com.james.compass.Compass;
import com.james.compass.data.AttributedPhoto;

public class PhotoTask extends AsyncTask<String, Void, AttributedPhoto> {

    Compass compass;
    private int height;
    private int width;

    public PhotoTask(Context context, int width, int height) {
        compass = (Compass) context.getApplicationContext();
        this.height = height;
        this.width = width;
    }

    @Override
    protected AttributedPhoto doInBackground(String... params) {
        if (params.length != 1) return null;

        final String placeId = params[0];
        AttributedPhoto attributedPhoto = null;

        PlacePhotoMetadataResult result = Places.GeoDataApi.getPlacePhotos(compass.googleApiClient, placeId).await();

        if (result.getStatus().isSuccess()) {
            PlacePhotoMetadataBuffer photoMetadataBuffer = result.getPhotoMetadata();
            if (photoMetadataBuffer.getCount() > 0 && !isCancelled()) {
                PlacePhotoMetadata photo = photoMetadataBuffer.get(0);
                CharSequence attribution = photo.getAttributions();
                Bitmap image = photo.getScaledPhoto(compass.googleApiClient, width, height).await().getBitmap();

                attributedPhoto = new AttributedPhoto(attribution, image);
            }
            photoMetadataBuffer.release();
        }

        return attributedPhoto;
    }
}
