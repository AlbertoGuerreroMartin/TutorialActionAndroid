package com.edu.tutorialaction.network;

import com.edu.tutorialaction.entity.Reserve;

import java.util.List;

import retrofit.RestAdapter;
import retrofit.client.OkClient;
import retrofit.client.Response;
import retrofit.http.GET;
import retrofit.http.POST;
import rx.Observable;

public enum NetworkManager {
    INSTANCE;

    private final RestClient client;
    private String userAgent;

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
        String ENDPOINT = "http://192.168.1.36";


        @POST("/login")
        Response login(String username, String password);


        @GET("/api/user/reserves")
        Observable<List<Reserve>> getReserves();
    }
}