package com.edu.tutorialaction;

import android.content.Intent;
import android.os.Bundle;
import android.support.v4.widget.SwipeRefreshLayout;
import android.view.LayoutInflater;
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

    @InjectView(R.id.completed_tutorships_swipe_container) CustomSwipeRefreshLayout swipeRefreshLayout;
    @InjectView(R.id.completed_tutorships_list) ListView completedTutorshipsList;
    @InjectView(R.id.completed_tutorships_emptyView) EmptyView emptyView;
    @InjectView(R.id.completed_tutorships_fab) FloatingActionButton floatingActionButton;
    private CompletedTutorshipsAdapter completedTutorshipsAdapter;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_completed_tutorships, container, false);

        ButterKnife.inject(this, view);

        return view;
    }
    

    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);

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


        this.floatingActionButton.attachToListView(this.completedTutorshipsList);
        /*
        TODO: Implement NewCompletedTutorshipActivity
        this.floatingActionButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(getActivity(), NewReserveActivity.class);
                intent.putExtra("courses", Course.LIST_SERIALIZER.toJson(((MainActivity) getActivity()).getUserInfo().getCourses()));
                startActivity(intent);
            }
        });
        */

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
    public void onError(Throwable e) {
        super.onError(e);
        this.emptyView.errorLoading();
        this.swipeRefreshLayout.setRefreshing(false);
    }

    @Override
    public void onNext(Object completedTutorships) {
        this.swipeRefreshLayout.setRefreshing(false);
        this.completedTutorshipsAdapter.clearTutorships();

        Collections.sort((List<Tutorship>) completedTutorships, new Comparator<Tutorship>() {
            @Override
            public int compare(Tutorship lhs, Tutorship rhs) {
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

        this.completedTutorshipsAdapter.addTutorships((List<Tutorship>) completedTutorships);

        if(this.completedTutorshipsAdapter.isEmpty()) {
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
