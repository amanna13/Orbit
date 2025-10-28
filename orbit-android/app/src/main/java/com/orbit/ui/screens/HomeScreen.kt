package com.orbit.ui.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.AccountCircle
import androidx.compose.material.icons.filled.Add
import androidx.compose.material3.ExtendedFloatingActionButton
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.FloatingActionButtonDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.LargeFloatingActionButton
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.orbit.ui.theme.Charcoal
import com.orbit.ui.theme.ChillyRed
import com.orbit.ui.theme.DarkGray

@Composable
fun HomeScreen(modifier: Modifier = Modifier) {
    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(DarkGray)
    ) {
        Column(modifier = Modifier.fillMaxSize()) {
            TopAppBar()
            Pods()
        }

        FloatingActionButton(onClick = {} , containerColor = ChillyRed, modifier = Modifier.align(
            Alignment.BottomEnd).padding(30.dp)){
            Icon(
                Icons.Filled.Add,
                contentDescription = "Add",
                tint = Color.White,

            )
        }
    }
}

@Composable
fun TopAppBar(modifier: Modifier = Modifier) {

        Box(
            modifier = modifier
                .fillMaxHeight(.1f)
                .fillMaxWidth()
                .background(Charcoal), contentAlignment = Alignment.BottomStart
        ) {
            Row(modifier = modifier.fillMaxWidth(), verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.SpaceBetween) {

            Box(
                modifier = Modifier
                    .padding(10.dp),
                contentAlignment = Alignment.BottomStart
            ) {
                Text("Orbit", fontSize = 20.sp, fontWeight = FontWeight.Bold)
            }

            Row(modifier = Modifier.padding(10.dp), verticalAlignment = Alignment.CenterVertically) {
                Box(modifier = Modifier
                    .clip(RoundedCornerShape(50.dp))
                    .width(70.dp)
                    .height(20.dp)
                    .background(Color.Red)) {
                    Icon(
                        Icons.Filled.Add,
                        contentDescription = "Profile Icon",
                        tint = Color.White
                    )
                }
                Spacer(Modifier.width(10.dp))

                Icon(Icons.Filled.AccountCircle, contentDescription = "Account Icon", tint = Color.White, modifier = Modifier)
            }
        }
    }
}


@Composable
fun Pods(modifier: Modifier = Modifier) {
    Box(
        modifier = modifier.padding((20.dp))
            .fillMaxWidth(.5f).height(190.dp).clip(RoundedCornerShape(20.dp))
            .background(Charcoal).padding(10.dp)
    ) {
        Text("Pods Screen", fontSize = 24.sp, color = ChillyRed)
    }
}