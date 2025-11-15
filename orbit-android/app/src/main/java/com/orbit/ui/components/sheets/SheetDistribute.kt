package com.orbit.ui.components.sheets

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.orbit.data.models.Member
import com.orbit.data.models.Resource
import com.orbit.ui.theme.Charcoal
import com.orbit.ui.theme.CustomRed
import com.orbit.ui.theme.CyanBlue
import com.orbit.ui.theme.Poppins

/**
 * Distribute Funds Bottom Sheet
 * Distributes pod funds to members
 *
 * Backend API: POST /api/flow/transfer (for each member)
 * Supports two modes:
 * - Equal distribution (divide equally among all members)
 * - Custom percentage distribution
 */
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun SheetDistribute(
    podId: Long,
    podName: String,
    currentBalance: Double,
    membersState: Resource<List<Member>>,
    onDismiss: () -> Unit,
    onDistribute: (distributionMode: String, customAmounts: Map<String, Double>?) -> Unit,
    isLoading: Boolean = false
) {
    var distributionMode by remember { mutableStateOf("EQUAL") } // EQUAL or CUSTOM
    var customPercentages by remember { mutableStateOf<Map<String, Double>>(emptyMap()) }
    var showError by remember { mutableStateOf(false) }
    var errorMessage by remember { mutableStateOf("") }

    val members = when (membersState) {
        is Resource.Success -> membersState.data ?: emptyList()
        else -> emptyList()
    }

    // Calculate distributions
    val distributions = remember(distributionMode, customPercentages, currentBalance, members) {
        if (members.isEmpty()) return@remember emptyMap()

        when (distributionMode) {
            "EQUAL" -> {
                val amountPerMember = currentBalance / members.size
                members.associate { it.address to amountPerMember }
            }
            "CUSTOM" -> {
                members.associate { member ->
                    val percentage = customPercentages[member.address] ?: 0.0
                    member.address to (currentBalance * percentage / 100.0)
                }
            }
            else -> emptyMap()
        }
    }

    // Validate custom percentages total 100%
    val customPercentageTotal = customPercentages.values.sum()
    val isCustomValid = distributionMode == "EQUAL" || (customPercentageTotal == 100.0)

    ModalBottomSheet(
        onDismissRequest = onDismiss,
        containerColor = Charcoal,
        dragHandle = {
            Column(
                modifier = Modifier.fillMaxWidth(),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                Box(
                    modifier = Modifier
                        .padding(vertical = 12.dp)
                        .width(40.dp)
                        .height(4.dp)
                        .background(Color.White.copy(alpha = 0.3f), RoundedCornerShape(2.dp))
                )
            }
        }
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(24.dp),
            verticalArrangement = Arrangement.spacedBy(20.dp)
        ) {
            // Header
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Column {
                    Text(
                        "Distribute Funds",
                        fontSize = 24.sp,
                        fontWeight = FontWeight.Bold,
                        color = Color.White,
                        fontFamily = Poppins
                    )
                    Text(
                        "From $podName to ${members.size} member${if (members.size != 1) "s" else ""}",
                        fontSize = 14.sp,
                        color = Color.White.copy(alpha = 0.7f),
                        fontFamily = Poppins
                    )
                }

                Icon(
                    Icons.Filled.Money,
                    contentDescription = null,
                    tint = Color(0xFF4CAF50),
                    modifier = Modifier.size(32.dp)
                )
            }

            // Balance Info
            Card(
                modifier = Modifier.fillMaxWidth(),
                colors = CardDefaults.cardColors(
                    containerColor = CustomRed.copy(alpha = 0.1f)
                ),
                shape = RoundedCornerShape(12.dp)
            ) {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(16.dp),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Column {
                        Text(
                            "Total to Distribute",
                            fontSize = 12.sp,
                            color = Color.White.copy(alpha = 0.7f)
                        )
                        Text(
                            "$${String.format("%.2f", currentBalance)}",
                            fontSize = 28.sp,
                            fontWeight = FontWeight.Bold,
                            color = CustomRed,
                            fontFamily = Poppins
                        )
                    }

                    if (members.isNotEmpty() && distributionMode == "EQUAL") {
                        Column(horizontalAlignment = Alignment.End) {
                            Text(
                                "Per Member",
                                fontSize = 12.sp,
                                color = Color.White.copy(alpha = 0.7f)
                            )
                            Text(
                                "$${String.format("%.2f", currentBalance / members.size)}",
                                fontSize = 20.sp,
                                fontWeight = FontWeight.Bold,
                                color = Color.White,
                                fontFamily = Poppins
                            )
                        }
                    }
                }
            }

            if (members.isEmpty()) {
                // No members state
                Card(
                    modifier = Modifier.fillMaxWidth(),
                    colors = CardDefaults.cardColors(
                        containerColor = Color.White.copy(alpha = 0.05f)
                    )
                ) {
                    Column(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(24.dp),
                        horizontalAlignment = Alignment.CenterHorizontally,
                        verticalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        Icon(
                            Icons.Filled.GroupOff,
                            contentDescription = null,
                            tint = Color.White.copy(alpha = 0.5f),
                            modifier = Modifier.size(48.dp)
                        )
                        Text(
                            "No Members",
                            fontSize = 16.sp,
                            fontWeight = FontWeight.Bold,
                            color = Color.White
                        )
                        Text(
                            "Add members to distribute funds",
                            fontSize = 14.sp,
                            color = Color.White.copy(alpha = 0.7f),
                            textAlign = TextAlign.Center
                        )
                    }
                }
            } else {
                // Distribution Mode Selection
                Text(
                    "Distribution Mode",
                    fontSize = 14.sp,
                    fontWeight = FontWeight.Medium,
                    color = Color.White
                )

                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(12.dp)
                ) {
                    // Equal Mode
                    Card(
                        modifier = Modifier
                            .weight(1f)
                            .clickable { distributionMode = "EQUAL" },
                        colors = CardDefaults.cardColors(
                            containerColor = if (distributionMode == "EQUAL")
                                Color(0xFF4CAF50).copy(alpha = 0.3f)
                            else
                                Color.White.copy(alpha = 0.05f)
                        ),
                        shape = RoundedCornerShape(12.dp),
                        border = if (distributionMode == "EQUAL")
                            androidx.compose.foundation.BorderStroke(2.dp, Color(0xFF4CAF50))
                        else null
                    ) {
                        Column(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(16.dp),
                            horizontalAlignment = Alignment.CenterHorizontally,
                            verticalArrangement = Arrangement.spacedBy(8.dp)
                        ) {
                            Icon(
                                Icons.Filled.Balance,
                                contentDescription = null,
                                tint = if (distributionMode == "EQUAL") Color(0xFF4CAF50) else Color.White.copy(alpha = 0.5f),
                                modifier = Modifier.size(28.dp)
                            )
                            Text(
                                "Equal",
                                fontSize = 14.sp,
                                fontWeight = FontWeight.Bold,
                                color = Color.White
                            )
                            Text(
                                "Divide equally",
                                fontSize = 11.sp,
                                color = Color.White.copy(alpha = 0.7f),
                                textAlign = TextAlign.Center
                            )
                        }
                    }

                    // Custom Mode
                    Card(
                        modifier = Modifier
                            .weight(1f)
                            .clickable { distributionMode = "CUSTOM" },
                        colors = CardDefaults.cardColors(
                            containerColor = if (distributionMode == "CUSTOM")
                                CyanBlue.copy(alpha = 0.3f)
                            else
                                Color.White.copy(alpha = 0.05f)
                        ),
                        shape = RoundedCornerShape(12.dp),
                        border = if (distributionMode == "CUSTOM")
                            androidx.compose.foundation.BorderStroke(2.dp, CyanBlue)
                        else null
                    ) {
                        Column(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(16.dp),
                            horizontalAlignment = Alignment.CenterHorizontally,
                            verticalArrangement = Arrangement.spacedBy(8.dp)
                        ) {
                            Icon(
                                Icons.Filled.Tune,
                                contentDescription = null,
                                tint = if (distributionMode == "CUSTOM") CyanBlue else Color.White.copy(alpha = 0.5f),
                                modifier = Modifier.size(28.dp)
                            )
                            Text(
                                "Custom",
                                fontSize = 14.sp,
                                fontWeight = FontWeight.Bold,
                                color = Color.White
                            )
                            Text(
                                "Set percentages",
                                fontSize = 11.sp,
                                color = Color.White.copy(alpha = 0.7f),
                                textAlign = TextAlign.Center
                            )
                        }
                    }
                }

                // Members List with Distribution
                Text(
                    "Distribution Preview",
                    fontSize = 14.sp,
                    fontWeight = FontWeight.Medium,
                    color = Color.White
                )

                Card(
                    modifier = Modifier.fillMaxWidth(),
                    colors = CardDefaults.cardColors(
                        containerColor = Color.White.copy(alpha = 0.05f)
                    ),
                    shape = RoundedCornerShape(12.dp)
                ) {
                    LazyColumn(
                        modifier = Modifier
                            .fillMaxWidth()
                            .heightIn(max = 300.dp)
                            .padding(12.dp),
                        verticalArrangement = Arrangement.spacedBy(12.dp)
                    ) {
                        items(members) { member ->
                            MemberDistributionItem(
                                member = member,
                                distributionMode = distributionMode,
                                distribution = distributions[member.address] ?: 0.0,
                                percentage = customPercentages[member.address] ?: (100.0 / members.size),
                                onPercentageChange = { newPercentage ->
                                    customPercentages = customPercentages + (member.address to newPercentage)
                                }
                            )
                        }
                    }
                }

                // Custom percentage validation
                if (distributionMode == "CUSTOM") {
                    Card(
                        modifier = Modifier.fillMaxWidth(),
                        colors = CardDefaults.cardColors(
                            containerColor = if (isCustomValid)
                                Color(0xFF4CAF50).copy(alpha = 0.1f)
                            else
                                Color.Red.copy(alpha = 0.1f)
                        ),
                        shape = RoundedCornerShape(8.dp)
                    ) {
                        Row(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(12.dp),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Text(
                                if (isCustomValid) "✓ Percentages valid" else "⚠️ Total must equal 100%",
                                fontSize = 12.sp,
                                color = if (isCustomValid) Color(0xFF4CAF50) else Color.Red
                            )
                            Text(
                                "${String.format("%.1f", customPercentageTotal)}%",
                                fontSize = 14.sp,
                                fontWeight = FontWeight.Bold,
                                color = if (isCustomValid) Color(0xFF4CAF50) else Color.Red
                            )
                        }
                    }
                }
            }

            HorizontalDivider(color = Color.White.copy(alpha = 0.1f))

            // Action Buttons
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                OutlinedButton(
                    onClick = onDismiss,
                    modifier = Modifier.weight(1f),
                    enabled = !isLoading
                ) {
                    Text("Cancel")
                }

                Button(
                    onClick = {
                        when {
                            members.isEmpty() -> {
                                showError = true
                                errorMessage = "No members to distribute to"
                            }
                            currentBalance <= 0 -> {
                                showError = true
                                errorMessage = "No balance to distribute"
                            }
                            distributionMode == "CUSTOM" && !isCustomValid -> {
                                showError = true
                                errorMessage = "Percentages must total 100%"
                            }
                            else -> {
                                onDistribute(
                                    distributionMode,
                                    if (distributionMode == "CUSTOM") distributions else null
                                )
                            }
                        }
                    },
                    modifier = Modifier.weight(1f),
                    enabled = members.isNotEmpty() && isCustomValid && !isLoading,
                    colors = ButtonDefaults.buttonColors(
                        containerColor = Color(0xFF4CAF50)
                    )
                ) {
                    if (isLoading) {
                        CircularProgressIndicator(
                            modifier = Modifier.size(20.dp),
                            color = Color.White,
                            strokeWidth = 2.dp
                        )
                    } else {
                        Text("Distribute Now")
                    }
                }
            }

            Spacer(modifier = Modifier.height(8.dp))
        }
    }
}

/**
 * Member distribution item
 */
@Composable
private fun MemberDistributionItem(
    member: Member,
    distributionMode: String,
    distribution: Double,
    percentage: Double,
    onPercentageChange: (Double) -> Unit,
    modifier: Modifier = Modifier
) {
    var percentageInput by remember(distributionMode) {
        mutableStateOf(if (distributionMode == "CUSTOM") percentage.toString() else "")
    }

    Row(
        modifier = modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
    ) {
        // Member Info
        Row(
            horizontalArrangement = Arrangement.spacedBy(12.dp),
            verticalAlignment = Alignment.CenterVertically,
            modifier = Modifier.weight(1f)
        ) {
            // Avatar
            Box(
                modifier = Modifier
                    .size(40.dp)
                    .clip(CircleShape)
                    .background(CustomRed.copy(alpha = 0.2f))
                    .border(2.dp, CustomRed.copy(alpha = 0.5f), CircleShape),
                contentAlignment = Alignment.Center
            ) {
                Text(
                    member.displayName?.firstOrNull()?.uppercase() ?: "?",
                    fontSize = 16.sp,
                    fontWeight = FontWeight.Bold,
                    color = CustomRed
                )
            }

            Column(modifier = Modifier.weight(1f)) {
                Text(
                    member.displayName ?: "Unknown",
                    fontSize = 14.sp,
                    fontWeight = FontWeight.Medium,
                    color = Color.White,
                    maxLines = 1
                )
                Text(
                    member.role,
                    fontSize = 11.sp,
                    color = Color.White.copy(alpha = 0.6f)
                )
            }
        }

        // Distribution Amount or Percentage Input
        if (distributionMode == "CUSTOM") {
            OutlinedTextField(
                value = percentageInput,
                onValueChange = {
                    percentageInput = it
                    it.toDoubleOrNull()?.let { percent -> onPercentageChange(percent) }
                },
                suffix = { Text("%", fontSize = 12.sp) },
                modifier = Modifier.width(80.dp),
                keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Decimal),
                singleLine = true,
                textStyle = LocalTextStyle.current.copy(fontSize = 14.sp),
                colors = OutlinedTextFieldDefaults.colors(
                    focusedBorderColor = CyanBlue,
                    cursorColor = CyanBlue
                )
            )
        } else {
            Column(horizontalAlignment = Alignment.End) {
                Text(
                    "$${String.format("%.2f", distribution)}",
                    fontSize = 16.sp,
                    fontWeight = FontWeight.Bold,
                    color = Color(0xFF4CAF50)
                )
                Text(
                    "${String.format("%.1f", percentage)}%",
                    fontSize = 11.sp,
                    color = Color.White.copy(alpha = 0.6f)
                )
            }
        }
    }
}

