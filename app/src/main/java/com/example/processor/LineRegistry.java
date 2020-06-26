package com.example.processor;

import android.content.Context;
import android.graphics.Color;
import android.graphics.DashPathEffect;
import android.graphics.drawable.Drawable;
import android.graphics.drawable.Icon;
import android.util.Log;
import com.example.hustmap.MainActivity;

import org.osmdroid.api.IMapController;
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
import org.osmdroid.views.overlay.Polyline;

public class LineRegistry {
    public static MapView drawLine(MapView mapView, String start, String end, Drawable startIcon, Drawable endIcon, Context context) throws NumberFormatException, SQLException, IOException, JSONException {
        System.setProperty("https.protocols", "TLSv1,TLSv1.1,TLSv1.2");
        OkHttpClient client = new OkHttpClient.Builder()
                //default timeout for not annotated requests
                .connectTimeout(60, TimeUnit.SECONDS)
                .readTimeout(60, TimeUnit.SECONDS)
                .callTimeout(60, TimeUnit.SECONDS)
                .writeTimeout(60, TimeUnit.MILLISECONDS)
                .build();
        HttpUrl url;
        if(start.equals("Current location")){
            start = getCurrentLocation.get(context);
            url = HttpUrl.parse(MainActivity.host + "rest/connect/queryFromGPS").newBuilder()
                    .addQueryParameter("start", start)
                    .addQueryParameter("end", end)
                    .build();
        }
        else if(end.equals("Current location")){
            end = getCurrentLocation.get(context);
            url = HttpUrl.parse(MainActivity.host + "rest/connect/queryToGPS").newBuilder()
                    .addQueryParameter("start", start)
                    .addQueryParameter("end", end)
                    .build();
        }
        else if(end.equals("Custom location")){
            end = MainActivity.getCustomLocation();
            url = HttpUrl.parse(MainActivity.host + "rest/connect/queryToGPS").newBuilder()
                    .addQueryParameter("start", start)
                    .addQueryParameter("end", end)
                    .build();
        }
        else if(start.equals("Custom location")){
            start = MainActivity.getCustomLocation();
            url = HttpUrl.parse(MainActivity.host + "rest/connect/queryFromGPS").newBuilder()
                    .addQueryParameter("start", start)
                    .addQueryParameter("end", end)
                    .build();
        }
        else {
             url = HttpUrl.parse(MainActivity.host + "rest/connect/queryPath").newBuilder()
                    .addQueryParameter("start", start)
                    .addQueryParameter("end", end)
                    .build();
        }
        Request request = new Request.Builder()
                .url(url)
                .build();

        Call call = client.newCall(request);
        Response response = call.execute();
        String jsonString = response.body().string();

        JSONArray array = new JSONArray(jsonString);

        for(int i = 0; i < array.length()-2; i++) {
            JSONArray arr = array.getJSONArray(i);
            List<GeoPoint> geoPoints = new ArrayList<>();
            Polyline line = new Polyline();
            for(int j = 0; j < arr.length(); j++){
                JSONArray mini = arr.getJSONArray(j);
                double longitude = Double.parseDouble(mini.getString(0));
                double latitude = Double.parseDouble(mini.getString(1));
                GeoPoint point = new GeoPoint(latitude, longitude);
                geoPoints.add(point);
            }
            line.setPoints(geoPoints);
            mapView.getOverlays().add(line);
        }

        JSONArray arr1 = array.getJSONArray(array.length()-2);
        JSONArray mini1 = arr1.getJSONArray(0);
        double longitude = Double.parseDouble(mini1.getString(0));
        double latitude = Double.parseDouble(mini1.getString(1));
        GeoPoint point1 = new GeoPoint(latitude, longitude);
        Marker endMarker = new Marker(mapView);
        //endMarker.setIcon(icon);
        endMarker.setPosition(point1);
        endMarker.setIcon(endIcon);
        endMarker.setAnchor((float) 0.15, (float) 0.4);
        mapView.getOverlays().add(endMarker);

        JSONArray arr2 = array.getJSONArray(array.length()-1);
        JSONArray mini2 = arr2.getJSONArray(0);
        double longitude2 = Double.parseDouble(mini2.getString(0));
        double latitude2 = Double.parseDouble(mini2.getString(1));
        GeoPoint point2 = new GeoPoint(latitude2, longitude2);
        Marker startMarker = new Marker(mapView);
        startMarker.setPosition(point2);
        startMarker.setIcon(startIcon);
        startMarker.setAnchor((float) 0.15, (float) 0.4);
        mapView.getOverlays().add(startMarker);

        IMapController mapController = mapView.getController();
        mapController.setZoom(19);
        mapController.animateTo(point2);

        if(start.equals("Current location") || end.equals("Current location") || start.equals("Custom location") || end.equals("Custom location")) {
            GeoPoint gp = MainActivity.currLoc;
            Marker curr = new Marker(mapView);
            curr.setPosition(gp);
            curr.setAnchor((float) 0.5, (float) 0.5);

            List<GeoPoint> gps = new ArrayList<>();
            gps.add(gp);
            gps.add(point2);


            Polyline line2 = new Polyline();

            line2.setPoints(gps);
            line2.getPaint().setPathEffect(new DashPathEffect(new float[]{10, 20}, 0));

            mapView.getOverlays().add(line2);
            mapView.getOverlays().add(curr);
        }

        return mapView;
    }
}
