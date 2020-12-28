package com.pi.ising;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.net.Uri;
import android.os.Build;
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
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.pi.ising.model.User;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.StreamSupport;

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
        Intent signInIntent = mGoogleSignInClient.getSignInIntent();
        startActivityForResult(signInIntent, RC_SIGN_IN);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        Log.d("LoginActivity", "begin of onActivityResult");
        if (requestCode == RC_SIGN_IN) {
            Task<GoogleSignInAccount> task = GoogleSignIn.getSignedInAccountFromIntent(data);
            handleSignInResult(task);
        }
    }

    private void handleSignInResult(Task<GoogleSignInAccount> task) {
        try {
            GoogleSignInAccount acc = task.getResult(ApiException.class);
            FirebaseGoogleAuth(acc);
        } catch (ApiException e) {
            Toast.makeText(StartActivity.this, "Sign In Failed", Toast.LENGTH_SHORT).show();
            FirebaseGoogleAuth(null);
        }
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
                } else {
                    Toast.makeText(StartActivity.this, "Failed", Toast.LENGTH_SHORT).show();
                    register(null,null);
                }
            }


        });
    }

    private void register(FirebaseUser user, final GoogleSignInAccount account) {

        if (account != null) {
            final String user_id = user.getUid();

            FirebaseDatabase.getInstance().getReference("Users").addValueEventListener(new ValueEventListener() {
                @Override
                public void onDataChange(@NonNull DataSnapshot snapshot) {
                    boolean isRegistered=false;
                    for (DataSnapshot ds:snapshot.getChildren()){
                        User user = ds.getValue(User.class);
                        if (user.getId().equals(user_id)){
                            isRegistered=true;

                            break;
                        }
                    }
                    if (!isRegistered) {
                        reference = FirebaseDatabase.getInstance().getReference("Users");

                        final HashMap<String, Object> hashMap = new HashMap<>();
                        hashMap.put("id", user_id);
                        hashMap.put("username", account.getDisplayName().toLowerCase());
                        hashMap.put("fullname", account.getGivenName() + " " + account.getFamilyName());
                        hashMap.put("bio", "");
                        hashMap.put("imageurl", account.getPhotoUrl());
                        hashMap.put("userRole", "user");
                        reference.setValue(hashMap).addOnCompleteListener(new OnCompleteListener<Void>() {
                            @Override
                            public void onComplete(@NonNull Task<Void> task) {
                                Toast.makeText(StartActivity.this, "Welcome " + hashMap.get("fullname").toString(), Toast.LENGTH_SHORT).show();
                                Intent intent = new Intent(StartActivity.this, AccountActivity.class);
                                intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK | Intent.FLAG_ACTIVITY_NEW_TASK);
                                startActivity(intent);
                                //finish();
                            }


                        });
                    }else{
                        Toast.makeText(StartActivity.this, "Welcome "+account.getDisplayName() , Toast.LENGTH_SHORT).show();
                        Intent intent = new Intent(StartActivity.this, AccountActivity.class);
                        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK | Intent.FLAG_ACTIVITY_NEW_TASK);
                        startActivity(intent);

                    }


                }

                @Override
                public void onCancelled(@NonNull DatabaseError error) {

                }
            });



        }
    }
}