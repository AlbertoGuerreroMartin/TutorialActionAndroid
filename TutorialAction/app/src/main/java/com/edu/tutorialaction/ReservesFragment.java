package com.edu.tutorialaction;

import android.app.Activity;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ListView;
import android.widget.TextView;

import com.edu.tutorialaction.entity.Reserve;
import com.edu.tutorialaction.network.ReserveModel;
import com.edu.tutorialaction.network.RxLoaderFragment;
import com.welbits.izanrodrigo.emptyview.library.EmptyView;

import java.util.List;

import butterknife.ButterKnife;
import butterknife.InjectView;

/**
 * Created by albertoguerreromartin on 16/03/15.
 */
public class ReservesFragment extends RxLoaderFragment<List<Reserve>> {

    @InjectView(R.id.reservesList) ListView reservesList;
    @InjectView(R.id.emptyView) EmptyView emptyView;
    private ReservesAdapter reservesAdapter;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_main, container, false);

        ButterKnife.inject(this, view);

        return view;
    }

    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);

        this.reservesList.setAdapter(this.reservesAdapter = new ReservesAdapter(getActivity()));
        this.emptyView.retry("Reintentar", new Runnable() {
            @Override
            public void run() {
                load();
            }
        });
        load();
    }

    private void load() {
        emptyView.startLoading();
        addSubscription(ReserveModel.INSTANCE.getReserves(ReservesFragment.this));
    }

    @Override
    public void onError(Throwable e) {
        super.onError(e);
        this.emptyView.errorLoading();
    }

    @Override
    public void onNext(List<Reserve> reserves) {
        this.reservesAdapter.clearReserves();
        this.reservesAdapter.addReserves(reserves);

        if(this.reservesAdapter.isEmpty()) {
            this.emptyView.displayEmpty();
        } else {
            this.emptyView.successLoading();
        }
    }
}
