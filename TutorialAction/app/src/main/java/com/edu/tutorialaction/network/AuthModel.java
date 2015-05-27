package com.edu.tutorialaction.network;

import android.content.Context;
import android.content.SharedPreferences;
import android.preference.PreferenceManager;

import java.util.Map;

import rx.Observer;
import rx.Subscription;
import rx.android.schedulers.AndroidSchedulers;
import rx.schedulers.Schedulers;
import rx.subjects.AsyncSubject;

/**
 * Created by albertoguerreromartin on 16/03/15.
 */
public enum AuthModel {
    INSTANCE, CompletedTutorshipModel;

    private static final String API_KEY_SHARED_PREFERENCES_KEY= "api_key";

    private AsyncSubject<Map<String, String>> request;

    public Subscription login(Observer<Map<String, String>> observer, String username, String password) {
        // If there's a request in background, subscribe to it
        if (request != null) {
            return request.subscribe(observer);
        }

        // Otherwise start a new request
        request = AsyncSubject.create();
        Subscription subscription = request.subscribe(observer);

        // Clear pending request on load finished
        request.subscribe(new EndObserver<Map<String, String>>() {
            @Override
            public void onEnd() {
                request = null;
            }
        });

        // Post login from network
        NetworkManager.INSTANCE.getClient().login(username, password)
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(request);

        return subscription;
    }

    public Subscription logout(Observer<Map<String, String>> observer, Context context) {
        // If there's a request in background, subscribe to it
        if (request != null) {
            return request.subscribe(observer);
        }

        // Otherwise start a new request
        request = AsyncSubject.create();
        Subscription subscription = request.subscribe(observer);

        // Clear pending request on load finished
        request.subscribe(new EndObserver<Map<String, String>>() {
            @Override
            public void onEnd() {
                request = null;
            }
        });

        // Post login from network
        SharedPreferences sharedPreferences = PreferenceManager.getDefaultSharedPreferences(context);
        NetworkManager.INSTANCE.getClient().logout(sharedPreferences.getString(API_KEY_SHARED_PREFERENCES_KEY, ""))
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(request);

        return subscription;
    }

}