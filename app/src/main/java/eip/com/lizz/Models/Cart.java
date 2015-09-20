package eip.com.lizz.Models;

import java.util.Date;
import java.util.List;

/**
 * Created by fortin_j on 9/9/15.
 */
public class Cart {

    private String      _cartId = null;
    private String      _shopName = null;

    private List<Product>   _products = null;
    private Transaction     _transaction = null;

    private String    _createdAt = null; // temporary
    private Date    _updateAt = null;

    private String  _userId = null;

    public Cart(List<Product> products, Transaction transaction, String shopName, String createdAt) {
        setProducts(products);
        setTransaction(transaction);
        setShopName(shopName);

        setCreatedAt(createdAt);
    }

    public String getShopName() { return this._shopName; }
    public Double getAmount() { return this._transaction.getAmount(); }
    public String getCreatedAt() { return this._createdAt; }
    public List<Product> getProducts() { return this._products; }

    public void setProducts(List<Product> products) { this._products = products; }
    public void setTransaction(Transaction transaction) { this._transaction = transaction; }

    public void setId(String id) { this._cartId = id; }
    public void setShopName(String shopName) { this._shopName = shopName; }

    public void setCreatedAt(String createdAt) { this._createdAt = " " + createdAt.substring(0, 10); }
}
