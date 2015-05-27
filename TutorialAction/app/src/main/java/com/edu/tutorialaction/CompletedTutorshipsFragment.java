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

import com.edu.tutorialaction.entity.Course;
import com.edu.tutorialaction.entity.Reserve;
import com.edu.tutorialaction.entity.Tutorship;
import com.edu.tutorialaction.network.CompletedTutorshipModel;
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


public class CompletedTutorshipsFragment extends RxLoaderFragment<Object> implements SwipeRefreshLayout.OnRefreshListener {

    private static final String ROLE_SHARED_PREFERENCES_KEY= "role";

    @InjectView(R.id.completed_tutorships_swipe_container) CustomSwipeRefreshLayout swipeRefreshLayout;
    @InjectView(R.id.completed_tutorships_list) ListView completedTutorshipsList;
    @InjectView(R.id.completed_tutorships_emptyView) EmptyView emptyView;
    @InjectView(R.id.completed_tutorships_fab) FloatingActionButton floatingActionButton;
    private CompletedTutorshipsAdapter completedTutorshipsAdapter;

    private int sortOption;
    private boolean completedTutorshipRegistered;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_completed_tutorships, container, false);

        ButterKnife.inject(this, view);
        setHasOptionsMenu(true);

        return view;
    }
    

    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);


        this.sortOption = R.id.order_by_date;
        this.completedTutorshipRegistered = false;

        //--- Set refresh ---
        this.swipeRefreshLayout.setList(completedTutorshipsList);
        this.swipeRefreshLayout.setOnRefreshListener(this);
        this.swipeRefreshLayout.setColorSchemeColors(
                getResources().getColor(R.color.colorPrimary),
                getResources().getColor(R.color.colorAccent));
        //-------------------

        //--- Set list adapter ---
        this.completedTutorshipsList.setAdapter(this.completedTutorshipsAdapter = new CompletedTutorshipsAdapter(getActivity()));
        //------------------------


        SharedPreferences sharedPreferences = PreferenceManager.getDefaultSharedPreferences(getActivity().getApplicationContext());
        String role = sharedPreferences.getString(ROLE_SHARED_PREFERENCES_KEY, "");

        if (role != null && role.compareTo("teacher") == 0) {
            this.floatingActionButton.attachToListView(this.completedTutorshipsList);
            this.floatingActionButton.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Intent intent = new Intent(getActivity(), NewCompletedTutorship.class);
                    intent.putExtra("courses", Course.LIST_SERIALIZER.toJson(((MainActivity) getActivity()).getUserInfo().getCourses()));
                    intent.putExtra("teacherID", ((MainActivity) getActivity()).getUserInfo().getUserID());
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
        addSubscription(CompletedTutorshipModel.INSTANCE.getCompletedTutorships(CompletedTutorshipsFragment.this, getActivity().getApplicationContext()));
    }


    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if(data != null) {
            this.completedTutorshipRegistered = data.getBooleanExtra("completedTutorshipRegistered", false);
            if (completedTutorshipRegistered) {
                System.out.println("Nueva tutoria completada");
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


    private void sortByReserveOrder(List<Tutorship> tutorships) {
        Collections.sort(tutorships, new Comparator<Tutorship>() {
            @Override
            public int compare(Tutorship lhs, Tutorship rhs) {
                return Integer.valueOf(rhs.getTutorshipid()).compareTo(lhs.getTutorshipid());
            }
        });
    }

    private void sortByDate(List<Tutorship> tutorships) {
        Collections.sort(tutorships, new Comparator<Tutorship>() {
            @Override
            public int compare(Tutorship lhs, Tutorship rhs) {
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
    public void onNext(Object completedTutorships) {
        this.swipeRefreshLayout.setRefreshing(false);
        this.completedTutorshipsAdapter.clearTutorships();

        if(this.sortOption == R.id.order_by_date) {
            sortByDate((List<Tutorship>) completedTutorships);
        } else {
            sortByReserveOrder((List<Tutorship>) completedTutorships);
        }

        this.completedTutorshipsAdapter.addTutorships((List<Tutorship>) completedTutorships);

        if(this.completedTutorshipsAdapter.isEmpty()) {
            this.emptyView.displayEmpty();
        } else {
            this.emptyView.successLoading();
        }
    }

    @Override
    public void onError(Throwable e) {
        super.onError(e);
        this.emptyView.errorLoading();
        this.swipeRefreshLayout.setRefreshing(false);
    }


    //--- Swipe refresh callback ---
    // Refresh reserves list

    @Override
    public void onRefresh() {
        load();
    }

    //------------------------------
}
