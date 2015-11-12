package eip.com.lizz.Network;

import android.app.Activity;
import android.content.Context;
import android.content.SharedPreferences;

import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;

import org.json.JSONException;
import org.json.JSONObject;

import eip.com.lizz.R;
import eip.com.lizz.Utils.UAlertBox;

/**
 * Created by fortin_j on 11/12/15.
 */
public class POST_LogUser
{
    private String                          URL         = null;
    private Activity                        mActivity   = null;
    private Context                         mContext    = null;
    private Response.ErrorListener          mError      = null;
    private Response.Listener<JSONObject>   mResponse   = null;
    private JsonObjectRequest               mRequest    = null;

    public POST_LogUser(Activity activity, Context context, Response.Listener<JSONObject> response, Response.ErrorListener error)
    {
        mActivity = activity;
        mContext = context;
        mError = error;
        mResponse = response;
        URL = context.getResources().getString(R.string.url_api_komyla_no_suffix)
                + context.getResources().getString(R.string.url_api_suffix)
                + context.getResources().getString(R.string.url_api_createSession);
    }
    public void create(JSONObject data)
    {
        mRequest = new JsonObjectRequest(Request.Method.POST, URL, data, new Response.Listener<JSONObject>() {
            @Override
            public void onResponse(JSONObject response) {
                if (mResponse != null)
                    mResponse.onResponse(response);
                try
                {
                    SharedPreferences sharedpreferences = mContext.getSharedPreferences("eip.com.lizz", Context.MODE_PRIVATE);
                    sharedpreferences.edit().putString("eip.com.lizz.firstname", response.getString("firstname")).apply();
                    sharedpreferences.edit().putString("eip.com.lizz.surname", response.getString("surname")).apply();
                    sharedpreferences.edit().putString("eip.com.lizz.email", response.getString("email")).apply();
                    sharedpreferences.edit().putString("eip.com.lizz.id_user", response.getString("id")).apply();
                    sharedpreferences.edit().putString("eip.com.lizz.phone", "0;").apply();
                    sharedpreferences.edit().putBoolean("eip.com.lizz.isLogged", true).apply();
                }
                catch (JSONException e) {
                    e.printStackTrace();
                    UAlertBox.alertOk(mActivity, mContext.getResources().getString(R.string.error), mContext.getResources().getString(R.string.code010));
                }
            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                if (mError != null)
                    mError.onErrorResponse(error);

                int statusCode = error.networkResponse.statusCode;
                switch (statusCode) {
                    case 403: {
                        UAlertBox.alertOk(mActivity, mContext.getResources().getString(R.string.error),
                                mContext.getResources().getString(R.string.error_server_ok_but_fail_login)
                                        + mContext.getResources().getString(R.string.code051));
                        break;
                    }
                    case 400: {
                        UAlertBox.alertOk(mActivity, mContext.getResources().getString(R.string.error),
                                mContext.getResources().getString(R.string.error_server_ok_but_fail_login)
                                        + mContext.getResources().getString(R.string.code054));
                        break;
                    }
                    case 500: {
                        UAlertBox.alertOk(mActivity, mContext.getResources().getString(R.string.error),
                                mContext.getResources().getString(R.string.error_server_ok_but_fail_login)
                                        + mContext.getResources().getString(R.string.code056));
                        break;
                    }
                    default:
                        UAlertBox.alertOk(mActivity, mContext.getResources().getString(R.string.error),
                                mContext.getResources().getString(R.string.unknow_error));
                }
            }
        });
    }

    public void add()
    {
        Network.getInstance(mContext).addToRequestQueue(mRequest);
    }

    public void run(JSONObject data)
    {
        create(data);
        add();
    }
}
