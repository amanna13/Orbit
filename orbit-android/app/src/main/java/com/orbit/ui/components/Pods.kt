package com.orbit.ui.components

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Person
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.orbit.ui.theme.Charcoal
import com.orbit.ui.theme.Poppins

// Data class for Pod information
data class PodInfo(
    val podId: String,
    val podName: String,
    val memberCount: Int,
    val balance: Double,  // Pod balance in currency
    val colorTag: Color,
    val emoji: String,  // Emoji for the pod
    val createdAt: String? = null,
    val lastActivity: String? = null
)

@Composable
fun PodCard(
    podInfo: PodInfo,
    modifier: Modifier = Modifier,
    onClick: () -> Unit = {}
) {
    Card(
        modifier = modifier
            .fillMaxWidth()
            .height(200.dp),
        shape = RoundedCornerShape(24.dp),
        elevation = CardDefaults.cardElevation(defaultElevation = 6.dp),
        colors = CardDefaults.cardColors(containerColor = Charcoal)
    ) {
        Box(
            modifier = Modifier
                .fillMaxSize()
                .background(
                    brush = Brush.verticalGradient(
                        colors = listOf(
                            podInfo.colorTag.copy(alpha = 0.4f),
                            podInfo.colorTag.copy(alpha = 0.2f),
                            Charcoal.copy(alpha = 0.95f)
                        ),
                        startY = 0f,
                        endY = 600f
                    )
                )
                .padding(20.dp)
        ) {
            Column(
                modifier = Modifier.fillMaxSize(),
                verticalArrangement = Arrangement.SpaceBetween
            ) {
                // Top section - Small Emoji
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    // Small emoji with background
                    Box(
                        modifier = Modifier
                            .size(40.dp)
                            .clip(CircleShape)
                            .background(Color.White.copy(alpha = 0.12f)),
                        contentAlignment = Alignment.Center
                    ) {
                        Text(
                            text = podInfo.emoji,
                            fontSize = 20.sp
                        )
                    }
                }

                Spacer(modifier = Modifier.height(8.dp))

                // Bottom section - Pod details
                Column(
                    verticalArrangement = Arrangement.spacedBy(10.dp)
                ) {
                    // Pod name
                    Text(
                        text = podInfo.podName,
                        fontFamily = Poppins,
                        fontSize = 20.sp,
                        fontWeight = FontWeight.SemiBold,
                        color = Color.White,
                        maxLines = 1,
                        overflow = TextOverflow.Ellipsis
                    )

                    // Stats row with pill-shaped badges
                    Row(
                        horizontalArrangement = Arrangement.spacedBy(8.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        // Member count pill (no "members" text)
                        Box(
                            modifier = Modifier
                                .clip(RoundedCornerShape(20.dp))
                                .background(Color.White.copy(alpha = 0.15f))
                                .padding(horizontal = 12.dp, vertical = 6.dp)
                        ) {
                            Row(
                                verticalAlignment = Alignment.CenterVertically,
                                horizontalArrangement = Arrangement.spacedBy(6.dp)
                            ) {
                                Icon(
                                    imageVector = Icons.Filled.Person,
                                    contentDescription = "Members",
                                    tint = podInfo.colorTag.copy(alpha = 0.9f),
                                    modifier = Modifier.size(16.dp)
                                )
                                Text(
                                    text = "${podInfo.memberCount}",
                                    fontFamily = Poppins,
                                    fontSize = 13.sp,
                                    fontWeight = FontWeight.SemiBold,
                                    color = Color.White
                                )
                            }
                        }

                        // Balance pill with currency symbol (no "balance" text)
                        Box(
                            modifier = Modifier
                                .clip(RoundedCornerShape(20.dp))
                                .background(podInfo.colorTag.copy(alpha = 0.25f))
                                .padding(horizontal = 12.dp, vertical = 6.dp)
                        ) {
                            Row(
                                verticalAlignment = Alignment.CenterVertically,
                                horizontalArrangement = Arrangement.spacedBy(4.dp)
                            ) {
                                Text(
                                    text = "₹",
                                    fontFamily = Poppins,
                                    fontSize = 14.sp,
                                    fontWeight = FontWeight.Bold,
                                    color = podInfo.colorTag
                                )
                                Text(
                                    text = String.format("%.0f", podInfo.balance),
                                    fontFamily = Poppins,
                                    fontSize = 13.sp,
                                    fontWeight = FontWeight.SemiBold,
                                    color = Color.White
                                )
                            }
                        }
                    }
                }
            }
        }
    }
}

