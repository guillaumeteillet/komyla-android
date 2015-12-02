package eip.com.lizz.Network;

import android.app.Activity;
import android.content.Context;
import android.widget.Toast;

import com.android.volley.Response;
import com.android.volley.VolleyError;

import org.json.JSONException;
import org.json.JSONObject;

import eip.com.lizz.R;

/**
 * Created by fortin_j on 12/1/15.
 */
public class POST_UpdateCreditCard extends Request
{
    public POST_UpdateCreditCard(final Activity activity, final Context context, String cardId)
    {
        super(com.android.volley.Request.Method.POST, context.getResources().getString(R.string.url_api_final_v1)
                + context.getResources().getString(R.string.url_api_update_creditCard) + cardId, context, new Response.Listener<JSONObject>() {
            @Override
            public void onResponse(JSONObject response) {
                Toast.makeText(context, context.getResources().getString(R.string.toast_valid_card_infos), Toast.LENGTH_LONG).show();
                activity.finish();
            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                if (error != null && error.networkResponse != null)
                {
                    String json = new String(error.networkResponse.data);
                    try {
                        JSONObject data = new JSONObject(json);
                        data = data.getJSONObject("result");

                        Toast.makeText(context, data.getString("shortMessage") + ": " + data.getString("longMessage"), Toast.LENGTH_LONG).show();

                    } catch (JSONException e) {
                        e.printStackTrace();
                        notifyUnknowError(context);
                    }
                }
                else
                    notifyUnknowError(context);
            }
        });
    }

    private static void notifyUnknowError(Context context)
    {
        Toast.makeText(context, context.getResources().getString(R.string.error_update_credit_card), Toast.LENGTH_LONG).show();
    }
}
