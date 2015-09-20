package eip.com.lizz.QueriesAPI;

import android.content.Context;
import android.os.AsyncTask;
import android.util.Log;

import org.apache.http.HttpResponse;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.params.ClientPNames;
import org.apache.http.client.params.CookiePolicy;
import org.apache.http.cookie.Cookie;
import org.apache.http.impl.client.BasicCookieStore;
import org.apache.http.impl.client.DefaultHttpClient;

import java.io.IOException;
import java.io.InputStream;
import java.util.List;

import eip.com.lizz.Utils.UApi;
import eip.com.lizz.Models.Cookies;
import eip.com.lizz.R;


public class GetPaymentMethodsFromAPI extends AsyncTask<Void, Void, String> {

    // Attributes
    private final Context   _context;
    private List<Cookie>    _cookies;

    public GetPaymentMethodsFromAPI(Context context) {
        this._context = context;
    }

    @Override
    protected String doInBackground(Void... params) {
        try {
            return downloadDataSet();
        } catch (IOException e) {
            e.printStackTrace();
        }
        return null;
    }

    @Override
    protected void onPostExecute(String s) {
        super.onPostExecute(s);
    }

    private String downloadDataSet() throws IOException {
        InputStream l_inputStream = null;

        try // TODO Gérer les codes d'erreurs autre que dans les logs
        {

            String url = _context.getResources().getString(R.string.url_api_final_v1)
                    + _context.getResources().getString(R.string.url_api_get_paymentMethods);
            DefaultHttpClient httpClient = new DefaultHttpClient();
            httpClient.getParams().setParameter(ClientPNames.COOKIE_POLICY, CookiePolicy.BROWSER_COMPATIBILITY);
            httpClient.setCookieStore(new BasicCookieStore());

            _cookies = Cookies.loadSharedPreferencesCookie(_context);

            httpClient.getCookieStore().addCookie(_cookies.get(0));
            HttpGet httpGet = new HttpGet(url);

            HttpResponse httpResponse = httpClient.execute(httpGet);
            l_inputStream = httpResponse.getEntity().getContent();

            int responseCode = httpResponse.getStatusLine().getStatusCode();

            try {
                Log.d("GetPaymentMethods", "Le code de retour est: " + responseCode + " et le contenu est: " + UApi.convertStreamToString(l_inputStream));
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
        finally
        {
            if (l_inputStream != null)
                l_inputStream.close();
        }
        return ("PlaceHolderString");
    }
}
