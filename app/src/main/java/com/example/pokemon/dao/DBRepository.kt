package com.example.pokemon.dao

import android.content.ContentValues
import android.content.Context
import android.database.Cursor
import android.database.sqlite.SQLiteDatabase
import android.database.sqlite.SQLiteOpenHelper
import com.example.pokemon.model.Item
import com.google.android.material.tabs.TabLayout

private const val DB_NAME = "items.db"
private const val DB_VERSION = 1
private const val TABLE_NAME = "items"

private val CREATE_TABLE = "create table $TABLE_NAME(" +
        "${Item::_id.name} integer primary key autoincrement, " +
        "${Item::name.name} text not null, " +
        "${Item::pokedexNumber.name} integer not null, " +
        "${Item::height.name} integer not null, " +
        "${Item::weight.name} integer not null, " +
        "${Item::type1.name} text not null, " +
        "${Item::type2.name} text not null, " +
        "${Item::hpStat.name} integer not null, " +
        "${Item::attackStat.name} integer not null, " +
        "${Item::defenseStat.name} integer not null, " +
        "${Item::specialAttackStat.name} integer not null, " +
        "${Item::specialDefenseStat.name} integer not null, " +
        "${Item::speedStat.name} integer not null, " +
        "${Item::spriteImagePath.name} text not null" +
        ")"

private const val DROP_TABLE = "drop table if exists $TABLE_NAME"
class DBRepository(context: Context?) :
    SQLiteOpenHelper(context, DB_NAME, null, DB_VERSION),
    Repository {
    override fun delete(
        selection: String?,
        selectionArgs: Array<String>?
    ): Int = writableDatabase.delete(
        TABLE_NAME,
        selection,
        selectionArgs
    )

    override fun update(
        values: ContentValues?,
        selection: String?,
        selectionArgs: Array<String>?
    ): Int = writableDatabase.update(
        TABLE_NAME,
        values,
        selection,
        selectionArgs
    )

    override fun query(
        projection: Array<String>?,
        selection: String?,
        selectionArgs: Array<String>?,
        sortOrder: String?
    ): Cursor = readableDatabase.query(
        TABLE_NAME,
        projection,
        selection,
        selectionArgs,
        null,
        null,
        sortOrder
    )

    override fun insert(values: ContentValues?): Long = writableDatabase.insert(
        TABLE_NAME,
        null,
        values
    )

    override fun onCreate(db: SQLiteDatabase?) {
        db?.execSQL(CREATE_TABLE)
    }

    override fun onUpgrade(
        db: SQLiteDatabase?,
        oldVersion: Int,
        newVersion: Int
    ) {
        db?.execSQL(DROP_TABLE)
        onCreate(db)
    }
}