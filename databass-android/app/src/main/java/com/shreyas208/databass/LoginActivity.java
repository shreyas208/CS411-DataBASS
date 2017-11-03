package com.shreyas208.databass;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.EditText;
import android.widget.Toast;


public class LoginActivity extends AppCompatActivity {

    private String testEmail = "test@test.com";
    private String testPassword = "password";
    private String testDisplayName = "Bob";

    @Override
    protected void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

    }

    public void clickEnterButton(View view) {

        EditText emailLoginEditText = (EditText) findViewById(R.id.emailLoginEditText);
        EditText passwordEditText = (EditText) findViewById(R.id.passwordLoginEditText);

        if (verifyLogin(emailLoginEditText.getText().toString(), passwordEditText.getText().toString())) {

            Dummy.email = emailLoginEditText.getText().toString();
            Dummy.password = passwordEditText.getText().toString();
            Dummy.displayName = testDisplayName;

            Intent i = new Intent(getApplicationContext(), ProfileActivity.class);
            startActivity(i);

        } else {

            Toast.makeText(getApplicationContext(), "Incorrect email or password", Toast.LENGTH_LONG).show();

        }

    }

    private boolean verifyLogin(String email, String password) {

        return email.equals(testEmail) && password.equals(testPassword);

    }

    public void clickRegisterButton(View view) {

        Intent i = new Intent(getApplicationContext(), RegisterActivity.class);
        startActivity(i);

    }

}
