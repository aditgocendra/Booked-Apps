package com.ark.bookedapps.Notification;

import java.util.HashMap;

import retrofit2.Call;
import retrofit2.http.Body;
import retrofit2.http.HeaderMap;
import retrofit2.http.POST;

public interface ServiceApi {
    @POST("send")
    Call<String> sendMessageNotification(
            @HeaderMap HashMap<String, String> headers,
            @Body String messageBody
            );
}
