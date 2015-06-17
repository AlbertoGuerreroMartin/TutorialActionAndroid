package com.edu.tutorialaction.network;

import android.content.Context;
import android.content.SharedPreferences;
import android.preference.PreferenceManager;
import com.edu.tutorialaction.entity.User;
import rx.Observer;
import rx.Subscription;
import rx.android.schedulers.AndroidSchedulers;
import rx.schedulers.Schedulers;
import rx.subjects.AsyncSubject;

/**
 * Created by albertoguerreromartin on 14/04/15.
 */
public enum UserModel {
    INSTANCE;

    private static final String API_KEY_SHARED_PREFERENCES_KEY= "api_key";

    private AsyncSubject<User> request;

    public Subscription getInfo(Observer<User> observer, Context context) {
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
        NetworkManager.INSTANCE.getClient().getInfo(sharedPreferences.getString(API_KEY_SHARED_PREFERENCES_KEY, ""))
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(request);

        return subscription;
    }
}