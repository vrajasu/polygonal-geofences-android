package com.vraj.polygonalgeofences;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.res.Resources;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Rect;
import android.graphics.drawable.BitmapDrawable;
import android.media.Image;
import android.net.Uri;
import android.os.Environment;
import android.provider.MediaStore;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Toast;

import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.MapFragment;
import com.google.android.gms.maps.model.BitmapDescriptor;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.CircleOptions;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.android.gms.maps.model.Polygon;
import com.google.android.gms.maps.model.PolygonOptions;

import org.json.JSONArray;
import org.json.JSONException;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.OutputStream;
import java.util.ArrayList;
import java.util.List;

public class AddGeofence extends AppCompatActivity {

    Button btnAddName,btnAddImage,btnAddGeofence;
    ImageView iv_GeoFenceimage;
    String nameGeofence = "";
    String imagePath = "";
    int countPoints = 0;
    GoogleMap googleMap;
    List<double[]> geofencePoints = new ArrayList<>();
    List<GeoFence> geofencesList = new ArrayList<>();
    String fileLocation= Environment.getExternalStorageDirectory() + File.separator +"/GeoFences"+File.separator+"Geofences";
    File file;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_geofence);
        btnAddImage=(Button)findViewById(R.id.addImageGeoFence);
        btnAddName=(Button)findViewById(R.id.addNameGeofence);
        btnAddGeofence=(Button)findViewById(R.id.addGeofence);
        iv_GeoFenceimage=(ImageView)findViewById(R.id.iv_geofence_image);

        if (googleMap == null) {
            googleMap = ((MapFragment) getFragmentManager().
                    findFragmentById(R.id.map)).getMap();
        }
        googleMap.setMapType(GoogleMap.MAP_TYPE_HYBRID);
        googleMap.setMyLocationEnabled(true);


        btnAddName.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                AlertDialog.Builder alert = new AlertDialog.Builder(AddGeofence.this);
                alert.setTitle("Adding GeoFence");
                alert.setMessage("Please enter name for your geofence");
                final EditText input = new EditText(AddGeofence.this);
                alert.setView(input);

                alert.setPositiveButton("Done", new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int whichButton) {
                        nameGeofence = input.getText().toString();
                    }
                });

                alert.setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int whichButton) {
                    }
                });
                alert.show();


            }
        });
        btnAddImage.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                selectImage();
            }
        });
        btnAddGeofence.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                if(nameGeofence.equals(""))
                {
                    Toast.makeText(AddGeofence.this,"Enter a name for the Geofence",Toast.LENGTH_LONG).show();
                }
                else if(imagePath.equals("")){
                    Toast.makeText(AddGeofence.this,"Select an image for the Geofence",Toast.LENGTH_LONG).show();
                }
                else {
                    if (countPoints >= 3) {
                        plotPoly();
                        writeToFile();
                        readFromFile();
                        geofencePoints.clear();
                    } else {
                        Toast.makeText(getApplicationContext(), "You need at least 3 points to plot", Toast.LENGTH_LONG).show();
                    }
                }
            }
        });

        googleMap.setOnMapLongClickListener(new GoogleMap.OnMapLongClickListener() {
            @Override
            public void onMapLongClick(LatLng latLng) {

                double temppoint[] = {latLng.latitude,latLng.longitude};
                geofencePoints.add(temppoint);
                countPoints++;
                CircleOptions marker = new CircleOptions().center(latLng).radius(1).strokeColor(Color.RED);
                googleMap.addCircle(marker);
            }
        });

        File folder = new File(Environment.getExternalStorageDirectory().getAbsolutePath() + "/GeoFences");
        file=new File(folder,"Geofences");

        readFromFile();

    }
    public void writeToFile(){
        GeoFence gf= new GeoFence(geofencePoints,imagePath,nameGeofence);
        geofencesList.add(gf);

        try {
            FileOutputStream fos =
                    new FileOutputStream(file);
            ObjectOutputStream os = new ObjectOutputStream(fos);
            os.writeObject(geofencesList);
            os.close();
        } catch(Exception ex) {
            ex.printStackTrace();
        }


    }
    public void readFromFile(){
        try{
            FileInputStream fis = new FileInputStream(fileLocation);
            ObjectInputStream ois = new ObjectInputStream(fis);
            geofencesList = (ArrayList<GeoFence>)ois.readObject();
            Log.d("geofencesList",""+geofencesList.size());
            ois.close();
        }catch (Exception e)
        {
            e.printStackTrace();
        }
    }
    private void selectImage() {

        final CharSequence[] options = { "Choose from Gallery","Cancel" };

        AlertDialog.Builder builder = new AlertDialog.Builder(AddGeofence.this);
        builder.setTitle("Add Image for Geofence");
        builder.setItems(options, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int item) {
               if (options[item].equals("Choose from Gallery")) {
                    Intent intent = new Intent(Intent.ACTION_PICK, android.provider.MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
                    startActivityForResult(intent, 2);
               }
               else if (options[item].equals("Cancel")) {
                    dialog.dismiss();
               }
            }
        });
        builder.show();
    }
    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (resultCode == RESULT_OK) {
            if (requestCode == 2) {
                Uri selectedImage = data.getData();
                String[] filePath = { MediaStore.Images.Media.DATA };
                Cursor c = getContentResolver().query(selectedImage,filePath, null, null, null);
                c.moveToFirst();
                int columnIndex = c.getColumnIndex(filePath[0]);
                String picturePath = c.getString(columnIndex);
                c.close();
                Bitmap thumbnail = (BitmapFactory.decodeFile(picturePath));
                imagePath = picturePath;
                iv_GeoFenceimage.setImageBitmap(thumbnail);
            }
        }
    }
    //calcu
    public LatLng calc_mid() {
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

    public void plotPoly() {
        PolygonOptions rectOptions = new PolygonOptions();

        for (int i = 0; i < geofencePoints.size(); i++) {
            rectOptions.add(new LatLng(geofencePoints.get(i)[0],geofencePoints.get(i)[1]));
        }

        googleMap.addPolygon(rectOptions);
        BitmapDescriptor b = BitmapDescriptorFactory.fromBitmap(drawCustomBitmap(nameGeofence));
        MarkerOptions marker = new MarkerOptions().position(calc_mid()).icon(b);
        googleMap.addMarker(marker);
    }

    public Bitmap drawCustomBitmap(String text) {
        Resources resources = getResources();
        float scale = resources.getDisplayMetrics().density;
        BitmapDrawable drawable = (BitmapDrawable)iv_GeoFenceimage.getDrawable();

        Bitmap bitmap = drawable.getBitmap();

        bitmap=Bitmap.createScaledBitmap(bitmap,200,200,true);

        android.graphics.Bitmap.Config bitmapConfig =
                bitmap.getConfig();
        if (bitmapConfig == null) {
            bitmapConfig = android.graphics.Bitmap.Config.ARGB_8888;
        }
        bitmap = bitmap.copy(bitmapConfig, true);
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
