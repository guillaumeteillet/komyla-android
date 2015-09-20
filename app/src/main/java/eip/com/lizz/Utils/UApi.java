package eip.com.lizz.Utils;

import android.app.Activity;
import android.content.Context;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;


public class UApi {

    public static String convertStreamToString(InputStream is) throws Exception {
        BufferedReader reader = new BufferedReader(new InputStreamReader(is));
        StringBuilder sb = new StringBuilder();
        String line;

        while ((line = reader.readLine()) != null) {
            sb.append(line);
        }
        is.close();
        return sb.toString();
    }

    public static JSONObject convertStreamToJSONObject(InputStream inputStream)
            throws JSONException, IOException {
        BufferedReader bufferedReader = new BufferedReader( new InputStreamReader(inputStream));
        String line = "";
        String result = "";
        while((line = bufferedReader.readLine()) != null)
            result += line;

        //inputStream.close();
        return new JSONObject(result);
    }

    public static boolean isOnline(Activity context) {
        ConnectivityManager cm =
                (ConnectivityManager) context.getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo netInfo = cm.getActiveNetworkInfo();
        return netInfo != null && netInfo.isConnectedOrConnecting();
    }


    public static JSONObject prepareJson(int responseCode, InputStream inputStream, JSONObject jObj, int responseCodeElse) throws Exception {
        String result = "";
        if(inputStream != null)
        {
            result = UApi.convertStreamToString(inputStream);
            if (!result.equals("") && !result.equals("CSRF mismatch"))
            {
                jObj = new JSONObject(result);
            }
            else
                jObj = new JSONObject();
            jObj.put("responseCode",responseCode);
        }
        else
        {
            jObj = new JSONObject();
            jObj.put("responseCode", responseCodeElse);
        }
        return jObj;
    }

}
