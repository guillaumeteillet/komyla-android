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
import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.io.InputStream;
import java.io.UnsupportedEncodingException;
import java.util.HashMap;
import java.util.List;

import eip.com.lizz.Models.Cookies;
import eip.com.lizz.R;
import eip.com.lizz.Utils.UApi;

public class                        UpdateCreditCardToAPI
        extends                     AsyncTask<HashMap<String, String>, Void, String>{

    private final   String          _tokenCSRF;
    private final   Context         _context;
    private         List<Cookie>    _cookies;
    private final   String          _cardId;

    public                          UpdateCreditCardToAPI(String _tokenCSRF, String cardId, Context _context) {

        this._tokenCSRF = _tokenCSRF;
        this._context = _context;
        this._cardId = cardId;
    }

    public String                   updateCreditCard(HashMap<String, String> modifiedFields)
            throws                  Exception
    {

        String url = _context.getResources().getString(R.string.url_api_final_v1)
                + _context.getResources().getString(R.string.url_api_update_creditCard)
                + _cardId;

        DefaultHttpClient httpClient = new DefaultHttpClient();
        httpClient.getParams().setParameter(ClientPNames.COOKIE_POLICY, CookiePolicy.BROWSER_COMPATIBILITY);
        httpClient.setCookieStore(new BasicCookieStore());

        _cookies = Cookies.loadSharedPreferencesCookie(_context);
        httpClient.getCookieStore().addCookie(_cookies.get(0));
        HttpPost httppost = new HttpPost(url);

        String json;
        InputStream inputStream = null;

        JSONObject dataToSend = new JSONObject();
        dataToSend.put("_csrf", _tokenCSRF);
        for (String key : modifiedFields.keySet()) {
            dataToSend.put(key, modifiedFields.get(key));
        }

        json = dataToSend.toString();

        StringEntity stringEntity = new StringEntity(json);

        httppost.setEntity(stringEntity);
        httppost.setHeader("Accept", "application/json");
        httppost.setHeader("Content-type", "application/json");

        HttpResponse httpResponse = httpClient.execute(httppost);
        inputStream = httpResponse.getEntity().getContent();

        int responseCode = httpResponse.getStatusLine().getStatusCode();
        Log.d("GetPaymentMethods", "Le code de retour est: " + responseCode +
                " et le contenu est: " + UApi.convertStreamToString(inputStream));

        return Integer.toString(responseCode);
    }
    @Override
    protected String                doInBackground(HashMap<String, String>... params) {
        try {
            return updateCreditCard(params[0]);
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }
}
