package com.edu.tutorialaction;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.support.v4.widget.SwipeRefreshLayout;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
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
import java.util.Collections;
import java.util.Comparator;
import java.util.List;
import java.util.Locale;

import butterknife.ButterKnife;
import butterknife.InjectView;
import retrofit.RetrofitError;

public class ReservesFragment extends RxLoaderFragment<Object> implements SwipeRefreshLayout.OnRefreshListener {

    private static final String ROLE_SHARED_PREFERENCES_KEY= "role";

    @InjectView(R.id.reserves_swipe_container) CustomSwipeRefreshLayout swipeRefreshLayout;
    @InjectView(R.id.reservesList) ListView reservesList;
    @InjectView(R.id.reserves_emptyView) EmptyView emptyView;
    @InjectView(R.id.reserves_fab) FloatingActionButton floatingActionButton;
    private ReservesAdapter reservesAdapter;

    private int numberOfReserves;
    private boolean reservesAdded;
    private int sortOption;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_reserves, container, false);

        ButterKnife.inject(this, view);
        setHasOptionsMenu(true);

        return view;
    }

    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);

        this.numberOfReserves = 0;
        this.reservesAdded = false;
        this.sortOption = R.id.order_by_date;

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


        SharedPreferences sharedPreferences = PreferenceManager.getDefaultSharedPreferences(getActivity().getApplicationContext());
        String role = sharedPreferences.getString(ROLE_SHARED_PREFERENCES_KEY, "");

        if (role != null && role.compareTo("student") == 0) {
            this.floatingActionButton.attachToListView(this.reservesList);
            this.floatingActionButton.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Intent intent = new Intent(getActivity(), NewReserveActivity.class);
                    intent.putExtra("courses", Course.LIST_SERIALIZER.toJson(((MainActivity) getActivity()).getUserInfo().getCourses()));
                    startActivityForResult(intent, 0);
                }
            });
        } else {
            this.floatingActionButton.setVisibility(View.GONE);
        }

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
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if(data != null) {
            this.reservesAdded = data.getBooleanExtra("reserveAdded", false);
            if (reservesAdded) {
                System.out.println("Nueva tutoria reservada");
                load();
            }
        }
    }


    //--- Menu ---
    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
        inflater.inflate(R.menu.reserves, menu);
        super.onCreateOptionsMenu(menu, inflater);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {

        if(item.getItemId() != this.sortOption) {
            this.sortOption = item.getItemId();
            load();
        }

        return super.onOptionsItemSelected(item);
    }

    //------------


    private void sortByReserveOrder(List<Reserve> reserves) {
        Collections.sort(reserves, new Comparator<Reserve>() {
            @Override
            public int compare(Reserve lhs, Reserve rhs) {
                return Integer.valueOf(rhs.getReserveid()).compareTo(lhs.getReserveid());
            }
        });
    }

    private void sortByDate(List<Reserve> reserves) {
        Collections.sort(reserves, new Comparator<Reserve>() {
            @Override
            public int compare(Reserve lhs, Reserve rhs) {
                DateFormat format = new SimpleDateFormat("dd/MM/yyyy", Locale.ENGLISH);
                int comparison = 0;
                try {
                    comparison = format.parse(lhs.getDate()).compareTo(format.parse(rhs.getDate()));
                } catch (ParseException e) {
                    e.printStackTrace();
                }

                return comparison;
            }
        });
    }

    @Override
    public void onNext(Object reserves) {
        this.swipeRefreshLayout.setRefreshing(false);
        this.reservesAdapter.clearReserves();

        if(this.sortOption == R.id.order_by_date) {
            sortByDate((List<Reserve>) reserves);
        } else {
            sortByReserveOrder((List<Reserve>) reserves);
        }

        this.reservesAdapter.addReserves((List<Reserve>) reserves);

        if(this.reservesAdapter.isEmpty()) {
            this.emptyView.displayEmpty();
        } else {
            if(this.reservesAdded) {
                int numberOfNewReserves = ((List<Reserve>) reserves).size() - this.numberOfReserves;
                String toastText = numberOfNewReserves == 1 ? "Hay 1 nueva reserva." : "Hay " + numberOfNewReserves + " nuevas reservas.";
                Toast.makeText(getActivity().getApplicationContext(), toastText, Toast.LENGTH_SHORT).show();
                this.numberOfReserves += numberOfNewReserves;
                this.reservesAdded = false;
            } else {
                this.numberOfReserves = ((List<Reserve>) reserves).size();
            }

            this.emptyView.successLoading();
        }
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


    //--- Swipe refresh callback ---
    // Refresh reserves list

    @Override
    public void onRefresh() {
        load();
    }

    //------------------------------
}
