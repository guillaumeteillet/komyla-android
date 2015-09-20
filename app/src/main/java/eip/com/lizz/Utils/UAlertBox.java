package eip.com.lizz.Utils;

import android.app.Activity;
import android.app.AlertDialog;
import android.widget.EditText;

import eip.com.lizz.R;


public class UAlertBox {

    public static void alertOk(Activity context, String title, String msg)
    {
        AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(context);
        alertDialogBuilder.setTitle(title);
        alertDialogBuilder
                .setMessage(msg)
                .setCancelable(false)
                .setPositiveButton(context.getResources().getString(R.string.dialog_ok), null);
        AlertDialog alertDialog = alertDialogBuilder.create();
        alertDialog.show();
    }

    public static AlertDialog.Builder alert(Activity context, String title, String msg)
    {
        AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(context);
        alertDialogBuilder.setTitle(title);
        alertDialogBuilder
                .setMessage(msg)
                .setCancelable(false);
        return alertDialogBuilder;
    }

    public static AlertDialog.Builder alertInputOk(Activity context, String title, String msg, EditText input) {
        AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(context);
        alertDialogBuilder.setTitle(title);
        alertDialogBuilder
                .setMessage(msg)
                .setCancelable(false);
        alertDialogBuilder.setView(input);
        alertDialogBuilder.setNegativeButton(context.getResources().getString(R.string.dialog_cancel), null);
        return alertDialogBuilder;
    }
}
