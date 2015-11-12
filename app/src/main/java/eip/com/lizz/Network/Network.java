package eip.com.lizz.Network;

import android.content.Context;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.toolbox.HttpClientStack;
import com.android.volley.toolbox.Volley;

import org.apache.http.client.protocol.ClientContext;
import org.apache.http.impl.client.BasicCookieStore;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.client.CookieStore;
import org.apache.http.protocol.BasicHttpContext;
import org.apache.http.protocol.HttpContext;

import java.net.CookieHandler;
import java.net.CookieManager;

/**
 * Created by fortin_j on 11/7/15.
 */
public class Network {

    private static Network  mNetwork        = null;
    private static Context  mContext        = null;

    private RequestQueue    mRequestQueue   = null;

    private Network(Context context)
    {
        mContext = context;
        mRequestQueue = getRequestQueue();
    }

    private RequestQueue getRequestQueue()
    {
        if (mRequestQueue == null)
        {
            CookieManager cookieManage = new CookieManager();
            CookieHandler.setDefault(cookieManage);
            mRequestQueue = Volley.newRequestQueue(mContext.getApplicationContext());
        }
        return mRequestQueue;
    }

    public static synchronized Network getInstance(Context context)
    {
        if (mNetwork == null)
            mNetwork = new Network(context);
        return mNetwork;
    }

    public <T> void addToRequestQueue(Request<T> req) {
        getRequestQueue().add(req);
    }

}
