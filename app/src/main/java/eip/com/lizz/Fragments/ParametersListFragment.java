package eip.com.lizz.Fragments;


import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import eip.com.lizz.PaymentMethodsActivity;
import eip.com.lizz.R;
import eip.com.lizz.Setting.SettingsCodePIN;
import eip.com.lizz.Setting.SettingsCoordonnees;
import eip.com.lizz.Setting.SettingsPassword;
import eip.com.lizz.Setting.SettingsPayementLimit;
import eip.com.lizz.Setting.SettingsScanner;


public class ParametersListFragment extends Fragment {

    private RecyclerView mRecyclerView;
    private RecyclerView.Adapter mAdapter;
    private RecyclerView.LayoutManager mLayoutManager;

    private Context mContext;

    public static ParametersListFragment newInstance(Context context) {
        ParametersListFragment fragment = new ParametersListFragment();
        /*Bundle args = new Bundle();
        args.putString(ARG_PARAM1, param1);
        args.putString(ARG_PARAM2, param2);
        fragment.setArguments(args);*/
        //return fragment;
        return fragment;
    }

    public ParametersListFragment() {
        // Required empty public constructor
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        /*if (getArguments() != null) {
            mParam1 = getArguments().getString(ARG_PARAM1);
            mParam2 = getArguments().getString(ARG_PARAM2);
        }*/
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        View view = inflater.inflate(R.layout.fragment_parameters_list, container, false);

        String[] placeholderParameters = {"Adresses de livraison", "Moyens de paiement",
                "Alerte de dépense", "Code PIN", "Mot de passe", "Scanner"};

        mRecyclerView = (RecyclerView) view.findViewById(R.id.parameters_recycler_view);
        mRecyclerView.getItemAnimator().setSupportsChangeAnimations(true);

        mAdapter = new ParameterListAdapter(placeholderParameters);
        mRecyclerView.setAdapter(mAdapter);

        mLayoutManager = new LinearLayoutManager(mContext);
        mRecyclerView.setLayoutManager(mLayoutManager);

        return view;
    }



    private class ParameterListAdapter extends RecyclerView.Adapter<ParameterListAdapter.ViewHolder> {

        private int[] parameterResId =
                {
                        R.drawable.ic_home_black_24dp,
                        R.drawable.ic_credit_card_black_24dp,
                        R.drawable.ic_error_black_24dp,
                        R.drawable.ic_lock_black_24dp,
                        R.drawable.ic_lock_black_24dp,
                        R.drawable.ic_camera_enhance_black_24dp
                };

        private String[] mPlaceholderParameters;

                    public class ViewHolder extends RecyclerView.ViewHolder implements RecyclerView.OnClickListener {

                        public int mPosition;
                        public ImageView parameterImage;
                        public TextView parameterName;

                        public ViewHolder(View itemView) {
                            super(itemView);
                            parameterName = (TextView) itemView.findViewById(R.id.rvitem_parameter_title);
                            parameterImage = (ImageView) itemView.findViewById(R.id.rvitem_parameter_image);
                            itemView.setOnClickListener(this);
                        }

                        @Override
                        public void onClick(View v) {
                            Intent intent;
                            switch (mPosition) {
                                case 0:
                                    intent = new Intent(getActivity(), SettingsCoordonnees.class);
                                    startActivity(intent);
                                    break;
                                case 1:
                                    intent = new Intent(getActivity(), PaymentMethodsActivity.class);
                                    startActivity(intent);
                                    break;
                                case 2:
                                    intent = new Intent(getActivity(), SettingsPayementLimit.class);
                                    startActivity(intent);
                                    break;
                                case 3:
                                    intent = new Intent(getActivity(), SettingsCodePIN.class);
                                    startActivity(intent);
                                    break;
                                case 4:
                                    intent = new Intent(getActivity(), SettingsPassword.class);
                                    startActivity(intent);
                                    break;
                                case 5:
                                    intent = new Intent(getActivity(), SettingsScanner.class);
                                    startActivity(intent);
                                    break;
                            }
                        }
                    }

        public ParameterListAdapter(String[] placeholderParameters) {

            mPlaceholderParameters = placeholderParameters;
        }

        @Override
        public ParameterListAdapter.ViewHolder onCreateViewHolder(ViewGroup parent, int i) {
            View v = LayoutInflater.from(parent.getContext()).inflate(R.layout.rvitem_parameter,
                    parent, false);
            ViewHolder vh = new ViewHolder(v);
            return vh;
        }

        @Override
        public void onBindViewHolder(ViewHolder viewHolder, int i) {
            viewHolder.mPosition = i;
            viewHolder.parameterName.setText(mPlaceholderParameters[i]);
            viewHolder.parameterImage.setImageDrawable(getResources().getDrawable(parameterResId[i]));
        }

        @Override
        public int getItemCount() {
            return mPlaceholderParameters.length;
        }
    }
}

