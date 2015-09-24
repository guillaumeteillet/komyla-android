package eip.com.lizz.Models;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;


public class                        Cart {

    private String                  _shopName = null;
    private ArrayList<Product>      _products = new ArrayList<>();

    public                          Cart() {

    }

    public                          Cart(Cart cart) {
        for (Product product : cart.getProducts()) {
            _products.add(product);
        }
    }

    public void                     clear() { _products.clear(); }

    // ShopName related methods
    public String                   getShopName() {
        return this._shopName;
    }
    public void                     setShopName(String shopName) {
        this._shopName = shopName;
    }

    // Products related methods
    public ArrayList<Product>       getProducts() {
        return this._products;
    }
    public Product                  getProductAt(int index) {
        return this._products.get(index);
    }
    public int                      getNumberOfProducts() {
        return _products.size();
    }
    public void                     addProduct(Product product) {
        this._products.add(product);
    }
    public boolean                  updateProduct() {
        return true;
    }
    public boolean                  removeProduct() {
        return true;
    }
    public Double                   getTotal() {

        Double total = 0.0;

        for (Product product : _products) {
            total += product.getPrice() * product.getQuantity();
        }
        return total;

    }


    /*private String          _cartId = null;
    private String          _createdAt = null; // temporary
    private Date            _updateAt = null;
    private String          _userId = null;
    private Transaction     _transaction = null;*/


    /*public                  Cart(List<Product> products, Transaction transaction,
                                 String shopName, String createdAt) {

        setProducts(products);
        setTransaction(transaction);
        setShopName(shopName);

        setCreatedAt(createdAt);
    }*/


//    public String           getCreatedAt() {
//        return this._createdAt;
//    }
//    public void             setProducts(List<Product> products) {
//        this._products = products;
//    }
//    public void             setTransaction(Transaction transaction) {
//        this._transaction = transaction;
//    }
//    public void             setId(String id) {
//        this._cartId = id;
//    }

//    public void             setCreatedAt(String createdAt) {
//        this._createdAt = " " + createdAt.substring(0, 10);
//    }
}
