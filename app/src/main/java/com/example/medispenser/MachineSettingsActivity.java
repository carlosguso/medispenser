package com.example.medispenser;

import android.content.Context;
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


public class MachineSettingsActivity extends AppCompatActivity {

    private RecyclerView mRecyclerView;
    private RecyclerView.Adapter mAdapter;
    FirebaseFirestore db = FirebaseFirestore.getInstance();
    private static final String TAG = "MachineSettingsActivity";
    public static final int ADD_REQUEST = 1;
    public static final int EDIT_REQUEST = 3;

    TextView txtMachineName;
    TextView txtMachineSlots;
    TextView txtMachineTitle;
    String mId;
    int titleLen;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_machine_settings);
        Intent intent = getIntent();
        String machineId = intent.getStringExtra("machineId");
        this.mId = machineId;
        txtMachineName = findViewById(R.id.txtMachineSettingsName);
        txtMachineTitle = findViewById(R.id.txtMachineSettingsTitle);
        titleLen = txtMachineTitle.getText().length();
        txtMachineSlots = findViewById(R.id.txtMachineSettingsSlots);
        mRecyclerView = findViewById(R.id.recycler_view_word_meds);
        getData(machineId);
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        System.out.println("Machine Settings Activity destoyed");
    }

    @Override
    public void onBackPressed() {
        // your code.
        regresar();
    }

    public void regresar() {
        Intent replyIntent = new Intent();
        setResult(RESULT_CANCELED, replyIntent);
        finish();
    }

    public void getData(String machineId) {
        DocumentReference docRef = db.collection("machines").document(machineId);
        docRef.get().addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
            @Override
            public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                if (task.isSuccessful()) {
                    DocumentSnapshot document = task.getResult();
                    ArrayList meds;
                    ArrayList medsName = new ArrayList();
                    if (document.exists()) {

                        Map<String, Object> machine = document.getData();

                        String machineTitleText = txtMachineTitle.getText().toString() + (String)machine.get("name");

                        if(txtMachineTitle.getText().length() <= titleLen) {
                            txtMachineTitle.setText(machineTitleText);
                        }


                        txtMachineName.setText((String)machine.get("name"));
                        txtMachineSlots.setText((String)machine.get("slots"));
                        meds = (ArrayList)machine.get("meds");
                        for(int i = 0; i < meds.size(); i++) {
                            Map<String, Object> item = (Map<String, Object>)meds.get(i);
                            String name = (String)item.get("nombre");
                            String slot = (String)item.get("slot");
                            if(slot.equals("0")) {
                                medsName.add(name);
                            } else {
                                medsName.add((name + " - slot: " + slot));
                            }

                        }
                        String [] data = (String [])medsName.toArray(new String[medsName.size()]);

                        // Create an adapter and supply the data to be displayed.
                        mAdapter = new WordListAdapter(getApplicationContext(), data);
                        mRecyclerView.setAdapter(mAdapter);
                        // Give the RecyclerView a default layout manager.
                        mRecyclerView.setLayoutManager(new LinearLayoutManager(getApplicationContext()));
                        Log.d(TAG, "DocumentSnapshot data: " + document.getData());
                    } else {
                        Log.d(TAG, "No such document");
                    }
                } else {
                    Log.d(TAG, "get failed with ", task.getException());
                }
            }
        });
    }

    public void addMed(View view) {
        Intent intentMed = new Intent(MachineSettingsActivity.this, MachineSettingsMedActivity.class);
        intentMed.putExtra("machineId", this.mId);
        startActivityForResult(intentMed, ADD_REQUEST);
    }

    public void editMachine(View view) {
        Intent intentMed = new Intent(MachineSettingsActivity.this, MachineSettingsEditActivity.class);
        intentMed.putExtra("machineId", this.mId);
        startActivityForResult(intentMed, EDIT_REQUEST);
    }

    public void onActivityResult(int requestCode,
                                 int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if(requestCode == ADD_REQUEST || requestCode == EDIT_REQUEST) {
            if(resultCode == RESULT_OK) {
                getData(this.mId);
            }
        } else {
            setResult(requestCode);
            finish();
        }
    }
}
