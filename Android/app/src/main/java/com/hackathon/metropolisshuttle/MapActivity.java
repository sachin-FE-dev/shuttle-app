package com.hackathon.metropolisshuttle;

/**
 * Created by Kavya Shravan on 03/04/25.
 */

import android.Manifest;
import android.app.ProgressDialog;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.graphics.drawable.ColorDrawable;
import android.location.Location;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.PopupMenu;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;

import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationCallback;
import com.google.android.gms.location.LocationRequest;
import com.google.android.gms.location.LocationResult;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.gson.Gson;
import com.hackathon.metropolisshuttle.model.RouteData;
import com.hackathon.metropolisshuttle.network.APIService;
import com.hackathon.metropolisshuttle.network.RetrofitClient;

import org.java_websocket.client.WebSocketClient;
import org.java_websocket.handshake.ServerHandshake;

import java.net.URI;
import java.net.URISyntaxException;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class MapActivity extends AppCompatActivity implements OnMapReadyCallback {

    private static final String TAG = "Location Details";
    private static final int REQUEST_LOCATION_PERMISSION = 1;
    private GoogleMap mMap;
    private FusedLocationProviderClient fusedLocationClient;
    private WebSocketClient webSocketClient;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_map);

        callRoutesAPI();

        // Get the ActionBar
        ActionBar actionBar = getSupportActionBar();
        if (actionBar != null) {
            // Set the action bar background color programmatically
            actionBar.setBackgroundDrawable(new ColorDrawable(getResources().getColor(R.color.primary)));
        }

        Gson gson = new Gson();

        // Get the SupportMapFragment and notify when the map is ready to be used
        SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager()
                .findFragmentById(R.id.map);
        if (mapFragment != null) {
            mapFragment.getMapAsync(this);
        }

        fusedLocationClient = LocationServices.getFusedLocationProviderClient(this);

        // Start WebSocket connection
        URI uri;
        try {
            uri = new URI("wss://c395-14-143-126-241.ngrok-free.app");
            webSocketClient = new WebSocketClient(uri) {
                @Override
                public void onOpen(ServerHandshake handshakedata) {
                    runOnUiThread(() -> {
                        Log.d(TAG, "onOpen: ");
                        //Toast.makeText(MapActivity.this, "WebSocket Connected", //Toast.LENGTH_SHORT).show();
                    });
                }

                @Override
                public void onMessage(String message) {
                    // Handle messages if needed
                }

                @Override
                public void onClose(int code, String reason, boolean remote) {
                    runOnUiThread(() -> {
                        Log.d(TAG, "onClose: ");
                        //Toast.makeText(MapActivity.this, "WebSocket Closed", //Toast.LENGTH_SHORT).show();
                    });
                }

                @Override
                public void onError(Exception ex) {
                    runOnUiThread(() -> {
                        Log.d(TAG, "onError: " + ex.getMessage());
                        Toast.makeText(MapActivity.this, "WebSocket Error: " + ex.getMessage(), Toast.LENGTH_SHORT).show();
                    });
                }
            };
            webSocketClient.connect();
        } catch (URISyntaxException e) {
            e.printStackTrace();
        }

        // Request location updates
        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.ACCESS_FINE_LOCATION}, REQUEST_LOCATION_PERMISSION);
            return;
        }

        startLocationUpdates();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu
        getMenuInflater().inflate(R.menu.menu_map, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Check if the action item clicked is "More Options"
        if (item.getItemId() == R.id.action_more_options) {
            // Show PopupMenu when the action item is clicked
            showPopupMenu();
            return true;
        }
        return super.onOptionsItemSelected(item);
    }

    private void showPopupMenu() {
        PopupMenu popupMenu = new PopupMenu(this, findViewById(R.id.action_more_options));
        popupMenu.getMenuInflater().inflate(R.menu.popup_menu, popupMenu.getMenu());
        popupMenu.setOnMenuItemClickListener(menuItem -> {
            if (menuItem.getItemId() == R.id.logout) {
                logout();
                return true;
            }
            return false;
        });

        // Show the PopupMenu
        popupMenu.show();
    }

    private void logout() {
        SharedPreferences sharedPreferences = getSharedPreferences("MyPreferences", MODE_PRIVATE);
        SharedPreferences.Editor editor = sharedPreferences.edit();
        editor.clear();
        editor.apply();
        Intent intent = new Intent(MapActivity.this, LoginActivity.class);
        startActivity(intent);
        finish();
    }

    private void startLocationUpdates() {
        LocationRequest locationRequest = LocationRequest.create();
        locationRequest.setInterval(10000);  // 10 seconds interval
        locationRequest.setFastestInterval(5000); // Fastest interval for location updates
        locationRequest.setPriority(LocationRequest.PRIORITY_HIGH_ACCURACY);

        LocationCallback locationCallback = new LocationCallback() {
            @Override
            public void onLocationResult(@NonNull LocationResult locationResult) {
                super.onLocationResult(locationResult);
                if (locationResult.getLocations().size() > 0) {
                    Location location = locationResult.getLastLocation();
                    Log.d(TAG, "onLocationResult: " + location.getLatitude() + " " + location.getLongitude());
                    sendLocationToWebSocket(location);
                }
            }
        };

        fusedLocationClient.requestLocationUpdates(locationRequest, locationCallback, null);
    }

    private void sendLocationToWebSocket(Location location) {
        // current location can be sent to web socket from here. This method is not implemented as we are simulating the data using US locations.
    }

    @Override
    protected void onPause() {
        super.onPause();
        if (webSocketClient != null && webSocketClient.isOpen()) {
            webSocketClient.close();
        }
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        if (webSocketClient != null) {
            webSocketClient.close();
        }
    }

    @Override
    public void onMapReady(@NonNull GoogleMap googleMap) {
        mMap = googleMap;

        // Check permission to access location
        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED
                && ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            // Request permission if not granted
            ActivityCompat.requestPermissions(this,
                    new String[]{Manifest.permission.ACCESS_FINE_LOCATION}, REQUEST_LOCATION_PERMISSION);
            return;
        }

        mMap.setMyLocationEnabled(true); // Enable the "my location" button on the map

        // Get the current location and move the camera to it
        fusedLocationClient.getLastLocation()
                .addOnSuccessListener(this, location -> {
                    if (location != null) {
                        // Get the user's current location
                        LatLng currentLocation = new LatLng(location.getLatitude(), location.getLongitude());

                        // Add a marker at the current location and move the camera
                        mMap.addMarker(new MarkerOptions().position(currentLocation).title("You are here").icon(BitmapDescriptorFactory.fromResource(R.drawable.ic_pin)).anchor(0.5f, 0.75f));
                        mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(currentLocation, 15));

                    } else {
                        Toast.makeText(MapActivity.this, "Unable to get current location", Toast.LENGTH_SHORT).show();
                    }
                });
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if (requestCode == REQUEST_LOCATION_PERMISSION) {
            if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                // Permission granted, try to get the current location again
                onMapReady(mMap);
                startLocationUpdates();
            } else {
                Toast.makeText(this, "Permission denied", Toast.LENGTH_SHORT).show();
            }
        }
    }

    private void callRoutesAPI() {
        ProgressDialog progressDialog = new ProgressDialog(this);
        Utils.showProgressDialog(progressDialog);
        // Retrofit setup
        APIService apiService = RetrofitClient.getClient(this).create(APIService.class);
        Call<RouteData> call = apiService.getRoutes();

        call.enqueue(new Callback<RouteData>() {
            @Override
            public void onResponse(@NonNull Call<RouteData> call, @NonNull Response<RouteData> response) {
                if (response.isSuccessful()) {
                    Utils.hideProgressDialog(progressDialog);
                    // Handle the response
                    RouteData routeData = response.body();
                    if (routeData != null && routeData.isSuccess()) {
                        SimulateData simulateData = new SimulateData(mMap, webSocketClient, routeData);
                        TextView routeNameText = findViewById(R.id.routeName);
                        routeNameText.setText(routeData.getData().get(0).getName());
                        TextView routeColorText = findViewById(R.id.routeColor);
                        routeColorText.setText(routeData.getData().get(0).getColor().toUpperCase());
                    } else {
                        Utils.hideProgressDialog(progressDialog);
                        //Toast.makeText(MapActivity.this, "Routes failed. Please try again.", //Toast.LENGTH_SHORT).show();
                    }
                } else {
                    Utils.hideProgressDialog(progressDialog);
                    Log.d("TAG", "onResponse: " + response.message());
                    Toast.makeText(MapActivity.this, "Error: " + response.message(), Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onFailure(@NonNull Call<RouteData> call, @NonNull Throwable t) {
                Utils.hideProgressDialog(progressDialog);
                Toast.makeText(MapActivity.this, "Error: " + t.getMessage(), Toast.LENGTH_SHORT).show();
            }
        });
    }
}
