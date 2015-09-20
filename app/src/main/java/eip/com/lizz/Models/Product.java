package eip.com.lizz.Models;

import java.util.Date;

/**
 * Created by fortin_j on 9/9/15.
 */
public class Product {

    private String _name = null;
    private String _productId = null;
    private String _shoppingCart = null;

    private Double _price = 0.0;
    private Integer _quantity = 0;

    private Date _createdAt = null;
    private Date _updateAt = null;

    public Product(String name, Double price, String productId, Integer quantity, String shoppingCart, String createdAt, String updatedAt) {
        setName(name);
        setPrice(price);
        setProductId(productId);
        setQuantity(quantity);
        setShoppingCart(shoppingCart);
    }

    public void setName(String name) { this._name = name; }
    public void setPrice(Double price) { this._price = Double.valueOf(price); }
    public void setProductId(String productId) { this._productId = productId; }
    public void setQuantity(Integer quantity) { this._quantity = quantity; }
    public void setShoppingCart(String shoppingCart) { this._shoppingCart = shoppingCart; }

    public void setCreatedAt(String createdAt) {}
    public void setUpdatedAt(String updatedAt) {}

    public String getName() { return this._name; }
    public Double getPrice() { return this._price; }
    public Integer getQuantity() { return this._quantity; }
}
