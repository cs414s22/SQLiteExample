package com.example.sqliteexample

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.view.View
import android.widget.Toast
import androidx.appcompat.app.AlertDialog
import kotlinx.android.synthetic.main.activity_main.*

class MainActivity : AppCompatActivity() {

    private val TAG = "MainActivity"

    //To access your database, instantiate your subclass of SQLiteOpenHelper
    private val dbHelper = ContactDbHelper(this)


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
    }


    fun addButton(view: View) {

        if (text_name.text.isEmpty() || text_email.text.isEmpty()){
            showToast("Please enter name and email")
            return
        }
        try {
            dbHelper.insertData(text_name.text.toString(), text_email.text.toString())
            clearEditTexts()
            showToast("Successfully added a record")
        } catch (e: Exception) {
            Log.e(TAG, "error: $e")
        }
    }


    fun viewAllDataButton(view: View) {
        try {
            val cursor = dbHelper.viewAllData
            if (cursor.count == 0) {
                showDialog("Error", "No record has been found")
                return
            }

            val buffer = StringBuffer()
            while (cursor.moveToNext()) {
                buffer.append("ID :" + cursor.getInt(0) + "\n")
                buffer.append("NAME :" + cursor.getString(1) + "\n")
                buffer.append("EMAIL :" + cursor.getString(2) + "\n\n")
            }
            showDialog("Data Listing", buffer.toString())
        } catch (e: Exception) {
            Log.e(TAG, "error: $e")
        }
    }

    fun deleteButton(view: View) {

        if (text_id.text.isEmpty()){
            showToast("An ID must be entered!")
            return
        }
        try {
            dbHelper.deleteData(text_id.text.toString())
            clearEditTexts()
            showToast("Record has been deleted.")
        } catch (e: Exception){
            e.printStackTrace()
            Log.e(TAG, "error: $e")
            showToast(e.message.toString())
        }
    }

    fun updateButton(view: View) {

        if (text_id.text.isEmpty()){
            showToast("An ID must be entered!")
            return
        }

        try {
            val isUpdated = dbHelper.updateData(
                text_id.text.toString(),
                text_name.text.toString(),
                text_email.text.toString()
            )

            if (isUpdated) {
                showToast("Record Updated Successfully")
                clearEditTexts()
            } else {
                showToast("Record Not Updated")
            }

        } catch (e: Exception){
            e.printStackTrace()
            Log.e(TAG, "error: $e")
            showToast(e.message.toString())
        }
    }


    /**
     * A helper function to show Toast message
     */
    private fun showToast(text: String){
        Toast.makeText(this, text, Toast.LENGTH_LONG).show()
    }

    /**
     * show an alert dialog with data dialog.
     */
    private fun showDialog(title : String,Message : String){
        val builder = AlertDialog.Builder(this)
        builder.setCancelable(true)
        builder.setTitle(title)
        builder.setMessage(Message)
        builder.show()
    }


    /**
     * A helper function to clear our editTexts
     */
    private fun clearEditTexts(){
        text_email.text.clear()
        text_id.text.clear()
        text_name.text.clear()
    }


    /**
     * Since getWritableDatabase() and getReadableDatabase() are expensive to call when the database
     * is closed, you should leave your database connection open for as long as you possibly need to access it.
     * Typically, it is optimal to close the database in the onDestroy() of the calling Activity.
     */
    override fun onDestroy() {
        dbHelper.close()
        super.onDestroy()
    }
}
