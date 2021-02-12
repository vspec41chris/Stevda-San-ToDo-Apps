package com.example.todo_apps_chris.fragments

import android.app.Application
import android.view.View
import android.widget.AdapterView
import android.widget.TextView
import androidx.core.content.ContextCompat
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.MutableLiveData
import com.example.todo_apps_chris.R
import com.example.todo_apps_chris.data.models.Priority
import com.example.todo_apps_chris.data.models.ToDoData

class SharedViewModel(application: Application): AndroidViewModel(application) {

    /** ============================= List Fragment ============================= */

    // for empty data icon
    val emptyDatabase: MutableLiveData<Boolean> = MutableLiveData(false)

    fun checkIfDatabaseEmpty(toDoData: List<ToDoData>){
        emptyDatabase.value = toDoData.isEmpty()
    }

    /** ============================= Add/Update Fragment ======================== */
    
    // For change spinner color
    val listener: AdapterView.OnItemSelectedListener = object :
        AdapterView.OnItemSelectedListener {

        override fun onNothingSelected(parent: AdapterView<*>?) {}

        override fun onItemSelected(parent: AdapterView<*>?, view: View?, position: Int, id: Long) {
            when(position) {
                0 -> { (parent?.getChildAt(0) as TextView).setTextColor(ContextCompat.getColor(application, R.color.red)) }
                1 -> { (parent?.getChildAt(0) as TextView).setTextColor(ContextCompat.getColor(application, R.color.yellow)) }
                2 -> { (parent?.getChildAt(0) as TextView).setTextColor(ContextCompat.getColor(application, R.color.green)) }
            }
        }
    }

    // Verify Data before inserting to DB
    fun verifyDataFromUser(title: String, description: String): Boolean {
        // Do not return if title & description is empty
        return !(title.isEmpty() || description.isEmpty())
    }

    // for converting priority object
    fun parsePriority(priority: String): Priority {
        return when (priority) {
            "High Priority" -> { Priority.HIGH }
            "Medium Priority" -> { Priority.MEDIUM }
            "Low Priority" -> { Priority.LOW }
            else -> Priority.LOW
        }
    }
}