package com.shreyas208.databass;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.EditText;
import android.widget.Toast;

public class SettingsActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_settings);

    }

    public void clickUpdateDisplayNameButton(View view) {

        EditText editDisplayNameEditText = (EditText) findViewById(R.id.editDisplayNameEditText);

        Dummy.displayName = editDisplayNameEditText.getText().toString();

        Toast.makeText(this, "Updated display name", Toast.LENGTH_LONG).show();

    }

    public void clickDoneButton(View view) {

        Intent i = new Intent(getApplicationContext(), ProfileActivity.class);
        startActivity(i);

    }

}
