package eip.com.lizz.Network;

import android.app.Activity;
import android.content.Context;

import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;

import org.json.JSONObject;

/**
 * Created by fortin_j on 12/1/15.
 */
public abstract class Request
{
    private int                             mMethod;
    private String                          URL         = null;
    private Context                         mContext    = null;
    private Response.ErrorListener          mError      = null;
    private Response.Listener<JSONObject>   mResponse   = null;
    private JsonObjectRequest               mRequest    = null;

    public Request(int method, String url, Context context, Response.Listener<JSONObject> response, Response.ErrorListener error)
    {
        URL = url;
        mMethod = method;
        mContext = context;
        mResponse = response;
        mError = error;
    }

    private void _create(JSONObject data)
    {
        mRequest = new JsonObjectRequest(mMethod, URL, data, new Response.Listener<JSONObject>() {
            @Override
            public void onResponse(JSONObject response) {
                if (mResponse != null)
                    mResponse.onResponse(response);
            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                if (mError != null)
                    mError.onErrorResponse(error);
            }
        });
    }

    public void create(JSONObject data)
    {
        _create(data);
    }

    public void create()
    {
        _create(null);
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

    public Context getContext() { return mContext; }

    public String getURL() { return URL; }
    public void setURL(String url) { URL = url; }
}
