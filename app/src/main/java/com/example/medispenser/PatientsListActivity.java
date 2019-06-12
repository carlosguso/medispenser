package com.example.medispenser;

import android.content.Intent;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.Query;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;

import java.util.ArrayList;
import java.util.Map;

public class PatientsListActivity extends AppCompatActivity {
    //My attributes
    private RecyclerView mRecyclerView;
    private RecyclerView.Adapter mAdapter;
    private RecyclerView.LayoutManager mLayoutManager;
    ArrayList data;
    FirebaseFirestore db = FirebaseFirestore.getInstance();
    FirebaseUser currentUser = FirebaseAuth.getInstance().getCurrentUser();
    private static final String TAG = "PatientsListActivity";
    private static final int ADD_PATIENT = 2;
    String machineId;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_patients_list);
        Intent intentData = getIntent();
        machineId = intentData.getStringExtra("machineId");
        /*TextView txtTitle = findViewById(R.id.txtPatientsListTitle);
        txtTitle.setText(txtTitle.getText().toString() + " " + machineId);*/
        data = new ArrayList();
        // Get a handle to the RecyclerView.
        mRecyclerView = findViewById(R.id.recycler_view_patients);
        //getListItems(currentUser.getUid());
        /*Button btnAddPatient = findViewById(R.id.btnPatientsListAdd);
        btnAddPatient.setOnClickListener(this);*/
        System.out.println("Patient list: machine id : " + machineId);
        getListItems(machineId);
    }

    //Funncion que trae los nombres de las maquinas
    public void getListItems(String uid) {
        final DocumentReference docRef = db.collection("machines").document(uid);
        docRef.get().addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
            @Override
            public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                if (task.isSuccessful()) {
                    DocumentSnapshot document = task.getResult();
                    if (document.exists()) {
                        Map<String, Object> machine =  document.getData();
                        String machineName = (String)machine.get("name");
                        TextView txtTitle = findViewById(R.id.txtPatientsListTitle);
                        txtTitle.setText(txtTitle.getText().toString() + " " + machineName);
                    } else {
                        Log.d(TAG, "No such document");
                    }
                } else {
                    Log.d(TAG, "get failed with ", task.getException());
                }
            }
        });
        CollectionReference machinesRef = db.collection("patients");
        Query query = machinesRef.whereArrayContains("machines", uid);
        query.get().addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
            @Override
            public void onComplete(@NonNull Task<QuerySnapshot> task) {
                if(task.isSuccessful()) {
                    ArrayList names = new ArrayList();
                    ArrayList ids = new ArrayList();
                    System.out.println("Query successful, items: " + task.getResult().size());
                    for (QueryDocumentSnapshot document: task.getResult()) {
                        Map<String, Object> patient = document.getData();
                        String name = (String)patient.get("name");
                        String lastName = (String)patient.get("lastName");
                        String displayName = name + " " + lastName;
                        names.add(displayName);
                        ids.add((String)patient.get("id"));
                    }
                    data = names;
                    // Create an adapter and supply the data to be displayed.
                    mAdapter = new PatientsListAdapter(getApplicationContext(),data, ids, PatientsListActivity.this, MachineSettingsActivity.class);
                    mRecyclerView.setAdapter(mAdapter);
                    // Give the RecyclerView a default layout manager.
                    mRecyclerView.setLayoutManager(new LinearLayoutManager(getApplicationContext()));
                }else {
                    System.out.println("Query failed");
                    Log.d(TAG, "get failed with ", task.getException());
                }
            }
        });
    }

    public void onActivityResult(int requestCode,
                                 int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if(requestCode == ADD_PATIENT) {
            if(resultCode == RESULT_OK) {
                getListItems(machineId);
            }
        }
    }

    public void addPatient(View view) {
        Intent intent = new Intent(this, AddPatientActivity.class);
        intent.putExtra("machineId", machineId);
        startActivityForResult(intent, ADD_PATIENT);
    }
}
