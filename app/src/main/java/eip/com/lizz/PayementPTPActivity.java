package eip.com.lizz;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.ContentUris;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.database.Cursor;
import android.net.Uri;
import android.os.Bundle;
import android.provider.ContactsContract;
import android.support.v7.app.ActionBarActivity;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Spinner;

import java.util.ArrayList;
import java.util.List;

import eip.com.lizz.Utils.UAlertBox;
import eip.com.lizz.Utils.UNetwork;


public class PayementPTPActivity extends ActionBarActivity {

    String name = "";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_payementptp);

        SharedPreferences sharedpreferences = getSharedPreferences("eip.com.lizz", Context.MODE_PRIVATE);
        boolean isLogged = sharedpreferences.getBoolean("eip.com.lizz.isLogged", false);

        if (isLogged) {

            final Button payementMethod = (Button) findViewById(R.id.paiementmethod);
            payementMethod.setOnClickListener(new View.OnClickListener() {
                public void onClick(View v) {
                    showPaiementMethodDialog(payementMethod);
                }
            });

            Button select = (Button) findViewById(R.id.select);
            select.setOnClickListener(new View.OnClickListener() {
                public void onClick(View v) {
                Intent intent = new Intent(Intent.ACTION_PICK, ContactsContract.Contacts.CONTENT_URI);
                startActivityForResult(intent, 1);
                }
            });

            Button next = (Button) findViewById(R.id.next);
            next.setOnClickListener(new View.OnClickListener() {
                public void onClick(View v) {
                    final EditText contact_input = (EditText) findViewById(R.id.contact);
                    EditText somme_input = (EditText) findViewById(R.id.somme);
                    final String contact_a_check = contact_input.getText().toString();
                    final String somme = somme_input.getText().toString();
                    final String id_payement = payementMethod();
                    final boolean isEmail = LoginActivity.isEmailValid(contact_a_check);
                    final boolean isPhone = isPhoneValid(contact_a_check);
                    if (contact_a_check.isEmpty())
                        UAlertBox.alertOk(PayementPTPActivity.this, getResources().getString(R.string.error), getResources().getString(R.string.error_contact_empty));
                    else if (somme.isEmpty())
                        UAlertBox.alertOk(PayementPTPActivity.this, getResources().getString(R.string.error), getResources().getString(R.string.error_somme_empty));
                    else if (!isEmail && !isPhone)
                        UAlertBox.alertOk(PayementPTPActivity.this, getResources().getString(R.string.error), getResources().getString(R.string.error_contact_ptp));
                    else if (id_payement.isEmpty())
                        UAlertBox.alertOk(PayementPTPActivity.this, getResources().getString(R.string.error), getResources().getString(R.string.error_id_payement));
                    else
                    {
                        final CharSequence[] choice = new CharSequence[2];
                        final boolean isInternet = UNetwork.checkInternetConnection(getBaseContext());
                        final boolean isMobile = UNetwork.isMobileAvailable(getBaseContext());
                        if (isInternet)
                            choice[0] = "Par Internet";
                        else
                            choice[0] = "Par Internet (Réseau indisponible)";
                        if (isMobile)
                            choice[1] = "Par SMS";
                        else
                            choice[1] = "Par SMS (Réseau indisponible)";
                        AlertDialog.Builder builder2 = new AlertDialog.Builder(PayementPTPActivity.this);
                        builder2.setTitle("Comment souhaitez-vous régler ?");
                        builder2.setItems(choice, new DialogInterface.OnClickListener() {

                            public void onClick(DialogInterface dialog, int which) {
                                Intent paiement = new Intent(getBaseContext(), PayementPTPConfirmActivity.class);
                                paiement.putExtra("contact", contact_a_check);
                                paiement.putExtra("somme", somme);
                                paiement.putExtra("isEmail", isEmail);
                                paiement.putExtra("isPhone", isPhone);
                                paiement.putExtra("idPayment", id_payement);
                                paiement.putExtra("isForced", false);
                                if (which == 0)
                                {
                                    if (isInternet) {
                                        paiement.putExtra("isInternet", true);
                                        startActivity(paiement);
                                    }
                                    else
                                        UAlertBox.alertOk(PayementPTPActivity.this, getResources().getString(R.string.dialog_title_no_internet), getResources().getString(R.string.dialog_no_internet));
                                }
                                else
                                {
                                    if (isMobile) {
                                        paiement.putExtra("isInternet", false);
                                        startActivity(paiement);
                                    }
                                    else
                                        UAlertBox.alertOk(PayementPTPActivity.this, getResources().getString(R.string.dialog_no_network), getResources().getString(R.string.error_network_phone));
                                }

                            }
                        });
                        AlertDialog alert = builder2.create();
                        alert.setOwnerActivity(PayementPTPActivity.this);
                        alert.show();
                    }
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

    private String payementMethod() {
            return "ID231KE2";
    }

    private void showPaiementMethodDialog(final Button payementMethod)
    {
        final CharSequence[] choice = new CharSequence[3];
        choice[0] = "Mastercard Guillaume Teillet 2345";
        choice[1] = "Paypal guillaume@lizz.com";
        choice[2] = "Chèque restaurant 10 euros";
        AlertDialog.Builder builder2 = new AlertDialog.Builder(PayementPTPActivity.this);
        builder2.setTitle("Séléctionner un moyen de paiement");
        builder2.setItems(choice, new DialogInterface.OnClickListener() {

            public void onClick(DialogInterface dialog, int which) {
                payementMethod.setText(choice[which]);
            }
        });
        AlertDialog alert = builder2.create();
        alert.setOwnerActivity(PayementPTPActivity.this);
        alert.show();
    }

    private boolean isPhoneValid(String contact_a_check) {

        if (!contact_a_check.matches("[0-9]+"))
        {
            return false;
        }
        if (contact_a_check.length() >= 10 && contact_a_check.length() <= 12)
        {
            if(contact_a_check.substring(0, 2).equals("33") || contact_a_check.substring(0, 2).equals("06") || contact_a_check.substring(0, 2).equals("07"))
                return true;
        }

        return false;
    }

    @Override
    public void onActivityResult(int reqCode, int resultCode, Intent data) {
        super.onActivityResult(reqCode, resultCode, data);
        if (resultCode == Activity.RESULT_OK) {
            Uri contactData = data.getData();
            Cursor c =  getContentResolver().query(contactData, null, null, null, null);
            int phoneIdx;
            if (c.moveToFirst()) {
                name = c.getString(c.getColumnIndexOrThrow(ContactsContract.Contacts.DISPLAY_NAME));
                Uri result = data.getData();
                String id = result.getLastPathSegment();

                boolean phone_multiple = false;
                boolean email_multiple = false;
                final String[] forbidden = {" ",",","#","$","%","&","+","(",")","*","\"","'",":",";","!","?","/","~","`","|","•","√","π","÷","×","¶","∆","}","{","=","°","^","¥","€","¢","£","\\","©","®","™","℅","[","]",">","<"};
                final String[] allowed = {"","","","","","","","","","","","","","","","","","","","","","","","","","","","","","","","","","","","","","","","","","","","",""};
                Cursor Cphone = getContentResolver().query(ContactsContract.CommonDataKinds.Phone.CONTENT_URI, null, ContactsContract.CommonDataKinds.Phone.CONTACT_ID +" = ?", new String[]{id}, null);
                Cursor emailCur = getContentResolver().query(ContactsContract.CommonDataKinds.Email.CONTENT_URI, null, ContactsContract.CommonDataKinds.Email.CONTACT_ID + " = ?", new String[]{id}, null);
                int nb_phone = Cphone.getCount();
                int nb_email = emailCur.getCount();
                final CharSequence[] contact = new CharSequence[Cphone.getCount() + emailCur.getCount()];
                int i=0;
                while (emailCur.moveToNext()) {
                    contact[i++] = emailCur.getString(emailCur.getColumnIndex(ContactsContract.CommonDataKinds.Email.DATA));
                    email_multiple = true;
                }
                emailCur.close();

                while(Cphone.moveToNext()) {
                    phoneIdx = Cphone.getColumnIndexOrThrow(ContactsContract.CommonDataKinds.Phone.NUMBER);
                    contact[i++] = Cphone.getString(phoneIdx);
                    phone_multiple = true;
                }
                Cphone.close();

                if ((nb_email + nb_phone) == 1)
                {
                    String contact_string = (String) contact[0];
                    EditText contact_input = (EditText) findViewById(R.id.contact);
                    for (int index =0; index < forbidden.length; index++){
                        contact_string = contact_string.replace(forbidden[index], allowed[index]);
                    }
                    contact_input.setText(contact_string);
                }
                else if ((nb_email + nb_phone) > 1)
                {
                    AlertDialog.Builder builder = new AlertDialog.Builder(this);

                    if (phone_multiple == true && email_multiple == true)
                        builder.setTitle(name+getResources().getString(R.string.label_phone_or_email));
                    else if (phone_multiple)
                        builder.setTitle(name+getResources().getString(R.string.label_phone));
                    else if (email_multiple)
                        builder.setTitle(name+getResources().getString(R.string.label_email));
                    builder.setItems(contact, new DialogInterface.OnClickListener() {

                        @Override
                        public void onClick(DialogInterface dialog, int item) {
                            String contact_string = (String) contact[item];
                            EditText contact_input = (EditText) findViewById(R.id.contact);
                            for (int index =0; index < forbidden.length; index++){
                                contact_string = contact_string.replace(forbidden[index], allowed[index]);
                            }
                            contact_input.setText(contact_string);
                        }
                    });
                    AlertDialog alert = builder.create();
                    alert.setOwnerActivity(this);
                    alert.show();
                }
                else
                {
                    EditText contact_input = (EditText) findViewById(R.id.contact);
                    contact_input.setText("");
                    UAlertBox.alertOk(PayementPTPActivity.this, getResources().getString(R.string.error), getResources().getString(R.string.error_contact));
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
        return MenuLizz.main_menu(item, getBaseContext(), PayementPTPActivity.this);
    }
}
