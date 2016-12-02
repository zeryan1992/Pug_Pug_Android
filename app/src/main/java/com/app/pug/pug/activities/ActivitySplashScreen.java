package com.app.pug.pug.activities;

import android.Manifest;
import android.annotation.TargetApi;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.location.LocationManager;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Build;
import android.os.Bundle;
import android.provider.Settings;
import android.support.v4.app.ActivityCompat;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;

import com.app.pug.pug.R;
import com.app.pug.pug.login_signup.LoginSignup;
import com.app.pug.pug.utils.LocationHandler;


public class ActivitySplashScreen extends AppCompatActivity {
    public static LocationHandler locationHandler;
    LocationManager manager;
    ConnectivityManager connectivityManager;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.splash_activity);
        manager = (LocationManager) getSystemService(LOCATION_SERVICE);
        locationHandler = new LocationHandler(manager);

    }

    @Override
    protected void onResume() {
        super.onResume();
        if (isConnectedtotheInternet()) {

            if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                    this.requestPermissions(new String[]{Manifest.permission.ACCESS_COARSE_LOCATION, Manifest.permission.ACCESS_FINE_LOCATION, Manifest.permission.ACCESS_NETWORK_STATE}, 1000);
                }
            } else if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION) == PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED) {

                if (manager.isProviderEnabled(LocationManager.GPS_PROVIDER)) {
                    LoginSignup login=new LoginSignup(this);
                    login.execute();
                } else {
                    showGPSAlert();
                }


            }
        } else {
            showConnectivityAlert();
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, String[] permissions, int[] grantResults) {
        if (isConnectedtotheInternet())
        {
            if (manager.isProviderEnabled(LocationManager.GPS_PROVIDER)) {
                if (grantResults[0]==0&&grantResults[1]==0&&grantResults[2]==0) {
                        LoginSignup login = new LoginSignup(this);
                        login.execute();
                    }

                }
                else{
                finish();
            }
            }
            else if (!manager.isProviderEnabled(LocationManager.GPS_PROVIDER))
            {
                showGPSAlert();
            }
        else
        {
            showConnectivityAlert();
        }
    }

    public void showGPSAlert() {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setCancelable(false);
        builder.setTitle("Location is required:-");
        builder.setMessage("You need to enable your GPS to move forward, do you want to enable it?");
        builder.setPositiveButton("Yes", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                startActivity(new Intent(Settings.ACTION_LOCATION_SOURCE_SETTINGS));

            }
        });
        AlertDialog dialog = builder.create();
        dialog.show();

    }

    public void showConnectivityAlert() {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setCancelable(false);
        builder.setTitle("Internet connection is required:-");
        builder.setMessage("You need internet connection to use this app would you like to enable it?");
        builder.setPositiveButton("Yes", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                startActivityForResult(new Intent(Settings.ACTION_WIFI_SETTINGS), 1);
            }
        });
        AlertDialog dialog = builder.create();
        dialog.show();

    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_NETWORK_STATE) != PackageManager.PERMISSION_GRANTED) {

            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                this.requestPermissions(new String[]{Manifest.permission.ACCESS_COARSE_LOCATION, Manifest.permission.ACCESS_FINE_LOCATION}, 1000);
            }

        } else if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION) == PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_NETWORK_STATE) == PackageManager.PERMISSION_GRANTED) {
            if (manager.isProviderEnabled(LocationManager.GPS_PROVIDER))
            {
                if (isConnectedtotheInternet()) {
                    LoginSignup login = new LoginSignup(this);
                    login.execute();
                }
                else
                {
                    showConnectivityAlert();
                }
            }
            else
            {
                showGPSAlert();
            }


        }
    }

    public boolean isConnectedtotheInternet() {
        connectivityManager = (ConnectivityManager) getSystemService(CONNECTIVITY_SERVICE);
        NetworkInfo networkInfo = connectivityManager.getActiveNetworkInfo();
        return networkInfo != null && networkInfo.isConnected();

    }



}
