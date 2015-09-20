package eip.com.lizz;

import android.content.Intent;
import android.support.v7.app.ActionBarActivity;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ListView;

import eip.com.lizz.Setting.SettingsCodePIN;
import eip.com.lizz.Setting.SettingsPassword;
import eip.com.lizz.Setting.SettingsPayementLimit;
import eip.com.lizz.Setting.SettingsProfil;
import eip.com.lizz.Setting.SettingsScanner;


public class SettingsActivity extends ActionBarActivity {

    ListView listView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_settings);// Get ListView object from xml
        listView = (ListView) findViewById(R.id.list);

        String[] values = new String[] {
                "Vos informations personnelles",
                "Alerte de dépense",
                "Code PIN Lizz",
                "Mot de passe",
                "Scanner",
        };

        ArrayAdapter<String> adapter = new ArrayAdapter<String>(this,
                android.R.layout.simple_list_item_1, android.R.id.text1, values);
        listView.setAdapter(adapter);
        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {

            @Override
            public void onItemClick(AdapterView<?> parent, View view,
                                    int position, long id) {

                int itemPosition     = position;
                String  itemValue    = (String) listView.getItemAtPosition(position);

                Intent loggedUser;


                switch (itemPosition)
                {
                    case 0:
                        loggedUser = new Intent(getBaseContext(), SettingsProfil.class);
                        startActivity(loggedUser);
                        break;
                    case 1:
                        loggedUser = new Intent(getBaseContext(), SettingsPayementLimit.class);
                        startActivity(loggedUser);
                        break;
                    case 2:
                        loggedUser = new Intent(getBaseContext(), SettingsCodePIN.class);
                        startActivity(loggedUser);
                        break;
                    case 3:
                        loggedUser = new Intent(getBaseContext(), SettingsPassword.class);
                        startActivity(loggedUser);
                        break;
                    case 4:
                        loggedUser = new Intent(getBaseContext(), SettingsScanner.class);
                        startActivity(loggedUser);
                        break;
                }

            }

        });
    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.settings, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        return MenuLizz.settings_menu(item, getBaseContext(), SettingsActivity.this);
    }
}
