package com.example.medispenser;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.TextView;

import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.FieldValue;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.Map;

public class MedActivity extends AppCompatActivity {
    Map<String, Object> medItem;
    String patientId;
    TextView txtName, txtCant, txtFrec;
    public static final int DELETE_MED = 4;
    FirebaseFirestore db = FirebaseFirestore.getInstance();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_med);
        Intent data = getIntent();
        this.patientId = data.getStringExtra("patientId");
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

        Intent reply = new Intent();
        setResult(RESULT_OK, reply);
        finish();
    }
}
