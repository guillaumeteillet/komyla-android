package eip.com.lizz;

import android.content.Context;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.app.ActionBarActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;

import org.apache.http.HttpResponse;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.params.ClientPNames;
import org.apache.http.client.params.CookiePolicy;
import org.apache.http.cookie.Cookie;
import org.apache.http.impl.client.BasicCookieStore;
import org.apache.http.impl.client.DefaultHttpClient;
import org.json.JSONException;

import java.io.InputStream;
import java.util.ArrayList;
import java.util.List;

import eip.com.lizz.Models.Cookies;
import eip.com.lizz.Models.CreditCard;
import eip.com.lizz.Adapter.PaymentMethodsAdapter;
import eip.com.lizz.Utils.UApi;
import eip.com.lizz.Utils.UJsonToData;

public class PaymentMethodsActivity extends ActionBarActivity {
// Retrocompatibilité Remettre ActionBarActivity et enlever android: dans le style

    /* Attributes */
    private RecyclerView                mRecyclerView;
    private PaymentMethodsAdapter       mAdapter;
    private LinearLayoutManager         mLayoutManager;
    private SwipeRefreshLayout          mSwipeRefreshLayout;

    private ArrayList<CreditCard>       mCreditCards = null;
    /* Methods */
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_payment_methods);

        /* Creation of the design */
        mCreditCards = new ArrayList<CreditCard>();
        Bindings();
        createRecyclerView();
        configureSwipeRefreshLayout(this);
    }

    @Override
    protected void onResume() {
        super.onResume();
        mSwipeRefreshLayout.setEnabled(true);
        new GetPaymentMethodsFromAPI(getApplication()).execute();
    }

    private void Bindings() {
        mSwipeRefreshLayout = (SwipeRefreshLayout)findViewById(R.id.swipePaymentMethods);
        mRecyclerView = (RecyclerView)findViewById(R.id.my_recycler_view);
    }

    private void createRecyclerView() {
        mLayoutManager = new LinearLayoutManager(this);
        mRecyclerView.setLayoutManager(mLayoutManager);

        mAdapter = new PaymentMethodsAdapter(mCreditCards, this);
        mRecyclerView.setAdapter(mAdapter);

        //new GetPaymentMethodsFromAPI(this).execute();
    }

    private void configureSwipeRefreshLayout(final Context context) {
        mSwipeRefreshLayout.setColorSchemeColors(getResources().getColor(R.color.primary));
        mSwipeRefreshLayout.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                mSwipeRefreshLayout.setRefreshing(true);
                new GetPaymentMethodsFromAPI(context).execute();
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

    private void refreshDisplay(String resultSet) {
        try {
            mCreditCards.clear();
            ArrayList<CreditCard> tmp = UJsonToData.getCreditCardListFromJSON(resultSet);

            if (tmp != null) {
                for (int i = 0; i < tmp.size(); i++){
                    mCreditCards.add(tmp.get(i));
                }
                mAdapter.notifyDataSetChanged();
                mSwipeRefreshLayout.setRefreshing(false);
            }

        } catch (JSONException e) {
            Log.d("NoCreditCards", "Il n'y a pas de carte de crédit dans le retour d'API");
            e.printStackTrace();
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_payment_methods, menu);
        getMenuInflater().inflate(R.menu.main_menu, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();

       if (id == R.id.action_add_item) {
            Intent intent = new Intent(this, AddEditPaymentMethodActivity.class);
//            intent.putExtra("EXTRA_TYPE", "add");
            startActivity(intent);
            return true;
        }

        return MenuLizz.main_menu(item, getBaseContext(), PaymentMethodsActivity.this);
    }

    /* ASYNCTASK POUR RÉCUPÉRER LES MOYENS DE PAIEMENTS STOCKÉS DANS L'API */
    public class                GetPaymentMethodsFromAPI extends AsyncTask<Void, Void, String> {

        // Attributes
        private final Context   _context;
        private List<Cookie>    _cookies;

        public GetPaymentMethodsFromAPI(Context context) {
            this._context = context;
        }

        @Override
        protected String        doInBackground(Void... params) {
            try {
                return downloadDataSet();
            } catch (Exception e) {
                e.printStackTrace();
            }
            return null;
        }

        private String          downloadDataSet() throws Exception {
            InputStream l_inputStream = null;
            String resultSet = null;

            try // TODO Gérer les codes d'erreurs autre que dans les logs
            {

                String url = _context.getResources().getString(R.string.url_api_komyla_no_suffix)
                        + _context.getResources().getString(R.string.url_api_suffix)
                        + _context.getResources().getString(R.string.url_api_get_paymentMethods);
                DefaultHttpClient httpClient = new DefaultHttpClient();
                httpClient.getParams().setParameter(ClientPNames.COOKIE_POLICY, CookiePolicy.BROWSER_COMPATIBILITY);
                httpClient.setCookieStore(new BasicCookieStore());

                _cookies = Cookies.loadSharedPreferencesCookie(_context);

                httpClient.getCookieStore().addCookie(_cookies.get(0));
                HttpGet httpGet = new HttpGet(url);

                HttpResponse httpResponse = httpClient.execute(httpGet);
                l_inputStream = httpResponse.getEntity().getContent();

                int responseCode = httpResponse.getStatusLine().getStatusCode();
                resultSet = UApi.convertStreamToString(l_inputStream);

            }
            finally
            {
                if (l_inputStream != null)
                    l_inputStream.close();
            }
            return (resultSet);
        }

        @Override
        protected void          onPostExecute(String resultSet) {
            super.onPostExecute(resultSet);
            refreshDisplay(resultSet);
        }
    }
}

