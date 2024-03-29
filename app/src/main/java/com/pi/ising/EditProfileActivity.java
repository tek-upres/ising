package com.pi.ising;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import android.app.ProgressDialog;
import android.content.ContentResolver;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.view.View;
import android.webkit.MimeTypeMap;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.google.android.gms.tasks.Continuation;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.StorageTask;
import com.google.firebase.storage.UploadTask;
import com.pi.ising.model.User;
import com.rengwuxian.materialedittext.MaterialEditText;
import com.theartofdev.edmodo.cropper.CropImage;
import com.theartofdev.edmodo.cropper.CropImageView;

import java.util.HashMap;

public class EditProfileActivity extends AppCompatActivity {

    ImageView close,imageProfile;
    EditText bio,fullname,username;
    TextView save,tv_change;
    FirebaseUser firebaseUser;
    private Uri mImageUri;
    private StorageTask storageTask;
    StorageReference storageRef ;
    private static final int IMAGE_REQUEST=1;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_edit_profile);

        close=findViewById(R.id.close);
        imageProfile=findViewById(R.id.image_profile);
        fullname=findViewById(R.id.fullname);
        username=findViewById(R.id.username);
        bio=findViewById(R.id.bio);
        save=findViewById(R.id.save);
        close=findViewById(R.id.close);
        tv_change=findViewById(R.id.tv_change);

        firebaseUser= FirebaseAuth.getInstance().getCurrentUser();
        storageRef= FirebaseStorage.getInstance().getReference("uploads");
        DatabaseReference reference= FirebaseDatabase.getInstance().getReference("Users").child(firebaseUser.getUid());

        reference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                User user=snapshot.getValue(User.class);
                fullname.setText(user.getFullname());
                username.setText(user.getUsername());
                bio.setText(user.getBio());
                Glide.with(getApplicationContext()).load(user.getImageurl()).into(imageProfile);
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });

        close.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                finish();
            }
        });

        tv_change.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
               /* CropImage.activity().setAspectRatio(1,1)
                        .setCropShape(CropImageView.CropShape.OVAL).start(EditProfileActivity.this);*/
                selectImage();
            }
        });

        imageProfile.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
               /* CropImage.activity().setAspectRatio(1,1)
                        .setCropShape(CropImageView.CropShape.OVAL).start(EditProfileActivity.this);*/
                selectImage();
            }
        });

        save.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                updateProfile(fullname.getText().toString(),username.getText().toString()
                        ,bio.getText().toString());
                Toast.makeText(EditProfileActivity.this, "Profile Updated", Toast.LENGTH_SHORT).show();
            }


        });

    }

    private void selectImage() {
        Intent i = new Intent();
        i.setType("image/*");
        i.setAction(Intent.ACTION_GET_CONTENT);
        startActivityForResult(i,IMAGE_REQUEST);
    }

    private void updateProfile(String fullname, String username, String bio) {
        DatabaseReference reference= FirebaseDatabase.getInstance().getReference("Users").child(firebaseUser.getUid());
        HashMap<String,Object> hashMap=new HashMap<>();
        hashMap.put("fullname",fullname);
        hashMap.put("username",username);
        hashMap.put("bio",bio);
        reference.updateChildren(hashMap);

    }

    private String getFileExtension(Uri uri){
        ContentResolver contentResolver=getContentResolver();
        MimeTypeMap mimeTypeMap=MimeTypeMap.getSingleton();

        return mimeTypeMap.getExtensionFromMimeType(contentResolver.getType(uri));
    }
    private void uploadImage(){
        final ProgressDialog pd=new ProgressDialog(this);
        pd.setMessage("uploading");
        pd.show();
        if (mImageUri != null){
            final StorageReference filerefence = storageRef.child(System.currentTimeMillis()+"."+getFileExtension(mImageUri));
            storageTask = filerefence.putFile(mImageUri);
            storageTask.continueWithTask(new Continuation<UploadTask.TaskSnapshot,Task<Uri>>() {
                @Override
                public Task<Uri> then(@NonNull Task<UploadTask.TaskSnapshot> task) throws Exception {
                    if (!task.isSuccessful()){
                        throw task.getException();
                    }
                    return filerefence.getDownloadUrl();
                }
            }).addOnCompleteListener(new OnCompleteListener<Uri>() {
                @Override
                public void onComplete(@NonNull Task<Uri> task) {
                    if (task.isSuccessful()){
                        Uri downloadUri = task.getResult();
                        String myUri = downloadUri.toString();

                        DatabaseReference reference=FirebaseDatabase.getInstance().getReference("Users").child(firebaseUser.getUid());

                        HashMap<String,Object> hashMap=new HashMap<>();
                        hashMap.put("imageurl",myUri);
                        reference.updateChildren(hashMap);
                        pd.dismiss();

                    }else {
                        Toast.makeText(EditProfileActivity.this, "Failed", Toast.LENGTH_SHORT).show();
                    }
                }
            }).addOnFailureListener(new OnFailureListener() {
                @Override
                public void onFailure(@NonNull Exception e) {
                    Toast.makeText(EditProfileActivity.this,e.getMessage(), Toast.LENGTH_SHORT).show();
                }
            });
        }else {
            Toast.makeText(this, "No Image Selected!", Toast.LENGTH_SHORT).show();
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode== IMAGE_REQUEST && resultCode == RESULT_OK && data != null && data.getData() != null){
            /*CropImage.ActivityResult result=CropImage.getActivityResult(data);
            mImageUri =result.getUri();*/
            mImageUri=data.getData();
            if (storageTask != null && storageTask.isInProgress()){
                Toast.makeText(this, "Upload in progress..", Toast.LENGTH_SHORT).show();
            }else {
                uploadImage();
            }
        }else {
            Toast.makeText(this, "Something gone wrong!", Toast.LENGTH_SHORT).show();

        }
    }
}