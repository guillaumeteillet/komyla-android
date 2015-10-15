package eip.com.lizz.Fragments;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.media.Image;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Base64;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.UnsupportedEncodingException;
import java.util.ArrayList;

import eip.com.lizz.CameraPreview;
import eip.com.lizz.Models.Cart;
import eip.com.lizz.Models.Product;
import eip.com.lizz.PayementActivity;
import eip.com.lizz.PayementWithUniqueCodeActivity;
import eip.com.lizz.R;
import eip.com.lizz.ScanQRCodeActivity;


public class                            CartFragment extends Fragment {

    private static final int            PICK_PRODUCT_REQUEST = 1;

    private static final int            VIEW_PRODUCT = 0;
    private static final int            VIEW_BUTTON = 1;

    private ImageView                   mEmptyCartImageView;
    private TextView                    mEmptyCartTextView;
    private ImageButton                 mCameraButton;
    private RecyclerView                mRecyclerView;
    private RecyclerView.Adapter        mAdapter;
    private RecyclerView.LayoutManager  mLayoutManager;

    private boolean                     scannerStatus;
    private Context                     mContext;

    private Cart                        mCart;

    public                              CartFragment() {
    }

    public static CartFragment          newInstance(Context context) {
        return new CartFragment();
    }

    @Override
        public void                     onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mCart = new Cart();
    }

    @Override
    public View                         onCreateView(LayoutInflater inflater, ViewGroup container,
                                                     Bundle savedInstanceState) {

        View view = inflater.inflate(R.layout.fragment_cart, container, false);

        mRecyclerView = (RecyclerView) view.findViewById(R.id.cart_recycler_view);
        mRecyclerView.getItemAnimator().setSupportsChangeAnimations(true);

        mAdapter = new ProductListAdapter();
        mRecyclerView.setAdapter(mAdapter);

        mLayoutManager = new LinearLayoutManager(mContext);
        mRecyclerView.setLayoutManager(mLayoutManager);

        mCameraButton = (ImageButton) view.findViewById(R.id.cart_camera_button);
        mEmptyCartImageView = (ImageView) view.findViewById(R.id.emptyCartImageView);
        mEmptyCartTextView = (TextView) view.findViewById(R.id.emptyCartTextView);

        mCameraButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                SharedPreferences sharedpreferences =
                        getActivity()
                                .getSharedPreferences("eip.com.lizz", Context.MODE_PRIVATE);
                scannerStatus = sharedpreferences.getBoolean("eip.com.lizz.scannerstatus", true);

                boolean apn = CameraPreview.checkCameraHardware(getActivity());
                if (scannerStatus && apn) {
                    Intent intent = new Intent(getActivity(), ScanQRCodeActivity.class);
                    startActivityForResult(intent, PICK_PRODUCT_REQUEST);
                } else {
                    Intent intent = new Intent(getActivity(), PayementWithUniqueCodeActivity.class);
                    startActivity(intent);
                }
            }
        });

        return view;
    }

    @Override
    public void                         onActivityResult(int requestCode, int resultCode,
                                                         Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        switch (requestCode) {
            case (PICK_PRODUCT_REQUEST) : {
                if (resultCode == Activity.RESULT_OK) {
                    //Toast.makeText(getActivity(), "Le code est : " + data.getStringExtra("ProductURL"), Toast.LENGTH_LONG).show();
                    analyzeQRCode(data.getStringExtra("ProductURL"));
                }
                break;
            }
            default:
                Toast.makeText(getActivity(), "Le QRCode ne semble pas être un QRCode Komyla valide"
                        , Toast.LENGTH_LONG).show();
        }
    }

    private void                        analyzeQRCode(String productURL)
    {
        String[] part_url = productURL.split("/");
        String[] infos = part_url[1].split("\\?");
        String code = infos[0];
        String data = infos[1];

        if(part_url[0].equals("d"))
        {
            Log.d("CartFragment", "Achat direct");
            addProductToCart(code, decipherProductData(data));
            doPaiement(true);
        }
        else if(part_url[0].equals("p"))
        {
            Log.d("CartFragment", "Achat panier");
            addProductToCart(code, decipherProductData(data));
        }
    }

    private String[]                    decipherProductData(String dataCiphered) {
        try {
            byte[] data_bytes = Base64.decode(dataCiphered, Base64.DEFAULT);
            String dataDeciphered = new String(data_bytes, "UTF-8");
            Log.d("CartFragment", "La data déchifrée est :" + dataDeciphered);
            return dataDeciphered.split("&");

        } catch (UnsupportedEncodingException e) {
            e.printStackTrace();
        }
        return null;
    }

    private void                        addProductToCart(String code, String[] productParams) {
        String                          productName = null,
                                        productDesc = null,
                                        productPrice = null,
                                        productUnite = null,
                                        productQuantity = null,
                                        productVersion = null;

        final Integer                   key = 0, value = 1;

        for (String param : productParams) {
            String[] keyValue = param.split("=");
            switch (keyValue[key]) {
                case "name":
                    productName = keyValue[value];
                    break;
                case "desc":
                    productDesc = keyValue[value];
                    break;
                case "price":
                    productPrice = keyValue[value];
                    break;
                case "unite":
                    productUnite = keyValue[value];
                    break;
                case "quantity":
                    productQuantity = keyValue[value];
                    break;
                case "version":
                    productVersion = keyValue[value];
                    break;
            }
        }
        if (productName != null && productPrice != null && productQuantity != null) {
            Product product = new Product(code, productName, productDesc, Double.valueOf(productPrice),
                    1);
            this.mCart.addProduct(product);
            updateDisplay();
            // Mettre à jour l'affichage
//            Log.d("CartFragment", "Produit - > name = " + product.getName() + " price = " + product.getPrice() + " quantity = " + product.getQuantity());
        }
    }

    private void                    updateDisplay() {
        if (this.mAdapter.getItemCount() <= 0) {
            this.mRecyclerView.setVisibility(View.INVISIBLE);
            this.mEmptyCartImageView.setVisibility(View.VISIBLE);
            this.mEmptyCartTextView.setVisibility(View.VISIBLE);
        }
        else {
            this.mRecyclerView.setVisibility(View.VISIBLE);
            this.mEmptyCartImageView.setVisibility(View.INVISIBLE);
            this.mEmptyCartTextView.setVisibility(View.INVISIBLE);
        }
        this.mAdapter.notifyDataSetChanged();
    }

    private void                    doPaiement(Boolean clear) {

        Cart cart = new Cart(mCart);

        if (clear) {
            mCart.clear();
            updateDisplay();
        }

        Intent payment = new Intent(getActivity(), PayementActivity.class);
        payment.putExtra("productArray", convertCartToJson(cart).toString());
        payment.putExtra("total", cart.getTotal().toString());
        startActivity(payment);
    }

    private JSONArray              convertCartToJson(Cart cart) {

        JSONArray productArray = new JSONArray();
        JSONObject productObj = new JSONObject();

        for (int i = 0; i < cart.getNumberOfProducts(); i++) {
            Product product = cart.getProductAt(i);
            try {
                productObj.remove("productId");
                productObj.remove("nbr");
                productObj.put("productId", product.getCode());
                productObj.put("nbr", product.getQuantity());
                productArray.put(productObj);
            } catch (JSONException e) {
                e.printStackTrace();
            }
        }
        return productArray;
    }



    private class ProductListAdapter extends RecyclerView.Adapter<ProductListAdapter.ViewHolder> {

        public ProductListAdapter() {

        }

        @Override
        public ProductListAdapter.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {

            View v;

            ProductViewHolder prodVh = null;
            PayButtonViewHolder payVh= null;

            switch (viewType) {
                case VIEW_PRODUCT:
                    v = LayoutInflater.from(parent.getContext()).inflate(R.layout.rvitem_cart, parent, false);
                    return prodVh = new ProductViewHolder(v);
                case VIEW_BUTTON:
                    v = LayoutInflater.from(parent.getContext()).inflate(R.layout.rvitem_cart_buttons, parent, false);
                    return payVh = new PayButtonViewHolder(v);
            }
            return null;
        }

        public abstract class ViewHolder extends RecyclerView.ViewHolder {

//            public TextView productQuantity;
//            public TextView productName;
//            public TextView productDesc;
//            public TextView productPrice;
//            public TextView productTotalPrice;

            public ViewHolder(View itemView) {
                super(itemView);
//                productQuantity = (TextView) itemView.findViewById(R.id.productQuantity);
//                productName = (TextView) itemView.findViewById(R.id.productName);
//                productDesc = (TextView) itemView.findViewById(R.id.productDesc);
//                productPrice = (TextView) itemView.findViewById(R.id.productPrice);
//                productTotalPrice = (TextView) itemView.findViewById(R.id.productTotalPrice);
                //itemView.setOnClickListener(this);
            }
//            @Override
//            public void onClick(View v) {
//            }

        }

        public class ProductViewHolder extends ProductListAdapter.ViewHolder implements RecyclerView.OnClickListener{

            public TextView productQuantity;
            public TextView productName;
            public TextView productDesc;
            public TextView productPrice;
            public TextView productTotalPrice;

            public ImageButton reduceQuantity;
            public ImageButton increaseQuantity;

            public ProductViewHolder(View itemView) {
                super(itemView);

                productQuantity = (TextView) itemView.findViewById(R.id.productQuantity);
                productName = (TextView) itemView.findViewById(R.id.productName);
                productDesc = (TextView) itemView.findViewById(R.id.productDesc);
                productPrice = (TextView) itemView.findViewById(R.id.productPrice);
                productTotalPrice = (TextView) itemView.findViewById(R.id.productTotalPrice);

                reduceQuantity = (ImageButton) itemView.findViewById(R.id.reduceQuantity);
                increaseQuantity = (ImageButton) itemView.findViewById(R.id.increaseQuantity);

                itemView.setOnClickListener(this);
            }
            @Override
            public void onClick(View v) {
                Log.d("CardFragment", "Un produit");
            }

        }

        public class PayButtonViewHolder extends ProductListAdapter.ViewHolder {

            public Button payButton;

            public PayButtonViewHolder(View itemView) {
                super(itemView);
                payButton = (Button) itemView.findViewById(R.id.payButton);
                payButton.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        doPaiement(false);
                    }
                });
            }
        }

        @Override
        public int getItemViewType(int position) {
            if (position == mCart.getNumberOfProducts())
                return VIEW_BUTTON;
            else
                return VIEW_PRODUCT;
        }

        @Override
        public void onBindViewHolder(ViewHolder viewHolder, final int i) {
            if (viewHolder instanceof ProductViewHolder) {
                ((ProductViewHolder)viewHolder).productQuantity.setText(String.valueOf(mCart.getProductAt(i).getQuantity()));
                ((ProductViewHolder)viewHolder).productName.setText(mCart.getProductAt(i).getName());
                ((ProductViewHolder)viewHolder).productDesc.setText(mCart.getProductAt(i).getDesc());
                ((ProductViewHolder)viewHolder).productPrice.setText(String.valueOf(mCart.getProductAt(i).getPrice())
                        .concat("€"));
                ((ProductViewHolder)viewHolder).productTotalPrice.setText(
                        String.valueOf(mCart.getProductAt(i).getPrice() * mCart.getProductAt(i).getQuantity())
                                .concat("€"));

                ((ProductViewHolder)viewHolder).reduceQuantity.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        mCart.getProductAt(i).removeUnit();
                        updateDisplay();
                    }
                });

                ((ProductViewHolder)viewHolder).increaseQuantity.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        mCart.getProductAt(i).addUnit();
                        updateDisplay();
                    }
                });

            }
            if (viewHolder instanceof PayButtonViewHolder) {
                ((PayButtonViewHolder)viewHolder).payButton.setText("Payer " + String.valueOf(mCart.getTotal()).concat("€"));
            }
        }

        @Override
        public int getItemCount() {
            int numberOfProducts = mCart.getNumberOfProducts();
            if (numberOfProducts <= 0)
                return 0;
            else
                return mCart.getNumberOfProducts() + 1;
        }
    }
}
