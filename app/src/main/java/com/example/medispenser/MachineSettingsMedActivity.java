package com.example.medispenser;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.EditText;

import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.FieldValue;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.HashMap;
import java.util.Map;

public class MachineSettingsMedActivity extends AppCompatActivity {
    FirebaseFirestore db = FirebaseFirestore.getInstance();
    EditText eTxtName;
    EditText eTxtCon;
    String machineId;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_machine_settings_med);
        Intent dataIntent = getIntent();
        this.machineId = dataIntent.getStringExtra("machineId");
        eTxtName = findViewById(R.id.eTxtMachineSettingsNameMed);
        eTxtCon = findViewById(R.id.eTxtMachineSettingsConMed);
    }

    public void updateMeds(View view) {
        String name = eTxtName.getText().toString();
        String con = eTxtCon.getText().toString();
        Map<String, Object> item = new HashMap<>();
        item.put("nombre", name);
        item.put("concentracion", con);
        item.put("slot", "0");
        DocumentReference washingtonRef = db.collection("machines").document(this.machineId);
        washingtonRef.update("meds", FieldValue.arrayUnion(item));
        setResult(RESULT_OK);
        finish();
    }
}
