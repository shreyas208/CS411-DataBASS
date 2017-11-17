package com.shreyas208.databass.ui;

import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.EditText;

import com.shreyas208.databass.R;
import com.shreyas208.databass.TravelationsApp;
import com.shreyas208.databass.api.model.GenericResponse;
import com.shreyas208.databass.api.service.TravelationsAPI;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;


/**
 * Settings Activity class.
 */
public class SettingsActivity extends AppCompatActivity implements View.OnClickListener, Callback<GenericResponse> {

    TravelationsApp app;

    private EditText etDisplayName, etEmailAddress;
    private FloatingActionButton fabDisplayName, fabEmailAddress;

    /**
     * Creates the Settings Activity instance.
     * @param savedInstanceState  previously saved instance
     */
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_settings);

        app = (TravelationsApp) getApplication();

        etDisplayName = findViewById(R.id.settings_et_display_name);
        fabDisplayName = findViewById(R.id.settings_fab_display_name);
        etEmailAddress = findViewById(R.id.settings_et_email_address);
        fabEmailAddress = findViewById(R.id.settings_fab_email_address);

        fabDisplayName.setOnClickListener(this);
        fabEmailAddress.setOnClickListener(this);

        etDisplayName.setText(app.getDisplayName());
        etEmailAddress.setText(app.getEmailAddress());
    }

    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.settings_fab_display_name:
                attemptChangeDisplayName();
                break;
            case R.id.settings_fab_email_address:
                attemptChangeEmailAddress();
                break;
        }
    }

    private void setControlsEnabled(boolean enabled) {
        setDisplayNameControlsEnabled(enabled);
        setEmailAddressControlsEnabled(enabled);
    }

    private void setDisplayNameControlsEnabled(boolean enabled) {
        etDisplayName.setEnabled(enabled);
        fabDisplayName.setEnabled(enabled);
    }

    private void setEmailAddressControlsEnabled(boolean enabled) {
        etEmailAddress.setEnabled(enabled);
        fabEmailAddress.setEnabled(enabled);
    }

    private void attemptChangeDisplayName() {
        setControlsEnabled(false);

        String displayName = etDisplayName.getText().toString();
        if (displayName.isEmpty()) {
            setControlsEnabled(true);
            TravelationsApp.showToast(this, R.string.settings_toast_display_name_empty);
            return;
        }

        TravelationsApp.getApi().changeDisplayName(app.getUsername(), app.getAccessToken(), displayName).enqueue(this);
    }

    private void attemptChangeEmailAddress() {
        setControlsEnabled(false);

        String emailAddress = etEmailAddress.getText().toString();
        if (emailAddress.isEmpty()) {
            setControlsEnabled(true);
            TravelationsApp.showToast(this, R.string.settings_toast_email_address_empty);
            return;
        }

        TravelationsApp.getApi().changeEmailAddress(app.getUsername(), app.getAccessToken(), emailAddress).enqueue(this);
    }

    @Override
    public void onResponse(Call<GenericResponse> call, Response<GenericResponse> response) {
        GenericResponse genericResponse = response.body();
        if (genericResponse == null) {
            Log.e(TravelationsApp.LOG_TAG, String.format("%s.onResponse: response body was null", getLocalClassName()));
            TravelationsApp.showToast(this, R.string.settings_toast_failure);
        } else if (!genericResponse.isSuccess()) {
            Log.e(TravelationsApp.LOG_TAG, String.format("%s.onResponse: response was unsuccessful, code: %d, message: %s", getLocalClassName(), response.code(), genericResponse.getErrorCode()));
            TravelationsApp.showToast(this, R.string.settings_toast_failure);
        } else {
            app.setDisplayName(etDisplayName.getText().toString());
            app.setEmailAddress(etEmailAddress.getText().toString());
            TravelationsApp.showToast(this, R.string.settings_toast_success);
        }
        setControlsEnabled(true);
    }

    @Override
    public void onFailure(Call<GenericResponse> call, Throwable t) {

    }
}
