package eip.com.lizz.Fragments;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;

import eip.com.lizz.CameraPreview;
import eip.com.lizz.PayementWithUniqueCodeActivity;
import eip.com.lizz.R;
import eip.com.lizz.ScanQRCodeActivity;


public class CartFragment extends Fragment {

    private ImageButton mCameraButton;
    private boolean scannerStatus;

    public CartFragment() {
    }

    public static CartFragment newInstance(Context context) {
        CartFragment fragment = new CartFragment();
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_cart, container, false);

        mCameraButton = (ImageButton) view.findViewById(R.id.cart_camera_button);

        mCameraButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                SharedPreferences sharedpreferences = getActivity().getSharedPreferences("eip.com.lizz", Context.MODE_PRIVATE);
                scannerStatus = sharedpreferences.getBoolean("eip.com.lizz.scannerstatus", true);
                boolean apn = CameraPreview.checkCameraHardware(getActivity());
                if (scannerStatus && apn)
                {
                    Intent intent = new Intent(getActivity(), ScanQRCodeActivity.class);
                    startActivity(intent);
                }
                else
                {
                    Intent intent = new Intent(getActivity(), PayementWithUniqueCodeActivity.class);
                    startActivity(intent);
                }
            }
        });

        return view;
    }
}
