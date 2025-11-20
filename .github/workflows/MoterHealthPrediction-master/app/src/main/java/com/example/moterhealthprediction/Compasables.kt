package com.example.moterhealthprediction

import android.content.Intent
import android.net.Uri
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextDecoration
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavHostController
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore

// --- ABOUT SCREEN ---
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AboutScreen() {
    val context = LocalContext.current
    val footerText = stringResource(id = R.string.footer_text)
    val linkedInUrl = stringResource(id = R.string.linkedin_url)

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("About", color = Color.White) },
                colors = TopAppBarDefaults.mediumTopAppBarColors(Color(0xFF1976D2))
            )
        }
    ) { padding ->
        Column(
            modifier = Modifier
                .padding(padding)
                .fillMaxSize()
                .background(Color(0xFFF5F5F5))
                .padding(20.dp)
                .verticalScroll(rememberScrollState()),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Spacer(modifier = Modifier.height(50.dp))
            Text(
                text = "Motor Health Prediction",
                fontSize = 26.sp,
                color = Color(0xFF1976D2)
            )

            Spacer(modifier = Modifier.height(12.dp))

            Text(
                text = "This app helps you monitor motor temperature, voltage, and water level efficiently.",
                fontSize = 16.sp,
                color = Color.Black
            )

            Spacer(modifier = Modifier.height(24.dp))

            Text(
                text = "Steps to Setup:",
                fontSize = 20.sp,
                color = Color(0xFF1976D2)
            )

            Spacer(modifier = Modifier.height(34.dp))

            Text(
                text = """
                    1. Install all sensors (temperature, voltage, water level) properly on your Motor Machine.
                    2. Connect the sensors with your hardware tool (Motor Monitoring Device).
                    3. Power up your motor monitoring hardware.
                    4. Ensure the device is transmitting data correctly.
                    5. Open the app, log in, and view your motor health data in real time!
                """.trimIndent(),
                fontSize = 15.sp,
                color = Color.Black
            )

            Spacer(modifier = Modifier.height(40.dp))

            Text(
                text = footerText,
                textDecoration = TextDecoration.Underline,
                color = Color.Gray,
                fontSize = 9.sp,
                modifier = Modifier.clickable {
                    val intent = Intent(Intent.ACTION_VIEW, Uri.parse(linkedInUrl))
                    context.startActivity(intent)
                }
            )
        }
    }
}

// --- NOTIFICATION HISTORY SCREEN ---
data class NotificationItem(val title: String, val description: String, val time: String)

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun HistoryScreen(navController: NavHostController) {
    val db = FirebaseFirestore.getInstance()
    var notifications by remember { mutableStateOf<List<NotificationItem>>(emptyList()) }
    var loading by remember { mutableStateOf(true) }
    var error by remember { mutableStateOf<String?>(null) }

    LaunchedEffect(Unit) {
        db.collection("Alerts")
            .orderBy("timestamp") // optional if you store timestamp
            .addSnapshotListener { snapshot, e ->
                if (e != null) {
                    error = e.message
                    loading = false
                    return@addSnapshotListener
                }
                if (snapshot != null) {
                    notifications = snapshot.documents.mapNotNull { doc ->
                        val title = doc.getString("title")
                        val description = doc.getString("description")
                        val time = doc.getString("time")
                        if (title != null && description != null && time != null) {
                            NotificationItem(title, description, time)
                        } else null
                    }
                    loading = false
                }
            }
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Notification History", color = Color.White) },
                colors = TopAppBarDefaults.mediumTopAppBarColors(Color(0xFF1976D2))
            )
        }
    ) { padding ->
        Column(
            modifier = Modifier
                .padding(padding)
                .fillMaxSize()
                .background(Color(0xFFF5F5F5))
                .padding(16.dp)
        ) {
            when {
                loading -> {
                    Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                        CircularProgressIndicator(color = Color(0xFF1976D2))
                    }
                }
                error != null -> {
                    Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                        Text("Error loading notifications: $error", color = Color.Red)
                    }
                }
                notifications.isEmpty() -> {
                    Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                        Text("No notifications available.", color = Color.Gray)
                    }
                }
                else -> {
                    LazyColumn(verticalArrangement = Arrangement.spacedBy(12.dp)) {
                        items(notifications) { notification ->
                            NotificationCard(notification)
                        }
                    }
                }
            }
        }
    }
}


@Composable
fun NotificationCard(notification: NotificationItem) {
    Card(
        shape = RoundedCornerShape(16.dp),
        colors = CardDefaults.cardColors(containerColor = Color.White),
        modifier = Modifier.fillMaxWidth()
    ) {
        Column(modifier = Modifier.padding(16.dp)) {
            Text(notification.title, fontSize = 18.sp, color = Color(0xFF1976D2))
            Spacer(modifier = Modifier.height(4.dp))
            Text(notification.description, fontSize = 14.sp, color = Color.Black)
            Spacer(modifier = Modifier.height(8.dp))
            Text(notification.time, fontSize = 12.sp, color = Color(0xFF888888))
        }
    }
}

// --- PROFILE SCREEN ---
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ProfileScreen(navController: NavHostController) {
    val auth = FirebaseAuth.getInstance()
    val currentUser = auth.currentUser
    val name = currentUser?.displayName ?: "User"
    val email = currentUser?.email ?: "No email"

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Profile", color = Color.White) },
                colors = TopAppBarDefaults.mediumTopAppBarColors(Color(0xFF1976D2))
            )
        }
    ) { padding ->
        Column(
            modifier = Modifier
                .padding(padding)
                .fillMaxSize()
                .background(Color(0xFFF5F5F5))
                .padding(16.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Top
        ) {
            Spacer(modifier = Modifier.height(40.dp))

            Surface(
                modifier = Modifier
                    .size(120.dp)
                    .clip(CircleShape),
                color = Color(0xFF1976D2)
            ) {
                Box(contentAlignment = Alignment.Center) {
                    Text(
                        text = name.firstOrNull()?.uppercase() ?: "U",
                        fontSize = 48.sp,
                        color = Color.White,
                        fontWeight = FontWeight.Bold
                    )
                }
            }

            Spacer(modifier = Modifier.height(20.dp))

            Text(name, fontSize = 24.sp, color = Color(0xFF333333), fontWeight = FontWeight.Bold)
            Spacer(modifier = Modifier.height(4.dp))
            Text(email, fontSize = 14.sp, color = Color(0xFF666666))

            Spacer(modifier = Modifier.height(40.dp))

            Card(
                shape = RoundedCornerShape(16.dp),
                colors = CardDefaults.cardColors(containerColor = Color.White),
                modifier = Modifier.fillMaxWidth()
            ) {
                Column(modifier = Modifier.padding(20.dp)) {
                    Text("App Version", fontSize = 18.sp, color = Color(0xFF1976D2))
                    Text("v1.0.0", fontSize = 14.sp, color = Color.Black)
                }
            }

            Spacer(modifier = Modifier.height(40.dp))

            Button(
                onClick = {
                    auth.signOut()
                    navController.navigate("auth") {
                        popUpTo(0) { inclusive = true }
                        launchSingleTop = true
                    }
                },
                colors = ButtonDefaults.buttonColors(containerColor = Color(0xFFD32F2F)),
                modifier = Modifier.fillMaxWidth()
            ) {
                Text("Logout", color = Color.White, fontSize = 16.sp)
            }
        }
    }
}
