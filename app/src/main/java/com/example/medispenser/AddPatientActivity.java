package com.example.medispenser;

import android.content.Intent;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.EditText;

import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;

public class AddPatientActivity extends AppCompatActivity {
    private String machineId;
    FirebaseFirestore db = FirebaseFirestore.getInstance();
    private static final String TAG = "AddPatientActivity";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_patient);
        Intent data = getIntent();
        this.machineId = data.getStringExtra("machineId");
    }

    public void addPatientFB(View view) {
        EditText name = findViewById(R.id.eTxtAddPatientName);
        EditText lastname = findViewById(R.id.eTxtAddPatientLastName);
        EditText gender = findViewById(R.id.eTxtAddPatientGender);
        EditText age = findViewById(R.id.eTxtAddPatientAge);
        EditText spec = findViewById(R.id.eTxtAddPatientSpec);

        final Map<String,Object> newPatient = new HashMap<>();
        newPatient.put("name", name.getText().toString());
        newPatient.put("lastName", lastname.getText().toString());
        newPatient.put("sexo", gender.getText().toString());
        newPatient.put("edad", age.getText().toString());
        newPatient.put("condiciones", spec.getText().toString());
        newPatient.put("machines", Arrays.asList(machineId));
        newPatient.put("medicaciones", Arrays.asList());

        db.collection("cities")
                .add(newPatient)
                .addOnSuccessListener(new OnSuccessListener<DocumentReference>() {
                    @Override
                    public void onSuccess(DocumentReference documentReference) {
                        Log.d(TAG, "DocumentSnapshot written with ID: " + documentReference.getId());
                        newPatient.put("id", documentReference.getId());
                        //db.collection("patients").document(documentReference.getId()).set(newPatient);
                        reUploadPatient(newPatient, documentReference.getId());

                    }
                })
                .addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        Log.w(TAG, "Error adding document", e);
                    }
                });
    }

    public void reUploadPatient(Map<String, Object> patient, String id) {
        db.collection("patients").document(id)
                .set(patient)
                .addOnSuccessListener(new OnSuccessListener<Void>() {
                    @Override
                    public void onSuccess(Void aVoid) {
                        Log.d(TAG, "DocumentSnapshot successfully written!");
                        Intent reply = new Intent();
                        setResult(RESULT_OK, reply);
                        finish();
                    }
                })
                .addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        Log.w(TAG, "Error writing document", e);
                    }
                });
    }
}
