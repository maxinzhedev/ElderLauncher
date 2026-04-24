package com.example.elderlauncher.model

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "contacts")
data class Contact(
    @PrimaryKey(autoGenerate = true) val id: Long = 0,
    val name: String,
    val wechatName: String,
    val colorHex: String = "#CCCCCC"
)
