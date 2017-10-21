package com.vraj.polygonalgeofences;

import android.content.res.Resources;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Rect;
import android.graphics.drawable.BitmapDrawable;
import android.os.Environment;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.widget.ImageView;

import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.MapFragment;
import com.google.android.gms.maps.model.BitmapDescriptor;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.CameraPosition;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.android.gms.maps.model.PolygonOptions;

import java.io.File;
import java.io.FileInputStream;
import java.io.ObjectInputStream;
import java.util.ArrayList;
import java.util.List;

public class ViewGeofences extends AppCompatActivity {

    GoogleMap googleMap;
    List<GeoFence> geofencesList=new ArrayList<>();
    String fileLocation= Environment.getExternalStorageDirectory() + File.separator +"/GeoFences"+File.separator+"Geofences";


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_view_geofences);

        if (googleMap == null) {
            googleMap = ((MapFragment) getFragmentManager().
                    findFragmentById(R.id.viewMap)).getMap();
        }

        googleMap.setMapType(GoogleMap.MAP_TYPE_HYBRID);
        googleMap.setMyLocationEnabled(true);
//        CameraPosition cameraPosition = new CameraPosition.Builder().target(
//                new LatLng(23.21644533, 72.64897903)).zoom(16).build();
//
//        googleMap.animateCamera(CameraUpdateFactory.newCameraPosition(cameraPosition));

        readFromFile();
        plotAll();
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
    public LatLng calc_mid(List<double[]> geofencePoints) {
        LatLng l;
        double lat = 0;
        double longi = 0;
        for (int i = 0; i < geofencePoints.size(); i++) {
            lat = geofencePoints.get(i)[0] + lat;
            longi = geofencePoints.get(i)[1] + longi;
        }
        lat = lat / geofencePoints.size();
        longi = longi / geofencePoints.size();

        l = new LatLng(lat, longi);

        return l;
    }
    public void plotAll() {

        for (int j = 0; j < geofencesList.size(); j++) {
            PolygonOptions rectOptions = new PolygonOptions();
            GeoFence geoFence =  geofencesList.get(j);
            List<double[]> geofencePoints = geoFence.geoFencePoints;
            String name = geoFence.geoFenceName;
            String imagePath = geoFence.imgPath;

            for (int i = 0; i < geofencePoints.size(); i++) {
                rectOptions.add(new LatLng(geofencePoints.get(i)[0], geofencePoints.get(i)[1]));
            }

            googleMap.addPolygon(rectOptions);
            BitmapDescriptor b = BitmapDescriptorFactory.fromBitmap(drawCustomBitmap(name,imagePath));
            MarkerOptions marker = new MarkerOptions().position(calc_mid(geofencePoints)).icon(b);
            googleMap.addMarker(marker);
        }
    }

    public Bitmap drawCustomBitmap(String text,String imagePath) {

        Resources resources = getResources();
        float scale = resources.getDisplayMetrics().density;

        File image = new File(imagePath);
        BitmapFactory.Options bmOptions = new BitmapFactory.Options();
        Bitmap bitmap = BitmapFactory.decodeFile(image.getAbsolutePath(),bmOptions);
        bitmap = Bitmap.createScaledBitmap(bitmap,200,200,true);
        Canvas canvas = new Canvas(bitmap);
        Paint paint = new Paint(Paint.ANTI_ALIAS_FLAG);
        paint.setColor(Color.rgb(255, 255, 255));
        paint.setTextSize((int) (14 * scale));
        paint.setShadowLayer(1f, 0f, 1f, Color.WHITE);

        Rect bounds = new Rect();
        paint.getTextBounds(text, 0, text.length(), bounds);

        int x = (bitmap.getWidth() - bounds.width()) / 2;
        int y = (bitmap.getHeight() + bounds.height()) / 2;

        canvas.drawText(text, x, y, paint);

        return bitmap;
    }
}
