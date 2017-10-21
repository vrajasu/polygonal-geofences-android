package com.vraj.polygonalgeofences;

import android.app.IntentService;
import android.content.Intent;
import android.content.Loader;
import android.location.Location;
import android.support.annotation.Nullable;
import android.util.Log;

import com.google.android.gms.location.LocationResult;

import java.util.List;

/**
 * Created by vrajdelhivala on 10/20/17.
 */

public class LocationService extends IntentService {

    static String update_action = "LOCATION_UPDATES_ACTION";
    public LocationService()
    {
        super("LocationService");
    }
    @Override
    protected void onHandleIntent(@Nullable Intent intent) {
        if (intent != null) {
            final String action = intent.getAction();
            if (update_action.equals(action)) {
                LocationResult result = LocationResult.extractResult(intent);
                if (result != null) {
                    List<Location> locations = result.getLocations();
                    // Save the location data to SharedPreferences.
                    Log.d("Locations",""+locations.get(0).getLatitude()+","+locations.get(0).getLongitude());
                }
            }
        }
    }
}
