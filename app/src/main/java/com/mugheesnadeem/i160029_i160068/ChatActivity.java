package com.mugheesnadeem.i160029_i160068;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.Manifest;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.Continuation;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.UUID;

public class ChatActivity extends AppCompatActivity {

    TextView name , status ;
    EditText msgBox ;
    ImageButton cam , send;
    String state = "";

    FirebaseUser firebaseUser;
    DatabaseReference dbRef ;
    FirebaseStorage storage;
    StorageReference storageReference;

    MessageAdapter messageAdapter;
    RecyclerView rv ;
    ImageButton back ;
    ArrayList<MessageProfile> Messages ;
    private static final int IMAGE_REQUEST = 1000 ;
    private static final int PERMISSION_CODE=1001;
    private Uri ImageURI ;
    int MESSAGE_FLAG = 0;
    String photoStringLink;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_chat);
        name = findViewById(R.id.RecName);
        msgBox = findViewById(R.id.msg);
        back = findViewById(R.id.Back);
        cam = findViewById(R.id.cam);
        send = findViewById(R.id.send);
        rv = findViewById(R.id.msgRv);

        rv.setHasFixedSize(true);
        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(getApplicationContext());
        linearLayoutManager.setStackFromEnd(true);
        rv.setLayoutManager(linearLayoutManager);


        storage = FirebaseStorage.getInstance();
        storageReference = storage.getReference();

        final String userID = getIntent().getStringExtra("userID");

        firebaseUser = FirebaseAuth.getInstance().getCurrentUser();
        dbRef = FirebaseDatabase.getInstance().getReference("Users").child(userID);

        dbRef.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                UserProfile up = dataSnapshot.getValue(UserProfile.class);
                name.setText(up.getUsername());

                readMessages(firebaseUser.getUid() , userID, up.getImageURL());
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });

        send.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String mesg = msgBox.getText().toString();

                if(!mesg.equals(""))
                {
                    sendMessage(firebaseUser.getUid() , userID , mesg);
                    msgBox.setText("");

                }

                else if (mesg.equals("") && MESSAGE_FLAG == 1)
                {
                    msgBox.setHint("Sending Image...");
                    sendMessage(firebaseUser.getUid() , userID , mesg);
                }
                else
                    Toast.makeText(ChatActivity.this , "Cannot send empty message" , Toast.LENGTH_SHORT).show();

            }
        });

        back.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(ChatActivity.this , MainActivity.class);
                startActivity(intent);
                finish();
            }
        });

        cam.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {


                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {

                    if (checkSelfPermission(Manifest.permission.READ_EXTERNAL_STORAGE)
                            == PackageManager.PERMISSION_DENIED) {

                        String[] permissions = {Manifest.permission.READ_EXTERNAL_STORAGE};
                        requestPermissions(permissions, PERMISSION_CODE);
                    }

                    else {

                        pickimagefromgallery();

                    }
                }

                else {

                    pickimagefromgallery();
                }

            }
        });
    }

    private void sendMessage(String sender , String reciever , String message)
    {
        DatabaseReference mDb = FirebaseDatabase.getInstance().getReference();
        MessageProfile msg = new MessageProfile() ;

        if (MESSAGE_FLAG == 1)
        {
            msg.setFlag(1);
            msg.setSender(sender);
            msg.setReciever(reciever);
            sendImage(msg);
        }
        else
        {
            msg = new MessageProfile(sender , reciever , message , MESSAGE_FLAG);
            mDb.child("Chats").push().setValue(msg);
        }
    }

    private String sendImage(final MessageProfile msg)
    {

        final DatabaseReference mDb = FirebaseDatabase.getInstance().getReference();
        Toast.makeText(ChatActivity.this, "Sending Picture", Toast.LENGTH_SHORT).show();
        String uniqueId = UUID.randomUUID().toString();
        final StorageReference ur_firebase_reference = storageReference.child("images/" + uniqueId);

        Uri file = ImageURI;
        UploadTask uploadTask = ur_firebase_reference.putFile(file);

        Task<Uri> urlTask = uploadTask.continueWithTask(new Continuation<UploadTask.TaskSnapshot, Task<Uri>>() {
            @Override
            public Task<Uri> then(@NonNull Task<UploadTask.TaskSnapshot> task) throws Exception {
                if (!task.isSuccessful()) {
                    throw task.getException();
                }


                // Continue with the task to get the download URL
                return ur_firebase_reference.getDownloadUrl();
            }
        }).addOnCompleteListener(new OnCompleteListener<Uri>() {
            @Override
            public void onComplete(@NonNull Task<Uri> task) {
                if (task.isSuccessful()) {
                    Uri downloadUri = task.getResult();
                    System.out.println("Send " + downloadUri);
                    Toast.makeText(ChatActivity.this, "Successfully sent", Toast.LENGTH_SHORT).show();
                    msgBox.setHint("Successfully sent");

                    if (downloadUri != null) {

                        photoStringLink = downloadUri.toString(); //YOU WILL GET THE DOWNLOAD URL HERE !!!!

                        // Get a URL to the uploaded content
                        //String g = taskSnapshot.getUploadSessionUri().toString();
                        System.out.println("Sent " + photoStringLink);
                        msgBox.setHint("");
                        msg.setMessage(photoStringLink);
                        mDb.child("Chats").push().setValue(msg);
                        MESSAGE_FLAG = 0 ;
                        msgBox.setEnabled(true);
                    }

                } else {
                    Toast.makeText(ChatActivity.this , "Failed" , Toast.LENGTH_SHORT).show();
                    // Handle failures
                    // ...
                }
            }
        });

        return photoStringLink;
    }

    private void readMessages(final String myID , final String userID , final String ImageURI)
    {
        Messages = new ArrayList<>();

        DatabaseReference mDb = FirebaseDatabase.getInstance().getReference("Chats");
        mDb.keepSynced(true);
        mDb.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                Messages.clear();
                for (DataSnapshot snapshot : dataSnapshot.getChildren())
                {
                    MessageProfile msg = snapshot.getValue(MessageProfile.class);
                    if ((msg.getReciever().equals(myID) && msg.getSender().equals(userID)) || (msg.getReciever().equals(userID) && msg.getSender().equals(myID)))
                    {
                        Messages.add(msg);
                    }

                    messageAdapter = new MessageAdapter(Messages , ChatActivity.this, ImageURI);
                    rv.setAdapter(messageAdapter);
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });
    }


    private void pickimagefromgallery() {
        Intent intent=new Intent(Intent.ACTION_PICK);
        intent.setType("image/*");
        startActivityForResult(intent,IMAGE_REQUEST);
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        switch (requestCode){
            case PERMISSION_CODE:{
                if (grantResults.length>0 && grantResults[0]== PackageManager.PERMISSION_GRANTED){
                    pickimagefromgallery();
                }
                else {
                    Toast.makeText(this, "Permissions denied...!", Toast.LENGTH_SHORT).show();
                }
            }

        }
    }


    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        if (resultCode==RESULT_OK && requestCode==IMAGE_REQUEST){
            ImageURI = data.getData();
            msgBox.setHint("Image Selected");
            msgBox.setEnabled(false);
            MESSAGE_FLAG = 1 ;
            //img.setImageURI(ImageURI);
        }
    }
}
