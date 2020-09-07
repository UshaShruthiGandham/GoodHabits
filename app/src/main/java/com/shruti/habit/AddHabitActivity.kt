package com.shruti.habit

import android.app.Activity
import android.content.Intent
import android.graphics.Bitmap
import android.graphics.ImageDecoder
import android.net.Uri
import android.os.Build
import android.os.Bundle
import android.provider.MediaStore
import android.util.Log
import android.view.View
import android.widget.EditText
import androidx.appcompat.app.AppCompatActivity
import com.shruti.habit.db.HabitDbTable
import kotlinx.android.synthetic.main.activity_add_habit.*
import java.io.IOException

class AddHabitActivity : AppCompatActivity() {
    private val TAG = AddHabitActivity::class.java.simpleName

    private val CHOOSE_IMG_REQ = 1

    private var imgBitMap: Bitmap? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_add_habit)
    }

    // android finds the best app which can fulfill this
    fun chooseImage(view: View) {
        val intent = Intent()
        intent.type = "image/*"
        intent.action = Intent.ACTION_GET_CONTENT

        val chooser = Intent.createChooser(intent, "Select an Image")
        startActivityForResult(chooser, CHOOSE_IMG_REQ)

        Log.d(TAG, "Chooser activity is opened")
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)

        if (requestCode == CHOOSE_IMG_REQ && resultCode == Activity.RESULT_OK && data != null && data.data != null) {
            Log.d(TAG, "An image is selected")

            val bitmap = tryReadBitMap(data.data!!)

            bitmap?.let {
                this.imgBitMap = bitmap
                previewImage.setImageBitmap(bitmap)
                Log.d(TAG, "Image is added to the preview")
            }
        }
    }

    private fun tryReadBitMap(data: Uri): Bitmap? {
        return try {
            if (Build.VERSION.SDK_INT > 28) {
                val source = ImageDecoder.createSource(this.contentResolver, data)
                ImageDecoder.decodeBitmap(source)
            } else {
                MediaStore.Images.Media.getBitmap(contentResolver, data)
            }
        } catch (e: IOException) {
            e.printStackTrace()
            null
        }
    }

    fun submitHabit(view: View) {

        if (habitName.isTVBlank() && habitDesc.isTVBlank()) {
            displayErrorMessage("Please fill the Name and Desc")
            Log.d(TAG, "Habit Name and Desc is missing")
            return
        } else if (imgBitMap == null) {
            displayErrorMessage("Please select an image")
            Log.d(TAG, "Image is missing")
            return
        } else {
            errorMsg.visibility = View.GONE
        }

        // Store habit...
        val name = habitName.text.toString()
        val desc = habitDesc.text.toString()
        val habit =  Habits(name,desc,imgBitMap!!)

        val id = HabitDbTable(this).storeToDb(habit)
        if(id== -1L){
            displayErrorMessage("Habit couldn't be stored")
        } else{
            val intent = Intent(this, MainActivity::class.java)
            startActivity(intent)
        }

    }

    private fun displayErrorMessage(s: String) {
        errorMsg.text = s
        errorMsg.visibility = View.VISIBLE
    }
}
private fun EditText.isTVBlank() = this.text.toString().isEmpty()  // file level