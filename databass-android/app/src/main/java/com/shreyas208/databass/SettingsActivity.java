package com.shreyas208.databass;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.EditText;
import android.widget.Toast;


/**
 * Settings Activity class.
 */
public class SettingsActivity extends AppCompatActivity {

    /**
     * Creates the Settings Activity instance.
     * @param savedInstanceState  previously saved instance
     */
    @Override
    protected void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_settings);

    }

    /**
     * Update button callback function. This will update the user's display name.
     * @param view  view
     */
    public void clickUpdateDisplayNameButton(View view) {

        EditText editDisplayNameEditText = (EditText) findViewById(R.id.editDisplayNameEditText);
        Dummy.displayName = editDisplayNameEditText.getText().toString();
        Toast.makeText(this, "Updated display name", Toast.LENGTH_LONG).show();

    }

    /**
     * Done button callback function. This will open the Profile Activity.
     * @param view  view
     */
    public void clickDoneButton(View view) {

        Intent i = new Intent(getApplicationContext(), ProfileActivity.class);
        startActivity(i);

    }

}
