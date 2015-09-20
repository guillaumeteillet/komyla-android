package eip.com.lizz.Setting;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.database.Cursor;
import android.graphics.Color;
import android.net.Uri;
import android.os.Environment;
import android.provider.MediaStore;
import android.support.v7.app.ActionBarActivity;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import eip.com.lizz.Utils.UAlertBox;
import eip.com.lizz.MenuLizz;
import eip.com.lizz.R;
import eip.com.lizz.Utils.UNetwork;
import eip.com.lizz.Utils.UPhoto;
import eip.com.lizz.Utils.UThread;


public class SettingsIDCard extends ActionBarActivity {

    static final int REQUEST_TAKE_PHOTO_RECTO = 1;
    static final int REQUEST_TAKE_PHOTO_VERSO = 2;
    private static int RESULT_LOAD_IMG_RECTO = 3;
    private static int RESULT_LOAD_IMG_VERSO = 4;
    String imgDecodableString;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_settings_idcard);

        final Button recto = (Button) findViewById(R.id.recto);
        final Button verso = (Button) findViewById(R.id.verso);

        SharedPreferences sharedpreferences = getSharedPreferences("eip.com.lizz", Context.MODE_PRIVATE);
        String returnAPIIDCardRecto = sharedpreferences.getString("eip.com.lizz.returnAPIIDCardRecto", "0");
        String returnAPIIDCardVerso = sharedpreferences.getString("eip.com.lizz.returnAPIIDCardVerso", "0");

        if (UNetwork.checkInternetConnection(getApplicationContext()))
        {
            returnAPIIDCardRecto = "0"; // RETOUR API
            returnAPIIDCardVerso = "0"; // RETOUR API
            sharedpreferences.edit().putString("eip.com.lizz.returnAPIIDCardRecto",returnAPIIDCardRecto).apply();
            sharedpreferences.edit().putString("eip.com.lizz.returnAPIIDCardVerso",returnAPIIDCardVerso).apply();
        }
        else
            UAlertBox.alertOk(SettingsIDCard.this, getResources().getString(R.string.dialog_title_no_internet), getResources().getString(R.string.dialog_no_internet));

        TextView statut = (TextView) findViewById(R.id.statut);
        TextView statut2 = (TextView) findViewById(R.id.statut2);
        if (returnAPIIDCardRecto.equals("0")) // Pas encore recu
        {
            statut.setText(getResources().getString(R.string.label_id_card_recto_not_receive));
            statut.setTextColor(Color.RED);
        }
        else if (returnAPIIDCardRecto.equals("1")) // Wait
        {
            statut.setText(getResources().getString(R.string.label_id_card_recto_wait));
            statut.setTextColor(Color.parseColor("#FF6633"));
        }
        else if (returnAPIIDCardRecto.equals("2")) // OK
        {
            statut.setText(getResources().getString(R.string.label_id_card_recto_ok));
            statut.setTextColor(Color.parseColor("#336600"));
            recto.setVisibility(View.GONE);
        }
        if (returnAPIIDCardVerso.equals("0")) // Pas encore recu
        {
            statut2.setText(getResources().getString(R.string.label_id_card_verso_not_receive));
            statut2.setTextColor(Color.RED);
        }
        else if (returnAPIIDCardVerso.equals("1")) // Wait
        {
            statut2.setText(getResources().getString(R.string.label_id_card_verso_wait));
            statut2.setTextColor(Color.parseColor("#FF6633"));
        }
        else if (returnAPIIDCardVerso.equals("2")) // OK
        {
            statut2.setText(getResources().getString(R.string.label_id_card_verso_ok));
            statut2.setTextColor(Color.parseColor("#336600"));
            verso.setVisibility(View.GONE);
        }

        if (returnAPIIDCardVerso.equals("2") && returnAPIIDCardRecto.equals("2"))
        {
            statut.setText(getResources().getString(R.string.label_id_card_ok));
            statut2.setText("");
            statut.setTextColor(Color.parseColor("#336600"));
            verso.setVisibility(View.GONE);
            recto.setVisibility(View.GONE);
        }

        recto.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                AlertDialog.Builder builder = new AlertDialog.Builder(SettingsIDCard.this);
                builder.setTitle(getResources().getString(R.string.labelSendRecto));
                final CharSequence[] nb = new CharSequence[2];
                nb[0] = getResources().getString(R.string.labelTakePicture);
                nb[1] = getResources().getString(R.string.labelSelectPicture);
                builder.setItems(nb, new DialogInterface.OnClickListener() {

                    @Override
                    public void onClick(DialogInterface dialog, int item) {
                        if (item == 0)
                        {
                            UPhoto.launchAPN(SettingsIDCard.this, REQUEST_TAKE_PHOTO_RECTO, "id_card_recto.jpg", "/lizzTMP/");
                        }
                        if (item == 1)
                        {
                            Intent galleryIntent = new Intent(Intent.ACTION_PICK, android.provider.MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
                            startActivityForResult(galleryIntent, RESULT_LOAD_IMG_RECTO);
                        }
                    }
                });
                AlertDialog alert = builder.create();
                alert.setOwnerActivity(SettingsIDCard.this);
                alert.show();
            }
        });

        verso.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                AlertDialog.Builder builder = new AlertDialog.Builder(SettingsIDCard.this);
                builder.setTitle(getResources().getString(R.string.labelSendVerso));
                final CharSequence[] nb = new CharSequence[2];
                nb[0] = getResources().getString(R.string.labelTakePicture);
                nb[1] = getResources().getString(R.string.labelSelectPicture);
                builder.setItems(nb, new DialogInterface.OnClickListener() {

                    @Override
                    public void onClick(DialogInterface dialog, int item) {
                        if (item == 0)
                        {
                            UPhoto.launchAPN(SettingsIDCard.this, REQUEST_TAKE_PHOTO_VERSO, "id_card_verso.jpg", "/lizzTMP/");
                        }
                        if (item == 1)
                        {
                            Intent galleryIntent = new Intent(Intent.ACTION_PICK, android.provider.MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
                            startActivityForResult(galleryIntent, RESULT_LOAD_IMG_VERSO);
                        }
                    }
                });
                AlertDialog alert = builder.create();
                alert.setOwnerActivity(SettingsIDCard.this);
                alert.show();
            }
        });
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {

        if (requestCode == REQUEST_TAKE_PHOTO_RECTO && resultCode == RESULT_OK) {
            UThread.send(SettingsIDCard.this, Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_PICTURES) + "/lizzTMP/id_card_recto.jpg", getResources().getString(R.string.pleasewaitidcardrecto), getResources().getString(R.string.labelMessageIDCardRecto), "idCardrecto");
        }
        else if (requestCode == REQUEST_TAKE_PHOTO_VERSO && resultCode == RESULT_OK) {
            UThread.send(SettingsIDCard.this, Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_PICTURES) + "/lizzTMP/id_card_verso.jpg", getResources().getString(R.string.pleasewaitidcardverso), getResources().getString(R.string.labelMessageIDCardVerso),"idCardVerso");
        }
        else if ( (requestCode == RESULT_LOAD_IMG_RECTO || requestCode == RESULT_LOAD_IMG_VERSO) && resultCode == RESULT_OK && null != data) {
            try {
                Uri selectedImage = data.getData();
                String[] filePathColumn = {MediaStore.Images.Media.DATA};
                Cursor cursor = getContentResolver().query(selectedImage, filePathColumn, null, null, null);
                cursor.moveToFirst();
                int columnIndex = cursor.getColumnIndex(filePathColumn[0]);
                imgDecodableString = cursor.getString(columnIndex);
                if (requestCode == RESULT_LOAD_IMG_RECTO)
                    UThread.send(SettingsIDCard.this, imgDecodableString, getResources().getString(R.string.pleasewaitidcardrecto), getResources().getString(R.string.labelMessageIDCardRecto), "idCardrecto");
                else if (requestCode == RESULT_LOAD_IMG_VERSO)
                    UThread.send(SettingsIDCard.this, imgDecodableString, getResources().getString(R.string.pleasewaitidcardverso), getResources().getString(R.string.labelMessageIDCardVerso), "idCardVerso");
                cursor.close();

            } catch (Exception e) {
                UAlertBox.alertOk(SettingsIDCard.this, getResources().getString(R.string.error), getResources().getString(R.string.errordefault));
            }
        }


    }



    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.settings, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        return MenuLizz.settings_menu(item, getBaseContext(), SettingsIDCard.this);
    }
}
