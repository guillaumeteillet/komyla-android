package eip.com.lizz.QueriesAPI;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.SharedPreferences;
import android.os.AsyncTask;
import android.widget.Toast;

import org.apache.http.HttpResponse;
import org.apache.http.client.methods.HttpPut;
import org.apache.http.client.params.ClientPNames;
import org.apache.http.client.params.CookiePolicy;
import org.apache.http.cookie.Cookie;
import org.apache.http.entity.ContentType;
import org.apache.http.entity.mime.HttpMultipartMode;
import org.apache.http.entity.mime.MultipartEntityBuilder;
import org.apache.http.entity.mime.content.FileBody;
import org.apache.http.entity.mime.content.StringBody;
import org.apache.http.impl.client.BasicCookieStore;
import org.apache.http.impl.client.DefaultHttpClient;
import org.json.JSONObject;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.util.List;

import eip.com.lizz.MenuLizz;
import eip.com.lizz.Models.Cookies;
import eip.com.lizz.R;
import eip.com.lizz.Utils.UAlertBox;
import eip.com.lizz.Utils.UApi;

/**
 * Created by guillaume on 06/02/15.
 */
public class AddFileToAPI extends AsyncTask<String, Void, HttpResponse> {

    private final Activity contextHere;
    private final String mUrl;
    private final String mapi;
    private final String mType;
    private final ProgressDialog mProgress;
    private List<Cookie> mCookies;


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

    public AddFileToAPI(final Activity context, String url, String api, ProgressDialog progress, String type) {
        contextHere = context;
        mUrl = url;
        mProgress = progress;
        mapi = api;
        mCookies = Cookies.loadSharedPreferencesCookie(context);
        mType = type;
    }

    @Override
    protected HttpResponse doInBackground(String... urls) {
        String tokenCSFR;
        SharedPreferences sharedpreferences = contextHere.getSharedPreferences("eip.com.lizz", Context.MODE_PRIVATE);
        tokenCSFR = sharedpreferences.getString("eip.com.lizz._csrf", "");

        DefaultHttpClient httpClient = new DefaultHttpClient();
        httpClient.getParams().setParameter(ClientPNames.COOKIE_POLICY, CookiePolicy.BROWSER_COMPATIBILITY);
        httpClient.setCookieStore(new BasicCookieStore());

        httpClient.getCookieStore().addCookie(mCookies.get(0));
        HttpResponse response = null;
        try {
            final String dir = mUrl;
            MultipartEntityBuilder builder = MultipartEntityBuilder.create();
            builder.setMode(HttpMultipartMode.BROWSER_COMPATIBLE);
            builder.addPart("_csrf", new StringBody(tokenCSFR, ContentType.TEXT_PLAIN));
            builder.addPart("pictureType", new StringBody(mType, ContentType.TEXT_PLAIN));
            builder.addPart("picture", new FileBody(new File(dir)));
            HttpPut put = new HttpPut(mapi);
            put.setEntity(builder.build());
            response = httpClient.execute(put);
        } catch (IOException e) {
            // TODO Auto-generated catch block
        } catch (Exception e) {
            e.printStackTrace();
        }

        return response;
    }

    @Override
    protected void onPostExecute(final HttpResponse jObj) {
        this._task_finished_event.OnTaskFihishedEvent(jObj);
    }

    public static void errors(final Activity context, HttpResponse response, final String OK, ProgressDialog progress)
    {
        int statusCode = response.getStatusLine().getStatusCode();
        AlertDialog.Builder alert;
        switch (statusCode)
        {
            case 200:
                try {
                    InputStream inputStream = response.getEntity().getContent();
                    if(inputStream != null) {
                        JSONObject jObj = UApi.convertStreamToJSONObject(inputStream);
                        if (jObj.getString("message").equals(context.getResources().getString(R.string.error_file_upload)))
                        {
                            progress.dismiss();
                            UAlertBox.alertOk(context, context.getResources().getString(R.string.error), context.getResources().getString(R.string.error_file_400));
                        }
                        else
                        {
                            alert = UAlertBox.alert(context, context.getResources().getString(R.string.labelDocumentOK), OK);
                            alert.setPositiveButton(context.getResources().getString(R.string.dialog_ok), new DialogInterface.OnClickListener() {
                                public void onClick(DialogInterface dialog, int whichButton) {
                                    context.finish();
                                }
                            });
                            alert.show();
                        }
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                }
                break;
            case 400:
                progress.dismiss();
                UAlertBox.alertOk(context, context.getResources().getString(R.string.error), context.getResources().getString(R.string.error_file_400));
                break;
            case 401:
                Toast.makeText(context, context.getResources().getString(R.string.error_connexion_csrf), Toast.LENGTH_LONG).show();
                MenuLizz.logout(context);
                break;
            case 403:
                Toast.makeText(context, context.getResources().getString(R.string.error_connexion_csrf), Toast.LENGTH_LONG).show();
                MenuLizz.logout(context);
                break;
            default:
                progress.dismiss();
                UAlertBox.alertOk(context, context.getResources().getString(R.string.error), context.getResources().getString(R.string.error_file_500));
                break;
        }
    }
}

