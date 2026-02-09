package com.example.pokemon.api

import android.content.Context
import android.content.Intent
import com.example.pokemon.PokemonReceiver
import com.example.pokemon.framework.sendBroadcast

class PokemonFetcher (private val context: Context){
    fun fetchItems(count: Int = 10){
        Thread.sleep(5000)

        context.sendBroadcast<PokemonReceiver>()
    }
}