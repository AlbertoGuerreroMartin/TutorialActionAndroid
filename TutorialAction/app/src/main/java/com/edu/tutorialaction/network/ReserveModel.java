package com.edu.tutorialaction.network;

import com.edu.tutorialaction.entity.Reserve;

import java.util.List;

import rx.Observer;
import rx.Subscription;
import rx.android.schedulers.AndroidSchedulers;
import rx.schedulers.Schedulers;
import rx.subjects.PublishSubject;

/**
 * Created by albertoguerreromartin on 16/03/15.
 */
public enum ReserveModel {
    INSTANCE;

    private PublishSubject<List<Reserve>> request;

    public Subscription getReserves(Observer<List<Reserve>> observer) {
        // If there's a request in background, subscribe to it
        if (request != null) {
            return request.subscribe(observer);
        }

        // Otherwise start a new request
        request = PublishSubject.create();
        Subscription subscription = request.subscribe(observer);

        // Clear pending request on load finished
        request.subscribe(new EndObserver<List<Reserve>>() {
            @Override
            public void onEnd() {
                request = null;
            }
        });

        // Load reserves from network
        NetworkManager.INSTANCE.getClient().getReserves()
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(request);

        return subscription;
    }


}