package com.james.compass.utils;

import android.content.Context;
import android.content.SharedPreferences;
import android.os.Parcel;
import android.os.Parcelable;
import android.preference.PreferenceManager;

import com.james.compass.Compass;
import com.james.compass.data.PlaceData;

import java.util.ArrayList;

public class Saved {

    public static ArrayList<PlaceData> getSavedPlaces(Context context) {
        SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(context);
        ArrayList<PlaceData> places = new ArrayList<>();
        int size = prefs.getInt("saved-size", 0);
        for (int i = 0; i < size; i++) {

        }
        return places;
    }

    public static void addPlace(Context context, PlaceData placeData) {
        SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(context);
    }

    public static void removePlace(Context context, PlaceData placeData) {
        SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(context);
    }
}
