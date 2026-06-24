package com.example.kolokvijum2.network;

import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

public class ClientUtils {

    // Zameniti tačnim base URL-om koji dobijete od beeceptor mock servera,
    // npr. "https://app.beeceptor.com/mock-server/dummy-json/"
    // VAŽNO: baseUrl mora da se završava sa "/"
    public static final String SERVICE_API_PATH = "https://app.beeceptor.com/mock-server/dummy-json/";

    public static Retrofit retrofit = new Retrofit.Builder()
            .baseUrl(SERVICE_API_PATH)
            .addConverterFactory(GsonConverterFactory.create())
            .build();

    public static PostService postService = retrofit.create(PostService.class);
}
