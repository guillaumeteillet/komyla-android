package eip.com.lizz.Setting;

import android.content.Context;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.support.v7.app.ActionBarActivity;
import android.text.InputType;
import android.text.method.PasswordTransformationMethod;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;

import eip.com.lizz.MenuLizz;
import eip.com.lizz.R;
import eip.com.lizz.Utils.UAlertBox;
import eip.com.lizz.Utils.USaveParams;


public class SettingsCodePIN extends ActionBarActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_settings_code_pin);

        final EditText codePinEditText = (EditText) findViewById(R.id.codepin);
        final EditText confirmCodePinEditText = (EditText) findViewById(R.id.confirmcodepin);

        SharedPreferences sharedpreferences = getSharedPreferences("eip.com.lizz", Context.MODE_PRIVATE);

        codePinEditText.setInputType(InputType.TYPE_CLASS_NUMBER | InputType.TYPE_TEXT_VARIATION_PASSWORD);
        codePinEditText.setTransformationMethod(PasswordTransformationMethod.getInstance());

        confirmCodePinEditText.setInputType(InputType.TYPE_CLASS_NUMBER | InputType.TYPE_TEXT_VARIATION_PASSWORD);
        confirmCodePinEditText.setTransformationMethod(PasswordTransformationMethod.getInstance());

        final Button save = (Button) findViewById(R.id.save);
        save.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {

                if (codePinEditText.getText().toString().equals(""))
                    USaveParams.displayError(6, SettingsCodePIN.this, null, null, false);
                else
                {
                    if (codePinEditText.getText().toString().equals(confirmCodePinEditText.getText().toString())) {
                        if (codePinEditText.getText().toString().length() == 4) {
                            USaveParams.checkIsForChangePinOrNot(true, SettingsCodePIN.this, "eip.com.lizz.codepinlizz", codePinEditText.getText().toString());
                        }
                        else
                            UAlertBox.alertOk(SettingsCodePIN.this, getResources().getString(R.string.error), getResources().getString(R.string.pinInvalid));
                    }
                    else
                        UAlertBox.alertOk(SettingsCodePIN.this, getResources().getString(R.string.error), getResources().getString(R.string.wrongPINConfirm));
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
        return MenuLizz.settings_menu(item, getBaseContext(), SettingsCodePIN.this);
    }
}
