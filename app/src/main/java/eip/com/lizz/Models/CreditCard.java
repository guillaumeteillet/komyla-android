package eip.com.lizz.Models;


import eip.com.lizz.AbstractClasses.APaymentMethod;

public class        CreditCard extends APaymentMethod {
    private String  _id;
    private String  _cardNumber;
    private String  _expirationDateMonth;
    private String  _expirationDateYear;
    private String  _cryptogram;
    private String  _cardHolder;
    private String  _displayName;

    public CreditCard(String id, String cardNumber, String expirationMonth, String expirationYear,
                      String cryptogram, String cardHolder, String displayName)
    {
        set_paymentMethodType(Type.CREDIT_CARD);
        this.set_id(id);
        this.set_cardNumber(cardNumber);
        this.set_expirationDateMonth(expirationMonth);
        this.set_expirationDateYear(expirationYear);
        this.set_cryptogram(cryptogram);
        this.set_cardHolder(cardHolder);
        this.set_displayName(displayName);
    }



    @Override
    public String toString() {
        return
                "Credit Card ->" +
                " ID: " + _id +
                " Number: " + _cardNumber +
                " Expiration date: " + concatExpirationDate(_expirationDateMonth, _expirationDateYear) +
                " cryptogram: " + _cryptogram +
                " cardHolder: " + _cardHolder +
                " displayName: " + _displayName;
    }

    private String concatExpirationDate(String expirationMonth, String expirationYear) {
        return expirationMonth + "/" + expirationYear;
    }



    /* Getters and setters*/

    public String get_id() {
        return _id;
    }

    public void set_id(String _id) {
        this._id = _id;
    }

    public String get_cardNumber() {
        return _cardNumber;
    }

    public void set_cardNumber(String _cardNumber) {
        /*if (_cardNumber.length() == 4)
            this._cardNumber = "**** **** **** " + _cardNumber;*/
        this._cardNumber = _cardNumber;
    }

    public String get_expirationDateMonth() {
        return _expirationDateMonth;
    }

    public void set_expirationDateMonth(String _expirationDateMonth) {
        this._expirationDateMonth = _expirationDateMonth;
    }

    public String get_expirationDateYear() {
        return _expirationDateYear;
    }

    public void set_expirationDateYear(String _expirationDateYear) {
        this._expirationDateYear = _expirationDateYear;
    }

    public String get_cryptogram() {
        return _cryptogram;
    }

    public void set_cryptogram(String _cryptogram) {
        if (_cryptogram.isEmpty())
            this._cryptogram = "***";
        this._cryptogram = _cryptogram;
    }

    public String get_cardHolder() {
        return _cardHolder;
    }

    public void set_cardHolder(String _cardHolder) {
        this._cardHolder = _cardHolder;
    }

    public String get_displayName() {
        return _displayName;
    }

    public void set_displayName(String _displayName) {
        this._displayName = _displayName;
    }

}
