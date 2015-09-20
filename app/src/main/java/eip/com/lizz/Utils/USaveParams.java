package eip.com.lizz.Utils;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.text.InputType;
import android.text.method.PasswordTransformationMethod;
import android.widget.EditText;

import eip.com.lizz.MenuLizz;
import eip.com.lizz.R;
import eip.com.lizz.Utils.UAlertBox;

/**
 * Created by guillaume on 12/12/14.
 */
public class USaveParams {

    public static void checkIsForChangePinOrNot(final Boolean isForChangePIN, final Activity context, final String key, final String params)
    {
        final SharedPreferences sharedpreferences = context.getSharedPreferences("eip.com.lizz", Context.MODE_PRIVATE);
        final String codePin = sharedpreferences.getString("eip.com.lizz.codepinlizz", "");
        final EditText input = new EditText(context);
        input.setInputType(InputType.TYPE_CLASS_NUMBER | InputType.TYPE_TEXT_VARIATION_PASSWORD);
        input.setTransformationMethod(PasswordTransformationMethod.getInstance());
        input.setTextColor(Color.BLACK);
        final AlertDialog.Builder alert;
        if (isForChangePIN)
            alert = UAlertBox.alertInputOk(context, context.getResources().getString(R.string.dialog_title_confirm), context.getResources().getString(R.string.dialog_confirm_hint_pin_change), input);
        else
            alert = UAlertBox.alertInputOk(context, context.getResources().getString(R.string.dialog_title_confirm), context.getResources().getString(R.string.dialog_confirm_hint_pin), input);
        alert.setPositiveButton(context.getResources().getString(R.string.dialog_ok), new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int whichButton) {
                if (input.getText().toString().equals(codePin))
                {
                     saveParamsString(context, key, params);
                     sharedpreferences.edit().putInt("eip.com.lizz.tentativePin", 0).apply();
                }
                else
                    tentativeCheck(context, sharedpreferences);
            }

        });
        alert.setNegativeButton(context.getResources().getString(R.string.dialog_cancel), null);
        alert.show();
    }

    public static void tentativeCheck(Activity context, SharedPreferences sharedpreferences)
    {
        int tentatives = sharedpreferences.getInt("eip.com.lizz.tentativePin", 0);
        int new_tentative = tentatives + 1;
        sharedpreferences.edit().putInt("eip.com.lizz.tentativePin", new_tentative).apply();
        if (new_tentative == 1)
            displayError(2, context, "2", null, true);
        else if (new_tentative == 2)
            displayError(2, context, "1", null, true);
        else if (new_tentative == 3)
        {
            sharedpreferences.edit().putInt("eip.com.lizz.tentativePin", 0).apply();
            MenuLizz.logout(context);
        }
    }

    public static void saveParamsString(Activity context, String key, String value) {
        SharedPreferences sharedpreferences = context.getSharedPreferences("eip.com.lizz", Context.MODE_PRIVATE);
        sharedpreferences.edit().putString(key, value).apply();
        context.finish();
    }

    public static void saveParamsBoolean(Activity context, String key, Boolean value) {
        SharedPreferences sharedpreferences = context.getSharedPreferences("eip.com.lizz", Context.MODE_PRIVATE);
        sharedpreferences.edit().putBoolean(key, value).apply();
        context.finish();
    }

    // DEV : Merci d'utiliser les codes d'erreur libre avant d'en créer de nouveau...

    public static void displayError(int i, final Activity context, final String params, final String key, final boolean isForChangePIN) {

        switch (i)
        {
            case 1:
                UAlertBox.alertOk(context, context.getResources().getString(R.string.error), context.getResources().getString(R.string.error_new_pincode));
                break;
            case 2:
                UAlertBox.alertOk(context, context.getResources().getString(R.string.error_tentatives), context.getResources().getString(R.string.error_nb_tentatives) + " " + params + " " + context.getResources().getString(R.string.tentatives));
                break;
            case 3:
                break;
            case 4:
                break;
            case 5:
                UAlertBox.alertOk(context, context.getResources().getString(R.string.error), context.getResources().getString(R.string.wrongPassword));
                break;
            case 6:
                UAlertBox.alertOk(context, context.getResources().getString(R.string.error), context.getResources().getString(R.string.emptyPasswordPIN));
                break;
            case 7:
                UAlertBox.alertOk(context, context.getResources().getString(R.string.error), context.getResources().getString(R.string.wrongRIB));
                break;
            case 8:
                UAlertBox.alertOk(context, context.getResources().getString(R.string.error), context.getResources().getString(R.string.IBANisNotFrench));
                break;
            case 9 :
                UAlertBox.alertOk(context, context.getResources().getString(R.string.error), context.getResources().getString(R.string.error_pin));
                break;
            default:
                UAlertBox.alertOk(context, context.getResources().getString(R.string.error), context.getResources().getString(R.string.errordefault));
                break;

        }
    }
}
