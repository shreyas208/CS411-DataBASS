package com.shreyas208.databass;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.ListView;
import android.widget.SimpleAdapter;
import android.widget.TextView;
import android.widget.Toast;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;


public class ProfileActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_profile);

        Toast.makeText(getApplicationContext(), "Logged in", Toast.LENGTH_LONG).show();

        TextView welcomeTextView = (TextView) findViewById(R.id.welcomeTextView);
        String welcome = "Welcome " + Dummy.displayName + "!";
        welcomeTextView.setText(welcome);

        ListView locListView = (ListView) findViewById(R.id.locListView);

        HashMap<String, String> locationStamps = new HashMap<>();

        for (int i = 0; i < Dummy.locations.length; i++) {

            locationStamps.put(Dummy.timestamps[i], Dummy.locations[i]);

        }

        List<HashMap<String, String>> listItems = new ArrayList<>();

        SimpleAdapter adapter = new SimpleAdapter(
                this,
                listItems,
                R.layout.list_item,
                new String[] { "First Line", "Second Line"},
                new int[] { R.id.text1, R.id.text2
        });

        for (Object o : locationStamps.entrySet()) {

            HashMap<String, String> resultMap = new HashMap<>();
            Map.Entry pair = (Map.Entry) o;
            resultMap.put("First Line", pair.getKey().toString());
            resultMap.put("Second Line", pair.getValue().toString());
            listItems.add(resultMap);

        }

        locListView.setAdapter(adapter);

    }

    public void clickLogoutButton(View view) {

        Toast.makeText(getApplicationContext(), "Logged out", Toast.LENGTH_LONG).show();

        Intent i = new Intent(getApplicationContext(), LoginActivity.class);
        startActivity(i);

    }

    public void clickSettingsButton(View view) {

        Intent i = new Intent(getApplicationContext(), SettingsActivity.class);
        startActivity(i);

    }

}
