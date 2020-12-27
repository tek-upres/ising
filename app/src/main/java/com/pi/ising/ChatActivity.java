package com.pi.ising;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;
import com.pi.ising.Adapetr.chatAdapter;
import com.pi.ising.model.Chat;
import com.squareup.picasso.Picasso;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;


public class ChatActivity extends AppCompatActivity {

    Toolbar toolbar;
    RecyclerView recyclerView;
    ImageView profileIv;
    TextView nametv,userStatustv;
    EditText messageet;
    ImageButton sendbtn;
    FirebaseAuth firebaseAuth;
    FirebaseDatabase firebaseDatabase;
    DatabaseReference usersDbRef;
ValueEventListener seenlistenr;
DatabaseReference userRefForSenn;
List<Chat> chatList;
chatAdapter chatAdapter;




    String hisUid;
  //  FirebaseUser user=firebaseAuth.getCurrentUser();
    String myUid ;
String HisImage;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_chat);
        Toolbar toolbar = findViewById(R.id.toolbar1);
        setSupportActionBar(toolbar);
        toolbar.setTitle("");
        recyclerView = findViewById(R.id.chat_recycelview);
        profileIv = findViewById(R.id.profileiv);
        nametv = findViewById(R.id.nametv);
        userStatustv = findViewById(R.id.userstatustv);
        messageet = findViewById(R.id.messageET);
        sendbtn = findViewById(R.id.sendbtn);
        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(this);
        linearLayoutManager.setStackFromEnd(true);
        recyclerView.setHasFixedSize(true);
        recyclerView.setLayoutManager(linearLayoutManager);
        Intent intent = getIntent();
        hisUid = intent.getStringExtra("hisUid");

        firebaseAuth = FirebaseAuth.getInstance();
        firebaseDatabase = FirebaseDatabase.getInstance();
        usersDbRef = firebaseDatabase.getReference("Users");
        Query userquery = usersDbRef.orderByChild("id").equalTo(hisUid);
        userquery.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                for (DataSnapshot ds : snapshot.getChildren()) {
                    String name = "" + ds.child("username").getValue();
                    HisImage = "" + ds.child("imageurl").getValue();
                    nametv.setText(name);

                    try {
                        Picasso.get().load(HisImage).into(profileIv);

                    } catch (Exception e) {
                        Picasso.get().load(R.drawable.ic_default).into(profileIv);
                    }
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });


        sendbtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String message = messageet.getText().toString().trim();
                if (TextUtils.isEmpty(message)) {
                    Toast.makeText(ChatActivity.this, "Cannot send the empty message", Toast.LENGTH_SHORT);
                } else {
                    sendMessage(message);
                }
            }
        });
        FirebaseUser user=firebaseAuth.getCurrentUser();
            myUid=user.getUid();

        readMessages();
        seenMessage();

 final DatabaseReference chatRef1=FirebaseDatabase.getInstance().getReference("Chatlist")
         .child(myUid)
         .child(hisUid);
 chatRef1.addValueEventListener(new ValueEventListener() {
     @Override
     public void onDataChange(@NonNull DataSnapshot snapshot) {
         if(!snapshot.exists()){
             chatRef1.child("id").setValue(hisUid);
         }
     }

     @Override
     public void onCancelled(@NonNull DatabaseError error) {

     }
 });
        final DatabaseReference chatRef2=FirebaseDatabase.getInstance().getReference("Chatlist")
                .child(hisUid)
                .child(myUid);
        chatRef2.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                if(!snapshot.exists()){
                    chatRef2.child("id").setValue(myUid);
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
    }


    private void seenMessage() {
        userRefForSenn=FirebaseDatabase.getInstance().getReference("Chats");
        seenlistenr=userRefForSenn.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                for(DataSnapshot ds:snapshot.getChildren()){
                    Chat chat1=ds.getValue(Chat.class);
                    if(chat1.getReceiver().equals(myUid)&& chat1.getSender().equals(hisUid)){
                        HashMap<String ,Object>hasSeenHasMap =new HashMap<>();
                   hasSeenHasMap.put("isSeen",true);
                   ds.getRef().updateChildren(hasSeenHasMap);
                    }
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
    }

    private void readMessages() {
        chatList=new ArrayList<>();
        DatabaseReference dfRef=FirebaseDatabase.getInstance().getReference("Chats");
        dfRef.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                chatList.clear();
                for(DataSnapshot ds:snapshot.getChildren()){
                    Chat chat1=ds.getValue(Chat.class);

                    System.out.println(chat1.getReceiver());
                    System.out.println(myUid);
                    System.out.println("ddddddddddddddddddddddddddddd"+ chat1.getSender().equals(hisUid));
                    if(chat1.getReceiver().equals(myUid)&& chat1.getSender().equals(hisUid)||chat1.getReceiver().equals(hisUid)&& chat1.getSender().equals(myUid)){
chatList.add(chat1);
                    }
                    chatAdapter=new chatAdapter(ChatActivity.this,chatList,HisImage);
               chatAdapter.notifyDataSetChanged();
               recyclerView.setAdapter(chatAdapter);
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
    }

    private void sendMessage(String message) {
      String tiemstamp=String.valueOf(System.currentTimeMillis());

        DatabaseReference databaseReference=FirebaseDatabase.getInstance().getReference();
        HashMap<String,Object>hashMap=new HashMap<>();
        hashMap.put("sender",myUid);
        hashMap.put("receiver",hisUid);
        hashMap.put("message",message);
        hashMap.put("timestamp",tiemstamp);
        hashMap.put("isSeen",false);
        databaseReference.child("Chats").push().setValue(hashMap);
        messageet.setText("");

    }

    private  void chekuserstatus(){
        FirebaseUser user=firebaseAuth.getCurrentUser();
  if(user!=null){
      myUid=user.getUid();

  }else{
      startActivity(new Intent(this,AccountActivity.class));
  finish();
  }
    }

    @Override
    protected void onStart() {
        chekuserstatus();
        super.onStart();
    }

    @Override
    protected void onPause() {
        super.onPause();
        userRefForSenn.removeEventListener(seenlistenr);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {

        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {

        return super.onOptionsItemSelected(item);
    }
}