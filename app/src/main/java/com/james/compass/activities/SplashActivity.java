package com.james.compass.activities;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.Toast;

import com.google.android.gms.auth.api.Auth;
import com.google.android.gms.auth.api.signin.GoogleSignInOptions;
import com.google.android.gms.auth.api.signin.GoogleSignInResult;
import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.SignInButton;
import com.google.android.gms.common.api.GoogleApiClient;
import com.james.compass.Compass;
import com.james.compass.R;

public class SplashActivity extends AppCompatActivity implements GoogleApiClient.ConnectionCallbacks, GoogleApiClient.OnConnectionFailedListener {

    Compass compass;
    SignInButton button;
    View progress;

    SharedPreferences prefs;
    boolean signedin;

    GoogleApiClient googleApiClient;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_splash);

        prefs = PreferenceManager.getDefaultSharedPreferences(this);

        compass = (Compass) getApplicationContext();
        button = (SignInButton) findViewById(R.id.sign_in_button);
        progress = findViewById(R.id.loading_bar);

        signedin = prefs.getBoolean("signedin", false);
        if (signedin) {
            button.setVisibility(View.GONE);
            progress.setVisibility(View.VISIBLE);
        }

        googleApiClient = new GoogleApiClient.Builder(this)
                .addConnectionCallbacks(this)
                .addOnConnectionFailedListener(this)
                .addApi(Auth.GOOGLE_SIGN_IN_API, new GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN).requestEmail().build())
                .build();

        findViewById(R.id.sign_in_button).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                button.setVisibility(View.GONE);
                progress.setVisibility(View.VISIBLE);
                startActivityForResult(Auth.GoogleSignInApi.getSignInIntent(googleApiClient), 6220);
            }
        });
    }

    @Override
    protected void onStart() {
        if (googleApiClient != null) googleApiClient.connect();
        super.onStart();
    }

    @Override
    protected void onStop() {
        if (googleApiClient != null && googleApiClient.isConnected()) googleApiClient.disconnect();
        super.onStop();
    }

    @Override
    public void onConnected(@Nullable Bundle bundle) {
        if (signedin) startActivityForResult(Auth.GoogleSignInApi.getSignInIntent(googleApiClient), 6220);
    }

    @Override
    public void onConnectionSuspended(int i) {
    }

    @Override
    public void onConnectionFailed(@NonNull ConnectionResult connectionResult) {
        Toast.makeText(this, connectionResult.getErrorMessage(), Toast.LENGTH_SHORT).show();
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (requestCode == 6220) {
            GoogleSignInResult result = Auth.GoogleSignInApi.getSignInResultFromIntent(data);

            if (result.isSuccess()) {
                compass.me = result.getSignInAccount();

                new Thread(new Runnable() {
                    @Override
                    public void run() {

                        try {
                            Thread.sleep(1500);
                        } catch (InterruptedException ignored) {
                        }

                        runOnUiThread(new Runnable() {
                            @Override
                            public void run() {
                                prefs.edit().putBoolean("signedin", true).apply();
                                startActivity(new Intent(SplashActivity.this, MainActivity.class));
                                finish();
                            }
                        });
                    }
                }).start();
            } else finish();
        }
    }
}
