package eip.com.lizz;

import android.content.Intent;
import android.support.v7.app.ActionBarActivity;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;


public class PayementWithUniqueCodeActivity extends ActionBarActivity {

    EditText uniqueCode;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_payement_with_unique_code);

        uniqueCode = (EditText) findViewById(R.id.uniqueCode);
        uniqueCode.addTextChangedListener(new TextWatcher() {

            public void afterTextChanged(Editable s) {
                String result = s.toString();
                String[] forbidden = {" ","a","b","c","d","e","f","g","h","i","j","k","l","m","n","o","p","q","r","s","t","u","v","w","x","y","z",",",".","@","#","$","%","&","-","+","(",")","*","\"","'",":",";","!","?","_","/","~","`","|","•","√","π","÷","×","¶","∆","}","{","=","°","^","¥","€","¢","£","\\","©","®","™","℅","[","]",">","<"};
                String[] allowed = {"","A","B","C","D","E","F","G","H","I","J","K","L","M","N","O","P","Q","R","S","T","U","V","W","X","Y","Z","","","","","","","","","","","","","","","","","","","","","","","","","","","","","","","","","","","","","","","","","","","","","","","",""};
                for (int index =0; index < forbidden.length; index++){
                    result = result.replace(forbidden[index], allowed[index]);
                }
                if (!s.toString().equals(result)) {
                    uniqueCode.setText(result.toUpperCase());
                    uniqueCode.setSelection(result.length());
                    // alert the user
                }
            }
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {}

            public void onTextChanged(CharSequence s, int start, int before, int count) {}
        });

        Button ok = (Button) findViewById(R.id.okay);
        ok.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                String unique_code = uniqueCode.getText().toString();
                Intent payement = new Intent(getBaseContext(), PayementActivity.class);
                payement.putExtra("unique_code", unique_code);
                payement.putExtra("sms_active", false);
                startActivity(payement);
                finish();
            }
        });
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.unique_code_menu, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        return MenuLizz.unique_code_menu(item, PayementWithUniqueCodeActivity.this);
    }
}
