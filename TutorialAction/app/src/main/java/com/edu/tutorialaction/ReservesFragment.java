package com.edu.tutorialaction;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.support.v4.widget.SwipeRefreshLayout;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ListView;
import android.widget.Toast;

import com.edu.tutorialaction.entity.Course;
import com.edu.tutorialaction.entity.Reserve;
import com.edu.tutorialaction.network.NetworkManager;
import com.edu.tutorialaction.network.ReserveModel;
import com.edu.tutorialaction.network.RxLoaderFragment;
import com.edu.tutorialaction.util.CustomSwipeRefreshLayout;
import com.melnykov.fab.FloatingActionButton;
import com.welbits.izanrodrigo.emptyview.library.EmptyView;

import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;
import java.util.Locale;

import butterknife.ButterKnife;
import butterknife.InjectView;
import retrofit.RetrofitError;

public class ReservesFragment extends RxLoaderFragment<Object> implements SwipeRefreshLayout.OnRefreshListener {

    @InjectView(R.id.reserves_swipe_container) CustomSwipeRefreshLayout swipeRefreshLayout;
    @InjectView(R.id.reservesList) ListView reservesList;
    @InjectView(R.id.reserves_emptyView) EmptyView emptyView;
    @InjectView(R.id.reserves_fab) FloatingActionButton floatingActionButton;
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
        this.swipeRefreshLayout.setList(reservesList);
        this.swipeRefreshLayout.setOnRefreshListener(this);
        this.swipeRefreshLayout.setColorSchemeColors(
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
                intent.putExtra("courses", Course.LIST_SERIALIZER.toJson(((MainActivity) getActivity()).getUserInfo().getCourses()));
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

        // If user unauthorized, show login
        int errorCode = ((RetrofitError) e).getResponse().getStatus();
        if(errorCode == 401) {
            NetworkManager.sessionExpiration(getActivity(), this);
        }
    }

    @Override
    public void onNext(Object reserves) {
        this.swipeRefreshLayout.setRefreshing(false);
        this.reservesAdapter.clearReserves();

        Collections.sort((List<Reserve>) reserves, new Comparator<Reserve>() {
            @Override
            public int compare(Reserve lhs, Reserve rhs) {
                DateFormat format = new SimpleDateFormat("dd/MM/yyyy", Locale.ENGLISH);
                int comparison = 0;
                try {
                    comparison = format.parse(rhs.getDate()).compareTo(format.parse(lhs.getDate()));
                } catch (ParseException e) {
                    e.printStackTrace();
                }

                return comparison;
            }
        });

        this.reservesAdapter.addReserves((List<Reserve>) reserves);

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
