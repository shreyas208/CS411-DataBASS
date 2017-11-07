package com.shreyas208.databass.ui;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Patterns;
import android.view.View;
import android.widget.EditText;
import android.widget.Toast;

import com.shreyas208.databass.R;

/**
 * Register Activity class.
 */
public class RegisterActivity extends AppCompatActivity {

    /**
     * Creates the Register Activity instance.
     * @param savedInstanceState  previously saved instance
     */
    @Override
    protected void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register);

    }

    /**
     * Enter button callback function for registering a new user.
     * @param view view
     */
    public void clickEnterRegisterButton(View view) {

        // Get fields
        EditText emailRegisterTextView = (EditText) findViewById(R.id.emailRegisterTextView);
        EditText displayNameRegisterTextView = (EditText) findViewById(R.id.displayNameRegisterTextView);
        EditText password1RegisterEditText = (EditText) findViewById(R.id.password1RegisterEditText);
        EditText password2RegisterEditText = (EditText) findViewById(R.id.password2RegisterEditText);

        // Verify email
        if (!verifyEmail(emailRegisterTextView.getText().toString())) {
            Toast.makeText(getApplicationContext(), "Invald email address", Toast.LENGTH_LONG).show();
            return;
        }

        // Verify password
        if (!verifyPassword(password1RegisterEditText.getText().toString(), password2RegisterEditText.getText().toString())) {
            Toast.makeText(getApplicationContext(), "Passwords do not match", Toast.LENGTH_LONG).show();
            return;
        }

        // TODO
        // Call API to register the account

        // Set dummy account
        Dummy.email = emailRegisterTextView.getText().toString();
        Dummy.displayName = displayNameRegisterTextView.getText().toString();
        Dummy.password = password1RegisterEditText.getText().toString();

        Toast.makeText(getApplicationContext(), "Registered", Toast.LENGTH_LONG).show();

        // Open Profile Activity
        Intent i = new Intent(getApplicationContext(), ProfileActivity.class);
        startActivity(i);

    }

    /**
     * Cancel button callback function. This will take the user back to the
     * Login Activity.
     * @param view  view
     */
    public void clickCancelRegisterButton(View view) {

        Intent i = new Intent(getApplicationContext(), LoginActivity.class);
        startActivity(i);

    }

    /**
     * Verifies the passed in email address.
     * @param email  email address
     * @return       true if the email is valid
     */
    private boolean verifyEmail(String email) {

        return Patterns.EMAIL_ADDRESS.matcher(email).matches();

    }

    /**
     * Verifies that the password is valid.
     * @param password1  first password
     * @param password2  second password
     * @return           true if password is valid and they match
     */
    private boolean verifyPassword(String password1, String password2) {

        return password1.equals(password2);

    }

}
