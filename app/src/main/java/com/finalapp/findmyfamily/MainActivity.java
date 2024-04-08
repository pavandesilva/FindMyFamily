package com.finalapp.findmyfamily;

import static com.finalapp.findmyfamily.model.MyLocation.myLat;
import static com.finalapp.findmyfamily.model.MyLocation.myLng;

import android.Manifest;
import android.annotation.SuppressLint;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.location.Address;
import android.location.Geocoder;
import android.location.Location;
import android.os.Bundle;
import android.view.MenuItem;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.ActionBarDrawerToggle;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.core.view.GravityCompat;
import androidx.drawerlayout.widget.DrawerLayout;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.finalapp.findmyfamily.Utils.Common;
import com.finalapp.findmyfamily.model.LocClass;
import com.finalapp.findmyfamily.model.MyLocation;
import com.finalapp.findmyfamily.model.User;
import com.finalapp.findmyfamily.model.UserDetails;
import com.finalapp.findmyfamily.navigation.ProfileActivity;
import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.material.appbar.MaterialToolbar;
import com.google.android.material.navigation.NavigationBarView;
import com.google.android.material.navigation.NavigationView;
import com.google.errorprone.annotations.Var;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;

public class MainActivity extends AppCompatActivity implements NavigationView.OnNavigationItemSelectedListener,
        NavigationBarView.OnItemSelectedListener {

    FirebaseAuth firebaseAuth;
    DatabaseReference databaseReference;
    UserDetails userDetails;
    MyAdapter myAdapter;
    ArrayList<User> list;

    TextView latitudeTxt, longitudeTxt, cityTxt, areaTxt, countryTxt, locationTitle;
    RecyclerView membersListView;
    String latitude,longitude;
    FusedLocationProviderClient fusedLocationProviderClient;
    private final static int REQUEST_CODE = 100;

    private DrawerLayout drawerLayout;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        latitudeTxt = findViewById(R.id.latitude);
        longitudeTxt = findViewById(R.id.longitude);
        cityTxt = findViewById(R.id.myCity);
        areaTxt = findViewById(R.id.myArea);
        countryTxt = findViewById(R.id.myCountry);
        locationTitle = findViewById(R.id.myLocationTitle);

        membersListView = findViewById(R.id.membersList);
        membersListView.setHasFixedSize(true);
        membersListView.setLayoutManager(new LinearLayoutManager(this));

        list = new ArrayList<>();
        list.clear();
        myAdapter = new MyAdapter(this, list);
        membersListView.setAdapter(myAdapter);

        fusedLocationProviderClient = LocationServices.getFusedLocationProviderClient(this);

        firebaseAuth = FirebaseAuth.getInstance();
        FirebaseUser currentUser = firebaseAuth.getCurrentUser();
        FirebaseDatabase firebaseDatabase = FirebaseDatabase.getInstance();
        databaseReference = firebaseDatabase.getReference();
//        DatabaseReference membersReference = databaseReference.child("members");

        if (currentUser != null) {
            String userid = currentUser.getUid();

            drawerLayout = findViewById(R.id.home_layout);
            NavigationView navigationView = findViewById(R.id.navigation_view);
            MaterialToolbar toolbar = findViewById(R.id.main_toolbar);

            View headerView = navigationView.getHeaderView(0);
            ImageView imageViewProfile = headerView.findViewById(R.id.side_nav_pic);
            TextView headerTitle = headerView.findViewById(R.id.side_nav_header_title);
            TextView headerSubtitle = headerView.findViewById(R.id.side_nav_header_subtitle);

            setSupportActionBar(toolbar);
            navigationView.setNavigationItemSelectedListener(this);

            ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(this, drawerLayout, toolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close);
            drawerLayout.addDrawerListener(toggle);
            toggle.syncState();

            navigationView.setNavigationItemSelectedListener(this);

            databaseReference.child("users").child(userid).addListenerForSingleValueEvent(new ValueEventListener() {
                @Override
                public void onDataChange(@NonNull DataSnapshot snapshot) {

                    if (snapshot.exists()) {
                        userDetails = snapshot.getValue(UserDetails.class);
                        Common.loggedUser = snapshot.getValue(User.class);
                        if (userDetails != null) {
                            String fullName = userDetails.getFirstname() + " " + userDetails.getLastname();
                            headerTitle.setText(fullName);
                            headerSubtitle.setText(userDetails.getEmail());

                        Toast.makeText(MainActivity.this, "Welcome " + userDetails.getFirstname() + " " + userDetails.getLastname(), Toast.LENGTH_LONG).show();
                            getLastLocation();
                        }
                    } else {
                        startActivity(new Intent(getApplicationContext(), RegisterNewUserActivity.class));
                        finish();
                    }
                }

                @Override
                public void onCancelled(@NonNull DatabaseError error) {
                    Toast.makeText(MainActivity.this, R.string.unknownErrorMessage, Toast.LENGTH_LONG).show();
                }
            });
        } else {
            startActivity(new Intent(getApplicationContext(), SignInActivity.class));
        }

        databaseReference.child("users").addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                for (DataSnapshot dataSnapshot : snapshot.getChildren()) {
                    User user = dataSnapshot.getValue(User.class);
                    list.add(user);
                }
                myAdapter.notifyDataSetChanged();
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });

        locationTitle.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                myLat = Double.valueOf(latitude);
                myLng = Double.valueOf(longitude);
                startActivity(new Intent(MainActivity.this, MyLocationActivity.class));
            }
        });

//        membersListView.setOnContextClickListener(new View.OnContextClickListener() {
//            @Override
//            public boolean onContextClick(View view) {
//
//                return false;
//            }
//        });
    }

    public void loadFragment(Fragment fragment) {
        FragmentManager supportFragmentManager = getSupportFragmentManager();
        FragmentTransaction fragmentTransaction = supportFragmentManager.beginTransaction();
        fragmentTransaction.commit();
    }

    @SuppressLint("NonConstantResourceId")
    @Override
    public boolean onNavigationItemSelected(@NonNull MenuItem item) {

        switch (item.getItemId()) {
            case R.id.side_nav_home:
//                loadFragment(new HomeFragment());
                drawerLayout.closeDrawer(GravityCompat.START);
                break;
            case R.id.side_nav_profile:
                startActivity(new Intent(getApplicationContext(), ProfileActivity.class));
//                startActivity(new Intent(getApplicationContext(), ViewLocationActivity.class));
                drawerLayout.closeDrawer(GravityCompat.START);
                break;
            case R.id.side_nav_search:
                startActivity(new Intent(getApplicationContext(), SearchPeopleActivity.class));
                drawerLayout.closeDrawer(GravityCompat.START);
                break;
            case R.id.side_nav_settings:
                startActivity(new Intent(getApplicationContext(), ViewLocationActivity.class));
//                loadFragment(new SettingsFragment());
                drawerLayout.closeDrawer(GravityCompat.START);
                break;
            case R.id.side_nav_reset_password:
                startActivity(new Intent(getApplicationContext(), ForgotPasswordActivity.class));
                drawerLayout.closeDrawer(GravityCompat.START);
                break;
            case R.id.side_nav_logout:
                firebaseAuth.signOut();
                startActivity(new Intent(getApplicationContext(), SignInActivity.class));
                finish();
                break;
        }
        return true;
    }

    private void getLastLocation() {

        if (ContextCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED) {
            fusedLocationProviderClient.getLastLocation()
                    .addOnSuccessListener(new OnSuccessListener<android.location.Location>() {
                        @Override
                        public void onSuccess(Location location) {
                            if (location != null) {
                                Geocoder geocoder = new Geocoder(MainActivity.this, Locale.getDefault());
                                List<Address> address = null;
                                try {
                                    address = geocoder.getFromLocation(location.getLatitude(), location.getLongitude(), 1);

                                    myLat = address.get(0).getLatitude();
                                    MyLocation.myLng = address.get(0).getLongitude();
                                    latitude = String.valueOf(myLat);
                                    longitude = String.valueOf(myLng);
                                    latitudeTxt.setText("Latitude :" + latitude);
                                    longitudeTxt.setText("Longitude :" + longitude);
                                    areaTxt.setText("Province :" + address.get(0).getAdminArea());
                                    cityTxt.setText("City :" + address.get(0).getAddressLine(0));
                                    countryTxt.setText("Country :" + address.get(0).getCountryName());
                                uploadLocation();
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

    private void uploadLocation() {

        Map<String,String> location = new HashMap<>();
        location.put(latitude,longitude);
        databaseReference.child("users").child(firebaseAuth.getUid()).child("location").setValue(new LocClass( latitude , longitude )).addOnSuccessListener(new OnSuccessListener<Void>() {
            @Override
            public void onSuccess(Void unused) {
                Toast.makeText(MainActivity.this, "Location updated", Toast.LENGTH_LONG).show();
            }
        });
        databaseReference.child("publicLocation").child(firebaseAuth.getUid()).setValue(new LocClass( latitude , longitude )).addOnSuccessListener(new OnSuccessListener<Void>() {
            @Override
            public void onSuccess(Void unused) {
//                Toast.makeText(MainActivity.this, "Location updated", Toast.LENGTH_LONG).show();
            }
        });
    }

    private void updateLocation(){
        getLastLocation();
        uploadLocation();
    }

    private void askPermission() {
        ActivityCompat.requestPermissions(MainActivity.this, new String[]
                {Manifest.permission.ACCESS_FINE_LOCATION,Manifest.permission.INTERNET,Manifest.permission.READ_EXTERNAL_STORAGE}, REQUEST_CODE);
    }

    @Override
    public void onBackPressed() {
        if (drawerLayout.isDrawerOpen(GravityCompat.START)) {
            drawerLayout.closeDrawer(GravityCompat.START);
        } else {
            super.onBackPressed();
        }
    }
}