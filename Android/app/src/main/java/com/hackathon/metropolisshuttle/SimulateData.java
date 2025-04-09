package com.hackathon.metropolisshuttle;

import android.graphics.Color;
import android.os.Handler;
import android.util.Log;

import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.android.gms.maps.model.PolylineOptions;
import com.google.gson.Gson;
import com.hackathon.metropolisshuttle.model.RouteData;
import com.hackathon.metropolisshuttle.model.SocketData;
import com.hackathon.metropolisshuttle.model.Stop;

import org.java_websocket.client.WebSocketClient;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by Kavya Shravan on 03/04/25.
 */
public class SimulateData {

    Gson gson;
    private final GoogleMap mMap;
    private List<LatLng> routeCoordinates;
    private final WebSocketClient webSocketClient;
    private RouteData routeData;
    int colour = 0;

    public SimulateData(GoogleMap mMap, WebSocketClient webSocketClient, RouteData routeData) {
        this.mMap = mMap;
        this.webSocketClient = webSocketClient;
        gson = new Gson();
        if (routeData != null) {
            this.routeData = routeData;
        }
        simulate();
    }

    private void simulate() {
        routeCoordinates = new ArrayList<>();

        for (Stop stop : routeData.getData().get(0).getStops()) {
            routeCoordinates.add(new LatLng(stop.getCoords().get(0), stop.getCoords().get(1)));
        }

        for (LatLng latLng : routeCoordinates) {
            addBlackDotMarker(latLng);
        }

        LatLng startLocation = routeCoordinates.get(0);
        //mMap.addMarker(new MarkerOptions().position(startLocation).title("Start Location"));

        // Move the camera to the start point
        mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(startLocation, 15));

        PolylineOptions greyPolylineOptions = new PolylineOptions()
                .addAll(routeCoordinates) // Add all points to the polyline
                .color(Color.GRAY)         // Set the polyline color to grey
                .width(8);                 // Set the polyline width
        mMap.addPolyline(greyPolylineOptions);

        String color = routeData.getData().get(0).getColor();

        if (color.equals("green")) {
            colour = Color.GREEN;
        } else if (color.equals("red")) {
            colour = Color.RED;
        } else if (color.equals("blue")) {
            colour = Color.BLUE;
        } else if (color.equals("yellow")) {
            colour = Color.YELLOW;
        } else if (color.equals("black")) {
            colour = Color.BLACK;
        }

        PolylineOptions bluePolylineOptions = new PolylineOptions()
                .add(routeCoordinates.get(0))
                .color(colour)         // Set the polyline color to blue
                .width(8);                 // Set the polyline width
        mMap.addPolyline(bluePolylineOptions);

        simulateDriverMovement();
    }

    // Simulate the driver moving along the route
    private void simulateDriverMovement() {

        // Create a marker at the start location
        Marker shuttleMarker = mMap.addMarker(new MarkerOptions().position(routeCoordinates.get(0)).title("Shuttle").icon(BitmapDescriptorFactory.fromResource(R.drawable.bus)).anchor(0.5f, 0.75f));

        // Animate shuttle along the route
        for (int i = 0; i < routeCoordinates.size(); i++) {
            final LatLng previousPoint;
            if (i > 0) {
                previousPoint = routeCoordinates.get(i - 1);
            } else {
                previousPoint = routeCoordinates.get(i);
            }
            final LatLng nextPoint = routeCoordinates.get(i);
            //shuttleMarker.setPosition(nextPoint);

            // Use a handler to update marker position after a delay
            new Handler().postDelayed(() -> {
                if (shuttleMarker != null) {
                    shuttleMarker.setPosition(nextPoint);
                }

                sendLocationToWebSocket(nextPoint);

                mMap.addPolyline(new PolylineOptions()
                        .add(previousPoint, nextPoint)
                        .width(8)
                        .color(colour));

                mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(nextPoint, 19));
            }, i * 3000L); // Adjust the delay for smoother movement
        }
    }

    private void addBlackDotMarker(LatLng latLng) {
        // Customize the marker to show a black dot
        MarkerOptions markerOptions = new MarkerOptions()
                .position(latLng)
                .icon(BitmapDescriptorFactory.fromResource(R.drawable.stop)).anchor(0.5f, 0.75f)
                .title("Point"); // Optional: Add a title to the marker

        // Add the marker to the map
        mMap.addMarker(markerOptions);
    }

    private void sendLocationToWebSocket(LatLng latLng) {
        if (webSocketClient != null && webSocketClient.isOpen()) {
            double latitude = latLng.latitude;
            double longitude = latLng.longitude;
            SocketData socketData = new SocketData(latitude, longitude);
            // Convert the LocationData object to JSON using Gson
            String jsonSocketData = gson.toJson(socketData);
            Log.d("Simulate", "sendLocationToWebSocket: " + jsonSocketData);
            webSocketClient.send(jsonSocketData);
        }
    }
}
