package com.pi.ising;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;

import com.google.android.material.floatingactionbutton.FloatingActionButton;

public class videoActivity extends AppCompatActivity {
 FloatingActionButton addVideoesBtn;

    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_video);
        //change title
        setTitle("videos");
        //views
        addVideoesBtn=findViewById(R.id.addVideos);
        //handle clicks
        addVideoesBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
startActivity(new Intent(videoActivity.this,addVIdeoActivity.class));
            }
        });

    }
}