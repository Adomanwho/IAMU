package com.example.pokemon.model

import com.example.pokemon.api.Sprites
import com.example.pokemon.api.Stats
import com.example.pokemon.api.Types
import com.google.gson.annotations.SerializedName

data class Item(
    val _id: Long? = null,
    val name: String,
    val pokedexNumber: Int,
    val height: Int,
    val weight: Int,
    val type1: String,
    val type2: String,
    val hpStat: Int,
    val attackStat: Int,
    val defenseStat: Int,
    val specialAttackStat: Int,
    val specialDefenseStat: Int,
    val speedStat: Int,
    val spriteImagePath: String,
    val read: Boolean
)
