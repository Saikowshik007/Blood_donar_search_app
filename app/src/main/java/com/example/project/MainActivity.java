package com.example.project;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.content.Intent;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.DocumentChange;
import com.google.firebase.firestore.EventListener;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.FirebaseFirestoreException;
import com.google.firebase.firestore.QuerySnapshot;

import java.util.ArrayList;
import java.util.List;

public class MainActivity extends AppCompatActivity {
    private List<User> list = new ArrayList<>();
    private RecyclerView userRecycler;
    private ListAdapter listAdapter;
    Button logout;
    FirebaseFirestore fs;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        fs = FirebaseFirestore.getInstance();
        userRecycler = findViewById(R.id.user_recycler);
        userRecycler.setLayoutManager(new LinearLayoutManager(this));
        listAdapter = new ListAdapter(this, list);
        userRecycler.setAdapter(listAdapter);
        ((LinearLayoutManager) userRecycler.getLayoutManager()).setStackFromEnd(true);
        if (FirebaseAuth.getInstance().getCurrentUser() == null) {
            startActivity(new Intent(MainActivity.this, Login.class));
            finish();
        }
    }

    @Override
    protected void onStart() {
        super.onStart();
        fs.collection("Users").addSnapshotListener(new EventListener<QuerySnapshot>() {
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
                        list.add(new User(name, phone, email, group, age, userId));
                        listAdapter.notifyDataSetChanged();
                        userRecycler.scrollToPosition(list.size() - 1);

                    }
                }

            }
        });
    }

    public void logout() {
                FirebaseAuth.getInstance().signOut();
                finish();
                startActivity(new Intent(MainActivity.this, Login.class));
                overridePendingTransition(R.anim.push_left_in, R.anim.push_left_out); }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater=getMenuInflater();
                inflater.inflate(R.menu.menu,menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        switch(item.getItemId()){
            case R.id.about_app:
                startActivity(new Intent(getApplicationContext(),About.class));
                overridePendingTransition(R.anim.push_left_in, R.anim.push_left_out);return true;
            case R.id.logout_user:
                logout();return true;
            default:
                return super.onOptionsItemSelected(item);
        }

    }
}