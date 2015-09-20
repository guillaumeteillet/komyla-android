package eip.com.lizz.Models;

import android.content.Context;
import android.content.SharedPreferences;
import android.preference.PreferenceManager;
import android.util.Base64;
import android.util.Base64InputStream;
import android.util.Base64OutputStream;

import org.apache.http.cookie.Cookie;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by guillaume on 22/01/15.
 */
public class Cookies {

    public static void saveSharedPreferencesCookies(List<Cookie> cookies, Context context) {
        SerializableCookie[] serializableCookies = new SerializableCookie[cookies.size()];
        for (int i=0;i<cookies.size();i++){
            SerializableCookie serializableCookie = new SerializableCookie(cookies.get(i));
            serializableCookies[i] = serializableCookie;
        }
        SharedPreferences preferences = PreferenceManager.getDefaultSharedPreferences(context);
        SharedPreferences.Editor editor = preferences.edit();
        ObjectOutputStream objectOutput;
        ByteArrayOutputStream arrayOutputStream = new ByteArrayOutputStream();
        try {
            objectOutput = new ObjectOutputStream(arrayOutputStream);


            objectOutput.writeObject(serializableCookies);
            byte[] data = arrayOutputStream.toByteArray();
            objectOutput.close();
            arrayOutputStream.close();

            ByteArrayOutputStream out = new ByteArrayOutputStream();
            Base64OutputStream b64 = new Base64OutputStream(out, Base64.DEFAULT);
            b64.write(data);
            b64.close();
            out.close();

            editor.putString("cookies", new String(out.toByteArray()));
            editor.apply();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public static List<Cookie> loadSharedPreferencesCookie(Context context) {
        SharedPreferences preferences = PreferenceManager.getDefaultSharedPreferences(context);
        byte[] bytes = preferences.getString("cookies", "{}").getBytes();
        if (bytes.length == 0 || bytes.length==2)
            return null;
        ByteArrayInputStream byteArray = new ByteArrayInputStream(bytes);
        Base64InputStream base64InputStream = new Base64InputStream(byteArray, Base64.DEFAULT);
        ObjectInputStream in;
        List<Cookie> cookies = new ArrayList<Cookie>();
        SerializableCookie[] serializableCookies;
        try {
            in = new ObjectInputStream(base64InputStream);
            serializableCookies = (SerializableCookie[]) in.readObject();
            for (int i=0;i<serializableCookies.length; i++){
                Cookie cookie = serializableCookies[i].getCookie();
                cookies.add(cookie);
            }
            return cookies;
        } catch (IOException e) {
            e.printStackTrace();
        } catch (ClassNotFoundException e) {
            e.printStackTrace();
        }
        return null;
    }
}
