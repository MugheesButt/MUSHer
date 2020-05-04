package com.mugheesnadeem.i160029_i160068;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import de.hdodenhof.circleimageview.CircleImageView;

import android.Manifest;
import android.app.ProgressDialog;
import android.content.ContentResolver;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.provider.MediaStore;
import android.util.Log;
import android.view.View;
import android.webkit.MimeTypeMap;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.Continuation;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
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
import com.google.firebase.storage.StorageTask;
import com.google.firebase.storage.UploadTask;
import com.squareup.picasso.Picasso;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.util.HashMap;
import java.util.HashSet;
import java.util.UUID;

public class Profile extends AppCompatActivity {

    TextView username ;
    CircleImageView img ;
    Button save ;

    FirebaseStorage storage;
    FirebaseUser firebaseUser;
    DatabaseReference myDb ;
    StorageReference storageReference;

    private static final int IMAGE_REQUEST = 1000 ;
    private static final int PERMISSION_CODE = 1001;
    boolean delResult = false;
    private Uri ImageURI ;
    String state = "";
    int flag = 0 ;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_profile);

        img = findViewById(R.id.img);
        username = findViewById(R.id.username);
        save = findViewById(R.id.save);

        storage = FirebaseStorage.getInstance();
        storageReference = storage.getReference();

        firebaseUser = FirebaseAuth.getInstance().getCurrentUser();
        myDb = FirebaseDatabase.getInstance().getReference("Users").child(firebaseUser.getUid());


        ImageURI = Uri.parse("android.resource://com.mugheesnadeem.i160029_i160068/drawable/ic_person_black_256dp");

        myDb.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                UserProfile userProfile = dataSnapshot.getValue(UserProfile.class);
                username.setText(userProfile.getUsername());


                if (userProfile.getImageURL().equals("default"))
                    img.setImageURI(ImageURI);
                else {
                    Picasso.get().load(userProfile.getImageURL()).into(img);
                }

            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });

        img.setOnClickListener(new View.OnClickListener() {
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


        save.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                if (flag == 1) {

                    final StorageReference ur_firebase_reference = storageReference.child("images/" + firebaseUser.getUid());
                    Toast.makeText(Profile.this, "Updating Profile Picture", Toast.LENGTH_LONG).show();
                    Uri file = ImageURI;
                    Bitmap bmp = null;
                    try {
                        bmp = MediaStore.Images.Media.getBitmap(getContentResolver(), file);
                        ByteArrayOutputStream baos = new ByteArrayOutputStream();
                        bmp.compress(Bitmap.CompressFormat.JPEG, 10, baos);
                        byte[] data = baos.toByteArray();
                        UploadTask uploadTask = ur_firebase_reference.putBytes(data);

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
                                    System.out.println("Upload " + downloadUri);
                                    Toast.makeText(Profile.this, "Successfully uploaded", Toast.LENGTH_SHORT).show();

                                    if (downloadUri != null) {

                                        String photoStringLink = downloadUri.toString(); //YOU WILL GET THE DOWNLOAD URL HERE !!!!

                                        // Get a URL to the uploaded content
                                        //String g = taskSnapshot.getUploadSessionUri().toString();
                                        FirebaseDatabase database = FirebaseDatabase.getInstance();
                                        DatabaseReference myRef = database.getReference("Users").child(firebaseUser.getUid());
                                        HashMap<String, Object> map = new HashMap<>();
                                        map.put("imageURL", photoStringLink);
                                        myRef.updateChildren(map);
                                        //myRef.setValue(contact);
                                        System.out.println("Upload " + photoStringLink);
                                        finish();

                                    }

                                } else {
                                    Toast.makeText(Profile.this, "Failed", Toast.LENGTH_SHORT).show();
                                    // Handle failures
                                    // ...
                                }
                            }
                        });
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                } else {
                    finish();
                }
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
            img.setImageURI(ImageURI);
            flag = 1;
        }
    }

}

/*


    private String getFileExtension(Uri uri)
    {
        ContentResolver contentResolver = getApplicationContext().getContentResolver();
        MimeTypeMap mimeTypeMap = MimeTypeMap.getSingleton();
        return  mimeTypeMap.getExtensionFromMimeType(contentResolver.getType(uri));
    }


    private void uploadImage()
    {
        final ProgressDialog pd = new ProgressDialog(getApplicationContext());
        pd.setMessage("Uploading...");
        pd.show();

        if(ImageURI != null)
        {
            final StorageReference fileReference = storageReference.child(System.currentTimeMillis() + "." + getFileExtension(ImageURI));

            uploadTask = fileReference.putFile(ImageURI);
            uploadTask.continueWithTask(new Continuation<UploadTask.TaskSnapshot, Task<Uri>>() {


                @Override
                public Task<Uri> then(@NonNull Task<UploadTask.TaskSnapshot> task) throws Exception {
                    if (!task.isSuccessful())
                    {
                        throw task.getException();
                    }

                    return fileReference.getDownloadUrl();
                }
            }).addOnCompleteListener(new OnCompleteListener<Uri>() {
                @Override
                public void onComplete(@NonNull Task<Uri> task) {

                    if (task.isSuccessful())
                    {
                        Uri downloadURI = task.getResult();
                        String mUri = downloadURI.toString();

                        myDb = FirebaseDatabase.getInstance().getReference("Users").child(firebaseUser.getUid());
                        HashMap<String, Object> map = new HashMap<>();
                        map.put("ImageURI" , mUri);
                        myDb.updateChildren(map);

                        pd.dismiss();
                    }

                    else
                    {
                        Toast.makeText(Profile.this , "Failed" , Toast.LENGTH_SHORT).show();
                        pd.dismiss();
                    }

                }
            }).addOnFailureListener(new OnFailureListener() {
                @Override
                public void onFailure(@NonNull Exception e) {

                    Toast.makeText(Profile.this , e.getMessage() , Toast.LENGTH_SHORT).show();
                    pd.dismiss();
                }
            });
        }

        else
        {

            Toast.makeText(Profile.this , "No Image Selected" , Toast.LENGTH_SHORT).show();
        }
    }


    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (requestCode == IMAGE_REQUEST && requestCode == RESULT_OK
        && data != null && data.getData() != null)
        {
            ImageURI = data.getData();
            img.setImageURI(ImageURI);

            if (uploadTask != null && uploadTask.isInProgress())
            {

                Toast.makeText(Profile.this , "Upload in progress" , Toast.LENGTH_SHORT).show();
            }
            else
            {
                uploadImage();
            }
        }
    }
 */
