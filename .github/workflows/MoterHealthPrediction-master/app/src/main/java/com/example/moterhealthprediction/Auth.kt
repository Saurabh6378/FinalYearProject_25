package com.example.moterhealthprediction

import android.widget.Toast
import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavHostController
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.UserProfileChangeRequest

@Composable
fun AuthScreen(navController: NavHostController) {
    val context = LocalContext.current
    val auth = FirebaseAuth.getInstance()

    var isLoginScreen by remember { mutableStateOf(true) }
    var email by remember { mutableStateOf("") }
    var password by remember { mutableStateOf("") }
    var name by remember { mutableStateOf("") }
    var isLoading by remember { mutableStateOf(false) }

    val currentUser = auth.currentUser

    // If user already logged in, directly go to dashboard
    LaunchedEffect(currentUser) {
        if (currentUser != null) {
            SessionManager.saveUserName(context, currentUser.displayName ?: "User")
            navController.navigate("dashboard") {
                popUpTo(0)
            }
        }
    }

    Scaffold { padding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(padding)
                .padding(24.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center
        ) {
            Image(
                painter = painterResource(id = R.drawable.logog),
                contentDescription = "Auth Image",
                modifier = Modifier.size(350.dp)
            )

            Spacer(modifier = Modifier.height(16.dp))

            Text(
                if (isLoginScreen) "Welcome Back!" else "Create Account",
                fontSize = 24.sp,
                color = Color(0xFF1976D2)
            )

            Spacer(modifier = Modifier.height(24.dp))

            if (!isLoginScreen) {
                OutlinedTextField(
                    value = name,
                    onValueChange = { name = it },
                    label = { Text("Name") },
                    modifier = Modifier.fillMaxWidth(),
                    keyboardOptions = KeyboardOptions.Default.copy(
                        keyboardType = KeyboardType.Text,
                        imeAction = ImeAction.Next
                    )
                )
                Spacer(modifier = Modifier.height(16.dp))
            }

            OutlinedTextField(
                value = email,
                onValueChange = { email = it },
                label = { Text("Email") },
                modifier = Modifier.fillMaxWidth(),
                keyboardOptions = KeyboardOptions.Default.copy(
                    keyboardType = KeyboardType.Email,
                    imeAction = ImeAction.Next
                )
            )

            Spacer(modifier = Modifier.height(16.dp))

            OutlinedTextField(
                value = password,
                onValueChange = { password = it },
                label = { Text("Password") },
                modifier = Modifier.fillMaxWidth(),
                keyboardOptions = KeyboardOptions.Default.copy(
                    keyboardType = KeyboardType.Password,
                    imeAction = ImeAction.Done
                )
            )

            Spacer(modifier = Modifier.height(24.dp))

            Button(
                onClick = {
                    isLoading = true
                    if (isLoginScreen) {
                        // LOGIN
                        auth.signInWithEmailAndPassword(email, password)
                            .addOnCompleteListener { task ->
                                isLoading = false
                                if (task.isSuccessful) {
                                    val userName = auth.currentUser?.displayName ?: "User"
                                    SessionManager.saveUserName(context, userName)
                                    navController.navigate("dashboard") {
                                        popUpTo(0)
                                    }
                                } else {
                                    Toast.makeText(context, "Login failed: ${task.exception?.message}", Toast.LENGTH_SHORT).show()
                                }
                            }
                    } else {
                        // SIGNUP
                        auth.createUserWithEmailAndPassword(email, password)
                            .addOnCompleteListener { task ->
                                if (task.isSuccessful) {
                                    // Update the user's name
                                    val user = auth.currentUser
                                    val profileUpdates = UserProfileChangeRequest.Builder()
                                        .setDisplayName(name)
                                        .build()
                                    user?.updateProfile(profileUpdates)
                                        ?.addOnCompleteListener {
                                            isLoading = false
                                            SessionManager.saveUserName(context, name)
                                            navController.navigate("dashboard") {
                                                popUpTo(0)
                                            }
                                        }
                                } else {
                                    isLoading = false
                                    Toast.makeText(context, "Signup failed: ${task.exception?.message}", Toast.LENGTH_SHORT).show()
                                }
                            }
                    }
                },
                colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF1976D2)),

                modifier = Modifier.fillMaxWidth(),
                enabled = !isLoading
            ) {
                Text(if (isLoading) "Please wait..." else if (isLoginScreen) "Login" else "Sign Up")
            }

            Spacer(modifier = Modifier.height(12.dp))

            TextButton(
                onClick = { isLoginScreen = !isLoginScreen }
            ) {
                Text(
                    if (isLoginScreen) "Don't have an account? Sign up"
                    else "Already have an account? Login",
                    color = Color(0xFF1976D2)
                )
            }
        }
    }
}
