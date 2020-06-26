package com.example.processor;

import android.Manifest;
import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.Context;
import android.content.pm.PackageManager;
import android.location.Location;
import android.location.LocationManager;
import android.util.Log;

import androidx.core.app.ActivityCompat;

import com.example.hustmap.MainActivity;

import org.osmdroid.api.IMapController;
import org.osmdroid.util.GeoPoint;
import org.osmdroid.views.MapView;
import org.osmdroid.views.overlay.Marker;

import java.io.IOException;
import java.sql.SQLException;

import org.json.*;

import static androidx.core.content.ContextCompat.getSystemService;

public class getCurrentLocation {

    public static String get(Context context) throws NumberFormatException, SQLException, IOException, JSONException {
        LocationManager locationManager = (LocationManager) context.getSystemService(Context.LOCATION_SERVICE);


        @SuppressLint("MissingPermission")
        Location location = locationManager.getLastKnownLocation(LocationManager.GPS_PROVIDER);

        System.out.println(location.getLatitude());
        System.out.println(location.getLongitude());

        double lat = (double) (location.getLatitude());
        double lng = (double) (location.getLongitude());

        String str = lng + " " + lat;
        System.out.println(str);

        return str;
    }

}
