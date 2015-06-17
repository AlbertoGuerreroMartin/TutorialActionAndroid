package com.edu.tutorialaction.network;

import android.support.v4.app.Fragment;
import rx.Observer;
import rx.Subscription;
import rx.subscriptions.CompositeSubscription;

public abstract class RxLoaderFragment<T> extends Fragment implements Observer<T> {

    private final CompositeSubscription subscription = new CompositeSubscription();

    @Override
    public void onDestroy() {
        super.onDestroy();
        if (subscription != null && !subscription.isUnsubscribed()) {
            subscription.unsubscribe();
        }
    }

    protected void addSubscription(Subscription subscription) {
        if (subscription != null && !subscription.isUnsubscribed()) {
            this.subscription.add(subscription);
        }
    }

    @Override
    public void onError(Throwable e) {
        if(e != null) {
            e.printStackTrace();
        }
    }

    @Override
    public void onCompleted() {
        // Override in subclasses
    }
}