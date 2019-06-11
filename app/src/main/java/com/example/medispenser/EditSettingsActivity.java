package com.example.medispenser;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.EditText;


public class EditSettingsActivity extends AppCompatActivity {
    EditText eTxtName;
    EditText eTxtLastName;
    EditText eTxtGender;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        //getActionBar().show();
        setContentView(R.layout.activity_edit_settings);
        eTxtName = findViewById(R.id.editTextName);
        eTxtLastName = findViewById(R.id.editTextLastName);
        eTxtGender = findViewById(R.id.editTextGender);

        Bundle data = getIntent().getExtras();
        eTxtName.setText(data.getString("name"));
        eTxtLastName.setText(data.getString("lastname"));
        eTxtGender.setText(data.getString("gender"));
    }

    public void updateInfo(View view) {
        System.out.println("-------");
        System.out.println("-------");
        System.out.println("-------");
        System.out.println("UpdateInfo Executed");
        Intent replyIntent = new Intent();
        Bundle replyData = new Bundle();
        replyData.putString("nameEdited", eTxtName.getText().toString());
        replyData.putString("lastnameEdited", eTxtLastName.getText().toString());
        replyData.putString("genderEdited", eTxtGender.getText().toString());
        replyIntent.putExtras(replyData);
        setResult(RESULT_OK, replyIntent);
        finish();
    }

    public void returnToPrevious(View view) {
        setResult(RESULT_CANCELED);
        finish();
    }
}
