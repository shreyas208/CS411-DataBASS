package com.shreyas208.databass.ui;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.Button;

import com.shreyas208.databass.R;
import com.shreyas208.databass.TravelationsApp;

public class MainActivity extends AppCompatActivity implements View.OnClickListener {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (((TravelationsApp) getApplication()).isLoggedIn()) {
            Intent i = new Intent(this, ProfileActivity.class);
            i.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
            startActivity(i);
            finish();
        }

        setContentView(R.layout.activity_main);

        Button btnLogin = findViewById(R.id.main_btn_login);
        Button btnRegister = findViewById(R.id.main_btn_register);

        btnLogin.setOnClickListener(this);
        btnRegister.setOnClickListener(this);
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.main_btn_login:
                startActivity(new Intent(this, LoginActivity.class));
                break;
            case R.id.main_btn_register:
                startActivity(new Intent(this, RegisterActivity.class));
                break;
        }
    }
}
