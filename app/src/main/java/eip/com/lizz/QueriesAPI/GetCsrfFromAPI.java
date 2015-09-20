package eip.com.lizz.QueriesAPI;

import android.app.Activity;
import android.content.Context;
import android.content.SharedPreferences;
import android.os.AsyncTask;

import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.CookieStore;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.protocol.ClientContext;
import org.apache.http.conn.HttpHostConnectException;
import org.apache.http.cookie.Cookie;
import org.apache.http.impl.client.BasicCookieStore;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.protocol.BasicHttpContext;
import org.apache.http.protocol.HttpContext;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.io.InputStream;
import java.util.List;

import eip.com.lizz.Models.Cookies;
import eip.com.lizz.R;
import eip.com.lizz.Utils.UApi;

/**
 * Created by guillaume on 20/12/14.
 */
public class GetCsrfFromAPI extends AsyncTask<Void, Void, JSONObject> {

    /*  CSRFToken : On récupère le token et le cookie */

    private Activity contextHere;
    private String url_token;
    CookieStore cookieStore;

    private OnTaskExecutionFinished _task_finished_event;

    public interface OnTaskExecutionFinished
    {
        public void OnTaskFihishedEvent(String Reslut, List<Cookie> cookies);
    }

    public void setOnTaskFinishedEvent(OnTaskExecutionFinished _event)
    {
        if(_event != null)
        {
            this._task_finished_event = _event;
        }
    }

    public GetCsrfFromAPI(Activity context) {

        contextHere = context;
        url_token = contextHere.getResources().getString(R.string.url_api_komyla_no_suffix)
                + contextHere.getResources().getString(R.string.url_api_csrfToken);
    }

    @Override
    protected JSONObject doInBackground(Void... params) {

        JSONObject jObj = null;

        HttpClient httpclient = new DefaultHttpClient();
        HttpGet httpget = new HttpGet(url_token);
        cookieStore = new BasicCookieStore();
        HttpContext context = new BasicHttpContext();
        context.setAttribute(ClientContext.COOKIE_STORE, cookieStore);
        HttpResponse response;
        try {
            response = httpclient.execute(httpget, context);
            HttpEntity entity = response.getEntity();

            if (entity != null) {
                InputStream instream = entity.getContent();
                String result = UApi.convertStreamToString(instream);

                jObj = new JSONObject(result);

                instream.close();
            }
        } catch (HttpHostConnectException r) {
            try {
                jObj = new JSONObject();
                jObj.put("_csrf", "000x000");
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
        catch (JSONException e) {
            e.printStackTrace();
        } catch (ClientProtocolException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        } catch (Exception e) {
            e.printStackTrace();
        }

        return jObj;
    }

    public void onPostExecute(JSONObject jObj) {
        String token_csrf;
        if (jObj != null) {
            try {
                token_csrf = jObj.get("_csrf").toString();
                List<Cookie> cookies = cookieStore.getCookies();

                Cookies.saveSharedPreferencesCookies(cookies, contextHere);
                /*
                if (cookies.isEmpty()) {
                    System.out.println("None");
                } else {
                    for (int i = 0; i < cookies.size(); i++) {
                        System.out.println("ORIGIN :- " + cookies.get(i).toString());
                    }
                }

                */
                SharedPreferences sharedpreferences = contextHere.getSharedPreferences("eip.com.lizz", Context.MODE_PRIVATE);
                sharedpreferences.edit().putString("eip.com.lizz._csrf", token_csrf).apply();

                this._task_finished_event.OnTaskFihishedEvent(token_csrf, cookies);
            } catch (JSONException e) {
                e.printStackTrace();
            }
        }
    }
}