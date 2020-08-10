package com.example.project;

import android.content.ActivityNotFoundException;
import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.net.Uri;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.view.animation.LayoutAnimationController;
import android.view.inputmethod.EditorInfo;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.SearchView;
import android.widget.Switch;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.ActionBarDrawerToggle;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.core.view.GravityCompat;
import androidx.core.view.MenuItemCompat;
import androidx.drawerlayout.widget.DrawerLayout;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.android.material.navigation.NavigationView;
import com.google.firebase.auth.FirebaseAuth;

import com.google.firebase.firestore.DocumentChange;

import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.EventListener;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.FirebaseFirestoreException;

import com.google.firebase.firestore.QuerySnapshot;

import java.util.ArrayList;
import java.util.List;

public class Home extends AppCompatActivity implements NavigationView.OnNavigationItemSelectedListener {
    private List<User> list = new ArrayList<User>() {
    };
   public static List<User> currentuser=new ArrayList<>();
    private RecyclerView userRecycler;
    public static ListAdapter listAdapter;
    private NavigationView navigationView;
    Toolbar toolbar;
    DrawerLayout drawerLayout;
    FirebaseFirestore fs;
    ProgressBar pb;
    MainActivity ma;
    String finalcity,userid,emailf;
    static String location,lat,lon;
    Boolean stop = false;
    User current,data;
    ImageView image;
    FirebaseAuth fa;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.home);
        toolbar = findViewById(R.id.toolbar2);
        setSupportActionBar(toolbar);
        drawerLayout = findViewById(R.id.drawer_layout);
        ActionBarDrawerToggle actionBarDrawerToggle = new ActionBarDrawerToggle(Home.this, drawerLayout, toolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close);
        actionBarDrawerToggle.syncState();
        image=findViewById(R.id.imageView9);
        drawerLayout.addDrawerListener(actionBarDrawerToggle);
        navigationView = findViewById(R.id.nav_view);
        navigationView.setNavigationItemSelectedListener(this);
        userRecycler = findViewById(R.id.user_recycler);
        userRecycler.setLayoutManager(new LinearLayoutManager(this));
        pb = findViewById(R.id.progressBar3);
        listAdapter = new ListAdapter(Home.this, list,currentuser);
        userRecycler.setAdapter(listAdapter);
        ((LinearLayoutManager) userRecycler.getLayoutManager()).setStackFromEnd(true);
    }

    @Override
    public void onStart() {
        super.onStart();
        ma = MainActivity.getActivityInstance();
        finalcity = ma.getData();
        fa=FirebaseAuth.getInstance();
        fs = FirebaseFirestore.getInstance();
        if (fa.getCurrentUser() == null) {
            Intent i = new Intent(Home.this, Login.class);
            startActivity(i);
            finish();
        } else if (ma.Lflag) {
            userid= fa.getCurrentUser().getUid().trim();
            emailf=fa.getCurrentUser().getEmail();

            getdbData();


        }
    }

    public void getdbData(){
        list.clear();
        currentuser.add(0,new User("No Data","No Data","No Data","No Data","No Data","No Data","No Data","No Data","No Data","0","0"));
            fs.collection("Users").whereEqualTo("location", finalcity).addSnapshotListener(new EventListener<QuerySnapshot>() {
                @Override
                public void onEvent(@Nullable QuerySnapshot queryDocumentSnapshots, @Nullable FirebaseFirestoreException e) {
                    if(e!=null){}
                    else {
                        for (DocumentChange doc : queryDocumentSnapshots.getDocumentChanges()) {
                            if (doc.getType() == DocumentChange.Type.ADDED) {
                                String name = doc.getDocument().getString("name");
                                String phone = doc.getDocument().getString("phone");
                                String email = doc.getDocument().getString("email");
                                String group = doc.getDocument().getString("group");
                                String age = doc.getDocument().getString("age");
                                String userId = doc.getDocument().getString("userId").trim();
                                location = doc.getDocument().getString("location");
                                String date = doc.getDocument().getString("date");
                                String available = doc.getDocument().getString("availability");
                                lat = doc.getDocument().getString("lat");
                                lon = doc.getDocument().getString("lon");
                                if (doc.getDocument().getId().contains(fa.getCurrentUser().getUid())) {
                                    if (date == "" || date == null) {
                                        date = "Not yet Donated";
                                    }
                                    currentuser.clear();
                                    current = new User(name, phone, email, group, age, userId, location, date, available, lat, lon);
                                    currentuser.add(0, current);
                                    image.setImageResource(currentuser.get(0).getAvailability().contains("0") ? R.drawable.rounded : R.drawable.rounded_green);
                                    updatedb();
                                } else {
                                    if (date == "" || date == null) {
                                        date = "Not yet Donated";
                                    }
                                    list.add(new User(name, phone, email, group, age, userId, location, date, available, lat, lon));
                                    userRecycler.scrollToPosition(0);
                                }


                            }
                        }
                    }
                    toolbar.setTitle(finalcity);
                    listAdapter.msort();
                    listAdapter.dup(list);
                }
            });
            listAdapter.notifyDataSetChanged();

    }

    public void logout() {
        fa.signOut();
        Intent i = new Intent(Home.this, Login.class);
        i.putExtra("Location", finalcity);
        startActivity(i);
        overridePendingTransition(R.anim.push_left_in, R.anim.push_left_out);
        finish();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.menu, menu);
        MenuItem searchViewItem = menu.findItem(R.id.app_bar_search);
        SearchView searchView = (SearchView) MenuItemCompat.getActionView(searchViewItem);
        searchView.onActionViewExpanded();
        searchView.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
            @Override
            public boolean onQueryTextSubmit(String query) {
                return false;
            }

            @Override
            public boolean onQueryTextChange(String newText) {
                listAdapter.getFilter().filter(newText);
                return false;
            }
        });
        return true;
    }

    @Override
    public boolean onNavigationItemSelected(@NonNull MenuItem menuItem) {
        switch (menuItem.getItemId()) {
            case R.id.nav_profile:
                startActivity(new Intent(Home.this,Profile.class));
                overridePendingTransition(R.anim.push_left_in, R.anim.push_left_out);
                finish();
                break;
            case R.id.about_app:
                startActivity(new Intent(Home.this, About.class));
                overridePendingTransition(R.anim.push_left_in, R.anim.push_left_out);
                finish();
                break;

            case R.id.logout_user:
                logout();
                break;

            case R.id.refresh:
                if (true) {
                    pb.setVisibility(View.VISIBLE);
                    finish();
                    overridePendingTransition(0, 0);
                    startActivity(getIntent());
                    overridePendingTransition(0, 0);
                    getSupportActionBar().setTitle(finalcity);
                    stop = true;
                    pb.setVisibility(View.INVISIBLE);
                } else {
                    Toast.makeText(Home.this, "No refresh required", Toast.LENGTH_LONG).show();

                }
                break;
            case R.id.nav_share:
                Intent shareIntent =   new Intent(android.content.Intent.ACTION_SEND);
                shareIntent.setType("text/plain");
                shareIntent.putExtra(Intent.EXTRA_SUBJECT,"Now get blood donations with ease at your location");
                String app_url = "https://drive.google.com/drive/folders/1FlfmK-cDQMoGbUzicx4BtYpwHR6fanr1?usp=sharing";
                shareIntent.putExtra(android.content.Intent.EXTRA_TEXT,app_url);
                startActivity(Intent.createChooser(shareIntent, "Share via"));
                break;
            case R.id.rate:
                Uri uri = Uri.parse("market://details?id=" + this.getPackageName());
                Intent goToMarket = new Intent(Intent.ACTION_VIEW, uri);
                goToMarket.addFlags(Intent.FLAG_ACTIVITY_NO_HISTORY |
                        Intent.FLAG_ACTIVITY_NEW_DOCUMENT |
                        Intent.FLAG_ACTIVITY_MULTIPLE_TASK);
                try {
                    startActivity(goToMarket);
                } catch (ActivityNotFoundException e) {
                    startActivity(new Intent(Intent.ACTION_VIEW,
                            Uri.parse("http://play.google.com/store/apps/details?id=" + this.getPackageName())));
                }
                break;
        }

       // menuItem.setChecked(true);
        drawerLayout.closeDrawer(GravityCompat.START);
        return true;
    }
    public void updatedb(){
        DocumentReference ref=fs.collection("Users").document(fa.getCurrentUser().getUid());

        if(!location.contains(finalcity)||!lat.contains(Double.toString(ma.lat))||!lon.contains(Double.toString(ma.lon))) {
            ref.update("location", finalcity,"lat",Double.toString(ma.lat),"lon",Double.toString(ma.lon)).addOnCompleteListener(new OnCompleteListener<Void>() {
                @Override
                public void onComplete(@NonNull Task<Void> task) {
                    if (task.isSuccessful()) {
                        Toast.makeText(Home.this, " Location updated", Toast.LENGTH_SHORT).show();

                    } else {
                        Toast.makeText(Home.this, " cannot update location" + task.getException(), Toast.LENGTH_SHORT).show();
                    }
                }
            });
        }
    }

}