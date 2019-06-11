package com.example.medispenser;

import android.content.Intent;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.Spinner;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

public class MachineSettingsEditActivity extends AppCompatActivity implements AdapterView.OnItemSelectedListener {

    Spinner spinnerMed;
    Spinner spinnerSlot;
    EditText eTxtMachineName;
    String mId;
    String machineName;
    ArrayList medList;
    ArrayList slotNumsList;
    FirebaseFirestore db = FirebaseFirestore.getInstance();
    private static final String TAG = "MachineSettEditActivity";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_machine_settings_edit);
        Intent data = getIntent();
        this.mId = data.getStringExtra("machineId");
        eTxtMachineName = findViewById(R.id.eTxtMachineSettingsMachName);
        spinnerMed = findViewById(R.id.spinnerMachineSettingsMed);
        spinnerSlot = findViewById(R.id.spinnerMachineSettingsSlot);

        getData(this.mId);
    }

    @Override
    public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {

    }

    @Override
    public void onNothingSelected(AdapterView<?> parent) {

    }

    public void prepareMedsData() {
        String medName = spinnerMed.getSelectedItem().toString();
        String slotNum = spinnerSlot.getSelectedItem().toString();


        for(int i = 0; i < medList.size(); i++) {
            Map<String, Object> medItem = (Map<String, Object>)medList.get(i);

            String medNameFB = (String)medItem.get("nombre");
            String medSlotFB = (String)medItem.get("slot");
            String conFB = (String)medItem.get("concentracion");

            if(medName.equals(medNameFB)) {
                Map<String, Object> newData = new HashMap<>();
                newData.put("nombre", medName);
                newData.put("slot", slotNum);
                newData.put("concentracion", conFB);
                medList.set(i, newData);
            }

            if(slotNum.equals(medSlotFB) && !medName.equals(medNameFB)) {
                Map<String, Object> newData = new HashMap<>();
                newData.put("nombre", medNameFB);
                newData.put("slot", "0");
                newData.put("concentracion", conFB);
                medList.set(i, newData);
            }

        }

    }

    public void updateMachineInfo(View view) {
        System.out.println(spinnerMed.getSelectedItem().toString());
        System.out.println(spinnerSlot.getSelectedItem().toString());

        prepareMedsData();

        DocumentReference machineRef = db.collection("machines").document(mId);


        machineRef.update("name", eTxtMachineName.getText().toString(), "meds", medList)
                .addOnSuccessListener(new OnSuccessListener<Void>() {
                    @Override
                    public void onSuccess(Void aVoid) {
                        Log.d(TAG, "DocumentSnapshot successfully updated!");
                        Intent replyIntent = new Intent();
                        setResult(RESULT_OK, replyIntent);
                        finish();
                    }
                })
                .addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        Log.w(TAG, "Error updating document", e);
                    }
                });

        /*for(int i = 0; i < medList.size(); i++) {
            System.out.println(medList.get(i));
        }*/
    }

    public void setSpinners(ArrayList medsList, ArrayList slotsList) {
        ArrayAdapter<String> medAdapter = new ArrayAdapter<String>(this,
                android.R.layout.simple_spinner_item, medsList);
        medAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spinnerMed.setAdapter(medAdapter);
        spinnerMed.setOnItemSelectedListener(this);

        ArrayAdapter<String> slotAdapter = new ArrayAdapter<String>(this,
                android.R.layout.simple_spinner_item, slotsList);
        slotAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spinnerSlot.setAdapter(slotAdapter);
        spinnerSlot.setOnItemSelectedListener(this);
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
                    ArrayList slotList = new ArrayList();;
                    if (document.exists()) {

                        Map<String, Object> machine = document.getData();

                        machineName = (String)machine.get("name");
                        eTxtMachineName.setText(machineName);
                        Integer slots = Integer.valueOf(machine.get("slots").toString());
                        meds = (ArrayList)machine.get("meds");

                        for(int i = 0; i < slots; i++) {
                            slotList.add(String.valueOf((i+1)));
                        }

                        for(int i = 0; i < meds.size(); i++) {
                            Map<String, Object> item = (Map<String, Object>)meds.get(i);
                            String name = (String)item.get("nombre");
                            medsName.add(name);
                        }
                        //String [] data = (String [])medsName.toArray(new String[medsName.size()]);
                        medList = meds;
                        slotNumsList = slotList;
                        setSpinners(medsName, slotList);


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
}
