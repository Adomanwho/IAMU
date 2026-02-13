package com.example.pokemon

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import com.example.pokemon.framework.setBooleanProperty
import com.example.pokemon.framework.startActivity

class PokemonReceiver : BroadcastReceiver() {

    override fun onReceive(context: Context, intent: Intent) {
        context.setBooleanProperty(DATA_IMPORTED)
        context.startActivity<HostActivity>()
    }
}