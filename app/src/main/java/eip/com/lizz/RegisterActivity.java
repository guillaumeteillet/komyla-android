package eip.com.lizz;

import android.animation.Animator;
import android.animation.AnimatorListenerAdapter;
import android.annotation.TargetApi;
import android.app.Activity;
import android.app.AlertDialog;
import android.app.LoaderManager.LoaderCallbacks;
import android.content.ContentResolver;
import android.content.Context;
import android.content.CursorLoader;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.Loader;
import android.content.SharedPreferences;
import android.database.Cursor;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Build.VERSION;
import android.os.Bundle;
import android.provider.ContactsContract;
import android.telephony.TelephonyManager;
import android.text.TextUtils;
import android.text.method.PasswordTransformationMethod;
import android.util.Log;
import android.view.KeyEvent;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.inputmethod.EditorInfo;
import android.view.inputmethod.InputMethodManager;
import android.widget.ArrayAdapter;
import android.widget.AutoCompleteTextView;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;

import org.apache.http.cookie.Cookie;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

import eip.com.lizz.Network.Network;
import eip.com.lizz.QueriesAPI.AddUserToAPI;
import eip.com.lizz.QueriesAPI.GetCsrfFromAPI;
import eip.com.lizz.QueriesAPI.LogUserToAPI;
import eip.com.lizz.Utils.UAlertBox;
import eip.com.lizz.Utils.UApi;
import eip.com.lizz.Utils.USaveParams;

public class RegisterActivity extends Activity implements LoaderCallbacks<Cursor> {

    private AutoCompleteTextView mEmailView;
    private EditText mSurnameView;
    private EditText mFirstnameView;
    private EditText mPhoneNumber;
    private EditText mPasswordView;
    private View mProgressView;
    private View mLoginFormView;
    private String phoneNumber;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register);

        mEmailView = (AutoCompleteTextView) findViewById(R.id.email);
        populateAutoComplete();

        TelephonyManager tMgr = (TelephonyManager) getBaseContext().getSystemService(Context.TELEPHONY_SERVICE);
        //phoneNumber = tMgr.getLine1Number();
        mFirstnameView = (EditText) findViewById(R.id.firstname);
        mSurnameView = (EditText) findViewById(R.id.name);
        mPhoneNumber = (EditText) findViewById(R.id.phone);
        /*if (getResources().getString(R.string.debugOrProd).equals("DEBUG"))
            phoneNumber = "0";

        if (phoneNumber != null)
            mPhoneNumber.setText(phoneNumber);*/
        mPasswordView = (EditText) findViewById(R.id.password);
        mPasswordView = (EditText) findViewById(R.id.password);
        mPasswordView.setOnEditorActionListener(new TextView.OnEditorActionListener() {
            @Override
            public boolean onEditorAction(TextView textView, int id, KeyEvent keyEvent) {
                if (id == R.id.login || id == EditorInfo.IME_NULL) {
                    attemptLogin();
                    return true;
                }
                return false;
            }
        });

        Button mEmailSignInButton = (Button) findViewById(R.id.email_sign_in_button);
        mEmailSignInButton.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View view) {
                if (UApi.isOnline(RegisterActivity.this))
                    attemptLogin();
                else
                    UAlertBox.alertOk(RegisterActivity.this, getResources().getString(R.string.error), getResources().getString(R.string.code000));
            }
        });

        mLoginFormView = findViewById(R.id.login_form);
        mProgressView = findViewById(R.id.login_progress);
    }

    private void populateAutoComplete() {
        if (VERSION.SDK_INT >= 14) {
            getLoaderManager().initLoader(0, null, this);
        } else if (VERSION.SDK_INT >= 8) {
            new SetupEmailAutoCompleteTask().execute(null, null);
        }
    }

    public void attemptLogin() {
        mFirstnameView.setError(null);
        mSurnameView.setError(null);
        mEmailView.setError(null);
        mPasswordView.setError(null);

        final String firstname = mFirstnameView.getText().toString();
        final String surname = mSurnameView.getText().toString();
        final String email = mEmailView.getText().toString();
        final String password = mPasswordView.getText().toString();
        final String phoneNumber = mPhoneNumber.getText().toString();

        boolean cancel = false;
        View focusView = null;


        if (!TextUtils.isEmpty(password) && !isPasswordValid(password)) {
            mPasswordView.setError(getString(R.string.error_invalid_password));
            focusView = mPasswordView;
            cancel = true;
        }

        if (TextUtils.isEmpty(firstname)) {
            mFirstnameView.setError(getString(R.string.error_field_required));
            focusView = mFirstnameView;
            cancel = true;
        } else if (TextUtils.isEmpty(surname)) {
            mSurnameView.setError(getString(R.string.error_field_required));
            focusView = mSurnameView;
            cancel = true;
        } else if (TextUtils.isEmpty(email)) {
            mEmailView.setError(getString(R.string.error_field_required));
            focusView = mEmailView;
            cancel = true;
        } else if (!isEmailValid(email)) {
            mEmailView.setError(getString(R.string.error_invalid_email));
            focusView = mEmailView;
            cancel = true;
        } else if (TextUtils.isEmpty(password)) {
            mPasswordView.setError(getString(R.string.error_field_required));
            focusView = mPasswordView;
            cancel = true;
        } else if (isPasswordNotValidPolitic(password)) {
            mPasswordView.setError(getString(R.string.error_password_politic));
            focusView = mPasswordView;
            cancel = true;
        }

        if (cancel) {
            focusView.requestFocus();
        } else {

            final EditText mdpConfirm = new EditText(RegisterActivity.this);
            mdpConfirm.setTransformationMethod(PasswordTransformationMethod.getInstance());
            final AlertDialog.Builder alert = UAlertBox.alertInputOk(RegisterActivity.this, getResources().getString(R.string.dialog_title_confirm), getResources().getString(R.string.dialog_confirm_mdp), mdpConfirm);
            alert.setPositiveButton(getResources().getString(R.string.dialog_ok), new DialogInterface.OnClickListener() {
                public void onClick(DialogInterface dialog, int whichButton) {


                    if (mdpConfirm.getText().toString().equals(mPasswordView.getText().toString())) {
                        InputMethodManager imm = (InputMethodManager) getSystemService(
                                Context.INPUT_METHOD_SERVICE);
                        imm.hideSoftInputFromWindow(mdpConfirm.getWindowToken(), 0);
                        View view = getCurrentFocus();
                        if (view != null) {
                            imm.hideSoftInputFromWindow(view.getWindowToken(), InputMethodManager.HIDE_NOT_ALWAYS);
                        }
                        showProgress(true);

                        String url = getBaseContext().getResources().getString(R.string.url_api_komyla_no_suffix)
                                + getBaseContext().getResources().getString(R.string.url_api_csrfToken);

                        JsonObjectRequest getCSRF = new JsonObjectRequest
                                (Request.Method.GET, url, null, new Response.Listener<JSONObject>() {
                                    @Override
                                    public void onResponse(JSONObject response) {
                                        String token_csrf = "";
                                        try {
                                            token_csrf = response.get("_csrf").toString();
                                        } catch (JSONException e) {
                                            UAlertBox.alertOk(RegisterActivity.this, getResources().getString(R.string.error), getResources().getString(R.string.code000));
                                            e.printStackTrace();
                                        }
                                        final String crsf = token_csrf;

                                        SharedPreferences sharedpreferences = RegisterActivity.this.getSharedPreferences("eip.com.lizz", Context.MODE_PRIVATE);
                                        sharedpreferences.edit().putString("eip.com.lizz._csrf", token_csrf).apply();

                                        String url_api = getBaseContext().getResources().getString(R.string.url_api_komyla_no_suffix)
                                                + getBaseContext().getResources().getString(R.string.url_api_suffix)
                                                + getBaseContext().getResources().getString(R.string.url_api_createUser);
                                        JSONObject data = new JSONObject();
                                        try {
                                            data.put("_csrf", token_csrf);
                                            data.put("firstname", firstname);
                                            data.put("surname", surname);
                                            data.put("email", email);
                                            data.put("phoneNumber", phoneNumber);
                                            data.put("password", password);
                                            data.put("passwordConfirmation", password);

                                            JsonObjectRequest addUser = new JsonObjectRequest(Request.Method.POST, url_api, data, new Response.Listener<JSONObject>() {
                                                @Override
                                                public void onResponse(JSONObject response) {
                                                    finish();
                                                    try {
                                                        String url_api = RegisterActivity.this.getResources().getString(R.string.url_api_komyla_no_suffix)
                                                                + RegisterActivity.this.getResources().getString(R.string.url_api_suffix)
                                                                + RegisterActivity.this.getResources().getString(R.string.url_api_createSession);

                                                        JSONObject data = new JSONObject();
                                                        data.put("_csrf", crsf);
                                                        data.put("email", mEmailView.getText().toString());
                                                        data.put("password", mPasswordView.getText().toString());

                                                        JsonObjectRequest logUser = new JsonObjectRequest(Request.Method.POST, url_api, data, new Response.Listener<JSONObject>() {
                                                            @Override
                                                            public void onResponse(JSONObject response) {
                                                                showProgress(false);
                                                                try {
                                                                    SharedPreferences sharedpreferences = getBaseContext().getSharedPreferences("eip.com.lizz", Context.MODE_PRIVATE);
                                                                    sharedpreferences.edit().putString("eip.com.lizz.firstname", response.getString("firstname")).apply();
                                                                    sharedpreferences.edit().putString("eip.com.lizz.surname", response.getString("surname")).apply();
                                                                    sharedpreferences.edit().putString("eip.com.lizz.email", response.getString("email")).apply();
                                                                    sharedpreferences.edit().putString("eip.com.lizz.id_user", response.getString("id")).apply();
                                                                    sharedpreferences.edit().putString("eip.com.lizz.phone", "0;").apply();
                                                                    sharedpreferences.edit().putBoolean("eip.com.lizz.isLogged", true).apply();
                                                                } catch (JSONException e) {
                                                                    e.printStackTrace();
                                                                }
                                                                RegisterActivity.this.finish();

                                                                Intent loggedUser = new Intent(getBaseContext(), MainMenuActivity.class);
                                                                loggedUser.putExtra("isLoginJustNow", true);
                                                                loggedUser.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK | Intent.FLAG_ACTIVITY_NEW_TASK);
                                                                getBaseContext().startActivity(loggedUser);
                                                            }
                                                        }, new Response.ErrorListener() {
                                                            @Override
                                                            public void onErrorResponse(VolleyError error) {
                                                                showProgress(false);

                                                                int statusCode = error.networkResponse.statusCode;
                                                                switch (statusCode) {
                                                                    case 403: {
                                                                        UAlertBox.alertOk(RegisterActivity.this, getBaseContext().getResources().getString(R.string.error),
                                                                                getBaseContext().getResources().getString(R.string.error_server_ok_but_fail_login)
                                                                                        + getBaseContext().getResources().getString(R.string.code051));
                                                                        break;
                                                                    }
                                                                    case 400: {
                                                                        UAlertBox.alertOk(RegisterActivity.this, getBaseContext().getResources().getString(R.string.error),
                                                                                getBaseContext().getResources().getString(R.string.error_server_ok_but_fail_login)
                                                                                        + getBaseContext().getResources().getString(R.string.code054));
                                                                        break;
                                                                    }
                                                                    case 500: {
                                                                        UAlertBox.alertOk(RegisterActivity.this, getBaseContext().getResources().getString(R.string.error),
                                                                                getBaseContext().getResources().getString(R.string.error_server_ok_but_fail_login)
                                                                                        + getBaseContext().getResources().getString(R.string.code056));
                                                                        break;
                                                                    }
                                                                    default:
                                                                        UAlertBox.alertOk(RegisterActivity.this, getBaseContext().getResources().getString(R.string.error),
                                                                                getBaseContext().getResources().getString(R.string.unknow_error));
                                                                }
                                                            }
                                                        });
                                                        Network.getInstance(RegisterActivity.this).addToRequestQueue(logUser);
                                                    } catch (JSONException e) {
                                                        e.printStackTrace();
                                                        UAlertBox.alertOk(RegisterActivity.this, getResources().getString(R.string.error), getResources().getString(R.string.code000));
                                                    }

                                                }
                                            }, new Response.ErrorListener() {
                                                @Override
                                                public void onErrorResponse(VolleyError error) {
                                                    showProgress(false);
                                                    UAlertBox.alertOk(RegisterActivity.this, getResources().getString(R.string.error), getResources().getString(R.string.error_server_ok_but_fail) + getResources().getString(R.string.code006));

                                                    /*
                                                    else if (jObj.get("responseCode").toString().equals("400")) {
                                                    JSONObject array = jObj.getJSONObject("invalidAttributes");
                                                    boolean firstnameIsMissing = array.has("firstname");
                                                    boolean surnameIsMissing = array.has("surname");
                                                    boolean emailIsMissing = array.has("email");
                                                    boolean passwordIsMissing = array.has("password");
                                                    boolean phone = array.has("phoneNumber");
                                                    if (firstnameIsMissing)
                                                        UAlertBox.alertOk(RegisterActivity.this, getResources().getString(R.string.error), getResources().getString(R.string.error_server_ok_but_fail) + getResources().getString(R.string.code002));
                                                    else if (surnameIsMissing)
                                                        UAlertBox.alertOk(RegisterActivity.this, getResources().getString(R.string.error), getResources().getString(R.string.error_server_ok_but_fail) + getResources().getString(R.string.code003));
                                                    else if (emailIsMissing)
                                                        UAlertBox.alertOk(RegisterActivity.this, getResources().getString(R.string.error), getResources().getString(R.string.error_server_ok_but_fail) + getResources().getString(R.string.code004));
                                                    else if (passwordIsMissing)
                                                        UAlertBox.alertOk(RegisterActivity.this, getResources().getString(R.string.error), getResources().getString(R.string.error_server_ok_but_fail) + getResources().getString(R.string.code005));
                                                    else if (phone)
                                                        UAlertBox.alertOk(RegisterActivity.this, getResources().getString(R.string.error), getResources().getString(R.string.error_server_ok_but_fail) + getResources().getString(R.string.code008));
                                                } else if (jObj.get("responseCode").toString().equals("403")) {
                                                    UAlertBox.alertOk(RegisterActivity.this, getResources().getString(R.string.error), getResources().getString(R.string.error_server_ok_but_fail) + getResources().getString(R.string.code001));
                                                } else if (jObj.get("responseCode").toString().equals("408")) {
                                                    UAlertBox.alertOk(RegisterActivity.this, getResources().getString(R.string.error), getResources().getString(R.string.code000));
                                                } else if (jObj.get("responseCode").toString().equals("500")) {
                                                    UAlertBox.alertOk(RegisterActivity.this, getResources().getString(R.string.error), getResources().getString(R.string.error_server_ok_but_fail) + getResources().getString(R.string.code006));
                                                } else {
                                                    UAlertBox.alertOk(RegisterActivity.this, getResources().getString(R.string.error), getResources().getString(R.string.error_server_ok_but_fail) + getResources().getString(R.string.code006));
                                                }
                                                     */
                                                }
                                            });
                                            Network.getInstance(RegisterActivity.this).addToRequestQueue(addUser);
                                        } catch (JSONException e) {
                                            e.printStackTrace();
                                        }

                                    }
                                }, new Response.ErrorListener() {
                                    @Override
                                    public void onErrorResponse(VolleyError error) {
                                        showProgress(false);
                                        UAlertBox.alertOk(RegisterActivity.this, getResources().getString(R.string.error), getResources().getString(R.string.code000));
                                    }
                                });
                        Network.getInstance(RegisterActivity.this).addToRequestQueue(getCSRF);
                    } else {
                        USaveParams.displayError(5, RegisterActivity.this, null, null, false);
                        mdpConfirm.setText("");
                    }
                }
            });
            alert.setNegativeButton(getResources().getString(R.string.dialog_cancel), null);
            alert.show();
        }
    }

    private boolean isEmailValid(String email) {
        if (email.contains("@") && email.contains("."))
            return true;
        else
            return false;
    }

    private boolean isPasswordValid(String password) {
        if (password.length() > 7)
            return true;
        else
            return false;
    }

    public static boolean isPasswordNotValidPolitic(String password) {


        boolean letters = false;
        boolean numbers = false;

        char[] stringArray;
        int i = 0;
        stringArray = password.toCharArray();
        while (i < stringArray.length) {
            if (letters == false) {
                if (Character.isLetter(stringArray[i]))
                    letters = true;
            }
            if (numbers == false) {
                if (Character.isDigit(stringArray[i]))
                    numbers = true;
            }
            i++;
        }

        if (letters == true && numbers == true)
            return false;
        else
            return true;
    }

    @TargetApi(Build.VERSION_CODES.HONEYCOMB_MR2)
    public void showProgress(final boolean show) {
        if (VERSION.SDK_INT >= Build.VERSION_CODES.HONEYCOMB_MR2) {
            int shortAnimTime = getResources().getInteger(android.R.integer.config_shortAnimTime);

            mLoginFormView.setVisibility(show ? View.GONE : View.VISIBLE);
            mLoginFormView.animate().setDuration(shortAnimTime).alpha(
                    show ? 0 : 1).setListener(new AnimatorListenerAdapter() {
                @Override
                public void onAnimationEnd(Animator animation) {
                    mLoginFormView.setVisibility(show ? View.GONE : View.VISIBLE);
                }
            });

            mProgressView.setVisibility(show ? View.VISIBLE : View.GONE);
            mProgressView.animate().setDuration(shortAnimTime).alpha(
                    show ? 1 : 0).setListener(new AnimatorListenerAdapter() {
                @Override
                public void onAnimationEnd(Animator animation) {
                    mProgressView.setVisibility(show ? View.VISIBLE : View.GONE);
                }
            });
        } else {
            mProgressView.setVisibility(show ? View.VISIBLE : View.GONE);
            mLoginFormView.setVisibility(show ? View.GONE : View.VISIBLE);
        }
    }

    @Override
    public Loader<Cursor> onCreateLoader(int i, Bundle bundle) {
        return new CursorLoader(this,
                Uri.withAppendedPath(ContactsContract.Profile.CONTENT_URI,
                        ContactsContract.Contacts.Data.CONTENT_DIRECTORY), ProfileQuery.PROJECTION,
                ContactsContract.Contacts.Data.MIMETYPE +
                        " = ?", new String[]{ContactsContract.CommonDataKinds.Email
                .CONTENT_ITEM_TYPE},

                ContactsContract.Contacts.Data.IS_PRIMARY + " DESC");
    }

    @Override
    public void onLoadFinished(Loader<Cursor> cursorLoader, Cursor cursor) {
        List<String> emails = new ArrayList<String>();
        cursor.moveToFirst();
        while (!cursor.isAfterLast()) {
            emails.add(cursor.getString(ProfileQuery.ADDRESS));
            cursor.moveToNext();
        }

        addEmailsToAutoComplete(emails);
    }

    @Override
    public void onLoaderReset(Loader<Cursor> cursorLoader) {

    }

    private interface ProfileQuery {
        String[] PROJECTION = {
                ContactsContract.CommonDataKinds.Email.ADDRESS,
                ContactsContract.CommonDataKinds.Email.IS_PRIMARY,
        };

        int ADDRESS = 0;
        int IS_PRIMARY = 1;
    }

    class SetupEmailAutoCompleteTask extends AsyncTask<Void, Void, List<String>> {

        @Override
        protected List<String> doInBackground(Void... voids) {
            ArrayList<String> emailAddressCollection = new ArrayList<String>();

            ContentResolver cr = getContentResolver();
            Cursor emailCur = cr.query(ContactsContract.CommonDataKinds.Email.CONTENT_URI, null,
                    null, null, null);
            while (emailCur.moveToNext()) {
                String email = emailCur.getString(emailCur.getColumnIndex(ContactsContract
                        .CommonDataKinds.Email.DATA));
                emailAddressCollection.add(email);
            }
            emailCur.close();

            return emailAddressCollection;
        }

        @Override
        protected void onPostExecute(List<String> emailAddressCollection) {
            addEmailsToAutoComplete(emailAddressCollection);
        }
    }

    private void addEmailsToAutoComplete(List<String> emailAddressCollection) {
        ArrayAdapter<String> adapter =
                new ArrayAdapter<String>(RegisterActivity.this,
                        android.R.layout.simple_dropdown_item_1line, emailAddressCollection);

        mEmailView.setAdapter(adapter);
    }
}



