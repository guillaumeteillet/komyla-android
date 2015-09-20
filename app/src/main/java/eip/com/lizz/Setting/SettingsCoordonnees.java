package eip.com.lizz.Setting;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.os.Bundle;
import android.support.v7.app.ActionBarActivity;
import android.telephony.TelephonyManager;
import android.text.InputType;
import android.text.method.PasswordTransformationMethod;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ListView;

import eip.com.lizz.Utils.USaveParams;
import eip.com.lizz.Utils.UAlertBox;
import eip.com.lizz.MenuLizz;
import eip.com.lizz.R;

public class SettingsCoordonnees  extends ActionBarActivity {

    ListView listView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_settings_coordonnees);

        listView = (ListView) findViewById(R.id.list);

       EditText name =  (EditText) findViewById(R.id.yournametxt);
       final EditText address =  (EditText) findViewById(R.id.youradresstxt);
       final EditText complement =  (EditText) findViewById(R.id.yourcomplementtxt);
       final EditText cp =  (EditText) findViewById(R.id.yourcptxt);
       final EditText city =  (EditText) findViewById(R.id.yourcitytxt);
       //EditText country =  (EditText) findViewById(R.id.yourcountrytxt);
       final EditText email =  (EditText) findViewById(R.id.youremailtxt);

        Bundle bundle = getIntent().getExtras();
        if (bundle != null) {
            if (bundle.getBoolean("numberAdd")) {
                UAlertBox.alertOk(SettingsCoordonnees.this, getResources().getString(R.string.dialog_numberAdd), getResources().getString(R.string.dialog_numberAddTxt));
            }
        }

        final SharedPreferences sharedpreferences = getSharedPreferences("eip.com.lizz", Context.MODE_PRIVATE);
        String phoneTMP = sharedpreferences.getString("eip.com.lizz.phoneTMP", "");
        String phone = sharedpreferences.getString("eip.com.lizz.phone", "");
        name.setText(sharedpreferences.getString("eip.com.lizz.firstname", "")+" "+sharedpreferences.getString("eip.com.lizz.surname", ""));
        address.setText(sharedpreferences.getString("eip.com.lizz.address", ""));
        complement.setText(sharedpreferences.getString("eip.com.lizz.complement", ""));
        cp.setText(sharedpreferences.getString("eip.com.lizz.postalcode", ""));
        city.setText(sharedpreferences.getString("eip.com.lizz.city", ""));
        email.setText(sharedpreferences.getString("eip.com.lizz.email", ""));

        String[] values;
        if (!phoneTMP.equals(""))
            values = phoneTMP.split(";");
        else
            values = phone.split(";");

        final ArrayAdapter<String> adapter = new ArrayAdapter<String>(this,
                android.R.layout.simple_list_item_1, android.R.id.text1, values);
        listView.setAdapter(adapter);
        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {

            @Override
            public void onItemClick(final AdapterView<?> parent, View view,
                                    final int position, long id) {

                final int itemPosition     = position;
                final String  itemValue    = (String) listView.getItemAtPosition(position);

                Intent loggedUser;
                AlertDialog.Builder alerte;

                alerte = UAlertBox.alert(SettingsCoordonnees.this, "Confirmation", "Êtes-vous sûr de vouloir supprimer ce numéro de votre compte ?");
                alerte.setPositiveButton(getResources().getString(R.string.dialog_ok), new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int whichButton) {
                        TelephonyManager tMgr = (TelephonyManager)getBaseContext().getSystemService(Context.TELEPHONY_SERVICE);
                        if (tMgr.getLine1Number().equals(itemValue))
                            UAlertBox.alertOk(SettingsCoordonnees.this, "Erreur", "Vous ne pouvez pas supprimer le numéro de votre carte SIM actuelle.");
                        else
                        {
                            String phone = sharedpreferences.getString("eip.com.lizz.phone", "");
                            String[] phones = phone.split(";");
                            String result = "";
                            int i = 0;
                            while((i <= (phones.length - 1))) {
                                if (!itemValue.equals(phones[i])) {
                                    result += phones[i]+";";
                                }
                                i++;
                            }
                            sharedpreferences.edit().putString("eip.com.lizz.phoneTMP", result).apply();

                            finish();
                            Intent intent = new Intent(getBaseContext(), SettingsCoordonnees.class);
                            intent.addFlags(Intent.FLAG_ACTIVITY_NO_ANIMATION);
                            startActivity(intent);
                        }
                    }
                });
                alerte.setNegativeButton(getResources().getString(R.string.dialog_cancel), new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int whichButton) {

                    }
                });
                alerte.show();
            }

        });

        final Button save = (Button) findViewById(R.id.save);
        save.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {

                String addressGet = address.getText().toString().replaceAll(" ", "");
                String cityGet = city.getText().toString().replaceAll(" ", "");

                if (addressGet.length() <= 3)
                {
                    address.setError(getString(R.string.error_address_short));
                    address.requestFocus();
                }
                else if (cp.getText().length() < 5)
                {
                    cp.setError(getString(R.string.error_cp_short));
                    cp.requestFocus();
                }
                else if (cityGet.length() <= 3)
                {
                    city.setError(getString(R.string.error_city_short));
                    city.requestFocus();
                }
               /* else if (TextUtils.isEmpty(email.getText())) {
                    email.setError(getString(R.string.error_field_required));
                    email.requestFocus();
                } else if (!email.getText().toString().contains("@") || !email.getText().toString().contains(".")) {
                    email.setError(getString(R.string.error_invalid_email));
                    email.requestFocus();
                }*/
                else
                {
                    // TODO : APPEL A L'API POUR SAUVEGARDER TOUT CA

                    final SharedPreferences sharedpreferences = getSharedPreferences("eip.com.lizz", Context.MODE_PRIVATE);
                    final String pinCode = sharedpreferences.getString("eip.com.lizz.codepinlizz", "");

                    final EditText input = new EditText(getBaseContext());
                    input.setInputType(InputType.TYPE_CLASS_NUMBER | InputType.TYPE_TEXT_VARIATION_PASSWORD);
                    input.setTransformationMethod(PasswordTransformationMethod.getInstance());
                    input.setTextColor(Color.BLACK);
                    final AlertDialog.Builder alert = UAlertBox.alertInputOk(SettingsCoordonnees.this, getResources().getString(R.string.dialog_title_confirm), getResources().getString(R.string.dialog_confirm_hint_pin), input);
                    alert.setPositiveButton(getResources().getString(R.string.dialog_ok), new DialogInterface.OnClickListener() {
                        public void onClick(DialogInterface dialog, int whichButton) {
                            if (input.getText().toString().equals(pinCode))
                            {
                                sharedpreferences.edit().putInt("eip.com.lizz.tentativePin", 0).apply();
                                sharedpreferences.edit().putString("eip.com.lizz.address", address.getText().toString()).apply();
                                sharedpreferences.edit().putString("eip.com.lizz.complement", complement.getText().toString()).apply();
                                sharedpreferences.edit().putString("eip.com.lizz.postalcode", cp.getText().toString()).apply();
                                sharedpreferences.edit().putString("eip.com.lizz.city", city.getText().toString()).apply();
                                String phoneTMP2 = sharedpreferences.getString("eip.com.lizz.phoneTMP", "");
                                if (!phoneTMP2.equals(""))
                                {
                                    sharedpreferences.edit().putString("eip.com.lizz.phone", phoneTMP2).apply();
                                    sharedpreferences.edit().putString("eip.com.lizz.phoneTMP", "").apply();
                                }
                                //sharedpreferences.edit().putString("eip.com.lizz.email", email.getText().toString()).apply();
                                InputMethodManager imm = (InputMethodManager)getSystemService(
                                        Context.INPUT_METHOD_SERVICE);
                                imm.hideSoftInputFromWindow(input.getWindowToken(), 0);
                                finish();
                            }
                            else
                            {
                                USaveParams.tentativeCheck(SettingsCoordonnees.this, sharedpreferences);
                            }
                        }
                    });
                    alert.show();
                }

     }
        });

        final Button cancel = (Button) findViewById(R.id.cancel);
        cancel.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                sharedpreferences.edit().putString("eip.com.lizz.phoneTMP", "").apply();
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
        return MenuLizz.settings_menu(item, getBaseContext(), SettingsCoordonnees.this);
    }
}
