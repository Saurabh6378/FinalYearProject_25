package com.example.moterhealthprediction

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.LinearProgressIndicator
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
//
//@Composable
//fun LiveSensorCard(label: String, value: Double, unit: String, max: Int) {
//    Card(
//        modifier = Modifier
//            .fillMaxWidth()
//            .height(100.dp),
//        colors = CardDefaults.cardColors(containerColor = Color.White)
//    ) {
//        Column(
//            modifier = Modifier
//                .padding(16.dp),
//            verticalArrangement = Arrangement.SpaceBetween,
//            horizontalAlignment = Alignment.Start
//        ) {
//            Text(label, fontSize = 16.sp, color = Color.Gray)
//            Box(
//                modifier = Modifier
//                    .fillMaxWidth()
//                    .height(8.dp)
//                    .background(Color(0xFFBBDEFB)) // Light background for progress
//            ) {
//                LinearProgressIndicator(
//                    modifier = Modifier
//                        .fillMaxWidth(fraction = (value / max).toFloat().coerceIn(0f, 1f))
//                        .height(8.dp),
//                    color = Color(0xFF2196F3),
//                    trackColor = Color.Transparent
//                )
//            }
//            Text("${"%.1f".format(value)} $unit", fontSize = 22.sp, color = Color.Black, fontWeight = FontWeight.Bold)
//        }
//    }
//}
