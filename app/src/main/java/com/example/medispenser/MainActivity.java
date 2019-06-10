package com.example.medispenser;

import android.content.Intent;
import android.os.Bundle;
import android.support.design.widget.BottomNavigationView;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v7.app.AppCompatActivity;
import android.support.annotation.NonNull;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.widget.TextView;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.Timestamp;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.Query;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;

import java.sql.Time;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class MainActivity extends AppCompatActivity {
    private TextView mTextMessage;

    final Fragment fragment1 = new HomeFragment();
    final Fragment fragment2 = new PatientsFragment();
    final Fragment fragment3 = new MachinesFragment();
    final Fragment fragment4 = new SettingsFragment();
    final FragmentManager fm = getSupportFragmentManager();
    Fragment active = fragment1;

    FirebaseUser currentUser;
    FirebaseFirestore db = FirebaseFirestore.getInstance();
    private static final String TAG = "MainActivity";
    ArrayList lastMeds = new ArrayList();



    private BottomNavigationView.OnNavigationItemSelectedListener mOnNavigationItemSelectedListener
            = new BottomNavigationView.OnNavigationItemSelectedListener() {

        @Override
        public boolean onNavigationItemSelected(@NonNull MenuItem item) {
            switch (item.getItemId()) {
                case R.id.navigation_home:
                    fm.beginTransaction().hide(active).show(fragment1).commit();
                    active = fragment1;
                    //mTextMessage.setText(R.string.title_home);
                    return true;
                case R.id.navigation_dashboard:
                    fm.beginTransaction().hide(active).show(fragment2).commit();
                    active = fragment2;

                    //mTextMessage.setText(R.string.title_dashboard);
                    return true;
                case R.id.navigation_notifications:
                    fm.beginTransaction().hide(active).show(fragment3).commit();
                    active = fragment3;

                    //mTextMessage.setText(R.string.title_notifications);
                    return true;
                case R.id.navigation_settings:
                    System.out.println("I am on settings");
                    fm.beginTransaction().hide(active).show(fragment4).commit();
                    active = fragment4;

                    //mTextMessage.setText("Settings");
                    return true;
            }
            return false;
        }
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        currentUser = FirebaseAuth.getInstance().getCurrentUser();
        BottomNavigationView navView = findViewById(R.id.nav_view);


        navView.setOnNavigationItemSelectedListener(mOnNavigationItemSelectedListener);
        fm.beginTransaction().add(R.id.main_container, fragment4, "4").hide(fragment4).commit();
        fm.beginTransaction().add(R.id.main_container, fragment3, "3").hide(fragment3).commit();
        fm.beginTransaction().add(R.id.main_container, fragment2, "2").hide(fragment2).commit();
        fm.beginTransaction().add(R.id.main_container,fragment1, "1").commit();

        getUserData();

    }

    public void getUserData() {
        DocumentReference docRef = db.collection("users").document(currentUser.getUid());
        docRef.get().addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
            @Override
            public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                if (task.isSuccessful()) {
                    DocumentSnapshot document = task.getResult();
                    if (document.exists()) {
                        Log.d(TAG, "DocumentSnapshot data: " + document.getData());
                        Map<String, Object> user = document.getData();
                        String uid = (String)user.get("uid");
                        getListItems(uid);
                        Log.d(TAG, "uid: " + user.get("uid"));
                    } else {
                        Log.d(TAG, "No such document");
                    }
                } else {
                    System.out.println("User document failed");
                    Log.d(TAG, "get failed with ", task.getException());
                }
            }
        });
    }

    //Funncion que trae los datos de lastMeds
    public void getListItems(String uid) {
        System.out.println("GetListItems");
        CollectionReference machinesRef = db.collection("machines");
        Query query = machinesRef.whereArrayContains("users", uid);
        query.get().addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
            @Override
            public void onComplete(@NonNull Task<QuerySnapshot> task) {
                if(task.isSuccessful()) {
                    ArrayList meds = new ArrayList();
                    System.out.println("Query successful, items: " + task.getResult().size());
                    for (QueryDocumentSnapshot document: task.getResult()) {
                        Map<String, Object> machine = document.getData();
                        ArrayList lastmeds = (ArrayList) machine.get("lastMeds");
                        for(int i = 0; i < lastmeds.size(); i++) {
                            Map<String, Object> lastmedObject = (Map<String, Object>)lastmeds.get(i);
                            lastmedObject.put("machine", (String)machine.get("name"));
                            meds.add(lastmedObject);
                        }
                        System.out.println("Document data: " + document.getData());
                    }
                    setList(meds);
                }else {
                    System.out.println("Query failed");
                    Log.d(TAG, "get failed with ", task.getException());
                }
            }
        });

    }

    //Funcion que ordena los datos y los guarda en el atributo.
    public void setList(ArrayList meds) {
        System.out.println("SetListItems");
        ArrayList orderedMeds =  new ArrayList();
        System.out.println("Meds size: " + meds.size());
        while(!meds.isEmpty()) {
            int index = 0;
            Map<String, Object> dataMain = (Map<String, Object>)meds.get(index);
            Timestamp timeMain = (Timestamp)dataMain.get("date");
            for(int i = index + 1; i < meds.size(); i++) {
                Map<String, Object> data = (Map<String, Object>)meds.get(i);
                Timestamp t = (Timestamp)data.get("date");
                if(t.compareTo(timeMain) >= 0) {
                    dataMain = data;
                }
            }
            orderedMeds.add(dataMain);
            meds.remove(dataMain);
        }
        this.lastMeds = orderedMeds;
        for(Object item : this.lastMeds) {
            System.out.println(item);
        }
    }


    @Override
    protected  void onStart() {
        super.onStart();
    }

    public void signOut(View view) {
        FirebaseAuth.getInstance().signOut();
        Intent intent = new Intent(MainActivity.this, Login.class);
        startActivity(intent);
        finish();
    }

}



