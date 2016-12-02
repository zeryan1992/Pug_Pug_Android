package com.app.pug.pug.utils;

import android.location.Criteria;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.Bundle;

import java.util.List;

/**
 * Created by zeryan on 2/7/16.
 */
public class LocationHandler {
    LocationManager manager;
    Criteria criteria;
    LocationListener locationListener;
    protected LocationListener newListener = new LocationListener() {
        @Override
        public void onLocationChanged(Location location) {
            if (locationListener != null && location != null) {
                locationListener.onLocationChanged(location);
                manager.removeUpdates(newListener);
            }
        }

        @Override
        public void onStatusChanged(String provider, int status, Bundle extras) {

        }

        @Override
        public void onProviderEnabled(String provider) {

        }

        @Override
        public void onProviderDisabled(String provider) {

        }
    };

    public LocationHandler(LocationManager manager) {
        this.manager = manager;
        criteria = new Criteria();
        criteria.setAccuracy(Criteria.ACCURACY_COARSE);

    }

    public Location getLastLocation() {
        float bestAccuracy = Float.MAX_VALUE;
        Location bestlocation = null;
        //LocationListener listener;

        List<String> providers = manager.getAllProviders();
        for (int i = 0; i < providers.size(); i++) {
            Location location = manager.getLastKnownLocation(providers.get(i));
            if (location != null) {
                float accuracy = location.getAccuracy();
                if (bestAccuracy > accuracy) {
                    bestlocation = location;
                } else if (bestAccuracy == Float.MAX_VALUE) {
                    bestlocation = location;
                }
            }
            if (locationListener != null) {
                String provider = manager.getBestProvider(criteria, true);
                if (provider != null) {
                    manager.requestLocationUpdates(provider, 0, 0, locationListener);
                }
            }

        }
        return bestlocation;
    }
}
