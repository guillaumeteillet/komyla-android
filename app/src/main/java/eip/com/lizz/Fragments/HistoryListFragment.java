package eip.com.lizz.Fragments;

import android.content.Context;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import org.apache.http.HttpResponse;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.params.ClientPNames;
import org.apache.http.client.params.CookiePolicy;
import org.apache.http.cookie.Cookie;
import org.apache.http.impl.client.BasicCookieStore;
import org.apache.http.impl.client.DefaultHttpClient;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.List;

import eip.com.lizz.Adapter.TicketHistoryAdapter;
import eip.com.lizz.Models.Cart;
import eip.com.lizz.Models.Cookies;
import eip.com.lizz.Models.CreditCard;
import eip.com.lizz.Models.Product;
import eip.com.lizz.Models.Transaction;
import eip.com.lizz.R;
import eip.com.lizz.Utils.UApi;
import eip.com.lizz.Utils.UJsonToData;


public class HistoryListFragment extends Fragment {

    private Context mContext = null;
    private RecyclerView mRecyclerView;
    private TicketHistoryAdapter mAdapter;
    private LinearLayoutManager mLayoutManager;
    private SwipeRefreshLayout mSwipeRefreshLayout;

    private List<Cart> mCarts = null;

    public HistoryListFragment() {
    }

    public HistoryListFragment(Context context) {
        mContext = context;
    }

    public static HistoryListFragment newInstance(Context context) {
        return new HistoryListFragment(context);
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Log.e("onCreate", "___");
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        super.onCreateView(inflater, container, savedInstanceState);
        View view = inflater.inflate(R.layout.fragment_history_list, container, false);
        Log.e("onCreateView", "-----");
        mSwipeRefreshLayout = (SwipeRefreshLayout) view.findViewById(R.id.swipePaymentMethods);
        mRecyclerView = (RecyclerView) view.findViewById(R.id.my_recycler_view);
        createRecyclerView();
        configureSwipeRefreshLayout(mContext);
        if (mCarts != null && mCarts.size() > 0)
            refreshDisplay();
        return view;
    }

    @Override
    public void onResume() {
        super.onResume();
        Log.e("onResume", "");
        mSwipeRefreshLayout.setEnabled(true);
        new GetTicketsFromAPI(mContext).execute();
    }

    private void configureSwipeRefreshLayout(final Context context) {
        mSwipeRefreshLayout.setColorSchemeColors(getResources().getColor(R.color.primary));
        mSwipeRefreshLayout.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                mSwipeRefreshLayout.setRefreshing(true);
                new GetTicketsFromAPI(mContext).execute();
            }
        });
        mRecyclerView.setOnScrollListener(new RecyclerView.OnScrollListener() {
            @Override
            public void onScrollStateChanged(RecyclerView recyclerView, int newState) {
                super.onScrollStateChanged(recyclerView, newState);
                if (recyclerView.getChildCount() != 0
                        && mLayoutManager.findFirstVisibleItemPosition() == 0
                        && mRecyclerView.getChildAt(0).getTop() >= 0) {
                    mSwipeRefreshLayout.setEnabled(true);
                }
                else {
                    mSwipeRefreshLayout.setEnabled(false);
                }
            }

            @Override
            public void onScrolled(RecyclerView recyclerView, int dx, int dy) {
                super.onScrolled(recyclerView, dx, dy);
            }
        });
    }

    private void createRecyclerView() {
        mLayoutManager = new LinearLayoutManager(mContext);
        mRecyclerView.setLayoutManager(mLayoutManager);

        mAdapter = new TicketHistoryAdapter(mCarts, mContext);
        mRecyclerView.setAdapter(mAdapter);
    }

    private void refreshDisplay() {
        Log.e("REFRESH", "UI");
        mAdapter.notifyDataSetChanged();
        mSwipeRefreshLayout.setRefreshing(false);
    }

    private class GetTicketsFromAPI extends AsyncTask<Void, Void, Void> {

        final private String _url = "http://api.komyla.com:8000/api/v1/user/gettickets";
        private final Context _context;
        private List<Cookie> _cookies;


        public GetTicketsFromAPI(Context context) {
            this._context = context;
        }

        @Override
        protected Void doInBackground(Void... params) {
            try {
                getTickets();
            } catch (IOException e) {
            }
            return null;
        }

        @Override
        protected void onPostExecute(Void aVoid) {
            super.onPostExecute(aVoid);
            refreshDisplay();
        }

        private void getTickets() throws IOException {

            InputStream l_inputStream = null;

            DefaultHttpClient httpClient = new DefaultHttpClient();
            httpClient.getParams().setParameter(ClientPNames.COOKIE_POLICY, CookiePolicy.BROWSER_COMPATIBILITY);
            httpClient.setCookieStore(new BasicCookieStore());

            _cookies = Cookies.loadSharedPreferencesCookie(_context);

            httpClient.getCookieStore().addCookie(_cookies.get(0));
            HttpGet httpGet = new HttpGet(_url);

            HttpResponse httpResponse = httpClient.execute(httpGet);
            l_inputStream = httpResponse.getEntity().getContent();

            int responseCode = httpResponse.getStatusLine().getStatusCode();

            try {

                BufferedReader streamReader = new BufferedReader(new InputStreamReader(l_inputStream, "UTF-8"));
                StringBuilder responseStrBuilder = new StringBuilder();

                String inputStr;
                while ((inputStr = streamReader.readLine()) != null)
                    responseStrBuilder.append(inputStr);

                JSONArray obj = new JSONArray(responseStrBuilder.toString());
                ArrayList<Cart> carts = new ArrayList<Cart>();

                for (int i = 0; i < obj.length(); ++i) {
                    JSONObject cart = obj.getJSONObject(i);
                    Log.e("cart#" + i, cart.toString());

                    JSONObject transaction = cart.getJSONObject("transaction");
                    Log.e("transaction", transaction.toString());
                    Transaction t = new Transaction(transaction.getBoolean("paid"),
                            transaction.getString("name"),
                            transaction.getString("ref"),
                            transaction.getString("createdAt"),
                            transaction.getString("updatedAt"),
                            transaction.getString("shoppingCart"),
                            transaction.getDouble("amount"));


                    ArrayList<Product> productsList = new ArrayList<Product>();
                    JSONArray products = cart.getJSONArray("productsCart");
                    // Log.e("array=", products.toString());
                    for (int y = 0; y < products.length(); ++y) {
                        JSONObject p = products.getJSONObject(y);
                        //Log.e("product#" + y, p.toString());
                        productsList.add(new Product(p.getString("name"),
                                p.getDouble("price"),
                                p.getString("productId"),
                                p.getInt("quantity"),
                                p.getString("shoppingCart"),
                                p.getString("createdAt"),
                                p.getString("updatedAt")));
                    }
                    carts.add(new Cart(productsList, t, cart.getString("shopName"), cart.getString("createdAt")));
//                    carts.add(new Cart());
                }
                mCarts = carts;
                Log.e("GetTicketsFromAPI", "Le code de retour est: " + responseCode + " et le contenu est: " + UApi.convertStreamToString(l_inputStream));
            } catch (Exception e) {
                mCarts.clear();
                e.printStackTrace();
            } finally {
                if (l_inputStream != null)
                    l_inputStream.close();
            }
        }
    }
}
