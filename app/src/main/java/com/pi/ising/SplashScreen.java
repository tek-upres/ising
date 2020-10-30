package com.pi.ising;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.util.Log;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

import static java.lang.Thread.sleep;

public class SplashScreen extends AppCompatActivity {
    private FirebaseAuth.AuthStateListener mAuthListener;
    private FirebaseUser user;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        user=FirebaseAuth.getInstance().getCurrentUser();
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_splash_screen);
        //getSupportActionBar().hide();

        new Handler().postDelayed(new Runnable() {
            @Override
            public void run() {
                if(user != null) {
                    startActivity(new Intent(SplashScreen.this, AccountActivity.class));
                    finish();
                }else {
                    startActivity(new Intent(SplashScreen.this,LoginActivity.class));
                    finish();
                }

                }


        }, 3000);

        /*Thread thread=new Thread(new Runnable() {
            @Override
            public void run() {
                Log.d("info :","it's here2");

                try {
                    sleep(6000);
                    mAuthListener = new FirebaseAuth.AuthStateListener() {
                        @Override
                        public void onAuthStateChanged(@NonNull FirebaseAuth firebaseAuth) {
                            Log.d("info :","it's here3");
                            if (firebaseAuth.getCurrentUser() != null) {
                                startActivity(new Intent(SplashScreen.this, AccountActivity.class));
                                Log.d("info :","it's here4");

                                finish();
                            }else {
                                startActivity(new Intent(SplashScreen.this,LoginActivity.class));
                            }
                        }
                    };



                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
            }
        });*/
        //finish();


    }

}