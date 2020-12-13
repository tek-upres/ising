package com.pi.ising.Adapetr;

import com.bumptech.glide.Glide;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.pi.ising.R;
import com.pi.ising.fragment.ProfileFragment;
import com.pi.ising.model.User;

import android.content.Context;
import android.content.SharedPreferences;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.fragment.app.FragmentActivity;
import androidx.recyclerview.widget.RecyclerView;

import java.util.List;

import de.hdodenhof.circleimageview.CircleImageView;

public class Useradapter  extends RecyclerView.Adapter<Useradapter.ViewHolder>{
    private Context nCOntext;
    private List<User> nUsers;
    private FirebaseUser firebaseUser;

    public Useradapter(Context nCOntext, List<User> nUsers) {
        this.nCOntext = nCOntext;
        this.nUsers = nUsers;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View  view=LayoutInflater.from(nCOntext).inflate(R.layout.user_item,parent,false);
        return new Useradapter.ViewHolder(view);

    }

    @Override
    public void onBindViewHolder(@NonNull final ViewHolder viewHolder, int position) {
        firebaseUser= (FirebaseUser)FirebaseAuth.getInstance().getCurrentUser();
        final User user=nUsers.get(position);
        viewHolder.btn_follow.setVisibility(View.VISIBLE);
        viewHolder.username.setText(user.getUsername());
        viewHolder.fulname.setText(user.getFullname());
        Glide.with(nCOntext).load(user.getImageurl()).into(viewHolder.image_profile);
       isFollowing(user.getId(),viewHolder.btn_follow);

        if(user.getId().equals((String)firebaseUser.getUid())){
            viewHolder.btn_follow.setVisibility(View.GONE);
        }
        viewHolder.itemView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                SharedPreferences.Editor editor= nCOntext.getSharedPreferences("PREFS",Context.MODE_PRIVATE).edit();
                editor.putString("profileid",user.getId());
                editor.apply();
                ((FragmentActivity)nCOntext).getSupportFragmentManager().beginTransaction().replace(R.id.fragment_container,
                        new ProfileFragment()).commit();
            }
        });
        viewHolder.btn_follow.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(viewHolder.btn_follow.getText().toString().equals("follow")){
                    FirebaseDatabase.getInstance().getReference().child("Follow").child(firebaseUser.getUid())
                            .child("following").child(user.getId()).setValue(true);
                    FirebaseDatabase.getInstance().getReference().child("Follow").child(user.getId())
                            .child("followers").child(firebaseUser.getUid()).setValue(true);
                }
                else{
                    FirebaseDatabase.getInstance().getReference().child("Follow").child(firebaseUser.getUid())
                            .child("following").child(user.getId()).removeValue();
                    FirebaseDatabase.getInstance().getReference().child("Follow").child(user.getId())
                            .child("followers").child(firebaseUser.getUid()).removeValue();
                }

            }
        });
    }




    @Override
    public int getItemCount() {
        return nUsers.size();
    }
    public class ViewHolder extends RecyclerView.ViewHolder{
public TextView username;
        public TextView fulname;
        public CircleImageView image_profile;
        public Button btn_follow;
        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            username=itemView.findViewById(R.id.username);
            fulname=itemView.findViewById(R.id.fullname);
            image_profile=itemView.findViewById(R.id.image_profile);
            btn_follow=itemView.findViewById(R.id.btn_follow);



        }
    }
    private void  isFollowing (final String id, final Button button){
       DatabaseReference reference= FirebaseDatabase.getInstance().getReference()
               .child("Follow").child(firebaseUser.getUid()).child("following");
       reference.addValueEventListener(new ValueEventListener() {
          @Override
          public void onDataChange(@NonNull DataSnapshot snapshot) {
              if ( snapshot.child(id).exists()){
                button.setText("following");
               }else{
                  button.setText("follow");
               }
           }

         @Override
           public void onCancelled(@NonNull DatabaseError error) {

           }
       });
    }
}
