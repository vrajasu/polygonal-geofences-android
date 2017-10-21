package com.vraj.polygonalgeofences;

import android.app.PendingIntent;
import android.content.Intent;
import android.icu.text.UnicodeSetSpanner;
import android.os.Environment;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;

import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.location.LocationRequest;
import com.google.android.gms.location.LocationServices;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.util.ArrayList;
import java.util.List;

public class MainActivity extends AppCompatActivity implements GoogleApiClient.ConnectionCallbacks,
        GoogleApiClient.OnConnectionFailedListener {

    File file;
    boolean hasFile = false;
    Button btn_start, btn_stop, btn_add, btn_view;
    String fileLocation = Environment.getExternalStorageDirectory() + File.separator + "/GeoFences" + File.separator + "Geofences";
    int num_geofences = 0;
    GoogleApiClient googleApiClient;
    LocationRequest locationRequest;
    int UPDATE_INTERVAL = 5*1000;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        btn_add = (Button) findViewById(R.id.btn_add_gf);
        btn_start = (Button) findViewById(R.id.btn_start_updates);
        btn_stop = (Button) findViewById(R.id.btn_stop_updates);
        btn_view = (Button) findViewById(R.id.btn_view_gf);
        File folder = new File(Environment.getExternalStorageDirectory().getAbsolutePath() + "/GeoFences");
        if (!folder.exists()) {
            folder.mkdir();

        }
        file = new File(folder, "Geofences");
        if (file.exists()) {
            hasFile = true;
        } else {
            try {
                FileOutputStream fos =
                        new FileOutputStream(file);
                ObjectOutputStream os = new ObjectOutputStream(fos);
                os.writeObject(new ArrayList<GeoFence>());
                os.close();
            } catch (Exception ex) {
                ex.printStackTrace();
            }
        }

        try {
            FileInputStream fis = new FileInputStream(fileLocation);
            ObjectInputStream ois = new ObjectInputStream(fis);
            List<GeoFence> geofencesList = (ArrayList<GeoFence>) ois.readObject();
            num_geofences = geofencesList.size();
            ois.close();
        } catch (Exception e) {
            e.printStackTrace();
        }

        btn_add.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent i = new Intent(MainActivity.this, AddGeofence.class);
                startActivity(i);
            }
        });
        btn_view.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                if (num_geofences > 0) {
                    Intent i = new Intent(MainActivity.this, ViewGeofences.class);
                    startActivity(i);
                } else
                    Toast.makeText(MainActivity.this, "You haven't created any geofences yet!", Toast.LENGTH_LONG).show();
            }
        });
        btn_start.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (num_geofences > 0) {
                    requestLocationUpdates();
                } else
                    Toast.makeText(MainActivity.this, "You haven't created any geofences yet!", Toast.LENGTH_LONG).show();
            }
        });
        btn_stop.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                removeLocationUpdates();
            }
        });
        buildGoogleApiClient();
        googleApiClient.connect();
    }

    private synchronized void buildGoogleApiClient() {

        googleApiClient = new GoogleApiClient.Builder(this)
                .addConnectionCallbacks(this)
                .addOnConnectionFailedListener(this)
                .addApi(LocationServices.API).build();

        createLocationRequest();
    }

    boolean isGoogleClientConnected = false;

    @Override
    public void onConnected(Bundle bundle) {
        isGoogleClientConnected = true;
        Log.d("GoogleClientConnected","Yes");
    }

    @Override
    public void onConnectionSuspended(int i) {

    }

    @Override
    public void onConnectionFailed(ConnectionResult connectionResult) {

    }

    public void createLocationRequest() {
        locationRequest = new LocationRequest();
        locationRequest.setInterval(UPDATE_INTERVAL);
        locationRequest.setFastestInterval(UPDATE_INTERVAL);
        locationRequest.setPriority(LocationRequest.PRIORITY_HIGH_ACCURACY);
    }

    public void requestLocationUpdates() {

        if (isGoogleClientConnected) {
            try {
                LocationServices.FusedLocationApi.requestLocationUpdates(
                        googleApiClient, locationRequest, getPendingIntent());
            } catch (SecurityException e) {
                e.printStackTrace();
            }
        }
        else
            Toast.makeText(MainActivity.this,"Waiting for the Google Client To Connect!",Toast.LENGTH_LONG).show();


    }

    public void removeLocationUpdates() {
        LocationServices.FusedLocationApi.removeLocationUpdates(googleApiClient,
                getPendingIntent());
    }

    private PendingIntent getPendingIntent() {
        Intent intent = new Intent(this, LocationService.class);
        intent.setAction(LocationService.update_action);
        return PendingIntent.getService(this, 0, intent, PendingIntent.FLAG_UPDATE_CURRENT);
    }
}
