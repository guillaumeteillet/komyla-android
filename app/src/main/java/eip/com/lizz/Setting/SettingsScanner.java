package eip.com.lizz.Setting;

import android.content.Context;
import android.content.SharedPreferences;
import android.os.Build;
import android.os.Bundle;
import android.support.v7.app.ActionBarActivity;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.CompoundButton;
import android.widget.Switch;
import android.widget.TextView;
import android.widget.ToggleButton;

import eip.com.lizz.CameraPreview;
import eip.com.lizz.MenuLizz;
import eip.com.lizz.R;
import eip.com.lizz.Utils.USaveParams;

/**
 * Created by guillaume on 18/12/14.
 */
public class SettingsScanner  extends ActionBarActivity {

    Switch scanner;
    ToggleButton scannerOld;
    Boolean scannerStatus;
    TextView error_camera2;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        SharedPreferences sharedpreferences = getSharedPreferences("eip.com.lizz", Context.MODE_PRIVATE);
        scannerStatus = sharedpreferences.getBoolean("eip.com.lizz.scannerstatus", true);
        final boolean apn = CameraPreview.checkCameraHardware(getBaseContext());

        if (Build.VERSION.SDK_INT >= 14) {
            setContentView(R.layout.activity_settings_scanner);
            scanner = (Switch) findViewById(R.id.onOrOff);
            if (!apn) {
                scanner.setChecked(false);
                scanner.setEnabled(false);
                error_camera2 = (TextView) findViewById(R.id.error_camera);
                error_camera2.setText(getResources().getString(R.string.error_camera_txt));
            }
            else
            {
                scanner.setChecked(scannerStatus);
                scanner.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {

                    @Override
                    public void onCheckedChanged(CompoundButton buttonView,
                                                 boolean isChecked) {

                        if (isChecked) {
                            scannerStatus = true;
                        } else {
                            scannerStatus = false;
                        }
                    }
                });
            }
        }
        else
        {
            setContentView(R.layout.activity_settings_scanner_old);
            scannerOld = (ToggleButton) findViewById(R.id.onOrOff);
            if (!apn) {
                scannerOld.setChecked(false);
                scannerOld.setEnabled(false);
                error_camera2 = (TextView) findViewById(R.id.error_camera);
                error_camera2.setText(getResources().getString(R.string.error_camera_txt));
            }
            else {
                scannerOld.setChecked(scannerStatus);
                scannerOld.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {

                    @Override
                    public void onCheckedChanged(CompoundButton buttonView,
                                                 boolean isChecked) {

                        if (isChecked) {
                            scannerStatus = true;
                        } else {
                            scannerStatus = false;
                        }
                    }
                });
            }
        }

        final Button save = (Button) findViewById(R.id.save);
        save.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {

                SharedPreferences sharedpreferences = getSharedPreferences("eip.com.lizz", Context.MODE_PRIVATE);

                if (scannerStatus) {
                    USaveParams.saveParamsBoolean(SettingsScanner.this, "eip.com.lizz.scannerstatus", true);
                }
                else
                {
                    USaveParams.saveParamsBoolean(SettingsScanner.this, "eip.com.lizz.scannerstatus", false);
                }
            }
        });

        final Button cancel = (Button) findViewById(R.id.cancel);
        cancel.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                finish();
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
        return MenuLizz.settings_menu(item, getBaseContext(), SettingsScanner.this);
    }
}
