package com.pi.ising.Adapetr;

import android.content.Context;
import android.media.MediaPlayer;
import android.net.Uri;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.MediaController;
import android.widget.Toast;
import android.widget.VideoView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.pi.ising.R;
import com.pi.ising.model.Post;
import com.squareup.picasso.Picasso;

import java.util.List;

public class MyVideoAdapter extends RecyclerView.Adapter<MyVideoAdapter.ViewHolder>{
    private Context context;
    private List<Post> mPost;

    public MyVideoAdapter(Context context, List<Post> mPost) {
        this.context = context;
        this.mPost = mPost;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view= LayoutInflater.from(context).inflate(R.layout.fotor_item,parent,false);

        return new MyVideoAdapter.ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull final ViewHolder holder, int position) {
Post post=mPost.get(position);
        System.out.println(post.getPostimage());
        Picasso.get().load(post.getPostimage()).into(holder.imageView);
        try{
            String link=post.getPostimage();
            MediaController mediaController=new MediaController(context);
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
            Toast.makeText(context, "Error connection", Toast.LENGTH_SHORT).show();
        }

    }

    @Override
    public int getItemCount() {
        return mPost.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder{
public VideoView post_video;
        public ImageView imageView;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            imageView=itemView.findViewById(R.id.pimage);
            post_video=itemView.findViewById(R.id.post_video);
        }
    }
}
