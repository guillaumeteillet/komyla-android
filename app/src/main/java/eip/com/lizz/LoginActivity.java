package eip.com.lizz;

import android.animation.Animator;
import android.animation.AnimatorListenerAdapter;
import android.annotation.TargetApi;
import android.app.Activity;
import android.app.LoaderManager.LoaderCallbacks;
import android.content.ContentResolver;
import android.content.Context;
import android.content.CursorLoader;
import android.content.Intent;
import android.content.Loader;
import android.content.SharedPreferences;
import android.database.Cursor;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Build.VERSION;
import android.os.Build;
import android.os.Bundle;
import android.provider.ContactsContract;
import android.text.TextUtils;
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

import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

import eip.com.lizz.Network.Network;
import eip.com.lizz.Utils.UAlertBox;
import eip.com.lizz.Utils.UApi;

public class LoginActivity extends Activity implements LoaderCallbacks<Cursor> {

    private AutoCompleteTextView mEmailView;
    private EditText mPasswordView;
    private View mProgressView;
    private View mLoginFormView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        mEmailView = (AutoCompleteTextView) findViewById(R.id.email);
        populateAutoComplete();

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
                if (UApi.isOnline(LoginActivity.this))
                    attemptLogin();
                else
                    UAlertBox.alertOk(LoginActivity.this, getResources().getString(R.string.error), getResources().getString(R.string.code000));
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
        mEmailView.setError(null);
        mPasswordView.setError(null);

        String email = mEmailView.getText().toString();
        final String password = mPasswordView.getText().toString();

        boolean cancel = false;
        View focusView = null;

        if (TextUtils.isEmpty(email)) {
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
        }

        if (cancel) {
            focusView.requestFocus();
        } else {
            InputMethodManager inputManager = (InputMethodManager) this.getSystemService(Context.INPUT_METHOD_SERVICE);
            View view = this.getCurrentFocus();
            if (view != null) {
                inputManager.hideSoftInputFromWindow(view.getWindowToken(), InputMethodManager.HIDE_NOT_ALWAYS);
            }
            showProgress(true);

            String url = this.getResources().getString(R.string.url_api_komyla_no_suffix)
                    + this.getResources().getString(R.string.url_api_csrfToken);

            JsonObjectRequest getCSRF = new JsonObjectRequest
                    (Request.Method.GET, url, null, new Response.Listener<JSONObject>() {
                        @Override
                        public void onResponse(JSONObject response) {
                            try {
                                String token_csrf = response.get("_csrf").toString();
                                SharedPreferences sharedpreferences = LoginActivity.this.getSharedPreferences("eip.com.lizz", Context.MODE_PRIVATE);
                                sharedpreferences.edit().putString("eip.com.lizz._csrf", token_csrf).apply();

                                String url_api = LoginActivity.this.getResources().getString(R.string.url_api_komyla_no_suffix)
                                        + LoginActivity.this.getResources().getString(R.string.url_api_suffix)
                                        + LoginActivity.this.getResources().getString(R.string.url_api_createSession);

                                JSONObject data = new JSONObject();
                                data.put("_csrf", token_csrf);
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

                                        LoginActivity.this.finish();

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
                                                UAlertBox.alertOk(LoginActivity.this, getBaseContext().getResources().getString(R.string.error),
                                                        getBaseContext().getResources().getString(R.string.error_server_ok_but_fail_login)
                                                                + getBaseContext().getResources().getString(R.string.code051));
                                                break;
                                            }
                                            case 400: {
                                                UAlertBox.alertOk(LoginActivity.this, getBaseContext().getResources().getString(R.string.error),
                                                        getBaseContext().getResources().getString(R.string.error_server_ok_but_fail_login)
                                                                + getBaseContext().getResources().getString(R.string.code054));
                                                break;
                                            }
                                            case 500: {
                                                UAlertBox.alertOk(LoginActivity.this, getBaseContext().getResources().getString(R.string.error),
                                                        getBaseContext().getResources().getString(R.string.error_server_ok_but_fail_login)
                                                                + getBaseContext().getResources().getString(R.string.code056));
                                                break;
                                            }
                                            default:
                                                UAlertBox.alertOk(LoginActivity.this, getBaseContext().getResources().getString(R.string.error),
                                                        getBaseContext().getResources().getString(R.string.unknow_error));
                                        }
                                    }
                                });
                                Network.getInstance(LoginActivity.this).addToRequestQueue(logUser);
                            } catch (JSONException e) {
                                e.printStackTrace();
                                UAlertBox.alertOk(LoginActivity.this, getResources().getString(R.string.error), getResources().getString(R.string.code000));
                            }
                        }
                    }, new Response.ErrorListener() {

                        @Override
                        public void onErrorResponse(VolleyError error) {
                            showProgress(false);
                            UAlertBox.alertOk(LoginActivity.this, getResources().getString(R.string.error), getResources().getString(R.string.code000));
                        }
                    });
            Network.getInstance(this).addToRequestQueue(getCSRF);
        }
    }

    public static boolean isEmailValid(String email) {
        return android.util.Patterns.EMAIL_ADDRESS.matcher(email).matches();
    }

    @TargetApi(Build.VERSION_CODES.HONEYCOMB_MR2)
    public void showProgress(final boolean show) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.HONEYCOMB_MR2) {
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
                new ArrayAdapter<String>(LoginActivity.this,
                        android.R.layout.simple_dropdown_item_1line, emailAddressCollection);

        mEmailView.setAdapter(adapter);
    }

}
