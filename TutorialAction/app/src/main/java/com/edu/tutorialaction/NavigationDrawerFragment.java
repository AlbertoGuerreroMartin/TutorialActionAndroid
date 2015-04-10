package com.edu.tutorialaction;


import android.app.Activity;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.res.Configuration;
import android.graphics.Color;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBar;
import android.support.v7.app.ActionBarDrawerToggle;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import com.balysv.materialripple.MaterialRippleLayout;
import com.edu.tutorialaction.network.AuthModel;
import com.edu.tutorialaction.network.RxLoaderFragment;

import java.util.List;
import java.util.Map;

import butterknife.ButterKnife;
import butterknife.InjectViews;
import rx.Observer;

/**
 * Fragment used for managing interactions for and presentation of a navigation drawer.
 * See the <a href="https://developer.android.com/design/patterns/navigation-drawer.html#Interaction">
 * design guidelines</a> for a complete explanation of the behaviors implemented here.
 */
public class NavigationDrawerFragment extends RxLoaderFragment<Map<String, String>> implements View.OnClickListener {

    /**
     * A pointer to the current callbacks instance (the Activity).
     */
    private NavigationDrawerCallbacks mCallbacks;

    /**
     * Helper component that ties the action bar to the navigation drawer.
     */
    private ActionBarDrawerToggle mDrawerToggle;
    private DrawerLayout mDrawerLayout;
    private int currentSelectedSectionId;

    @InjectViews({R.id.section_1, R.id.section_2, R.id.section_3, R.id.logout_section})
    List<MaterialRippleLayout> drawerSections;

    private View mFragmentContainerView;

    public NavigationDrawerFragment() {
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        // Indicate that this fragment would like to influence the set of actions in the action bar.
        setHasOptionsMenu(true);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_navigation_drawer, container, false);
        ButterKnife.inject(this, view);

        for(MaterialRippleLayout section : drawerSections) {
            section.setOnClickListener(this);
        }

        // Select either the default item (0) or the last selected item.
        selectItem(0, new ReservesFragment());
        currentSelectedSectionId = R.id.section1;

        return view;
    }

    @Override public void onClick(View v) {

        if(currentSelectedSectionId == v.getId()) {
            mDrawerLayout.closeDrawer(mFragmentContainerView);
        } else {

            currentSelectedSectionId = v.getId();

            switch (v.getId()) {
                case R.id.section1:
                    Toast.makeText(getActivity().getApplicationContext(), "Sección 1", Toast.LENGTH_SHORT).show();
                    selectItem(drawerSections.indexOf((MaterialRippleLayout) v.getParent()), new ReservesFragment());
                    break;

                case R.id.section2:
                    Toast.makeText(getActivity().getApplicationContext(), "Sección 2", Toast.LENGTH_SHORT).show();
//                selectItem(drawerSections.indexOf((MaterialRippleLayout) v.getParent()));
                    break;

                case R.id.section3:
                    Toast.makeText(getActivity().getApplicationContext(), "Sección 3", Toast.LENGTH_SHORT).show();
//                selectItem(drawerSections.indexOf((MaterialRippleLayout) v.getParent()));
                    break;

                case R.id.logout_section_textview:
                    System.out.println("ATTEMPT TO LOGOUT");
                    addSubscription(AuthModel.INSTANCE.logout(new Observer<Map<String, String>>() {
                        @Override
                        public void onCompleted() {
                            System.out.println("LOGOUT COMPLETED");
                        }

                        @Override
                        public void onError(Throwable e) {
                            System.out.println("LOGOUT ERROR");
                        }

                        @Override
                        public void onNext(Map<String, String> stringStringMap) {
                            System.out.println("LOGOUT NEXT");
                            System.out.println("Logout status: " + stringStringMap.get("status"));
                            SharedPreferences sharedPreferences = PreferenceManager.getDefaultSharedPreferences(getActivity().getApplicationContext());
                            sharedPreferences.edit().remove("api_key").apply();

                            Intent intent = new Intent(getActivity(), LoginActivity.class);
                            startActivity(intent);
                            getActivity().finish();
                        }
                    }, getActivity().getApplicationContext()));
                    break;
            }
        }
    }

    //--- Butterknife interfaces ---
    static final ButterKnife.Action<View> UNCHECK = new ButterKnife.Action<View>() {
        @Override
        public void apply(View view, int index) {
            ((MaterialRippleLayout) view).setRippleBackground(Color.WHITE);
        }
    };
    //------------------------------

    private void selectItem(int position, RxLoaderFragment<Object> fragment) {

        ButterKnife.apply(drawerSections, UNCHECK);
        drawerSections.get(position).setRippleBackground(Color.LTGRAY);

        if (mDrawerLayout != null) {
            mDrawerLayout.closeDrawer(mFragmentContainerView);
        }
        if (mCallbacks != null) {
            mCallbacks.onNavigationDrawerItemSelected(position, fragment);
        }
    }

    public boolean isDrawerOpen() {
        return mDrawerLayout != null && mDrawerLayout.isDrawerOpen(mFragmentContainerView);
    }

    /**
     * Users of this fragment must call this method to set up the navigation drawer interactions.
     *
     * @param fragmentId   The android:id of this fragment in its activity's layout.
     * @param drawerLayout The DrawerLayout containing this fragment's UI.
     */
    public void setUp(int fragmentId, DrawerLayout drawerLayout) {
        mFragmentContainerView = getActivity().findViewById(fragmentId);
        mDrawerLayout = drawerLayout;

        // set a custom shadow that overlays the main content when the drawer opens
        mDrawerLayout.setDrawerShadow(R.drawable.drawer_shadow, GravityCompat.START);
        // set up the drawer's list view with items and click listener

        ActionBar actionBar = ((MainActivity) getActivity()).getSupportActionBar();
        actionBar.setDisplayHomeAsUpEnabled(true);
        actionBar.setHomeButtonEnabled(true);

        // ActionBarDrawerToggle ties together the the proper interactions
        // between the navigation drawer and the action bar app icon.

        mDrawerToggle = new ActionBarDrawerToggle(
                getActivity(),                    /* host Activity */
                mDrawerLayout,                    /* DrawerLayout object */
                R.string.navigation_drawer_open,  /* "open drawer" description for accessibility */
                R.string.navigation_drawer_close  /* "close drawer" description for accessibility */
        ) {
            @Override
            public void onDrawerClosed(View drawerView) {
                super.onDrawerClosed(drawerView);
                if (!isAdded()) {
                    return;
                }

                getActivity().invalidateOptionsMenu(); // calls onPrepareOptionsMenu()
            }

            @Override
            public void onDrawerOpened(View drawerView) {
                super.onDrawerOpened(drawerView);
                if (!isAdded()) {
                    return;
                }

                getActivity().invalidateOptionsMenu(); // calls onPrepareOptionsMenu()
            }
        };


        // Defer code dependent on restoration of previous instance state.
        mDrawerLayout.post(new Runnable() {
            @Override
            public void run() {
                mDrawerToggle.syncState();
            }
        });

        mDrawerLayout.setDrawerListener(mDrawerToggle);
    }

    @Override
    public void onAttach(Activity activity) {
        super.onAttach(activity);
        try {
            mCallbacks = (NavigationDrawerCallbacks) activity;
        } catch (ClassCastException e) {
            throw new ClassCastException("Activity must implement NavigationDrawerCallbacks.");
        }
    }

    @Override
    public void onDetach() {
        super.onDetach();
        mCallbacks = null;
    }

    @Override
    public void onConfigurationChanged(Configuration newConfig) {
        super.onConfigurationChanged(newConfig);
        // Forward the new configuration the drawer toggle component.
        mDrawerToggle.onConfigurationChanged(newConfig);
    }

    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
        // If the drawer is open, show the global app actions in the action bar. See also
        // showGlobalContextActionBar, which controls the top-left area of the action bar.
        if (mDrawerLayout != null && isDrawerOpen()) {
            inflater.inflate(R.menu.global, menu);
            showGlobalContextActionBar();
        }
        super.onCreateOptionsMenu(menu, inflater);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if (mDrawerToggle.onOptionsItemSelected(item)) {
            return true;
        }

        if (item.getItemId() == R.id.action_example) {
            Toast.makeText(getActivity(), "Example action.", Toast.LENGTH_SHORT).show();
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    /**
     * Per the navigation drawer design guidelines, updates the action bar to show the global app
     * 'context', rather than just what's in the current screen.
     */
    private void showGlobalContextActionBar() {
        ActionBar actionBar = getActionBar();
        actionBar.setDisplayShowTitleEnabled(true);
        actionBar.setNavigationMode(ActionBar.NAVIGATION_MODE_STANDARD);
        actionBar.setTitle(R.string.app_name);
    }

    private ActionBar getActionBar() {
        return ((MainActivity) getActivity()).getSupportActionBar();
    }

    @Override
    public void onNext(Map<String, String> stringStringMap) {

    }

    /**
     * Callbacks interface that all activities using this fragment must implement.
     */
    public static interface NavigationDrawerCallbacks {
        /**
         * Called when an item in the navigation drawer is selected.
         */
        void onNavigationDrawerItemSelected(int position, RxLoaderFragment<Object> fragment);
    }
}
