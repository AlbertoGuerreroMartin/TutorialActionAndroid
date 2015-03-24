package com.edu.tutorialaction;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ProgressBar;

import com.edu.tutorialaction.network.AuthModel;
import com.edu.tutorialaction.network.RxLoaderActivity;
import com.welbits.izanrodrigo.emptyview.library.EmptyView;

import java.util.Map;

import butterknife.ButterKnife;
import butterknife.InjectView;


public class LoginActivity extends RxLoaderActivity<Map<String, String>> {

    private static final String API_KEY_SHARED_PREFERENCES_KEY= "api_key";


    @InjectView(R.id.login_username) EditText username;
    @InjectView(R.id.login_password) EditText password;
    @InjectView(R.id.login_button) Button loginButton;
    @InjectView(R.id.login_progress_bar) ProgressBar progressBar;

    @Override
    protected void onCreate(Bundle savedInstanceState) {

        SharedPreferences sharedPreferences = PreferenceManager.getDefaultSharedPreferences(getApplicationContext());
        String storedApiKey = sharedPreferences.getString(API_KEY_SHARED_PREFERENCES_KEY, "");
        if (storedApiKey != null && !storedApiKey.equalsIgnoreCase("")) {
            Intent intent = new Intent(this, MainActivity.class);
            startActivity(intent);
            finish();
        }

        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        ButterKnife.inject(this);
        progressBar.setVisibility(View.GONE);

        loginButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                load();
            }
        });
    }

    private void closeKeyboard(EditText input) {
        if (input != null) {
            InputMethodManager imm = (InputMethodManager) getSystemService(INPUT_METHOD_SERVICE);
            imm.hideSoftInputFromWindow(input.getWindowToken(), 0);
        }
    }

    private void load() {
        progressBar.setVisibility(View.VISIBLE);
        addSubscription(AuthModel.INSTANCE.login(LoginActivity.this, username.getText().toString(), password.getText().toString()));
    }


    @Override
    public void onError(Throwable e) {
        super.onError(e);
        progressBar.setVisibility(View.GONE);
    }

    @Override
    public void onNext(Map<String, String> stringStringMap) {
        SharedPreferences sharedPreferences = PreferenceManager.getDefaultSharedPreferences(getApplicationContext());
        progressBar.setVisibility(View.GONE);

        if (sharedPreferences.contains(API_KEY_SHARED_PREFERENCES_KEY)) {
            sharedPreferences.edit().remove(API_KEY_SHARED_PREFERENCES_KEY);
        }

        sharedPreferences.edit().putString(API_KEY_SHARED_PREFERENCES_KEY, stringStringMap.get("api_key")).apply();

        Intent intent = new Intent(this, MainActivity.class);
        startActivity(intent);
        finish();
    }
}

