package com.example.pract3_3

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import com.example.pract3_3.presentation.navigation.AuthNavGraph
import com.example.pract3_3.ui.theme.Pract3_3Theme

class MainActivity : ComponentActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        setContent {
            Pract3_3Theme {
                AuthNavGraph()
            }
        }
    }
}