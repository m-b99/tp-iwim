package com.example.tp_iwim.Activity;

import android.Manifest;
import android.app.ProgressDialog;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

import com.example.tp_iwim.Modal.Emploi;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.example.tp_iwim.R;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.OnProgressListener;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;

import java.time.LocalDate;
import java.util.HashMap;
import java.util.Map;

@RequiresApi(api = Build.VERSION_CODES.O)
public class AddTimetable extends AppCompatActivity implements View.OnClickListener{

    Spinner annee, semestre, semaine, periode;
    Button upload, ajouter;
    TextView source;

    Emploi emploi;

    Uri pdf;

    ProgressDialog progress;

    FirebaseStorage storage; // for uploading file
    FirebaseDatabase database; // for saving data about that file

    FirebaseFirestore db = FirebaseFirestore.getInstance();

    LocalDate today;

    private static final String TAG = "ajTimetable";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_timetable);

        annee= findViewById(R.id.annee);
        semestre= findViewById(R.id.semestre);
        semaine= findViewById(R.id.semaine);
        periode= findViewById(R.id.periode);

        upload= findViewById(R.id.upload);
        ajouter= findViewById(R.id.ajouter);

        source= findViewById(R.id.filesrc);

        emploi= new Emploi();

        storage= FirebaseStorage.getInstance();
        database= FirebaseDatabase.getInstance();

        upload.setOnClickListener(this);
        ajouter.setOnClickListener(this);
    }

    @Override
    public void onClick(View v) {
        if(v.getId()==R.id.upload){
            if(ContextCompat.checkSelfPermission(this, Manifest.permission.READ_EXTERNAL_STORAGE)== PackageManager.PERMISSION_GRANTED){
                selectPdf();
            }else{
                ActivityCompat.requestPermissions(AddTimetable.this, new String[]{Manifest.permission.READ_EXTERNAL_STORAGE}, 9);

            }
        }else if(v.getId()==R.id.ajouter){
            if(pdf!=null){
                String an = annee.getSelectedItem().toString();
                String sem = semestre.getSelectedItem().toString();

                if(an.equals("Troisième année")){
                    emploi.setAnnee("A3");
                }else if(an.equals("Deuxième année")){
                    emploi.setAnnee("A2");
                }else if(an.equals("Première année")){
                    emploi.setAnnee("A1");
                }

                if(sem.equals("Premier semestre")){
                    emploi.setSemestre("S1");
                }else if(sem.equals("Deuxième semestre")){
                    emploi.setSemestre("S2");
                }

                emploi.setPeriode(periode.getSelectedItem().toString());
                emploi.setSemaine(semaine.getSelectedItem().toString());
                addEmploiInfo(emploi);
            }else{
                Toast.makeText(AddTimetable.this,"Select a file", Toast.LENGTH_SHORT).show();
            }
        }
    }

    public void addEmploiInfo(Emploi emploi){
        Map<String, Object> timetable = new HashMap<>();
        timetable.put("annee", emploi.getAnnee());
        timetable.put("semestre", emploi.getSemestre());
        timetable.put("periode", emploi.getPeriode());
        timetable.put("semaine",emploi.getSemaine());
        uploadPdf(pdf, emploi);

        db.collection("emploiInfo")
                .add(timetable)
                .addOnSuccessListener(new OnSuccessListener<DocumentReference>() {
                    @Override
                    public void onSuccess(DocumentReference documentReference) {
                        Log.d(TAG, "DocumentSnapshot added with ID: " + documentReference.getId());
                    }
                })
                .addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        Log.w(TAG, "Error adding document", e);
                    }
                });
    }

    public String generateFileName(Emploi emp){
        String name="";

        name+=emp.getAnnee()+emp.getSemestre()+emp.getPeriode()+"Semaine"+emp.getSemaine();
        return name;
    }

    public void uploadPdf(Uri pdf, final Emploi emp){

        progress= new ProgressDialog(this);
        progress.setProgressStyle(ProgressDialog.STYLE_HORIZONTAL);
        progress.setTitle("Uploading file..");
        progress.setProgress(0);
        progress.show();


        final String fileName= generateFileName(emp);
        final StorageReference storageRef=storage.getReference();

        storageRef.child("Emplois").child(fileName).putFile(pdf).addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
            @Override
            public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                String url=taskSnapshot.getMetadata().getReference().getDownloadUrl().toString();
                DatabaseReference reference=database.getReference();

                reference.child(fileName).setValue(url).addOnCompleteListener(new OnCompleteListener<Void>() {
                    @Override
                    public void onComplete(@NonNull Task<Void> task) {
                        if(task.isSuccessful()){
                            Toast.makeText(AddTimetable.this, "L'emploi du temps est ajouté", Toast.LENGTH_SHORT).show();
                            startActivity(new Intent(getApplicationContext(), ManageTimetable.class));
                        }else{
                            Toast.makeText(AddTimetable.this, "File not successfully uploaded", Toast.LENGTH_SHORT).show();
                        }
                    }
                });
            }
        }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {
                Toast.makeText(AddTimetable.this, "File not successfully uploaded", Toast.LENGTH_SHORT).show();
            }
        }).addOnProgressListener(new OnProgressListener<UploadTask.TaskSnapshot>() {
            @Override
            public void onProgress(@NonNull UploadTask.TaskSnapshot taskSnapshot) {
                int currentProgress= (int) (100*taskSnapshot.getBytesTransferred()/taskSnapshot.getTotalByteCount());
                progress.setProgress(currentProgress);
            }
        });
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        if(requestCode==9 && grantResults[0]==PackageManager.PERMISSION_GRANTED){
            selectPdf();
        }else{
            Toast.makeText(AddTimetable.this, "Please provide permission..", Toast.LENGTH_SHORT).show();
        }
    }

    public void selectPdf(){
        // offer user to select a file using file Manager
        Intent intent= new Intent();
        intent.setType("application/pdf");
        intent.setAction(Intent.ACTION_GET_CONTENT);
        startActivityForResult(intent, 86);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == 86 && resultCode == RESULT_OK && data != null) {
            pdf = data.getData();
            source.setText(data.getData().getLastPathSegment());
        } else {
            Toast.makeText(AddTimetable.this, "Please select a file", Toast.LENGTH_SHORT);
        }
    }
}
