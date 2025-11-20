package com.example.moterhealthprediction

import android.annotation.SuppressLint
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.List
import androidx.compose.material.icons.filled.Person
import androidx.compose.material.icons.filled.Settings
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavHostController

@SuppressLint("UnusedMaterial3ScaffoldPaddingParameter")
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun UserDashboard(navController: NavHostController) {
    val context = LocalContext.current
    val userName = SessionManager.getUserName(context)


    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Dashboard", color = Color.White, fontWeight = FontWeight.Bold) },
                colors = TopAppBarDefaults.topAppBarColors(containerColor = Color(0xFF1976D2))
            )
        },
//        bottomBar = {
//            BottomBar(navController = navController)
//        }
    )
    { padding ->
        Column(
            modifier = Modifier
                .padding(padding)
                .fillMaxSize()
                .background(Color(0xFFE8F0F8)) // Minimal almost white background
                .padding(16.dp)
        ) {
            Spacer(modifier = Modifier.height(20.dp))

            // Welcome Card
            Card(
                shape = RoundedCornerShape(20.dp),
                colors = CardDefaults.cardColors(containerColor = Color.White),
                modifier = Modifier.fillMaxWidth()
            ) {
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    modifier = Modifier.padding(20.dp)
                ) {
                    Icon(
                        Icons.Default.Person,
                        contentDescription = "",
                        tint = Color(0xFF1976D2),
                        modifier = Modifier.size(60.dp)
                    )
                    Spacer(modifier = Modifier.width(16.dp))
                    Column {
                        Text(
                            text = "Hey, $userName 👋",
                            fontSize = 24.sp,
                            fontWeight = FontWeight.Bold,
                            color = Color(0xFF1976D2)
                        )
                        Text("Let's check your motor health!", fontSize = 14.sp, color = Color(0xFF757575))
                    }
                }
            }

            Spacer(modifier = Modifier.height(24.dp))

            // About the App Card
            Card(
                shape = RoundedCornerShape(20.dp),
                colors = CardDefaults.cardColors(containerColor = Color.White),
                modifier = Modifier.fillMaxWidth()
            ) {
                Column(
                    horizontalAlignment = Alignment.CenterHorizontally,
                    modifier = Modifier.padding(20.dp)
                ) {
                    Image(
                        painter = painterResource(id = R.drawable.m3),
                        contentDescription = "About App",
                        modifier = Modifier.size(90.dp)
                    )
                    Spacer(modifier = Modifier.height(12.dp))
                    Text(
                        "PowerPump",
                        fontSize = 20.sp,
                        fontWeight = FontWeight.ExtraBold,
                        color = Color(0xFF212121)
                    )
                    Text(
                        "Your Motor Pump Health Predictor",
                        fontSize = 15.sp,
                        fontWeight = FontWeight.SemiBold,
                        fontStyle = FontStyle.Italic,
                        color = Color(0xFF212121) ,
                        modifier = Modifier.padding(top = 5.dp)
                    )
                    Text(
                        "Live temperature, voltage & water level updates at your fingertips.",
                        fontSize = 14.sp,
                        color = Color(0xFF757575),
                        modifier = Modifier.padding(top = 8.dp)
                    )
                }
            }

            Spacer(modifier = Modifier.height(24.dp))

            // Quick Actions Section
            Text(
                "Quick Actions",
                fontSize = 20.sp,
                fontWeight = FontWeight.Bold,
                color = Color(0xFF1976D2),
                modifier = Modifier.padding(bottom = 12.dp)
            )

            Row(
                horizontalArrangement = Arrangement.SpaceEvenly,
                modifier = Modifier.fillMaxWidth()
            ) {
                QuickActionButton(icon = Icons.Default.Add, label = "Add Motor") {
                    navController.navigate("addMotor")
                }
                QuickActionButton(icon = Icons.Default.List, label = "Motors") {
                    navController.navigate("motorList")
                }
                QuickActionButton(icon = Icons.Default.Settings, label = "Settings") {
                    navController.navigate("settings")
                }
            }
        }
    }
}

@Composable
fun QuickActionButton(icon: ImageVector, label: String, onClick: () -> Unit) {
    Column(
        horizontalAlignment = Alignment.CenterHorizontally,
        modifier = Modifier
            .clip(RoundedCornerShape(16.dp))
            .background(Color.White)
            .clickable { onClick() }
            .padding(vertical = 16.dp, horizontal = 20.dp)
    ) {
        Icon(
            icon,
            contentDescription = label,
            tint = Color(0xFF1976D2),
            modifier = Modifier.size(36.dp)
        )
        Spacer(modifier = Modifier.height(8.dp))
        Text(label, fontSize = 13.sp, color = Color(0xFF424242))
    }
}
