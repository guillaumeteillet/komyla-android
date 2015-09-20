package eip.com.lizz.Utils;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.net.Uri;
import android.os.Environment;
import android.provider.MediaStore;

import java.io.File;
import java.io.IOException;

/**
 * Created by guillaume on 15/02/15.
 */
public class UPhoto {

    public static boolean launchAPN(Activity context, int code, String file_name, String dos) {

        final String dir = Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_PICTURES) + dos;
        File newdir = new File(dir);
        newdir.mkdirs();

        String file = dir+file_name;
        File newfile = new File(file);
        try {
            newfile.createNewFile();
        } catch (IOException e) {}

        Uri outputFileUri = Uri.fromFile(newfile);

        Intent cameraIntent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
        cameraIntent.putExtra(MediaStore.EXTRA_OUTPUT, outputFileUri);

        context.startActivityForResult(cameraIntent, code);
        return false;
    }
}
