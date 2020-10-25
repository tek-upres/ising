package com.pi.ising;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import com.facebook.FacebookSdk;
import com.facebook.appevents.AppEventsLogger;

import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Patterns;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.facebook.CallbackManager;
import com.facebook.FacebookCallback;
import com.facebook.FacebookException;
import com.facebook.login.LoginResult;
import com.google.android.gms.auth.api.signin.GoogleSignInClient;
import com.google.android.gms.auth.api.signin.GoogleSignInOptions;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.facebook.login.widget.LoginButton;
import com.squareup.picasso.Picasso;

import java.util.Arrays;

public class LoginActivity extends AppCompatActivity {
    private static final String EMAIL = "email";

    //private ImageView profile;
    //private TextView info;
    private EditText mEmailfield;
    private EditText mPasswordfield;
    private Button mloginbtn;
    private LoginButton login;
    CallbackManager callbackManager = CallbackManager.Factory.create();
    private FirebaseAuth mAuth;
    private FirebaseAuth.AuthStateListener mAuthListener;
    private GoogleSignInOptions gso ;
    private GoogleSignInClient mGoogleSignInClient;
    //private ActivityGoogleBinding mBinding;
    private static final int RC_SIGN_IN = 9001;

    private void signIn() {
        Intent signInIntent = mGoogleSignInClient.getSignInIntent();
        startActivityForResult(signInIntent, RC_SIGN_IN);
    }


    @Override
    protected void onCreate(Bundle savedInstanceState) {

        getSupportActionBar().hide();
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);
       // info= findViewById(R.id.info);
      //  profile=findViewById(R.id.imageprofile);
        //login = findViewById(R.id.hj);

//    login.registerCallback(callbackManager, new FacebookCallback<LoginResult>() {
//             @Override
//             public void onSuccess(LoginResult loginResult) {
//                 info.setText("user id"+loginResult.getAccessToken().getUserId());
//             String umageurl="https://graph.facebook.com/"+loginResult.getAccessToken().getUserId()+"/picture?return_ssl_resources=1";
//                 Picasso.get().load(umageurl).into(profile);
//             }
//
//             @Override
//             public void onCancel() {
//
//             }
//
//             @Override
//             public void onError(FacebookException error) {
//
//             }
//         });
        gso=new GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
                .requestIdToken(getString(R.string.default_web_client_id))
                .requestEmail()
                .build();
        mAuth = FirebaseAuth.getInstance();
        mEmailfield = (EditText) findViewById(R.id.Emailfiled);
        mPasswordfield = (EditText) findViewById(R.id.Passwordfiled);
        mloginbtn = (Button) findViewById(R.id.loginbnt);

        mAuthListener = new FirebaseAuth.AuthStateListener() {
            @Override
            public void onAuthStateChanged(@NonNull FirebaseAuth firebaseAuth) {
                if (firebaseAuth.getCurrentUser() != null) {
                    startActivity(new Intent(LoginActivity.this, AccountActivity.class));
                    finish();
                }
            }
        };
        mloginbtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startSingIn();
            }
        });

    }

//    @Override
//    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
//        super.onActivityResult(requestCode, resultCode, data);
//        callbackManager.onActivityResult(requestCode,resultCode,data);
//    }

    @Override
    protected void onStart() {
        super.onStart();
        mAuth.addAuthStateListener(mAuthListener);
    }

    private void startSingIn() {
        String email = mEmailfield.getText().toString();
        String password = mPasswordfield.getText().toString();

        if (TextUtils.isEmpty(email) || TextUtils.isEmpty(password)) {
            Toast.makeText(LoginActivity.this, "Field is empty", Toast.LENGTH_LONG).show();
        }else if (!Patterns.EMAIL_ADDRESS.matcher(email).matches()){
            mEmailfield.setError("Invalid mail");
            mEmailfield.setFocusable(true);

        }
        else {
            mAuth.signInWithEmailAndPassword(email, password).addOnCompleteListener(new OnCompleteListener<AuthResult>() {
                @Override
                public void onComplete(@NonNull Task<AuthResult> task) {
                    if (!task.isSuccessful()) {
                        Toast.makeText(LoginActivity.this, "Sign in Problem", Toast.LENGTH_LONG).show();
                    }
                }
            });
        }

    }

}



