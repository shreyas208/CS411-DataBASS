package com.shreyas208.databass;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

public class MainActivity extends AppCompatActivity implements View.OnClickListener {

    private Button btnClickMe;
    private TextView tvMainText;

    private int count = 0;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        btnClickMe = (Button) findViewById(R.id.btnClickMe);
        tvMainText = (TextView) findViewById(R.id.tvMainText);

        btnClickMe.setOnClickListener(this);
    }

    public void onClick(View v) {
        if (v.getId() == R.id.btnClickMe) {
            count++;
            tvMainText.setText("Hello World! You have clicked the button " + count + " times.");
            Toast.makeText(this, R.string.main_toast_text, Toast.LENGTH_SHORT).show();
        }
    }

}
