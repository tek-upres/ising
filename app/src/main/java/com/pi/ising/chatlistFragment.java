package com.pi.ising;

import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.RecyclerView;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.pi.ising.Adapetr.chatlist;
import com.pi.ising.model.Chat;
import com.pi.ising.model.ChatList;
import com.pi.ising.model.User;

import java.util.ArrayList;
import java.util.List;

/**
 * A simple {@link Fragment} subclass.
 * Use the {@link chatlistFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class chatlistFragment extends Fragment {

    FirebaseAuth firebaseAuth;
    RecyclerView recyclerView;
    // TODO: Rename parameter arguments, choose names that match
    // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";

    // TODO: Rename and change types of parameters
    private String mParam1;
    private String mParam2;
    List<ChatList> chatListList;
    List<User>userList;
    DatabaseReference reference;
    FirebaseUser currentUser;
     chatlist adapterChatlist;
    public chatlistFragment() {
        // Required empty public constructor
    }

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @param param1 Parameter 1.
     * @param param2 Parameter 2.
     * @return A new instance of fragment chatlistFragment.
     */
    // TODO: Rename and change types and number of parameters
    public static chatlistFragment newInstance(String param1, String param2) {
        chatlistFragment fragment = new chatlistFragment();
        Bundle args = new Bundle();
        args.putString(ARG_PARAM1, param1);
        args.putString(ARG_PARAM2, param2);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            mParam1 = getArguments().getString(ARG_PARAM1);
            mParam2 = getArguments().getString(ARG_PARAM2);
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view=inflater.inflate(R.layout.fragment_chatlist,container,false);
        firebaseAuth=FirebaseAuth.getInstance();
        currentUser=FirebaseAuth.getInstance().getCurrentUser();
        recyclerView=view.findViewById(R.id.recyclerView);
        chatListList=new ArrayList<>();
        reference= FirebaseDatabase.getInstance().getReference("Chatlist").child(currentUser.getUid());
        reference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                chatListList.clear();
                for(DataSnapshot ds:snapshot.getChildren()){
                    ChatList chatList=ds.getValue(ChatList.class);
                    chatListList.add(chatList);

                }
                loadchats();
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
        // Inflate the layout for this fragment
        return view;
    }

    private void loadchats() {
        userList=new ArrayList<>();
        reference=FirebaseDatabase.getInstance().getReference("Users");
        reference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                userList.clear();
                for(DataSnapshot ds:snapshot.getChildren()){
                    User user=ds.getValue(User.class);
                    for (ChatList chatList:chatListList){
                        if(user.getId()!=null&&user.getId().equals(chatList.getId())){
                            userList.add(user);
                            break;
                        }
                    }
adapterChatlist=new chatlist(getContext(),userList);
                    recyclerView.setAdapter(adapterChatlist);
                    for(int i=0;i<userList.size();i++){
                       lastMessage(userList.get(i).getId());
                    }
                }


            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });

    }

    private void lastMessage(final String userid) {
        DatabaseReference reference=FirebaseDatabase.getInstance().getReference("Chats");
        reference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                String thelastMessage="default";
                for(DataSnapshot ds:snapshot.getChildren()){
                    Chat chat =ds.getValue(Chat.class);
                    if(chat==null){
                        continue;
                    }
                    String sender=chat.getSender();
                    String reciver=chat.getReceiver();
                    if(sender==null|| reciver==null){
                        continue;
                    }
                    if(chat.getReceiver().equals(currentUser.getUid())&&
                        chat.getSender().equals(userid)||
                                chat.getReceiver().equals(userid) && chat.getSender().equals(currentUser.getUid())){
                        thelastMessage=chat.getMessage();

                    }
                }
                adapterChatlist.setLastMessageMap(userid,thelastMessage);
                adapterChatlist.notifyDataSetChanged();

            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
    }
}