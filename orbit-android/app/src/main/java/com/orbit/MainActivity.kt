package com.orbit

import android.annotation.SuppressLint
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import com.orbit.navigation.OrbitNavigation
import com.orbit.ui.screens.SignInScreen
import com.orbit.ui.theme.OrbitTheme
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        enableEdgeToEdge()

        val deepLinkUri = intent?.data

        setContent {
            OrbitTheme {
                Scaffold(modifier = Modifier.fillMaxSize()) {innerpadding->
                    OrbitNavigation(deepLinkUri = deepLinkUri, innerPaddingValues = innerpadding)
                }
            }
        }
    }
}
