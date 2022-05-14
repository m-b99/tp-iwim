package com.example.tp_iwim.Activity;

import android.content.Intent;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import com.bumptech.glide.Glide;
import com.example.tp_iwim.R;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.auth.UserProfileChangeRequest;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.EventListener;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.FirebaseFirestoreException;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;

import java.io.ByteArrayOutputStream;
import java.util.HashMap;
import java.util.Map;

import javax.annotation.Nullable;

import de.hdodenhof.circleimageview.CircleImageView;

public class EditProfile extends AppCompatActivity implements View.OnClickListener{
    EditText email,firstname,secondname;
    EditText email1,firstname1,secondname1;
    CircleImageView profile;
    String name1,name2,mail;
    FirebaseAuth auth;
    FirebaseFirestore fstore;
    int TAKE_IMAGE_CODE = 1001;
    String userId;
    private StorageReference Folder;
    FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
    Button btn,edit;
    ImageView back;
    private static final int ImageBack = 1;
    public static final String TAG = "TAG";
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_edit_profile);
        Folder = FirebaseStorage.getInstance().getReference().child("ImageFolder");
        firstname = findViewById(R.id.fname);
        secondname = findViewById(R.id.sname);
        profile = findViewById(R.id.profile_image);
        email = findViewById(R.id.email);
        auth = FirebaseAuth.getInstance();
        btn = findViewById(R.id.save);
        back = findViewById(R.id.ab_back);

        back.setOnClickListener(this);

        fstore = FirebaseFirestore.getInstance();
        userId = auth.getCurrentUser().getUid();
        if(user!=null){
            if(user.getPhotoUrl()!=null){
                Glide.with(this).load(user.getPhotoUrl()).into(profile);
            }
        }
        DocumentReference document = fstore.collection("users").document(userId);
        document.addSnapshotListener(this, new EventListener<DocumentSnapshot>() {
            @Override
            public void onEvent(@Nullable DocumentSnapshot documentSnapshot, @Nullable FirebaseFirestoreException e) {
                firstname.setText(documentSnapshot.getString("prenom"));
                secondname.setText(documentSnapshot.getString("nom"));
                email.setText(documentSnapshot.getString("email"));


            }
        });
        firstname1 = findViewById(R.id.fname);
        secondname1 = findViewById(R.id.sname);
        email1 = findViewById(R.id.email);

        btn.setOnClickListener(this);
    }

    @Override
    public void onClick(View v) {
        firstname1 = findViewById(R.id.fname);
        secondname1 = findViewById(R.id.sname);
        email1 = findViewById(R.id.email);
        name1 = firstname1.getText().toString().trim();
        name2 = secondname1.getText().toString().trim();
        mail = email1.getText().toString().trim();
        if(v.getId()==R.id.save){
            userId = auth.getCurrentUser().getUid();
            Map<String,Object> user = new HashMap<>();

            user.put("prenom",name1);
            user.put("nom",name2);
            user.put("email",mail);
            fstore.collection("users").document(userId).update(user).addOnSuccessListener(new OnSuccessListener<Void>() {
                @Override
                public void onSuccess(Void aVoid) {
                    Toast.makeText(EditProfile.this,"SUCCESS"+name1+name2,Toast.LENGTH_SHORT).show();
                    Intent intent = new Intent(EditProfile.this,Profile.class);
                    startActivity(intent);
                    finish();
                }
            }).addOnFailureListener(new OnFailureListener() {
                @Override
                public void onFailure(@NonNull Exception e) {
                    Toast.makeText(EditProfile.this,"Error:"+e.getMessage(),Toast.LENGTH_SHORT).show();
                }
            });
        }
        if(v.getId()==R.id.ab_back){
            startActivity(new Intent(EditProfile.this, Profile.class));
            finish();
        }
    }


    public void handleImageClick(View view) {
        Intent intent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
        if(intent.resolveActivity(getPackageManager())!=null){
            startActivityForResult(intent,TAKE_IMAGE_CODE);
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @androidx.annotation.Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if(requestCode == TAKE_IMAGE_CODE){
            switch(resultCode){
                case RESULT_OK:
                    Bitmap bitmap =(Bitmap) data.getExtras().get("data");
                    profile.setImageBitmap(bitmap);
                    handleUpload(bitmap);
            }
        }
    }

    private void handleUpload(Bitmap bitmap) {
        ByteArrayOutputStream boas = new ByteArrayOutputStream();
        bitmap.compress(Bitmap.CompressFormat.JPEG,100, boas);
        final StorageReference reference = FirebaseStorage.getInstance().getReference().
                child("profileImages").child(userId+".jpeg");
        reference.putBytes(boas.toByteArray()).addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
            @Override
            public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                getDownloadUrl(reference);
            }
        }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {
                Log.e(TAG, "OnFaimure",e.getCause());
            }
        });
    }

    private void getDownloadUrl(StorageReference reference) {
        reference.getDownloadUrl().addOnSuccessListener(new OnSuccessListener<Uri>() {
            @Override
            public void onSuccess(Uri uri) {

                Log.d(TAG,"success"+uri);
                setIUserProfileurl(uri);
            }
        });
    }

    private void setIUserProfileurl(Uri uri) {
        FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
        UserProfileChangeRequest request = new UserProfileChangeRequest.Builder().setPhotoUri(uri).build();
        user.updateProfile(request).addOnSuccessListener(new OnSuccessListener<Void>() {
            @Override
            public void onSuccess(Void aVoid) {
                Toast.makeText(EditProfile.this,"profile image success",Toast.LENGTH_SHORT);
            }
        }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {
                Toast.makeText(EditProfile.this,"profile image failed",Toast.LENGTH_SHORT);
            }
        });
    }

}
