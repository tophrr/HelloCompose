package com.example.ch03

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import com.example.ch03.ui.theme.Ch03Theme

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            Ch03Theme {
                // TODO 1: Ganti StarterScreen dengan MainScreen dari README.
                StarterScreen()
            }
        }
    }
}

@Composable
private fun StarterScreen() {
    // TODO 2: Buat MainScreen dengan Scaffold, TopAppBar, dan NavigationBar.
    Text("Lengkapi MainActivity.kt sesuai README")
}
