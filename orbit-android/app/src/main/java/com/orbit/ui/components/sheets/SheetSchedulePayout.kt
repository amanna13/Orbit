package com.orbit.ui.components.sheets

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.orbit.ui.theme.Charcoal
import com.orbit.ui.theme.CustomRed
import com.orbit.ui.theme.IndigoDeep
import com.orbit.ui.theme.Poppins
import java.text.SimpleDateFormat
import java.util.*

/**
 * Schedule Payout Bottom Sheet
 * Creates recurring payment schedule using Forte API
 *
 * Backend API: POST /api/forte/scheduled-payments
 * Body: { payerPodID, receiverPodID, amount, intervalDays }
 */
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun SheetSchedulePayout(
    podId: Long,
    podName: String,
    currentBalance: Double,
    onDismiss: () -> Unit,
    onSchedule: (amount: Double, frequency: String, startDate: String, endDate: String?) -> Unit,
    isLoading: Boolean = false
) {
    var amount by remember { mutableStateOf("") }
    var selectedFrequency by remember { mutableStateOf("WEEKLY") }
    var intervalDays by remember { mutableStateOf(7) }
    var startDate by remember { mutableStateOf(getTodayDate()) }
    var endDate by remember { mutableStateOf<String?>(null) }
    var hasEndDate by remember { mutableStateOf(false) }
    var showError by remember { mutableStateOf(false) }
    var errorMessage by remember { mutableStateOf("") }

    val frequencies = listOf(
        "DAILY" to 1,
        "WEEKLY" to 7,
        "BIWEEKLY" to 14,
        "MONTHLY" to 30
    )

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
                        "Schedule Payout",
                        fontSize = 24.sp,
                        fontWeight = FontWeight.Bold,
                        color = Color.White,
                        fontFamily = Poppins
                    )
                    Text(
                        "Set up recurring payment from $podName",
                        fontSize = 14.sp,
                        color = Color.White.copy(alpha = 0.7f),
                        fontFamily = Poppins
                    )
                }

                Icon(
                    Icons.Filled.Schedule,
                    contentDescription = null,
                    tint = IndigoDeep,
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
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(16.dp),
                    verticalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    Text(
                        "Pod Balance",
                        fontSize = 12.sp,
                        color = Color.White.copy(alpha = 0.7f)
                    )
                    Text(
                        "$${String.format("%.2f", currentBalance)}",
                        fontSize = 24.sp,
                        fontWeight = FontWeight.Bold,
                        color = CustomRed,
                        fontFamily = Poppins
                    )

                    // Payment preview
                    val paymentAmount = amount.toDoubleOrNull()
                    if (paymentAmount != null && paymentAmount > 0) {
                        HorizontalDivider(
                            color = Color.White.copy(alpha = 0.1f),
                            modifier = Modifier.padding(vertical = 4.dp)
                        )
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween
                        ) {
                            Text(
                                "$selectedFrequency Payment",
                                fontSize = 12.sp,
                                color = Color.White.copy(alpha = 0.7f)
                            )
                            Text(
                                "$${String.format("%.2f", paymentAmount)}",
                                fontSize = 14.sp,
                                fontWeight = FontWeight.Bold,
                                color = Color.White
                            )
                        }
                    }
                }
            }

            // Amount Input
            OutlinedTextField(
                value = amount,
                onValueChange = {
                    amount = it
                    showError = false
                },
                label = { Text("Amount per Payment") },
                placeholder = { Text("0.00") },
                leadingIcon = {
                    Icon(Icons.Filled.AttachMoney, contentDescription = null)
                },
                keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Decimal),
                modifier = Modifier.fillMaxWidth(),
                singleLine = true,
                isError = showError,
                supportingText = {
                    if (showError) {
                        Text(errorMessage, color = MaterialTheme.colorScheme.error)
                    } else {
                        Text("Amount to pay each interval")
                    }
                },
                colors = OutlinedTextFieldDefaults.colors(
                    focusedBorderColor = IndigoDeep,
                    focusedLabelColor = IndigoDeep,
                    cursorColor = IndigoDeep
                )
            )

            // Frequency Selection
            Text(
                "Payment Frequency",
                fontSize = 14.sp,
                fontWeight = FontWeight.Medium,
                color = Color.White
            )

            Column(
                verticalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                frequencies.forEach { (freq, days) ->
                    Card(
                        modifier = Modifier
                            .fillMaxWidth()
                            .clickable {
                                selectedFrequency = freq
                                intervalDays = days
                            },
                        colors = CardDefaults.cardColors(
                            containerColor = if (selectedFrequency == freq)
                                IndigoDeep.copy(alpha = 0.3f)
                            else
                                Color.White.copy(alpha = 0.05f)
                        ),
                        shape = RoundedCornerShape(12.dp),
                        border = if (selectedFrequency == freq)
                            androidx.compose.foundation.BorderStroke(2.dp, IndigoDeep)
                        else null
                    ) {
                        Row(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(16.dp),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Row(
                                horizontalArrangement = Arrangement.spacedBy(12.dp),
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Icon(
                                    when (freq) {
                                        "DAILY" -> Icons.Filled.Today
                                        "WEEKLY" -> Icons.Filled.CalendarToday
                                        "BIWEEKLY" -> Icons.Filled.Event
                                        "MONTHLY" -> Icons.Filled.CalendarMonth
                                        else -> Icons.Filled.Schedule
                                    },
                                    contentDescription = null,
                                    tint = if (selectedFrequency == freq) IndigoDeep else Color.White.copy(alpha = 0.5f)
                                )
                                Column {
                                    Text(
                                        freq,
                                        fontSize = 16.sp,
                                        fontWeight = FontWeight.Medium,
                                        color = Color.White
                                    )
                                    Text(
                                        "Every $days day${if (days > 1) "s" else ""}",
                                        fontSize = 12.sp,
                                        color = Color.White.copy(alpha = 0.7f)
                                    )
                                }
                            }

                            if (selectedFrequency == freq) {
                                Icon(
                                    Icons.Filled.CheckCircle,
                                    contentDescription = "Selected",
                                    tint = IndigoDeep
                                )
                            }
                        }
                    }
                }
            }

            // Date Selection
            Text(
                "Schedule Period",
                fontSize = 14.sp,
                fontWeight = FontWeight.Medium,
                color = Color.White
            )

            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                OutlinedTextField(
                    value = startDate,
                    onValueChange = { startDate = it },
                    label = { Text("Start Date") },
                    leadingIcon = {
                        Icon(Icons.Filled.DateRange, contentDescription = null)
                    },
                    modifier = Modifier.weight(1f),
                    singleLine = true,
                    readOnly = true,
                    colors = OutlinedTextFieldDefaults.colors(
                        focusedBorderColor = IndigoDeep,
                        focusedLabelColor = IndigoDeep
                    )
                )
            }

            // Optional End Date
            Row(
                modifier = Modifier.fillMaxWidth(),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                Text(
                    "Set End Date (Optional)",
                    fontSize = 14.sp,
                    color = Color.White.copy(alpha = 0.7f)
                )
                Switch(
                    checked = hasEndDate,
                    onCheckedChange = { hasEndDate = it },
                    colors = SwitchDefaults.colors(
                        checkedThumbColor = IndigoDeep,
                        checkedTrackColor = IndigoDeep.copy(alpha = 0.5f)
                    )
                )
            }

            if (hasEndDate) {
                OutlinedTextField(
                    value = endDate ?: "",
                    onValueChange = { endDate = it },
                    label = { Text("End Date") },
                    leadingIcon = {
                        Icon(Icons.Filled.DateRange, contentDescription = null)
                    },
                    modifier = Modifier.fillMaxWidth(),
                    singleLine = true,
                    readOnly = true,
                    colors = OutlinedTextFieldDefaults.colors(
                        focusedBorderColor = IndigoDeep,
                        focusedLabelColor = IndigoDeep
                    )
                )
            }

            // Summary Card
            Card(
                modifier = Modifier.fillMaxWidth(),
                colors = CardDefaults.cardColors(
                    containerColor = IndigoDeep.copy(alpha = 0.2f)
                ),
                shape = RoundedCornerShape(12.dp)
            ) {
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(16.dp),
                    verticalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    Text(
                        "📅 Schedule Summary",
                        fontSize = 14.sp,
                        fontWeight = FontWeight.Bold,
                        color = Color.White
                    )

                    val paymentAmount = amount.toDoubleOrNull()
                    if (paymentAmount != null && paymentAmount > 0) {
                        Text(
                            "• $${String.format("%.2f", paymentAmount)} will be paid $selectedFrequency",
                            fontSize = 12.sp,
                            color = Color.White.copy(alpha = 0.9f),
                            lineHeight = 16.sp
                        )
                        Text(
                            "• Starts on $startDate",
                            fontSize = 12.sp,
                            color = Color.White.copy(alpha = 0.9f),
                            lineHeight = 16.sp
                        )
                        if (hasEndDate && endDate != null) {
                            Text(
                                "• Ends on $endDate",
                                fontSize = 12.sp,
                                color = Color.White.copy(alpha = 0.9f),
                                lineHeight = 16.sp
                            )
                        } else {
                            Text(
                                "• Continues until manually cancelled",
                                fontSize = 12.sp,
                                color = Color.White.copy(alpha = 0.9f),
                                lineHeight = 16.sp
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
                        val scheduleAmount = amount.toDoubleOrNull()
                        when {
                            scheduleAmount == null || scheduleAmount <= 0 -> {
                                showError = true
                                errorMessage = "Please enter a valid amount"
                            }
                            scheduleAmount > currentBalance -> {
                                showError = true
                                errorMessage = "Amount exceeds current balance"
                            }
                            else -> {
                                onSchedule(
                                    scheduleAmount,
                                    selectedFrequency,
                                    startDate,
                                    if (hasEndDate) endDate else null
                                )
                            }
                        }
                    },
                    modifier = Modifier.weight(1f),
                    enabled = amount.toDoubleOrNull()?.let { it > 0 } == true && !isLoading,
                    colors = ButtonDefaults.buttonColors(containerColor = IndigoDeep)
                ) {
                    if (isLoading) {
                        CircularProgressIndicator(
                            modifier = Modifier.size(20.dp),
                            color = Color.White,
                            strokeWidth = 2.dp
                        )
                    } else {
                        Text("Create Schedule")
                    }
                }
            }

            Spacer(modifier = Modifier.height(8.dp))
        }
    }
}

/**
 * Get today's date in YYYY-MM-DD format
 */
private fun getTodayDate(): String {
    val sdf = SimpleDateFormat("yyyy-MM-dd", Locale.getDefault())
    return sdf.format(Date())
}

