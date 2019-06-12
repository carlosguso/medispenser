package com.example.medispenser;

import android.content.Intent;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.TextView;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.Timestamp;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FieldValue;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.ArrayList;
import java.util.Date;
import java.util.Map;

public class MedActivity extends AppCompatActivity {
    Map<String, Object> medItem;
    String patientId, machineId;
    TextView txtName, txtCant, txtFrec;
    public static final int DELETE_MED = 4;
    FirebaseFirestore db = FirebaseFirestore.getInstance();
    private static final String TAG = "MedActivity";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_med);
        Intent data = getIntent();
        this.patientId = data.getStringExtra("patientId");
        this.machineId = data.getStringExtra("machineId");
        this.medItem = (Map<String, Object>)data.getSerializableExtra("medItem");

        String nombre = medItem.get("medicamento").toString();
        String cantidad = medItem.get("cantidad").toString();
        String frecuencia = medItem.get("frecuencia").toString();

        txtName = findViewById(R.id.txtSingleMedName);
        txtCant = findViewById(R.id.txtSingleMedCant);
        txtFrec = findViewById(R.id.txtSingleMedFrec);

        txtName.setText(nombre);
        txtCant.setText(cantidad);
        txtFrec.setText(frecuencia);
    }

    public void deleteMed(View view) {
        DocumentReference docRef = db.collection("patients").document(patientId);
        docRef.update("medicaciones", FieldValue.arrayRemove(this.medItem));

        deleteInMachine();
    }

    public void deleteInMachine() {
        DocumentReference docRef = db.collection("machines").document(machineId);

        docRef.get().addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
            @Override
            public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                if (task.isSuccessful()) {
                    DocumentSnapshot document = task.getResult();
                    if (document.exists()) {
                        Log.d(TAG, "DocumentSnapshot data: " + document.getData());
                        Map<String, Object> machine = document.getData();
                        ArrayList lastMeds = (ArrayList)machine.get("lastMeds");
                        findMeds(lastMeds);
                    } else {
                        Log.d(TAG, "No such document");
                    }
                } else {
                    Log.d(TAG, "get failed with ", task.getException());
                }
            }

        });
    }

    private void findMeds(ArrayList lastMeds) {
        String nombre = medItem.get("medicamento").toString();
        for(int i = 0; i < lastMeds.size(); i++) {
            Map<String, Object> item = (Map<String, Object>)lastMeds.get(i);
            String patient_id = item.get("patientId").toString();
            String med_name = item.get("medName").toString();
            if(patient_id.equals(patientId) && med_name.equals(nombre)) {
                Timestamp current = new Timestamp(new Date());
                Timestamp itemStamp = (Timestamp)item.get("date");
                if(current.compareTo(itemStamp) < 0) {
                    DocumentReference docRef = db.collection("machines").document(machineId);
                    docRef.update("lastMeds", FieldValue.arrayRemove(item)).addOnSuccessListener(new OnSuccessListener<Void>() {
                        @Override
                        public void onSuccess(Void aVoid) {
                            Log.d(TAG, "DocumentSnapshot successfully updated!");
                        }
                    }).addOnFailureListener(new OnFailureListener() {
                                @Override
                                public void onFailure(@NonNull Exception e) {
                                    Log.w(TAG, "Error updating document", e);
                                }
                            });
                    System.out.println("Se encontro medicacion a borrar");
                } else {
                    System.out.println("Ya se registro esta medicacion");
                }
            }
        }

        Intent reply = new Intent();
        setResult(RESULT_OK, reply);
        finish();
    }
}
