package com.pi.ising.adapetr;

import com.firebase.client.Firebase;
import com.pi.ising.model.user;

import android.content.Context;
import android.text.Layout;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import java.util.List;

public class Useradapter  extends RecyclerView.Adapter<Useradapter.ViewHolder>{
    private Context nCOntext;
    private List<user> nUsers;
    private Firebase firebaseUser;

    public Useradapter(Context nCOntext, List<user> nUsers) {
        this.nCOntext = nCOntext;
        this.nUsers = nUsers;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        return null;
    }

    @NonNull
   // @Override
//    public ViewHolder onCreateViewHolder(@NonNull ViewGroup viewGroup, int viewType) {
//        View view=LayoutInflater.from(nCOntext).inflate(android.support.v4.R.layout,viewGroup);
//        return new ViewHolder(view);
//
//    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {

    }

    @Override
    public int getItemCount() {
        return 0;
    }

    public class ViewHolder extends RecyclerView.ViewHolder{

        public ViewHolder(@NonNull View itemView) {
            super(itemView);
        }
    }
}
