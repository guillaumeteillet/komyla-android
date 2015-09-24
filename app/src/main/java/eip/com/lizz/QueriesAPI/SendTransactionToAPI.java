package eip.com.lizz.QueriesAPI;

import android.content.Context;
import android.os.AsyncTask;
import android.util.Log;

import org.apache.http.HttpResponse;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.client.params.ClientPNames;
import org.apache.http.client.params.CookiePolicy;
import org.apache.http.cookie.Cookie;
import org.apache.http.entity.StringEntity;
import org.apache.http.impl.client.BasicCookieStore;
import org.apache.http.impl.client.DefaultHttpClient;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.InputStream;
import java.util.List;

import eip.com.lizz.Models.Cookies;
import eip.com.lizz.R;


public class SendTransactionToAPI extends AsyncTask<Void, Void, HttpResponse> {
    private final String        _tokenCSRF;
    private final Context       _context;
    private List<Cookie>        _cookies;
    private String              productArray;

    private OnTaskExecutionFinished _task_finished_event;

    public interface OnTaskExecutionFinished
    {
        public void OnTaskFihishedEvent(HttpResponse jObj);
    }

    public void setOnTaskFinishedEvent(OnTaskExecutionFinished _event)
    {
        if(_event != null)
        {
            this._task_finished_event = _event;
        }
    }

    public SendTransactionToAPI(String _tokenCSRF, Context _context, String productArray) {
        this._tokenCSRF = _tokenCSRF;
        this._context = _context;
        this.productArray = productArray;
    }



    private HttpResponse send() throws Exception {

        String url_api = _context.getResources().getString(R.string.url_api_komyla_no_suffix)
                + _context.getResources().getString(R.string.url_api_suffix)
                + _context.getResources().getString(R.string.url_api_user_pay);
        DefaultHttpClient httpClient = new DefaultHttpClient();
        httpClient.getParams().setParameter(ClientPNames.COOKIE_POLICY, CookiePolicy.BROWSER_COMPATIBILITY);
        httpClient.setCookieStore(new BasicCookieStore());

        _cookies = Cookies.loadSharedPreferencesCookie(_context);
        httpClient.getCookieStore().addCookie(_cookies.get(0));
        HttpPost httppost = new HttpPost(url_api);
        String json = "";
        InputStream inputStream = null;
        try {
            JSONObject products = new JSONObject();

            JSONArray productArray = new JSONArray(this.productArray);

            JSONObject dataToSend = new JSONObject();
            dataToSend.put("_csrf", _tokenCSRF);
            dataToSend.put("products", productArray);
            dataToSend.put("cardInd", 1);

            json = dataToSend.toString();

            Log.d("API", ">>>---->>>>" + json);

        } catch (JSONException e) {
            e.printStackTrace();
        }
            StringEntity se = new StringEntity(json);
            httppost.setEntity(se);
            httppost.setHeader("Accept", "application/json");
            httppost.setHeader("Content-type", "application/json");

            return httpClient.execute(httppost);
    }

    @Override
    protected void onPostExecute(final HttpResponse httpResponse) {
        this._task_finished_event.OnTaskFihishedEvent(httpResponse);
    }

    @Override
    protected HttpResponse doInBackground(Void... params) {
        try {
            return send();
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }
}
