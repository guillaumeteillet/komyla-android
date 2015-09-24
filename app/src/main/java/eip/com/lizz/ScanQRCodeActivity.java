package eip.com.lizz;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.ActivityInfo;
import android.content.pm.PackageManager;
import android.hardware.Camera;
import android.hardware.Camera.AutoFocusCallback;
import android.hardware.Camera.Parameters;
import android.hardware.Camera.PreviewCallback;
import android.hardware.Camera.Size;
import android.os.Bundle;
import android.os.Handler;
import android.support.v7.app.ActionBarActivity;
import android.util.Log;
import android.view.Gravity;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.Button;
import android.widget.FrameLayout;
import android.widget.TextView;
import android.widget.Toast;

import net.sourceforge.zbar.Config;
import net.sourceforge.zbar.Image;
import net.sourceforge.zbar.ImageScanner;
import net.sourceforge.zbar.Symbol;
import net.sourceforge.zbar.SymbolSet;

import org.apache.http.HttpResponse;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.io.InputStream;

import eip.com.lizz.QueriesAPI.GetTransactionFromAPI;
import eip.com.lizz.Utils.UAlertBox;
import eip.com.lizz.Utils.UApi;
import eip.com.lizz.Utils.UNetwork;

/* Import ZBar Class files */

public class ScanQRCodeActivity extends ActionBarActivity
{
    private Camera mCamera;
    private CameraPreview mPreview;
    private Handler autoFocusHandler;

    TextView scanText;
    Button scanButton;

    private Toast msg;

    ImageScanner scanner;

    private boolean barcodeScanned = false;
    private boolean previewing = true;

    static {
        System.loadLibrary("iconv");
    }

    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_scan);

        setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);

        autoFocusHandler = new Handler();
        mCamera = getCameraInstance();

        scanner = new ImageScanner();
        scanner.setConfig(0, Config.X_DENSITY, 3);
        scanner.setConfig(0, Config.Y_DENSITY, 3);

        mPreview = new CameraPreview(this, mCamera, previewCb, autoFocusCB);
        FrameLayout preview = (FrameLayout)findViewById(R.id.cameraPreview);
        preview.addView(mPreview);
    }

    public void onPause() {
        super.onPause();
        releaseCamera();
    }

    public void onStop() {
        super.onStop();
        releaseCamera();
    }

    /** A safe way to get an instance of the Camera object. */
    public static Camera getCameraInstance(){
        Camera c = null;
        try {
            c = Camera.open();
        } catch (Exception e){
        }
        return c;
    }

    private void releaseCamera() {
        if (mCamera != null) {
            previewing = false;
            mCamera.setPreviewCallback(null);
            mPreview.getHolder().removeCallback(mPreview);
            mCamera.release();
            mCamera = null;
            scanner = null;
            mPreview = null;
        }
    }

    private Runnable doAutoFocus = new Runnable() {
        public void run() {
            if (previewing)
                mCamera.autoFocus(autoFocusCB);
        }
    };

    PreviewCallback previewCb = new PreviewCallback() {
        public void onPreviewFrame(byte[] data, Camera camera) {
            Camera.Parameters parameters = camera.getParameters();
            final SharedPreferences sharedpreferences = getSharedPreferences("eip.com.lizz", Context.MODE_PRIVATE);
            Boolean flash = sharedpreferences.getBoolean("eip.com.lizz.flash", false);
            if (flash) {
                parameters.setFlashMode(Parameters.FLASH_MODE_TORCH);
            }
            else {
                parameters.setFlashMode(Camera.Parameters.FLASH_MODE_OFF);
            }
            mCamera.setParameters(parameters);
            Size size = parameters.getPreviewSize();

            Image barcode = new Image(size.width, size.height, "Y800");
            barcode.setData(data);

            int result = scanner.scanImage(barcode);

            if (result != 0) {
                previewing = false;
                mCamera.setPreviewCallback(null);
                mCamera.stopPreview();

                SymbolSet syms = scanner.getResults();
                for (Symbol sym : syms) {
                        String contents = sym.getData();
                        if (contents != null) {
                            if (contents.length() >= 27) {
                                String urlLizzOrNot = contents.substring(0, 27);
                                if (urlLizzOrNot.equals(getResources().getString(R.string.urllizzcode))) {

                                    final String productCode = contents.replace(getResources().getString(R.string.urllizzcode), "");

                                    Intent resultIntent = new Intent();
                                    resultIntent.putExtra("ProductURL", productCode);
                                    setResult(Activity.RESULT_OK, resultIntent);
                                    finish();

                                    /*final boolean isInternet = UNetwork.checkInternetConnection(getBaseContext());
                                    final boolean isMobile = UNetwork.isMobileAvailable(getBaseContext());
                                   *//* MediaPlayer mp = MediaPlayer.create(ScanQRCodeActivity.this, R.raw.beep);
                                    mp.setOnCompletionListener(new MediaPlayer.OnCompletionListener() {

                                        @Override
                                        public void onCompletion(MediaPlayer mp) {
                                            // TODO Auto-generated method stub
                                            mp.release();
                                        }

                                    });
                                    mp.start(); *//*
                                    final String unique_code = contents.replace(getResources().getString(R.string.urllizzcode), "");
                                    if (isInternet)
                                    {
                                        final ProgressDialog progress = ProgressDialog.show(ScanQRCodeActivity.this, getResources().getString(R.string.pleasewait), getResources().getString(R.string.pleasewaitgetTransaction), true);
                                        new Thread(new Runnable() {
                                            @Override
                                            public void run()
                                            {
                                                analyzeQRCode(progress, unique_code);
                                            }
                                        }).start();
                                    }
                                    else if (isMobile)
                                    {
                                        final AlertDialog.Builder alert = UAlertBox.alert(ScanQRCodeActivity.this, getResources().getString(R.string.dialog_no_internet), getResources().getString(R.string.dialog_no_internet_label));
                                        alert.setPositiveButton(getResources().getString(R.string.dialog_ok), new DialogInterface.OnClickListener() {
                                            public void onClick(DialogInterface dialog, int whichButton) {
                                                Intent payement = new Intent(getBaseContext(), PayementSMSActivity.class);
                                                payement.putExtra("unique_code", unique_code);
                                                startActivity(payement);
                                                finish();
                                            }
                                        });
                                        alert.show();
                                    }
                                    else
                                    {
                                        final AlertDialog.Builder alert = UAlertBox.alert(ScanQRCodeActivity.this, getResources().getString(R.string.dialog_title_no_internet), getResources().getString(R.string.dialog_no_network_pay));
                                        alert.setPositiveButton(getResources().getString(R.string.dialog_ok), new DialogInterface.OnClickListener() {
                                            public void onClick(DialogInterface dialog, int whichButton) {
                                                finish();
                                            }
                                        });
                                        alert.show();
                                    }*/

                                } else {
                                    errorQRCode();
                                }
                            }
                            else
                            {
                                errorQRCode();
                            }
                        } else {
                            finish();
                        }
                    barcodeScanned = true;
                }
            }
        }
    };

    private void analyzeQRCode(ProgressDialog progress, String uniqueCode)
    {
        Log.d("RETOUR API", ">>>" + uniqueCode);
        String[] part_url = uniqueCode.split("/");
        Log.d("RETOUR API", ">>>" + part_url[0]);
        Log.d("RETOUR API", ">>>" + part_url[1]);
        String[] infos = part_url[1].split("\\?");
        String code = infos[0];
        String data = infos[1];
        if(part_url[0].equals("d"))
        {
            Log.d("RETOUR API", ">>> DIRECT >> "+ code + "--"+ data);
            Intent payement = new Intent(getBaseContext(), PayementActivity.class);
            payement.putExtra("unique_code", code);
            payement.putExtra("data", data);
            startActivity(payement);
        }
        else if(part_url[0].equals("p"))
        {
            Log.d("RETOUR API", ">>> PANIER");
        }
        progress.dismiss();
    }

    private void dataAPI(ProgressDialog progress, HttpResponse httpResponse, String unique_code) {
        InputStream inputStream = null;
        try {
            progress.dismiss();
            Log.d("RETOUR API", ">>>" + httpResponse);
            Log.d("RETOUR API", ">>>" + unique_code);
            inputStream = httpResponse.getEntity().getContent();
            String jString =  UApi.convertStreamToString(inputStream);
            JSONObject jObj = new JSONObject(jString);

            int responseCode = httpResponse.getStatusLine().getStatusCode();

            Log.d("RETOUR API", ">>>" + responseCode);
            Log.d("RETOUR API", ">>>"+jObj.toString());
            Log.d("RETOUR API", ">>>"+jObj);

            if (responseCode == 200)
                API_200(jString, unique_code);
            else if (responseCode == 400)
                API_400(jObj);
            else if (responseCode == 403 || responseCode == 401)
                API_401_403();

        } catch (IOException e) {
            progress.dismiss();
            e.printStackTrace();
        } catch (Exception e) {
            progress.dismiss();
            e.printStackTrace();
        }
    }

    private void API_401_403() {
        Toast.makeText(getBaseContext(), getResources().getString(R.string.error_403_token_expire) + getResources().getString(R.string.error_403_passwordChange), Toast.LENGTH_LONG).show();
        MenuLizz.logout(ScanQRCodeActivity.this);
    }

    private void API_400(JSONObject jObj) throws JSONException {
        if (jObj.has(getResources().getString(R.string.api_error_checkout_error)))
        {
            Log.d("API400", ">"+jObj.getString(getResources().getString(R.string.api_error_checkout_error)));
            if (jObj.getString(getResources().getString(R.string.api_error_checkout_error)).equals(getResources().getString(R.string.api_error_no_pending_ticket)))
            {
                new Thread(new Runnable() {
                    @Override
                    public void run() {
                        runOnUiThread(new Runnable() {
                            public void run() {

                                final AlertDialog.Builder alert = UAlertBox.alert(ScanQRCodeActivity.this, getResources().getString(R.string.error), getResources().getString(R.string.error_400_transaction_empty));
                                alert.setPositiveButton(getResources().getString(R.string.dialog_ok), new DialogInterface.OnClickListener() {
                                    public void onClick(DialogInterface dialog, int whichButton) {
                                        finish();
                                    }
                                });
                                alert.show();
                            }
                        });
                    }
                }).start();
            }
            else
            {
                new Thread(new Runnable() {
                    @Override
                    public void run() {
                        runOnUiThread(new Runnable() {
                            public void run() {

                                final AlertDialog.Builder alert = UAlertBox.alert(ScanQRCodeActivity.this, getResources().getString(R.string.error), getResources().getString(R.string.error_400_checkout_empty));
                                alert.setPositiveButton(getResources().getString(R.string.dialog_ok), new DialogInterface.OnClickListener() {
                                    public void onClick(DialogInterface dialog, int whichButton) {
                                        finish();
                                    }
                                });
                                alert.show();
                            }
                        });
                    }
                }).start();
            }

        }
        else if (jObj.has(getResources().getString(R.string.api_error_qrcode_empty)))
        {
            final AlertDialog.Builder alert = UAlertBox.alert(ScanQRCodeActivity.this, getResources().getString(R.string.error), getResources().getString(R.string.error_400_qrcode_empty));
            alert.setPositiveButton(getResources().getString(R.string.dialog_ok), new DialogInterface.OnClickListener() {
                public void onClick(DialogInterface dialog, int whichButton) {
                    finish();
                }
            });
            alert.show();
        }
    }

    private void API_200(String jObjString, String unique_code) {
        Intent payement = new Intent(getBaseContext(), PayementActivity.class);
        payement.putExtra("jObjString", jObjString);
        payement.putExtra("unique_code", unique_code);
        startActivity(payement);
        finish();
    }

    private void errorQRCode()
    {

        mCamera.setPreviewCallback(previewCb);
        mCamera.startPreview();
        previewing = true;

        if(null == msg)
        {
            msg = Toast.makeText(getBaseContext(), getResources().getString(R.string.errorQRCodeNotLizz), Toast.LENGTH_SHORT);
            msg.setGravity(Gravity.CENTER, msg.getXOffset() / 2, msg.getYOffset() / 2);
            msg.show();

            new Handler().postDelayed(new Runnable()
            {
                public void run()
                {
                    msg = null;

                }
            }, 2000);

        }
        mCamera.autoFocus(autoFocusCB);
    }

    AutoFocusCallback autoFocusCB = new AutoFocusCallback() {
        public void onAutoFocus(boolean success, Camera camera) {
            autoFocusHandler.postDelayed(doAutoFocus, 1000);
        }
    };

    @Override
    public void onResume(){
        super.onResume();

        if (mCamera == null)
        {
            finish();
        }
    }

    public boolean onPrepareOptionsMenu(Menu menu) {

        boolean available = getPackageManager().hasSystemFeature(PackageManager.FEATURE_CAMERA_FLASH);

        if (!available)
            menu.removeItem(R.id.action_flash);
        else
        {
            SharedPreferences sharedpreferences = getSharedPreferences("eip.com.lizz", Context.MODE_PRIVATE);
            Boolean flashlight = sharedpreferences.getBoolean("eip.com.lizz.flash", false);

            MenuItem flash = menu.findItem(R.id.action_flash);
            if (flashlight)
                flash.setTitle(R.string.action_flash_off);
            else
                flash.setTitle(R.string.action_flash_on);
        }

        return true;
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.scanner_menu, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        return MenuLizz.scan_menu(item, ScanQRCodeActivity.this);
    }
}
