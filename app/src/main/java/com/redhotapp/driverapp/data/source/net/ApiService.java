package com.redhotapp.driverapp.data.source.net;

import com.redhotapp.driverapp.data.GitHubRepo;
import com.redhotapp.driverapp.data.Task;

import java.util.List;

import io.reactivex.Observable;
import retrofit2.Call;
import retrofit2.http.GET;
import retrofit2.http.Path;

public interface ApiService {

    @GET("/users/{user}/repos")
    Call<List<GitHubRepo>> reposForuser(@Path("user") String user);
}