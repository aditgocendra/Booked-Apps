package com.ark.bookedapps.Utility;

import android.content.Context;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.widget.Toast;

import androidx.annotation.NonNull;

import com.ark.bookedapps.Notification.ClientApi;
import com.ark.bookedapps.Notification.ServiceApi;
import com.ark.bookedapps.View.OrderSalon;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class Utilities {

    public static void sendNotification(String message, Context mContext){
        ClientApi.getClient().create(ServiceApi.class).sendMessageNotification(
                Constant.getRemoteMessageHeaders(),
                message
        ).enqueue(new Callback<String>() {
            @Override
            public void onResponse(@NonNull Call<String> call, @NonNull Response<String> response) {
                if (response.isSuccessful()){
                    try {
                        if (response.body() != null){
                            JSONObject responseJSON = new JSONObject(response.body());
                            JSONArray result = responseJSON.getJSONArray("result");

                            if (responseJSON.getInt("failure") == 1){
                                JSONObject error = (JSONObject) result.get(0);
                                Toast.makeText(mContext, error.getString("error"), Toast.LENGTH_SHORT).show();
                                return;
                            }
                        }
                    }catch (JSONException e){
                        e.printStackTrace();
                    }
                    Toast.makeText(mContext, "Notifikasi berhasil dikirim", Toast.LENGTH_SHORT).show();
                }else {
                    Toast.makeText(mContext, "Error : "+response.code(), Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onFailure(@NonNull Call<String> call, @NonNull Throwable t) {
                Toast.makeText(mContext, t.getMessage(), Toast.LENGTH_SHORT).show();
            }
        });
    }

    public static boolean checkConnection(Context mContext){

        ConnectivityManager connectivityManager = (ConnectivityManager) mContext.getSystemService(Context.CONNECTIVITY_SERVICE);

        NetworkInfo wifiConn = connectivityManager.getNetworkInfo(ConnectivityManager.TYPE_WIFI);
        NetworkInfo mobileConn = connectivityManager.getNetworkInfo(ConnectivityManager.TYPE_MOBILE);

        if (wifiConn != null && wifiConn.isConnected() || mobileConn != null && mobileConn.isConnected()){
            return true;
        }else {
            return false;
        }
    }
}
