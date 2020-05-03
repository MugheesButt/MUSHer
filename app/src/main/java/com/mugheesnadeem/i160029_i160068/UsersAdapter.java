package com.mugheesnadeem.i160029_i160068;

import android.content.Context;
import android.content.Intent;
import android.content.res.ColorStateList;
import android.content.res.Resources;
import android.graphics.Color;
import android.net.Uri;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.squareup.picasso.Picasso;

import java.util.ArrayList;

import androidx.annotation.NonNull;
import androidx.core.content.ContextCompat;
import androidx.recyclerview.widget.RecyclerView;

class UsersAdapter extends RecyclerView.Adapter<UsersAdapter.UsersViewHolder> {

    ArrayList<UserProfile> Users;
    ArrayList<MessageProfile> Messages ;
    Context c;

    public UsersAdapter(ArrayList<UserProfile> Users, Context c) {
        this.Users = Users;
        this.c = c;
    }


    @NonNull
    @Override
    public UsersAdapter.UsersViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {

        View row ;

        row = LayoutInflater.from(parent.getContext()).inflate(R.layout.all_users_row,parent,false);

        return new UsersAdapter.UsersViewHolder(row);
    }



    @Override
    public void onBindViewHolder(@NonNull final UsersAdapter.UsersViewHolder holder, final int position){

        final UserProfile user = Users.get(position);


        Uri ImageURI = Uri.parse("android.resource://com.mugheesnadeem.i160029_i160068/drawable/ic_person_black_24dp");

        if (user.getImageURL().equals("default"))
            holder.image.setImageURI(ImageURI);
        else
            Picasso.get().load(user.getImageURL()).into(holder.image);

        holder.name.setText(user.getUsername());


        if (user.getState().equals("Offline"))
        {
            holder.state.setTextColor(ContextCompat.getColor(c, R.color.red));
        }
        else
        {
            holder.state.setTextColor(ContextCompat.getColor(c, R.color.green));
        }

        holder.state.setText(user.getState());

        holder.UserRowLayout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(c , ChatActivity.class);
                intent.putExtra("userID" , user.getId());
                c.startActivity(intent);
            }
        });
    }


    @Override
    public int getItemCount() {

        return Users.size();
    }



    public class UsersViewHolder extends RecyclerView.ViewHolder{
        TextView name , state;
        LinearLayout UserRowLayout ;
        ImageView image;

        public UsersViewHolder(@NonNull View itemView) {
            super(itemView);
            name = itemView.findViewById(R.id.Username);
            state = itemView.findViewById(R.id.UserState);
            UserRowLayout = itemView.findViewById(R.id.UserRowLayout);
            image = itemView.findViewById(R.id.UserRowImg);

        }

    }
}
