package com.example.hustmap;

import android.util.Log;

import org.osmdroid.util.GeoPoint;
import org.osmdroid.views.MapView;
import org.osmdroid.views.overlay.Marker;

import java.io.IOException;
import java.sql.SQLException;
import java.util.concurrent.TimeUnit;

import okhttp3.Call;
import okhttp3.HttpUrl;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;
import org.json.*;

public class PointRegistry {
    public static MapView drawPoint(MapView mapView, String query) throws NumberFormatException, SQLException, IOException, JSONException {
        System.setProperty("https.protocols", "TLSv1,TLSv1.1,TLSv1.2");
        OkHttpClient client = new OkHttpClient.Builder()
                //default timeout for not annotated requests
                .connectTimeout(60, TimeUnit.SECONDS)
                .readTimeout(60, TimeUnit.SECONDS)
                .callTimeout(60, TimeUnit.SECONDS)
                .writeTimeout(60, TimeUnit.MILLISECONDS)
                .build();
        
        HttpUrl url = HttpUrl.parse("http://192.168.43.230:8080/HustMapServer/rest/connect/queryPoint").newBuilder()
                .addQueryParameter("q", query)
                .build();

        Request request = new Request.Builder()
                .url(url)
                .build();

        Call call = client.newCall(request);
        Response response = call.execute();
        String jsonString = response.body().string();

        JSONArray array = new JSONArray(jsonString);

        for(int i = 0; i < array.length(); i++) {
            JSONArray arr = array.getJSONArray(i);
            float longitude = Float.parseFloat(arr.getString(0));
            float latitude = Float.parseFloat(arr.getString(1));
            GeoPoint startPoint = new GeoPoint(latitude, longitude);
            Marker startMarker = new Marker(mapView);
            startMarker.setPosition(startPoint);
            mapView.getOverlays().add(startMarker);
        }

        return mapView;
    }
}
