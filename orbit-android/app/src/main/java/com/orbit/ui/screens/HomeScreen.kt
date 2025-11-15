package com.orbit.ui.screens

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.border
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
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.layout.wrapContentHeight
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.pager.HorizontalPager
import androidx.compose.foundation.pager.rememberPagerState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.AccountCircle
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Refresh
import androidx.compose.material3.Button
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.util.lerp
import androidx.hilt.navigation.compose.hiltViewModel
import com.airbnb.lottie.compose.LottieAnimation
import com.airbnb.lottie.compose.LottieCompositionSpec
import com.airbnb.lottie.compose.LottieConstants
import com.airbnb.lottie.compose.rememberLottieComposition
import com.orbit.R
import com.orbit.data.models.ErrorType
import com.orbit.data.models.Pod
import com.orbit.data.models.Resource
import com.orbit.ui.components.PodCard
import com.orbit.ui.components.PodDialogFlow
import com.orbit.ui.components.PodInfo
import com.orbit.ui.components.glassEffect
import com.orbit.ui.theme.Charcoal
import com.orbit.ui.theme.ChillyRed
import com.orbit.ui.theme.CustomRed
import com.orbit.ui.theme.CyanBlue
import com.orbit.ui.theme.DarkGray
import com.orbit.ui.theme.Gatians
import com.orbit.ui.theme.IndigoDeep
import com.orbit.ui.theme.Poppins
import com.orbit.viewmodel.PodListViewModel
import com.orbit.viewmodel.PodsViewModel
import kotlin.math.absoluteValue

@Composable
fun HomeScreen(
    modifier: Modifier = Modifier,
    onNavigateToPods: () -> Unit = {},
    podListViewModel: PodListViewModel = hiltViewModel(),
    podsViewModel: PodsViewModel = hiltViewModel()
) {
    var showDialog by remember { mutableStateOf(false) }

    // Collect states from ViewModel
    val userName by podListViewModel.userName.collectAsState()
    val walletAddress by podListViewModel.walletAddress.collectAsState()
    val flowBalance by podListViewModel.flowBalance.collectAsState()

    // Collect create pod state to get join code
    val createPodState by podsViewModel.createPodState.collectAsState()
    val joinCode = (createPodState as? Resource.Success)?.data?.joinCode

    Box(
        modifier = modifier
            .fillMaxSize()
            .background(DarkGray)
    ) {
        Column(modifier = Modifier.fillMaxSize()) {
            TopAppBar(
                userName = userName ?: "User",
                walletAddress = walletAddress,
                flowBalance = flowBalance
            )

            // Main content
            LazyColumn(
                modifier = Modifier.fillMaxSize()
            ) {
                // Recent Activity
                item {
                    RecentActivity()
                }

                // Info Cards - These now navigate to actual screens
                item {
                    InfoCards(onNavigateToPods = onNavigateToPods)
                }

                // Bottom spacer
                item {
                    Spacer(modifier = Modifier.height(100.dp))
                }

                item {
                    Box(
                        modifier = Modifier.fillMaxWidth(),
                        contentAlignment = Alignment.BottomCenter
                    ) {
                        Image(
                            painterResource(R.drawable.undraw_positive_attitude_xx3v),
                            contentDescription = null,
                            modifier = Modifier
                                .fillMaxWidth()
                                .size(150.dp)
                                .alpha(0.6f)
                        )
                    }
                }
            }
        }


        // FAB
        FloatingActionButton(
            onClick = {
                if (podListViewModel.onCreatePodClicked()) {
                    showDialog = true
                }
            },
            containerColor = CustomRed,
            modifier = Modifier
                .align(Alignment.BottomEnd)
                .padding(30.dp)
        ) {
            Icon(
                Icons.Filled.Add, contentDescription = "Add", tint = Color.White
            )
        }
        // Pod Dialog Flow
        PodDialogFlow(
            showDialog = showDialog,
            onDismiss = { showDialog = false },
            onCreatePod = { podName ->
                podsViewModel.createPod(podName)
            },
            onJoinPod = { qrCode ->
                // Extract join code from QR if formatted
                val extractedCode = com.orbit.util.QRCodeGenerator.extractJoinCode(qrCode)
                podsViewModel.joinPod(extractedCode)
                showDialog = false
            },
            onInviteOthers = {
                // TODO: Wire invite logic
                println("Invite others clicked")
            },
            onViewPods = {
                showDialog = false
                onNavigateToPods()
            },
            createdPodJoinCode = joinCode // Pass the join code from backend
        )


    }
}


@Composable
fun TopAppBar(
    userName: String = "User",
    walletAddress: String? = null,
    flowBalance: Double? = null,
    modifier: Modifier = Modifier
) {
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
            Column(
                modifier = Modifier.padding(10.dp)
            ) {
                Text(
                    "Hey ${userName.split(" ").firstOrNull().orEmpty() ?: "User"}!",
                    fontFamily = Poppins,
                    fontSize = 20.sp,
                    fontWeight = FontWeight.Normal,
                    color = Color.White
                )
            }

            Row(
                modifier = Modifier.padding(10.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                // Flow Balance Pill
                Box(
                    modifier = Modifier
                        .clip(RoundedCornerShape(50.dp))
                        .height(28.dp)
                        .background(CyanBlue.copy(alpha = 0.3f))
                        .border(
                            color = IndigoDeep,
                            width = 1.dp,
                            shape = RoundedCornerShape(50.dp)
                        )
                        .padding(horizontal = 12.dp, vertical = 4.dp),
                    contentAlignment = Alignment.Center
                ) {
                    Text(
                        text = if (flowBalance != null) {
                            String.format("%.2f FLOW", flowBalance)
                        } else {
                            "-- FLOW"
                        },
                        fontFamily = Poppins,
                        fontSize = 12.sp,
                        fontWeight = FontWeight.Medium,
                        color = Color.White
                    )
                }

                Spacer(Modifier.width(10.dp))

                Icon(
                    Icons.Filled.AccountCircle,
                    contentDescription = "Account Icon",
                    tint = Color.White,
                    modifier = Modifier.size(32.dp)
                )
            }
        }
    }
}

/**
 * Note: PodsSection, LoadingState, EmptyState, PodsSuccessState, and ErrorState
 * have been removed as pods are now displayed in the dedicated PodsScreen.
 * The HomeScreen focuses on Recent Activity and Info Cards for navigation.
 */


@Composable
fun InfoCard(
    title: String,
    iconRes: Int,
    imageRes: Int,
    onClick: () -> Unit = {},
    modifier: Modifier = Modifier
) {
    Box(
        modifier = modifier
            .padding(10.dp)
            .clip(RoundedCornerShape(20.dp))
            .background(Charcoal)
            .fillMaxWidth()
            .height(110.dp)
            .clickable { onClick() }) {
        Column(modifier = Modifier.padding(20.dp)) {
            Text(
                title,
                fontFamily = Gatians,
                fontSize = 24.sp,
                fontWeight = FontWeight.Normal,
            )
            Icon(
                painter = painterResource(iconRes),
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
            painter = painterResource(imageRes),
            modifier = Modifier
                .align(Alignment.BottomEnd)
                .size(160.dp)
                .offset(y = 20.dp, x = 10.dp),
            contentDescription = null
        )

    }
}

@Composable
fun InfoCards(
    modifier: Modifier = Modifier, onNavigateToPods: () -> Unit = {}
) {
    Column(modifier = modifier.fillMaxWidth()) {
        InfoCard(
            title = "Check your pods",
            iconRes = R.drawable.undraw_arrow,
            imageRes = R.drawable.pods,
            onClick = onNavigateToPods
        )
        InfoCard(
            title = "See wallet balance",
            iconRes = R.drawable.undraw_arrow,
            imageRes = R.drawable.wallet,
            onClick = onNavigateToPods
        )
        InfoCard(
            title = "Transaction history",
            iconRes = R.drawable.undraw_arrow,
            imageRes = R.drawable.shopping_checklist,
            onClick = onNavigateToPods
        )
    }
}

@Composable
fun RecentActivity() {
    // Hardcoded recent activity data - pod-related actions
    val recentActivities = remember {
        listOf(
            ActivityItem(
                icon = "💸",
                title = "Received payment",
                description = "From Team Alpha • $150.00",
                time = "2 hours ago"
            ),
            ActivityItem(
                icon = "🎉",
                title = "New pod created",
                description = "Coffee Lovers is live!",
                time = "5 hours ago"
            ),
            ActivityItem(
                icon = "👥",
                title = "New member joined",
                description = "Sarah joined Study Squad",
                time = "1 day ago"
            ),
            ActivityItem(
                icon = "📤",
                title = "Sent payment",
                description = "To Weekend Warriors • $75.00",
                time = "2 days ago"
            )
        )
    }

    val pagerState = rememberPagerState(pageCount = { recentActivities.size })
    Box(modifier = Modifier.padding(10.dp)) {
        //lottie animation
        val composition by rememberLottieComposition(
            LottieCompositionSpec.RawRes(R.raw.user_observation)
        )

        Column {
            Text(
                "Recent activity",
                fontSize = 24.sp,
                fontFamily = Gatians,
                fontWeight = FontWeight.Light
            )
            Row(verticalAlignment = Alignment.CenterVertically) {
                LottieAnimation(
                    composition = composition,
                    iterations = LottieConstants.IterateForever,
                    modifier = Modifier
                        .size(50.dp)
                        .graphicsLayer { rotationY = 180f })
                Column {
                    HorizontalPager(state = pagerState, pageSpacing = 10.dp) { page ->
                        val activity = recentActivities[page]
                        Card(
                            modifier = Modifier
                                .height(80.dp)
                                .fillMaxWidth()
                                .graphicsLayer {
                                    val pageOffset =
                                        ((pagerState.currentPage - page) + pagerState.currentPageOffsetFraction).absoluteValue

                                    alpha = lerp(
                                        start = 0.5f,
                                        stop = 1f,
                                        fraction = 1f - pageOffset.coerceIn(0f, 1f)
                                    )
                                }, colors = CardDefaults.cardColors(
                                containerColor = Charcoal
                            ), shape = RoundedCornerShape(12.dp)
                        ) {
                            Row(
                                modifier = Modifier
                                    .fillMaxSize()
                                    .padding(12.dp),
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                // Icon
                                Box(
                                    modifier = Modifier
                                        .size(48.dp)
                                        .clip(CircleShape)
                                        .background(Color.White.copy(alpha = 0.1f)),
                                    contentAlignment = Alignment.Center
                                ) {
                                    Text(
                                        text = activity.icon,
                                        fontSize = 24.sp
                                    )
                                }

                                Spacer(modifier = Modifier.width(12.dp))

                                // Activity details
                                Column(
                                    modifier = Modifier.weight(1f)
                                ) {
                                    Text(
                                        text = activity.title,
                                        fontSize = 14.sp,
                                        fontWeight = FontWeight.SemiBold,
                                        color = Color.White,
                                        fontFamily = Poppins
                                    )
                                    Text(
                                        text = activity.description,
                                        fontSize = 12.sp,
                                        color = Color.White.copy(alpha = 0.7f),
                                        fontFamily = Poppins
                                    )
                                    Text(
                                        text = activity.time,
                                        fontSize = 10.sp,
                                        color = Color.White.copy(alpha = 0.5f),
                                        fontFamily = Poppins,
                                        modifier = Modifier.padding(top = 2.dp)
                                    )
                                }
                            }
                        }
                    }

                    Row(
                        Modifier
                            .wrapContentHeight()
                            .fillMaxWidth()
                            .align(Alignment.CenterHorizontally)
                            .padding(top = 8.dp),
                        horizontalArrangement = Arrangement.Center
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

// Data class for activity items
data class ActivityItem(
    val icon: String,
    val title: String,
    val description: String,
    val time: String
)

@Suppress("unused")
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

