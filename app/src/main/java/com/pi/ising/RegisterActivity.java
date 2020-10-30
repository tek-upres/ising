package com.pi.ising;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.EditText;
import android.widget.RadioButton;
import android.widget.Toast;

import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.firestore.FirebaseFirestore;
import com.pi.ising.model.Utilisateur;

public class RegisterActivity extends AppCompatActivity {
        EditText username,fullName,email,password,city,birthday,number;
        RadioButton asUser,asTalentedUser,asStar;
        FirebaseDatabase firebaseDatabase;
        DatabaseReference databaseReference;
        FirebaseAuth fauth;
        boolean valid=true;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register);
        ///////////////////////////////
        firebaseDatabase=FirebaseDatabase.getInstance();
        fauth=FirebaseAuth.getInstance();
        databaseReference=firebaseDatabase.getReference("Users");
        //////////////////////////////
        username=findViewById(R.id.inputUserName);
        fullName=findViewById(R.id.inputFullName);
        email=findViewById(R.id.inputEmail);
        password=findViewById(R.id.password);
        city=findViewById(R.id.inputCity);
        birthday=findViewById(R.id.inputBirthday);
        number=findViewById(R.id.inputPhoneNumber);
        asUser=findViewById(R.id.asUser);
        asTalentedUser=findViewById(R.id.asTalentedUser);
        asStar=findViewById(R.id.asStar);


        ///////////////////////////////




        
    }
    public boolean checkField(EditText textField){
        if(textField.getText().toString().isEmpty()){
            textField.setError("Field is empty");
            valid=false;
        }else{
            valid = true;
        }
        return valid;
    }
    public void register(View view){
        checkField(fullName);
        checkField(email);
        checkField(password);
        checkField(city);
        checkField(birthday);
        checkField(number);
        checkField(username);


        if (valid){
            //start the user registration process
            fauth.createUserWithEmailAndPassword(email.getText().toString(),password.getText().toString()).addOnSuccessListener(new OnSuccessListener<AuthResult>() {
                @Override
                public void onSuccess(AuthResult authResult) {
                    Toast.makeText(RegisterActivity.this,"Account created",Toast.LENGTH_SHORT).show();
                    registerDatabase();
                    startActivity(new Intent(getApplicationContext(),AccountActivity.class));
                    finish();
                }

            }).addOnFailureListener(new OnFailureListener() {
                @Override
                public void onFailure(@NonNull Exception e) {
                    Toast.makeText(RegisterActivity.this,"Failed to create Account",Toast.LENGTH_SHORT).show();

                }
            });

        }
    }

    private void registerDatabase() {
        String id= fauth.getCurrentUser().getUid();
        String valusername=username.getText().toString();
        String valfullname=fullName.getText().toString();
        String valemail=email.getText().toString();
        String valpassword=password.getText().toString();
        String valcity=city.getText().toString();
        String valphone=number.getText().toString();
        String valBirthday=birthday.getText().toString();
        String userRole="";
        if(asUser.isChecked())userRole="user";
        else if(asTalentedUser.isChecked())userRole="talented user";
        else userRole="star";
        Utilisateur user=new Utilisateur(valfullname,valusername,valemail,valpassword,valcity
        ,userRole,valBirthday,valphone);

        databaseReference.child(id).setValue(user).addOnSuccessListener(new OnSuccessListener<Void>() {
            @Override
            public void onSuccess(Void aVoid) {
                Toast.makeText(getApplicationContext(),"Registration completed",Toast.LENGTH_SHORT).show();
            }
        }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {
                Toast.makeText(getApplicationContext(),"Registration failed",Toast.LENGTH_SHORT).show();
            }
        });


    }
}