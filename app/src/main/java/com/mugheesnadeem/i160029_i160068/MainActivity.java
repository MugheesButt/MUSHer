package com.mugheesnadeem.i160029_i160068;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.content.Intent;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ImageView;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;
import com.rengwuxian.materialedittext.MaterialEditText;

import java.util.ArrayList;
import java.util.HashMap;

public class MainActivity extends AppCompatActivity {

    ImageButton NewMsg , logout, prof;
    FirebaseUser user ;
    DatabaseReference myDb;
    DatabaseReference usersListRef ;
    RecyclerView Rv ;
    RVAdapter adapter;
    ArrayList<String> usersList ;
    ArrayList<MessageProfile> Messages ;
    ArrayList<UserProfile> users ;
    String state = "default" ;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_contacts);

        logout = findViewById(R.id.logout);
        prof = findViewById(R.id.profile);
        NewMsg = findViewById(R.id.newmsg);
        Rv = findViewById(R.id.rv);

        Rv.setHasFixedSize(true);
        Rv.setLayoutManager(new LinearLayoutManager(MainActivity.this));

        user = FirebaseAuth.getInstance().getCurrentUser();

        usersList = new ArrayList<String>();
        Messages = new ArrayList<MessageProfile>();


        readUsers();
        logout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                FirebaseAuth.getInstance().signOut();
                Intent intent = new Intent(MainActivity.this , Login.class).setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                startActivity(intent);
                finish();
            }
        });

        NewMsg.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(MainActivity.this , AllUsers.class);
                startActivity(intent);
            }
        });

        prof.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(MainActivity.this , Profile.class);
                startActivity(intent);
            }
        });

        manageConnection();
        
    }


    private void readUsers()
    {
        usersListRef = FirebaseDatabase.getInstance().getReference("Chats");
        usersListRef.keepSynced(true);
        usersListRef.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {

                    usersList.clear();

                    for (DataSnapshot snapshot : dataSnapshot.getChildren())
                    {
                        MessageProfile messageProfile = snapshot.getValue(MessageProfile.class);

                        if((messageProfile.getSender().equals(user.getUid())) && (!usersList.contains(messageProfile.getReciever())))
                        {
                            usersList.add(messageProfile.getReciever());
                        }
                        if((messageProfile.getReciever().equals(user.getUid())) && (!usersList.contains(messageProfile.getSender())))
                        {
                            usersList.add(messageProfile.getSender());
                        }
                    }

                    readChat();
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });

    }

    private  void readChat()
    {
        users = new ArrayList<UserProfile>();

        myDb = FirebaseDatabase.getInstance().getReference("Users");
        myDb.keepSynced(true);
        myDb.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                users.clear();

                for (DataSnapshot snapshot : dataSnapshot.getChildren())
                {
                    UserProfile userProfile = snapshot.getValue(UserProfile.class);

                    for (String id : usersList)
                    {
                        if (userProfile.getId().equals(id))
                        {
                            if (users.size() != 0)
                            {
                                for (UserProfile userProfile1 : users)
                                {
                                    if (!userProfile.getId().equals(userProfile1.getId()))
                                    {
                                        users.add(userProfile);
                                        break;
                                    }

                                }
                            }
                            else
                            {
                                users.add(userProfile);
                                break;
                            }
                        }
                    }
                }

                adapter = new RVAdapter(users, MainActivity.this);
                Rv.setAdapter(adapter);
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });

    }


    private void State (String s)
    {
        DatabaseReference stateRef = FirebaseDatabase.getInstance().getReference("Users").child(user.getUid());
        HashMap<String , Object> map = new HashMap<>();
        map.put("state" , s);
        stateRef.updateChildren(map);

    }

    private void manageConnection()
    {
        final DatabaseReference connectionRef = FirebaseDatabase.getInstance().getReference("Users").child(user.getUid());
        DatabaseReference infoConnection = FirebaseDatabase.getInstance().getReference(".info/connected");

        infoConnection.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                boolean connected = dataSnapshot.getValue(Boolean.class);

                if (connected)
                {
                    HashMap<String , Object> mapOnline = new HashMap<>();
                    mapOnline.put("state" , "Online");
                    connectionRef.updateChildren(mapOnline);

                    HashMap<String , Object> mapOffline = new HashMap<>();
                    mapOffline.put("state" , "Offline");
                    connectionRef.onDisconnect().updateChildren(mapOffline);
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });
    }


    @Override
    protected void onPause() {

        super.onPause();
        /*
        state = "Offline";
        State(state);

         */
    }

    @Override
    protected void onResume() {
        super.onResume();
        /*
        state = "Online";
        State(state);

         */
    }

}
