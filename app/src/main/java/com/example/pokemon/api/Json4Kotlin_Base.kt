package com.example.pokemon.api

import com.google.gson.annotations.SerializedName

data class Json4Kotlin_Base(
    @SerializedName("height") val height : Int,
    @SerializedName("id") val id : Int,
    @SerializedName("name") val name : String,
    @SerializedName("sprites") val sprites : Sprites,
    @SerializedName("stats") val stats : List<Stats>,
    @SerializedName("types") val types : List<Types>,
    @SerializedName("weight") val weight : Int
)
