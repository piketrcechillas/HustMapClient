package com.example.processor;

import android.graphics.Color;
import android.util.Log;

import com.example.hustmap.MainActivity;

import org.osmdroid.util.GeoPoint;
import org.osmdroid.views.MapView;
import org.osmdroid.views.overlay.Marker;

import java.io.IOException;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.TimeUnit;

import okhttp3.Call;
import okhttp3.HttpUrl;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;
import org.json.*;
import org.osmdroid.views.overlay.Polygon;
import org.osmdroid.views.overlay.Polyline;

public class InitializeZone {
    public static MapView drawPolygon(MapView mapView) throws NumberFormatException, SQLException, IOException, JSONException {

        JSONArray array = new JSONArray(MainActivity.zone);

        for(int i = 0; i < array.length(); i++) {
            JSONArray arr = array.getJSONArray(i);
            List<GeoPoint> geoPoints = new ArrayList<>();
            Polygon polygon = new Polygon();
            for(int j = 0; j < arr.length(); j++){
                JSONArray mini = arr.getJSONArray(j);
                float longitude = Float.parseFloat(mini.getString(0));
                float latitude = Float.parseFloat(mini.getString(1));
                GeoPoint point = new GeoPoint(latitude, longitude);
                geoPoints.add(point);
            }
            geoPoints.add(geoPoints.get(0));
            //polygon.setFillColor(0x7FFF0000);
            polygon.setStrokeWidth(3);
            polygon.setStrokeColor(0xFFFF0000);
            polygon.setPoints(geoPoints);
            mapView.getOverlays().add(polygon);
        }

        return mapView;
    }
}
