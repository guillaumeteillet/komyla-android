package eip.com.lizz.QueriesAPI;

import android.telephony.SmsManager;

import java.util.ArrayList;

/**
 * Created by guillaume on 28/03/15.
 */
public class SendSMSToAPI {

    public static int send(String message)
    {
        try {
            SmsManager smsManager = SmsManager.getDefault();
            ArrayList<String> parts = smsManager.divideMessage(message);
            smsManager.sendMultipartTextMessage("+8615652310367", null, parts, null, null);
            return 0;
        } catch (Exception e) {
            return 1;
        }
    }
}
