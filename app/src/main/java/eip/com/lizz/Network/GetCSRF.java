package eip.com.lizz.Network;

import android.content.Context;

import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;

import org.json.JSONObject;

import eip.com.lizz.R;

/**
 * Created by fortin_j on 11/11/15.
 */
public class GetCSRF {

    private String                          URL;
    private Context                         mContext;
    private Response.ErrorListener          mError;
    private Response.Listener<JSONObject>   mResponse;

    public GetCSRF(Context context, Response.Listener<JSONObject> response, Response.ErrorListener error)
    {
        mContext = context;
        mError = error;
        mResponse = response;
        URL = context.getResources().getString(R.string.url_api_komyla_no_suffix) + context.getResources().getString(R.string.url_api_csrfToken);
    }

    public void run()
    {
        JsonObjectRequest getCSRF = new JsonObjectRequest(Request.Method.GET, URL, null, mResponse, mError);
    }
}
