package com.example.medispenser;

import android.content.Intent;
import android.graphics.Color;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.EditText;
import android.widget.TextView;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FieldValue;
import com.google.firebase.firestore.FirebaseFirestore;

public class AddMachineActivity extends AppCompatActivity {
    FirebaseUser currentUser = FirebaseAuth.getInstance().getCurrentUser();
    FirebaseFirestore db = FirebaseFirestore.getInstance();
    private static final String TAG = "AddMachineActivity";

    EditText eTxtCode;
    TextView txtAlert;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_machine);
        eTxtCode = findViewById(R.id.eTxtMachineCode);
        txtAlert = findViewById(R.id.txtAlert);
    }

    public void addMachine(View view) {
        final String code = eTxtCode.getText().toString();
        DocumentReference docRef = db.collection("machines").document(code);
        docRef.get().addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
            @Override
            public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                if (task.isSuccessful()) {
                    DocumentSnapshot document = task.getResult();
                    if (document.exists()) {
                        Log.d(TAG, "DocumentSnapshot data: " + document.getData());
                        linkMachine(code);
                    } else {
                        Log.d(TAG, "No such document");
                        txtAlert.setText("Error: Codigo Incorrecto");
                    }
                } else {
                    Log.d(TAG, "get failed with ", task.getException());
                }
            }
        });

    }

    public void linkMachine(String code) {
        DocumentReference userDocRef = db.collection("users").document(currentUser.getUid());
        userDocRef.update("medispenser", FieldValue.arrayUnion(code));

        DocumentReference machineDoc = db.collection("machines").document(code);
        machineDoc.update("users", FieldValue.arrayUnion(currentUser.getUid()));
        txtAlert.setTextColor(Color.parseColor("#3796BE"));
        txtAlert.setText("Maquina agregada");
        Intent replyIntent = new Intent();
        setResult(RESULT_OK, replyIntent);
        finish();
    }
}
