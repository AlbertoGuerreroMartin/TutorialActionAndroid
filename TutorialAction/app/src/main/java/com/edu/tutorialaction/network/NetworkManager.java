package com.edu.tutorialaction.network;

import android.app.Activity;
import android.content.Intent;
import android.content.SharedPreferences;
import android.preference.PreferenceManager;
import android.support.v4.app.Fragment;
import android.widget.Toast;

import com.edu.tutorialaction.LoginActivity;
import com.edu.tutorialaction.entity.Reserve;
import com.edu.tutorialaction.entity.Tutorship;
import com.edu.tutorialaction.entity.TutorshipDay;
import com.edu.tutorialaction.entity.User;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import retrofit.RestAdapter;
import retrofit.RetrofitError;
import retrofit.client.OkClient;
import retrofit.http.DELETE;
import retrofit.http.Field;
import retrofit.http.FormUrlEncoded;
import retrofit.http.GET;
import retrofit.http.Header;
import retrofit.http.POST;
import retrofit.http.Path;
import rx.Observable;

public enum NetworkManager {
    INSTANCE;

    private final RestClient client;

    NetworkManager() {
        client = new RestAdapter.Builder()
                .setEndpoint(RestClient.ENDPOINT)
                .setClient(new OkClient())
                .build()
                .create(RestClient.class);
    }

    public static void sessionExpiration(Activity activity, Fragment fragment) {

        SharedPreferences sharedPreferences = PreferenceManager.getDefaultSharedPreferences(activity.getApplicationContext());
        sharedPreferences.edit().remove("api_key").apply();

        Toast.makeText(activity.getApplicationContext(), "La sesión ha expirado. Inicie sesión de nuevo.", Toast.LENGTH_LONG).show();

        Intent intent = new Intent(activity, LoginActivity.class);

        if(fragment != null) {
            fragment.startActivity(intent);
        } else {
            activity.startActivity(intent);
        }

        activity.finish();
    }

    public RestClient getClient() {
        return client;
    }


    public interface RestClient {
        String ENDPOINT = "http://pieta.dalumetsisi.es";

        @FormUrlEncoded
        @POST("/login")
        Observable<Map<String, String>> login(@Field("username") String username, @Field("password") String password);

        @POST("/logout")
        Observable<Map<String, String>> logout(@Header("Api-Key") String apiKey);


        @GET("/api/user/reserves")
        Observable<List<Reserve>> getReserves(@Header("Api-Key") String apiKey);

        @GET("/api/user/completed")
        Observable<List<Tutorship>> getCompletedTutorships(@Header("Api-Key") String apiKey);

        @GET("/api/user/info")
        Observable<User> getInfo(@Header("Api-Key") String apiKey);

        @GET("/api/user/{teacherID}/timetable")
        Observable<ArrayList<TutorshipDay>> getTimetable(@Header("Api-Key") String apiKey, @Path("teacherID") int teacherID);

        @FormUrlEncoded
        @POST("/api/reserves/create")
        Observable<Map<String, String>> createReserve(@Header("Api-Key") String apiKey, @Field("teacherID") int teacherID, @Field("courseID") int courseID, @Field("tutorshipType") int tutorshipType, @Field("reason") String reason, @Field("date") String date, @Field("hour") String hour);

        @DELETE("/api/reserves/remove")
        Observable<List<Reserve>> removeReserve(@Header("Api-Key") String apiKey, @Field("reserveID") int reserveID);
    }
}