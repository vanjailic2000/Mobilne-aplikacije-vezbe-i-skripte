package com.example.kolokvijum2.network;

import com.example.kolokvijum2.model.Post;

import java.util.ArrayList;

import retrofit2.Call;
import retrofit2.http.GET;
import retrofit2.http.Path;

public interface PostService {

    @GET("posts")
    Call<ArrayList<Post>> getAll();

    // korisno ako varijanta zadatka traži i pojedinačni post po ID-u
    @GET("posts/{id}")
    Call<Post> getById(@Path("id") int id);
}
