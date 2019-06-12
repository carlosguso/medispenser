package com.example.medispenser;

import android.app.DatePickerDialog;
import android.app.Dialog;
import android.content.Context;
import android.content.Intent;
import android.os.Build;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.ContextThemeWrapper;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.CalendarView;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.TimePicker;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.Timestamp;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FieldValue;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

public class AddMedActivity extends AppCompatActivity {
    TimePicker tp;
    Calendar calendar;
    Spinner spinnerMed;
    EditText eTxtCant, eTxtFrec;

    private int year, month, day, hour, minute, second = 0;
    ArrayList medNames = new ArrayList();
    String machineId, patientId;
    private static final String TAG = "AddMedActivity";
    private static final int ADD_MED = 3;

    FirebaseFirestore db = FirebaseFirestore.getInstance();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_med);

        tp = findViewById(R.id.timePicker);
        spinnerMed = findViewById(R.id.spinnerMed);
        eTxtCant = findViewById(R.id.eTxtMedCant);
        eTxtFrec = findViewById(R.id.eTxtMedFrec);

        calendar = Calendar.getInstance();
        year = calendar.get(Calendar.YEAR);
        month = calendar.get(Calendar.MONTH);
        day = calendar.get(Calendar.DAY_OF_MONTH);

        Intent data = getIntent();
        machineId = data.getStringExtra("machineId");
        patientId = data.getStringExtra("patientId");
        System.out.println(patientId);
        getMedsAvailable();
    }

    public void getMedsAvailable() {
        DocumentReference docRef = db.collection("machines").document(machineId);
        docRef.get().addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
            @Override
            public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                if (task.isSuccessful()) {
                    DocumentSnapshot document = task.getResult();
                    if (document.exists()) {
                        Log.d(TAG, "DocumentSnapshot data: " + document.getData());
                        Map<String, Object> machine = document.getData();
                        ArrayList meds = (ArrayList)machine.get("meds");
                        for (int i = 0; i < meds.size(); i++) {
                            Map<String, Object> medItem = ( Map<String, Object>)meds.get(i);
                            String slot = (String)medItem.get("slot");
                            if(!slot.equals("0")) {
                                String medName = (String)medItem.get("nombre");
                                medNames.add(medName);
                            }
                        }
                        setSpinner();
                    } else {
                        Log.d(TAG, "No such document");
                    }
                } else {
                    Log.d(TAG, "get failed with ", task.getException());
                }
            }
        });
    }

    public void setSpinner() {
        ArrayAdapter<String> medAdapter = new ArrayAdapter<String>(this,
                android.R.layout.simple_spinner_item, medNames);
        medAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spinnerMed.setAdapter(medAdapter);
        spinnerMed.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {

            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });
    }


    @SuppressWarnings("deprecation")
    public void setDate(View view) {
        showDialog(999);
    }


    @NonNull
    @Override
    public Dialog onCreateDialog(int id) {
        /* calendar code here */

        Context context = getApplicationContext();
        if (isBrokenSamsungDevice()) {
            context = new ContextThemeWrapper(this, android.R.style.Theme_Holo_Light_Dialog);
        }
        return new DatePickerDialog(context, myDateListener, year, month, day);
    }


    private static boolean isBrokenSamsungDevice() {
        return (Build.MANUFACTURER.equalsIgnoreCase("samsung")
                && isBetweenAndroidVersions(
                Build.VERSION_CODES.LOLLIPOP,
                Build.VERSION_CODES.LOLLIPOP_MR1));
    }


    private static boolean isBetweenAndroidVersions(int min, int max) {
        return Build.VERSION.SDK_INT >= min && Build.VERSION.SDK_INT <= max;
    }


    private DatePickerDialog.OnDateSetListener myDateListener = new
            DatePickerDialog.OnDateSetListener() {
                @Override
                public void onDateSet(DatePicker arg0,
                                      int arg1, int arg2, int arg3) {
                    // TODO Auto-generated method stub
                    // arg1 = year
                    // arg2 = month
                    // arg3 = day
                    showDate(arg1, arg2+1, arg3);
                    year = arg1;
                    month = arg2;
                    day = arg3;
                }
            };

    private void showDate(int year, int month, int day) {
        StringBuilder date = new StringBuilder().append(day).append("/")
                .append(month).append("/").append(year);
        System.out.println("Date: " + date);
    }

    public void constructMed(View view) {
        hour = tp.getCurrentHour();
        minute = tp.getCurrentMinute();

        Calendar newCalendar = Calendar.getInstance();
        newCalendar.set(year,month,day,hour,minute);

        /*Toast.makeText(this, (String)data.result,
                Toast.LENGTH_LONG).show();*/
        Timestamp timestamp = new Timestamp(newCalendar.getTime());
        String medication = spinnerMed.getSelectedItem().toString();
        String quantity = eTxtCant.getText().toString();
        String frequency = eTxtFrec.getText().toString();
        Map<String, Object> newMedication = new HashMap<>();
        newMedication.put("medicamento", medication);
        newMedication.put("frecuencia", frequency);
        newMedication.put("cantidad", quantity);
        newMedication.put("tomaInicial", timestamp);

        DocumentReference docRef = db.collection("patients").document(patientId);
        docRef.update("medicaciones", FieldValue.arrayUnion(newMedication));

        Intent reply = new Intent();
        setResult(RESULT_OK, reply);
        finish();
    }
}
