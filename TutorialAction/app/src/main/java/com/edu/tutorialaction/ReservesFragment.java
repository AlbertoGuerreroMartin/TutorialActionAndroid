package com.edu.tutorialaction;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ListView;

import com.edu.tutorialaction.entity.Reserve;
import com.edu.tutorialaction.network.ReserveModel;
import com.edu.tutorialaction.network.RxLoaderFragment;
import com.melnykov.fab.FloatingActionButton;
import com.welbits.izanrodrigo.emptyview.library.EmptyView;

import java.util.List;

import butterknife.ButterKnife;
import butterknife.InjectView;

/**
 * Created by albertoguerreromartin on 16/03/15.
 */
public class ReservesFragment extends RxLoaderFragment<Object> {

    @InjectView(R.id.reservesList) ListView reservesList;
    @InjectView(R.id.emptyView) EmptyView emptyView;
    @InjectView(R.id.fab) FloatingActionButton floatingActionButton;
    private ReservesAdapter reservesAdapter;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_reserves, container, false);

        ButterKnife.inject(this, view);

        return view;
    }

    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);

        this.reservesList.setAdapter(this.reservesAdapter = new ReservesAdapter(getActivity()));
        this.floatingActionButton.attachToListView(this.reservesList);

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
        addSubscription(ReserveModel.INSTANCE.getReserves(ReservesFragment.this, getActivity().getApplicationContext()));
    }

    @Override
    public void onError(Throwable e) {
        super.onError(e);
        this.emptyView.errorLoading();
    }

    @Override
    public void onNext(Object reserves) {
        this.reservesAdapter.clearReserves();
        this.reservesAdapter.addReserves((List<Reserve>) reserves);

        if(this.reservesAdapter.isEmpty()) {
            this.emptyView.displayEmpty();
        } else {
            this.emptyView.successLoading();
        }
    }
}
