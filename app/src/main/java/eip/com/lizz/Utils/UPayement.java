package eip.com.lizz.Utils;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.graphics.Color;
import android.os.Bundle;
import android.text.InputType;
import android.text.method.PasswordTransformationMethod;
import android.util.Log;
import android.widget.EditText;

import org.apache.http.HttpResponse;

import java.io.IOException;
import java.security.InvalidKeyException;
import java.security.NoSuchAlgorithmException;
import java.security.NoSuchProviderException;
import java.security.spec.InvalidKeySpecException;

import javax.crypto.BadPaddingException;
import javax.crypto.IllegalBlockSizeException;
import javax.crypto.NoSuchPaddingException;

import eip.com.lizz.PayementPTPConfirmActivity;
import eip.com.lizz.PayementPTPFinishActivity;
import eip.com.lizz.Protocols.PED;
import eip.com.lizz.QueriesAPI.SendSMSToAPI;
import eip.com.lizz.QueriesAPI.SendTransactionPTPToAPI;
import eip.com.lizz.R;

/**
 * Created by guillaume on 03/04/15.
 */
public class UPayement {

    public static void popUpPIN(final Bundle bundle, final Activity activity, final Context context)
    {
        final SharedPreferences sharedpreferences = activity.getSharedPreferences("eip.com.lizz", Context.MODE_PRIVATE);

        final String id_payement_method = bundle.getString("idPayment");
        final String PIN = sharedpreferences.getString("eip.com.lizz.codepinlizz", "");
        final String email_sender = sharedpreferences.getString("eip.com.lizz.email", "");
        final String receiver = bundle.getString("contact");
        final String id_user = sharedpreferences.getString("eip.com.lizz.id_user", "");
        final String amount = bundle.getString("somme");
        try {
            tryToSecureMsg(id_payement_method, PIN, email_sender, receiver, id_user, amount, bundle, sharedpreferences, activity, context);
        } catch (IOException | NoSuchAlgorithmException | InvalidKeyException | PackageManager.NameNotFoundException | InvalidKeySpecException | NoSuchPaddingException | BadPaddingException | NoSuchProviderException | IllegalBlockSizeException e) {
            AlertDialog.Builder alert;
            alert = UAlertBox.alert(activity, activity.getResources().getString(R.string.error), activity.getResources().getString(R.string.error_rsa_key));
            final ProgressDialog progress = new ProgressDialog(activity);
            alert.setPositiveButton(activity.getResources().getString(R.string.dialog_yes), new DialogInterface.OnClickListener() {
                public void onClick(DialogInterface dialog, int whichButton) {
                    new Thread(new Runnable() {
                        @Override
                        public void run()
                        {
                            activity.runOnUiThread(new Runnable() {
                                public void run() {

                                    progress.setTitle(activity.getResources().getString(R.string.dialog_download));
                                    progress.setMessage(activity.getResources().getString(R.string.dialog_download_rsa_key));
                                    progress.setCancelable(false);
                                    progress.show();
                                }
                            });
                            String isOk;
                            isOk = UDownload.downloadFile("http://test-ta-key.lizz.fr/lastKey.key", "keyrsa.pub", context);
                            if (isOk.equals("ok"))
                            {
                                progress.dismiss();
                                activity.finish();
                                Intent paiement = new Intent(context, PayementPTPConfirmActivity.class);
                                paiement.putExtra("contact", receiver);
                                paiement.putExtra("somme", amount);
                                paiement.putExtra("isEmail", bundle.getBoolean("isEmail"));
                                paiement.putExtra("isPhone", bundle.getBoolean("isPhone"));
                                paiement.putExtra("idPayment", id_payement_method);
                                paiement.putExtra("isForced", true);
                                activity.startActivity(paiement);
                            }
                        }
                    }).start();
                }
            });
            alert.setNegativeButton(activity.getResources().getString(R.string.dialog_cancel), new DialogInterface.OnClickListener() {
                public void onClick(DialogInterface dialog, int whichButton) {
                    UAlertBox.alertOk(activity, activity.getResources().getString(R.string.error), activity.getResources().getString(R.string.error_rsa_key_cancel));
                }
            });
            alert.show();
        }
    }

    public static void tryToSecureMsg(String id_payement_method, final String PIN, String email_sender, String receiver, String id_user, String amount, final Bundle bundle, final SharedPreferences sharedpreferences, final Activity activity, final Context context) throws IOException, NoSuchAlgorithmException, NoSuchProviderException, InvalidKeyException, PackageManager.NameNotFoundException, NoSuchPaddingException, BadPaddingException, InvalidKeySpecException, IllegalBlockSizeException {
        final String token = PED.cryptped(id_payement_method, PIN, email_sender, receiver, id_user, amount, context, activity);
        final Intent paiement = new Intent(context, PayementPTPFinishActivity.class);
        paiement.putExtra("somme", amount);
        paiement.putExtra("contact", bundle.getString("contact"));
        paiement.putExtra("isEmail", bundle.getBoolean("isEmail"));
        paiement.putExtra("isPhone", bundle.getBoolean("isPhone"));
        paiement.putExtra("isInternet", bundle.getBoolean("isInternet"));
        final EditText input = new EditText(context);
        input.setInputType(InputType.TYPE_CLASS_NUMBER | InputType.TYPE_TEXT_VARIATION_PASSWORD);
        input.setTransformationMethod(PasswordTransformationMethod.getInstance());
        input.setTextColor(Color.BLACK);
        final AlertDialog.Builder alert = UAlertBox.alertInputOk(activity, context.getResources().getString(R.string.dialog_title_confirm), context.getResources().getString(R.string.dialog_confirm_pin), input);
        alert.setPositiveButton(context.getResources().getString(R.string.dialog_ok), new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int whichButton) {
                confirmTransaction(input, token, paiement, PIN, sharedpreferences, bundle.getBoolean("isInternet"), activity, context);
            }

        });
        alert.setNegativeButton(context.getResources().getString(R.string.dialog_cancel), null);
        alert.show();
    }

    public static void confirmTransaction(EditText input, String token, Intent paiement, String PIN, SharedPreferences sharedpreferences, Boolean isInternet, Activity activity, Context context)
    {
        if (input.getText().toString().equals(PIN))
        {
            sharedpreferences.edit().putInt("eip.com.lizz.tentativePin", 0).apply();
            if (context.getResources().getString(R.string.debugOrProd).equals("PROD"))
            {
                if (isInternet)
                {
                    Log.d("DEBUG MODE", "PAIEMENT INTERNET");
                    sendTransactionToAPI(token, sharedpreferences, paiement, activity, context);
                }
                else
                {
                    int err = SendSMSToAPI.send(token);
                    if (err == 0)
                    {
                        activity.startActivity(paiement);
                        activity.finish();
                    }
                    else
                    {
                        UAlertBox.alertOk(activity, context.getResources().getString(R.string.error), context.getResources().getString(R.string.error_send_sms));
                    }
                }
            }
            else
            {
                if (isInternet)
                {
                    Log.d("DEBUG MODE", "PAIEMENT PED INTERNET >>>>" + token);
                    sendTransactionToAPI(token, sharedpreferences, paiement, activity, context);
                }
                else {
                    Log.d("DEBUG MODE", "PAIEMENT PED SMS >>>>" + token);
                    activity.startActivity(paiement);
                    activity.finish();
                }
            }
        }
        else
        {
            USaveParams.tentativeCheck(activity, sharedpreferences);
        }
    }

    private static void sendTransactionToAPI(final String token, final SharedPreferences sharedpreferences, final Intent paiement, final Activity activity, final Context context) {
        final ProgressDialog progress = ProgressDialog.show(activity, context.getResources().getString(R.string.pleasewait), context.getResources().getString(R.string.pleasewaitTransaction), true);
        new Thread(new Runnable() {
            @Override
            public void run()
            {
                activity.runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        SendTransactionPTPToAPI mAuthTask = new SendTransactionPTPToAPI(sharedpreferences.getString("eip.com.lizz._csrf", ""), context, token);
                        mAuthTask.setOnTaskFinishedEvent(new SendTransactionPTPToAPI.OnTaskExecutionFinished() {

                            @Override
                            public void OnTaskFihishedEvent(HttpResponse httpResponse) {
                                PayementPTPConfirmActivity.dataAPI(progress, httpResponse, paiement, activity, context);
                            }
                        });
                        mAuthTask.execute();
                    }
                });
            }
        }).start();
    }
}
