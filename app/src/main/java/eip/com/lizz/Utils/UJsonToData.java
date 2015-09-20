package eip.com.lizz.Utils;


import android.util.Log;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;

import eip.com.lizz.Models.CreditCard;

public class UJsonToData {
    public static ArrayList<CreditCard> getCreditCardListFromJSON(String resultSet) throws JSONException {
        ArrayList<CreditCard> creditCards = new ArrayList<>();
        //JSONArray jsonArray = null;
        JSONObject dataSet = new JSONObject(resultSet);
        JSONArray allCards;

        dataSet = dataSet.getJSONObject("cardsList");
        allCards = dataSet.getJSONArray("cards");

        Log.d("CreditCards", "cardListArray" + allCards.toString());

        if (allCards.length() == 0) {
            return null;
        }

        for (int i = 0; i < allCards.length(); i++) {
            if (allCards.getJSONObject(i).getInt("isDisabled") == 0) {
                JSONObject card = allCards.getJSONObject(i);
                creditCards.add(new CreditCard(
                        card.getString("cardInd"),
                        card.getJSONObject("card").getString("number"),
                        card.getJSONObject("card").getString("expirationDate").substring(0, 2),
                        card.getJSONObject("card").getString("expirationDate").substring(2, 4),
                        "",
                        "",
                        ""));
            }

        }

        /*jsonArray = new JSONArray(resultSet);
        for (int i = 0; i < jsonArray.length(); i++)
        {
            JSONObject oneObject = jsonArray.getJSONObject(i);
            creditCards.add(new CreditCard(
                    oneObject.getString("id"),
                    "************" + oneObject.getString("cardNumberHidden"),
                    oneObject.getString("cardExpireMonth"),
                    oneObject.getString("cardExpireYear"),
                    "***",
                    oneObject.getString("cardHolder"),
                    oneObject.getString("cardName")
            ));
        }*/

        return creditCards;
    }
}
