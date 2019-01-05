package com.serviceonwheel.fragment;


import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.text.Html;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.serviceonwheel.R;
import com.serviceonwheel.activity.AddressActivity;
import com.serviceonwheel.activity.ServiceDetailActivity;

/**
 * A simple {@link Fragment} subclass.
 */
public class ServiceLevelFourFragment extends Fragment {

    Activity context;

    TextView tvNote;

    public ServiceLevelFourFragment() {
        // Required empty public constructor
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_service_level_four, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        context = getActivity();

        initComp(view);

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
            tvNote.setText(Html.fromHtml(ServiceDetailActivity.notes, Html.FROM_HTML_MODE_COMPACT));
        } else {
            tvNote.setText(Html.fromHtml(ServiceDetailActivity.notes));
        }
    }

    private void initComp(View view) {
        tvNote = view.findViewById(R.id.tvNote);
    }

}
