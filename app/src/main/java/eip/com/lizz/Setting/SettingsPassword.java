package eip.com.lizz.Setting;

import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.support.v7.app.ActionBarActivity;
import android.os.Bundle;
import android.text.InputType;
import android.text.method.PasswordTransformationMethod;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import org.apache.http.HttpResponse;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.io.InputStream;

import eip.com.lizz.QueriesAPI.UserChangePasswordAPI;
import eip.com.lizz.RegisterActivity;
import eip.com.lizz.Utils.UAlertBox;
import eip.com.lizz.MenuLizz;
import eip.com.lizz.R;
import eip.com.lizz.Utils.UApi;


public class SettingsPassword extends ActionBarActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_settings_password);

        final EditText mdp = (EditText) findViewById(R.id.newpassword);
        final EditText mdpConfirm = (EditText) findViewById(R.id.confirmpassword);

        final Button save = (Button) findViewById(R.id.save);
        save.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                saveNewMdp(mdp, mdpConfirm);
            }
        });

        final Button cancel = (Button) findViewById(R.id.cancel);
        cancel.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                finish();
            }
        });
    }

    private void saveNewMdp(final EditText mdp, EditText mdpConfirm)
    {
        if (mdp.getText().toString().equals(mdpConfirm.getText().toString()))
        {
            if (mdp.getText().toString().length() >= 8)
            {
                if (!RegisterActivity.isPasswordNotValidPolitic(mdp.getText().toString()))
                {
                    final EditText input = new EditText(getBaseContext());
                    input.setInputType(InputType.TYPE_TEXT_VARIATION_PASSWORD);
                    input.setTransformationMethod(PasswordTransformationMethod.getInstance());
                    input.setTextColor(Color.BLACK);
                    final AlertDialog.Builder alert = UAlertBox.alertInputOk(SettingsPassword.this, getResources().getString(R.string.dialog_title_confirm), getResources().getString(R.string.dialog_confirm_hint_pwd_change), input);
                    alert.setPositiveButton(getResources().getString(R.string.dialog_ok), new DialogInterface.OnClickListener() {
                        public void onClick(DialogInterface dialog, int whichButton) {
                            confirmPassword(input, mdp);
                        }

                    });
                    alert.setNegativeButton(getResources().getString(R.string.dialog_cancel), null);
                    alert.show();
                }
                else
                    UAlertBox.alertOk(SettingsPassword.this, getResources().getString(R.string.error), getResources().getString(R.string.passwordPoliticInvalid));
            }
            else
                UAlertBox.alertOk(SettingsPassword.this, getResources().getString(R.string.error), getResources().getString(R.string.passwordTooShort));
        }
        else
            UAlertBox.alertOk(SettingsPassword.this, getResources().getString(R.string.error), getResources().getString(R.string.wrongPasswordConfirm));

    }

    private void confirmPassword(EditText input, EditText mdp)
    {
        final String old_pwd = input.getText().toString();
        final String new_pwd = mdp.getText().toString();
        final ProgressDialog progress;
        InputMethodManager imm = (InputMethodManager)getSystemService(Context.INPUT_METHOD_SERVICE);
        imm.hideSoftInputFromWindow(input.getWindowToken(), 0);
        progress = ProgressDialog.show(SettingsPassword.this, getResources().getString(R.string.pleasewait), getResources().getString(R.string.pleasewaitsave), true);
        new Thread(new Runnable() {
            @Override
            public void run()
            {
                runOnUiThread(new Runnable() {
                    @Override
                    public void run()
                    {
                        UserChangePasswordAPI mAuthTask;
                        SharedPreferences sharedpreferences = getApplicationContext().getSharedPreferences("eip.com.lizz", Context.MODE_PRIVATE);
                        mAuthTask = new UserChangePasswordAPI(sharedpreferences.getString("eip.com.lizz._csrf", ""),getBaseContext(), new_pwd, old_pwd);
                        mAuthTask.setOnTaskFinishedEvent(new UserChangePasswordAPI.OnTaskExecutionFinished() {

                            @Override
                            public void OnTaskFihishedEvent(HttpResponse httpResponse) {
                                dataAPI(progress, httpResponse);
                            }
                        });
                        mAuthTask.execute();
                    }
                });
            }
        }).start();
    }

    private void dataAPI(ProgressDialog progress, HttpResponse httpResponse)
    {
        InputStream inputStream = null;
        try {
            progress.dismiss();
            inputStream = httpResponse.getEntity().getContent();
            String jString =  UApi.convertStreamToString(inputStream);
            JSONObject jObj = new JSONObject(jString);

            int responseCode = httpResponse.getStatusLine().getStatusCode();

            if (responseCode == 200)
                API_200();
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

    private void API_200()
    {
        Toast.makeText(getBaseContext(), getResources().getString(R.string.pwdsave), Toast.LENGTH_LONG).show();
        finish();
    }

    private  void API_400(JSONObject jObj)
    {
        if (jObj.has(getResources().getString(R.string.api_change_password_old)))
        {
            try {
                String oldPwd = jObj.get(getResources().getString(R.string.api_change_password_old)).toString();
                if (oldPwd.equals(getResources().getString(R.string.api_change_password_dontmatch)))
                {
                    UAlertBox.alertOk(SettingsPassword.this, getResources().getString(R.string.error), getResources().getString(R.string.error_400_old_password_dont_match));
                }
                else if (oldPwd.equals(getResources().getString(R.string.api_change_password_cannotbeempty)))
                {
                    UAlertBox.alertOk(SettingsPassword.this, getResources().getString(R.string.error), getResources().getString(R.string.error_400_old_password_empty));
                }
            } catch (JSONException e) {
                e.printStackTrace();
            }
        }
        else if (jObj.has(getResources().getString(R.string.api_change_password_new)))
        {
            try {
                String newPwd = jObj.get(getResources().getString(R.string.api_change_password_new)).toString();
                if (newPwd.equals(getResources().getString(R.string.api_change_password_cannotbeempty)))
                {
                    UAlertBox.alertOk(SettingsPassword.this, getResources().getString(R.string.error), getResources().getString(R.string.error_400_new_password_empty));
                }
                else if (newPwd.equals(getResources().getString(R.string.api_change_password_cannotbethesameasoldone)))
                {
                    UAlertBox.alertOk(SettingsPassword.this, getResources().getString(R.string.error), getResources().getString(R.string.error_400_new_old_same));
                }
            } catch (JSONException e) {
                e.printStackTrace();
            }
        }
    }

    private void API_401_403()
    {
        Toast.makeText(getBaseContext(), getResources().getString(R.string.error_403_token_expire) + getResources().getString(R.string.error_403_passwordChange), Toast.LENGTH_LONG).show();
        MenuLizz.logout(SettingsPassword.this);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.settings, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        return MenuLizz.settings_menu(item, getBaseContext(), SettingsPassword.this);
    }
}
