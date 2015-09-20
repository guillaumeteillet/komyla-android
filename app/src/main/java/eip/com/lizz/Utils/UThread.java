package eip.com.lizz.Utils;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.util.Log;

import org.apache.http.HttpResponse;

import java.io.IOException;
import java.io.InputStream;

import eip.com.lizz.QueriesAPI.AddFileToAPI;
import eip.com.lizz.R;

/**
 * Created by guillaume on 15/02/15.
 */
public class UThread {

    public static AddFileToAPI mAuthTask = null;

    public static void send(final Activity context, final String file_name, String pleasewait, final String OK, final String type) {


        final ProgressDialog progress = ProgressDialog.show(context, context.getResources().getString(R.string.pleasewait), pleasewait, true);
    new Thread(new Runnable() {
        @Override
        public void run()
        {
            mAuthTask = new AddFileToAPI(context, file_name, context.getResources().getString(R.string.url_api_final_v1)+context.getResources().getString(R.string.url_api_upload), progress, type);
            mAuthTask.setOnTaskFinishedEvent(new AddFileToAPI.OnTaskExecutionFinished() {
                @Override
                public void OnTaskFihishedEvent(HttpResponse response) {

                    try {
                        AddFileToAPI.errors(context, response, OK, progress);
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                }
            });
            mAuthTask.execute();
        }
    }).start();
    }
}
