package com.orbit.ui.screens

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.layout.wrapContentHeight
import androidx.compose.foundation.pager.HorizontalPager
import androidx.compose.foundation.pager.rememberPagerState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.AccountCircle
import androidx.compose.material.icons.filled.Add
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.util.lerp
import com.airbnb.lottie.compose.LottieAnimation
import com.airbnb.lottie.compose.LottieCompositionSpec
import com.airbnb.lottie.compose.LottieConstants
import com.airbnb.lottie.compose.rememberLottieComposition
import com.orbit.R
import com.orbit.ui.components.glassEffect
import com.orbit.ui.components.PodDialogFlow
import com.orbit.ui.theme.Charcoal
import com.orbit.ui.theme.ChillyRed
import com.orbit.ui.theme.CustomRed
import com.orbit.ui.theme.DarkGray
import com.orbit.ui.theme.Gatians
import kotlin.math.absoluteValue

@Composable
fun HomeScreen(modifier: Modifier = Modifier) {
    var showDialog by remember { mutableStateOf(false) }

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(DarkGray)
    ) {
        Column(modifier = Modifier.fillMaxSize()) {
            TopAppBar()
            RecentActivity()
            InfoCards()
        }

        FloatingActionButton(
            onClick = { showDialog = true }, 
            containerColor = CustomRed, 
            modifier = Modifier
                .align(Alignment.BottomEnd)
                .padding(30.dp)
        ) {
            Icon(
                Icons.Filled.Add,
                contentDescription = "Add",
                tint = Color.White
            )
        }

        // Pod Dialog Flow with all states
        PodDialogFlow(
            showDialog = showDialog,
            onDismiss = { showDialog = false },
            onCreatePod = { podName ->
                // TODO: Wire network call here
                // Example: viewModel.createPod(podName)
                println("Creating pod: $podName")
            },
            onJoinPod = { qrCode ->
                // TODO: Wire network call to verify and join pod
                // Example: viewModel.joinPod(qrCode)
                println("Joining pod with code: $qrCode")
            },
            onInviteOthers = {
                // TODO: Wire invite others navigation/logic here
                println("Invite others clicked")
            },
            onViewPods = {
                // TODO: Wire view pods navigation here
                println("View pods clicked")
            }
        )
    }
}

@Composable
fun TopAppBar(modifier: Modifier = Modifier) {
    Box(
        modifier = modifier
            .fillMaxHeight(.1f)
            .fillMaxWidth()
            .background(Charcoal),
        contentAlignment = Alignment.BottomStart
    ) {
        Row(
            modifier = modifier.fillMaxWidth(),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            Box(
                modifier = Modifier.padding(10.dp),
                contentAlignment = Alignment.BottomStart
            ) {
                Text("Hey Avirup ! ", fontSize = 20.sp, fontWeight = FontWeight.Bold)
            }

            Row(
                modifier = Modifier.padding(10.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                Box(
                    modifier = Modifier
                        .clip(RoundedCornerShape(50.dp))
                        .width(70.dp)
                        .height(20.dp)
                        .background(Color.Red)
                ) {
                    Icon(
                        Icons.Filled.Add,
                        contentDescription = "Profile Icon",
                        tint = Color.White
                    )
                }
                Spacer(Modifier.width(10.dp))

                Icon(
                    Icons.Filled.AccountCircle,
                    contentDescription = "Account Icon",
                    tint = Color.White,
                    modifier = Modifier
                )
            }
        }
    }
}



@Composable
fun InfoCards(modifier: Modifier = Modifier) {
    Box(
        modifier = Modifier
            .padding(10.dp)
            .clip(RoundedCornerShape(20.dp))
            .background(Charcoal)
            .fillMaxWidth()
            .height(110.dp)
    ) {
        Column(modifier = Modifier.padding(20.dp)) {
            Text(
                "Check your pods",
                fontFamily = Gatians,
                fontSize = 24.sp,
                fontWeight = FontWeight.Normal,
            )
            Icon(
                painter = painterResource(R.drawable.undraw_arrow),
                contentDescription = null,
                modifier
                    .clip(RoundedCornerShape(50.dp))
                    .size(60.dp)
                    .background(Color.White)
                    .padding(10.dp)
                    .align(Alignment.Start),
                tint = Color.Black
            )
        }
        Image(
            painter = painterResource(R.drawable.pods),
            modifier = Modifier
                .align(Alignment.BottomEnd)
                .size(140.dp)
                .offset(y = 20.dp, x = 10.dp),
            contentDescription = null
        )

    }
}

@Composable
fun RecentActivity(modifier: Modifier = Modifier) {
    val pagerState = rememberPagerState(pageCount = { 4 })
    Box(modifier = Modifier.padding(10.dp)) {
        //lottie animation can be added here
        val composition by rememberLottieComposition(
            LottieCompositionSpec.RawRes(R.raw.user_observation)
        )

            Column {
                Text("Recent activity", fontSize = 24.sp, fontFamily = Gatians, fontWeight = FontWeight.Light)
                Row(verticalAlignment = Alignment.CenterVertically) {
                    LottieAnimation(composition = composition, iterations = LottieConstants.IterateForever, modifier = Modifier.size(50.dp).graphicsLayer{rotationY = 180f})
                Column {
                    HorizontalPager(state = pagerState) { page ->
                        Card(
                            modifier = Modifier
                                .padding(start = 10.dp)
                                .height(60.dp).fillMaxWidth()
                                .graphicsLayer {
                                    val pageOffset =
                                        ((pagerState.currentPage - page) + pagerState.currentPageOffsetFraction).absoluteValue

//                            lerp(
//                                start = 0.85f,
//                                stop = 1f,
//                                fraction = 1f - pageOffset.coerceIn(0f, 1f)
//                            ).also { scale ->
//                                scaleX = scale
//                                scaleY = scale
//                            }

                                    alpha = lerp(
                                        start = 0.5f,
                                        stop = 1f,
                                        fraction = 1f - pageOffset.coerceIn(0f, 1f)
                                    )
                                }, colors = CardDefaults.cardColors(
                                containerColor = Charcoal
                            ), shape = RoundedCornerShape(12.dp)
                        ) {
                            Box(
                                modifier = Modifier
                                    .fillMaxSize(),
                                contentAlignment = Alignment.Center
                            ) {
                                Text("Activity ${page + 1}", fontSize = 14.sp, color = Color.White)
                            }
                        }
                    }

                    Row(
                        Modifier
                            .wrapContentHeight()
                            .fillMaxWidth()
                            .align(Alignment.CenterHorizontally)
                            .padding(bottom = 8.dp, top = 8.dp), horizontalArrangement = Arrangement.Center
                    ) {
                        repeat(pagerState.pageCount) { iteration ->
                            val color =
                                if (pagerState.currentPage == iteration) Color.LightGray else Color.DarkGray
                            Box(
                                modifier = Modifier
                                    .padding(2.dp)
                                    .clip(CircleShape)
                                    .background(color)
                                    .size(8.dp)
                            )
                        }
                    }
                }
            }
        }
    }
}

@Composable
fun Pods(modifier: Modifier = Modifier) {
    Box(
        modifier = modifier
            .padding((20.dp))
            .fillMaxWidth(.5f)
            .height(190.dp)
            .clip(RoundedCornerShape(20.dp))
            .glassEffect(20.dp)
            .background(Charcoal)
            .padding(10.dp)
    ) {
        Text("Pods Screen", fontSize = 24.sp, color = ChillyRed)
    }
}

