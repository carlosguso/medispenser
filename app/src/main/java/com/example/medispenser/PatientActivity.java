package com.example.medispenser;

import android.content.Intent;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.View;
import android.widget.TextView;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.ArrayList;
import java.util.Map;

public class PatientActivity extends AppCompatActivity {
    String machineId;
    String patientId;
    FirebaseFirestore db = FirebaseFirestore.getInstance();
    private static final String TAG = "PatientActivity";

    //My attributes
    private RecyclerView mRecyclerView;
    private RecyclerView.Adapter mAdapter;
    private RecyclerView.LayoutManager mLayoutManager;
    ArrayList data;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_patient);
        Intent data = getIntent();
        machineId = data.getStringExtra("machineId");
        patientId = data.getStringExtra("patientId");
        mRecyclerView = findViewById(R.id.recycler_view_meds);
        getPatientData();
    }

    private void getPatientData() {
        DocumentReference docRef = db.collection("patients").document(patientId);
        docRef.get().addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
            @Override
            public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                if (task.isSuccessful()) {
                    DocumentSnapshot document = task.getResult();
                    if (document.exists()) {
                        Log.d(TAG, "DocumentSnapshot data: " + document.getData());
                        Map<String, Object> patient = document.getData();
                        updateUiWithPatientInfo(patient);
                    } else {
                        Log.d(TAG, "No such document");
                    }
                } else {
                    Log.d(TAG, "get failed with ", task.getException());
                }
            }
        });
    }

    public void updateUiWithPatientInfo(Map<String, Object> patient) {
        //Update UI
        TextView txtName = findViewById(R.id.txtPatientName);
        txtName.setText(patient.get("name").toString());

        TextView txtLastName = findViewById(R.id.txtPatientLastName);
        txtLastName.setText(patient.get("lastName").toString());

        TextView txtGender = findViewById(R.id.txtPatientGender);
        txtGender.setText(patient.get("sexo").toString());

        TextView txtAge = findViewById(R.id.txtPatientAge);
        txtAge.setText(patient.get("edad").toString());

        TextView txtSpec = findViewById(R.id.txtPatientSpec);
        txtSpec.setText(patient.get("condiciones").toString());

        //Get meds
        ArrayList meds = (ArrayList)patient.get("medicaciones");
        ArrayList medsName = new ArrayList();
        for(int i = 0; i <meds.size(); i++) {
            Map<String, Object> medItem = (Map<String, Object>)meds.get(i);
            String medName = medItem.get("medicamento").toString();
            medsName.add(medName);
        }
        mAdapter = new MedListAdapter(getApplicationContext(),medsName, meds, PatientActivity.this, MedActivity.class);
        //((PatientsListAdapter) mAdapter).setMachineId(machineId);
        mRecyclerView.setAdapter(mAdapter);
        // Give the RecyclerView a default layout manager.
        mRecyclerView.setLayoutManager(new LinearLayoutManager(getApplicationContext()));
    }

    public void addPatientMed(View view) {
    }
}
