package eip.com.lizz.Adapter;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import eip.com.lizz.R;

public class PaymentHistoryAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {

    // Attributes
    private Context mContext;
    private String[] mDataset;


    // Constructors
    // Provide a suitable constructor (depends on the kind of dataset)
    public              PaymentHistoryAdapter(String[] myDataset, Context context) {
        mDataset = myDataset;
        mContext = context;
    }


    // Methods
    // Create new views (invoked by the layout manager)
    @Override
    public ViewHolder onCreateViewHolder(ViewGroup viewGroup, int i) {

        // create a new view
        View v = LayoutInflater
                .from(viewGroup.getContext())
                .inflate(R.layout.card_payment_history, viewGroup, false);

        // set the view's size, margins, paddings and layout parameters
        return new ViewHolder(v);
    }

    // Replace the contents of a view (invoked by the layout manager)
    @Override
    public void onBindViewHolder(RecyclerView.ViewHolder viewHolder, int i) {
        // - get element from your dataset at this position
        // - replace the contents of the view with that element
        ViewHolder vh = (PaymentHistoryAdapter.ViewHolder)viewHolder;
        vh.paymentName.setText(mDataset[i]);
        vh.paymentAmount.setText("15$");
    }

    // Return the size of your dataset (invoked by the layout manager)
    @Override
    public int  getItemCount() {
        return mDataset.length;
    }


    // Classes
    public static class ViewHolder extends RecyclerView.ViewHolder {
        // Attributes
        public TextView paymentName;
        public TextView paymentAmount;

        // Constructor
        public ViewHolder(View v) {
            super(v);
            paymentName = (TextView)v.findViewById(R.id.paymentName);
            paymentAmount = (TextView)v.findViewById(R.id.paymentAmount);
        }
    }

}
