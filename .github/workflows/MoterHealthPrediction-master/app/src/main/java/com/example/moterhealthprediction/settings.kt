package com.example.moterhealthprediction

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Call
import androidx.compose.material.icons.filled.ExitToApp
//import androidx.compose.material.icons.filled.Help
import androidx.compose.material.icons.filled.Info
import androidx.compose.material.icons.filled.Lock
import androidx.compose.material.icons.filled.Notifications
//import androidx.compose.material.icons.filled.PrivacyTip
import androidx.compose.material.icons.filled.Settings
import androidx.compose.material.icons.filled.Warning
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavHostController
import com.google.firebase.auth.FirebaseAuth

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun SettingsScreen(navController: NavHostController) {
    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Settings", color = Color.White) },
                colors = TopAppBarDefaults.mediumTopAppBarColors(Color(0xFF1976D2))
            )
        },
        containerColor = Color(0xFFF7F9FC)
    ) { padding ->
        Column(
            modifier = Modifier
                .padding(padding)
                .padding(16.dp)
                .fillMaxSize(),
            verticalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            SettingsItem(
                title = "Notifications",
                subtitle = "Manage notification settings",
                icon = Icons.Default.Notifications,
                onClick = { /* navigate or open notification settings */ }
            )

            SettingsItem(
                title = "Privacy Policy",
                subtitle = "Read our privacy policy",
                icon = Icons.Default.Warning,
                onClick = { /* open privacy screen or link */ }
            )

            SettingsItem(
                title = "Terms of Service",
                subtitle = "View the terms and conditions",
                icon = Icons.Default.Lock,
                onClick = { /* open terms screen or link */ }
            )

            SettingsItem(
                title = "Help & Support",
                subtitle = "Get assistance",
                icon = Icons.Default.Call,
                onClick = { /* open help screen */ }
            )

            SettingsItem(
                title = "About",
                subtitle = "Learn more about the app",
                icon = Icons.Default.Info,
                onClick = { navController.navigate("about") }
            )
            SettingsItem(title = "Logout", subtitle ="Logout from your account" , icon =Icons.Default.ExitToApp , onClick = {
                val auth = FirebaseAuth.getInstance()
                auth.signOut()

                navController.navigate("auth") {
                    popUpTo(0) { inclusive = true } // clear all backstack
                    launchSingleTop = true
                }
            })

        }
    }
}

@Composable
fun SettingsItem(
    title: String,
    subtitle: String,
    icon: androidx.compose.ui.graphics.vector.ImageVector,
    onClick: () -> Unit
) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .clickable { onClick() },
        colors = CardDefaults.cardColors(containerColor = Color.White),
        shape = RoundedCornerShape(16.dp),
        elevation = CardDefaults.cardElevation(4.dp)
    ) {
        Row(
            modifier = Modifier
                .padding(16.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Icon(
                imageVector = icon,
                contentDescription = title,
                tint = Color(0xFF1976D2),
                modifier = Modifier.size(32.dp)
            )
            Spacer(modifier = Modifier.width(16.dp))
            Column {
                Text(text = title, fontSize = 18.sp, color = Color.Black)
                Text(text = subtitle, fontSize = 14.sp, color = Color.Gray)
            }
        }
    }
}
