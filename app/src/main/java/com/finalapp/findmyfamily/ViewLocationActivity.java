package com.finalapp.findmyfamily;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

import android.Manifest;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.location.Address;
import android.location.Geocoder;
import android.location.Location;
import android.location.LocationManager;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.SystemClock;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import com.finalapp.findmyfamily.databinding.ActivityMainBinding;
import com.finalapp.findmyfamily.model.LocClass;
import com.finalapp.findmyfamily.model.Request;
import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.android.gms.tasks.OnSuccessListener;

import java.io.IOException;
import java.util.List;
import java.util.Locale;

public class ViewLocationActivity extends AppCompatActivity {

    FusedLocationProviderClient fusedLocationProviderClient;
    TextView latitude, longitude;
    Button getLocation;
    private LocationManager locManager;
    private ActivityMainBinding binding;
    private final static int REQUEST_CODE = 100;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_view_location);

        latitude = findViewById(R.id.latitude);
        longitude = findViewById(R.id.logitude);
        getLocation = findViewById(R.id.getLocationButton);

        fusedLocationProviderClient = LocationServices.getFusedLocationProviderClient(this);

        getLocation.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                getLastLocation();

//                LocClass location;
//                LatLng latLng = new LatLng(location.getLatitude(), location.getLongitude());
//                MarkerOptions markerOptions = new MarkerOptions();
//                markerOptions.position(latLng);
//                markerOptions.title("Current Position");
//                markerOptions.icon(BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_MAGENTA));
//                Object mCurrLocationMarker = mGoogleMap.addMarker(markerOptions);

//                boolean locationEnabled = locManager.isProviderEnabled(LocationManager.GPS_PROVIDER);
//                boolean waitingForLocation = locationEnabled && !validLocation(location);
//                boolean haveLocation = locationEnabled && !waitingForLocation;
//
//                if (haveLocation) {
//
//                    String uri = formatLocation(lastLocation, "geo:{0},{1}?q={0},{1}");
//                    Intent intent = new Intent(Intent.ACTION_VIEW, Uri.parse(uri));
//                    startActivity(Intent.createChooser(intent, getString(R.string.view_location_via)));
//                }
            }
        });
    }

    private boolean validLocation(Location location) {
        if (location == null) {
            return false;
        }

        // Location must be from less than 30 seconds ago to be considered valid
        if (Build.VERSION.SDK_INT < 17) {
            return System.currentTimeMillis() - location.getTime() < 30e3;
        } else {
            return SystemClock.elapsedRealtimeNanos() - location.getElapsedRealtimeNanos() < 30e9;
        }
    }

    private void getLastLocation() {

        if (ContextCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED) {
            fusedLocationProviderClient.getLastLocation()
                    .addOnSuccessListener(new OnSuccessListener<Location>() {
                        @Override
                        public void onSuccess(Location location) {
                            if (location != null) {
                                Geocoder geocoder = new Geocoder(ViewLocationActivity.this, Locale.getDefault());
                                List<Address> address = null;
                                try {
                                    address = geocoder.getFromLocation(location.getLatitude(), location.getLongitude(), 1);
                                    latitude.setText("Latitude :" + address.get(0).getLatitude());
                                    longitude.setText("Longitude :" + address.get(0).getLongitude());
                                } catch (IOException e) {
                                    e.printStackTrace();
                                }
                            }
                        }
                    });
        } else {
            askPermission();
        }
    }

    private void askPermission() {
        ActivityCompat.requestPermissions(ViewLocationActivity.this, new String[]
                {Manifest.permission.ACCESS_FINE_LOCATION}, REQUEST_CODE);
    }
}