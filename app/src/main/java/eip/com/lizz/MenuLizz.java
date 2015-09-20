package eip.com.lizz;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Handler;
import android.view.MenuItem;

import org.apache.http.client.CookieStore;

import eip.com.lizz.Utils.UAlertBox;

/**
 * Created by guillaume on 16/12/14.
 */
public class MenuLizz {

    static CookieStore cookieStore;
    static ProgressDialog dialog;
    static Handler handler = null;

    public static boolean main_menu(MenuItem item, Context context, Activity ctx)
    {
        int id = item.getItemId();
        if (id == R.id.action_params) {
            return settings(context);
        }
        if (id == R.id.action_signout) {
            return signout(context, ctx);
        }
        return false;
    }

    public static boolean settings_menu(MenuItem item, Context context, Activity ctx) {
        int id = item.getItemId();
        if (id == R.id.action_signout) {
            return signout(context, ctx);
        }
        return false;
    }

    public static boolean scan_menu(MenuItem item, Activity context) {
        int id = item.getItemId();
        if (id == R.id.action_flash) {
            return onOrOffFlash(item, context);
        }
        if (id == R.id.action_cant_scan) {
            return cant_scan(context);
        }
        return false;
    }

    public static boolean unique_code_menu(MenuItem item, Activity context) {
        int id = item.getItemId();
        if (id == R.id.action_scan) {
            return scan(context);
        }
        return false;
    }


    // Options des menus

    private static boolean settings(Context context)
    {
        Intent loggedUser = new Intent(context, SettingsActivity.class);
        loggedUser.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        context.startActivity(loggedUser);
        return true;
    }

    public static boolean signout(final Context context, Activity ctx)
    {
        AlertDialog.Builder alert;
        alert = UAlertBox.alert(ctx, context.getResources().getString(R.string.confirm), context.getResources().getString(R.string.logout));
        alert.setPositiveButton(context.getResources().getString(R.string.dialog_ok), new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int whichButton) {
                logout(context);
            }
        });
        alert.setNegativeButton(context.getResources().getString(R.string.dialog_cancel), null);
        alert.show();
        return true;
    }

    public static boolean logout(Context context)
    {
        SharedPreferences sharedpreferences = context.getSharedPreferences("eip.com.lizz", Context.MODE_PRIVATE);
        sharedpreferences.edit().putString("eip.com.lizz._csrf", "").apply();
        sharedpreferences.edit().putBoolean("eip.com.lizz.isLogged", false).apply();
        sharedpreferences.edit().putString("eip.com.lizz.firstname", "").apply();
        sharedpreferences.edit().putString("eip.com.lizz.surname", "").apply();
        sharedpreferences.edit().putString("eip.com.lizz.email", "").apply();
        sharedpreferences.edit().putString("eip.com.lizz.phone", "").apply();
        sharedpreferences.edit().putString("eip.com.lizz.address", "").apply();
        sharedpreferences.edit().putString("eip.com.lizz.complement", "").apply();
        sharedpreferences.edit().putString("eip.com.lizz.postalcode", "").apply();
        sharedpreferences.edit().putString("eip.com.lizz.city", "").apply();
        sharedpreferences.edit().putString("eip.com.lizz.phoneTMP", "").apply();
        sharedpreferences.edit().putString("eip.com.lizz.rib", "").apply();
        sharedpreferences.edit().putString("eip.com.lizz.payementLimit", "").apply();
        sharedpreferences.edit().putString("eip.com.lizz.codepinlizz", "").apply();
        sharedpreferences.edit().putBoolean("eip.com.lizz.scannerstatus", true).apply();
        Intent loggedUser = new Intent(context, HomeActivity.class);
        loggedUser.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK|Intent.FLAG_ACTIVITY_NEW_TASK);
        context.startActivity(loggedUser);
        return true;
    }

    private static boolean onOrOffFlash(MenuItem item, Activity context) {
            SharedPreferences sharedpreferences = context.getSharedPreferences("eip.com.lizz", Context.MODE_PRIVATE);
            Boolean flash = sharedpreferences.getBoolean("eip.com.lizz.flash", false);
            if (flash)
            {
                sharedpreferences.edit().putBoolean("eip.com.lizz.flash", false).apply();
            }
            else
            {
                sharedpreferences.edit().putBoolean("eip.com.lizz.flash", true).apply();
            }
            context.finish();
            Intent loggedUser = new Intent(context, ScanQRCodeActivity.class);
            context.startActivity(loggedUser);
        return true;
    }

    private static boolean cant_scan(Activity context) {
        Intent cantscan = new Intent(context, PayementWithUniqueCodeActivity.class);
        context.startActivity(cantscan);
        return true;
    }

    private static boolean scan(Activity context) {
        Intent scan = new Intent(context, ScanQRCodeActivity.class);
        context.startActivity(scan);
        context.finish();
        return true;
    }
}
