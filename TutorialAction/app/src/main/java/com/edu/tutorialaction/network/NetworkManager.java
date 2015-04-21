package com.edu.tutorialaction.network;

import com.edu.tutorialaction.entity.Reserve;
import com.edu.tutorialaction.entity.TutorshipDay;
import com.edu.tutorialaction.entity.User;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import retrofit.RestAdapter;
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