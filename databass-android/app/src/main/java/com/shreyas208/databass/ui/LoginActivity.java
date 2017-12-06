package com.shreyas208.databass.ui;

import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.shreyas208.databass.R;
import com.shreyas208.databass.TravelationsApp;
import com.shreyas208.databass.api.model.LoginResponse;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;


/**
 * Login Activity class.
 */
public class LoginActivity extends AppCompatActivity implements View.OnClickListener, Callback<LoginResponse> {

    private EditText etUsername;
    private EditText etPassword;
    private LinearLayout llLogin;
    private Button btnLogin;
    private TextView tvRegister;

    private String username;

    /**
     * Creates the Login Activity instance.
     * @param savedInstanceState  previously saved instance
     */
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        etUsername = findViewById(R.id.login_et_username);
        etPassword = findViewById(R.id.login_et_password);
        llLogin = findViewById(R.id.login_ll_login);
        btnLogin = findViewById(R.id.login_btn_login);
        tvRegister = findViewById(R.id.login_tv_register);

        btnLogin.setOnClickListener(this);
        tvRegister.setOnClickListener(this);

        setControlsEnabled(true);
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.login_btn_login:
                attemptLogin();
                break;
            case R.id.login_tv_register:
                startActivity(new Intent(this, RegisterActivity.class));
                finish();
                break;
        }
    }

    private void setControlsEnabled(boolean enabled) {
        btnLogin.setEnabled(enabled);
        tvRegister.setEnabled(enabled);
        llLogin.setBackgroundColor(getResources().getColor(enabled ? R.color.colorAccentDark : R.color.gray));
        btnLogin.setText(enabled ? R.string.login_btn_login : R.string.login_btn_logging_in);
    }

    private void attemptLogin() {
        setControlsEnabled(false);

        String username = etUsername.getText().toString();
        String password = etPassword.getText().toString();

        if (username.isEmpty() || password.isEmpty()) {
            Toast.makeText(this, getString(R.string.login_toast_empty_field), Toast.LENGTH_SHORT).show();
            setControlsEnabled(true);
            return;
        }

        this.username = username;

        TravelationsApp.getApi().login(username, password).enqueue(this);
    }

    @Override
    public void onResponse(@NonNull Call<LoginResponse> call, @NonNull Response<LoginResponse> response) {
        LoginResponse loginResponse = response.body();
        if (loginResponse == null) {
            Log.e(TravelationsApp.LOG_TAG, String.format("%s.onResponse: response body was null", getLocalClassName()));
            setControlsEnabled(true);
            TravelationsApp.showToast(LoginActivity.this, R.string.login_toast_failure);
        } else if (!loginResponse.isSuccess()) {
                Log.e(TravelationsApp.LOG_TAG, String.format("%s.onResponse: response was unsuccessful, code: %d, message: %s", getLocalClassName(), response.code(), loginResponse.getErrorCode()));
            setControlsEnabled(true);
            TravelationsApp.showToast(LoginActivity.this, R.string.login_toast_failure);
        } else {
            ((TravelationsApp) getApplication()).setLoginValues(username, loginResponse.getAccessToken(), loginResponse.getEmailAddress(), loginResponse.getDisplayName());
            //Intent i = new Intent(this, ProfileActivity.class);
            Intent i = new Intent(this, MainActivity.class);
            i.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
            startActivity(i);
            finish();
        }
    }

    @Override
    public void onFailure(@NonNull Call<LoginResponse> call, @NonNull Throwable t) {
        Log.e(TravelationsApp.LOG_TAG, String.format("%s.onFailure: request was unsuccessful, message:\n%s", this.getLocalClassName(), t.getMessage()));
        setControlsEnabled(true);
        TravelationsApp.showToast(LoginActivity.this, R.string.login_toast_failure);
    }
}
