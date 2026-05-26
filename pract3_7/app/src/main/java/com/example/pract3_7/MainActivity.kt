package com.example.pract3_7

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import com.example.pract3_7.presentation.navigation.AppNavigation
import com.example.pract3_7.ui.theme.Pract3_7Theme

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            Pract3_7Theme {
                AppNavigation()
            }
        }
    }
}
