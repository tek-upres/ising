package com.pi.ising.Adapetr;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.VideoView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.pi.ising.R;
import com.pi.ising.model.Post;

import java.util.List;

public class MyVideoAdapter extends RecyclerView.Adapter<MyVideoAdapter.ViewHolder>{
    private Context context;
    private List<Post> mPost;

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view= LayoutInflater.from(context).inflate(R.layout.fotor_item,parent,false);

        return new MyVideoAdapter.ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
Post post=mPost.get(position);
 //       Glide.with(context).load(post.getPostimage()).into(holder.post_video);

    }

    @Override
    public int getItemCount() {
        return 0;
    }

    public class ViewHolder extends RecyclerView.ViewHolder{
public VideoView post_video;
        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            post_video=itemView.findViewById(R.id.post_video);
        }
    }
}
