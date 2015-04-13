package com.edu.tutorialaction;

import android.content.Intent;
import android.os.Bundle;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.app.ActionBarActivity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ListView;
import android.widget.TextView;

import com.edu.tutorialaction.entity.Reserve;
import com.edu.tutorialaction.network.ReserveModel;
import com.edu.tutorialaction.network.RxLoaderFragment;
import com.melnykov.fab.FloatingActionButton;
import com.welbits.izanrodrigo.emptyview.library.EmptyView;

import java.util.Date;
import java.util.List;

import butterknife.ButterKnife;
import butterknife.InjectView;

/**
 * Created by albertoguerreromartin on 16/03/15.
 */
public class ReservesFragment extends RxLoaderFragment<List<Reserve>> implements SwipeRefreshLayout.OnRefreshListener {

    @InjectView(R.id.swipe_container) SwipeRefreshLayout swipeRefreshLayout;
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

        //--- Set refresh ---
        this.swipeRefreshLayout.setOnRefreshListener(this);
        swipeRefreshLayout.setColorSchemeColors(
                getResources().getColor(R.color.colorPrimary),
                getResources().getColor(R.color.colorAccent));
        //-------------------

        //--- Set list adapter ---
        this.reservesList.setAdapter(this.reservesAdapter = new ReservesAdapter(getActivity()));
        //------------------------


        this.floatingActionButton.attachToListView(this.reservesList);
        this.floatingActionButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(getActivity(), NewReserveActivity.class);
                startActivity(intent);
            }
        });

        this.emptyView.retry("Reintentar", new Runnable() {
            @Override
            public void run() {
                load();
            }
        });
        load();
    }

    private void load() {
        addSubscription(ReserveModel.INSTANCE.getReserves(ReservesFragment.this, getActivity().getApplicationContext()));
    }

    @Override
    public void onError(Throwable e) {
        super.onError(e);
        this.emptyView.errorLoading();
        this.swipeRefreshLayout.setRefreshing(false);
    }

    @Override
    public void onNext(List<Reserve> reserves) {
        this.swipeRefreshLayout.setRefreshing(false);
        this.reservesAdapter.clearReserves();
        this.reservesAdapter.addReserves(reserves);

        if(this.reservesAdapter.isEmpty()) {
            this.emptyView.displayEmpty();
        } else {
            this.emptyView.successLoading();
        }
    }





    //--- Swipe refresh callback ---
    // Refresh reserves list

    @Override
    public void onRefresh() {
        load();
    }

    //------------------------------
}
