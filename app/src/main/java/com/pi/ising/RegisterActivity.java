package com.pi.ising;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.app.ProgressDialog;
import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.RadioButton;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import java.util.HashMap;

public class RegisterActivity extends AppCompatActivity {
EditText username,fullname,email,password;
Button register;
TextView txt_login;
FirebaseAuth auth;
DatabaseReference reference;
ProgressDialog pd;
    String userRole;
    RadioButton asUser,asTalentedUser,asStar;
    boolean valid=true;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register);
        username=findViewById(R.id.username);
        fullname=findViewById(R.id.fullname);
        email=findViewById(R.id.email);
        password=findViewById(R.id.password);
        register=findViewById(R.id.register);
        txt_login=findViewById(R.id.txt_login);
        auth=FirebaseAuth.getInstance();
        asUser=findViewById(R.id.asUser);
        asTalentedUser=findViewById(R.id.asTalentedUser);
        asStar=findViewById(R.id.asStar);
txt_login.setOnClickListener(new View.OnClickListener() {
    @Override
    public void onClick(View v) {
        startActivity(new Intent(RegisterActivity.this,LoginActivity.class));
    }
});
register.setOnClickListener(new View.OnClickListener() {
    @Override
    public void onClick(View v) {
        pd=new ProgressDialog(RegisterActivity.this);
        pd.setMessage("Pleas wait...");
        pd.show();
        String str_username=username.getText().toString();
        String str_fullname=fullname.getText().toString();
        String str_email=email.getText().toString();
        String str_password=password.getText().toString();

        if(asUser.isChecked())userRole="user";
        else if(asTalentedUser.isChecked())userRole="talented user";
        else userRole="star";
        if(TextUtils.isEmpty(str_username)||TextUtils.isEmpty(str_fullname)||TextUtils.isEmpty(str_email)||TextUtils.isEmpty(str_password)){
            Toast.makeText(RegisterActivity.this,"All fileds are required",Toast.LENGTH_SHORT).show();

        }else if(str_password.length()<6) {
            Toast.makeText(RegisterActivity.this,"Password mut have 6 characters",Toast.LENGTH_SHORT).show();

        } else{
register(str_username,str_fullname,str_email,str_password,userRole);
        }
    }
});

    }

    private  void register (final String username, final String fullname , String email , String password, final String userRole){

        auth.createUserWithEmailAndPassword(email,password)
                .addOnCompleteListener(RegisterActivity.this, new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {

                        if(task.isSuccessful()){
                            FirebaseUser firebaseUser=auth.getCurrentUser();
                            String userid=firebaseUser.getUid();
                            reference=FirebaseDatabase.getInstance().getReference().child("Users").child(userid);
                            HashMap<String,Object> hashMap=new HashMap<>();
                            hashMap.put("id",userid);
                            hashMap.put("username",username.toLowerCase());
                            hashMap.put("fullname",fullname);
                            hashMap.put("bio","");
                            hashMap.put("imageurl","https://firebasestorage.googleapis.com/v0/b/ising-vers0.appspot.com/o/User_icon_BLACK-01.png?alt=media&token=4bbc3cba-3117-4fe8-a4d8-e5b8a619dd08");
                            hashMap.put("userRole",userRole);
                            reference.setValue(hashMap).addOnCompleteListener(new OnCompleteListener<Void>() {
                                @Override
                                public void onComplete(@NonNull Task<Void> task) {
                                 if(task.isSuccessful()){
                                     pd.dismiss();
                                     Intent intent =new Intent(RegisterActivity.this,AccountActivity.class);
                                     intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK|Intent.FLAG_ACTIVITY_NEW_TASK);
                                     startActivity(intent);
                                 }
                                }
                            });



                        }else {
                            pd.dismiss();
                            Toast.makeText(RegisterActivity.this,"You can't register width thi email or password",Toast.LENGTH_SHORT).show();
                        }
                    }
                });


    }
}