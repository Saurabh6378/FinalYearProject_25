package com.example.motorhealthprediction

import android.annotation.SuppressLint
import android.widget.Toast
import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavHostController
import com.example.moterhealthprediction.R
import com.google.firebase.database.FirebaseDatabase

data class Motor(
    val id: String = "",
    val name: String = "",
    val power: String = "",
    val phase: String = ""
)

@SuppressLint("UnusedMaterial3ScaffoldPaddingParameter")
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AddMotorForm(navController: NavHostController) {
    var motorName by remember { mutableStateOf("") }
    var motorPower by remember { mutableStateOf("") }
    var motorPhase by remember { mutableStateOf("") }
    val context = LocalContext.current

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Add Motor", color = Color.White, fontWeight = FontWeight.Bold) },
                colors = TopAppBarDefaults.mediumTopAppBarColors(Color(0xFF1976D2))
            )
        },
        containerColor = Color(0xFFF7F9FC)
    ) { padding ->
        Column(
            modifier = Modifier
                .padding(padding)
                .fillMaxSize()
                .padding(20.dp),
            verticalArrangement = Arrangement.Top,
            horizontalAlignment = Alignment.CenterHorizontally
        ) {

            // Motor Illustration
            Image(
                painter = painterResource(id = R.drawable.m1),
                contentDescription = "Motor Image",
                modifier = Modifier
                    .size(160.dp)
                    .padding(16.dp)
            )

            Text(
                text = "Let's Add Your Motor!",
                fontSize = 24.sp,
                fontWeight = FontWeight.Bold,
                color = Color(0xFF1976D2)
            )

            Spacer(modifier = Modifier.height(20.dp))

            StyledInputField(label = "Motor Name", value = motorName) { motorName = it }
            StyledInputField(label = "Motor Power (HP)", value = motorPower) { motorPower = it }
            StyledInputField(label = "Motor Phase", value = motorPhase) { motorPhase = it }

            Spacer(modifier = Modifier.height(24.dp))

            Button(
                onClick = {
                    if (motorName.isNotBlank() && motorPower.isNotBlank() && motorPhase.isNotBlank()) {
                        val newMotor = Motor("", motorName, motorPower, motorPhase)
                        addMotorToFirebase(newMotor)
                        Toast.makeText(context, "Motor added successfully", Toast.LENGTH_SHORT).show()
                        navController.navigate("motorList")
                    } else {
                        Toast.makeText(context, "Please fill all fields", Toast.LENGTH_SHORT).show()
                    }
                },
                colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF1976D2)),
                shape = RoundedCornerShape(16.dp),
                modifier = Modifier
                    .fillMaxWidth()
                    .height(56.dp)
            ) {
                Icon(Icons.Filled.Add, contentDescription = null, tint = Color.White)
                Spacer(modifier = Modifier.width(8.dp))
                Text("Add Motor", color = Color.White, fontWeight = FontWeight.Bold)
            }
        }
    }
}

@Composable
fun StyledInputField(label: String, value: String, onValueChange: (String) -> Unit) {
    OutlinedTextField(
        value = value,
        onValueChange = onValueChange,
        label = { Text(label) },
        modifier = Modifier
            .fillMaxWidth()
            .height(60.dp),
        shape = RoundedCornerShape(12.dp),
        colors = OutlinedTextFieldDefaults.colors(
            focusedBorderColor = Color(0xFF1976D2),
            unfocusedBorderColor = Color.LightGray,
            focusedLabelColor = Color(0xFF1976D2)
        )
    )
}

fun addMotorToFirebase(motor: Motor) {
    val dbRef = FirebaseDatabase.getInstance().getReference("motors")
    val key = dbRef.push().key ?: return
    dbRef.child(key).setValue(motor.copy(id = key))
}
