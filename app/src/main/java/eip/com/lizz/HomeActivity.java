package eip.com.lizz;

import android.content.Intent;
import android.content.IntentSender;
import android.graphics.Typeface;
import android.os.Bundle;
import android.support.v4.app.FragmentActivity;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.VolleyError;
import com.facebook.Request;
import com.facebook.RequestAsyncTask;
import com.facebook.Response;
import com.facebook.Session;
import com.facebook.model.GraphUser;
import com.facebook.widget.LoginButton;
import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.GooglePlayServicesClient;
import com.google.android.gms.common.GooglePlayServicesUtil;
import com.google.android.gms.common.SignInButton;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.common.api.Scope;
import com.google.android.gms.plus.Plus;

import org.apache.http.HttpResponse;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.io.InputStream;
import java.util.Arrays;

import eip.com.lizz.Network.GET_CSRF;
import eip.com.lizz.Network.POST_LogUser;
import eip.com.lizz.Network.POST_SSOFB;
import eip.com.lizz.QueriesAPI.UserCreateSSOGoogle;
import eip.com.lizz.Utils.UAlertBox;
import eip.com.lizz.Utils.UApi;


public class HomeActivity extends FragmentActivity implements View.OnClickListener,
        GooglePlayServicesClient.ConnectionCallbacks, GooglePlayServicesClient.OnConnectionFailedListener, GoogleApiClient.ConnectionCallbacks, GoogleApiClient.OnConnectionFailedListener {

    private String fbAccessToken;
    public String email;
    private UserCreateSSOGoogle mAuthTask3 = null;
    private static final String TAG = "ExampleActivity";
    private static final int RC_SIGN_IN = 0;
    private GoogleApiClient mGoogleApiClient;
    private boolean mIntentInProgress;
    private boolean mSignInClicked;
    private ConnectionResult mConnectionResult;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_home);

        LoginButton authButton = (LoginButton) findViewById(R.id.btnConnectFB);
        authButton.setBackgroundResource(R.drawable.facebook);
        authButton.setReadPermissions(Arrays.asList("email", "user_friends", "user_birthday", "user_likes"));

        mGoogleApiClient = new GoogleApiClient.Builder(this)
                .addConnectionCallbacks(this)
                .addOnConnectionFailedListener(this)
                .addApi(Plus.API)
                .addScope(Plus.SCOPE_PLUS_LOGIN)
                .addScope(new Scope("https://www.googleapis.com/auth/userinfo.email"))
                .addScope(new Scope("https://www.googleapis.com/auth/userinfo.profile"))
                .build();

        Button lizzConnect = (Button) findViewById(R.id.btnConnectLizz);
        lizzConnect.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                Intent intent = new Intent(HomeActivity.this, LoginActivity.class);
                startActivity(intent);
            }
        });

        Button lizzNewAccount = (Button) findViewById(R.id.btnNewAccountLizz);
        lizzNewAccount.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                /*TelephonyManager tMgr = (TelephonyManager)getBaseContext().getSystemService(Context.TELEPHONY_SERVICE);
                if ((tMgr.getLine1Number() == null || tMgr.getLine1Number().equals(""))&& getResources().getString(R.string.debugOrProd).equals("PROD"))
                {
                    UAlertBox.alertOk(HomeActivity.this, getResources().getString(R.string.error), getResources().getString(R.string.code007));
                }
                else
                {
                    Intent intent = new Intent(HomeActivity.this, RegisterActivity.class);
                    startActivity(intent);
                }*/
                Intent intent = new Intent(HomeActivity.this, RegisterActivity.class);
                startActivity(intent);
            }
        });

        Button gplus = (Button) findViewById(R.id.plus_sign_in_button);
        Typeface myTypeface = Typeface.createFromAsset(getAssets(), "fonts/Roboto-Bold.ttf");
        gplus.setTypeface(myTypeface);
        gplus.setBackgroundResource(R.drawable.googleplus);
        gplus.setText(getResources().getString(R.string.google));
        // setGooglePlusButtonText(gplus,getResources().getString(R.string.google));
        if (!supportsGooglePlayServices()) {
            gplus.setVisibility(View.GONE);
            return;
        } else {
            gplus.setOnClickListener(new View.OnClickListener() {
                public void onClick(View v) {
                    mSignInClicked = true;
                    resolveSignInError();
                }
            });
        }
    }

    protected void setGooglePlusButtonText(SignInButton signInButton, String buttonText) {
        // Find the TextView that is inside of the SignInButton and set its text
        for (int i = 0; i < signInButton.getChildCount(); i++) {
            View v = signInButton.getChildAt(i);

            if (v instanceof TextView) {
                TextView tv = (TextView) v;
                tv.setText(buttonText);
                return;
            }
        }
    }

    /* A helper method to resolve the current ConnectionResult error. */
    private void resolveSignInError() {
        if (mConnectionResult.hasResolution()) {
            try {
                mIntentInProgress = true;
                startIntentSenderForResult(mConnectionResult.getResolution().getIntentSender(),
                        RC_SIGN_IN, null, 0, 0, 0);
            } catch (IntentSender.SendIntentException e) {
                // The intent was canceled before it was sent.  Return to the default
                // state and attempt to connect to get an updated ConnectionResult.
                mIntentInProgress = false;
                mGoogleApiClient.connect();
            }
        }
    }

    public void onConnectionFailed(ConnectionResult result) {
        if (!mIntentInProgress) {
            // Store the ConnectionResult so that we can use it later when the user clicks
            // 'sign-in'.
            mConnectionResult = result;

            if (mSignInClicked) {
                // The user has already clicked 'sign-in' so we attempt to resolve all
                // errors until the user is signed in, or they cancel.
                resolveSignInError();
            }
        }
    }

    protected void onStart() {
        super.onStart();
        mGoogleApiClient.connect();
    }

    protected void onStop() {
        super.onStop();

        if (mGoogleApiClient.isConnected()) {
            mGoogleApiClient.disconnect();
        }
    }

    public void onClick(View view) {
        Log.d("API", "CLICK");
        if (view.getId() == R.id.plus_sign_in_button
                && !mGoogleApiClient.isConnecting()) {
            mSignInClicked = true;
            resolveSignInError();
        }
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (requestCode == RC_SIGN_IN) {
            if (resultCode != RESULT_OK) {
                mSignInClicked = false;
            }

            mIntentInProgress = false;

            if (!mGoogleApiClient.isConnecting()) {
                mGoogleApiClient.connect();
            }
        } else {
            Session session = Session.getActiveSession();
            session.onActivityResult(this, requestCode, resultCode, data);
            if (session.isOpened()) {
                fbAccessToken = session.getAccessToken();
                RequestAsyncTask requestAsyncTask = Request.executeMeRequestAsync(session, new Request.GraphUserCallback() {
                    @Override
                    public void onCompleted(GraphUser user, Response response) {

                        new GET_CSRF(HomeActivity.this, getBaseContext(), new com.android.volley.Response.Listener<JSONObject>() {
                            @Override
                            public void onResponse(JSONObject response) {
                                try {
                                    String tokenCSRF = response.get("_csrf").toString();

                                    JSONObject data = new JSONObject();
                                    data.put("_csrf", tokenCSRF);
                                    data.put("access_token", fbAccessToken);

                                    new POST_SSOFB(HomeActivity.this, getBaseContext(), new com.android.volley.Response.Listener<JSONObject>() {
                                        @Override
                                        public void onResponse(JSONObject response) {
                                            try {
                                                API_200(response, "fb");
                                            } catch (JSONException e) {
                                                e.printStackTrace();
                                                ssoFBTokenEmpty();
                                            }
                                        }
                                    }, new com.android.volley.Response.ErrorListener() {
                                        @Override
                                        public void onErrorResponse(VolleyError error) {
                                            if (error != null && error.networkResponse != null && error.networkResponse.statusCode == 403)
                                                API_403();
                                            else
                                                ssoFBTokenEmpty();
                                        }
                                    }).run(data);
                                } catch (JSONException e) {
                                    e.printStackTrace();
                                }
                            }
                        }, null).run();
                    }
                });
            }
        }
    }

    private void ssoFBTokenEmpty() {
        UAlertBox.alertOk(HomeActivity.this, getResources().getString(R.string.error), getResources().getString(R.string.error_400_sso_fb_token_empty));
        Session session = Session.getActiveSession();
        if (session != null) {
            session.closeAndClearTokenInformation();
        }
    }

    private void dataAPI(HttpResponse httpResponse, String sso) {
        InputStream inputStream = null;
        try {
            inputStream = httpResponse.getEntity().getContent();
            String jString = UApi.convertStreamToString(inputStream);
            JSONObject jObj = new JSONObject(jString);

            int responseCode = httpResponse.getStatusLine().getStatusCode();

            if (sso.equals("fb"))
                data_fb(jObj, responseCode);
            else if (sso.equals("google"))
                data_googleplus(jObj, responseCode);
        } catch (IOException e) {
            e.printStackTrace();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void data_fb(JSONObject jObj, int responseCode) throws JSONException {
        if (responseCode == 200)
            API_200(jObj, "fb");
    }

    private void data_googleplus(JSONObject jObj, int responseCode) throws JSONException {
        if (responseCode == 200)
            API_200(jObj, "google");
        else if (responseCode == 400)
            API_400(jObj, "google");
        else if (responseCode == 403)
            API_403();
    }

    private void API_200(JSONObject jObj, String sso) throws JSONException {
        POST_LogUser.saveLocalParams(jObj.getString("firstname"), jObj.getString("surname"), jObj.getString("email"), jObj.getString("id"), "0;", getBaseContext());
        if (sso.equals("fb")) {
            Session session = Session.getActiveSession();
            if (session != null) {
                session.closeAndClearTokenInformation();
            }
        }
        Intent loggedUser = new Intent(getBaseContext(), HomeLizzActivity.class);
        loggedUser.putExtra("isLoginJustNow", true);
        loggedUser.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK | Intent.FLAG_ACTIVITY_NEW_TASK);// On supprime les vues précédentes, l'utilisateur est connecté.
        startActivity(loggedUser);
    }

    private void API_400(JSONObject jObj, String sso) {
        if (sso.equals("fb")) {
            if (jObj.has(getResources().getString(R.string.api_user_sso_fb_error))) {
                try {
                    String error = jObj.get(getResources().getString(R.string.api_user_sso_fb_error)).toString();
                    if (error.equals(getResources().getString(R.string.api_user_sso_fb_access_token_empty))) {
                        UAlertBox.alertOk(HomeActivity.this, getResources().getString(R.string.error), getResources().getString(R.string.error_400_sso_fb_token_empty));
                    } else // L'access token est expiré.
                    {
                        UAlertBox.alertOk(HomeActivity.this, getResources().getString(R.string.error), getResources().getString(R.string.error_400_sso_fb_token_expire));
                    }
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }
            Session session = Session.getActiveSession();
            if (session != null) {
                session.closeAndClearTokenInformation();
            }
        } else if (sso.equals("google")) {
            if (jObj.has(getResources().getString(R.string.api_user_sso_google_error))) {
                try {
                    String error = jObj.get(getResources().getString(R.string.api_user_sso_google_error)).toString();
                    if (error.equals(getResources().getString(R.string.api_user_sso_google_access_token_empty))) {
                        UAlertBox.alertOk(HomeActivity.this, getResources().getString(R.string.error), getResources().getString(R.string.error_400_sso_google_token_empty));
                    } else // L'access token est expiré.
                    {
                        UAlertBox.alertOk(HomeActivity.this, getResources().getString(R.string.error), getResources().getString(R.string.error_400_sso_google_token_expire));
                    }
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }
        }
    }

    private void API_403() {
        Toast.makeText(getBaseContext(), getResources().getString(R.string.error_403_token_expire), Toast.LENGTH_LONG).show();
    }

    public void onDisconnected() {
        Log.d(TAG, "disconnected");
    }

    @Override
    public void onConnected(Bundle connectionHint) {
        mSignInClicked = false;
        // TO DO : Verifier si le token Google+ existe déjà ou non

       /* */
        if (mGoogleApiClient.isConnected()) {
            new GET_CSRF(this, getBaseContext(), new com.android.volley.Response.Listener<JSONObject>() {
                @Override
                public void onResponse(JSONObject response) {
                    try {
                        String tokenCSRF = response.get("_csrf").toString();
                        mAuthTask3 = new UserCreateSSOGoogle(tokenCSRF, getBaseContext(), mGoogleApiClient);
                        mAuthTask3.setOnTaskFinishedEvent(new UserCreateSSOGoogle.OnTaskExecutionFinished() {
                            @Override
                            public void OnTaskFihishedEvent(HttpResponse httpResponse) {
                                dataAPI(httpResponse, "google");
                                Plus.AccountApi.clearDefaultAccount(mGoogleApiClient);
                                mGoogleApiClient.disconnect();
                                mGoogleApiClient.connect();
                            }
                        });
                        mAuthTask3.execute();
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                }
            }, null).run();
        }
    }

    public void onConnectionSuspended(int cause) {
        mGoogleApiClient.connect();
    }

    /**
     * Check if the device supports Google Play Services.  It's best
     * practice to check first rather than handling this as an error case.
     *
     * @return whether the device supports Google Play Services
     */
    private boolean supportsGooglePlayServices() {
        return GooglePlayServicesUtil.isGooglePlayServicesAvailable(this) ==
                ConnectionResult.SUCCESS;
    }
}