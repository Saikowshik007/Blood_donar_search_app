package com.example.project;


import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.view.View;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;

public class About extends AppCompatActivity {
    ImageButton b;
    ImageView facebook,linkedin,github;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.about);
        TextView t = findViewById(R.id.textView7);
        b = findViewById(R.id.imageButton);
        facebook=findViewById(R.id.facebook);
        github=findViewById(R.id.github);
        linkedin=findViewById(R.id.linked);

        t.setText("Tech Enthusiast\n"+"Student of Gitam Universiy,Visakhapatnam\n"+"Always try to solve my problems using technology\n");
        b.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(About.this, Home.class));
                finish();
                overridePendingTransition(R.anim.push_left_in, R.anim.push_left_out);

            }
        });
        github.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String url = "https://github.com/Saikowshik007";
                Intent i = new Intent(Intent.ACTION_VIEW);
                i.setData(Uri.parse(url));
                startActivity(i);

            }
        });
        facebook.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String url = "https://www.facebook.com/profile.php?id=100011025258775";
                Intent i = new Intent(Intent.ACTION_VIEW);
                i.setData(Uri.parse(url));
                startActivity(i);
            }
        });
linkedin.setOnClickListener(new View.OnClickListener() {
    @Override
    public void onClick(View v) {
        String url = "https://www.linkedin.com/in/sai-kowshik-ananthula-9b1816177/";
        Intent i = new Intent(Intent.ACTION_VIEW);
        i.setData(Uri.parse(url));
        startActivity(i);
    }
});
    }
    @Override
    public void onBackPressed()
    {
        startActivity(new Intent(About.this, Home.class));
        finish();
        overridePendingTransition(R.anim.push_left_in, R.anim.push_left_out);
        super.onBackPressed();
    }
}