package com.example.tp_iwim.Activity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;

import com.bumptech.glide.Glide;
import com.example.tp_iwim.R;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.EventListener;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.FirebaseFirestoreException;
import com.google.firebase.storage.StorageReference;

import javax.annotation.Nullable;

import de.hdodenhof.circleimageview.CircleImageView;

public class Profile extends AppCompatActivity implements View.OnClickListener {
    ImageView editfname,editsname,editemail;
    TextView email,firstname,secondname,code;
    FirebaseAuth auth;
    FirebaseFirestore fstore;
    String userId;
    Button btn,edit;
    ImageView back;
    final long ONE_MEGABYTE = 1024*1024;
    CircleImageView image;
    StorageReference mImageRef;
    FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_profile);
        btn = (Button)findViewById(R.id.logout);
        edit = (Button)findViewById(R.id.editprofile);
        firstname = findViewById(R.id.fname);
        secondname = findViewById(R.id.sname);
        email = findViewById(R.id.email);
        code = findViewById(R.id.code);
        auth = FirebaseAuth.getInstance();
        image = findViewById(R.id.profile_image);
        fstore = FirebaseFirestore.getInstance();
        userId = auth.getCurrentUser().getUid();
        back = findViewById(R.id.ab_back);

        back.setOnClickListener(this);

        if(user!=null){
            if(user.getPhotoUrl()!=null){
                Glide.with(this).load(user.getPhotoUrl()).into(image);
            }
        }
        DocumentReference document = fstore.collection("users").document(userId);
        document.addSnapshotListener(this, new EventListener<DocumentSnapshot>() {
            @Override
            public void onEvent(@Nullable DocumentSnapshot documentSnapshot, @Nullable FirebaseFirestoreException e) {
                firstname.setText(documentSnapshot.getString("prenom"));
                secondname.setText(documentSnapshot.getString("nom"));
                email.setText(documentSnapshot.getString("email"));
                code.setText(documentSnapshot.getString("cine"));

            }
        });
        btn.setOnClickListener(this);
        edit.setOnClickListener(this);


    }
    @Override
    public void onClick(View view) {
        if(view.getId()==R.id.logout){
            FirebaseAuth.getInstance().signOut();
            startActivity(new Intent(this, MainActivity.class));
            finish();
        }
        if(view.getId()==R.id.editprofile){
            startActivity(new Intent(Profile.this, EditProfile.class));
            finish();
        }
        if(view.getId()==R.id.ab_back){
            startActivity(new Intent(Profile.this, Home.class));
            finish();
        }
    }

}
