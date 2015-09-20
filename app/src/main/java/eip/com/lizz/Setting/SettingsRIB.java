package eip.com.lizz.Setting;

import android.content.Context;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.support.v7.app.ActionBarActivity;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;

import org.iban4j.IbanFormatException;
import org.iban4j.IbanUtil;
import org.iban4j.InvalidCheckDigitException;
import org.iban4j.UnsupportedCountryException;

import eip.com.lizz.MenuLizz;
import eip.com.lizz.R;
import eip.com.lizz.Utils.USaveParams;

public class SettingsRIB  extends ActionBarActivity {

    String rib_save;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_settings_rib);

        final EditText rib = (EditText) findViewById(R.id.rib);
        rib.addTextChangedListener(new TextWatcher() {

            public void afterTextChanged(Editable s) {
                String result = s.toString();
                String[] forbidden = {" ","a","b","c","d","e","f","g","h","i","j","k","l","m","n","o","p","q","r","s","t","u","v","w","x","y","z",",",".","@","#","$","%","&","-","+","(",")","*","\"","'",":",";","!","?","_","/","~","`","|","•","√","π","÷","×","¶","∆","}","{","=","°","^","¥","€","¢","£","\\","©","®","™","℅","[","]",">","<"};
                String[] allowed = {"","A","B","C","D","E","F","G","H","I","J","K","L","M","N","O","P","Q","R","S","T","U","V","W","X","Y","Z","","","","","","","","","","","","","","","","","","","","","","","","","","","","","","","","","","","","","","","","","","","","","","","",""};
                for (int index =0; index < forbidden.length; index++){
                    result = result.replace(forbidden[index], allowed[index]);
                }
                if (!s.toString().equals(result)) {
                    rib.setText(result.toUpperCase());
                    rib.setSelection(result.length());
                }
            }
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {}

            public void onTextChanged(CharSequence s, int start, int before, int count) {}
        });

        SharedPreferences sharedpreferences = getSharedPreferences("eip.com.lizz", Context.MODE_PRIVATE);
        rib_save = sharedpreferences.getString("eip.com.lizz.rib", "");

        rib.setText(rib_save);

        final Button save = (Button) findViewById(R.id.save);
        save.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {

                    try {
                        IbanUtil.validate(rib.getText().toString());
                        if (rib.getText().toString().length() == 27 && rib.getText().toString().substring(0, 2).equals("FR")) // On verifie que c'est un IBAN FR.
                        {
                            USaveParams.checkIsForChangePinOrNot(false, SettingsRIB.this, "eip.com.lizz.rib", rib.getText().toString());
                        }
                        else
                        {
                            USaveParams.displayError(8, SettingsRIB.this, null, null, false);
                        }
                    } catch (IbanFormatException |
                            InvalidCheckDigitException |
                            UnsupportedCountryException e) {
                        USaveParams.displayError(7, SettingsRIB.this, null, null, false);
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
        return MenuLizz.settings_menu(item, getBaseContext(), SettingsRIB.this);
    }
}
