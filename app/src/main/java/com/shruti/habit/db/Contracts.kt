package com.shruti.habit.db

import android.provider.BaseColumns

val DATABASE_NAME = "goodhabits.db"
val DATABASE_VERSION = 1

// db coloumns
object HabitEntry : BaseColumns{
    val TABLE_NAME = "habit"
    val _ID = "id"
    val NAME_COL = "name"
    val DESC_COL = "desc"
    val IMAGE_COL = "image"
}
