package com.example.tp_iwim.Activity;


import static android.view.View.GONE;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import com.example.tp_iwim.Modal.User;
import com.example.tp_iwim.R;
import com.google.firebase.firestore.DocumentChange;
import com.google.firebase.firestore.EventListener;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.FirebaseFirestoreException;
import com.google.firebase.firestore.QuerySnapshot;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.Map;


public class ManageTeachers extends AppCompatActivity implements View.OnClickListener {

    private static final String TAG = "ManageTeachers";

    ImageView back;
    ListView list;
    String statut;
    static ArrayList<User> myList;

    FirebaseFirestore firestore ;

    @Override

    protected void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_manage_teachers);

        back = findViewById(R.id.ab_back);

        back.setOnClickListener(this);

        list = findViewById(R.id.usersList);
        list.setDivider(null);
        list.setDividerHeight(20);

        statut = "Professeur";

        myList = new ArrayList<>();

        firestore = FirebaseFirestore.getInstance();

        firestore.collection("users").whereIn("statut", Collections.singletonList(statut)).addSnapshotListener(new EventListener<QuerySnapshot>() {
            @Override
            public void onEvent(@Nullable QuerySnapshot queryDocumentSnapshots, @Nullable FirebaseFirestoreException e) {
                if(e != null){
                    Log.d("Snapshot","Error: "+e.getMessage());
                }
                for(DocumentChange doc: queryDocumentSnapshots.getDocumentChanges()){
                    if(doc.getType() == DocumentChange.Type.ADDED){
                        User user = new User();

                        String enabled = doc.getDocument().getString("enabled");

                        if(enabled.equals("false")){
                            user.setUid(doc.getDocument().getId());
                            user.setNom(doc.getDocument().getString("nom"));
                            user.setPrenom(doc.getDocument().getString("prenom"));
                            user.setEmail(doc.getDocument().getString("email"));

                            myList.add(user);
                        }
                    }
                }
                System.out.println("******************* List size:"+myList.size());

                myAdapter adapter = new myAdapter(getApplicationContext(), myList);
                list.setAdapter(adapter);
            }
        });
    }

    @Override
    public void onClick(View v) {
        if(v.getId() == R.id.ab_back){
            Intent intent = new Intent(this, Home.class);
            finish();
            startActivity(intent);
        }
    }

    class myAdapter extends ArrayAdapter<String>{
        Context context;
        ArrayList<User> usersList;

        myAdapter(Context context, ArrayList<User> list){
            super(context, R.layout.useritem);
            this.context=context;
            this.usersList=list;

            if(this.usersList.size()==0){
                Toast.makeText(getApplicationContext(),"Pas de demande d'adhésion",
                        Toast.LENGTH_SHORT).show();
            }
        }

        public int getCount() {
            return usersList.size();
        }


        public View getView (final int position, @Nullable View converView, @Nullable ViewGroup parent){

            LayoutInflater layoutInflater= (LayoutInflater)getApplicationContext().getSystemService(Context.LAYOUT_INFLATER_SERVICE);
            final View row = layoutInflater.inflate(R.layout.useritem, parent, false);

            ImageView photo = row.findViewById(R.id.userPicture);
            final TextView nom = row.findViewById(R.id.userName);
            TextView email = row.findViewById(R.id.userEmail);
            ImageView accept = row.findViewById(R.id.userAdd);
            ImageView delete = row.findViewById(R.id.userDelete);

            User user = this.usersList.get(position);

            final String uid = user.getUid();

            photo.setImageResource(R.drawable.defavatar);
            nom.setText(user.getNom()+" "+user.getPrenom());
            email.setText(user.getEmail());

            accept.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {

                    Map<String,Object> usr = new HashMap<>();
                    usr.put("enabled","true");
                    firestore.collection("users").document(uid).update(usr);
                    row.setVisibility(GONE);
                    finish();
                    startActivity(new Intent(getApplicationContext(), ManageTeachers.class));
                    Toast.makeText(getApplicationContext(),"Le compte de "+nom.getText()+" est validé",
                            Toast.LENGTH_SHORT).show();
                }
            });
            delete.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v){
                    firestore.collection("users").document(uid).delete();
                    row.setVisibility(GONE);
                    finish();
                    startActivity(new Intent(getApplicationContext(), ManageTeachers.class));
                    Toast.makeText(getApplicationContext(),"Le compte de "+nom.getText()+" est supprimé",
                            Toast.LENGTH_SHORT).show();
                }
            });
            return row;
        }
    }

}


