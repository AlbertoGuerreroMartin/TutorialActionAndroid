package com.edu.tutorialaction.network;

import android.content.Context;
import android.content.SharedPreferences;
import android.preference.PreferenceManager;

import com.edu.tutorialaction.entity.Reserve;

import java.util.List;

import rx.Observer;
import rx.Subscription;
import rx.android.schedulers.AndroidSchedulers;
import rx.schedulers.Schedulers;
import rx.subjects.AsyncSubject;

/**
 * Created by albertoguerreromartin on 16/03/15.
 */
public enum ReserveModel {
    INSTANCE;

    private static final String API_KEY_SHARED_PREFERENCES_KEY= "api_key";

    private AsyncSubject<Object> request;

    public Subscription getReserves(Observer<Object> observer, Context context) {
        // If there's a request in background, subscribe to it
        if (request != null) {
            return request.subscribe(observer);
        }

        // Otherwise start a new request
        request = AsyncSubject.create();
        Subscription subscription = request.subscribe(observer);

        // Clear pending request on load finished
        request.subscribe(new EndObserver<Object>() {
            @Override
            public void onEnd() {
                request = null;
            }
        });

        // Load reserves from network
        SharedPreferences sharedPreferences = PreferenceManager.getDefaultSharedPreferences(context);
        NetworkManager.INSTANCE.getClient().getReserves(sharedPreferences.getString(API_KEY_SHARED_PREFERENCES_KEY, ""))
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(request);

        return subscription;
    }

    public Subscription removeReserve(Observer<Object> observer, Context context, int reserveID) {
        // If there's a request in background, subscribe to it
        if (request != null) {
            return request.subscribe(observer);
        }

        // Otherwise start a new request
        request = AsyncSubject.create();
        Subscription subscription = request.subscribe(observer);

        // Clear pending request on load finished
        request.subscribe(new EndObserver<Object>() {
            @Override
            public void onEnd() {
                request = null;
            }
        });

        // Load reserves from network
        SharedPreferences sharedPreferences = PreferenceManager.getDefaultSharedPreferences(context);
        NetworkManager.INSTANCE.getClient().removeReserve(sharedPreferences.getString(API_KEY_SHARED_PREFERENCES_KEY, ""), reserveID)
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(request);

        return subscription;
    }

}