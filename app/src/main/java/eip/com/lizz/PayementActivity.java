package eip.com.lizz;

import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v7.app.ActionBarActivity;
import android.telephony.TelephonyManager;
import android.text.InputType;
import android.text.method.PasswordTransformationMethod;
import android.util.Base64;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.webkit.WebView;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import org.apache.http.HttpResponse;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.io.InputStream;
import java.io.UnsupportedEncodingException;
import java.net.URL;
import java.security.InvalidKeyException;
import java.security.NoSuchAlgorithmException;
import java.security.NoSuchProviderException;
import java.security.spec.InvalidKeySpecException;
import java.text.DecimalFormat;

import javax.crypto.BadPaddingException;
import javax.crypto.IllegalBlockSizeException;
import javax.crypto.NoSuchPaddingException;

import eip.com.lizz.Protocols.PCC;
import eip.com.lizz.Protocols.PED;
import eip.com.lizz.QueriesAPI.SendSMSToAPI;
import eip.com.lizz.QueriesAPI.SendTransactionPTPToAPI;
import eip.com.lizz.QueriesAPI.SendTransactionToAPI;
import eip.com.lizz.Utils.UAlertBox;
import eip.com.lizz.Utils.UApi;
import eip.com.lizz.Utils.UDownload;
import eip.com.lizz.Utils.UNetwork;
import eip.com.lizz.Utils.USaveParams;


public class PayementActivity extends ActionBarActivity {

    String shopNameString = "", amount = "0", unique_code = "", data = "", type_qrcode = "";
    String[] sommeTxt;
    double total = 0;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_payement);

        final SharedPreferences sharedpreferences = getSharedPreferences("eip.com.lizz", Context.MODE_PRIVATE);
        boolean isLogged = sharedpreferences.getBoolean("eip.com.lizz.isLogged", false);

        if (isLogged) {

            final Bundle bundle = getIntent().getExtras();
            if (bundle != null) {
                if (bundle.getString("unique_code") != null) {
                    unique_code = bundle.getString("unique_code");
                } else {
                    Toast.makeText(getBaseContext(), getResources().getString(R.string.no_unique_code), Toast.LENGTH_LONG).show();
                    finish();
                }
                if (bundle.getString("data") != null) {
                    data = bundle.getString("data");
                    Log.d("TEST", data);
                    try {
                        byte[] data_bytes = Base64.decode(data, Base64.DEFAULT);
                        String url = new String(data_bytes, "UTF-8");
                        Log.d(">>>>", url);
                        String[] params = url.split("&");

                        String[] nom = params[0].split("=");
                        sommeTxt = params[2].split("=");

                        TextView shopName = (TextView) findViewById(R.id.name_shop);
                        shopName.setText(nom[1]);


                    } catch (UnsupportedEncodingException e) {
                        e.printStackTrace();
                    }
                } else {
                    Toast.makeText(getBaseContext(), getResources().getString(R.string.no_data), Toast.LENGTH_LONG).show();
                    finish();
                }
            }
            else {
                Toast.makeText(getBaseContext(), getResources().getString(R.string.no_unique_code), Toast.LENGTH_LONG).show();
                finish();
            }

            final Button payementMethod = (Button) findViewById(R.id.paiementmethod);
            payementMethod.setOnClickListener(new View.OnClickListener() {
                public void onClick(View v) {
                    showPaiementMethodDialog(payementMethod);
                }
            });

            final Button address = (Button) findViewById(R.id.address);
            address.setOnClickListener(new View.OnClickListener() {
                public void onClick(View v) {
                    showAddressDialog(address);
                }
            });

            Button next = (Button) findViewById(R.id.payer);
            next.setText("Payer " + sommeTxt[1] + " €");
            next.setOnClickListener(new View.OnClickListener() {
                public void onClick(View v) {

                   /* if (contact_a_check.isEmpty())
                        UAlertBox.alertOk(PayementPTPActivity.this, getResources().getString(R.string.error), getResources().getString(R.string.error_contact_empty));
                    else if (somme.isEmpty())
                        UAlertBox.alertOk(PayementPTPActivity.this, getResources().getString(R.string.error), getResources().getString(R.string.error_somme_empty));
                    else if (!isEmail && !isPhone)
                        UAlertBox.alertOk(PayementPTPActivity.this, getResources().getString(R.string.error), getResources().getString(R.string.error_contact_ptp));
                    else if (id_payement.isEmpty())
                        UAlertBox.alertOk(PayementPTPActivity.this, getResources().getString(R.string.error), getResources().getString(R.string.error_id_payement));
                    else
                    {*/

                        /*String id_payement = "12";
                        String PIN = "4825";
                        String email_user = "jean.dupont@gmail.com";
                      //  unique_code = "3JS2B43N21";
                        String id_user = "132";*/

                    checkNetwork(bundle);
                    Log.d("PAIEMENT OK", "Paiement okk");
                    //}
                }
            });
        } else {
            Intent loggedUser = new Intent(getBaseContext(), HomeActivity.class);
            loggedUser.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK | Intent.FLAG_ACTIVITY_NO_ANIMATION | Intent.FLAG_ACTIVITY_NEW_TASK);
            startActivity(loggedUser);
        }
    }

    private void checkNetwork(Bundle bundle) {
        if (!UNetwork.checkInternetConnection(getBaseContext()))
        {
            final AlertDialog.Builder alert = UAlertBox.alert(PayementActivity.this, getResources().getString(R.string.dialog_no_internet), getResources().getString(R.string.dialog_no_internet_label_sms));
            alert.setPositiveButton(getResources().getString(R.string.dialog_bysms), new DialogInterface.OnClickListener() {
                public void onClick(DialogInterface dialog, int whichButton) {
                    Intent payement = new Intent(getBaseContext(), PayementSMSActivity.class);
                    payement.putExtra("unique_code", unique_code);
                    startActivity(payement);
                    finish();
                }
            });
            alert.setNegativeButton(getResources().getString(R.string.dialog_cancel), new DialogInterface.OnClickListener() {

                @Override
                public void onClick(DialogInterface dialog, int which) {
                    Toast.makeText(getBaseContext(), getResources().getString(R.string.error_transaction_cancel), Toast.LENGTH_LONG).show();
                    finish();
                }
            });
            alert.show();
        }
        else
        {
          //  popUpPIN(bundle);
            pay(bundle);
        }
    }

    public void pay(final Bundle bundle)
    {
        final SharedPreferences sharedpreferences = getSharedPreferences("eip.com.lizz", Context.MODE_PRIVATE);

        final String id_payement_method = "1"; //payementMethodId();
        final String PIN = sharedpreferences.getString("eip.com.lizz.codepinlizz", "");
        final String receiver = bundle.getString("unique_code");

        Log.d("DEBUG", "-->"+id_payement_method+"--"+PIN+"--"+receiver);
        final Intent paiement = new Intent(getBaseContext(), PayementPTPFinishActivity.class);
        paiement.putExtra("somme", amount);
        paiement.putExtra("contact", bundle.getString("contact"));
        paiement.putExtra("isEmail", bundle.getBoolean("isEmail"));
        paiement.putExtra("isPhone", bundle.getBoolean("isPhone"));
        paiement.putExtra("isInternet", bundle.getBoolean("isInternet"));
        final EditText input = new EditText(getBaseContext());
        input.setInputType(InputType.TYPE_CLASS_NUMBER | InputType.TYPE_TEXT_VARIATION_PASSWORD);
        input.setTransformationMethod(PasswordTransformationMethod.getInstance());
        input.setTextColor(Color.BLACK);
        final AlertDialog.Builder alert = UAlertBox.alertInputOk(PayementActivity.this, getResources().getString(R.string.dialog_title_confirm), getResources().getString(R.string.dialog_confirm_pin), input);
        alert.setPositiveButton(getResources().getString(R.string.dialog_ok), new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int whichButton) {
                //confirmTransaction(input, token, paiement, PIN, sharedpreferences);
                confirmTransactionKomyla(input, paiement, PIN, sharedpreferences);
            }

        });
        alert.setNegativeButton(getResources().getString(R.string.dialog_cancel), null);
        alert.show();
    }

    public void popUpPIN(final Bundle bundle)
    {
        final SharedPreferences sharedpreferences = getSharedPreferences("eip.com.lizz", Context.MODE_PRIVATE);

        final String id_payement_method = payementMethodId();
        final String PIN = sharedpreferences.getString("eip.com.lizz.codepinlizz", "");
        final String email_sender = sharedpreferences.getString("eip.com.lizz.email", "");
        final String receiver = unique_code;
        final String id_user = sharedpreferences.getString("eip.com.lizz.id_user", "");
        try {
            tryToSecureMsg(id_payement_method, PIN, email_sender, receiver, id_user, amount, bundle, sharedpreferences);
        } catch (IOException | NoSuchAlgorithmException | InvalidKeyException | PackageManager.NameNotFoundException | InvalidKeySpecException | NoSuchPaddingException | BadPaddingException | NoSuchProviderException | IllegalBlockSizeException e) {
            AlertDialog.Builder alert;
            alert = UAlertBox.alert(PayementActivity.this, getResources().getString(R.string.error), getResources().getString(R.string.error_rsa_key));
            final ProgressDialog progress = new ProgressDialog(PayementActivity.this);
            alert.setPositiveButton(getResources().getString(R.string.dialog_yes), new DialogInterface.OnClickListener() {
                public void onClick(DialogInterface dialog, int whichButton) {
                    new Thread(new Runnable() {
                        @Override
                        public void run()
                        {
                            runOnUiThread(new Runnable() {
                                public void run() {

                                    progress.setTitle(getResources().getString(R.string.dialog_download));
                                    progress.setMessage(getResources().getString(R.string.dialog_download_rsa_key));
                                    progress.setCancelable(false);
                                    progress.show();
                                }
                            });
                            String isOk;
                            isOk = UDownload.downloadFile("http://test-ta-key.lizz.fr/lastKey.key", "keyrsa.pub", getBaseContext());
                            if (isOk.equals("ok"))
                            {
                                progress.dismiss();
                                finish();
                                /*Intent paiement = new Intent(getBaseContext(), PayementPTPConfirmActivity.class);
                                paiement.putExtra("contact", receiver);
                                paiement.putExtra("somme", amount);
                                paiement.putExtra("isEmail", bundle.getBoolean("isEmail"));
                                paiement.putExtra("isPhone", bundle.getBoolean("isPhone"));
                                paiement.putExtra("idPayment", id_payement_method);
                                paiement.putExtra("isForced", true);
                                startActivity(paiement);*/
                                Log.d("FINISH", "FINISH PAYER");
                            }
                        }
                    }).start();
                }
            });
            alert.setNegativeButton(getResources().getString(R.string.dialog_cancel), new DialogInterface.OnClickListener() {
                public void onClick(DialogInterface dialog, int whichButton) {
                    UAlertBox.alertOk(PayementActivity.this, getResources().getString(R.string.error), getResources().getString(R.string.error_rsa_key_cancel));
                }
            });
            alert.show();
        }
    }

    public void tryToSecureMsg(String id_payement_method, final String PIN, String email_sender, String unique_code, String id_user, String amount, final Bundle bundle, final SharedPreferences sharedpreferences) throws IOException, NoSuchAlgorithmException, NoSuchProviderException, InvalidKeyException, PackageManager.NameNotFoundException, NoSuchPaddingException, BadPaddingException, InvalidKeySpecException, IllegalBlockSizeException {
        final String token = PCC.cryptped(id_payement_method, PIN, email_sender, unique_code, id_user, amount, getBaseContext(), PayementActivity.this);
        final Intent paiement = new Intent(getBaseContext(), PayementPTPFinishActivity.class);
        paiement.putExtra("somme", amount);
        paiement.putExtra("contact", bundle.getString("contact"));
        paiement.putExtra("isEmail", bundle.getBoolean("isEmail"));
        paiement.putExtra("isPhone", bundle.getBoolean("isPhone"));
        paiement.putExtra("isInternet", bundle.getBoolean("isInternet"));
        final EditText input = new EditText(getBaseContext());
        input.setInputType(InputType.TYPE_CLASS_NUMBER | InputType.TYPE_TEXT_VARIATION_PASSWORD);
        input.setTransformationMethod(PasswordTransformationMethod.getInstance());
        input.setTextColor(Color.BLACK);
        final AlertDialog.Builder alert = UAlertBox.alertInputOk(PayementActivity.this, getResources().getString(R.string.dialog_title_confirm), getResources().getString(R.string.dialog_confirm_pin), input);
        alert.setPositiveButton(getResources().getString(R.string.dialog_ok), new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int whichButton) {
                confirmTransaction(input, token, paiement, PIN, sharedpreferences);
            }

        });
        alert.setNegativeButton(getResources().getString(R.string.dialog_cancel), null);
        alert.show();
    }

    public void confirmTransaction(EditText input, String token, Intent paiement, String PIN, SharedPreferences sharedpreferences)
    {
        if (input.getText().toString().equals(PIN))
        {
            sharedpreferences.edit().putInt("eip.com.lizz.tentativePin", 0).apply();
            Log.d("DEBUG MODE", "PAIEMENT INTERNET");
            sendTransactionToAPI(token, sharedpreferences, paiement);
        }
        else
        {
            USaveParams.tentativeCheck(PayementActivity.this, sharedpreferences);
        }
    }

    public void confirmTransactionKomyla(EditText input, Intent paiement, String PIN, SharedPreferences sharedpreferences)
    {
        if (input.getText().toString().equals(PIN))
        {
            sharedpreferences.edit().putInt("eip.com.lizz.tentativePin", 0).apply();
            Log.d("DEBUG MODE", "PAIEMENT INTERNET");
            sendTransactionToAPIKomyla(sharedpreferences, paiement, unique_code);
        }
        else
        {
            USaveParams.tentativeCheck(PayementActivity.this, sharedpreferences);
        }
    }

    private void sendTransactionToAPIKomyla(final SharedPreferences sharedpreferences, final Intent paiement, final String code) {
        final ProgressDialog progress = ProgressDialog.show(PayementActivity.this, getResources().getString(R.string.pleasewait), getResources().getString(R.string.pleasewaitTransaction), true);
        new Thread(new Runnable() {
            @Override
            public void run()
            {
                runOnUiThread(new Runnable() {
                    @Override
                    public void run()
                    {
                        SendTransactionToAPI mAuthTask = new SendTransactionToAPI(sharedpreferences.getString("eip.com.lizz._csrf", ""), getBaseContext(), code, "1");
                        mAuthTask.setOnTaskFinishedEvent(new SendTransactionToAPI.OnTaskExecutionFinished() {

                            @Override
                            public void OnTaskFihishedEvent(HttpResponse httpResponse) {
                                dataAPI(progress, httpResponse, paiement);
                            }
                        });
                        mAuthTask.execute();
                    }
                });
            }
        }).start();
    }

    private void sendTransactionToAPI(final String token, final SharedPreferences sharedpreferences, final Intent paiement) {
        final ProgressDialog progress = ProgressDialog.show(PayementActivity.this, getResources().getString(R.string.pleasewait), getResources().getString(R.string.pleasewaitTransaction), true);
        new Thread(new Runnable() {
            @Override
            public void run()
            {
                runOnUiThread(new Runnable() {
                    @Override
                    public void run()
                    {
                        SendTransactionPTPToAPI mAuthTask = new SendTransactionPTPToAPI(sharedpreferences.getString("eip.com.lizz._csrf", ""), getBaseContext(), token);
                        mAuthTask.setOnTaskFinishedEvent(new SendTransactionPTPToAPI.OnTaskExecutionFinished() {

                            @Override
                            public void OnTaskFihishedEvent(HttpResponse httpResponse) {
                                dataAPI(progress, httpResponse, paiement);
                            }
                        });
                        mAuthTask.execute();
                    }
                });
            }
        }).start();
    }


    private void dataAPI(ProgressDialog progress, HttpResponse httpResponse, Intent paiement)
    {
        InputStream inputStream = null;
        try {
            progress.dismiss();
            inputStream = httpResponse.getEntity().getContent();
            String jString =  UApi.convertStreamToString(inputStream);
//            JSONObject jObj = new JSONObject(jString);

            int responseCode = httpResponse.getStatusLine().getStatusCode();

            Log.d("RETOUR API", ">>>=====" + data + "----" + responseCode + "---" + jString);
            paiement.putExtra("data", data);
            startActivity(paiement);
            finish();
        } catch (IOException e) {
            progress.dismiss();
            e.printStackTrace();
        } catch (Exception e) {
            progress.dismiss();
            e.printStackTrace();
        }
    }

    private String payementMethodId() {
        return "12";
    }

    /*void dataAPI(String jObjString) throws JSONException {
        JSONObject jObj = new JSONObject(jObjString);
        if(jObj.has(getResources().getString(R.string.api_checkout_picture)))
        {
            new LoadImage().execute(jObj.getString(getResources().getString(R.string.api_checkout_picture)));
        }
        if (jObj.has(getResources().getString(R.string.api_checkout_shopname)))
        {
            TextView shopName = (TextView) findViewById(R.id.name_shop);
            shopNameString = jObj.getString(getResources().getString(R.string.api_checkout_shopname));
            shopName.setText(shopNameString);
        }
        if (jObj.has(getResources().getString(R.string.api_checkout_transaction)))
        {
            JSONObject jObj2 = new JSONObject(jObj.getString(getResources().getString(R.string.api_checkout_transaction)));
            if (jObj2.has(getResources().getString(R.string.api_checkout_amount)))
            {
                Button payer = (Button) findViewById(R.id.payer);
                amount = jObj2.getString(getResources().getString(R.string.api_checkout_amount));
                payer.setText(getResources().getString(R.string.label_payer)+ " " + amount +" €");
            }
        }
        if (jObj.has(getResources().getString(R.string.api_checkout_products)))
        {
            JSONArray jObj2 = new JSONArray(jObj.getString(getResources().getString(R.string.api_checkout_products)));
            //createPreviewTicket(jObj2, jObj);
        }
    }*/

   /* public void createPreviewTicket(JSONArray jObj2, JSONObject jObj) throws JSONException {
        WebView ticket = (WebView) findViewById(R.id.ticket);
        String Sticket = "<html><body style='margin:0px; margin-left:1px;'><table style='width:100%;'><tbody>";
        Sticket += "<tr><td style='text-align:left;'><b>"+getResources().getString(R.string.ticket_designation)+"</b></td><td style='text-align:right;'><b>"+getResources().getString(R.string.ticket_prix)+"</b></td><td style='text-align:right;'><b>"+getResources().getString(R.string.ticket_total)+"</b></td></tr>" ;

        DecimalFormat df = new DecimalFormat("###.##");
        double sousTotal = 0;
        total = 0;
        double reduction = 0;
        String devise = "€";
        for(int i=0; i<jObj2.length(); i++){
            JSONObject json_data = jObj2.getJSONObject(i);

            double quantity = json_data.getDouble("quantity");
            double price = json_data.getDouble("price");
            double total_product = quantity*price;
            String name_product = json_data.getString("name");
            sousTotal += total_product;
            reduction = 10;
            total = sousTotal - reduction;

            Sticket += "<tr><td style='text-align:left;'>"+String.valueOf(df.format(quantity))+" x "+name_product+"</td><td style='text-align:right;'>"+String.valueOf(df.format(price))+" "+devise+"</td><td style='text-align:right;'>"+String.valueOf(df.format(total_product))+" "+devise+"</td></tr>" ;

        }
        Sticket += "<tr><td><br/>"+getResources().getString(R.string.ticket_sous_total)+" :</td><td></td><td style='text-align: right;'><br/>"+String.valueOf(df.format(sousTotal))+" "+devise+"</td>";

        Sticket += "<tr><td>Remise patate</td><td></td><td style='text-align: right;'>- "+String.valueOf(df.format(reduction))+" "+devise+"</td>";

        Sticket += "<tr><td><span style='font-size: 25px;'><b>"+getResources().getString(R.string.ticket_total_ttc)+" :</b></span></td><td></td><td style='text-align: right;'><span style='font-size: 25px;'><b>"+String.valueOf(df.format(total))+" "+devise+"</b></span></td>";
        Sticket += "</tbody></table><br/>";
        Sticket += "<table style='width:100%; text-align:center;'><tbody><tr><td><b>"+getResources().getString(R.string.ticket_taux)+"</b></td><td><b>"+getResources().getString(R.string.ticket_ht)+"</b></td><td><b>"+getResources().getString(R.string.ticket_tva)+"</b></td><td><b>"+getResources().getString(R.string.ticket_ttc)+"</b></td></tr>";

        Sticket += "<tr><td>5.50 %</td><td>3.30</td><td>0.40</td><td>3.70</td></tr>";
        Sticket += "<tr><td>21 %</td><td>17.00</td><td>3.60</td><td>20.60</td></tr>";

        Sticket += "<tr><td><b>"+getResources().getString(R.string.ticket_total_maj)+"</b></td><td>20.30</td><td>4.00</td><td>24.30</td></tr>";
        Sticket += "</tbody></table>";
        Sticket += "<p>"+getResources().getString(R.string.ticket_by)+" : Charlotte<br/>"+getResources().getString(R.string.ticket_moyen_de_paiement)+" : "+getResources().getString(R.string.app_name)+"<br/><br/>"+getResources().getString(R.string.ticket_date)+" : ";
        if (jObj.has(getResources().getString(R.string.api_checkout_transaction))) {
            JSONObject obj = new JSONObject(jObj.getString(getResources().getString(R.string.api_checkout_transaction)));
            if (obj.has(getResources().getString(R.string.api_checkout_date))) {
                String dateBrut = obj.getString(getResources().getString(R.string.api_checkout_date));
                String[] dateAndHeure = dateBrut.split("T");
                String[] date = dateAndHeure[0].split("-");
                String[] heure = dateAndHeure[1].split(":");
                String[] sec = heure[2].split("\\.");
                Sticket += date[2]+"/"+date[1]+"/"+date[0];
                Sticket += "<br/>"+getResources().getString(R.string.ticket_heure)+" : ";
                Sticket += heure[0]+":"+heure[1]+":"+sec[0];
            }
        }
        Sticket += "<br/>"+getResources().getString(R.string.ticket_ticket)+" : 0000001<br/>"+getResources().getString(R.string.ticket_magasin)+" : 29342</p><p style='text-align:center'>MERCI DE VOTRE VISITE<br/>A BIENTÔT</p></body></html>";
        ticket.loadData(Sticket, "text/html; charset=UTF-8", null);
        ticket.setBackgroundColor(Color.TRANSPARENT);
        ticket.setHapticFeedbackEnabled(false);
        ticket.setOnLongClickListener(new View.OnLongClickListener() {
            @Override
            public boolean onLongClick(View v) {
                return true;
            }
        });
        ticket.setLongClickable(false);
    }*/

    private void showPaiementMethodDialog(final Button payementMethod)
    {
        final CharSequence[] choice = new CharSequence[3];
        choice[0] = "Mastercard Guillaume Teillet 2345";
        choice[1] = "Paypal guillaume@lizz.com";
        choice[2] = "Chèque restaurant 10 euros";
        AlertDialog.Builder builder2 = new AlertDialog.Builder(PayementActivity.this);
        builder2.setTitle("Sélectionner un moyen de paiement");
        builder2.setItems(choice, new DialogInterface.OnClickListener() {

            public void onClick(DialogInterface dialog, int which) {
                payementMethod.setText(choice[which]);
            }
        });
        AlertDialog alert = builder2.create();
        alert.setOwnerActivity(PayementActivity.this);
        alert.show();
    }

    private void showAddressDialog(final Button address)
    {
        final CharSequence[] choice = new CharSequence[3];
        choice[0] = "Domicile - Nantes";
        choice[1] = "Bureau - Paris";
        choice[2] = "Bureau - Nantes";
        AlertDialog.Builder builder2 = new AlertDialog.Builder(PayementActivity.this);
        builder2.setTitle("Sélectionner une adresse");
        builder2.setItems(choice, new DialogInterface.OnClickListener() {

            public void onClick(DialogInterface dialog, int which) {
                address.setText(choice[which]);
            }
        });
        AlertDialog alert = builder2.create();
        alert.setOwnerActivity(PayementActivity.this);
        alert.show();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.main_menu, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        return MenuLizz.main_menu(item, getBaseContext(), PayementActivity.this);
    }

    public class LoadImage extends AsyncTask<String, String, Bitmap> {
        @Override
        protected void onPreExecute() {
            super.onPreExecute();
        }
        protected Bitmap doInBackground(String... args) {
            Bitmap bitmap = null;
            try {
                bitmap = BitmapFactory.decodeStream((InputStream) new URL(args[0]).getContent());
            } catch (Exception e) {
                e.printStackTrace();
            }
            return bitmap;
        }
        protected void onPostExecute(Bitmap image) {
            if(image != null){
                ImageView img = (ImageView) findViewById(R.id.picture);
                img.setImageBitmap(image);
            }
        }
    }
}
