package com.pi.ising.Adapetr;

import android.content.Context;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.pi.ising.AccountActivity;
import com.pi.ising.R;
import com.pi.ising.model.Comment;
import com.pi.ising.model.User;

import java.util.List;

public class CommentsAdpter extends RecyclerView.Adapter<CommentsAdpter.ViewHolder>
{

    private Context mcontext;
    private List<Comment> mComment;
    private FirebaseUser firebaseUser;

    public CommentsAdpter(Context context, List<Comment> mComment) {
        this.mcontext = context;
        this.mComment = mComment;
    }


    // private
    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view= LayoutInflater.from(mcontext).inflate(R.layout.comments_item,parent,false);


        return new CommentsAdpter.ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
firebaseUser= FirebaseAuth.getInstance().getCurrentUser();
final Comment comment=mComment.get(position);
holder.comment.setText(comment.getComment());
getUserInfo(holder.image_profile,holder.username,comment.getPublisher());
holder.comment.setOnClickListener(new View.OnClickListener() {
    @Override
    public void onClick(View v) {
        Intent intent =new Intent(mcontext, AccountActivity.class);
  intent.putExtra("publisherid",comment.getPublisher() );
  mcontext.startActivity(intent);
    }
});
        holder.image_profile.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent =new Intent(mcontext, AccountActivity.class);
                intent.putExtra("publisherid",comment.getPublisher() );
                mcontext.startActivity(intent);
            }
        });
    }

    @Override
    public int getItemCount() {
        return mComment.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder{
        public ImageView image_profile;
        public TextView username,comment;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);

            image_profile=itemView.findViewById(R.id.image_profile);
            username=itemView.findViewById(R.id.username);
            comment=itemView.findViewById(R.id.comment);

        }

    }
    private void getUserInfo(final ImageView imageView, final TextView username, String publisherid){
        DatabaseReference reference= FirebaseDatabase.getInstance().getReference().child("Users").child(publisherid);
        reference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                User  user=snapshot.getValue(User.class);
                Glide.with(mcontext).load(user.getImageurl()).into(imageView);
                username.setText(user.getUsername());
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });

    }

}

