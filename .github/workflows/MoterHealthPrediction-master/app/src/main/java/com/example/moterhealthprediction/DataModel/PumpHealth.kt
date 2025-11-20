package com.example.moterhealthprediction.DataModel
data class PumpHealth(
    val temperature: Float,
    val voltage: Float,
    val waterLevel: Float,
    val relayModel: String,
    val tankWaterLevel: Float,
    val lastError: String
)