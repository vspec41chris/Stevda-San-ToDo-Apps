package com.example.todo_apps_chris.data.models

import android.os.Parcelable
import androidx.room.Entity
import androidx.room.PrimaryKey
import kotlinx.android.parcel.Parcelize

// Entity Table class
@Entity(tableName = "todo_table")
@Parcelize
data class ToDoData(

    // Column in DB table
    @PrimaryKey(autoGenerate = true)
    var id: Int,
    var title: String,
    var priority: Priority,
    var description: String

): Parcelable

