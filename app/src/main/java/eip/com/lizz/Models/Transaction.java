package eip.com.lizz.Models;

import java.util.Date;

/**
 * Created by fortin_j on 9/9/15.
 */
public class Transaction {

    private Boolean _paid;

    private Double _amount;

    private String  _name;
    private String  _ref;
    private String  _cartId;

    private Date    _createdAt = null;
    private Date    _updateAt = null;

    public Transaction(Boolean paid, String name, String ref, String createdAt, String updatedAt, String shoppingCart, Double amount)
    {
        this.setPaid(paid);
        this.setName(name);
        this.setRef(ref);
        this.setCreatedAt(createdAt);
        this.setUpdatedAt(updatedAt);
        this.setShoppingCart(shoppingCart);
        this.setAmount(amount);
    }

    public Double getAmount() { return this._amount; }

    public void setPaid(Boolean paid) { this._paid = paid; }

    public void setName(String name) { this._name = name; }

    public void setRef(String ref) { this._ref = ref; }

    public void setShoppingCart(String shoppingCart) { this._cartId = shoppingCart; }

    public void setAmount(Double amount) { this._amount = Double.valueOf(amount); }

    public void setCreatedAt(String createdAt) {}

    public void setUpdatedAt(String updatedAt) {}
}
