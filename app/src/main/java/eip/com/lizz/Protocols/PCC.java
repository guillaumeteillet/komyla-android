package eip.com.lizz.Protocols;

import android.app.Activity;
import android.content.Context;
import android.content.pm.PackageManager;
import android.util.Base64;
import android.util.Log;

import java.io.IOException;
import java.security.InvalidKeyException;
import java.security.NoSuchAlgorithmException;
import java.security.NoSuchProviderException;
import java.security.spec.InvalidKeySpecException;

import javax.crypto.BadPaddingException;
import javax.crypto.IllegalBlockSizeException;
import javax.crypto.NoSuchPaddingException;

import eip.com.lizz.Utils.UCrypt;

/**
 * Created by guillaume on 20/03/15.
 */
public class PCC {

    static String[] salts  = { "Mystery Spot", "Au clair de la lune", "Nikola Tesla", "île de pâques", "Cyril Hanouna", "Il court, il court le furet",
            "Le Triangle des Bermudes", "A la pêche aux moules", "Carlos", "Kalthoum Sarraï", "Rio Tinto", "Jacques Vileret", "Une souris verte",
            "Racetrack Playa", "Laurence Boccolini", "McMurdo Dry Valleys", "Nous n'irons plus au bois", "Leonardo da Vinci", "A la claire fontaine",
            "Clara Morgane", "Il était un p'tit cordonnier", "Le Lac Vostok", "Leonard Nimoy", "Le Mont Roraima", "C'est la mère Michel",
            "Jean Petit qui danse", "Ne pleure pas Jeannette", "Steve Jobs", "Le Fly Geyser", "Socotra" };

    public static String cryptped(String id_payement, String PIN, String email_sender, String unique_code, String id_user, String amount, Context context, Activity activity) throws IOException, NoSuchAlgorithmException, InvalidKeyException, PackageManager.NameNotFoundException, InvalidKeySpecException, NoSuchPaddingException, BadPaddingException, NoSuchProviderException, IllegalBlockSizeException {
        char[] pinArray = PIN.toCharArray();
        char[] uniqueArray = unique_code.toCharArray();

        String key = ""+uniqueArray[3]+pinArray[0]+uniqueArray[4]+"-"+uniqueArray[7]+pinArray[3]+" "+uniqueArray[9]+email_sender+uniqueArray[6]+uniqueArray[2]+" "+pinArray[1]+"-"+pinArray[2]+uniqueArray[1]+uniqueArray[8]+"--"+uniqueArray[6]+uniqueArray[0]+" "+uniqueArray[4]+"-"+uniqueArray[5]+id_payement;

        return sha256(key, id_user, amount, id_payement, unique_code, context, activity);
    }

    private static String sha256(String key, String id_user, String amount, String id_payement, String unique_code, Context context, Activity activity) throws IOException, NoSuchAlgorithmException, InvalidKeyException, PackageManager.NameNotFoundException, NoSuchPaddingException, BadPaddingException, IllegalBlockSizeException, NoSuchProviderException, InvalidKeySpecException {
        int i = 0;
        while (i < 30) {
            key = UCrypt.bin2hex(UCrypt.getHash(key + salts[i]));
            i++;
        }
        return print_secure(key, id_user, amount, id_payement, unique_code, context, activity);
    }

    private static String print_secure(String key, String id_user, String amount, String id_payement, String unique_code, final Context context, final Activity activity) throws IOException, NoSuchAlgorithmException, NoSuchProviderException, InvalidKeyException, PackageManager.NameNotFoundException, NoSuchPaddingException, BadPaddingException, InvalidKeySpecException, IllegalBlockSizeException {
        key = UCrypt.RSAEncrypt(id_payement + "-" + unique_code + "-" + key, context);
        return rsa_secure_format(key, id_payement, unique_code, id_user);
    }

    private static String rsa_secure_format(String key_secure, String id_payement, String receiver, String id_exp) {

        String two_random = UCrypt.random(2);
        String one_random = UCrypt.random(1);
        String final_message = "=C"+two_random+key_secure+one_random+id_exp+"=";
        return Base64.encodeToString(final_message.getBytes(), Base64.DEFAULT);
    }
}
