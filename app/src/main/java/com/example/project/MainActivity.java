package com.example.project;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.cardview.widget.CardView;
import androidx.core.app.ActivityCompat;
import androidx.core.view.MenuItemCompat;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import android.Manifest;
import android.annotation.SuppressLint;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.location.Address;
import android.location.Geocoder;
import android.location.Location;
import android.location.LocationManager;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.provider.Settings;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.ProgressBar;
import android.widget.SearchView;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationCallback;
import com.google.android.gms.location.LocationRequest;
import com.google.android.gms.location.LocationResult;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.DocumentChange;
import com.google.firebase.firestore.EventListener;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.FirebaseFirestoreException;
import com.google.firebase.firestore.QuerySnapshot;

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Locale;
import java.util.Set;
import java.util.Timer;

public class MainActivity extends AppCompatActivity {
    Set<User> s = new HashSet<>();
    private List<User> list = new ArrayList<User>() {
    };
    private RecyclerView userRecycler;
    private ListAdapter listAdapter;
    FirebaseFirestore fs;
    int PERMISSION_ID = 44;
    double lat, lon;
    FusedLocationProviderClient mFusedLocationClient;
    String finalcity;
    static MainActivity INSTANCE;
    ProgressBar pb;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        INSTANCE = this;
        fs = FirebaseFirestore.getInstance();
        userRecycler = findViewById(R.id.user_recycler);
        userRecycler.setLayoutManager(new LinearLayoutManager(this));
        pb = findViewById(R.id.progressBar3);
        mFusedLocationClient = LocationServices.getFusedLocationProviderClient(this);
        pb.setVisibility(View.VISIBLE);
        pb.setVisibility(View.INVISIBLE);
        listAdapter = new ListAdapter(this, list);
        userRecycler.setAdapter(listAdapter);
        getSupportActionBar().setBackgroundDrawable(new ColorDrawable(Color.parseColor("#E43F3F")));
        ((LinearLayoutManager) userRecycler.getLayoutManager()).setStackFromEnd(true);
        getLastLocation();
        if (FirebaseAuth.getInstance().getCurrentUser() == null) {
            Intent i = new Intent(MainActivity.this, Login.class);
            startActivity(i);
            finish();
        }
    }

    @Override
    public void onStart()
    { super.onStart();
            listAdapter.clear();
            getdbData();}





    public void getdbData() {
        if (!checkPermissions()) {
            requestPermissions();
        }
        fs.collection("Users").whereEqualTo("location", finalcity).whereEqualTo("group", "O+").addSnapshotListener(new EventListener<QuerySnapshot>() {
            @Override
            public void onEvent(@Nullable QuerySnapshot queryDocumentSnapshots, @Nullable FirebaseFirestoreException e) {
                for (DocumentChange doc : queryDocumentSnapshots.getDocumentChanges()) {
                    if (doc.getType() == DocumentChange.Type.ADDED) {
                        String name = doc.getDocument().getString("name");
                        String phone = doc.getDocument().getString("phone");
                        String email = doc.getDocument().getString("email");
                        String group = doc.getDocument().getString("group");
                        String age = doc.getDocument().getString("age");
                        String userId = doc.getDocument().getString("userId");
                        String location = doc.getDocument().getString("location");
                        list.add(new User(name, phone, email, group, age, userId, location));
                        listAdapter.notifyDataSetChanged();
                        userRecycler.scrollToPosition(list.size() - 1);
                        getSupportActionBar().setTitle(getData());
                    }
                }

            }
        });
    }

    public void logout() {
        FirebaseAuth.getInstance().signOut();
        finish();
        Intent i = new Intent(MainActivity.this, Login.class);
        i.putExtra("Location", finalcity);
        startActivity(i);
        overridePendingTransition(R.anim.push_left_in, R.anim.push_left_out);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.menu, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        switch (item.getItemId()) {
            case R.id.about_app:
                startActivity(new Intent(getApplicationContext(), About.class));
                overridePendingTransition(R.anim.push_left_in, R.anim.push_left_out);
                return true;

            case R.id.logout_user:
                logout();
                return true;
            case R.id.app_bar_search:
                return true;
            case R.id.refresh:
                if(finalcity==null) {
                    pb.setVisibility(View.VISIBLE);
                    getLastLocation();
                }
                else {
                    Toast.makeText(MainActivity.this, "No refresh required", Toast.LENGTH_LONG).show();
                    finish();
                    overridePendingTransition(0, 0);
                    startActivity(getIntent());
                    overridePendingTransition(0, 0);
                }return true;

            default:
                return super.onOptionsItemSelected(item);
        }

    }

    @SuppressLint("MissingPermission")
    private void getLastLocation() {
        if (checkPermissions()) {
            if (isLocationEnabled()) {
                mFusedLocationClient.getLastLocation().addOnCompleteListener(
                        new OnCompleteListener<Location>() {
                            @Override
                            public void onComplete(@NonNull Task<Location> task) {
                                Location location = task.getResult();
                                if (location == null) {
                                    requestNewLocationData();
                                } else {
                                    lat = location.getLatitude();
                                    lon = location.getLongitude();
                                    finalcity = hereLocation(lat, lon);
                                    getdbData();
                                }
                            }
                        }
                );

            } else {
                Toast.makeText(this, "Turn on location", Toast.LENGTH_LONG).show();
                Intent intent = new Intent(Settings.ACTION_LOCATION_SOURCE_SETTINGS);
                startActivity(intent);
                finish();
            }
        } else {
            requestPermissions();
        }
    }


    @SuppressLint("MissingPermission")
    private void requestNewLocationData() {

        LocationRequest mLocationRequest = new LocationRequest();
        mLocationRequest.setPriority(LocationRequest.PRIORITY_HIGH_ACCURACY);
        mLocationRequest.setInterval(0);
        mLocationRequest.setFastestInterval(0);
        mLocationRequest.setNumUpdates(1);

        mFusedLocationClient = LocationServices.getFusedLocationProviderClient(this);
        mFusedLocationClient.requestLocationUpdates(
                mLocationRequest, mLocationCallback,
                Looper.myLooper()
        );

    }

    private LocationCallback mLocationCallback = new LocationCallback() {
        @Override
        public void onLocationResult(LocationResult locationResult) {
            Location mLastLocation = locationResult.getLastLocation();
                lat = mLastLocation.getLatitude();
                lon = mLastLocation.getLongitude();
                finalcity = hereLocation(lat, lon);
        }


    };

    private boolean checkPermissions() {
        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION) == PackageManager.PERMISSION_GRANTED &&
                ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED) {
            return true;
        }
        return false;
    }

    protected void requestPermissions() {
        ActivityCompat.requestPermissions(
                this,
                new String[]{Manifest.permission.ACCESS_COARSE_LOCATION, Manifest.permission.ACCESS_FINE_LOCATION},
                PERMISSION_ID
        );
    }

    private boolean isLocationEnabled() {
        LocationManager locationManager = (LocationManager) getSystemService(Context.LOCATION_SERVICE);
        return locationManager.isProviderEnabled(LocationManager.GPS_PROVIDER) || locationManager.isProviderEnabled(
                LocationManager.NETWORK_PROVIDER
        );
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, String[] permissions, int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if (requestCode == PERMISSION_ID) {
            if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                getLastLocation();
            }
        }
    }
    private String hereLocation(double lat, double lon){
        String cityName="";
        Geocoder geocoder=new Geocoder(this, Locale.getDefault());
        List<Address> addresses;
        try{
            addresses=geocoder.getFromLocation(lat,lon,1);
            if(addresses.size()>0){
                for(Address adr:addresses){
                    if(adr.getLocality()!=null&&adr.getLocality().length()>0){
                        cityName=adr.getLocality();
                    }
                }
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
        return cityName;
    }
    public static MainActivity getActivityInstance()
    {
        return INSTANCE;
    }

    public String getData()
    {
        return this.finalcity;
    }
}