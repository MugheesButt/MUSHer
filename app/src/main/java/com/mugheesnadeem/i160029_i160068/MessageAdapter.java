package com.mugheesnadeem.i160029_i160068;

import android.content.Context;
import android.content.Intent;
import android.database.sqlite.SQLiteDatabase;
import android.net.Uri;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
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
import com.google.firebase.storage.FirebaseStorage;
import com.squareup.picasso.Picasso;

import java.util.ArrayList;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;
import androidx.recyclerview.widget.RecyclerView;

class MessageAdapter extends RecyclerView.Adapter<MessageAdapter.MessageViewHolder> {

    public static final int MSG_RIGHT_TEXT = 0;
    public static final int MSG_RIGHT_IMAGE = 1;
    public static final int MSG_LEFT_TEXT = 2;
    public static final int MSG_LEFT_IMAGE = 3;

    private Context c;
    private ArrayList<MessageProfile> Messages;
    private String ImageURI ;
    private FirebaseUser firebaseUser;

    public MessageAdapter(ArrayList<MessageProfile> Messages, Context c, String ImageURI) {
        this.Messages = Messages;
        this.c = c;
        this.ImageURI = ImageURI;
    }


    @NonNull
    @Override
    public MessageAdapter.MessageViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View row ;
        if(viewType == MSG_RIGHT_TEXT)
        {
            row = LayoutInflater.from(parent.getContext()).inflate(R.layout.chat_right,parent,false);
        }

        else if (viewType == MSG_RIGHT_IMAGE)
        {
            row = LayoutInflater.from(parent.getContext()).inflate(R.layout.image_right,parent,false);
        }

        else if (viewType == MSG_LEFT_TEXT){
            row = LayoutInflater.from(parent.getContext()).inflate(R.layout.chat_left,parent,false);
        }

        else
        {
            row = LayoutInflater.from(parent.getContext()).inflate(R.layout.image_left,parent,false);
        }

        return new MessageViewHolder(row);
    }


    @Override
    public void onBindViewHolder(@NonNull final MessageAdapter.MessageViewHolder holder, final int position){

        MessageProfile message = Messages.get(position);
        FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();

        if(getItemViewType(position) == MSG_RIGHT_TEXT)
        {
                holder.right_msg.setText(message.getMessage());
        }

        else if (getItemViewType(position) == MSG_RIGHT_IMAGE)
        {
            Picasso.get().load(message.getMessage()).into(holder.pic_right);
        }

        else if (getItemViewType(position) == MSG_LEFT_TEXT)
        {
            setImage(message.getSender(),holder.sender_image);
            holder.left_msg.setText(message.getMessage());
        }

        else
        {
            setImage(message.getSender(),holder.sender_image);
            Picasso.get().load(message.getMessage()).into(holder.pic_left);

        }

    }

    private void setImage(final String userID, final ImageView img)
    {
        img.setImageURI(Uri.parse("android.resource://com.mugheesnadeem.i160029_i160068/drawable/ic_person_black_24dp"));
        DatabaseReference myDb = FirebaseDatabase.getInstance().getReference("Users");
        myDb.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                for (DataSnapshot snapshot : dataSnapshot.getChildren())
                {
                    UserProfile userProfile = snapshot.getValue(UserProfile.class);
                    if (userID == userProfile.getId())
                    {
                        /*
                        if (userProfile.getImageURL().equals("default"))
                            img.setImageURI(Uri.parse("android.resource://com.mugheesnadeem.i160029_i160068/drawable/ic_person_black_24dp"));
                        else
                            Picasso.get().load(userProfile.getImageURL()).into(img);

                         */

                    }
                }

            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });

    }

    @Override
    public int getItemViewType(int position) {
        firebaseUser = FirebaseAuth.getInstance().getCurrentUser();
        if ((Messages.get(position).getSender().equals(firebaseUser.getUid())) && (Messages.get(position).getFlag() == 0))
        {
            return MSG_RIGHT_TEXT;
        }


        else if ((Messages.get(position).getSender().equals(firebaseUser.getUid())) && (Messages.get(position).getFlag() == 1))
        {
            return MSG_RIGHT_IMAGE;
        }

        else if ((Messages.get(position).getReciever().equals(firebaseUser.getUid())) && (Messages.get(position).getFlag() == 0))
        {
            return MSG_LEFT_TEXT;
        }

        else
        {
            return MSG_LEFT_IMAGE;
        }

    }

    @Override
    public int getItemCount() {

        return Messages.size();
    }


    public class MessageViewHolder extends RecyclerView.ViewHolder{
        TextView left_msg,right_msg;
        LinearLayout RightRowLayout , LeftRowLayout ;
        ImageView sender_image , pic_right , pic_left;

        public MessageViewHolder(@NonNull View itemView) {
            super(itemView);
            left_msg = itemView.findViewById(R.id.recievermsg);
            right_msg = itemView.findViewById(R.id.sendermsg);
           // RightRowLayout = itemView.findViewById(R.id.RightRowLayout);
            //LeftRowLayout = itemView.findViewById(R.id.LeftRowLayout);
            sender_image = itemView.findViewById(R.id.RecRowImg);
            pic_right = itemView.findViewById(R.id.pic_right);
            pic_left = itemView.findViewById(R.id.pic_left);
        }

    }

}
