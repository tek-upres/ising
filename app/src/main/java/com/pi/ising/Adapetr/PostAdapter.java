package com.pi.ising.Adapetr;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.media.MediaPlayer;
import android.net.Uri;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.webkit.WebChromeClient;
import android.webkit.WebView;
import android.widget.ImageView;
import android.widget.MediaController;
import android.widget.TextView;
import android.widget.Toast;
import android.widget.VideoView;

import androidx.annotation.NonNull;
import androidx.fragment.app.FragmentActivity;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.engine.DiskCacheStrategy;
import com.bumptech.glide.request.RequestOptions;
import com.google.android.exoplayer2.ui.PlayerView;
import com.google.android.exoplayer2.upstream.BandwidthMeter;
import com.google.android.exoplayer2.upstream.DefaultBandwidthMeter;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.pi.ising.CommentsActivity;
import com.pi.ising.FollowersActivity;
import com.pi.ising.R;
import com.pi.ising.fragment.PostDetailsFragment;
import com.pi.ising.model.Post;
import com.pi.ising.model.User;
import com.squareup.picasso.Picasso;

import java.io.File;
import java.util.HashMap;
import java.util.List;

public class PostAdapter extends RecyclerView.Adapter<PostAdapter.ViewHolder> {
    public Context mContext;
    public List<Post> mPost;
    private FirebaseUser firebaseUser;

    public PostAdapter(Context mContext, List<Post> mPost) {
        this.mContext = mContext;
        this.mPost = mPost;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(mContext).inflate(R.layout.post_item,parent,false);
        return new PostAdapter.ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull final ViewHolder holder, int position) {



firebaseUser= FirebaseAuth.getInstance().getCurrentUser();
    final Post post=mPost.get(position);


        System.out.println("dddddddddddd"+post.getPostimage());
        Picasso.get().load(post.getPostimage()).into(holder.imageView);
        try{
            String link=post.getPostimage();
            MediaController mediaController=new MediaController(mContext);
            mediaController.setAnchorView(holder.post_video);
            Uri video=Uri.parse(link);
            holder.post_video.setMediaController(mediaController);
            holder.post_video.setVideoURI(video);
            holder.imageView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    holder.imageView.setVisibility(View.GONE);
                    holder.post_video.start();
                }
            });
            holder.post_video.setOnCompletionListener(new MediaPlayer.OnCompletionListener() {
                @Override
                public void onCompletion(MediaPlayer mp) {
                    holder.imageView.setVisibility(View.VISIBLE);
                }
            });
        }catch (Exception e){
            Toast.makeText(mContext, "Error connection", Toast.LENGTH_SHORT).show();
        }



  // Glide.with(mContext).load(post.getPostimage()).load(holder.post_video);
if(post.getDescription().equals("")){
    holder.description.setVisibility(View.GONE);
}else{
    holder.description.setVisibility(View.VISIBLE);
    holder.description.setText(post.getDescription());
}

publisherInfo(holder.imaga_profile,holder.username,holder.publisher,post.getPublisher());
isliked(post.getPostid(),holder.like);
nrlikes(holder.likes,post.getPostid());
getComments(post.getPostid(),holder.comments);
holder.like.setOnClickListener(new View.OnClickListener() {
    @Override
    public void onClick(View v) {
        if(holder.like.getTag().equals("like")){
                FirebaseDatabase.getInstance().getReference().child("Likes").child(post.getPostid()).child(firebaseUser.getUid()).setValue(true);
            addNotifications(post.getPublisher(),post.getPostid());
        }else {
            FirebaseDatabase.getInstance().getReference().child("Likes").child(post.getPostid()).child(firebaseUser.getUid()).removeValue();

        }
    }
});
        holder.post_video.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                SharedPreferences.Editor editor=mContext.getSharedPreferences("PREFS",Context.MODE_PRIVATE).edit();
                editor.putString("postid",post.getPostid()  );
                editor.apply();
                ((FragmentActivity)mContext).getSupportFragmentManager().beginTransaction().replace(R.id.fragment_container,
                        new PostDetailsFragment()).commit();
            }
        });
        holder.likes.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent=new Intent(mContext, FollowersActivity.class);
                intent.putExtra("id",post.getPostid());
                intent.putExtra("title","likes");
                mContext.startActivity(intent);
            }
        });
        holder.comment.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent=new Intent(mContext, CommentsActivity.class);
                intent.putExtra("postid",post.getPostid());
                intent.putExtra("publisshier",post.getPublisher());
                mContext.startActivity(intent);
            }
        });
        holder.comments.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent=new Intent(mContext, CommentsActivity.class);
                intent.putExtra("postid",post.getPostid());
                intent.putExtra("publisshier",post.getPublisher());
                mContext.startActivity(intent);
            }
        });
    }

    @Override
    public int getItemCount() {
        return mPost.size();
    }

    public class  ViewHolder extends RecyclerView.ViewHolder{
        public ImageView imaga_profile,like,comment,save;public VideoView post_video;
        public ImageView imageView;
        public TextView username,likes,publisher,description,comments;
        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            imageView=itemView.findViewById(R.id.pimage);
            imaga_profile=itemView.findViewById(R.id.image_profile);
            post_video=itemView.findViewById(R.id.post_video);
          //  post_video=itemView.findViewById(R.id.post_video);
            like=itemView.findViewById(R.id.like);
            comment=itemView.findViewById(R.id.comment);
            comments=itemView.findViewById(R.id.comments);
            save=itemView.findViewById(R.id.fav);
            username=itemView.findViewById(R.id.username);
            likes=itemView.findViewById(R.id.likes);
            publisher=itemView.findViewById(R.id.publisher);
            description=itemView.findViewById(R.id.description);
        }
    }
    private void getComments(String postid, final TextView commnets){
        DatabaseReference reference=FirebaseDatabase.getInstance().getReference().child("Comments").child(postid);
        reference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                commnets.setText("View All  "+snapshot.getChildrenCount()+"  Comments");
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });

    }
    public void isliked(String postid, final ImageView imageView){
        final FirebaseUser firebaseUser=FirebaseAuth.getInstance().getCurrentUser();
        DatabaseReference reference=FirebaseDatabase.getInstance().getReference().child("Likes").child(postid);
        reference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                if(snapshot.child(firebaseUser.getUid()).exists()){
                    imageView.setImageResource(R.drawable.ic_liked);
                    imageView.setTag("liked");
                }else {
                    imageView.setImageResource(R.drawable.ic_like);
                    imageView.setTag("like");
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
    }
    private  void nrlikes (final TextView likes , String postid){
        DatabaseReference reference1=FirebaseDatabase.getInstance().getReference().child("Likes").child(postid);
        reference1.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                likes.setText(snapshot.getChildrenCount()+"  likes");
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
    }
    private void addNotifications(String publisher, String postid) {
        DatabaseReference reference=FirebaseDatabase.getInstance().getReference("Notifications").child(publisher);

        HashMap<String,Object> hashMap=new HashMap<>();
        hashMap.put("userid",firebaseUser.getUid());
        hashMap.put("text","Liked your post");
        hashMap.put("postid",postid);
        hashMap.put("ispost",true);
        reference.push().setValue(hashMap);
    }
    private void  publisherInfo(final ImageView image_profile, final TextView username, final TextView publisher, final String userid){
        DatabaseReference reference = FirebaseDatabase.getInstance().getReference("Users").child(userid);

        reference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                User user=snapshot.getValue(User.class);

             Glide.with(mContext).load(user.getImageurl()).into(image_profile);
               username.setText(user.getUsername());
              publisher.setText(user.getUsername());
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
    }
}

