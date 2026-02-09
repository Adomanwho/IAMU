package com.example.pokemon.api

import android.content.Context
import androidx.work.Worker
import androidx.work.WorkerParameters

class PokemonWorker(
    private val context: Context,
    workerParams: WorkerParameters,
) : Worker(context, workerParams) {
    override fun doWork(): Result {
        PokemonFetcher(context).fetchItems()
        return Result.success()
    }
}