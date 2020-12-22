package com.pi.ising.fragment;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.pi.ising.Adapetr.MyVideoAdapter;
import com.pi.ising.EditProfileActivity;
import com.pi.ising.R;
import com.pi.ising.model.Post;
import com.pi.ising.model.User;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.List;


public class ProfileFragment extends Fragment {

ImageView image_profile,options;
TextView posts,followers,following,fullname,bio,username;
Button edit_profile;
RecyclerView recyclerView;
MyVideoAdapter myVideoAdapter;
List<Post> postlist;
FirebaseUser firebaseUser;
String profileid;
ImageButton my_videos,saved_videos;
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
     View view=inflater.inflate(R.layout.fragment_profile,container,false);
     firebaseUser= FirebaseAuth.getInstance().getCurrentUser();
     // SharedPreferences.Editor editor= nCOntext.getSharedPreferences("PREFS",Context.MODE_PRIVATE).edit();
        SharedPreferences prefs=getContext().getSharedPreferences("PREFS",Context.MODE_PRIVATE);
        profileid=prefs.getString("profileid","none");

        image_profile=view.findViewById(R.id.image_profile);
        options=view.findViewById(R.id.options);
        posts=view.findViewById(R.id.posts);
        followers=view.findViewById(R.id.followers);
        following=view.findViewById(R.id.following);
        fullname=view.findViewById(R.id.fullname);
        bio=view.findViewById(R.id.bio);
        username=view.findViewById(R.id.username);
        edit_profile=view.findViewById(R.id.edit_profile);
        my_videos=view.findViewById(R.id.my_Posts);
        saved_videos=view.findViewById(R.id.save_videos);
     recyclerView=view.findViewById(R.id.recycle_view);
       recyclerView.setHasFixedSize(true);
        LinearLayoutManager linearLayoutManager=new GridLayoutManager(getContext(),3);
        postlist=new ArrayList<>();
        myVideoAdapter=new MyVideoAdapter(getContext(),postlist);
        recyclerView.setAdapter(myVideoAdapter);
        userinfo();
        getFollowers();
        getnrposts();
        myvideo();
        if(profileid.equals(firebaseUser.getUid())){
            edit_profile.setText("Edit Profile");
        }else{
checkFollow();
saved_videos.setVisibility(View.GONE);
        }
edit_profile.setOnClickListener(new View.OnClickListener() {
    @Override
    public void onClick(View v) {
        String btn=edit_profile.getText().toString();

        if(btn.equals("Edit Profile")){
            startActivity(new Intent(getContext(), EditProfileActivity.class));

        }else if (btn.equals("follow")){
            FirebaseDatabase.getInstance().getReference().child("Follow").child(firebaseUser.getUid())
                    .child("following").child(profileid).setValue(true);
            FirebaseDatabase.getInstance().getReference().child("Follow").child(profileid)
                    .child("followers").child(firebaseUser.getUid()).setValue(true);

        }else if(btn.equals("following")){
            FirebaseDatabase.getInstance().getReference().child("Follow").child(firebaseUser.getUid())
                    .child("following").child(profileid).removeValue();
            FirebaseDatabase.getInstance().getReference().child("Follow").child(profileid)
                    .child("followers").child(firebaseUser.getUid()).removeValue();
        }
    }
});
        return view;
    }
    private  void userinfo(){

        DatabaseReference reference=FirebaseDatabase.getInstance().getReference("Users").child(profileid);
        reference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                if(getContext()==null){
                    return;
                }
                User user=dataSnapshot.getValue(User.class);
                Glide.with(getContext()).load(user.getImageurl()).into(image_profile);
                username.setText(user.getUsername());
                fullname.setText(user.getFullname());
                bio.setText(user.getBio());
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
    }
    private void checkFollow (){
        DatabaseReference reference =FirebaseDatabase.getInstance().getReference()
                .child("Follow").child(firebaseUser.getUid()).child("following");
        reference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot  dataSnapshot) {
                if (dataSnapshot.child(profileid).exists()){
                    edit_profile.setText("following");
                }else{
                    edit_profile.setText("follow");
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
    }
    private void getFollowers(){
        DatabaseReference reference =FirebaseDatabase.getInstance().getReference()
                .child("Follow").child(profileid).child("followers");
        reference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
               followers.setText(""+snapshot.getChildrenCount());
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
        DatabaseReference referencee =FirebaseDatabase.getInstance().getReference()
                .child("Follow").child(profileid).child("following");
        referencee.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                following.setText(""+snapshot.getChildrenCount());
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
    }
private void getnrposts(){
    DatabaseReference reference=FirebaseDatabase.getInstance().getReference("videos").child(profileid);
    reference.addValueEventListener(new ValueEventListener() {
        @Override
        public void onDataChange(@NonNull DataSnapshot snapshot) {
            int i =0;
            for (DataSnapshot snapshot1:snapshot.getChildren()){
                Post post=snapshot1.getValue(Post.class);

                if(post.getPublisher().equals(profileid)){
                   i++;
                }
            }
            posts.setText(""+i);
        }

        @Override
        public void onCancelled(@NonNull DatabaseError error) {

        }
    });
}
private void myvideo(){
        DatabaseReference  reference=FirebaseDatabase.getInstance().getReference("videos");
        reference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                postlist.clear();
                for(DataSnapshot snapshot1:snapshot.getChildren()){
                    Post post=snapshot1.getValue(Post.class);
                    if(post.getPublisher().equals(profileid)){
                        postlist.add(post);
                    }
                }
                Collections.reverse(postlist);
                myVideoAdapter.notifyDataSetChanged();

            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
}
}