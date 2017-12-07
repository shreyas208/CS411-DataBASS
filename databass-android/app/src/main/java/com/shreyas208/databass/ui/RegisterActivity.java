package com.shreyas208.databass.ui;

import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.util.Patterns;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.shreyas208.databass.R;
import com.shreyas208.databass.TravelationsApp;
import com.shreyas208.databass.api.model.GenericResponse;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

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

        ((TravelationsApp) getApplication()).clearLoginValues();

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
            TravelationsApp.showToast(this, R.string.register_toast_empty_field);
            setControlsEnabled(true);
            return;
        }

        if (!password.equals(confirmPassword)) {
            TravelationsApp.showToast(this, R.string.register_toast_password_mismatch);
            setControlsEnabled(true);
            return;
        }

        if (!Patterns.EMAIL_ADDRESS.matcher(emailAddress).matches()) {
            TravelationsApp.showToast(this, R.string.register_toast_invalid_email);
            setControlsEnabled(true);
            return;
        }

        TravelationsApp.getApi().register(username, password, emailAddress, displayName).enqueue(this);
    }

    @Override
    public void onResponse(@NonNull Call<GenericResponse> call, @NonNull Response<GenericResponse> response) {
        GenericResponse genericResponse = response.body();
        if (genericResponse == null) {
            Log.e(TravelationsApp.LOG_TAG, String.format("%s.onResponse: response body was null", getLocalClassName()));
            setControlsEnabled(true);
            TravelationsApp.showToast(this, R.string.register_toast_failure);
        } else if (!genericResponse.isSuccess()) {
            Log.e(TravelationsApp.LOG_TAG, String.format("%s.onResponse: response was unsuccessful, message: %s", getLocalClassName(), genericResponse.getErrorCode()));
            setControlsEnabled(true);
            TravelationsApp.showToast(this, R.string.register_toast_failure);
        } else {
            TravelationsApp.showToast(this, R.string.register_toast_success);
            Intent i = new Intent(this, LoginActivity.class);
            i.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
            startActivity(i);
            finish();
        }
    }

    @Override
    public void onFailure(@NonNull Call<GenericResponse> call, @NonNull Throwable t) {
        Log.e(TravelationsApp.LOG_TAG, String.format("%s.onFailure: request was unsuccessful, message: %s", this.getLocalClassName(), t.getMessage()));
        setControlsEnabled(true);
        TravelationsApp.showToast(this, R.string.toast_request_failure);
    }
}
