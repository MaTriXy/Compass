package com.james.compass.utils;

import android.content.Context;
import android.content.SharedPreferences;
import android.preference.PreferenceManager;

import com.google.gson.Gson;
import com.james.compass.data.PlaceData;

import java.util.ArrayList;

public class Saved {

    public static ArrayList<PlaceData> getSavedPlaces(Context context) {
        SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(context);
        ArrayList<PlaceData> places = new ArrayList<>();
        Gson gson = new Gson();
        int size = prefs.getInt("saved-size", 0);
        for (int i = 0; i < size; i++) {
            if (!prefs.contains("saved-" + i) && i < size) {
                i--;
                continue;
            }

            places.add(gson.fromJson(prefs.getString("saved-" + i, null), PlaceData.class));
        }
        return places;
    }

    public static boolean isSavedPlace(Context context, PlaceData placeData) {
        SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(context);
        Gson gson = new Gson();

        int size = prefs.getInt("saved-size", 0);
        for (int i = 0; i < size; i++) {
            if (!prefs.contains("saved-" + i)) continue;
            PlaceData data = gson.fromJson(prefs.getString("saved-" + i, ""), PlaceData.class);
            if (data.id.matches(placeData.id)) return true;
        }

        return false;
    }

    public static void addPlace(Context context, PlaceData placeData) {
        SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(context);
        Gson gson = new Gson();

        int size = prefs.getInt("saved-size", 0);
        for (int i = 0; i < size; i++) {
            if (!prefs.contains("saved-" + i)) continue;
            PlaceData data = gson.fromJson(prefs.getString("saved-" + i, ""), PlaceData.class);
            if (data.id.matches(placeData.id)) return;
        }

        prefs.edit().putString("saved-" + size, gson.toJson(placeData)).putInt("saved-size", size + 1).apply();
    }

    public static void removePlace(Context context, PlaceData placeData) {
        SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(context);
        Gson gson = new Gson();

        int position = -1;
        int size = prefs.getInt("saved-size", 0);
        for (int i = 0; i < size; i++) {
            if (!prefs.contains("saved-" + i)) continue;
            PlaceData data = gson.fromJson(prefs.getString("saved-" + i, ""), PlaceData.class);
            if (data.id.matches(placeData.id)) {
                position = i;
                break;
            }
        }

        if (position < 0) return;
        for (int i = position; i < size; i++) {
            prefs.edit().putString("saved-" + i, prefs.getString("saved-" + (i + 1), null)).apply();
        }
        prefs.edit().putInt("saved-size", size - 1);
    }
}
