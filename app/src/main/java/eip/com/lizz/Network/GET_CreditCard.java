package eip.com.lizz.Network;

import android.content.Context;

import com.android.volley.Response;

import org.json.JSONObject;

import eip.com.lizz.R;

/**
 * Created by fortin_j on 12/2/15.
 */
public class GET_CreditCard extends Request {

    public GET_CreditCard(Context context, Response.Listener<JSONObject> response, Response.ErrorListener error)
    {
        super(com.android.volley.Request.Method.GET, context.getResources().getString(R.string.url_api_komyla_no_suffix)
                + context.getResources().getString(R.string.url_api_suffix)
                + context.getResources().getString(R.string.url_api_get_paymentMethods), context, response, error);
    }
}
