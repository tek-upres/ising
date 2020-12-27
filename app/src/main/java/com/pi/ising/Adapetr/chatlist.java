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

import com.pi.ising.ChatActivity;
import com.pi.ising.R;
import com.pi.ising.model.User;
import com.squareup.picasso.Picasso;

import java.util.HashMap;
import java.util.List;

public class chatlist extends RecyclerView.Adapter<chatlist.MyHolder> {
    Context context;
    List<User> userList;
    private HashMap<String,String>lastMessageMap;

    public chatlist(Context context, List<User> userList) {
        this.context = context;
        this.userList = userList;
        lastMessageMap = new HashMap<>();
    }

    @NonNull
    @Override
    public MyHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
     View view= LayoutInflater.from(context).inflate(R.layout.row_chatlist,parent,false);
        return new MyHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull MyHolder holder, int position) {
final String hisUid=userList.get(position).getId();
        String userImage=userList.get(position).getImageurl();
        String username=userList.get(position).getUsername();
        String lastMessage=lastMessageMap.get(hisUid);
        System.out.println("messaaaaaa"+lastMessageMap.get(hisUid));
        holder.nameTv.setText(username);
        if(lastMessage==null|| lastMessage.equals("default")){
holder.lastMessageTv.setVisibility(View.GONE);

        }else {
          holder.lastMessageTv.setVisibility(View.VISIBLE);
            holder.lastMessageTv.setText(lastMessage);
        }
        try {
            Picasso.get().load(userImage).placeholder(R.drawable.ic_default).into(holder.profileTv);
        }catch (Exception e){
            Picasso.get().load(R.drawable.ic_default).into(holder.profileTv);
        }
      //  if(userList.get(position).getOnlineStatus().equals("online")){
         //   holder.onlineStatusTv.setImageResource(R.drawable.cercle_online);
      //  }
      //  else {
        //    holder.onlineStatusTv.setImageResource(R.drawable.circle_ofline);
       // }
        holder.itemView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent=new Intent(context, ChatActivity.class);
                intent.putExtra("hisUid",hisUid);
                context.startActivity(intent);
            }
        });

    }


    public  void setLastMessageMap(String userId,String lastMessage){

        lastMessageMap.put(userId,lastMessage);
    }

    @Override
    public int getItemCount() {
        return userList.size();
    }

    class MyHolder extends RecyclerView.ViewHolder{
ImageView profileTv,onlineStatusTv;
TextView nameTv,lastMessageTv;

        public MyHolder(@NonNull View itemView) {
            super(itemView);
            profileTv=itemView.findViewById(R.id.profileTv);
            onlineStatusTv=itemView.findViewById(R.id.onlineStatusTv);
            nameTv=itemView.findViewById(R.id.nameTv);
            lastMessageTv=itemView.findViewById(R.id.lastMessageTv);

        }
    }
}
