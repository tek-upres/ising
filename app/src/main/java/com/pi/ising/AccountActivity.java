package com.pi.ising;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentTransaction;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;

import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.firebase.auth.FirebaseAuth;
import com.pi.ising.fragment.ProfileFragment;
import com.pi.ising.fragment.homeFragment;
import com.pi.ising.fragment.notificationFragment;
import com.pi.ising.fragment.searchFragment;

public class AccountActivity extends AppCompatActivity {
   BottomNavigationView bottomNavigationView,bottomNavigation;
   Fragment selectedFragment =null;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_account);
          bottomNavigation=findViewById(R.id.button_bar);
        bottomNavigationView=findViewById(R.id.button_navigation);
        bottomNavigation.setOnNavigationItemSelectedListener(navigationItenSelectedLintener);
        bottomNavigationView.setOnNavigationItemSelectedListener(navigationItenSelectedLintener);
      Bundle intent=getIntent().getExtras();
      if(intent!=null){
          String publisher=intent.getString("publisherid");
          SharedPreferences.Editor editor=getSharedPreferences("PREFS",MODE_PRIVATE).edit();
      editor.putString("profileid",publisher);
      editor.apply();
          getSupportFragmentManager().beginTransaction().replace(R.id.fragment_container,new ProfileFragment()).commit();
      }else {


          getSupportFragmentManager().beginTransaction().replace(R.id.fragment_container, new homeFragment()).commit();
      }
    }
    private BottomNavigationView.OnNavigationItemSelectedListener navigationItenSelectedLintener=
            new BottomNavigationView.OnNavigationItemSelectedListener() {
                @Override
                public boolean onNavigationItemSelected(@NonNull MenuItem item) {
                    switch (item.getItemId()){
                        case R.id.nav_home:
                            selectedFragment=new homeFragment();
                        break;
                        case R.id.logout:
                            signout();
                            break;
                        case R.id.nav_search:

                            selectedFragment=new searchFragment();
                            break;
                        case R.id.nav_uplode:
                            selectedFragment=null;
                            startActivity(new Intent(AccountActivity.this, addVIdeoActivity.class));
                            break;
                        case R.id.nav_chat:
                            selectedFragment=new chatlistFragment();
                            break;
                        case R.id.nav_persone:
                            SharedPreferences.Editor editor=getSharedPreferences("PREFS",MODE_PRIVATE).edit();
                           editor.putString("profileid",FirebaseAuth.getInstance().getCurrentUser().getUid());
                           editor.apply();
                           selectedFragment=new ProfileFragment();
                            break;
                        case R.id.nav_heart:
                            selectedFragment=new notificationFragment();
                            break;
                    }
if (selectedFragment !=null){
    getSupportFragmentManager().beginTransaction().replace(R.id.fragment_container,selectedFragment).commit();

}

                    return true;
                }
            };
    public void signout(){
        //Log.d("info :","here 2");
        //startActivity(new Intent(AccountActivity.this,LoginActivity.class));
        FirebaseAuth.getInstance().signOut();
        startActivity(new Intent(AccountActivity.this,LoginActivity.class));
        finish();
    }

    /*public void goToProfile(View view){
        startActivity(new Intent(AccountActivity.this,ProfileActivity.class));
    }*/

}