package com.example.processor;

import android.graphics.Canvas;
import android.graphics.Matrix;

import org.osmdroid.views.MapView;
import org.osmdroid.views.overlay.Marker;

public class CustomMarker extends Marker {
    public CustomMarker(MapView mapView) {
        super(mapView);
    }

    @Override
    public void draw(Canvas canvas, MapView mapView, boolean shadow)
    {
        canvas.save();
        canvas.setMatrix(new Matrix());
        super.draw(canvas, mapView, shadow);
        canvas.restore();
    }
}
