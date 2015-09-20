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
import org.json.JSONObject;

import java.io.InputStream;
import java.util.List;

import eip.com.lizz.Utils.UApi;
import eip.com.lizz.Models.Cookies;
import eip.com.lizz.Models.CreditCard;
import eip.com.lizz.R;


public class UserChangePasswordAPI extends AsyncTask<Void, Void, HttpResponse> {
    private final String        _tokenCSRF;
    private final Context       _context;
    private List<Cookie>        _cookies;
    private String              mNewPassword;
    private String              mOldPassword;

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

    public UserChangePasswordAPI(String _tokenCSRF, Context _context, String newPassword, String oldPassword) {
        this._tokenCSRF = _tokenCSRF;
        this._context = _context;
        mNewPassword = newPassword;
        mOldPassword = oldPassword;
    }



    private HttpResponse changePassword() throws Exception {

        String url = _context.getResources().getString(R.string.url_api_final_v1)
                + _context.getResources().getString(R.string.url_api_user_update_password);
        DefaultHttpClient httpClient = new DefaultHttpClient();
        httpClient.getParams().setParameter(ClientPNames.COOKIE_POLICY, CookiePolicy.BROWSER_COMPATIBILITY);
        httpClient.setCookieStore(new BasicCookieStore());

        _cookies = Cookies.loadSharedPreferencesCookie(_context);
        httpClient.getCookieStore().addCookie(_cookies.get(0));
        HttpPost httppost = new HttpPost(url);

        String json;

        JSONObject dataToSend = new JSONObject();
        dataToSend.put("_csrf", _tokenCSRF);
        dataToSend.put("newPassword", mNewPassword);
        dataToSend.put("oldPassword", mOldPassword);

        json = dataToSend.toString();

        StringEntity stringEntity = new StringEntity(json);

        httppost.setEntity(stringEntity);
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
            return changePassword();
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }
}
