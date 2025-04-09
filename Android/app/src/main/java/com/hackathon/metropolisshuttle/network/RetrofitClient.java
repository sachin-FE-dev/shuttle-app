package com.hackathon.metropolisshuttle.network;

/**
 * Created by Kavya Shravan on 03/04/25.
 */

import android.content.Context;

import com.hackathon.metropolisshuttle.Utils;

import okhttp3.OkHttpClient;
import okhttp3.logging.HttpLoggingInterceptor;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

public class RetrofitClient {

    private static Retrofit retrofit = null;

    public static Retrofit getClient(Context context) {
        if (retrofit == null) {

            // Set up logging interceptor for API requests
            HttpLoggingInterceptor interceptor = new HttpLoggingInterceptor();
            interceptor.setLevel(HttpLoggingInterceptor.Level.BODY);

            // Create OkHttpClient with the interceptor
            OkHttpClient client = new OkHttpClient.Builder()
                    .addInterceptor(interceptor)
                    .addInterceptor(chain -> {
                        // Add headers, Authorization token
                        String token = Utils.getString(context, "Token");
                        if (token != null && !token.trim().isEmpty()) {
                            return chain.proceed(chain.request().newBuilder()
                                    .addHeader("Content-Type", "application/json")
                                    //.addHeader("Authorization", "Bearer " + token)
                                    .build());
                        }
                        return chain.proceed(chain.request().newBuilder()
                                .addHeader("Content-Type", "application/json")
                                .build());
                    })
                    .build();

            // Set up Retrofit instance
            retrofit = new Retrofit.Builder()
                    .baseUrl("https://mt-shuttle.loca.lt")
                    .client(client)
                    .addConverterFactory(GsonConverterFactory.create())
                    .build();
        }
        return retrofit;
    }
}
