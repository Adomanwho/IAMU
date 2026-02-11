package com.example.pokemon.framework

import android.annotation.SuppressLint
import android.app.Activity
import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.net.ConnectivityManager
import android.net.NetworkCapabilities
import android.os.Handler
import android.os.Looper
import androidx.preference.PreferenceManager
import android.view.View
import android.view.animation.AnimationUtils
import androidx.core.content.edit
import androidx.core.content.getSystemService
import com.example.pokemon.POKEMON_PROVIDER_CONTENT_URI
import com.example.pokemon.model.Item

fun View.applyAnimation(animationId: Int)
    = startAnimation(AnimationUtils.loadAnimation(context, animationId))

inline fun <reified T: Activity> Context.startActivity(){
    startActivity(Intent(this, T::class.java).apply {
        addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
    })
}
inline fun <reified T: BroadcastReceiver> Context.sendBroadcast(){
    sendBroadcast(Intent(this, T::class.java))
}

fun callDelayed(delay: Long, work: Runnable) {
    Handler(Looper.getMainLooper()).postDelayed(
        work,
        delay
    )
}

fun Context.getBooleanProperty (key: String) =
    PreferenceManager.getDefaultSharedPreferences(this)
        .getBoolean(key, false)

fun Context.setBooleanProperty (key: String, value: Boolean = true) =
    PreferenceManager.getDefaultSharedPreferences(this)
        .edit {
            putBoolean(key, value)
        }

fun Context.isOnline() : Boolean {
    val connectivityManager = getSystemService<ConnectivityManager>()
    connectivityManager?.activeNetwork?.let { network ->
        connectivityManager.getNetworkCapabilities(network)?.let { capabilities ->
            return capabilities.hasTransport(NetworkCapabilities.TRANSPORT_CELLULAR)
                    || capabilities.hasTransport(NetworkCapabilities.TRANSPORT_WIFI)
        }
    }
    return false
}

@SuppressLint("Range")
fun Context.fetchItems() : MutableList<Item>{
    val items = mutableListOf<Item>()

    contentResolver.query(
        POKEMON_PROVIDER_CONTENT_URI,
        null,
        null,
        null,
        null,
    ).use { rs ->
        while(rs?.moveToNext() == true){
            items.add(Item(
                rs.getLong(rs.getColumnIndex(Item::_id.name)),
                rs.getString(rs.getColumnIndex(Item::name.name)),
                rs.getInt(rs.getColumnIndex(Item::pokedexNumber.name)),
                rs.getInt(rs.getColumnIndex(Item::height.name)),
                rs.getInt(rs.getColumnIndex(Item::weight.name)),
                rs.getString(rs.getColumnIndex(Item::type1.name)),
                rs.getString(rs.getColumnIndex(Item::type2.name)),
                rs.getInt(rs.getColumnIndex(Item::hpStat.name)),
                rs.getInt(rs.getColumnIndex(Item::attackStat.name)),
                rs.getInt(rs.getColumnIndex(Item::defenseStat.name)),
                rs.getInt(rs.getColumnIndex(Item::specialAttackStat.name)),
                rs.getInt(rs.getColumnIndex(Item::specialDefenseStat.name)),
                rs.getInt(rs.getColumnIndex(Item::speedStat.name)),
                rs.getString(rs.getColumnIndex(Item::spriteImagePath.name)),
                rs.getInt(rs.getColumnIndex(Item::read.name)) == 0
            ))
        }
    }
    return items
}