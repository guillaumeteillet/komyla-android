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

import java.util.ArrayList;

import eip.com.lizz.AddEditPaymentMethodActivity;
import eip.com.lizz.Models.CreditCard;
import eip.com.lizz.R;


public class PaymentMethodsAdapter extends RecyclerView.Adapter<PaymentMethodsAdapter.PaymentMethodViewHolder> {

    private Context mContext;
    private ArrayList<CreditCard> mCreditCards;

    public PaymentMethodsAdapter(ArrayList<CreditCard> myDataset, Context context) {
        mCreditCards = myDataset;
        mContext = context;
    }

    @Override
    public PaymentMethodViewHolder onCreateViewHolder(ViewGroup parent, int i) {
        View v = LayoutInflater.from(mContext).inflate(R.layout.card_payment_methods, parent, false);
        return new PaymentMethodViewHolder(mContext, v);
    }

    @Override
    public void onBindViewHolder(PaymentMethodViewHolder paymentMethodViewHolder, int i) {
        Drawable myDrawable = mContext.getResources().getDrawable(R.drawable.facebook);

        paymentMethodViewHolder.paymentMethod = mCreditCards.get(i);
        paymentMethodViewHolder.paymentMethodName.setText(mCreditCards.get(i).get_cardNumber());
        paymentMethodViewHolder.paymentMethodImage.setImageDrawable(myDrawable);
    }

    @Override
    public int  getItemCount() {
        if (mCreditCards == null)
            return 0;
        return mCreditCards.size();
    }


    // Classes
    public static class PaymentMethodViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {

        public  CreditCard  paymentMethod;
        public  TextView    paymentMethodName;
        public  ImageView   paymentMethodImage;
        private Context     context;

        public PaymentMethodViewHolder(Context context, View itemView) {
            super(itemView);
            this.context = context;

            paymentMethodName = (TextView) itemView.findViewById(R.id.paymentMethodName);
            paymentMethodImage = (ImageView) itemView.findViewById(R.id.paymentMethodImage);
            paymentMethodName.setOnClickListener(this);
        }

        @Override
        public void onClick(View v) {
            //Toast.makeText(context, "L'id de la carte bleue est : " + this.paymentMethod.get_id(), Toast.LENGTH_SHORT).show();
            Intent intent = new Intent(context, AddEditPaymentMethodActivity.class);
            intent.putExtra("EXTRA_CREDIT_CARD", this.paymentMethod);
            context.startActivity(intent);
        }
    }
}

