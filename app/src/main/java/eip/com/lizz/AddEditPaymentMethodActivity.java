package eip.com.lizz;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.support.v7.app.ActionBarActivity;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.Toast;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.Calendar;
import java.util.HashMap;
import java.util.concurrent.ExecutionException;

import eip.com.lizz.Network.POST_AddCreditCard;
import eip.com.lizz.QueriesAPI.DeleteCreditCardFromAPI;
import eip.com.lizz.QueriesAPI.UpdateCreditCardToAPI;
import eip.com.lizz.Utils.UAlertBox;
import eip.com.lizz.Utils.UNetwork;
import io.card.payment.CardIOActivity;
import io.card.payment.CreditCard;


public class AddEditPaymentMethodActivity extends ActionBarActivity {

    private enum E_MODE {
        ADDITION,
        EDITION
    }

    private static final int MY_SCAN_REQUEST_CODE = 1;
    private static E_MODE inputMode;

    // XML Attributes
    private EditText    edittextCardNumber = null;
    private EditText    edittextExpirationDateMonth = null;
    private EditText    edittextExpirationDateYear = null;
    private EditText    edittextCryptogram = null;
    private EditText    edittextOwnerName = null;
    private EditText    edittextDisplayName = null;
    private Button      saveCard = null;
    private Button      deleteCard = null;
    private ImageButton scanCard = null;

    // Data attributes
    private String cardNumberStr = null;
    private int monthInput = 0;
    private int yearInput = 0;
    private eip.com.lizz.Models.CreditCard oldCreditCard;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_edit_payment_method);

        setInputMode();
        buttonBinding();
        configurationOfUI(this);
    }

    private void setInputMode() {
        eip.com.lizz.Models.CreditCard cb = (eip.com.lizz.Models.CreditCard) getIntent().getSerializableExtra("EXTRA_CREDIT_CARD");
        if (cb == null) {
            Log.v("Extra", "Extra null -> Ajout d'un nouveau moyen de paiement");

            inputMode = E_MODE.ADDITION;
        }
        else {
            Log.v("Extra", "Extra non null -> Edition d'un moyen de paiement existant");

            inputMode = E_MODE.EDITION;
            oldCreditCard = cb;
        }
    }

    private void configurationOfUI(Context context) {

        if (inputMode == E_MODE.EDITION) {
            this.edittextCardNumber.setText(oldCreditCard.get_cardNumber());
            this.edittextExpirationDateMonth.setText(oldCreditCard.get_expirationDateMonth());
            this.edittextExpirationDateYear.setText(oldCreditCard.get_expirationDateYear());
            this.edittextCryptogram.setText(oldCreditCard.get_cryptogram());
            this.edittextOwnerName.setText(oldCreditCard.get_cardHolder());
            this.edittextDisplayName.setText(oldCreditCard.get_displayName());
            this.setTitle(getResources().getString(R.string.title_activity_add_edit_card_edition));
        }

        if (inputMode == E_MODE.EDITION) {
            saveCard.setVisibility(View.GONE);
        }

        if (inputMode == E_MODE.ADDITION) {
            deleteCard.setVisibility(View.GONE);
        }

        PackageManager pm = getBaseContext().getPackageManager();
        if (!pm.hasSystemFeature(PackageManager.FEATURE_CAMERA)) {
            scanCard.setVisibility(View.GONE);
        }

        configureButtonSaveCard(this);
        configureButtonDeleteCard(this);
    }

    private void buttonBinding() {
        edittextCardNumber = (EditText)findViewById(R.id.edittextCardNumber);
        edittextExpirationDateMonth = (EditText)findViewById(R.id.edittextExpirationDateMonth);
        edittextExpirationDateYear = (EditText)findViewById(R.id.edittextExpirationDateYear);
        edittextCryptogram = (EditText)findViewById(R.id.edittextCryptogram);
        edittextOwnerName = (EditText)findViewById(R.id.edittextOwnerName);
        edittextDisplayName = (EditText)findViewById(R.id.edittextDisplayName);

        scanCard = (ImageButton)findViewById(R.id.buttonScanCard);
        saveCard = (Button)findViewById(R.id.buttonSaveCard);
        deleteCard = (Button)findViewById(R.id.buttonDeleteCard);
    }



    private void configureButtonSaveCard(final Context context) {
        saveCard.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (UNetwork.checkInternetConnection(context)) {
                /* VERIFICATION DES CHAMPS DU FORMULAIRE */
                    if (inputMode == E_MODE.ADDITION)
                        buttonSaveCardAdditionMode(context);
                    else if (inputMode == E_MODE.EDITION)
                        buttonSaveCardEditionMode(context);
                }
                else {
                    UAlertBox.alertOk(AddEditPaymentMethodActivity.this,
                            getResources().getString(R.string.dialog_title_no_internet),
                            getResources().getString(R.string.dialog_no_internet));
                }
            }
        });
    }

    private void buttonSaveCardAdditionMode(final Context context) {
        if (allFieldAreGood())
        {
            JSONObject data = new JSONObject();
            JSONObject cardData = new JSONObject();
            try
            {
                data.put("_csrf", getApplicationContext().getSharedPreferences("eip.com.lizz", Context.MODE_PRIVATE).getString("eip.com.lizz._csrf", ""));

                cardData.put("number", edittextCardNumber.getText().toString());
                cardData.put("type", "CB");
                cardData.put("expirationDate", edittextExpirationDateMonth.getText().toString() + edittextExpirationDateYear.getText().toString());
                cardData.put("cvx", edittextCryptogram.getText().toString());
                cardData.put("default", "false");
                data.put("cardData", cardData);

                new POST_AddCreditCard(this, getBaseContext()).run(data);

            } catch (JSONException e) {
                e.printStackTrace();
            }
        }
    }

    private void buttonSaveCardEditionMode(final Context context) {

        HashMap<String, String> modifiedFields = new HashMap<String, String>();
        saveChangedData(modifiedFields);

        if (modifiedFields.size() == 0)
            finish();

        if (verifyChangedData(modifiedFields)) {
            SharedPreferences sharedpreferences = getApplicationContext().getSharedPreferences("eip.com.lizz", Context.MODE_PRIVATE);
            UpdateCreditCardToAPI task = new UpdateCreditCardToAPI(sharedpreferences.getString("eip.com.lizz._csrf", ""),
                    oldCreditCard.get_id(), getApplicationContext());
            try {
                String responseCode = task.execute(modifiedFields).get();
                // IL FAUT TRAITER LE RETOUR DE L'API ICI
                if (responseCode.compareTo("200") == 0) {
                    Toast.makeText(context, getResources().getString(R.string.toast_valid_card_infos), Toast.LENGTH_LONG).show();
                    finish();
                }
            } catch (InterruptedException | ExecutionException e) {
                e.printStackTrace();
            }
        }
        else {
            Toast.makeText(getApplicationContext(), getResources().getString(R.string.wrong_card_infos), Toast.LENGTH_LONG).show();
        }
    }

    private void saveChangedData(HashMap<String, String> modifiedFields) {
        if (edittextCardNumber.getText().toString().compareTo(oldCreditCard.get_cardNumber()) != 0)
            modifiedFields.put("cardNumber", edittextCardNumber.getText().toString());
        if (edittextExpirationDateMonth.getText().toString().compareTo(oldCreditCard.get_expirationDateMonth()) != 0)
            modifiedFields.put("cardExpireMonth", edittextExpirationDateMonth.getText().toString());
        if (edittextExpirationDateYear.getText().toString().compareTo(oldCreditCard.get_expirationDateYear()) != 0)
            modifiedFields.put("cardExpireYear", edittextExpirationDateYear.getText().toString());
        if (edittextCryptogram.getText().toString().compareTo(oldCreditCard.get_cryptogram()) != 0)
            modifiedFields.put("cardVerificationValue", edittextCryptogram.getText().toString());
        if (edittextOwnerName.getText().toString().compareTo(oldCreditCard.get_cardHolder()) != 0)
            modifiedFields.put("cardHolder", edittextOwnerName.getText().toString());
        if (edittextDisplayName.getText().toString().compareTo(oldCreditCard.get_displayName()) != 0)
            modifiedFields.put("cardName", edittextDisplayName.getText().toString());
    }

    private boolean verifyChangedData(HashMap<String, String> modifiedData) {
        for (String key : modifiedData.keySet()) {
            if (key.compareTo("cardNumber") == 0)
                if (!isCardNumberIsValide())
                    return false;
            if (key.compareTo("cardExpireMonth") == 0)
                if (!checkExpirationMonth())
                    return false;
                else
                    if (!isExpirationDateValide())
                        return false;
            if (key.compareTo("cardExpireYear") == 0)
                if (!checkExpirationYear())
                    return false;
                else
                    if (!isExpirationDateValide())
                        return false;
            if (key.compareTo("cardVerificationValue") == 0)
                if (!isCryptogramValid())
                    return false;
            if (key.compareTo("cardHolder") == 0)
                if (this.edittextOwnerName.length() == 0)
                    return false;
            if (key.compareTo("cardName") == 0)
                if (this.edittextDisplayName.length() == 0)
                    return false;

            Log.d("Edition", "" + key + ":" + modifiedData.get(key));
        }
        return true;
    }

    private void configureButtonDeleteCard(final Context context) {
        deleteCard.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (UNetwork.checkInternetConnection(context)) {
                    SharedPreferences sharedpreferences = getApplicationContext().getSharedPreferences("eip.com.lizz", Context.MODE_PRIVATE);
                    DeleteCreditCardFromAPI task = new DeleteCreditCardFromAPI(sharedpreferences.getString("eip.com.lizz._csrf", ""), getApplicationContext());
                    try {
                        String responseCode = task.execute(oldCreditCard).get();
                        if (responseCode.compareTo("200") == 0) {
                            Toast.makeText(context, "La carte a été supprimée avec succès", Toast.LENGTH_LONG).show();
                            finish();
                        }
                    } catch (InterruptedException | ExecutionException e) {
                        e.printStackTrace();
                    }
                }
                else {
                    UAlertBox.alertOk(AddEditPaymentMethodActivity.this,
                            getResources().getString(R.string.dialog_title_no_internet),
                            getResources().getString(R.string.dialog_no_internet));
                }
            }
        });
    }

    private boolean allFieldAreGood() {
        if (isCardNumberIsValide() && checkExpirationMonth() && checkExpirationYear()
                && isExpirationDateValide() && isCryptogramValid()
                && this.edittextOwnerName.length() != 0 && this.edittextDisplayName.length() != 0) {
            return true;
        }

        return false;
    }

    private boolean isCardNumberIsValide() {
        cardNumberStr = edittextCardNumber.getText().toString();
        if (cardNumberStr == null) {
            edittextCardNumber.setError(getResources().getString(R.string.error_wrong_card_number));
            return false;
        }
        if (!cardNumberStr.matches("\\d{16}")) {
            edittextCardNumber.setError(getResources().getString(R.string.error_wrong_card_number));
            return false;
        }

        // *2 every 2 number
        int[] resultTab = new int[15];
        for (int i = 0; i < cardNumberStr.length() - 1; i++) {
            if (i % 2 == 0) {
                int tmp = Character.getNumericValue(cardNumberStr.charAt(i));
                tmp *= 2;
                if (tmp > 9) {
                    tmp -= 9;
                }
                resultTab[i] = tmp;
            } else {
                resultTab[i] = Character.getNumericValue(cardNumberStr.charAt(i));
            }
        }

        // Add all the number beetween them
        int totalSum = 0;
        for (int aResultTab : resultTab) {
            totalSum += aResultTab;
        }

        // % 10 and 10 - result
        totalSum = totalSum % 10;
        totalSum = 10 - totalSum;

        if (totalSum - Character.getNumericValue(cardNumberStr.charAt(cardNumberStr.length() - 1)) != 0) {
            Log.d("CardNumber", "carte nop");
            edittextCardNumber.setError(getResources().getString(R.string.error_wrong_card_number));
            return false;
        }
        return true;
    }

    private boolean checkExpirationMonth() {

        // VERIFICATION DU FORMAT
        if (edittextExpirationDateMonth == null || edittextExpirationDateMonth.getText().length() != 2) {
            edittextExpirationDateMonth.setError(getResources().getString(R.string.error_format_wrong_expiration_date));
            return false;
        }
        // RECUPERATION DU MOIS
        monthInput = 0;
        monthInput += Character.getNumericValue(edittextExpirationDateMonth.getText().charAt(0));
        monthInput *= 10;
        monthInput += Character.getNumericValue(edittextExpirationDateMonth.getText().charAt(1));
        // VERIFICATION DU MOIS
        if (monthInput < 1 || monthInput > 12) {
            edittextExpirationDateMonth.setError(getResources().getString(R.string.error_wrong_expiration_month));
            return false;
        }
        return true;
    }

    private boolean checkExpirationYear() {

        if (edittextExpirationDateYear == null || edittextExpirationDateYear.getText().length() != 2) {
            edittextExpirationDateYear.setError(getResources().getString(R.string.error_format_wrong_expiration_date));
            return false;
        }
        // RECUPERATION DE L'ANNÉE
        yearInput = 0;
        yearInput += Character.getNumericValue(edittextExpirationDateYear.getText().charAt(0));
        yearInput *= 10;
        yearInput += Character.getNumericValue(edittextExpirationDateYear.getText().charAt(1));
        yearInput += 2000;

        return true;
    }

    private boolean isExpirationDateValide() {
        int monthCalendar;
        int yearCalendar;

        if (checkExpirationMonth() && checkExpirationYear()) {
            Calendar calendar = Calendar.getInstance();
            monthCalendar = calendar.get(Calendar.MONTH) + 1;
            yearCalendar = calendar.get(Calendar.YEAR);

            if (yearInput > yearCalendar && yearInput < yearCalendar + 3)
                return true;
            else if (yearInput == yearCalendar && monthInput >= monthCalendar)
                return true;
            else if (yearInput < yearCalendar) {
                edittextExpirationDateYear.setError(getResources().getString(R.string.error_wrong_expiration_date));
            }
            else if (yearInput > yearCalendar + 2) {
                edittextExpirationDateYear.setError("Année_actuelle + 2 est le maximum");
            }
        }
        return false;
    }

    private boolean isCryptogramValid() {
        if (this.edittextCryptogram.getText().toString().matches("\\d{3}"))
            return true;
        edittextCryptogram.setError(getResources().getString(R.string.error_wrong_cryptogram));
        return false;
    }



    public void onScanPress(View v) {
        Intent scanIntent = new Intent(this, CardIOActivity.class);

        scanIntent.putExtra(CardIOActivity.EXTRA_REQUIRE_EXPIRY, true);
        scanIntent.putExtra(CardIOActivity.EXTRA_REQUIRE_CVV, true);
        scanIntent.putExtra(CardIOActivity.EXTRA_USE_CARDIO_LOGO, true);
        scanIntent.putExtra(CardIOActivity.EXTRA_SUPPRESS_MANUAL_ENTRY, true);

        startActivityForResult(scanIntent, MY_SCAN_REQUEST_CODE);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        String card_number = "", card_number_hide = "", card_number2 = "", cvv = "", expiryYear = "0";
        int expiryMonth = 0;

        if (requestCode == MY_SCAN_REQUEST_CODE) {
            String resultDisplayStr;
            if (data != null && data.hasExtra(CardIOActivity.EXTRA_SCAN_RESULT)) {
                CreditCard scanResult = data.getParcelableExtra(CardIOActivity.EXTRA_SCAN_RESULT);

                card_number = scanResult.getFormattedCardNumber();
                card_number_hide = scanResult.getRedactedCardNumber();
                card_number2 = scanResult.cardNumber;
                expiryMonth = scanResult.expiryMonth;
                expiryYear = ""+scanResult.expiryYear;
                cvv = scanResult.cvv;
                edittextCardNumber.setText(card_number2);
                if (expiryMonth >= 1 && expiryMonth <= 9)
                    edittextExpirationDateMonth.setText("0"+expiryMonth);
                else
                    edittextExpirationDateMonth.setText(expiryMonth);
                expiryYear = expiryYear.replace("20", "");
                edittextExpirationDateYear.setText(expiryYear);
                edittextCryptogram.setText(cvv);
                edittextOwnerName.requestFocus();
            }
            else {
                // Scan was canceled
            }

            /* DEBUG POUR MATHIEU
             *
              * A supprimer avant la mise en prod :D
              *
              * */
           /* Log.d("TEST", card_number); //1234 1234 1234 1234
            Log.d("TEST", card_number_hide);// •••• •••• •••• 1234
            Log.d("TEST", card_number2); //1234123412341234
            Log.d("TEST", ""+expiryMonth); // 4
            Log.d("TEST", ""+expiryYear); // 2014
            Log.d("TEST", cvv); // 123*/
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.main_menu, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        return MenuLizz.main_menu(item, getBaseContext(), AddEditPaymentMethodActivity.this);
    }
}
