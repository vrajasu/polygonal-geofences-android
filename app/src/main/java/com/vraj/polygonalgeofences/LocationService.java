package com.vraj.polygonalgeofences;

import android.app.IntentService;
import android.app.Notification;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.app.TaskStackBuilder;
import android.content.Context;
import android.content.Intent;
import android.content.Loader;
import android.graphics.Color;
import android.location.Location;
import android.os.Environment;
import android.support.annotation.Nullable;
import android.util.Log;

import com.google.android.gms.location.LocationResult;

import java.io.File;
import java.io.FileInputStream;
import java.io.ObjectInputStream;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by vrajdelhivala on 10/20/17.
 * adapting the screen
 */

public class LocationService extends IntentService {

    static String update_action = "LOCATION_UPDATES_ACTION";
    NotificationManager notificationManager;
    List<GeoFence> geofencesList = new ArrayList<>();
    String fileLocation= Environment.getExternalStorageDirectory() + File.separator +"/GeoFences"+File.separator+"Geofences";

    public LocationService() {
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
                    Log.d("Locations", "" + locations.get(0).getLatitude() + "," + locations.get(0).getLongitude());
                    readFromFile();
                    String geofenceName = "";
                    for(int i=0;i<geofencesList.size();i++)
                    {
                        String name = geofencesList.get(i).geoFenceName;
                        List<double[]> list = geofencesList.get(i).geoFencePoints;
                        if(PolygonTest.PointIsInRegion(locations.get(0).getLatitude(),locations.get(0).getLongitude(),list)){
                            geofenceName=name;
                            break;
                        }
                    }
                    showNotification(geofenceName);
                }
            }
        }
    }
    public void readFromFile(){
        try{
            FileInputStream fis = new FileInputStream(fileLocation);
            ObjectInputStream ois = new ObjectInputStream(fis);
            geofencesList = (ArrayList<GeoFence>)ois.readObject();
            ois.close();
        }catch (Exception e)
        {
            e.printStackTrace();
        }
    }

    private NotificationManager getNotificationManager() {
        if (notificationManager == null) {
            notificationManager = (NotificationManager) getApplicationContext().getSystemService(
                    Context.NOTIFICATION_SERVICE);
        }
        return notificationManager;
    }

    void showNotification(String s) {
        Context context = getApplicationContext();
        Intent notificationIntent = new Intent(context, MainActivity.class);

        TaskStackBuilder stackBuilder = TaskStackBuilder.create(context);

        stackBuilder.addParentStack(MainActivity.class);

        stackBuilder.addNextIntent(notificationIntent);

        PendingIntent notificationPendingIntent =
                stackBuilder.getPendingIntent(0, PendingIntent.FLAG_UPDATE_CURRENT);

        String p;

        if(s.equals("")){
             p = "You haven't visited any geofence yet!";
        }
        else{
            p = "Last visited geofence : "+s;
        }
        Notification notification = new Notification.Builder(context)
                .setContentTitle("Tracking Location Updates")
                .setContentText(p)
                .setSmallIcon(R.mipmap.ic_launcher)
                .setAutoCancel(true)
                .setContentIntent(notificationPendingIntent).build();

        getNotificationManager().notify(0, notification);
    }
}

