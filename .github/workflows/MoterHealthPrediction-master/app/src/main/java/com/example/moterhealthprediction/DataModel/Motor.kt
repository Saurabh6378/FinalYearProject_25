package com.example.moterhealthprediction.DataModel

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "motor")
data class Motor(
    @PrimaryKey(autoGenerate = false) val id: String,
    val name: String,
    val power: String,
    val phase: String
)