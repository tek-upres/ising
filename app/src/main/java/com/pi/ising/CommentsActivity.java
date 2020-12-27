package com.pi.ising;

import androidx.annotation.NonNull;
import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;
import android.widget.Toolbar;

import com.bumptech.glide.Glide;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.pi.ising.Adapetr.CommentsAdpter;
import com.pi.ising.model.Comment;
import com.pi.ising.model.User;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

public class CommentsActivity extends AppCompatActivity {
private RecyclerView recyclerView;
private CommentsAdpter commentsAdpter ;
private List<Comment> commentList;
    EditText addcomment;
ImageView image_profile;
TextView post;
String postid;
String publisshier;
FirebaseUser firebaseUser;

    @RequiresApi(api = Build.VERSION_CODES.LOLLIPOP)
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_comments);
        Toolbar toolbar= findViewById(R.id.toolbar);
toolbar.setTitle("Comments");
    //    setSupportActionBar(toolbar);
//getSupportActionBar().setDisplayHomeAsUpEnabled(true);
recyclerView=findViewById(R.id.recycle_view);
     recyclerView.setHasFixedSize(true);
     LinearLayoutManager linearLayoutManager=new LinearLayoutManager(this);
     recyclerView.setLayoutManager(linearLayoutManager);
     commentList =new ArrayList<>();
     commentsAdpter=new CommentsAdpter(this,commentList);
recyclerView.setAdapter(commentsAdpter);


        addcomment=findViewById(R.id.add_comment);
image_profile=findViewById(R.id.image_profile);
post=findViewById(R.id.post);
firebaseUser= FirebaseAuth.getInstance().getCurrentUser();
        Intent intent=getIntent();
        postid=intent.getStringExtra("postid");
        publisshier=intent.getStringExtra("publisshier");
post.setOnClickListener(new View.OnClickListener() {
    @Override
    public void onClick(View v) {

        if(addcomment.getText().toString().equals("")){
            Toast.makeText(CommentsActivity.this,"You can't send empty comment",Toast.LENGTH_SHORT).show();
        }else {
          addComment();
        }
    }
});
getImage();
readComments();

    }
    private void addComment(){
        DatabaseReference reference= FirebaseDatabase.getInstance().getReference("Comments").child(postid);
        HashMap<String,Object> hashMap=new HashMap<>();
        hashMap.put("comment",addcomment.getText().toString());
        hashMap.put("publisher",firebaseUser.getUid());
        reference.push().setValue(hashMap);
        addNotifications();
        addcomment.setText("");
    }
    private void getImage() {
        DatabaseReference reference = FirebaseDatabase.getInstance().getReference("Users").child(firebaseUser.getUid());
        reference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                User user = snapshot.getValue(User.class);
                Glide.with(getApplicationContext()).load(user.getImageurl()).into(image_profile);
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
    }
    private void addNotifications() {
        DatabaseReference reference=FirebaseDatabase.getInstance().getReference("Notifications").child(publisshier);

        HashMap<String,Object> hashMap=new HashMap<>();
        hashMap.put("userid",firebaseUser.getUid());
        hashMap.put("text","Commented "+addcomment.getText().toString());
        hashMap.put("postid","");
        hashMap.put("ispost",false);
        reference.push().setValue(hashMap);
    }

    private  void readComments(){
        DatabaseReference reference=FirebaseDatabase.getInstance().getReference("Comments").child(postid);
        reference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                commentList.clear();
                for (DataSnapshot snapshot1:snapshot.getChildren()){
                    Comment comment=snapshot1.getValue(Comment.class);
               commentList.add(comment);


                }
                commentsAdpter.notifyDataSetChanged();
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
}

}