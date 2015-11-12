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
public class POST_AddUser
{
    private String                          URL         = null;
    private Activity                        mActivity   = null;
    private Context                         mContext    = null;
    private Response.ErrorListener          mError      = null;
    private Response.Listener<JSONObject>   mResponse   = null;
    private JsonObjectRequest               mRequest    = null;

    public POST_AddUser(Activity activity, Context context, Response.Listener<JSONObject> response, Response.ErrorListener error)
    {
        mActivity = activity;
        mContext = context;
        mError = error;
        mResponse = response;
        URL = context.getResources().getString(R.string.url_api_komyla_no_suffix)
                + context.getResources().getString(R.string.url_api_suffix)
                + context.getResources().getString(R.string.url_api_createUser);
    }

    public void create(JSONObject data)
    {
        mRequest = new JsonObjectRequest(Request.Method.POST, URL, data, new Response.Listener<JSONObject>() {
            @Override
            public void onResponse(JSONObject response) {
                if (mResponse != null)
                    mResponse.onResponse(response);
            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                UAlertBox.alertOk(mActivity, mContext.getResources().getString(R.string.error),
                        mContext.getResources().getString(R.string.error_server_ok_but_fail)
                                + mContext.getResources().getString(R.string.code006));
                if (mError != null)
                    mError.onErrorResponse(error);
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

