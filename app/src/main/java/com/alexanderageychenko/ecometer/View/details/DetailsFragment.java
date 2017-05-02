package com.alexanderageychenko.ecometer.View.details;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.alexanderageychenko.ecometer.View.ExFragment;
import com.alexanderageychenko.ecometer.Tools.DialogBuilder;
import com.alexanderageychenko.ecometer.Tools.dagger2.Dagger;
import com.alexanderageychenko.ecometer.Model.Depository.IMetersDepository;
import com.alexanderageychenko.ecometer.Model.Listener.DetailsEditListener;
import com.alexanderageychenko.ecometer.Model.Entity.IMeter;
import com.alexanderageychenko.ecometer.Model.Entity.MeterType;
import com.alexanderageychenko.ecometer.Model.Entity.MeterValue;
import com.alexanderageychenko.ecometer.R;

import javax.inject.Inject;

/**
 * Created by alexanderageychenko on 9/14/16.
 */

public class DetailsFragment extends ExFragment implements DetailsAdapter.Listener {
    private ImageView imageView;
    private IMeter meter = null;
    private RecyclerView recyclerView;
    private RecyclerView.LayoutManager layoutManager;
    private DetailsAdapter detailsAdapter;
    private View plus;

    @Inject
    IMetersDepository iMetersDepository;

    public DetailsFragment() {
        Dagger.get().getInjector().inject(this);
    }

    public static DetailsFragment newInstance() {

        DetailsFragment fragment = new DetailsFragment();

        return fragment;
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_details, container, false);
    }

    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        imageView = (ImageView) view.findViewById(R.id.image);


        MeterType type = null;
        meter = iMetersDepository.getSelectedMeter();
        type = meter.getType();

        plus =  view.findViewById(R.id.plus);
        plus.setVisibility(View.GONE);

        if (type == null) {
            imageView.setBackgroundColor(getResources().getColor(R.color.main_green_A0));
            imageView.setImageResource(R.drawable.ic_public_white_48dp);
        } else {
            imageView.setBackgroundColor(getResources().getColor(type.getBackgroundResource()));
            imageView.setImageResource(type.getImageResource());
        }

        if (meter != null) {
            ((TextView) view.findViewById(R.id.name)).setText(meter.getFullName());
            ((TextView) view.findViewById(R.id.mean_day_text)).setText(meter.getMeanValuePerDayString());
            ((TextView) view.findViewById(R.id.mean_month_text)).setText(meter.getMeanValuePerMonthString());
        }

        recyclerView = (RecyclerView) view.findViewById(R.id.details_recycler_view);
        layoutManager = new LinearLayoutManager(getActivity(), LinearLayoutManager.VERTICAL, false);
        recyclerView.setLayoutManager(layoutManager);
        detailsAdapter = new DetailsAdapter(getActivity());
        detailsAdapter.setListener(this);
        recyclerView.setAdapter(detailsAdapter);
    }

    @Override
    public void onResume() {
        super.onResume();
        detailsAdapter.setData(meter.getAllValues());
        recyclerView.scrollToPosition(0);
    }

    @Override
    public void onValueClick(MeterValue item) {
        DialogBuilder.getEditDetailsDialog(getActivity(), meter, meter.getAllValues().indexOf(item), new DetailsEditListener() {
            @Override
            public void valueWasEdited() {
                detailsAdapter.setData(meter.getAllValues());
            }
        }).show();
    }

    @Override
    public void onDateClick(MeterValue item) {
        DialogBuilder.getExitEditDateDialog(getActivity(), meter, meter.getAllValues().indexOf(item), new DetailsEditListener() {
            @Override
            public void valueWasEdited() {
                detailsAdapter.setData(meter.getAllValues());
            }
        }).show();
    }
}
