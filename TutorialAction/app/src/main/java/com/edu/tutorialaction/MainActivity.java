package com.edu.tutorialaction;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBar;
import android.support.v7.app.ActionBarActivity;
import android.view.MenuItem;

import com.edu.tutorialaction.entity.Reserve;
import com.edu.tutorialaction.entity.User;
import com.edu.tutorialaction.network.RxLoaderActivity;
import com.edu.tutorialaction.network.RxLoaderFragment;
import com.edu.tutorialaction.network.UserModel;

import java.util.List;
import java.util.Map;

import rx.Observer;


public class MainActivity extends ActionBarActivity implements NavigationDrawerFragment.NavigationDrawerCallbacks {

    /**
     * Fragment managing the behaviors, interactions and presentation of the navigation drawer.
     */
    private NavigationDrawerFragment mNavigationDrawerFragment;
    private User userInfo;

    /**
     * Used to store the last screen title. For use in {@link #restoreActionBar()}.
     */
    private CharSequence mTitle;

    public User getUserInfo() {
        return userInfo;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        mNavigationDrawerFragment = (NavigationDrawerFragment) getSupportFragmentManager().findFragmentById(R.id.navigation_drawer);
        mTitle = getTitle();

        loadUserInfo();

        getSupportActionBar().setElevation(2);

    }

    public void loadUserInfo() {
        RxLoaderActivity<User> loader = new RxLoaderActivity<User>() {
            @Override
            public void onNext(User user) {
            }
        };

        loader.addSubscription(UserModel.INSTANCE.getInfo(new Observer<User>() {
            @Override
            public void onCompleted() {
                System.out.println("onCompleted");
            }

            @Override
            public void onError(Throwable e) {
                System.out.println("onError");
                e.printStackTrace();
            }

            @Override
            public void onNext(User user) {

                System.out.println(user.toString());
                userInfo = user;

                // Set up the drawer.
                mNavigationDrawerFragment.setUp(
                        R.id.navigation_drawer,
                        (DrawerLayout) findViewById(R.id.drawer_layout));

            }
        }, getApplicationContext()));
    }

//    @Override
//    public void onBackPressed() {
//    }

    @Override
    public void onNavigationDrawerItemSelected(int position, RxLoaderFragment<Object> fragment) {
        // update the main content by replacing fragments
        FragmentManager fragmentManager = getSupportFragmentManager();
        fragmentManager.beginTransaction()
                .replace(R.id.container, fragment)
                .commit();
    }

    public void restoreActionBar() {
        ActionBar actionBar = getSupportActionBar();
        actionBar.setDisplayShowTitleEnabled(true);
        actionBar.setTitle(mTitle);
    }


    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings) {
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

}
