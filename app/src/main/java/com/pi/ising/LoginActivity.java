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

import com.firebase.client.Firebase;
import com.google.android.gms.auth.api.signin.GoogleSignIn;
import com.google.android.gms.auth.api.signin.GoogleSignInAccount;
import com.google.android.gms.auth.api.signin.GoogleSignInClient;
import com.google.android.gms.auth.api.signin.GoogleSignInOptions;
import com.google.android.gms.common.api.ApiException;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthCredential;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.auth.GoogleAuthProvider;

public class LoginActivity extends AppCompatActivity {
    private EditText mEmailfield;
    private EditText mPasswordfield;
    private Button mloginbtn;
    private FirebaseAuth mAuth;
    private FirebaseAuth.AuthStateListener mAuthListener;
    //////////

    private GoogleSignInClient mGoogleSignInClient;
    //private ActivityGoogleBinding mBinding;
    private static final int RC_SIGN_IN = 9001;




    @Override
    protected void onCreate(Bundle savedInstanceState) {

        getSupportActionBar().hide();
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);
        ///////////////////////////////////////

        GoogleSignInOptions gso=new GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
                .requestIdToken(getString(R.string.default_web_client_id))
                .requestEmail()
                .build();
        mGoogleSignInClient= GoogleSignIn.getClient(this,gso);
        ///////////////////////////////////////
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
        /*mloginbtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startSingIn();
            }
        });*/

    }

    @Override
    protected void onStart() {
        super.onStart();
        mAuth.addAuthStateListener(mAuthListener);
    }
    ///////////////////Googleauth/////////////////
    public void signingoogle(View view){
        Log.d("LoginActivity","begin of signingoogle");
        Intent signInIntent = mGoogleSignInClient.getSignInIntent();
        startActivityForResult(signInIntent,RC_SIGN_IN);
        //startActivity(new Intent(LoginActivity.this,AccountActivity.class));
        Log.d("LoginActivity","end of signingoogle");
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        Log.d("LoginActivity","begin of onActivityResult");
        if (requestCode==RC_SIGN_IN){
          Task<GoogleSignInAccount> task = GoogleSignIn.getSignedInAccountFromIntent(data);
          handleSignInResult(task);
        }
        Log.d("LoginActivity","end of onActivityResult");
    }

    private void handleSignInResult(Task<GoogleSignInAccount> task) {
        Log.d("LoginActivity","begin of handleSignInResult");
        try {
            GoogleSignInAccount acc = task.getResult(ApiException.class);
            FirebaseGoogleAuth(acc);
        } catch (ApiException e) {
            Toast.makeText(LoginActivity.this, "Sign In Failed", Toast.LENGTH_SHORT).show();
            FirebaseGoogleAuth(null);
        }
        Log.d("LoginActivity","end of handleSignInResult");
    }

    private void FirebaseGoogleAuth(GoogleSignInAccount acct) {
        Log.d("LoginActivity","begin of FirebaseGoogleAuth");
        AuthCredential authCredential= GoogleAuthProvider.getCredential(acct.getIdToken(),null);
        mAuth.signInWithCredential(authCredential).addOnCompleteListener(this, new OnCompleteListener<AuthResult>() {
            @Override
            public void onComplete(@NonNull Task<AuthResult> task) {
                Log.d("LoginActivity","begin of onComplete");
                if (task.isSuccessful()){
                    Toast.makeText(LoginActivity.this,"Successful",Toast.LENGTH_SHORT).show();
                    FirebaseUser user =mAuth.getCurrentUser();
                    updateUI(user);
                }else{
                    Toast.makeText(LoginActivity.this,"Failed",Toast.LENGTH_SHORT).show();
                    updateUI(null);
                }
                Log.d("LoginActivity","end of onComplete");
            }


        });
        Log.d("LoginActivity","end of FirebaseGoogleAuth");
    }
    private void updateUI(FirebaseUser user) {
        Log.d("LoginActivity","begin of updateUI");
        GoogleSignInAccount account=GoogleSignIn.getLastSignedInAccount(getApplicationContext());
        if (account != null){
            String personName = account.getDisplayName();
            String personGivenName = account.getGivenName();
            String personFamilyName = account.getFamilyName();
            String personEmail = account.getEmail();
            String personId = account.getId();
            Uri personPhoto= account.getPhotoUrl();
            //startActivity(new Intent(LoginActivity.this,AccountActivity.class));

            Toast.makeText(LoginActivity.this,personName + personEmail,Toast.LENGTH_SHORT).show();
            //finish();
        }
        Log.d("LoginActivity","end of updateUI");

    }


    ///////////Firebaseauth////////////////
    public void startSingIn(View view) {
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



