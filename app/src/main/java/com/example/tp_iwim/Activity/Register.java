package com.example.tp_iwim.Activity;

import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.RadioGroup;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import com.example.tp_iwim.Modal.User;
import com.example.tp_iwim.R;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.auth.UserProfileChangeRequest;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.HashMap;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class Register extends AppCompatActivity implements View.OnClickListener{

    FirebaseAuth mAuth;
    FirebaseUser mUser;
    FirebaseFirestore db ;

    FirebaseFirestore restore;
    String userID;

    Button signup;
    TextView compte_ex;
    EditText nom, prenom, cine, telephone, email, password;
    Spinner annee;
    RadioGroup radioGroup;
    ProgressBar prog;

    User usr;

    private static final String TAG = "Register";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register);

        db = FirebaseFirestore.getInstance();
        mAuth = FirebaseAuth.getInstance();
        restore = FirebaseFirestore.getInstance();

        usr = new User();

        nom=findViewById(R.id.nom);
        prenom=findViewById(R.id.prenom);
        cine=findViewById(R.id.cine);
        email=findViewById(R.id.email);
        password=findViewById(R.id.password);
        telephone=findViewById(R.id.telephone);
        annee=findViewById(R.id.anneeSp);

        radioGroup=findViewById(R.id.radioGrp);

        prog = findViewById(R.id.prog_bar);
        signup = findViewById(R.id.UpBtt);
        compte_ex = findViewById(R.id.compte_ex);

        signup.setOnClickListener(this);
        compte_ex.setOnClickListener(this);

    }

    public boolean isEmailValid(String email)
    {
        String regExpn =
                "^(([\\w-]+\\.)+[\\w-]+|([a-zA-Z]{1}|[\\w-]{2,}))@"
                        +"((([0-1]?[0-9]{1,2}|25[0-5]|2[0-4][0-9])\\.([0-1]?"
                        +"[0-9]{1,2}|25[0-5]|2[0-4][0-9])\\."
                        +"([0-1]?[0-9]{1,2}|25[0-5]|2[0-4][0-9])\\.([0-1]?"
                        +"[0-9]{1,2}|25[0-5]|2[0-4][0-9])){1}|"
                        +"([a-zA-Z]+[\\w-]+\\.)+[a-zA-Z]{2,4})$";

        CharSequence inputStr = email;

        Pattern pattern = Pattern.compile(regExpn,Pattern.CASE_INSENSITIVE);
        Matcher matcher = pattern.matcher(inputStr);

        if(matcher.matches())
            return true;
        else
            return false;
    }

    public void onRadioButtonClicked(View view) {
        switch(view.getId()) {
            case R.id.etd:
                cine.setHint("CNE");
                annee.setVisibility(View.VISIBLE);
                break;
            case R.id.prof:
                cine.setHint("CIN");
                annee.setVisibility(View.GONE);
                break;
        }
    }

    @Override
    public void onClick(View v) {
        if(v.getId() == R.id.compte_ex){
            startActivity(new Intent(this, MainActivity.class));
        }
        if (v.getId() == R.id.UpBtt) {
            String _nom = nom.getText().toString();
            String _prenom = prenom.getText().toString();
            String _cine = cine.getText().toString();
            String _email = email.getText().toString();
            String _password = password.getText().toString();
            String _telephone = telephone.getText().toString();
            if (TextUtils.isEmpty(_nom)) {
                nom.setError("Saisir le nom !");
            } else if (TextUtils.isEmpty(_prenom)) {
                prenom.setError("Saisir le prenom !");
            } else if (TextUtils.isEmpty(_cine)) {
                cine.setError("Saisir le CIN/CNE !");
            } else if (TextUtils.isEmpty(_telephone)) {
                telephone.setError("Saisir le numéro de téléphone !");
            } else if (TextUtils.isEmpty(_email)) {
                email.setError("Saisir l'e-mail !");
            } else if (TextUtils.isEmpty(_password)) {
                password.setError("Saisir le mot de passe !");
            } else if (_telephone.length() != 10 || (!_telephone.substring(0, 2).equals("06") && !_telephone.substring(0, 2).equals("07"))) {
                telephone.setError("Le numéro de téléphone est invalide !");
            } else if (!isEmailValid(_email)) {
                email.setError("L'email est invalide");
            } else if (_password.length() < 6) {
                password.setError("Le mot de passe doit contient au moins 6 caractères!");
            } else {
                usr.setNom(_nom);
                usr.setPrenom(_prenom);
                usr.setCine(_cine);
                usr.setEmail(_email);
                usr.setPassword(_password);
                usr.setTelephone(_telephone);

                int selectedId = radioGroup.getCheckedRadioButtonId();
                if (selectedId == R.id.etd) {
                    usr.setStatut("Etudiant");
                    usr.setAnnee(annee.getSelectedItem().toString());
                } else if (selectedId == R.id.prof) {
                    usr.setStatut("Professeur");
                }
                signUp(usr);
            }
        }
    }


    void signUp(final User usr){

        prog.setVisibility(View.VISIBLE);
        mAuth.createUserWithEmailAndPassword(usr.getEmail(),usr.getPassword()).addOnCompleteListener(new OnCompleteListener<AuthResult>() {
            @Override
            public void onComplete(@NonNull Task<AuthResult> task) {
                if(task.isSuccessful()){
                    Toast.makeText(Register.this,"Votre compte a été créé avec succès",Toast.LENGTH_LONG).show();

                    FirebaseUser currentUser = mAuth.getCurrentUser();
                    userID = currentUser.getUid();
                    DocumentReference document = restore.collection("users").document(userID);
                    Map<String,Object> user = new HashMap<>();
                    user.put("nom",usr.getNom());
                    user.put("prenom",usr.getPrenom());
                    user.put("telephone",usr.getTelephone());
                    user.put("cine",usr.getCine());
                    user.put("statut",usr.getStatut());

                    UserProfileChangeRequest profileUpdates = new UserProfileChangeRequest.Builder()
                            .setDisplayName(usr.getStatut())
                            .build();

                    currentUser.updateProfile(profileUpdates)
                            .addOnCompleteListener(new OnCompleteListener<Void>() {
                                @Override
                                public void onComplete(@NonNull Task<Void> task) {
                                    if (task.isSuccessful()) {
                                        Log.d(TAG, "User profile updated.");
                                    }
                                }
                            });
                    if(usr.getStatut().equals("Etudiant")){
                        user.put("annee",usr.getAnnee());
                    }

                    user.put("email",usr.getEmail());
                    user.put("password",usr.getPassword());
                    //
                    user.put("enabled","false");
                    document.set(user).addOnSuccessListener(new OnSuccessListener<Void>() {
                        @Override
                        public void onSuccess(Void aVoid) {
                            Log.d(TAG,"success"+userID);
                            Intent intent = new Intent(getApplicationContext(), Home.class);
                            startActivity(intent);
                            finish();
                        }
                    });
                }else{
                    prog.setVisibility(View.INVISIBLE);
                    Toast.makeText(Register.this,"L'erreur :"+task.getException().getMessage(),Toast.LENGTH_SHORT).show();
                }
            }
        });

    }
}