package com.serviceonwheel.fragment;


import android.app.Activity;
import android.content.Context;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.TextView;

import com.serviceonwheel.R;
import com.serviceonwheel.adapter.QuestionAdapter;
import com.serviceonwheel.listner.GlobalListener;
import com.serviceonwheel.model.LevelOneService;

import java.util.ArrayList;
import java.util.List;

/**
 * A simple {@link Fragment} subclass.
 */
public class ServiceLevelOneFragment extends Fragment {

    GlobalListener mCallback;

    TextView tvTitle;

    RecyclerView rvQuestion;

    Activity context;

    List<LevelOneService> levelOneServices = new ArrayList<>();

    QuestionAdapter questionAdapter;

    public ServiceLevelOneFragment() {
        // Required empty public constructor
    }

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_service_level_one, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        context = getActivity();

        initComp(view);

        final LinearLayoutManager mLayoutManager = new LinearLayoutManager(context);
        rvQuestion.setLayoutManager(mLayoutManager);
        rvQuestion.setHasFixedSize(true);

    }

    public void setData(List<LevelOneService> levelOneServices) {
        this.levelOneServices = levelOneServices;
        tvTitle.setText(levelOneServices.get(0).getQ_title());
        questionAdapter = new QuestionAdapter(context, levelOneServices);
        rvQuestion.setAdapter(questionAdapter);
        questionAdapter.setOnItemClickListener(new QuestionAdapter.OnClickListener() {
            @Override
            public void onClick(int position, int witch) {
                mCallback.onRadioClick(position, 1);
            }
        });
    }

    private void initComp(View view) {
        tvTitle = view.findViewById(R.id.tvTitle);
        rvQuestion = view.findViewById(R.id.rvQuestion);
    }

    @Override
    public void onAttach(Context con) {
        super.onAttach(con);
        try {
            mCallback = (GlobalListener) con;
        } catch (ClassCastException e) {
            e.printStackTrace();
        }
    }

    @Override
    public void onDetach() {
        mCallback = null;
        super.onDetach();
    }
}