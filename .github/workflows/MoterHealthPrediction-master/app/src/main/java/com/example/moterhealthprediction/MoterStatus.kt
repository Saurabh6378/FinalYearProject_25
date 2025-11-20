package com.example.moterhealthprediction

import android.annotation.SuppressLint
import android.app.NotificationChannel
import android.app.NotificationManager
import android.content.Context
import android.os.Build
import android.widget.Toast
import androidx.compose.animation.core.*
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Refresh
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.rotate
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.core.app.NotificationCompat
import androidx.core.app.NotificationManagerCompat
import androidx.navigation.NavHostController
import com.google.firebase.database.*
import com.google.firebase.messaging.FirebaseMessaging
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import retrofit2.http.Body
import retrofit2.http.POST

// Data classes
data class PredictionRequest(val sensor_data: List<List<Double>>)
data class PredictionResponse(val predictions: List<String>)

// Retrofit
interface FlaskApi {
    @POST("predict")
    suspend fun getPrediction(@Body request: PredictionRequest): PredictionResponse
}

val retrofitFlask = Retrofit.Builder()
    .baseUrl("http://10.0.2.2:5000/")
    .addConverterFactory(GsonConverterFactory.create())
    .build()
    .create(FlaskApi::class.java)

// Notifications
@SuppressLint("MissingPermission")
fun showNotification(context: Context, title: String, message: String) {
    val channelId = "alerts_channel"
    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
        val channel = NotificationChannel(channelId, "Motor Alerts", NotificationManager.IMPORTANCE_HIGH)
        val notificationManager = context.getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
        notificationManager.createNotificationChannel(channel)
    }
    val notification = NotificationCompat.Builder(context, channelId)
        .setSmallIcon(android.R.drawable.ic_dialog_alert)
        .setContentTitle(title)
        .setContentText(message)
        .setPriority(NotificationCompat.PRIORITY_HIGH)
        .build()
    NotificationManagerCompat.from(context).notify(System.currentTimeMillis().toInt(), notification)
}

// Prediction Logic
suspend fun predictMotorHealth(
    waterLevel: Double,
    prevWaterLevel: Double,
    temperature: Double,
    voltage: Double,
    motorRunning: Boolean,
    onResult: (String) -> Unit,
    onError: (String) -> Unit,
    onAlert: (String) -> Unit
) {
    try {
        val waterLevelChange = waterLevel - prevWaterLevel
        val motorStatus = if (motorRunning) 1.0 else 0.0
        val request = PredictionRequest(sensor_data = listOf(listOf(voltage, temperature, waterLevelChange, motorStatus)))
        val result = retrofitFlask.getPrediction(request)
        val predictionLabel = result.predictions.firstOrNull()
        val mapped = when (predictionLabel) {
            "Normal" -> "Healthy"
            "Overheat" -> "Overheating"
            "Faulty" -> "Faulty"
            else -> "Unknown"
        }
        onResult(mapped)

        if (mapped != "Healthy") {
            onAlert("Motor status: $mapped")
        }

        if (temperature > 110) {
            onAlert("High Temperature: $temperature°C")
        } else if (waterLevel > 90) {
            onAlert("Water Tank Almost Full: $waterLevel%")
        }

    } catch (e: Exception) {
        onError("Prediction failed: ${e.localizedMessage}")
    }
}

@Composable
fun LiveSensorCard(label: String, value: Double, unit: String, max: Int) {
    val progress = (value / max).coerceIn(0.0, 1.0)
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .height(100.dp),
        colors = CardDefaults.cardColors(containerColor = Color.White),
        shape = RoundedCornerShape(16.dp)
    ) {
        Column(modifier = Modifier.padding(16.dp), verticalArrangement = Arrangement.SpaceEvenly) {
            Text(label, fontSize = 16.sp)
            LinearProgressIndicator(progress = progress.toFloat(), modifier = Modifier.fillMaxWidth())
            Text("${String.format("%.1f", value)} $unit", fontSize = 14.sp)
        }
    }
}

@Composable
fun ShimmerSensorCard() {
    val shimmerAlpha = rememberInfiniteTransition().animateFloat(
        initialValue = 0.3f,
        targetValue = 1f,
        animationSpec = infiniteRepeatable(
            animation = tween(1000, easing = LinearEasing),
            repeatMode = RepeatMode.Reverse
        )
    )
    Box(
        modifier = Modifier
            .fillMaxWidth()
            .height(100.dp)
            .background(Color.LightGray.copy(alpha = shimmerAlpha.value), shape = RoundedCornerShape(16.dp))
    )
}

@SuppressLint("UnusedMaterial3ScaffoldPaddingParameter")
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun MotorStatusScreen(
    navController: NavHostController,
    motorName: String,
    motorHP: String
) {
    val context = LocalContext.current
    val scope = rememberCoroutineScope()

    var waterLevel by remember { mutableStateOf(0.0) }
    var prevWaterLevel by remember { mutableStateOf(0.0) }
    var temperature by remember { mutableStateOf(0.0) }
    var voltage by remember { mutableStateOf(0.0) }
    var prediction by remember { mutableStateOf("Predicting...") }
    var isLoading by remember { mutableStateOf(true) }
    var motorRunning by remember { mutableStateOf(false) }
    var lastWaterUpdateTime by remember { mutableStateOf(System.currentTimeMillis()) }

    val dbRef = FirebaseDatabase.getInstance().getReference("SensorData")
    val alertRef = FirebaseDatabase.getInstance().getReference("Alerts")
    val motorStatusRef = FirebaseDatabase.getInstance().getReference("MotorStatus")

    LaunchedEffect(Unit) {
        FirebaseMessaging.getInstance().subscribeToTopic("motor")
    }

    fun sendAlert(message: String) {
        alertRef.push().setValue(message)
        showNotification(context, "Motor Alert", message)
        Toast.makeText(context, message, Toast.LENGTH_SHORT).show()
    }

    fun updateMotorStatus(isRunning: Boolean) {
        motorStatusRef.setValue(if (isRunning) "ON" else "OFF")
        Toast.makeText(context, if (isRunning) "Motor Turned ON" else "Motor Turned OFF", Toast.LENGTH_SHORT).show()
    }

    LaunchedEffect(Unit) {
        dbRef.addValueEventListener(object : ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {
                val newWaterLevel = snapshot.child("WaterLevel").getValue(Double::class.java) ?: 0.0

                if (newWaterLevel != waterLevel) {
                    prevWaterLevel = waterLevel
                    waterLevel = newWaterLevel
                    lastWaterUpdateTime = System.currentTimeMillis()
                }

                temperature = snapshot.child("Temperature").getValue(Double::class.java) ?: 0.0
                voltage = snapshot.child("Voltage").getValue(Double::class.java) ?: 0.0
                isLoading = false

                if (motorRunning && (temperature > 110 || waterLevel >= 90)) {
                    motorRunning = false
                    updateMotorStatus(false)
                    sendAlert("Motor stopped automatically due to safety threshold exceeded")
                }
            }

            override fun onCancelled(error: DatabaseError) {}
        })
    }

    LaunchedEffect(true) {
        while (true) {
            delay(1000)
            if (System.currentTimeMillis() - lastWaterUpdateTime > 1000) {
                prevWaterLevel = waterLevel // No new update in the last second
            }
        }
    }

    LaunchedEffect(motorRunning) {
        while (motorRunning) {
            scope.launch {
                predictMotorHealth(
                    waterLevel, prevWaterLevel, temperature, voltage, motorRunning,
                    onResult = { prediction = it },
                    onError = { prediction = it },
                    onAlert = { sendAlert(it) }
                )
            }
            delay(5000)
        }
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Motor Health Monitor", color = Color.White) },
                colors = TopAppBarDefaults.mediumTopAppBarColors(containerColor = Color(0xFF1976D2)),
                actions = {
                    IconButton(onClick = {
                        scope.launch {
                            predictMotorHealth(
                                waterLevel, prevWaterLevel, temperature, voltage, motorRunning,
                                onResult = { prediction = it },
                                onError = { prediction = it },
                                onAlert = { sendAlert(it) }
                            )
                        }
                    }) {
                        Icon(Icons.Default.Refresh, contentDescription = "Refresh Prediction", tint = Color.White)
                    }
                }
            )
        }
    ) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .background(Color(0xFFE3F2FD))
                .padding(16.dp)
                .padding(top = 80.dp),
            verticalArrangement = Arrangement.spacedBy(20.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Text("Motor: $motorName (${motorHP} HP)", fontSize = 22.sp, color = Color(0xFF0D47A1))

            if (isLoading) {
                repeat(3) { ShimmerSensorCard() }
            } else {
                LiveSensorCard("Water Level", waterLevel, "%", 100)
                LiveSensorCard("Temperature", temperature, "°C", 150)
                LiveSensorCard("Voltage", voltage, "V", 50)
            }

            Card(
                modifier = Modifier.fillMaxWidth(),
                colors = CardDefaults.cardColors(containerColor = Color(0xFFD1C4E9)),
                shape = RoundedCornerShape(12.dp)
            ) {
                Text(
                    text = "Prediction: $prediction",
                    fontSize = 18.sp,
                    color = Color(0xFF4A148C),
                    modifier = Modifier.padding(16.dp)
                )
            }

            Spacer(modifier = Modifier.height(10.dp))

            Image(
                painter = painterResource(id = if (motorRunning) R.drawable.red else R.drawable.green),
                contentDescription = "Motor Button",
                modifier = Modifier
                    .size(80.dp)
                    .clickable {
                        if (!motorRunning && (temperature > 90 || waterLevel > 85)) {
                            sendAlert("Cannot start motor due to safety conditions")
                        } else {
                            motorRunning = !motorRunning
                            updateMotorStatus(motorRunning)
                        }
                    }
            )
        }
    }
}