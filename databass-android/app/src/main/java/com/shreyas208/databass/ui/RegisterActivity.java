package com.shreyas208.databass.ui;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.util.Patterns;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.shreyas208.databass.R;
import com.shreyas208.databass.TravelationsApp;
import com.shreyas208.databass.api.model.Error;
import com.shreyas208.databass.api.model.GenericResponse;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;

/**
 * Register Activity class.
 */
public class RegisterActivity extends AppCompatActivity implements View.OnClickListener, Callback<GenericResponse> {

    private EditText etUsername;
    private EditText etPassword;
    private EditText etConfirmPassword;
    private EditText etEmailAddress;
    private EditText etDisplayName;
    private LinearLayout llRegister;
    private Button btnRegister;
    private TextView tvLogin;

    /**
     * Creates the Register Activity instance.
     * @param savedInstanceState  previously saved instance
     */
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register);

        etUsername = findViewById(R.id.register_et_username);
        etPassword = findViewById(R.id.register_et_password);
        etConfirmPassword = findViewById(R.id.register_et_confirm_password);
        etEmailAddress = findViewById(R.id.register_et_email_address);
        etDisplayName = findViewById(R.id.register_et_display_name);
        llRegister = findViewById(R.id.register_ll_register);
        btnRegister = findViewById(R.id.register_btn_register);
        tvLogin = findViewById(R.id.register_tv_login);

        btnRegister.setOnClickListener(this);
        tvLogin.setOnClickListener(this);
    }

    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.register_btn_register:
                attemptRegistration();
                break;
            case R.id.register_tv_login:
                startActivity(new Intent(this, LoginActivity.class));
                finish();
                break;
        }
    }

    private void setControlsEnabled(boolean enabled) {
        etUsername.setEnabled(enabled);
        etPassword.setEnabled(enabled);
        etConfirmPassword.setEnabled(enabled);
        etEmailAddress.setEnabled(enabled);
        etDisplayName.setEnabled(enabled);
        btnRegister.setEnabled(enabled);
        tvLogin.setEnabled(enabled);
        llRegister.setBackgroundColor(getResources().getColor(enabled ? R.color.colorAccentDark : R.color.gray));
        btnRegister.setText(enabled ? R.string.register_btn_register : R.string.register_btn_registering);
    }

    private void attemptRegistration() {
        setControlsEnabled(false);

        String username = etUsername.getText().toString();
        String password = etPassword.getText().toString();
        String confirmPassword = etConfirmPassword.getText().toString();
        String emailAddress = etEmailAddress.getText().toString();
        String displayName = etDisplayName.getText().toString();

        if (username.isEmpty() || password.isEmpty() || confirmPassword.isEmpty() || emailAddress.isEmpty() || displayName.isEmpty()) {
            showToast(R.string.register_toast_empty_field);
            setControlsEnabled(true);
            return;
        }

        if (!password.equals(confirmPassword)) {
            showToast(R.string.register_toast_password_mismatch);
            setControlsEnabled(true);
            return;
        }

        if (!Patterns.EMAIL_ADDRESS.matcher(emailAddress).matches()) {
            showToast(R.string.register_toast_invalid_email);
            setControlsEnabled(true);
            return;
        }

        TravelationsApp.getApi().register(username, password, emailAddress, displayName).enqueue(this);
    }

        private void showToast(int message) {
            Toast.makeText(this, getString(message), Toast.LENGTH_SHORT).show();
        }

    /**
     * Enter button callback function for registering a new user.
     * @param view view
     */
    public void clickEnterRegisterButton(View view) {

        /*// Get fields
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
        startActivity(i);*/

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

    @Override
    public void onResponse(Call<GenericResponse> call, Response<GenericResponse> response) {
        GenericResponse genericResponse = response.body();
        if (genericResponse == null) {
            Log.e(TravelationsApp.LOG_TAG, "RegisterActivity.onResponse: response body was null");
            setControlsEnabled(true);
            showToast(R.string.register_toast_failure);
        } else if (!genericResponse.isSuccess()) {
            Log.e(TravelationsApp.LOG_TAG, String.format("RegisterActivity.onResponse: response was unsuccessful, code: %d, message: %s", response.code(), genericResponse.getErrorCode()));
            setControlsEnabled(true);
            showToast(R.string.register_toast_failure);
        } else {
            showToast(R.string.register_toast_success);
            Intent i = new Intent(this, LoginActivity.class);
            i.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
            startActivity(i);
            finish();
        }
    }

    @Override
    public void onFailure(Call<GenericResponse> call, Throwable t) {
        Log.e(TravelationsApp.LOG_TAG, String.format("%s.onFailure: request was unsuccessful", this.getLocalClassName()));
        setControlsEnabled(true);
        showToast(R.string.toast_request_failure);
    }
}
