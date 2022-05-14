package com.example.tp_iwim.Activity;


import static android.view.View.VISIBLE;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import com.example.tp_iwim.R;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.EventListener;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.FirebaseFirestoreException;


public class Home extends AppCompatActivity  {

    private static final String TAG = "Home";

    FirebaseAuth auth;
    FirebaseUser user;
    FirebaseFirestore firebaseFirestore;
    String userId;

    ImageView profile, message;

    TextView non_valide;

    Button logout;

    ListView menu;

    String enabled = "false";

    @Override

    protected void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_home);

        auth = FirebaseAuth.getInstance();
        user = auth.getCurrentUser();
        userId = user.getUid();

        logout = findViewById(R.id.logout);

        firebaseFirestore = FirebaseFirestore.getInstance();

        profile = findViewById(R.id.ab_profile);
        message = findViewById(R.id.ab_message);

        non_valide = findViewById(R.id.pasvalide);

        menu = findViewById(R.id.homeMenu);
        menu.setDivider(null);
        menu.setDividerHeight(20);


        if (userId != null) {
//            System.out.println(user.getDisplayName()+"##############################");
            if(userId.equals("mOoMxvB7IGRei9akdu65Kn1oTIo2")  || userId.equals("3VRbWKKE5MhpYs4QkOsWpZj0U002")){

                profile.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        startActivity(new Intent(getApplicationContext(), Profile.class));
                        finish();
                    }
                });

                message.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        Toast.makeText(Home.this,"Message picked",
                                Toast.LENGTH_SHORT).show();
                    }
                });

                String titres[] = {"Gérer l'emploi du temps", "Gérer les matières", "Gérer les professeurs", "Gérer les étudiants"};
                int images[] = { R.drawable.timetable, R.drawable.subjects, R.drawable.teachers, R.drawable.students};

                myAdapter adapter = new myAdapter(this, titres, images);

                menu.setAdapter(adapter);
                menu.setOnItemClickListener(new AdapterView.OnItemClickListener(){
                    @Override
                    public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                        if(position == 0){
                            startActivity(new Intent(getApplicationContext(), ManageTimetable.class));
                        }else if(position == 1){
                            startActivity(new Intent(getApplicationContext(), ManageSubjects.class));
                        }else if(position == 2){
                            Intent intent = new Intent(getApplicationContext(), ManageTeachers.class);
                            startActivity(intent);
                        }else if(position == 3){
                            Intent intent = new Intent(getApplicationContext(), ManageStudents.class);
                            startActivity(intent);
                        }
                    }
                });
            }else{
                firebaseFirestore.collection("users").document(userId).addSnapshotListener(this, new EventListener<DocumentSnapshot>() {
                    @Override
                    public void onEvent(@javax.annotation.Nullable DocumentSnapshot documentSnapshot, @javax.annotation.Nullable FirebaseFirestoreException e) {
                        enabled = documentSnapshot.getString("enabled");

                        if(enabled.equals("false")){

                            menu.setVisibility(View.INVISIBLE);

                            non_valide.setVisibility(View.VISIBLE);

                            logout.setVisibility(VISIBLE);
                            logout.setOnClickListener(new View.OnClickListener() {
                                @Override
                                public void onClick(View v) {
                                    startActivity(new Intent(getApplicationContext(), MainActivity.class));
                                    finish();
                                }
                            });

                        }else {

                            profile.setOnClickListener(new View.OnClickListener() {
                                @Override
                                public void onClick(View v) {
                                    startActivity(new Intent(getApplicationContext(), Profile.class));
                                }
                            });

                            message.setOnClickListener(new View.OnClickListener() {
                                @Override
                                public void onClick(View v) {
                                    Toast.makeText(Home.this,"Message picked",
                                            Toast.LENGTH_SHORT).show();
                                }
                            });


                            if(user.getDisplayName().equals("Etudiant")){
                                String titres[] = {"Consulter un emploi du temps","Consulter les matières"};
                                int images[] = { R.drawable.timetable, R.drawable.subjects};

                                myAdapter adapter = new myAdapter(getApplicationContext(), titres, images);

                                menu.setAdapter(adapter);
                                menu.setOnItemClickListener(new AdapterView.OnItemClickListener(){
                                    @Override
                                    public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                                        if(position == 0){
                                            startActivity(new Intent(getApplicationContext(), DownloadTimetable.class));
                                        }else if(position == 1){
                                            //startActivity(new Intent(this, )); Not yet
                                        }
                                    }
                                });
                            }
                            else if(user.getDisplayName().equals("Professeur")){
                                String titres[] = {"Consulter l'emploi du temps", "Consulter mes matières"};
                                int images[] = { R.drawable.timetable, R.drawable.subjects};

                                myAdapter adapter = new myAdapter(getApplicationContext(), titres, images);

                                menu.setAdapter(adapter);
                                menu.setOnItemClickListener(new AdapterView.OnItemClickListener(){
                                    @Override
                                    public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                                        if(position == 0){

                                        }else if(position == 1){

                                        }
                                    }
                                });
                            }
                        }
                    }
                });
            }

        } else {
            startActivity(new Intent(this, MainActivity.class));
            finish();
        }


    }


    class myAdapter extends ArrayAdapter<String>{
        Context context;
        String titles[];
        int icons[];

        myAdapter(Context context, String titles[], int icons[]){
            super(context, R.layout.itemmenu, titles);
            this.context=context;
            this.titles=titles;
            this.icons=icons;
        }

        public View getView (int position,@Nullable View converView, @Nullable ViewGroup parent){
            LayoutInflater layoutInflater= (LayoutInflater)getApplicationContext().getSystemService(Context.LAYOUT_INFLATER_SERVICE);
            View row = layoutInflater.inflate(R.layout.itemmenu, parent, false);

            ImageView icon = row.findViewById(R.id.iconMenu);
            TextView title = row.findViewById(R.id.titleMenu);

            icon.setImageResource(this.icons[position]);
            title.setText(this.titles[position]);
            return row;
        }
    }

}


