package com.edu.tutorialaction.network;

import android.content.Context;
import android.content.SharedPreferences;
import android.preference.PreferenceManager;

import rx.Observer;
import rx.Subscription;
import rx.android.schedulers.AndroidSchedulers;
import rx.schedulers.Schedulers;
import rx.subjects.AsyncSubject;

/**
 * Created by albertoguerreromartin on 21/04/15.
 */
public enum NewReserveModel {
    INSTANCE;

    private static final String API_KEY_SHARED_PREFERENCES_KEY= "api_key";

    private AsyncSubject<Object> request;

    public Subscription getTimetable(Observer<Object> observer, Context context, int teacherID) {
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
        NetworkManager.INSTANCE.getClient().getTimetable(sharedPreferences.getString(API_KEY_SHARED_PREFERENCES_KEY, ""), teacherID)
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(request);

        return subscription;
    }

    public Subscription createReserve(Observer<Object> observer, Context context, int teacherID, int courseID, int tutorshipType, String reason, String date, String hour) {
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
        NetworkManager.INSTANCE.getClient().createReserve(sharedPreferences.getString(API_KEY_SHARED_PREFERENCES_KEY, ""), teacherID, courseID, tutorshipType, reason, date, hour)
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(request);

        return subscription;
    }

}
