package com.innobins.innotrack.fragments;

import android.net.Uri;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v7.widget.CardView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.ViewParent;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.innobins.innotrack.R;

/**
 * Created by silence12 on 4/7/17.
 */

public class    AddFragment extends Fragment implements View.OnClickListener {

    public static LinearLayout linlaHeaderProgress;
    static int page_pos = 1;
    public View rootView;
    ImageView imageView;
    private OnFragmentInteractionListener mListener;
    private TextView no_loads_available;
    private CardView addTruckCardView;
    private Button addtruckBuution;


    public AddFragment() {
    }

    public static AddFragment newInstance(int position) {
        AddFragment fragment = new AddFragment();
        Bundle args = new Bundle();
        args.putInt("pos", position);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        if (rootView == null) {
            rootView = inflater.inflate(R.layout.fragment_add, container, false);
            addTruckCardView = (CardView) rootView.findViewById(R.id.addload_cardView);
            imageView = (ImageView) rootView.findViewById(R.id.addload_iv);
            addTruckCardView.setOnClickListener(this);
            Bundle b = getArguments();
            page_pos = b.getInt("pos");
            if (page_pos == 1) {
                imageView.setImageResource(R.drawable.banner3);
            }
//            } else if (page_pos == 2) {
//                imageView.setImageResource(R.drawable.banner3);
//            }

        } else {

            final ViewParent parent = rootView.getParent();
            if (parent != null && parent instanceof ViewGroup) {
                ((ViewGroup) parent).removeView(rootView);
            }
        }
        return rootView;
    }


    public void onButtonPressed(Uri uri) {
        if (mListener != null) {
            mListener.onFragmentInteraction(uri);
        }
    }

    @Override
    public void onClick(View v) {

    }

    public interface OnFragmentInteractionListener {
        public void onFragmentInteraction(Uri uri);
    }
}

