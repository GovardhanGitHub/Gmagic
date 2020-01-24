package com.example.gmagic;


import android.os.Build;
import android.util.Log;

import androidx.annotation.RequiresApi;

import org.jetbrains.annotations.NotNull;

import java.io.IOException;

import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.MediaType;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;
public class PostContact {

    StringBuffer stringBuffer = new StringBuffer();


    private static final String TAG = "PostContact";

    String result = "Nothing to Know";




    OkHttpClient client = new OkHttpClient.Builder()
            .addInterceptor(new BasicAuthInterceptor("elastic", "hCKQi4CqydovNvoWugyOegPd"))
            .build();
    public static final MediaType JSON = MediaType.get("application/json; charset=utf-8");


    @RequiresApi(api = Build.VERSION_CODES.KITKAT)
    String post(String url, String json) throws IOException {
        RequestBody body = RequestBody.create(json, JSON);
        Request request = new Request.Builder()
                .url(url)
                .put(body)
                .build();
        client.newCall(request).enqueue(new Callback() {
            @Override
            public void onFailure(@NotNull Call call, @NotNull final IOException e) {
                Log.i(TAG, "onFailure: "+e.getMessage());
              String  result = "Error : something went wrong check Internet connectivity "+e.getMessage().toString();
            }

            @Override
            public void onResponse(@NotNull Call call, @NotNull final Response response) throws IOException {
                Log.i(TAG, "onResponse: "+response.toString());
                if (response.isSuccessful())
                {
                 String   result = "Successfully loaded : "+response.code();
                }
                String result = "Error : something went wrong in onResponse Method..";

            }
        });
        /*try (Response response = client.newCall(request).execute()) {
            return response.body().string();
        }*/
        return result;

    }






   /* @RequiresApi(api = Build.VERSION_CODES.KITKAT)
    String post(String url, String json) throws IOException {
        RequestBody body = RequestBody.create(json, JSON);
        Request request = new Request.Builder()
                .url(url)
                .post(body)
                .build();


        client.newCall(request).enqueue(new Callback() {
            @Override
            public void onFailure(@NotNull Call call, @NotNull IOException e) {
                MainActivity.this.runOnUiThread(new Runnable() {
                    @Override
                    public void run() {


                    }
                });
            }

            @Override
            public void onResponse(@NotNull Call call, @NotNull Response response) throws IOException {
            }
        });
        *//*try (Response response = client.newCall(request).execute()) {
            return response.body().string();
        }*//*
        return null;
    }*/
}