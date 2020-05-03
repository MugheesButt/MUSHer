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
import android.widget.EditText;
import android.widget.ImageButton;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.HashMap;

public class AllUsers extends AppCompatActivity {

    ImageButton Search_img, signout , profileDetails;
    EditText Search;
    RecyclerView rv ;
    FirebaseUser user ;
    DatabaseReference usersListRef ;
    UsersAdapter adapter;
    ArrayList<UserProfile> users = new ArrayList<>() ;
    String state = "";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_all_users);
        rv = findViewById(R.id.rvUsers);
        profileDetails = findViewById(R.id.profileUsers);
        signout = findViewById(R.id.logoutUsers);
        Search = findViewById(R.id.SearchText);
        Search_img = findViewById(R.id.Search);

        user = FirebaseAuth.getInstance().getCurrentUser();
        usersListRef = FirebaseDatabase.getInstance().getReference("Users");

        adapter = new UsersAdapter(users, this);
        RecyclerView.LayoutManager layoutManager = new LinearLayoutManager(AllUsers.this);
        rv.setLayoutManager(layoutManager);
        rv.setAdapter(adapter);
        readUsers();

        Search.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {
                searchUsers(s.toString().toLowerCase());
            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {

            }

            @Override
            public void afterTextChanged(Editable s) {

            }
        });

        signout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                FirebaseAuth.getInstance().signOut();
                Intent intent = new Intent(AllUsers.this , Login.class);
                startActivity(intent);
                finish();

            }
        });

        profileDetails.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(AllUsers.this , Profile.class);
                startActivity(intent);
            }
        });

    }



    private void searchUsers(String s) {

        final FirebaseUser firebaseUser = FirebaseAuth.getInstance().getCurrentUser();

        Query query = FirebaseDatabase.getInstance().getReference("Users").orderByChild("search")
                .startAt(s)
                .endAt(s + "\uf8ff");

        query.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {

                for (DataSnapshot snapshot : dataSnapshot.getChildren()) {
                    users.clear();
                    UserProfile userProfile = snapshot.getValue(UserProfile.class);

                    assert userProfile != null;
                    if (!userProfile.getId().equals(firebaseUser.getUid())) {
                        users.add(userProfile);
                    }
                }

                adapter = new UsersAdapter(users, AllUsers.this);
                rv.setAdapter(adapter);
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });
    }



    private void readUsers()
    {

        usersListRef.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {

                if (Search.getText().toString().equals("")) {

                    users.clear();
                    for (DataSnapshot snapshot : dataSnapshot.getChildren()) {
                        UserProfile userProfile = snapshot.getValue(UserProfile.class);
                        assert userProfile != null;
                        assert user != null;

                        if (!userProfile.getId().equals(user.getUid()))
                            users.add(userProfile);

                    }

                    adapter = new UsersAdapter(users , AllUsers.this);
                    rv.setAdapter(adapter);
                }

            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });

    }
}
