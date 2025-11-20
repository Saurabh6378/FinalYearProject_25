package com.example.moterhealthprediction

import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Home
import androidx.compose.material.icons.filled.Info
import androidx.compose.material.icons.filled.Notifications
import androidx.compose.material.icons.filled.Person
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.graphics.Color
import androidx.navigation.NavHostController
import androidx.navigation.compose.currentBackStackEntryAsState


@Composable
fun BottomBar(navController: NavHostController) {
    val navBackStackEntry by navController.currentBackStackEntryAsState()
    val currentRoute = navBackStackEntry?.destination?.route

    NavigationBar(
        containerColor = Color.White,
        contentColor = Color(0xFF1976D2)
    ) {
        NavigationBarItem(
            selected = currentRoute == "dashboard",
            onClick = { navController.navigate("dashboard") },
            icon = { Icon(Icons.Default.Home, contentDescription = "Home", tint = Color(0xFF1976D2)) },
            label = { Text("Home", color = Color(0xFF1976D2)) }
        )



        NavigationBarItem(
            selected = currentRoute == "history",
            onClick = { navController.navigate("history") },
            icon = { Icon(Icons.Default.Notifications, contentDescription = "History", tint = Color(0xFF1976D2)) },
            label = { Text("History", color = Color(0xFF1976D2)) }
        )

        NavigationBarItem(
            selected = currentRoute == "profile",
            onClick = { navController.navigate("profile") },
            icon = { Icon(Icons.Default.Person, contentDescription = "Profile", tint = Color(0xFF1976D2)) },
            label = { Text("Profile", color = Color(0xFF1976D2)) }
        )
        NavigationBarItem(
            selected = currentRoute == "about",
            onClick = { navController.navigate("about") },
            icon = { Icon(Icons.Default.Info, contentDescription = "About", tint = Color(0xFF1976D2)) },
            label = { Text("About", color = Color(0xFF1976D2)) }
        )
    }
}
