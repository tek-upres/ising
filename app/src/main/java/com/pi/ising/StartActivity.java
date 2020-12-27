package com.pi.ising;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.util.Patterns;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.google.android.gms.auth.api.signin.GoogleSignIn;
import com.google.android.gms.auth.api.signin.GoogleSignInAccount;
import com.google.android.gms.auth.api.signin.GoogleSignInClient;
import com.google.android.gms.auth.api.signin.GoogleSignInOptions;
import com.google.android.gms.common.api.ApiException;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthCredential;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.auth.GoogleAuthProvider;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.pi.ising.model.User;

import java.util.HashMap;

public class StartActivity extends AppCompatActivity {
    Button login, register;
    FirebaseUser firebaseUser;
    FirebaseAuth mAuth;
    DatabaseReference reference;
    private GoogleSignInClient mGoogleSignInClient;
    private static final int RC_SIGN_IN = 9001;


    protected void onStart() {
        super.onStart();
        firebaseUser = FirebaseAuth.getInstance().getCurrentUser();
        if (firebaseUser != null) {
            startActivity(new Intent(StartActivity.this, AccountActivity.class));
            finish();
        }
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.start_activity);

        GoogleSignInOptions gso = new GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
                .requestIdToken(getString(R.string.default_web_client_id))
                .requestEmail()
                .build();
        mGoogleSignInClient = GoogleSignIn.getClient(this, gso);
        mAuth = FirebaseAuth.getInstance();
        login = findViewById(R.id.login);
        register = findViewById(R.id.register);
        login.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(StartActivity.this, LoginActivity.class));
            }
        });
        register.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(StartActivity.this, RegisterActivity.class));
            }
        });
    }

    public void signingoogle(View view) {
        Log.d("LoginActivity", "begin of signingoogle");
        Intent signInIntent = mGoogleSignInClient.getSignInIntent();
        startActivityForResult(signInIntent, RC_SIGN_IN);
        //startActivity(new Intent(LoginActivity.this,AccountActivity.class));
        Log.d("LoginActivity", "end of signingoogle");
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        Log.d("LoginActivity", "begin of onActivityResult");
        if (requestCode == RC_SIGN_IN) {
            Task<GoogleSignInAccount> task = GoogleSignIn.getSignedInAccountFromIntent(data);
            handleSignInResult(task);
        }
        Log.d("LoginActivity", "end of onActivityResult");
    }

    private void handleSignInResult(Task<GoogleSignInAccount> task) {
        Log.d("LoginActivity", "begin of handleSignInResult");
        try {
            GoogleSignInAccount acc = task.getResult(ApiException.class);
            FirebaseGoogleAuth(acc);
        } catch (ApiException e) {
            Toast.makeText(StartActivity.this, "Sign In Failed", Toast.LENGTH_SHORT).show();
            FirebaseGoogleAuth(null);
        }
        Log.d("LoginActivity", "end of handleSignInResult");
    }

    private void FirebaseGoogleAuth(GoogleSignInAccount acct) {
        AuthCredential authCredential = GoogleAuthProvider.getCredential(acct.getIdToken(),null);


        mAuth.signInWithCredential(authCredential).addOnCompleteListener(this, new OnCompleteListener<AuthResult>() {
            @Override
            public void onComplete(@NonNull Task<AuthResult> task) {
                Log.d("LoginActivity", "begin of onComplete");
                if (task.isSuccessful()) {
                    Toast.makeText(StartActivity.this, "Successful", Toast.LENGTH_SHORT).show();
                    FirebaseUser user = mAuth.getCurrentUser();
                    GoogleSignInAccount account = GoogleSignIn.getLastSignedInAccount(getApplicationContext());
                    register(user,account);
                    //updateUI(user);
                } else {
                    Toast.makeText(StartActivity.this, "Failed", Toast.LENGTH_SHORT).show();
                    //updateUI(null);
                    register(null,null);
                }
                Log.d("LoginActivity", "end of onComplete");
            }


        });
        Log.d("LoginActivity", "end of FirebaseGoogleAuth");
    }

    private void updateUI(FirebaseUser user) {
        Log.d("LoginActivity", "begin of updateUI");
        Toast.makeText(StartActivity.this, "UpdateUI", Toast.LENGTH_SHORT).show();

        //GoogleSignInAccount account = GoogleSignIn.getLastSignedInAccount(getApplicationContext());
        //register(user, account);
            /*reference.setValue(hashMap).addOnCompleteListener(new OnCompleteListener<Void>() {
                @Override
                public void onComplete(@NonNull Task<Void> task) {
                    if (task.isSuccessful()) {
                        //pd.dismiss();
                        Toast.makeText(StartActivity.this,"user created",Toast.LENGTH_SHORT).show();


                    }else{
                        Toast.makeText(StartActivity.this,"Fail to create user",Toast.LENGTH_SHORT).show();

                    }*/
            /*String personName = account.getDisplayName();
            String personGivenName = account.getGivenName();
            String personFamilyName = account.getFamilyName();
            String personEmail = account.getEmail();
            String personId = account.getId();
            Uri personPhoto= account.getPhotoUrl();
            //startActivity(new Intent(LoginActivity.this,AccountActivity.class));*/
        //finish();

        //});
    }

    private void register(FirebaseUser user, GoogleSignInAccount account) {

        if (account != null) {
            Toast.makeText(StartActivity.this, "register user ", Toast.LENGTH_SHORT).show();

            String user_id = user.getUid();
            reference = FirebaseDatabase.getInstance().getReference().child("Users").child(user_id);
            //reference=FirebaseDatabase.getInstance().getReference("/Users").child(user_id);
            final HashMap<String, Object> hashMap = new HashMap<>();
            hashMap.put("id", user_id);
            hashMap.put("onlineStatus", "");
            hashMap.put("typingTo", "");
            hashMap.put("username", account.getDisplayName().toLowerCase());
            hashMap.put("fullname", account.getGivenName() + " " + account.getFamilyName());
            hashMap.put("bio", "");
            hashMap.put("imageurl", account.getPhotoUrl());
            hashMap.put("userRole", "user");
            Toast.makeText(StartActivity.this, "end of register", Toast.LENGTH_SHORT).show();
            //reference.setValue(hashMap);
            reference.setValue(new User(hashMap.get("id").toString(),hashMap.get("username").toString(),hashMap.get("fullname").toString(),
                    hashMap.get("imageurl").toString(),
                    hashMap.get("bio").toString(),hashMap.get("typingTo").toString(),hashMap.get("onlineStatus").toString(),hashMap.get("userRole").toString())).addOnCompleteListener(new OnCompleteListener<Void>() {
                @Override
                public void onComplete(@NonNull Task<Void> task) {
                    Toast.makeText(StartActivity.this, "Welcome "+hashMap.get("fullname").toString() , Toast.LENGTH_SHORT).show();
                    Intent intent = new Intent(StartActivity.this, AccountActivity.class);
                    intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK | Intent.FLAG_ACTIVITY_NEW_TASK);
                    startActivity(intent);
                }
            });

        }
    }
}