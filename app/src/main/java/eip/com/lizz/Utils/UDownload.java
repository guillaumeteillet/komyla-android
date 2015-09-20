package eip.com.lizz.Utils;

import android.content.Context;
import android.util.Log;

import org.apache.http.util.ByteArrayBuffer;

import java.io.BufferedInputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.URL;
import java.net.URLConnection;

/**
 * Created by guillaume on 24/03/15.
 */
public class UDownload {

    public static String downloadFile(String DownloadUrl, String fileName, Context context) {
        try {
            URL url = new URL(DownloadUrl);

            URLConnection uconn = url.openConnection();
            uconn.setReadTimeout(5000);
            uconn.setConnectTimeout(5000);

            InputStream is = uconn.getInputStream();
            BufferedInputStream bufferinstream = new BufferedInputStream(is);
            ByteArrayBuffer baf = new ByteArrayBuffer(5000);
            int current = 0;
            while ((current = bufferinstream.read()) != -1) {
                baf.append((byte) current);
            }
            String filepath = context.getFilesDir().getPath();
            File f = new File(fileName);
            if (f.exists())
                f.delete();
            FileOutputStream fos = context.openFileOutput(fileName, Context.MODE_PRIVATE);
            fos.write(baf.toByteArray());
            fos.flush();
            fos.close();
            int dotindex = fileName.lastIndexOf('.');
            if (dotindex >= 0) {
                fileName = fileName.substring(0, dotindex);
            }
            return "ok";
        }
            catch(IOException e){
            }
        return "";
    }
}
