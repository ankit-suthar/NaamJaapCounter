package com.example.mainactivitykt

import android.content.Context
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            CounterApp()
        }
    }
}

@Composable
fun CounterApp() {
    val context = LocalContext.current
    val sharedPreferences = context.getSharedPreferences("CounterPrefs", Context.MODE_PRIVATE)

    var countPerMinute by remember { mutableStateOf("") }
    var durationMinutes by remember { mutableStateOf("") }
    var count by remember { mutableStateOf(sharedPreferences.getInt("last_count", 0)) }
    var lastStoppedCount by remember { mutableStateOf(0) } // Stores last count on stop
    var isRunning by remember { mutableStateOf(false) }
    var isPaused by remember { mutableStateOf(false) }
    var isResumePause by remember { mutableStateOf(false) }

    val handler = Handler(Looper.getMainLooper())
    var interval = (60 * 1000) / (countPerMinute.toIntOrNull() ?: 1)

    fun saveLastCount() {
        sharedPreferences.edit().putInt("last_count", count).apply()
    }

    fun startCounter() {
        val countRate = countPerMinute.toIntOrNull() ?: return
        val duration = durationMinutes.toIntOrNull() ?: return
        interval = (60 * 1000) / countRate
        isRunning = true
        isPaused = false

        if(!isResumePause) {
            count = 0
        }

        fun updateCount() {
            if (count < countRate * duration && isRunning && !isPaused) {
                count++
                saveLastCount() // Save count after every increment
                handler.postDelayed({ updateCount() }, interval.toLong())
            }
        }
        updateCount()
    }

    fun togglePauseResume() {
        isPaused = !isPaused
        if (!isPaused) {
            isResumePause = true
            startCounter()
        }
    }


    fun stopCounter() {
        lastStoppedCount = count // Store last count when stopped
        saveLastCount() // Save last count persistently
        count = 0
        isRunning = false
        isPaused = false
        isResumePause = false
    }

    Column(
        modifier = Modifier.fillMaxSize().padding(16.dp),
//        verticalArrangement = Arrangement.Center
    ) {
        Spacer(modifier = Modifier.height(80.dp))

        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.Center // Centers the text inside the Row
        ) {
            Text(
                text = "शिव",
                style = MaterialTheme.typography.displayLarge.copy(fontSize = 200.sp), // Bigger font size
                color = Color.Black
            )
        }

        OutlinedTextField(
            value = countPerMinute,
            onValueChange = { countPerMinute = it },
            label = { Text("Counts per Minute") }
        )

        OutlinedTextField(
            value = durationMinutes,
            onValueChange = { durationMinutes = it },
            label = { Text("Duration (Minutes)") }
        )

        Spacer(modifier = Modifier.height(16.dp))

        Row {
            Button(onClick = { startCounter() }, enabled = !isRunning) {
                Text("Start")
            }

            Spacer(modifier = Modifier.width(8.dp))

            Button(onClick = { togglePauseResume() }, enabled = isRunning) {
                Text(if (isPaused) "Resume" else "Pause")
            }

            Spacer(modifier = Modifier.width(8.dp))

            Button(onClick = { stopCounter() }, enabled = isRunning) {
                Text("Stop")
            }
        }

        Spacer(modifier = Modifier.height(16.dp))
        Text(text = "$count", style = MaterialTheme.typography.headlineLarge)
        Text(text = "Last Stopped Count: $lastStoppedCount", style = MaterialTheme.typography.bodyLarge)
    }
}

@Composable
fun CustomImage() {
    Image(
        painter = painterResource(id = R.drawable.ic_launcher),
        contentDescription = "NaamJaapCounter",
        modifier = Modifier.size(200.dp)
    )
}
