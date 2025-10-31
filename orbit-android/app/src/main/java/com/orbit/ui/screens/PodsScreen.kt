package com.orbit.ui.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.statusBarsPadding
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.Add
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
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
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.orbit.ui.components.PodCard
import com.orbit.ui.components.PodDialogFlow
import com.orbit.ui.components.PodInfo
import com.orbit.ui.theme.AmberOrange
import com.orbit.ui.theme.Charcoal
import com.orbit.ui.theme.CoralOrange
import com.orbit.ui.theme.CustomRed
import com.orbit.ui.theme.CyanBlue
import com.orbit.ui.theme.DarkGray
import com.orbit.ui.theme.DeepPurple
import com.orbit.ui.theme.EmeraldGreen
import com.orbit.ui.theme.IndigoDeep
import com.orbit.ui.theme.LimeGreen
import com.orbit.ui.theme.PinkVivid
import com.orbit.ui.theme.Poppins
import com.orbit.ui.theme.TealAccent
import com.orbit.ui.theme.VividBlue

@Composable
fun PodsScreen(
    onNavigateBack: () -> Unit = {},
    modifier: Modifier = Modifier
) {
    var showDialog by remember { mutableStateOf(false) }

    // Sample static data - TODO: Replace with actual backend data
    val podColors = listOf(
        DeepPurple,      // Purple
        VividBlue,       // Blue
        TealAccent,      // Teal
        LimeGreen,       // Green
        AmberOrange,     // Orange
        CyanBlue,        // Cyan
        IndigoDeep,      // Indigo
        PinkVivid,       // Pink
        EmeraldGreen,    // Emerald
        CoralOrange      // Coral
    )

    val smileyEmojis = listOf(
        "😊", "😎", "🤩", "😇", "🥳",
        "😄", "😁", "🙂", "😉", "😌",
        "🤗", "😋", "😍", "🥰", "😏"
    )

    val samplePods = remember {
        listOf(
            PodInfo(
                podId = "POD001",
                podName = "Team Alpha",
                memberCount = 8,
                balance = 12500.0,
                colorTag = podColors[0],
                emoji = smileyEmojis[0],
                lastActivity = "2h ago"
            ),
            PodInfo(
                podId = "POD002",
                podName = "Weekend Warriors",
                memberCount = 5,
                balance = 8750.0,
                colorTag = podColors[1],
                emoji = smileyEmojis[1],
                lastActivity = "5h ago"
            ),
            PodInfo(
                podId = "POD003",
                podName = "Study Squad",
                memberCount = 12,
                balance = 23400.0,
                colorTag = podColors[2],
                emoji = smileyEmojis[2],
                lastActivity = "1d ago"
            ),
            PodInfo(
                podId = "POD004",
                podName = "Gym Buddies",
                memberCount = 6,
                balance = 5600.0,
                colorTag = podColors[3],
                emoji = smileyEmojis[3],
                lastActivity = "3h ago"
            ),
            PodInfo(
                podId = "POD005",
                podName = "Coffee Lovers",
                memberCount = 15,
                balance = 18900.0,
                colorTag = podColors[4],
                emoji = smileyEmojis[4],
                lastActivity = "just now"
            ),
            PodInfo(
                podId = "POD006",
                podName = "Night Owls",
                memberCount = 4,
                balance = 3200.0,
                colorTag = podColors[5],
                emoji = smileyEmojis[5],
                lastActivity = "6h ago"
            ),
            PodInfo(
                podId = "POD007",
                podName = "Music Makers",
                memberCount = 9,
                balance = 14750.0,
                colorTag = podColors[6],
                emoji = smileyEmojis[6],
                lastActivity = "4h ago"
            ),
            PodInfo(
                podId = "POD008",
                podName = "Adventure Seekers",
                memberCount = 11,
                balance = 27800.0,
                colorTag = podColors[7],
                emoji = smileyEmojis[7],
                lastActivity = "yesterday"
            )
        )
    }

    Box(
        modifier = modifier
            .fillMaxSize()
            .background(DarkGray)
    ) {
        Column(
            modifier = Modifier.fillMaxSize()
        ) {
            // Top App Bar
            PodsTopBar(onNavigateBack = onNavigateBack)

            // Pods Grid
            if (samplePods.isEmpty()) {
                // Empty state
                EmptyPodsState()
            } else {
                LazyVerticalGrid(
                    columns = GridCells.Fixed(2),
                    contentPadding = PaddingValues(16.dp),
                    horizontalArrangement = Arrangement.spacedBy(12.dp),
                    verticalArrangement = Arrangement.spacedBy(12.dp),
                    modifier = Modifier.fillMaxSize()
                ) {
                    items(samplePods) { pod ->
                        PodCard(
                            podInfo = pod,
                            onClick = {
                                // TODO: Navigate to pod details
                                println("Clicked on pod: ${pod.podName}")
                            }
                        )
                    }
                }
            }
        }

        // FAB for creating new pod
        FloatingActionButton(
            onClick = { showDialog = true },
            containerColor = CustomRed,
            modifier = Modifier
                .align(Alignment.BottomEnd)
                .padding(24.dp)
        ) {
            Icon(
                Icons.Filled.Add,
                contentDescription = "Create Pod",
                tint = Color.White
            )
        }

        // Pod Dialog Flow
        PodDialogFlow(
            showDialog = showDialog,
            onDismiss = { showDialog = false },
            onCreatePod = { podName ->
                // TODO: Wire network call here
                println("Creating pod: $podName")
            },
            onJoinPod = { qrCode ->
                // TODO: Wire network call to verify and join pod
                println("Joining pod with code: $qrCode")
            },
            onInviteOthers = {
                // TODO: Wire invite others navigation/logic here
                println("Invite others clicked")
            },
            onViewPods = {
                // Dismiss dialog since we're already on pods screen
                showDialog = false
            }
        )
    }
}

@Composable
private fun PodsTopBar(
    onNavigateBack: () -> Unit,
    modifier: Modifier = Modifier
) {
    Box(
        modifier = modifier
            .fillMaxWidth()
            .background(Charcoal)
            .statusBarsPadding()
            .padding(horizontal = 16.dp, vertical = 16.dp)
    ) {
        Row(
            modifier = Modifier.fillMaxWidth(),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            // Back button
            IconButton(
                onClick = onNavigateBack,
                modifier = Modifier
                    .size(40.dp)
                    .clip(CircleShape)
                    .background(Color.White.copy(alpha = 0.1f))
            ) {
                Icon(
                    Icons.AutoMirrored.Filled.ArrowBack,
                    contentDescription = "Back",
                    tint = Color.White,
                    modifier = Modifier.size(24.dp)
                )
            }

            // Title
            Text(
                text = "Your Pods",
                fontFamily = Poppins,
                fontSize = 22.sp,
                fontWeight = FontWeight.SemiBold,
                color = Color.White
            )

            // Spacer to balance the layout
            Spacer(modifier = Modifier.size(40.dp))
        }
    }
}

@Composable
private fun EmptyPodsState(modifier: Modifier = Modifier) {
    Box(
        modifier = modifier.fillMaxSize(),
        contentAlignment = Alignment.Center
    ) {
        Column(
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            Text(
                text = "🏝️",
                fontSize = 64.sp
            )

            Text(
                text = "No Pods Yet",
                fontFamily = Poppins,
                fontSize = 24.sp,
                fontWeight = FontWeight.SemiBold,
                color = Color.White
            )

            Text(
                text = "Create or join a pod to get started",
                fontFamily = Poppins,
                fontSize = 14.sp,
                fontWeight = FontWeight.Normal,
                color = Color.White.copy(alpha = 0.7f)
            )
        }
    }
}

