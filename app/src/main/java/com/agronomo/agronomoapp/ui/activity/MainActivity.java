package com.agronomo.agronomoapp.ui.activity;

import android.content.SharedPreferences;
import android.os.Bundle;
import android.util.Log;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;

import com.agronomo.agronomoapp.databinding.ActivityMainBinding;
import com.agronomo.agronomoapp.network.RetrofitInstance;
import com.agronomo.agronomoapp.network.dto.AnimalListDto;
import com.agronomo.agronomoapp.network.service.CowApiService;
import com.agronomo.agronomoapp.ui.adapter.CowListAdapter;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Set;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class MainActivity extends AppCompatActivity {

    private static final String TAG = "MainActivity";

    private ActivityMainBinding binding;

    private SharedPreferences sharedPreferences;

    private CowListAdapter CowListAdapter = new CowListAdapter();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityMainBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        sharedPreferences = getSharedPreferences("com.agronomo.agronomoapp", MODE_PRIVATE);
        configureRecyclerView();
        loadDogBreeds();

    }

    private void loadDogBreeds() {

        CowApiService cowApiService = RetrofitInstance.getRetrofitInstance(sharedPreferences)
                .create(CowApiService.class);
        Call<AnimalListDto> call = cowApiService.getAllAnimal();
        call.enqueue(new Callback<AnimalListDto>() {
            @Override
            public void onResponse(Call<AnimalListDto> call, Response<AnimalListDto> response) {
                if (response.isSuccessful()) {
                    Map<String, String[]> animalMap = response.body().getMessage();
                    Set<String> animals = animalMap.keySet();
                    Log.d("Developer", "Animal: " + animals);
                    String breed = (String) animals.toArray()[0];
                    Log.d("Developer", "Animal: " + breed);
                    loadAnimalImages(breed);

                } else {
                    Log.e(TAG, "Error en la respuesta de la API");
                }
            }

            private void loadAnimalImages(String breed) {
                Call<BreedImagesDto> breedImages = dogApiService.getBreedImages(breed);
                breedImages.enqueue(new Callback<BreedImagesDto>() {
                    @Override
                    public void onResponse(Call<BreedImagesDto> call, Response<BreedImagesDto> response) {
                        if (response.isSuccessful()) {
                            BreedImagesDto body = response.body();
                            List<DogBreed> dogs = new ArrayList<>();
                            for (String image : body.getMessage()) {
                                dogs.add(new DogBreed(breed, image));
                            }
                            dogsListAdapter.update(dogs);
                        } else {
                            Log.e(TAG, "Error en la respuesta de la API");
                        }
                    }

                    @Override
                    public void onFailure(Call<BreedImagesDto> call, Throwable t) {
                        Log.e(TAG, "Error al llamar a la API", t);
                    }
                });
                Log.d("Developer", "Breed: " + breed);
            }

            @Override
            public void onFailure(Call<AnimalListDto> call, Throwable t) {
                Log.e(TAG, "Error al llamar a la API", t);
            }
        });
    }

    private void configureRecyclerView() {
        binding.include recyclerView.setLayoutManager(new LinearLayoutManager(this));
        binding.recyclerView.setAdapter(dogsListAdapter);
    }


    private void logout() {
        sharedPreferences.edit().remove("token").apply();
    }

    private void saveToken() {
        sharedPreferences.edit().putString("token", "token").apply();
    }

}