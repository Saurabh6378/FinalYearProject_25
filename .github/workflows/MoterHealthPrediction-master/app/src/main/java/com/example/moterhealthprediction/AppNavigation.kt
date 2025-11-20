package com.example.moterhealthprediction

import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.runtime.*
import androidx.compose.ui.platform.LocalContext
import androidx.navigation.compose.*
import androidx.room.Room
import com.example.moterhealthprediction.DataModel.AppDatabase
import com.example.moterhealthprediction.DataModel.Motor
import com.google.firebase.auth.FirebaseAuth
import com.example.motorhealthprediction.*

import androidx.compose.material3.*
import androidx.navigation.NavHostController

@Composable
fun AppNavigation() {
    val navController = rememberNavController()
    val context = LocalContext.current.applicationContext
    val auth = FirebaseAuth.getInstance()
    val startDestination = if (auth.currentUser != null) "dashboard" else "auth"

    // Database setup
    val db = remember {
        Room.databaseBuilder(
            context,
            AppDatabase::class.java,
            "motor-database"
        )
            .fallbackToDestructiveMigration()
            .build()
    }
    val motorDao = db.motorDao()
    val motorRecords = remember { mutableStateListOf<Motor>() }

    val navBackStackEntry by navController.currentBackStackEntryAsState()
    val currentRoute = navBackStackEntry?.destination?.route

    NavHost(
        navController = navController,
        startDestination = startDestination
    ) {
        composable("auth") {
            // 🔥 Direct screen without Scaffold
            AuthScreen(navController)
        }

        composable("dashboard") {
            // 🔥 Now wrap Dashboard and other screens inside Scaffold
            AppScaffold(navController) {
                UserDashboard(navController)
            }
        }
        composable("addMotor") {
            AppScaffold(navController) {
                AddMotorForm(navController)
            }
        }
        composable("motorList") {
            AppScaffold(navController) {
                MotorListScreen(navController)
            }
        }
        composable("about") {
            AppScaffold(navController) {
                AboutScreen()
            }
        }
        composable("history") {
            AppScaffold(navController) {
                HistoryScreen(navController)
            }
        }
        composable("profile") {
            AppScaffold(navController) {
                ProfileScreen(navController)
            }
        }
        composable("settings") {
            AppScaffold(navController) {
                SettingsScreen(navController)
            }
        }
        composable("motor_status/{motorId}/{motorName}/{motorHP}") { backStackEntry ->
            val motorId = backStackEntry.arguments?.getString("motorId") ?: ""
            val motorName = backStackEntry.arguments?.getString("motorName") ?: ""
            val motorHP = backStackEntry.arguments?.getString("motorHP") ?: ""

            AppScaffold(navController) {
                MotorStatusScreen(
                    navController = navController,
//                    motorId = motorId,
                    motorName = motorName,
                    motorHP = motorHP
                )
            }
        }
    }
}

@Composable
fun AppScaffold(navController: NavHostController, content: @Composable (PaddingValues) -> Unit) {
    Scaffold(
        bottomBar = {
            BottomBar(navController = navController)
        }
    ) { padding ->
        content(padding)
    }
}
