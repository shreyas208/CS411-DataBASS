package com.shreyas208.databass;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Patterns;
import android.view.View;
import android.widget.EditText;
import android.widget.Toast;


public class RegisterActivity extends AppCompatActivity {

    private String testUser = "test@test.com";
    private String testPassword = "password";

    @Override
    protected void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register);

    }

    public void clickEnterRegisterButton(View view) {

        EditText emailRegisterTextView = (EditText) findViewById(R.id.emailRegisterTextView);
        EditText displayNameRegisterTextView = (EditText) findViewById(R.id.displayNameRegisterTextView);
        EditText password1RegisterEditText = (EditText) findViewById(R.id.password1RegisterEditText);
        EditText password2RegisterEditText = (EditText) findViewById(R.id.password2RegisterEditText);

        if (!verifyEmail(emailRegisterTextView.getText().toString())) {
            Toast.makeText(getApplicationContext(), "Invald email address", Toast.LENGTH_LONG).show();
            return;
        }

        if (!verifyPassword(password1RegisterEditText.getText().toString(), password2RegisterEditText.getText().toString())) {
            Toast.makeText(getApplicationContext(), "Passwords do not match", Toast.LENGTH_LONG).show();
            return;
        }

        Dummy.email = emailRegisterTextView.getText().toString();
        Dummy.displayName = displayNameRegisterTextView.getText().toString();
        Dummy.password = password1RegisterEditText.getText().toString();

        Intent i = new Intent(getApplicationContext(), ProfileActivity.class);
        startActivity(i);

    }

    public void clickCancelRegisterButton(View view) {

        Intent i = new Intent(getApplicationContext(), LoginActivity.class);
        startActivity(i);

    }

    private boolean verifyEmail(String email) {

        return Patterns.EMAIL_ADDRESS.matcher(email).matches();

    }

    private boolean verifyPassword(String password1, String password2) {

        return password1.equals(password2);

    }

}
