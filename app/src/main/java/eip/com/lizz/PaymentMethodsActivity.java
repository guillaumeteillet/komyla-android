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
import android.widget.Toast;

import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.Volley;

import org.apache.http.HttpResponse;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.params.ClientPNames;
import org.apache.http.client.params.CookiePolicy;
import org.apache.http.cookie.Cookie;
import org.apache.http.impl.client.BasicCookieStore;
import org.apache.http.impl.client.DefaultHttpClient;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.InputStream;
import java.util.ArrayList;
import java.util.List;

import eip.com.lizz.Models.Cookies;
import eip.com.lizz.Models.CreditCard;
import eip.com.lizz.Adapter.PaymentMethodsAdapter;
import eip.com.lizz.Network.GET_CreditCard;
import eip.com.lizz.Utils.UApi;
import eip.com.lizz.Utils.UJsonToData;

public class PaymentMethodsActivity extends ActionBarActivity {
// Retrocompatibilité Remettre ActionBarActivity et enlever android: dans le style

    /* Attributes */
    private RecyclerView mRecyclerView;
    private PaymentMethodsAdapter mAdapter;
    private LinearLayoutManager mLayoutManager;
    private SwipeRefreshLayout mSwipeRefreshLayout;

    private ArrayList<CreditCard> mCreditCards = null;

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

    public void getPaymentMethods() {
        new GET_CreditCard(getBaseContext(), new Response.Listener<JSONObject>() {
            @Override
            public void onResponse(JSONObject response) {
                Log.e("Response", response.toString());
                refreshDisplay(response.toString());
            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                if (error != null && error.networkResponse != null) {
                    String json = new String(error.networkResponse.data);
                    try {
                        JSONObject data = new JSONObject(json);
                        data = data.getJSONObject("result");

                        Toast.makeText(getBaseContext(), data.getString("shortMessage") + ": " + data.getString("longMessage"), Toast.LENGTH_LONG).show();

                    } catch (JSONException e) {
                        e.printStackTrace();
                        notifyUnknowError();
                    }
                } else
                    notifyUnknowError();
            }
        }).run();
    }

    private void notifyUnknowError() {
        Toast.makeText(getBaseContext(), getBaseContext().getResources().getString(R.string.error_fetch_credit_card), Toast.LENGTH_LONG).show();
    }

    @Override
    protected void onResume() {
        super.onResume();
        mSwipeRefreshLayout.setEnabled(true);
        getPaymentMethods();
    }

    private void Bindings() {
        mSwipeRefreshLayout = (SwipeRefreshLayout) findViewById(R.id.swipePaymentMethods);
        mRecyclerView = (RecyclerView) findViewById(R.id.my_recycler_view);
    }

    private void createRecyclerView() {
        mLayoutManager = new LinearLayoutManager(this);
        mRecyclerView.setLayoutManager(mLayoutManager);

        mAdapter = new PaymentMethodsAdapter(mCreditCards, this);
        mRecyclerView.setAdapter(mAdapter);
    }

    private void configureSwipeRefreshLayout(final Context context) {
        mSwipeRefreshLayout.setColorSchemeColors(getResources().getColor(R.color.primary));
        mSwipeRefreshLayout.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                mSwipeRefreshLayout.setRefreshing(true);
                getPaymentMethods();
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
                } else {
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
                for (int i = 0; i < tmp.size(); i++) {
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
}

