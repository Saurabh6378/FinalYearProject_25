package com.example.motorhealthprediction

import android.annotation.SuppressLint
import android.os.Handler
import android.os.Looper
import android.widget.Toast
import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavHostController
import com.example.moterhealthprediction.R
import com.google.firebase.database.*
import com.google.firebase.database.ktx.getValue

@SuppressLint("UnusedMaterial3ScaffoldPaddingParameter")
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun MotorListScreen(navController: NavHostController) {
    val context = LocalContext.current
    val motors = remember { mutableStateListOf<Motor>() }
    var isLoading by remember { mutableStateOf(true) }
    var isNetworkSlow by remember { mutableStateOf(false) }

    val dbRef = FirebaseDatabase.getInstance().getReference("motors")

    DisposableEffect(Unit) {
        val handler = Handler(Looper.getMainLooper())
        val slowNetworkRunnable = Runnable {
            if (isLoading) {
                isNetworkSlow = true
                Toast.makeText(context, "Network seems slow, please wait...", Toast.LENGTH_LONG).show()
            }
        }

        // Post a delayed runnable to detect slow network (after 4 seconds)
        handler.postDelayed(slowNetworkRunnable, 4000)

        val listener = object : ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {
                motors.clear()
                snapshot.children.mapNotNullTo(motors) { it.getValue<Motor>() }
                isLoading = false
                handler.removeCallbacks(slowNetworkRunnable) // Data loaded, cancel the slow network toast
            }

            override fun onCancelled(error: DatabaseError) {
                Toast.makeText(context, "Failed to load motors", Toast.LENGTH_SHORT).show()
                isLoading = false
                handler.removeCallbacks(slowNetworkRunnable)
            }
        }
        dbRef.addValueEventListener(listener)

        onDispose {
            dbRef.removeEventListener(listener)
            handler.removeCallbacks(slowNetworkRunnable)
        }
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("My Motors", color = Color.White, fontWeight = FontWeight.Bold) },
                colors = TopAppBarDefaults.mediumTopAppBarColors(Color(0xFF1976D2))
            )
        },
        containerColor = Color(0xFFF7F9FC)
    ) { padding ->
        if (isLoading) {
            Box(
                modifier = Modifier.fillMaxSize(),
                contentAlignment = Alignment.Center
            ) {
                CircularProgressIndicator(color = Color(0xFF1976D2))
            }
        } else {
            LazyColumn(
                modifier = Modifier
                    .padding(padding)
                    .fillMaxSize()
                    .padding(horizontal = 16.dp) // Horizontal padding
                    .padding(bottom = 72.dp),    // Added extra bottom padding
                verticalArrangement = Arrangement.spacedBy(16.dp)
            ) {
                // Statistics Card
                item {
                    Card(
                        modifier = Modifier
                            .fillMaxWidth()
                            .shadow(8.dp, RoundedCornerShape(16.dp)),
                        colors = CardDefaults.cardColors(containerColor = Color.White)
                    ) {
                        Column(
                            modifier = Modifier.padding(20.dp),
                            horizontalAlignment = Alignment.CenterHorizontally
                        ) {
                            Text(
                                text = "Total Motors",
                                fontSize = 20.sp,
                                color = Color(0xFF1976D2),
                                fontWeight = FontWeight.Bold
                            )
                            Text(
                                text = "${motors.size}",
                                fontSize = 40.sp,
                                color = Color.Black,
                                fontWeight = FontWeight.Bold
                            )
                        }
                    }
                    Spacer(modifier = Modifier.height(20.dp))
                }

                // Motors list
                items(motors) { motor ->
                    MotorCard(
                        motor = motor,
                        onDelete = { deleteMotorFromFirebase(motor.id) },
                        navController = navController
                    )
                }
            }
        }
    }
}

@Composable
fun MotorCard(motor: Motor, onDelete: () -> Unit, navController: NavHostController) {
    val randomImage = listOf(
        R.drawable.m1,
        R.drawable.m2,
        R.drawable.m3,
        R.drawable.m4,
        R.drawable.m5
    ).random()

    Card(
        modifier = Modifier
            .fillMaxWidth()
            .shadow(8.dp, RoundedCornerShape(16.dp)),
        colors = CardDefaults.cardColors(containerColor = Color.White),
        onClick = {
            navController.navigate("motor_status/${motor.id}/${motor.name}/${motor.power}")
        }
    ) {
        Row(
            modifier = Modifier
                .padding(16.dp)
                .fillMaxWidth(),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Image(
                painter = painterResource(id = randomImage),
                contentDescription = "Motor Image",
                modifier = Modifier
                    .size(70.dp)
                    .padding(8.dp)
            )

            Spacer(modifier = Modifier.width(16.dp))

            Column(
                modifier = Modifier.weight(1f)
            ) {
                Text(
                    text = "Name: ${motor.name}",
                    fontWeight = FontWeight.Bold,
                    fontSize = 18.sp,
                    color = Color(0xFF1976D2)
                )
                Text(
                    text = "Power: ${motor.power} HP",
                    fontSize = 14.sp,
                    color = Color.Gray
                )
                Text(
                    text = "Phase: ${motor.phase}",
                    fontSize = 14.sp,
                    color = Color.Gray
                )
            }

            IconButton(onClick = onDelete) {
                Icon(Icons.Filled.Delete, contentDescription = "Delete", tint = Color.Red)
            }
        }
    }
}

fun deleteMotorFromFirebase(motorId: String) {
    val dbRef = FirebaseDatabase.getInstance().getReference("motors")
    dbRef.child(motorId).removeValue()
}
