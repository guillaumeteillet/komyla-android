package eip.com.lizz.QueriesAPI;

import android.content.Context;
import android.os.AsyncTask;
import android.util.Log;

import org.apache.http.HttpResponse;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.client.params.ClientPNames;
import org.apache.http.client.params.CookiePolicy;
import org.apache.http.cookie.Cookie;
import org.apache.http.entity.StringEntity;
import org.apache.http.impl.client.BasicCookieStore;
import org.apache.http.impl.client.DefaultHttpClient;
import org.json.JSONObject;

import java.io.InputStream;
import java.util.List;

import eip.com.lizz.Utils.UApi;
import eip.com.lizz.Models.Cookies;
import eip.com.lizz.Models.CreditCard;
import eip.com.lizz.R;


public class                    DeleteCreditCardFromAPI extends AsyncTask<CreditCard, Void, String> {
    private final String        _tokenCSRF;
    private final Context       _context;
    private List<Cookie>        _cookies;

    public DeleteCreditCardFromAPI(String _tokenCSRF, Context _context) {
        this._tokenCSRF = _tokenCSRF;
        this._context = _context;
    }

    private String removeCreditCard(CreditCard creditCard) throws Exception {

        String url = _context.getResources().getString(R.string.url_api_komyla_no_suffix)
                + _context.getResources().getString(R.string.url_api_suffix)
                + _context.getResources().getString(R.string.url_api_delete_creditCard);

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
        dataToSend.put("cardInd", creditCard.get_id());

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
    protected String doInBackground(CreditCard... params) {
        try {
            return removeCreditCard(params[0]);
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }
}
