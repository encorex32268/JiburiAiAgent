package com.lihan.jiburiaiagent

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.navigation.compose.rememberNavController
import com.lihan.jiburiaiagent.core.presentation.JiburiTheme

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            JiburiTheme {
                val navController = rememberNavController()
                AppNavigation(navController = navController)
            }
        }
    }
}