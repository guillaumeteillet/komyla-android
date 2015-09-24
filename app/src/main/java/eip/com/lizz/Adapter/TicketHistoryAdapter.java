package eip.com.lizz.Adapter;

import android.content.Context;
import android.content.Intent;
import android.graphics.drawable.Drawable;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import org.w3c.dom.Text;

import java.util.ArrayList;
import java.util.List;

import eip.com.lizz.AddEditPaymentMethodActivity;
import eip.com.lizz.Models.Cart;
import eip.com.lizz.Models.CreditCard;
import eip.com.lizz.Models.Product;
import eip.com.lizz.R;


public class TicketHistoryAdapter extends RecyclerView.Adapter<TicketHistoryAdapter.TicketHistoryViewHolder> {

    private Context mContext;
    private List<Cart> mCarts;

    public TicketHistoryAdapter(List<Cart> myDataset, Context context) {
        mCarts = myDataset;
        mContext = context;
    }

    @Override
    public TicketHistoryViewHolder onCreateViewHolder(ViewGroup parent, int i) {
        View v = LayoutInflater.from(mContext).inflate(R.layout.card_ticket_history, parent, false);
        return new TicketHistoryViewHolder(mContext, v);
    }

    @Override
    public void onBindViewHolder(TicketHistoryViewHolder paymentMethodViewHolder, int i) {
        paymentMethodViewHolder.paymentMethod = mCarts.get(i);
        /*paymentMethodViewHolder.date.setText(mCarts.get(i).getCreatedAt());
        paymentMethodViewHolder.ticketAmount.setText(mCarts.get(i).getAmount().toString() + "€ ");*/
        //paymentMethodViewHolder.shopName.setText(mCarts.get(i).getShopName());

        String str = "";
        List<Product> products = mCarts.get(i).getProducts();
        for (int y = 0; y < 2 && y < products.size(); ++y)
        {
            Product p = products.get(y);
            str += p.getQuantity() + "x " + p.getName() + "\t\t" + p.getPrice() + "€/u\n";
        }
        if (products.size() > 2)
            str += "...";
        paymentMethodViewHolder.products.setText(str);
    }

    @Override
    public int getItemCount() {
        if (mCarts == null)
            return 0;
        return mCarts.size();
    }


    // Classes
    public static class TicketHistoryViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {

        public Cart paymentMethod;
        public TextView ticketAmount;
        public TextView date;
        public TextView products;
        public TextView shopName;
        private Context context;

        public TicketHistoryViewHolder(Context context, View itemView) {
            super(itemView);
            this.context = context;

            ticketAmount = (TextView) itemView.findViewById(R.id.ticketAmount);
            date = (TextView) itemView.findViewById(R.id.date);
            products = (TextView) itemView.findViewById(R.id.products);
            shopName = (TextView) itemView.findViewById(R.id.paymentMethodName);
            shopName.setOnClickListener(this);
        }

        @Override
        public void onClick(View v) {
            Toast.makeText(context, "L'id de la carte bleue est : ", Toast.LENGTH_SHORT).show();
        }
    }
}

