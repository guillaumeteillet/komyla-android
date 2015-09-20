package eip.com.lizz;

import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.support.v7.app.ActionBarActivity;
import android.telephony.TelephonyManager;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;

import eip.com.lizz.Setting.SettingsCoordonnees;
import eip.com.lizz.Utils.UAlertBox;
import eip.com.lizz.Utils.UDownload;


public class HomeLizzActivity extends ActionBarActivity {

    boolean scannerStatus;
    boolean isLogged, isLoginJustNow = false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_home_lizz);

       SharedPreferences sharedpreferences = getSharedPreferences("eip.com.lizz", Context.MODE_PRIVATE);
        isLogged = sharedpreferences.getBoolean("eip.com.lizz.isLogged", false);
        final Bundle bundle = getIntent().getExtras();
        if (bundle != null)
        {
            isLoginJustNow = bundle.getBoolean("isLoginJustNow");
        }

        sharedpreferences.edit().putBoolean("eip.com.lizz.flash", false).apply(); // flash off
        String firstname, surname, email, phone, id_user;

        firstname = sharedpreferences.getString("eip.com.lizz.firstname", "");
        surname = sharedpreferences.getString("eip.com.lizz.surname", "");
        email = sharedpreferences.getString("eip.com.lizz.email", "");
        id_user = sharedpreferences.getString("eip.com.lizz.id_user", "");
        phone = sharedpreferences.getString("eip.com.lizz.phone", "");

        Log.d("DEBUG LOL", ">>>> "+id_user+" ----- "+firstname+ " -- "+ surname + "-- "+ email +"---"+phone+"--");

        if (isLogged)
        {

            if (isLoginJustNow)
            {
                final ProgressDialog progress = new ProgressDialog(HomeLizzActivity.this);
                progress.setTitle(getResources().getString(R.string.dialog_download));
                progress.setMessage(getResources().getString(R.string.dialog_download_rsa_key));
                progress.setCancelable(false);
                progress.show();
                new Thread(new Runnable() {
                    @Override
                    public void run()
                    {
                        UDownload.downloadFile("http://test-ta-key.lizz.fr/lastKey.key", "keyrsa.pub", getBaseContext());
                        progress.dismiss();
                    }
                }).start();
            }
            if (getResources().getString(R.string.debugOrProd).equals("PROD"))
                checkSIMNumber();

            Button payer = (Button) findViewById(R.id.payer);
            payer.setOnClickListener(new View.OnClickListener() {
                public void onClick(View v) {
                    SharedPreferences sharedpreferences = getSharedPreferences("eip.com.lizz", Context.MODE_PRIVATE);
                    scannerStatus = sharedpreferences.getBoolean("eip.com.lizz.scannerstatus", true);
                    boolean apn = CameraPreview.checkCameraHardware(getBaseContext());
                    if (scannerStatus && apn)
                    {
                        Intent intent = new Intent(getBaseContext(), ScanQRCodeActivity.class);
                        startActivity(intent);
                    }
                    else
                    {
                        Intent intent = new Intent(getBaseContext(), PayementWithUniqueCodeActivity.class);
                        startActivity(intent);
                    }
                }
            });

            Button payerpap = (Button) findViewById(R.id.payerpap);
            payerpap.setOnClickListener(new View.OnClickListener() {
                public void onClick(View v) {
                    Intent intent = new Intent(getBaseContext(), PayementPTPActivity.class);
                    startActivity(intent);
                }
            });


            Button AddPaymentMethod = (Button) findViewById(R.id.add);
            AddPaymentMethod.setOnClickListener(new View.OnClickListener() {
                public void onClick(View v) {
                        Intent intent = new Intent(getBaseContext(), PaymentMethodsActivity.class);
                        startActivity(intent);
                }
            });

            Button ViewTickets = (Button)findViewById(R.id.tickets);
            ViewTickets.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    Intent intent = new Intent(getBaseContext(), PaymentHistoryActivity.class);
                    startActivity(intent);
                }
            });
        }
        else
        {
            Intent loggedUser = new Intent(getBaseContext(), HomeActivity.class);
            loggedUser.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK | Intent.FLAG_ACTIVITY_NO_ANIMATION | Intent.FLAG_ACTIVITY_NEW_TASK);
            startActivity(loggedUser);
        }
    }

    private void checkSIMNumber() {

        final TelephonyManager tMgr = (TelephonyManager)getBaseContext().getSystemService(Context.TELEPHONY_SERVICE);
        int simState = tMgr.getSimState();
        if (simState == TelephonyManager.SIM_STATE_READY)
        {
            if (!tMgr.getLine1Number().equals("") && tMgr.getLine1Number() != null)
            {
                final SharedPreferences sharedpreferences = getSharedPreferences("eip.com.lizz", Context.MODE_PRIVATE);
                String phoneNever = sharedpreferences.getString("eip.com.lizz.phonenever", "");
                boolean foundNever = false;
                int i = 0;
                if (!phoneNever.equals(""))
                {
                    String[] values;
                    values = phoneNever.split(";");
                    while (i < values.length)
                    {
                        if (values[i].equals(tMgr.getLine1Number()+sharedpreferences.getString("eip.com.lizz.email", "")))
                            foundNever = true;
                        i++;
                    }
                }
                if (!foundNever)
                {
                    String phone;
                    boolean isNew = true;
                    boolean loop = true;
                    i = 0;
                    phone = sharedpreferences.getString("eip.com.lizz.phone", "");
                    String[] phones = phone.split(";");
                    while((i <= (phones.length - 1)) && loop == true) {
                        if (tMgr.getLine1Number().equals(phones[i])) {
                            isNew = false;
                            loop = false;
                        }
                        i++;
                    }

                    if (isNew)
                    {
                        AlertDialog.Builder alert;
                        alert = UAlertBox.alert(HomeLizzActivity.this, getResources().getString(R.string.dialog_new_phone_number), getResources().getString(R.string.dialog_new_phone_number_txt1) + tMgr.getLine1Number() + getResources().getString(R.string.dialog_new_phone_number_txt2));
                        alert.setPositiveButton(getResources().getString(R.string.dialog_yes), new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialog, int whichButton) {
                                String phone_before;
                                phone_before = sharedpreferences.getString("eip.com.lizz.phone", "");
                                sharedpreferences.edit().putString("eip.com.lizz.phone", phone_before+tMgr.getLine1Number()+";").apply();
                                Intent intent = new Intent(getBaseContext(), SettingsCoordonnees.class);
                                intent.putExtra("numberAdd", true);
                                startActivity(intent);
                            }
                        });
                        alert.setNeutralButton(getResources().getString(R.string.dialog_notnow), null);
                        alert.setNegativeButton(getResources().getString(R.string.dialog_never), new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialog, int whichButton) {
                                String phone_before;
                                phone_before = sharedpreferences.getString("eip.com.lizz.phonenever", "");
                                sharedpreferences.edit().putString("eip.com.lizz.phonenever", phone_before+tMgr.getLine1Number()+sharedpreferences.getString("eip.com.lizz.email", "")+";").apply();
                            }
                        });
                        alert.show();
                    }
                }
            }
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.main_menu, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
            return MenuLizz.main_menu(item, getBaseContext(), HomeLizzActivity.this);
    }

    public void onResume()
    {
        super.onResume();
        SharedPreferences sharedpreferences = getSharedPreferences("eip.com.lizz", Context.MODE_PRIVATE);
        sharedpreferences.edit().putBoolean("eip.com.lizz.flash", false).apply();
    }
}
