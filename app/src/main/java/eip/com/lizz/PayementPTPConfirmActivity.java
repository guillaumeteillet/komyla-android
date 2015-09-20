package eip.com.lizz;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.graphics.Color;
import android.net.Uri;
import android.os.Bundle;
import android.support.v7.app.ActionBarActivity;
import android.telephony.TelephonyManager;
import android.text.InputType;
import android.text.method.PasswordTransformationMethod;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;

import org.apache.http.HttpResponse;
import org.json.JSONObject;

import java.io.IOException;
import java.io.InputStream;
import java.security.InvalidKeyException;
import java.security.NoSuchAlgorithmException;
import java.security.NoSuchProviderException;
import java.security.spec.InvalidKeySpecException;

import javax.crypto.BadPaddingException;
import javax.crypto.IllegalBlockSizeException;
import javax.crypto.NoSuchPaddingException;

import eip.com.lizz.Protocols.PED;
import eip.com.lizz.QueriesAPI.SendSMSToAPI;
import eip.com.lizz.QueriesAPI.SendTransactionPTPToAPI;
import eip.com.lizz.Utils.UAlertBox;
import eip.com.lizz.Utils.UApi;
import eip.com.lizz.Utils.UDownload;
import eip.com.lizz.Utils.UNetwork;
import eip.com.lizz.Utils.UPayement;
import eip.com.lizz.Utils.UPhoneBook;
import eip.com.lizz.Utils.USaveParams;


public class PayementPTPConfirmActivity extends ActionBarActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_payement_ptpconfirm);

        boolean api = false;
        final Bundle bundle = getIntent().getExtras();
        // On check si le contact est associé à un compte lizz ou pas. Si oui, on set le nom du contact de l'API sur le label.

        TextView name_label = (TextView) findViewById(R.id.name_destinataire);
        TextView somme_label = (TextView) findViewById(R.id.somme);
        TextView contact_label = (TextView) findViewById(R.id.contactLabel);
        TextView no_account_label = (TextView) findViewById(R.id.no_account);
        ImageView profil_picture = (ImageView) findViewById(R.id.profil_picture);

        if (bundle != null) {
            if (bundle.getString("contact") != null) {

                boolean isEmail = bundle.getBoolean("isEmail");
                boolean isPhone = bundle.getBoolean("isPhone");
                String contact = bundle.getString("contact");
                contact_label.setText(contact);

                if (api)
                {
                    name_label.setText("MATHIEU ROBLIN API"); // A remplacer par le retour API.
                }
                else
                {
                    String contactName = "", uri = "";
                    if (isPhone)
                    {
                        String[] array = UPhoneBook.infosByPhone(getContentResolver(), contact);
                        contactName = array[0];
                        uri = array[1];
                    }
                    else if (isEmail)
                    {
                        String[] array = UPhoneBook.infosByEmail(getContentResolver(), contact);
                        contactName = array[0];
                        uri = array[1];
                    }
                    if (contactName.equals(""))
                    {
                        name_label.setText(contact);
                        contact_label.setVisibility(View.GONE);
                        if (isEmail)
                            no_account_label.setText(contact+" "+getResources().getString(R.string.label_no_account_email));
                        else if (isPhone)
                            no_account_label.setText(contact+" "+getResources().getString(R.string.label_no_account_sms));
                    }
                    else
                    {
                        name_label.setText(contactName);
                        if (isEmail)
                            no_account_label.setText(contactName+" ("+contact+") "+getResources().getString(R.string.label_no_account_email));
                        else if (isPhone)
                            no_account_label.setText(contactName+" ("+contact+") "+getResources().getString(R.string.label_no_account_sms));
                    }
                    if (!uri.equals(""))
                        profil_picture.setImageURI(Uri.parse(uri));
                    else
                        profil_picture.setImageResource(R.drawable.ic_launcher);
                    if(profil_picture.getDrawable() == null)
                        profil_picture.setImageResource(R.drawable.ic_launcher);
                }
            }
            if (bundle.getString("somme") != null) {
                String somme = bundle.getString("somme");
                somme_label.setText(somme+" €");
            }
            if (bundle.getBoolean("isForced"))
            {
                checkSimCARD(bundle);
            }
        }


        Button confirm = (Button) findViewById(R.id.confirm);
        confirm.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                 checkSimCARD(bundle);
            }
        });
    }

    private void checkSimCARD(Bundle bundle) {
        TelephonyManager tMgr = (TelephonyManager)getBaseContext().getSystemService(Context.TELEPHONY_SERVICE);
        if ((tMgr.getLine1Number() == null || tMgr.getLine1Number().equals("")))
            UAlertBox.alertOk(PayementPTPConfirmActivity.this, getResources().getString(R.string.warning), getResources().getString(R.string.error_sim_card));
        else if (!UNetwork.isMobileAvailable(getBaseContext()))
            UAlertBox.alertOk(PayementPTPConfirmActivity.this, getResources().getString(R.string.warning), getResources().getString(R.string.error_network_phone));
        else
        {
            UPayement.popUpPIN(bundle, PayementPTPConfirmActivity.this, getBaseContext());
        }
    }

    public static void dataAPI(ProgressDialog progress, HttpResponse httpResponse, Intent paiement, Activity activity, Context context)
    {
        InputStream inputStream = null;
        try {
            progress.dismiss();
            inputStream = httpResponse.getEntity().getContent();
            String jString =  UApi.convertStreamToString(inputStream);
            JSONObject jObj = new JSONObject(jString);

            int responseCode = httpResponse.getStatusLine().getStatusCode();

            Log.d("RETOUR API", ">>>"+responseCode+"---");
            activity.startActivity(paiement);
            activity.finish();
        } catch (IOException e) {
            progress.dismiss();
            e.printStackTrace();
        } catch (Exception e) {
            progress.dismiss();
            e.printStackTrace();
        }
    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.main_menu, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        return MenuLizz.main_menu(item, getBaseContext(), PayementPTPConfirmActivity.this);
    }
}