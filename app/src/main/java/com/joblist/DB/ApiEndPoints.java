package com.joblist.DB;

import com.joblist.Data.Repository.TransaksiRepository;

import retrofit2.Call;
import retrofit2.http.GET;

public interface ApiEndPoints {
    @GET("positions.json")
    Call<TransaksiRepository> readSiswa();
}
