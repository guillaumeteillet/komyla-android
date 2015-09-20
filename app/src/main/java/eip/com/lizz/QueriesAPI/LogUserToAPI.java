package eip.com.lizz.QueriesAPI;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.AsyncTask;

import org.apache.http.HttpResponse;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.client.params.ClientPNames;
import org.apache.http.client.params.CookiePolicy;
import org.apache.http.cookie.Cookie;
import org.apache.http.entity.StringEntity;
import org.apache.http.impl.client.BasicCookieStore;
import org.apache.http.impl.client.DefaultHttpClient;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.io.InputStream;
import java.util.List;

import eip.com.lizz.HomeLizzActivity;
import eip.com.lizz.MainMenuActivity;
import eip.com.lizz.PayementPTPConfirmActivity;
import eip.com.lizz.R;
import eip.com.lizz.Utils.UAlertBox;
import eip.com.lizz.Utils.UApi;
import eip.com.lizz.Utils.UDownload;

/**
 * Created by guillaume on 21/12/14.
 */
public class LogUserToAPI extends AsyncTask<Void, Void, JSONObject> {

    private String mEmail;
    private String mPassword;
    private String mtokenCSFR;
    private Activity contextHere;
    private List<Cookie> mCookies;

    private OnTaskExecutionFinished _task_finished_event;

    public interface OnTaskExecutionFinished
    {
        public void OnTaskFihishedEvent(JSONObject jObj);
    }

    public void setOnTaskFinishedEvent(OnTaskExecutionFinished _event)
    {
        if(_event != null)
        {
            this._task_finished_event = _event;
        }
    }

    public LogUserToAPI(String email, String password, String tokenCSFR, Activity context, List<Cookie> cookies) {
        mEmail = email;
        mPassword = password;
        contextHere = context;
        mtokenCSFR = tokenCSFR;
        mCookies = cookies;
    }

    @Override
    protected JSONObject doInBackground(Void... params) {
        JSONObject jObj = null;
        String url_api = contextHere.getResources().getString(R.string.url_api_komyla_no_suffix)
                + contextHere.getResources().getString(R.string.url_api_suffix)
                + contextHere.getResources().getString(R.string.url_api_createSession);
        DefaultHttpClient httpClient = new DefaultHttpClient();
        httpClient.getParams().setParameter(ClientPNames.COOKIE_POLICY, CookiePolicy.BROWSER_COMPATIBILITY);
        httpClient.setCookieStore(new BasicCookieStore());

        httpClient.getCookieStore().addCookie(mCookies.get(0));
        HttpPost httppost = new HttpPost(url_api);
        String json;
        InputStream inputStream = null;
        try {
            JSONObject data = new JSONObject();
            try {
                data.put("_csrf", mtokenCSFR);
                data.put("email", mEmail);
                data.put("password", mPassword);

            } catch (JSONException e) {
                e.printStackTrace();
            }
            json = data.toString();
            StringEntity se = new StringEntity(json);
            httppost.setEntity(se);
            httppost.setHeader("Accept", "application/json");
            httppost.setHeader("Content-type", "application/json");
            HttpResponse httpResponse = httpClient.execute(httppost);
            inputStream = httpResponse.getEntity().getContent();

            int responseCode = httpResponse.getStatusLine().getStatusCode();
            switch(responseCode) {
                case 200:
                    jObj = UApi.prepareJson(responseCode, inputStream, jObj, 202);
                    break;
                case 400:
                    jObj = UApi.prepareJson(responseCode, inputStream, jObj, 400);
                    break;
                case 403:
                    jObj = UApi.prepareJson(responseCode, inputStream, jObj, 403);
                    break;
                default:
                    jObj = UApi.prepareJson(responseCode, inputStream, jObj, 500);
                    break;
            }

        } catch (ClientProtocolException e) {
            try {
               jObj = UApi.prepareJson(408, null, jObj, 408);
            } catch (Exception e1) {
                e1.printStackTrace();
            }
        } catch (IOException e) {
        } catch (Exception e) {
            e.printStackTrace();
        }

        return jObj;
    }

    @Override
    protected void onPostExecute(final JSONObject jObj) {
        this._task_finished_event.OnTaskFihishedEvent(jObj);
    }

    public static void LogUserSaveLocalParams(String lFirstname, String lSurname, String lEmail, String lId, String lPhone, final Context context, Activity activity)
    {
        String firstname, surname, email, phone, id;
        firstname = lFirstname;
        surname = lSurname;
        email = lEmail;
        id = lId;
        phone = lPhone; //obj.getString("phone"); // TODO : Récupérer les numéros de téléphone from l'API.
        SharedPreferences sharedpreferences = context.getSharedPreferences("eip.com.lizz", Context.MODE_PRIVATE);
        sharedpreferences.edit().putString("eip.com.lizz.firstname", firstname).apply();
        sharedpreferences.edit().putString("eip.com.lizz.surname", surname).apply();
        sharedpreferences.edit().putString("eip.com.lizz.email", email).apply();
        sharedpreferences.edit().putString("eip.com.lizz.id_user", id).apply();
        sharedpreferences.edit().putString("eip.com.lizz.phone", phone).apply();
        sharedpreferences.edit().putBoolean("eip.com.lizz.isLogged", true).apply();
    }

    public static void checkErrorsAndLaunch(JSONObject jObj, Activity activity, Context context) {
        try {
            if (jObj.get("responseCode").toString().equals("200"))
            {
                LogUserSaveLocalParams(jObj.getString("firstname"), jObj.getString("surname"), jObj.getString("email"), jObj.getString("id"), "0;", context, activity);
                activity.finish();

                //Intent loggedUser = new Intent(context, HomeLizzActivity.class);
                Intent loggedUser = new Intent(context, MainMenuActivity.class);

                loggedUser.putExtra("isLoginJustNow", true);
                loggedUser.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK|Intent.FLAG_ACTIVITY_NEW_TASK);// On supprime les vues précédentes, l'utilisateur est connecté.
                context.startActivity(loggedUser);
            }
            else if(jObj.get("responseCode").toString().equals("403"))
            {
                UAlertBox.alertOk(activity, context.getResources().getString(R.string.error), context.getResources().getString(R.string.error_server_ok_but_fail_login) + context.getResources().getString(R.string.code051));
            }
            else if (jObj.get("responseCode").toString().equals("400"))
            {
                String error = jObj.getString("message");
                if (error.equals("Unknown user"))
                    UAlertBox.alertOk(activity, context.getResources().getString(R.string.error), context.getResources().getString(R.string.error_server_ok_but_fail_login) + context.getResources().getString(R.string.code052));
                else if (error.equals("Invalid password"))
                    UAlertBox.alertOk(activity, context.getResources().getString(R.string.error), context.getResources().getString(R.string.error_server_ok_but_fail_login) + context.getResources().getString(R.string.code053));
                else if (error.equals("Invalid password"))
                    UAlertBox.alertOk(activity, context.getResources().getString(R.string.error), context.getResources().getString(R.string.error_server_ok_but_fail_login) + context.getResources().getString(R.string.code053));
            }
            else if (jObj.get("responseCode").toString().equals("500"))
            {
                UAlertBox.alertOk(activity, context.getResources().getString(R.string.error), context.getResources().getString(R.string.error_server_ok_but_fail_login) + context.getResources().getString(R.string.code056));
            }
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }
}