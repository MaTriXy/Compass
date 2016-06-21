package com.james.compass.activities;

import android.os.Bundle;
import android.os.PersistableBundle;
import android.support.v7.app.AppCompatActivity;

import com.james.compass.Compass;
import com.james.compass.data.PlaceData;

public class PlaceActivity extends AppCompatActivity {

    Compass compass;
    PlaceData placeData;

    @Override
    public void onCreate(Bundle savedInstanceState, PersistableBundle persistentState) {
        super.onCreate(savedInstanceState, persistentState);

        placeData = getIntent().getParcelableExtra("place");
    }
}
