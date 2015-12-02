package eip.com.lizz.Network;

import android.app.Activity;
import android.content.Context;

import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;

import org.json.JSONObject;

import eip.com.lizz.R;
import eip.com.lizz.Utils.UAlertBox;

/**
 * Created by fortin_j on 11/12/15.
 */
public class POST_AddUser extends eip.com.lizz.Network.Request
{
    public POST_AddUser(Context context, Response.Listener<JSONObject> response, Response.ErrorListener error)
    {
        super(Request.Method.POST, context.getResources().getString(R.string.url_api_komyla_no_suffix)
                + context.getResources().getString(R.string.url_api_suffix)
                + context.getResources().getString(R.string.url_api_createUser), context, response, error);
    }
}

