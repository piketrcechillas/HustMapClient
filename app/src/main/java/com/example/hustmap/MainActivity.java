package com.example.hustmap;

import android.Manifest;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.os.StrictMode;
import android.preference.PreferenceManager;
import android.view.View;
import android.widget.Button;
import android.widget.CompoundButton;
import android.widget.EditText;
import android.widget.Switch;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

import com.example.processor.CurrentLocationRegistry;
import com.example.processor.InitializeLabel;
import com.example.processor.LineRegistry;
import com.google.android.material.floatingactionbutton.FloatingActionButton;

import org.json.JSONException;
import org.osmdroid.api.IMapController;
import org.osmdroid.config.Configuration;
import org.osmdroid.events.MapEventsReceiver;
import org.osmdroid.tileprovider.tilesource.TileSourceFactory;
import org.osmdroid.util.GeoPoint;
import org.osmdroid.views.MapView;
import org.osmdroid.views.overlay.MapEventsOverlay;
import org.osmdroid.views.overlay.Marker;
import org.osmdroid.views.overlay.mylocation.MyLocationNewOverlay;

import java.io.IOException;
import java.sql.SQLException;
import java.util.ArrayList;

public class MainActivity extends AppCompatActivity{
    private final int REQUEST_PERMISSIONS_REQUEST_CODE = 1;
    private MapView map = null;
    private MyLocationNewOverlay mLocationOverlay;
    private static String customLocation;
    private static Marker mark;


    public static String host = "https://hustmapapi.azurewebsites.net/";
    public static String zone;
    public static GeoPoint currLoc;



    EditText start, end;
    FloatingActionButton gps, main, scan, type;
    Button query, reset;


    private String[] listItem = {"Current location", "Nhà D6", "Nhà C1", "Nhà TC", "Sân vận động Bách Khoa", "Trung tâm ngoại ngữ CFL", "Khoa Tại chức", "Cao đẳng nghề Bách khoa"
                                , "Nhà D8", "Hồ Tiền", "Nhà D9", "Nhà D7", "Nhà D5", "Love Hust", "Nhà C7", "Nhà B6", "Nhà C9", "Cafe Mộc"
                                , "Nhà B13", "Nhà B5", "Nhà T"};
    private String[] infoItem;
    private Drawable startIcon;
    private Drawable endIcon;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        requestPermissionsIfNecessary(new String[] {
                Manifest.permission.WRITE_EXTERNAL_STORAGE
        });
        requestPermissionsIfNecessary(new String[] {
                Manifest.permission.ACCESS_FINE_LOCATION
        });
        requestPermissionsIfNecessary(new String[] {
                Manifest.permission.INTERNET
        });
        requestPermissionsIfNecessary(new String[] {
                Manifest.permission.ACCESS_NETWORK_STATE
        });

        StrictMode.ThreadPolicy policy = new StrictMode.ThreadPolicy.Builder().permitAll().build();

        StrictMode.setThreadPolicy(policy);
        super.onCreate(savedInstanceState);

        Context ctx = getApplicationContext();
        Configuration.getInstance().load(ctx, PreferenceManager.getDefaultSharedPreferences(ctx));

        setContentView(R.layout.activity_main);
        endIcon = getResources().getDrawable( R.drawable.dest );
        startIcon = getResources().getDrawable( R.drawable.start );

        try {
            listItem = InitializeLabel.getLabel();
        } catch (JSONException e) {
            e.printStackTrace();
        }
        //GetZone.getZone();

        map = (MapView) findViewById(R.id.map);
        map.setTileSource(TileSourceFactory.MAPNIK);
        //map.setBuiltInZoomControls(true);
        map.setMultiTouchControls(true);
    /*
        try {
           // map = PointRegistry.drawPoint(map, "cafe");
           //map = PolygonToPoint.transformPolygon(map, "");
            map = LineRegistry.drawLine(map, "Nhà C1", "Nhà TC");
        } catch (SQLException | IOException | JSONException e) {
            e.printStackTrace();
        }
    */


        IMapController mapController = map.getController();
        mapController.setZoom(16);
        GeoPoint startPoint = new GeoPoint(21.003983, 105.845836);
        mapController.setCenter(startPoint);

        MapEventsReceiver mReceive = new MapEventsReceiver() {
            @Override
            public boolean singleTapConfirmedHelper(GeoPoint p) {
                if(mark != null) {
                    map.getOverlays().remove(mark);}
                start.setText("Custom location");
                customLocation = p.getLongitude() + " " + p.getLatitude();
                System.out.println(customLocation);
                setGP(p);
                mark = new Marker(map);
                mark.setPosition(p);

                map.getOverlays().add(mark);
                map.invalidate();

                return true;
            }

            @Override
            public boolean longPressHelper(GeoPoint p) {
                // write your code here
                return false;
            }
        };
        final MapEventsOverlay OverlayEvents = new MapEventsOverlay(getBaseContext(), mReceive);
        map.getOverlays().add(OverlayEvents);

        requestPermissionsIfNecessary(new String[] {
                Manifest.permission.WRITE_EXTERNAL_STORAGE
        });


        start = findViewById(R.id.start);
        end = findViewById(R.id.end);
        query = findViewById(R.id.query);
        reset = findViewById(R.id.reset);
        gps = findViewById(R.id.gps);
        main = findViewById(R.id.main2);
        scan = findViewById(R.id.scan2);
        type = findViewById(R.id.type3);

        start.setFocusableInTouchMode(false);
        end.setFocusableInTouchMode(false);

        start.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                AlertDialog.Builder mBuilder = new AlertDialog.Builder(MainActivity.this);
                mBuilder.setTitle("Choose starting location");
                mBuilder.setSingleChoiceItems(listItem, -1, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int i) {
                        if(listItem[i].equals(end.getText().toString())){
                            AlertDialog alertDialog = new AlertDialog.Builder(MainActivity.this).create();
                            alertDialog.setTitle("Alert");
                            alertDialog.setMessage("Starting position and Destination must not be similar!");
                            alertDialog.setButton(AlertDialog.BUTTON_NEGATIVE, "OK",
                                    new DialogInterface.OnClickListener() {
                                        public void onClick(DialogInterface dialog, int which) {
                                            dialog.dismiss();
                                        }
                                    });
                            alertDialog.show();
                        }
                        else {
                            start.setText(listItem[i]);
                            dialog.dismiss();
                        }
                    }
                });
                AlertDialog mDialog = mBuilder.create();
                mDialog.setButton(AlertDialog.BUTTON_NEUTRAL, "Cancel",
                        new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialog, int which) {
                                dialog.dismiss();
                            }
                        });
                mDialog.show();

            }
        });

        end.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                AlertDialog.Builder mBuilder = new AlertDialog.Builder(MainActivity.this);
                mBuilder.setTitle("Choose destination");
                mBuilder.setSingleChoiceItems(listItem, -1, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int i) {
                        if(listItem[i].equals(start.getText().toString())){
                            AlertDialog alertDialog = new AlertDialog.Builder(MainActivity.this).create();
                            alertDialog.setTitle("Alert");
                            alertDialog.setMessage("Starting position and Destination must not be similar!");
                            alertDialog.setButton(AlertDialog.BUTTON_NEGATIVE, "OK",
                                    new DialogInterface.OnClickListener() {
                                        public void onClick(DialogInterface dialog, int which) {
                                            dialog.dismiss();
                                        }
                                    });
                            alertDialog.show();
                        }
                        else {
                            end.setText(listItem[i]);
                            dialog.dismiss();
                        }
                    }
                });
                AlertDialog mDialog = mBuilder.create();
                mDialog.setButton(AlertDialog.BUTTON_NEUTRAL, "Cancel",
                        new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialog, int which) {
                                dialog.dismiss();
                            }
                        });
                mDialog.show();
            }
        });

        query.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {
                map.getOverlays().clear();
                String startPos = start.getText().toString();
                String endPos = end.getText().toString();
                try {
                    map = LineRegistry.drawLine(map, startPos, endPos, startIcon, endIcon, MainActivity.this);
                } catch (SQLException | IOException | JSONException e) {
                    e.printStackTrace();
                }
                map.invalidate();
            }
        });

        reset.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {
                map.getOverlays().clear();
                map.invalidate();
                map.getOverlays().add(OverlayEvents);
            }
        });


        gps.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {
                try {
                    map = CurrentLocationRegistry.drawPoint(map, MainActivity.this);
                    start.setText("Current location");
                } catch (SQLException | IOException | JSONException e) {
                    e.printStackTrace();
                }
            }
        });

        scan.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {
                startActivity(new Intent(MainActivity.this, ScanActivity.class));
            }
        });

        type.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {
                startActivity(new Intent(MainActivity.this, TypeActivity.class));
            }
        });


    }

    @Override
    public void onResume() {
        super.onResume();

        map.onResume();
    }


    @Override
    public void onPause() {
        super.onPause();
        map.onPause();
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, String[] permissions, int[] grantResults) {
        ArrayList<String> permissionsToRequest = new ArrayList<>();
        for (int i = 0; i < grantResults.length; i++) {
            permissionsToRequest.add(permissions[i]);
        }
        if (permissionsToRequest.size() > 0) {
            ActivityCompat.requestPermissions(
                    this,
                    permissionsToRequest.toArray(new String[0]),
                    REQUEST_PERMISSIONS_REQUEST_CODE);
        }
    }

    public static void setZone(String jsonString){
        zone = jsonString;
    }

    public static void setGP(GeoPoint gp){
        currLoc = gp;
    }

    public static String getCustomLocation(){
        return customLocation;
    }

    private void requestPermissionsIfNecessary(String[] permissions) {
        ArrayList<String> permissionsToRequest = new ArrayList<>();
        for (String permission : permissions) {
            if (ContextCompat.checkSelfPermission(this, permission)
                    != PackageManager.PERMISSION_GRANTED) {
                permissionsToRequest.add(permission);
            }
        }
        if (permissionsToRequest.size() > 0) {
            ActivityCompat.requestPermissions(
                    this,
                    permissionsToRequest.toArray(new String[0]),
                    REQUEST_PERMISSIONS_REQUEST_CODE);
        }
    }
}