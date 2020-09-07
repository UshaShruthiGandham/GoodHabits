package com.shruti.habit.db

import android.content.ContentValues
import android.content.Context
import android.database.Cursor
import android.database.sqlite.SQLiteDatabase
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.util.Log
import com.shruti.habit.Habits
import com.shruti.habit.db.HabitEntry.DESC_COL
import com.shruti.habit.db.HabitEntry.IMAGE_COL
import com.shruti.habit.db.HabitEntry.NAME_COL
import com.shruti.habit.db.HabitEntry.TABLE_NAME
import com.shruti.habit.db.HabitEntry._ID
import java.io.ByteArrayOutputStream

class HabitDbTable(context: Context) {

    private val TAG = HabitDbTable::class.java.simpleName

    private val dbHelper = GoodHabitsDb(context)

    fun storeToDb(habits: Habits): Long {
        val db = dbHelper.writableDatabase
        val values = ContentValues()

        with(values) {
            put(NAME_COL, habits.name)
            put(DESC_COL, habits.des)
            put(IMAGE_COL, toByteArray(habits.image))
        }

        // a lot of code to just insert the values into DB, this can be avoided using higher order functions
        /* db.beginTransaction()
         val id = try {
             val returnValue = db.insert(HabitEntry.TABLE_NAME, null,values)
             db.setTransactionSuccessful()
             returnValue
         } finally {
             db.endTransaction()
         }
          db.close()*/
        val id = db.transaction {
            insert(TABLE_NAME, null, values)
        }

        Log.d(TAG, "Stored new habit to DB $habits")

        return id
    }

    fun readHabits(): List<Habits> {
        val columns = arrayOf(
            _ID, NAME_COL,
            DESC_COL, IMAGE_COL
        )
        val order = "${_ID} ASC"
        val db = dbHelper.readableDatabase

        // cursor helps in going through all the items in the db and read them one by one
        val cursor = db.doQuery(TABLE_NAME, columns, orderBy = order)

        return  parseHabitsFrom(cursor)
    }

    private fun parseHabitsFrom(cursor: Cursor): MutableList<Habits> {
        val habits = mutableListOf<Habits>()
        while (cursor.moveToNext()) {
            val name = cursor.getColString(NAME_COL)
            val desc = cursor.getColString(DESC_COL)
            val bitmap = cursor.getColBlob(IMAGE_COL)
            habits.add(Habits(name, desc, bitmap))
        }
        cursor.close()
        return habits
    }

    private fun toByteArray(bitmap: Bitmap): ByteArray {

        val stream = ByteArrayOutputStream()
        bitmap.compress(Bitmap.CompressFormat.PNG, 0, stream)
        return stream.toByteArray()

    }
}

private fun SQLiteDatabase.doQuery(
    table: String, columns : Array<String>,
    selection: String? = null, selectionArgs: Array<String>? = null,
    groupBy: String? = null, having: String? = null, orderBy: String? = null
): Cursor {
    return query(table,columns,selection,selectionArgs, groupBy, having, orderBy)
}

private fun Cursor.getColString(columnName: String):String = getString(getColumnIndex(columnName))

private fun Cursor.getColBlob(columnName: String):Bitmap {
    val byteArray = getBlob(getColumnIndex(columnName))
    return BitmapFactory.decodeByteArray(byteArray, 0, byteArray.size)
}


// create extension higher-order function on SQLiteDatabase , SQLiteDatabase.() is an extension function on SQLiteDatabase
// Since we don't know what this function can return we make it generic by adding <T>
// in many cases T might be Unit(i.e void)
// when we have a higher order fun which takes another function , it is advised to inline that function
// means whenever the transaction func is called this block of code in the fun be copied to the call side i.e only body is
// taken directly at the compile time , i.e its going to inline the statements to the variable instead of creating a
// anonymous class for this higher order function , i.e it avoids the object creations
private inline fun <T> SQLiteDatabase.transaction(function: SQLiteDatabase.() -> T): T {
    beginTransaction()
    val result = try {
        val returnValue = function()
        setTransactionSuccessful()
        returnValue
    } finally {
        endTransaction()
    }
    close()
    return result
}
