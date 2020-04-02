package com.example.project;


import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.ImageButton;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;

public class About extends AppCompatActivity {
    ImageButton b;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.about);
        TextView t = findViewById(R.id.textView7);
        b = findViewById(R.id.imageButton);
        t.setText("This program uses the following:" +
                "\n1.Java" +
                "\n2.Android Studio" +
                "\n3.Xml" +
                "\n4.Dialog flow" +
                "\n5.Common sense" +
                "\n\n\nCreators:Sai Kowshik B6004" +
                "\n             Dr.Lakshmeeshwari garu " +
                "\n\n\n\nReferences:" +
                "\n                 Youtube");
        b.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(About.this, Home.class));
                finish();
                overridePendingTransition(R.anim.push_left_in, R.anim.push_left_out);

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