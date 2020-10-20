package com.pi.ising;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;

import com.google.firebase.auth.FirebaseAuth;

public class AccountActivity extends AppCompatActivity {
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_account);
        Log.d("info :","here 1");
        Button btn=findViewById(R.id.logout);


    }
    public void signout(View view){
        //Log.d("info :","here 2");
        //startActivity(new Intent(AccountActivity.this,LoginActivity.class));
        FirebaseAuth.getInstance().signOut();
        startActivity(new Intent(AccountActivity.this,LoginActivity.class));
    }
    public void goToProfile(View view){
        startActivity(new Intent(AccountActivity.this,ProfileActivity.class));
    }

}