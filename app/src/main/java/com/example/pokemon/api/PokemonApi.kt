package com.example.pokemon.api

import retrofit2.Call
import retrofit2.http.GET
import retrofit2.http.Path

const val API_URL = "https://pokeapi.co/api/v2/"

interface PokemonApi {
    @GET("pokemon/{id}")
    fun fetchItem(@Path("id") id: Int) : Call<Json4Kotlin_Base>
}