package com.agronomo.agronomoapp.network.service;

import com.agronomo.agronomoapp.network.dto.AnimalListDto;


import retrofit2.Call;
import retrofit2.http.GET;
import retrofit2.http.Path;

public interface CowApiService {
    @GET("api/animal")
    Call<AnimalListDto> getAllAnimal();


}