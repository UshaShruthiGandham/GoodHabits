package com.shruti.habit.db

import android.content.Context
import android.database.sqlite.SQLiteDatabase
import android.database.sqlite.SQLiteOpenHelper

class GoodHabitsDb(context: Context) : SQLiteOpenHelper(
    context, DATABASE_NAME, null, DATABASE_VERSION
) {
    private val SQL_CREATE_QUERY = "CREATE TABLE ${HabitEntry.TABLE_NAME} (" +
            "${HabitEntry._ID} INTEGER PRIMARY KEY," +
            "${HabitEntry.NAME_COL} TEXT," +
            "${HabitEntry.DESC_COL} TEXT," +
            "${HabitEntry.IMAGE_COL} BLOB" +
            ")"
    private val SQL_DELETE_QUERY = "DROP TABLE IF EXISTS ${HabitEntry.TABLE_NAME}"

    override fun onCreate(db: SQLiteDatabase) {
        db.execSQL(SQL_CREATE_QUERY)
    }

    override fun onUpgrade(db: SQLiteDatabase, p1: Int, p2: Int) {
        db.execSQL(SQL_DELETE_QUERY)
        onCreate(db)
    }
}