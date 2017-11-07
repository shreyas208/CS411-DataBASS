package com.shreyas208.databass.ui;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.EditText;
import android.widget.Toast;

import com.shreyas208.databass.R;
import com.shreyas208.databass.api.service.TravelationsAPI;

import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;


/**
 * Login Activity class.
 */
public class LoginActivity extends AppCompatActivity {

    /**
     * Dummy user for verification. Delete after login is actually implemented.
     */
    private String testEmail = "test@test.com";
    private String testPassword = "password";
    private String testDisplayName = "Bob";

    /**
     * Creates the Login Activity instance.
     * @param savedInstanceState  previously saved instance
     */
    @Override
    protected void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        // Initialize API
        if (Global.api == null) {

            Retrofit.Builder builder = new Retrofit.Builder()
                    .baseUrl("http://fa17-cs411-18.cs.illinois.edu/")
                    .addConverterFactory(GsonConverterFactory.create());

            Retrofit retrofit = builder.build();

            Global.api  = retrofit.create(TravelationsAPI.class);

        }

    }

    /**
     * Enter button for sending login information. Currently using the dummy account to test
     * the verification.
     * @param view  view
     */
    public void clickEnterButton(View view) {

        // Get fields
        EditText emailLoginEditText = (EditText) findViewById(R.id.emailLoginEditText);
        EditText passwordEditText = (EditText) findViewById(R.id.passwordLoginEditText);

        // Verify login
        if (verifyLogin(emailLoginEditText.getText().toString(), passwordEditText.getText().toString())) {

            // TODO
            // Maybe we should save app data such as access token in Global?

            Dummy.email = emailLoginEditText.getText().toString();
            Dummy.password = passwordEditText.getText().toString();
            Dummy.displayName = testDisplayName;

            Toast.makeText(getApplicationContext(), "Logged in", Toast.LENGTH_LONG).show();

            Intent i = new Intent(getApplicationContext(), ProfileActivity.class);
            startActivity(i);

        } else {

            // Incorrect login
            Toast.makeText(getApplicationContext(), "Incorrect email or password", Toast.LENGTH_LONG).show();

        }

    }

    /**
     * Helper method for verifying login.
     * @param email     user email
     * @param password  user password
     * @return          true on success
     */
    private boolean verifyLogin(String email, String password) {

        // TODO
        // Call API to verify login

        return email.equals(testEmail) && password.equals(testPassword);

    }

    /**
     * Register button callback function. Will open the Register Activity.
     * @param view  view
     */
    public void clickRegisterButton(View view) {

        Intent i = new Intent(getApplicationContext(), RegisterActivity.class);
        startActivity(i);

    }

}
