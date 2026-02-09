package com.example.pokemon.api

import android.content.ContentValues
import android.content.Context
import android.util.Log
import com.example.pokemon.POKEMON_PROVIDER_CONTENT_URI
import com.example.pokemon.PokemonReceiver

import com.example.pokemon.framework.sendBroadcast
import com.example.pokemon.model.Item
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import retrofit2.create
import java.io.File
import java.io.FileOutputStream
import java.net.URL
import java.util.concurrent.atomic.AtomicInteger
import kotlin.concurrent.thread

class PokemonFetcher (private val context: Context){
    private val pokemonApi: PokemonApi
    private val pokemonList = mutableListOf<Item>()


    init {
        val retrofit = Retrofit.Builder()
            .baseUrl(API_URL)
            .addConverterFactory(GsonConverterFactory.create())
            .build()
        pokemonApi = retrofit.create<PokemonApi>()
    }

    fun fetchItems(count: Int = 151){
        Log.d("FETCHING ITEMS STARTED", "STARTED")
        pokemonList.clear()
        val completedRequests = AtomicInteger(0)
        val totalRequests = count

        for (number in 1..count){
            val request = pokemonApi.fetchItem(number)
            request.enqueue(object : Callback<Json4Kotlin_Base>{
                override fun onResponse(
                    call: Call<Json4Kotlin_Base?>,
                    response: Response<Json4Kotlin_Base?>
                ) {
                    response.body()?.let { pokemon ->
                        // Download image in background
                        thread {
                            try {
                                val imagePath = downloadImage(pokemon.sprites.front_default, pokemon.id)
                                val item = convertToItem(pokemon, imagePath)
                                saveToDatabase(item)
                                synchronized(pokemonList) {
                                    pokemonList.add(item)
                                }
                            } catch (e: Exception) {
                                Log.e("ERROR", "Failed to download image for ${pokemon.name}", e)
                            }

                            // Check if all requests completed
                            if (completedRequests.incrementAndGet() == totalRequests) {
                                onAllDataFetched()
                            }
                        }
                    } ?: run {
                        if (completedRequests.incrementAndGet() == totalRequests) {
                            onAllDataFetched()
                        }
                    }
                }

                override fun onFailure(
                    call: Call<Json4Kotlin_Base?>,
                    t: Throwable
                ) {
                    Log.e("ERROR", t.toString(), t)

                    // Still count failed requests
                    if (completedRequests.incrementAndGet() == totalRequests) {
                        onAllDataFetched()
                    }
                }
            })
        }
    }

    private fun downloadImage(imageUrl: String, pokemonId: Int): String {
        // Create pokemon_sprites directory in internal storage
        val spritesDir = File(context.filesDir, "pokemon_sprites")
        if (!spritesDir.exists()) {
            spritesDir.mkdirs()
        }

        // Create file for this pokemon's sprite
        val imageFile = File(spritesDir, "pokemon_$pokemonId.png")
        Log.d("IMAGE_DOWNLOAD", "Saving image to: ${imageFile.absolutePath}")
        // Download image
        URL(imageUrl).openStream().use { input ->
            FileOutputStream(imageFile).use { output ->
                input.copyTo(output)
            }
        }

        Log.d("IMAGE_DOWNLOAD", "File exists after download: ${imageFile.exists()}")
        return imageFile.absolutePath
    }

    private fun convertToItem(pokemon: Json4Kotlin_Base, imagePath: String): Item {
        // Get types
        val type1 = pokemon.types.getOrNull(0)?.type?.name ?: "None"
        val type2 = pokemon.types.getOrNull(1)?.type?.name ?: "None"

        // Get stats - create a map for easy lookup
        val statsMap = pokemon.stats.associate { it.stat.name to it.base_stat }

        return Item(
            name = pokemon.name,
            pokedexNumber = pokemon.id,
            height = pokemon.height,
            weight = pokemon.weight,
            type1 = type1,
            type2 = type2,
            hpStat = statsMap["hp"] ?: 0,
            attackStat = statsMap["attack"] ?: 0,
            defenseStat = statsMap["defense"] ?: 0,
            specialAttackStat = statsMap["special-attack"] ?: 0,
            specialDefenseStat = statsMap["special-defense"] ?: 0,
            speedStat = statsMap["speed"] ?: 0,
            spriteImagePath = imagePath,
            read = false
        )
    }

    private fun saveToDatabase(item: Item) {
        val values = ContentValues().apply {
            put(Item::name.name, item.name)
            put(Item::pokedexNumber.name, item.pokedexNumber)
            put(Item::height.name, item.height)
            put(Item::weight.name, item.weight)
            put(Item::type1.name, item.type1)
            put(Item::type2.name, item.type2)
            put(Item::hpStat.name, item.hpStat)
            put(Item::attackStat.name, item.attackStat)
            put(Item::defenseStat.name, item.defenseStat)
            put(Item::specialAttackStat.name, item.specialAttackStat)
            put(Item::specialDefenseStat.name, item.specialDefenseStat)
            put(Item::speedStat.name, item.speedStat)
            put(Item::spriteImagePath.name, item.spriteImagePath)
            put(Item::read.name, false)
        }

        context.contentResolver.insert(
            POKEMON_PROVIDER_CONTENT_URI,
            values
        )

        Log.d("PokemonFetcher", "Saved ${item.name} to database")
    }

    private fun onAllDataFetched() {
        Log.d("PokemonFetcher", "Fetched ${pokemonList.size} pokemon")
        // Do something with pokemonList (save to database, etc.)
        context.sendBroadcast<PokemonReceiver>()
    }

    fun getPokemonList(): List<Item> = pokemonList.toList()
}