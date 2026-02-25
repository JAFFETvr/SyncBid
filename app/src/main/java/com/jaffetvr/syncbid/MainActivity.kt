package com.jaffetvr.syncbid

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.navigation.compose.rememberNavController
import com.jaffetvr.syncbid.core.ui.theme.SyncBidTheme
import com.jaffetvr.syncbid.navigation.SyncBidNavGraph
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            SyncBidTheme {
                val navController = rememberNavController()
                SyncBidNavGraph(navController = navController)
            }
        }
    }
}