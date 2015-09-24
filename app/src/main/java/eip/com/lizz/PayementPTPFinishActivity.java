package eip.com.lizz;

import android.content.ContentUris;
import android.content.Intent;
import android.database.Cursor;
import android.net.Uri;
import android.os.Bundle;
import android.provider.ContactsContract;
import android.support.v7.app.ActionBarActivity;
import android.util.Base64;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import java.io.UnsupportedEncodingException;

import eip.com.lizz.Utils.UPhoneBook;

/**
 * Created by guillaume on 28/03/15.
 */
public class PayementPTPFinishActivity extends ActionBarActivity {

        String data = "";

        @Override
        protected void onCreate(Bundle savedInstanceState) {
            super.onCreate(savedInstanceState);
            setContentView(R.layout.activity_payement_ptpfinish);


            TextView somme_label = (TextView) findViewById(R.id.somme);
            TextView name_destinataire = (TextView) findViewById(R.id.name_destinataire);
            ImageView profil_picture = (ImageView) findViewById(R.id.profil_picture);
            Button backHome = (Button) findViewById(R.id.back);
            backHome.setOnClickListener(new View.OnClickListener() {
                public void onClick(View v) {
                    Intent loggedUser = new Intent(getBaseContext(), MainMenuActivity.class);
                    loggedUser.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK | Intent.FLAG_ACTIVITY_NO_ANIMATION | Intent.FLAG_ACTIVITY_NEW_TASK);
                    startActivity(loggedUser);
                }
            });

            Bundle bundle = getIntent().getExtras();
            if (bundle != null) {
                /*if (bundle.getString("contact") != null) {
                    String contact = bundle.getString("contact");
                    String contactName = "", uri = "";
                    boolean isPhone = bundle.getBoolean("isPhone");
                    boolean isEmail = bundle.getBoolean("isEmail");
                    String[] array = new String[0];
                    if (isPhone)
                        array = UPhoneBook.infosByPhone(getContentResolver(), contact);
                    else if (isEmail)
                        array = UPhoneBook.infosByEmail(getContentResolver(), contact);
                    contactName = array[0];
                    uri = array[1];
                    name_destinataire.setText(contactName);
                    if (!uri.equals(""))
                        profil_picture.setImageURI(Uri.parse(uri));
                    else
                        profil_picture.setImageResource(R.drawable.ic_launcher);
                    if(profil_picture.getDrawable() == null)
                        profil_picture.setImageResource(R.drawable.ic_launcher);
                }*/

                if (bundle.getString("somme") != null) {
                    String somme = bundle.getString("somme");
                    somme_label.setText(somme+" €");
                }

                name_destinataire.setText("E.Leclerc");

                /*if (bundle.getString("data") != null) {
                    data = bundle.getString("data");
                    Log.d("TEST", data);
                    try {
                        byte[] data_bytes = Base64.decode(data, Base64.DEFAULT);
                        String url = new String(data_bytes, "UTF-8");
                        Log.d(">>>>", url);
                        String[] params = url.split("&");

                        String[] nom = params[0].split("=");
                        String[] somme = params[2].split("=");

                        TextView shopName = (TextView) findViewById(R.id.name_destinataire);
                        shopName.setText(nom[1]);

                        TextView sommeTxt = (TextView) findViewById(R.id.somme);
                        sommeTxt.setText(somme[1] + " €");


                    } catch (UnsupportedEncodingException e) {
                        e.printStackTrace();
                    }
                } else {
                    Toast.makeText(getBaseContext(), getResources().getString(R.string.no_data), Toast.LENGTH_LONG).show();
                    finish();
                }*/

            }
        }


        @Override
        public boolean onCreateOptionsMenu(Menu menu) {
            getMenuInflater().inflate(R.menu.main_menu, menu);
            return true;
        }

        @Override
        public boolean onOptionsItemSelected(MenuItem item) {
            return MenuLizz.main_menu(item, getBaseContext(), PayementPTPFinishActivity.this);
        }
}
