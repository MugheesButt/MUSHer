package com.mugheesnadeem.i160029_i160068;

import android.content.Context;
import android.content.Intent;
import android.database.sqlite.SQLiteDatabase;
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

class RVAdapter extends RecyclerView.Adapter<RVAdapter.RVViewHolder> {

    ArrayList<UserProfile> Users;
    ArrayList<MessageProfile> Messages ;
    Context c;
    String theLastMessage ;

    public RVAdapter(ArrayList<UserProfile> Users, Context c) {
        this.Users = Users;
        this.c = c;
    }


    @NonNull
    @Override
    public RVAdapter.RVViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {

        View row ;
        row = LayoutInflater.from(parent.getContext()).inflate(R.layout.row,parent,false);
        return new RVViewHolder(row);
    }


    @Override
    public void onBindViewHolder(@NonNull final RVAdapter.RVViewHolder holder, final int position){

        final UserProfile user = Users.get(position);


        Uri ImageURI = Uri.parse("android.resource://com.mugheesnadeem.i160029_i160068/drawable/ic_person_black_256dp");

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

        readLastMessage(user.getId(),holder.msg);

        holder.RowLayout.setOnClickListener(new View.OnClickListener() {
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


    private void readLastMessage(final String myID , final TextView lastMessage)
    {
        theLastMessage = "default";
        final FirebaseUser firebaseUser = FirebaseAuth.getInstance().getCurrentUser();

        if (firebaseUser != null) {
            DatabaseReference mDb = FirebaseDatabase.getInstance().getReference("Chats");

            mDb.addValueEventListener(new ValueEventListener() {
                @Override
                public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                    for (DataSnapshot snapshot : dataSnapshot.getChildren()) {
                        MessageProfile msg = snapshot.getValue(MessageProfile.class);
                        if ((msg.getReciever().equals(myID) && msg.getSender().equals(firebaseUser.getUid())) ||
                                (msg.getReciever().equals(firebaseUser.getUid()) && msg.getSender().equals(myID))) {
                            if (msg.getFlag() == 0)
                                theLastMessage = msg.getMessage();
                            else
                                theLastMessage = "Photo";
                        }
                    }

                    switch (theLastMessage) {
                        case "default":
                            lastMessage.setText(theLastMessage);
                            break;

                        default:
                            lastMessage.setText(theLastMessage);
                            break;

                    }

                    theLastMessage = "default";
                }

                @Override
                public void onCancelled(@NonNull DatabaseError databaseError) {

                }
            });
        }
    }

    public class RVViewHolder extends RecyclerView.ViewHolder{
        TextView name,msg,state;
        LinearLayout RowLayout ;
        ImageView image;

        public RVViewHolder(@NonNull View itemView) {
            super(itemView);
            name = itemView.findViewById(R.id.name);
            msg = itemView.findViewById(R.id.msg);
            state = itemView.findViewById(R.id.State);
            RowLayout = itemView.findViewById(R.id.RowLayout);
            image = itemView.findViewById(R.id.RowImg);

        }

    }

}
