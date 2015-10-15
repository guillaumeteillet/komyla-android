package eip.com.lizz.Models;

import java.util.Date;


public class        Product {

    private String  _code = null;
    private String  _name = null;
    private String  _desc = null;
    private Double  _price = 0.0;
    private Integer _quantity = 0;

//    private String  _productId = null;
//    private String  _shoppingCart = null;
//    private Date    _createdAt = null;
//    private Date    _updateAt = null;

    public          Product(String name, Double price, String productId, Integer quantity,
                        String shoppingCart, String createdAt, String updatedAt) {
        setName(name);
        setPrice(price);
        setQuantity(quantity);
//        setProductId(productId);
//        setShoppingCart(shoppingCart);
    }

    public          Product(String code, String name, String desc, Double price, Integer quantity) {
        setCode(code);
        setName(name);
        setDesc(desc);
        setPrice(price);
        setQuantity(quantity);
    }

    public void     setCode(String code) { this._code = code; }
    public void     setName(String name) { this._name = name; }
    public void     setDesc(String desc) { this._desc = desc; }
    public void     setPrice(Double price) { this._price = Double.valueOf(price); }
    public void     setQuantity(Integer quantity) { this._quantity = quantity; }

    public void     addUnit() {
        if (this._quantity < 99)
            this._quantity += 1;
    }

    public void     removeUnit() {
        if (this._quantity > 1)
            this._quantity -= 1;
    }

    public String   getCode() { return this._code; }
    public String   getName() { return this._name; }
    public String   getDesc() { return this._desc; }
    public Double   getPrice() { return this._price; }
    public Integer  getQuantity() { return this._quantity; }

    //    public void     setProductId(String productId) { this._productId = productId; }
    //    public void     setShoppingCart(String shoppingCart) { this._shoppingCart = shoppingCart; }
    //    public void     setCreatedAt(String createdAt) {}
    //    public void     setUpdatedAt(String updatedAt) {}

}
