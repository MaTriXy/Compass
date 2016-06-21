package com.james.compass;

import android.app.Application;

import com.google.android.gms.auth.api.signin.GoogleSignInAccount;
import com.google.android.gms.common.api.GoogleApiClient;

public class Compass extends Application {

    public GoogleApiClient googleApiClient;
    public GoogleSignInAccount me;
}
