package com.edu.tutorialaction;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.util.ArrayMap;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ListView;

import com.edu.tutorialaction.entity.Reserve;
import com.edu.tutorialaction.network.AuthModel;
import com.edu.tutorialaction.network.ReserveModel;
import com.edu.tutorialaction.network.RxLoaderFragment;
import com.welbits.izanrodrigo.emptyview.library.EmptyView;

import java.util.List;
import java.util.Map;

import butterknife.ButterKnife;
import butterknife.InjectView;
import rx.Observer;

/**
 * Created by albertoguerreromartin on 16/03/15.
 */
public class ReservesFragment extends RxLoaderFragment<List<Reserve>> {

    @InjectView(R.id.reservesList) ListView reservesList;
    @InjectView(R.id.emptyView) EmptyView emptyView;
    @InjectView(R.id.logout_button) Button logoutButton;
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

        logoutButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                addSubscription(AuthModel.INSTANCE.logout(new Observer<Map<String, String>>() {
                    @Override
                    public void onCompleted() {

                    }

                    @Override
                    public void onError(Throwable e) {

                    }

                    @Override
                    public void onNext(Map<String, String> stringStringMap) {
                        System.out.println("Logout status: " + stringStringMap.get("status"));
                        SharedPreferences sharedPreferences = PreferenceManager.getDefaultSharedPreferences(getActivity().getApplicationContext());
                        sharedPreferences.edit().remove("api_key").apply();

                        Intent intent = new Intent(getActivity(), LoginActivity.class);
                        startActivity(intent);
                        getActivity().finish();
                    }
                }, getActivity().getApplicationContext()));
            }
        });

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
        addSubscription(ReserveModel.INSTANCE.getReserves(ReservesFragment.this, getActivity().getApplicationContext()));
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
