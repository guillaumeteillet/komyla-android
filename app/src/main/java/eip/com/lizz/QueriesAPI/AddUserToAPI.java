package eip.com.lizz.QueriesAPI;

import android.app.Activity;
import android.os.AsyncTask;
import android.util.Log;

import org.apache.http.HttpResponse;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.client.params.ClientPNames;
import org.apache.http.client.params.CookiePolicy;
import org.apache.http.conn.HttpHostConnectException;
import org.apache.http.cookie.Cookie;
import org.apache.http.entity.StringEntity;
import org.apache.http.impl.client.BasicCookieStore;
import org.apache.http.impl.client.DefaultHttpClient;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.io.InputStream;
import java.util.List;

import eip.com.lizz.R;
import eip.com.lizz.Utils.UApi;

/**
 * Created by guillaume on 20/12/14.
 */
public class AddUserToAPI extends AsyncTask<Void, Void, JSONObject> {

    private String mFirstname;
    private String mSurname;
    private String mEmail;
    private String mPhone;
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

    public AddUserToAPI(String firstname, String surname, String email, String phone, String password, String tokenCSFR, Activity context, List<Cookie> cookies) {
        mFirstname = firstname;
        mSurname = surname;
        mEmail = email;
        mPhone = phone;
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
                + contextHere.getResources().getString(R.string.url_api_createUser);
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
                data.put("firstname", mFirstname);
                data.put("surname", mSurname);
                data.put("email", mEmail);
                data.put("phoneNumber", mPhone);
                data.put("password", mPassword);
                data.put("passwordConfirmation", mPassword);

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
        } catch (HttpHostConnectException r) {
            try {
                jObj = UApi.prepareJson(408, null, jObj, 408);
            } catch (Exception e) {
                e.printStackTrace();
            }
        }catch (ClientProtocolException e) {
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
}