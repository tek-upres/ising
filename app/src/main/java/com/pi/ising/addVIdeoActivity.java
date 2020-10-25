package com.pi.ising;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

import android.Manifest;
import android.app.Activity;
import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.media.MediaPlayer;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.text.TextUtils;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.MediaController;
import android.widget.Toast;
import android.widget.VideoView;

import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;

import java.util.HashMap;

public class addVIdeoActivity extends AppCompatActivity {
private ActionBar actionBar;
private EditText titleEt;
private VideoView videoView;
private Button uploadVideoBTn;
private FloatingActionButton pickvideo;
private static final int video_pick_gallery_code=100;
    private static final int video_pick_camera_code=101;
    private static final int camera_code_request=102;
    private String[] cameraPermissions;
    private Uri videoUri=null;
    private ProgressDialog progressDialog;
    private String title;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_v_ideo);
    actionBar=getSupportActionBar();

    actionBar.setTitle("Add new Video");
    actionBar.setDisplayShowHomeEnabled(true);
    actionBar.setDisplayHomeAsUpEnabled(true);
titleEt=findViewById(R.id.titleEt);
        videoView=findViewById(R.id.videoView);
        uploadVideoBTn=findViewById(R.id.uploadVideoBTn);
        pickvideo=findViewById(R.id.pickvideo);
progressDialog=new ProgressDialog(this);
progressDialog.setTitle("Please wait");
progressDialog.setMessage("uploading video");
progressDialog.setCanceledOnTouchOutside(false);
        //camera persmissions
        cameraPermissions = new String[]{Manifest.permission.CAMERA,Manifest.permission.WRITE_EXTERNAL_STORAGE};
        //uplode video
        uploadVideoBTn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
              uploadVideoFirebase();
            }
        });
        //video from camera/galery
        pickvideo.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
            title=titleEt.getText().toString().trim();
                if(TextUtils.isEmpty(title)){
                    Toast.makeText(addVIdeoActivity.this, "title is required", Toast.LENGTH_SHORT).show();
                }
                else if(videoUri==null){
                    Toast.makeText(addVIdeoActivity.this, "pick video before you can uplode", Toast.LENGTH_SHORT).show();
                }else {
                    uploadVideoFirebase();

                }

            }
        });
        pickvideo.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                videoPickDialog();
            }
        });
    }

    private void uploadVideoFirebase() {
        progressDialog.show();
        final String timestamp=""+System.currentTimeMillis();
        String filepathAndName="Videos/"+"video_"+timestamp;
        StorageReference storageReference= FirebaseStorage.getInstance().getReference(filepathAndName);
       storageReference.putFile(videoUri).addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
           @Override
           public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
               Task<Uri> uriTask=taskSnapshot.getStorage().getDownloadUrl();
               while (!uriTask.isSuccessful());
               Uri downloadUri =uriTask.getResult();
               if(uriTask.isSuccessful()){
                   HashMap<String,Object> hashMap=new HashMap<>();
                   hashMap.put("id",""+timestamp);
                   hashMap.put("title",""+title);
                   hashMap.put("timestamp",""+timestamp);
                   hashMap.put("videoUrl",""+downloadUri);
                   DatabaseReference  reference= FirebaseDatabase.getInstance().getReference("videos");
                   reference.child(timestamp).setValue(hashMap).addOnSuccessListener(new OnSuccessListener<Void>() {
                       @Override
                       public void onSuccess(Void aVoid) {
                      progressDialog.dismiss();
                           Toast.makeText(addVIdeoActivity.this, "video uploded", Toast.LENGTH_SHORT).show();
                       }
                   }).addOnFailureListener(new OnFailureListener() {
                       @Override
                       public void onFailure(@NonNull Exception e) {
                           progressDialog.dismiss();
                           Toast.makeText(addVIdeoActivity.this, ""+e.getMessage(), Toast.LENGTH_SHORT).show();
                       }
                   });
               }
           }
       }).addOnFailureListener(new OnFailureListener() {
           @Override
           public void onFailure(@NonNull Exception e) {
               progressDialog.dismiss();
               Toast.makeText(addVIdeoActivity.this, ""+e.getMessage(), Toast.LENGTH_SHORT).show();
           }
       });


    }

    private void videoPickDialog() {
        String[]option={"Camera","Gallery"};
        AlertDialog.Builder builder=new AlertDialog.Builder(this);
        builder.setTitle("uplode video from").setItems(option, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                if(which==0){
                  if(!checkcameraPermission()){
                      //camera not allowed
                      requestCameraPermission();
                  }else{
                      //camera allowed
                      videoPickCamera();
                  }
                }
                else if(which==1){
                  videoPickGallery();

                }
            }
        }).show();
    }
private void requestCameraPermission (){
    ActivityCompat.requestPermissions(this,cameraPermissions,camera_code_request);

}
private boolean checkcameraPermission(){
        boolean resulta1= ContextCompat.checkSelfPermission(this,Manifest.permission.CAMERA)== PackageManager.PERMISSION_GRANTED;
    boolean resulta2= ContextCompat.checkSelfPermission(this,Manifest.permission.WAKE_LOCK)== PackageManager.PERMISSION_GRANTED;
return resulta1 &&resulta2;
}
private void videoPickGallery(){
    Intent intent=new Intent();
    intent.setType("video/*");
    intent.setAction(Intent.ACTION_GET_CONTENT);
    startActivityForResult(Intent.createChooser(intent,"select Video"),video_pick_gallery_code);
}
private void videoPickCamera(){
    Intent intent=new Intent(MediaStore.ACTION_VIDEO_CAPTURE);
    startActivityForResult(intent,video_pick_camera_code);

}
private void   setVideoToVideoView(){
    MediaController mediaController=new MediaController(this);
    mediaController.setAnchorView(videoView);
    videoView.setMediaController(mediaController);
    videoView.setVideoURI(videoUri);
    videoView.requestFocus();
    videoView.setOnPreparedListener(new MediaPlayer.OnPreparedListener() {
        @Override
        public void onPrepared(MediaPlayer mp) {
            videoView.pause();
        }
    });
}

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        switch (requestCode){
            case camera_code_request:
                if(grantResults.length>0){
                    boolean cameraAccepted=grantResults[0]==PackageManager.PERMISSION_GRANTED;
                    boolean storageAccepted=grantResults[1]==PackageManager.PERMISSION_GRANTED;
                    if(cameraAccepted&&storageAccepted){
                        videoPickCamera();
                    }else {
                        Toast.makeText(this,"sorry Camera&storage permission are required",Toast.LENGTH_SHORT).show();
                    }
                }
        }
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        if (resultCode==RESULT_OK){
            if(requestCode==video_pick_gallery_code){
                videoUri=data.getData();
                setVideoToVideoView();
            }
            else if (requestCode==video_pick_camera_code){
                videoUri=data.getData();
                setVideoToVideoView();
            }
        }

        super.onActivityResult(requestCode, resultCode, data);
    }

    @Override
    public boolean onSupportNavigateUp() {
        onBackPressed();
        return super.onSupportNavigateUp();
    }

}