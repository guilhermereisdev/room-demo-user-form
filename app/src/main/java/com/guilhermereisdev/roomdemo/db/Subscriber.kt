package com.guilhermereisdev.roomdemo.db

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "subscriber_data_table")
data class Subscriber(

    @PrimaryKey(autoGenerate = true)
    @ColumnInfo(name = "subscriber_id")
    val id: String,

    @ColumnInfo(name = "subscriber_name")
    val name: String,

    @ColumnInfo(name = "subscriber_email")
    val email: String
)
