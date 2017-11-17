package com.shreyas208.databass.ui;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.ListView;
import android.widget.SimpleAdapter;
import android.widget.TextView;
import android.widget.Toast;

import com.shreyas208.databass.R;
import com.shreyas208.databass.TravelationsApp;
import com.shreyas208.databass.api.service.DoNothingCallback;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;


/**
 * Profile Activity class.
 */
public class ProfileActivity extends AppCompatActivity {

    private TravelationsApp app;

    /**
     * Creates the Profile Activity instance.
     * @param savedInstanceState  previously saved instance
     */
    @Override
    protected void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_profile);

        app = (TravelationsApp) getApplication();

        // Personalize text view
        TextView welcomeTextView = (TextView) findViewById(R.id.welcomeTextView);
        String welcome = "Welcome " + app.getDisplayName() + "!";
        welcomeTextView.setText(welcome);

        // Gather location and timestamp data
        HashMap<String, String> locationStamps = new HashMap<>();
        for (int i = 0; i < Dummy.locations.length; i++) {

            locationStamps.put(Dummy.timestamps[i], Dummy.locations[i]);

        }

        // Create ListView adapter
        List<HashMap<String, String>> listItems = new ArrayList<>();
        SimpleAdapter adapter = new SimpleAdapter(
                this,
                listItems,
                R.layout.list_item,
                new String[] { "First Line", "Second Line"},
                new int[] { R.id.text1, R.id.text2
        });

        // Insert data into adapter
        for (Object o : locationStamps.entrySet()) {

            HashMap<String, String> resultMap = new HashMap<>();
            Map.Entry pair = (Map.Entry) o;
            resultMap.put("First Line", pair.getKey().toString());
            resultMap.put("Second Line", pair.getValue().toString());
            listItems.add(resultMap);

        }

        // Create list view
        ListView locListView = (ListView) findViewById(R.id.locListView);
        locListView.setAdapter(adapter);

    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.menu_profile, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem menuItem) {
        switch (menuItem.getItemId()) {
            case R.id.profile_menu_logout:
                TravelationsApp.getApi().logout(app.getUsername(), app.getAccessToken()).enqueue(new DoNothingCallback(this.getLocalClassName()));
                app.clearLoginValues();
                Intent i = new Intent(this, MainActivity.class);
                i.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
                startActivity(i);
                finish();
                break;
        }
        return true;
    }

    /**
     * Logout button callback function. Will open the Login Activity.
     * @param view  view
     */
    public void clickLogoutButton(View view) {

        // TODO
        // Something with access tokens?

        Toast.makeText(getApplicationContext(), "Logged out", Toast.LENGTH_LONG).show();

        Intent i = new Intent(getApplicationContext(), LoginActivity.class);
        startActivity(i);

    }

    /**
     * Settings button callback function. Will open the Settings Activity.
     * @param view  view
     */
    public void clickSettingsButton(View view) {

        Intent i = new Intent(getApplicationContext(), SettingsActivity.class);
        startActivity(i);

    }

}
