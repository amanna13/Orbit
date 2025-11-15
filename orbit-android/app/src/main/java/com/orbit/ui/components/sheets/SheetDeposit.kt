package com.orbit.ui.components.sheets

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.AccountBalance
import androidx.compose.material.icons.filled.AttachMoney
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
import com.orbit.ui.theme.Poppins

/**
 * Deposit Bottom Sheet
 * Allows user to deposit funds to pod
 *
 * Backend API: POST /api/flow/pods/deposit (when ready)
 * For now: Simulates deposit with amount input
 */
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun SheetDeposit(
    podName: String,
    currentBalance: Double,
    onDismiss: () -> Unit,
    onDeposit: (amount: Double) -> Unit,
    isLoading: Boolean = false
) {
    var amount by remember { mutableStateOf("") }
    var showError by remember { mutableStateOf(false) }
    var errorMessage by remember { mutableStateOf("") }

    // Validate amount
    val isAmountValid = remember(amount) {
        amount.toDoubleOrNull()?.let { it > 0 } ?: false
    }

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
                        "Deposit Funds",
                        fontSize = 24.sp,
                        fontWeight = FontWeight.Bold,
                        color = Color.White,
                        fontFamily = Poppins
                    )
                    Text(
                        "Add money to $podName",
                        fontSize = 14.sp,
                        color = Color.White.copy(alpha = 0.7f),
                        fontFamily = Poppins
                    )
                }

                Icon(
                    Icons.Filled.AccountBalance,
                    contentDescription = null,
                    tint = CustomRed,
                    modifier = Modifier.size(32.dp)
                )
            }

            // Current Balance Card
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
                            "Current Balance",
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
                    }

                    // Preview new balance
                    if (isAmountValid) {
                        Column(horizontalAlignment = Alignment.End) {
                            Text(
                                "New Balance",
                                fontSize = 12.sp,
                                color = Color.White.copy(alpha = 0.7f)
                            )
                            Text(
                                "$${String.format("%.2f", currentBalance + amount.toDouble())}",
                                fontSize = 20.sp,
                                fontWeight = FontWeight.Bold,
                                color = Color.White,
                                fontFamily = Poppins
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
                label = { Text("Amount to Deposit") },
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
                        Text(
                            errorMessage,
                            color = MaterialTheme.colorScheme.error
                        )
                    } else {
                        Text("Enter amount in USD")
                    }
                },
                colors = OutlinedTextFieldDefaults.colors(
                    focusedBorderColor = CustomRed,
                    focusedLabelColor = CustomRed,
                    cursorColor = CustomRed
                )
            )

            // Quick Amount Buttons
            Text(
                "Quick Select",
                fontSize = 14.sp,
                fontWeight = FontWeight.Medium,
                color = Color.White.copy(alpha = 0.7f)
            )

            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                listOf(10.0, 50.0, 100.0, 500.0).forEach { quickAmount ->
                    OutlinedButton(
                        onClick = { amount = quickAmount.toString() },
                        modifier = Modifier.weight(1f),
                        colors = ButtonDefaults.outlinedButtonColors(
                            containerColor = if (amount == quickAmount.toString())
                                CustomRed.copy(alpha = 0.2f)
                            else Color.Transparent
                        ),
                        border = ButtonDefaults.outlinedButtonBorder.copy(
                            width = 1.dp,
                            brush = androidx.compose.ui.graphics.SolidColor(
                                if (amount == quickAmount.toString()) CustomRed else Color.White.copy(alpha = 0.3f)
                            )
                        )
                    ) {
                        Text(
                            "$${quickAmount.toInt()}",
                            fontSize = 12.sp,
                            color = Color.White
                        )
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
                        val depositAmount = amount.toDoubleOrNull()
                        when {
                            depositAmount == null || depositAmount <= 0 -> {
                                showError = true
                                errorMessage = "Please enter a valid amount"
                            }
                            depositAmount > 10000 -> {
                                showError = true
                                errorMessage = "Maximum deposit is $10,000"
                            }
                            else -> {
                                onDeposit(depositAmount)
                            }
                        }
                    },
                    modifier = Modifier.weight(1f),
                    enabled = isAmountValid && !isLoading,
                    colors = ButtonDefaults.buttonColors(
                        containerColor = CustomRed
                    )
                ) {
                    if (isLoading) {
                        CircularProgressIndicator(
                            modifier = Modifier.size(20.dp),
                            color = Color.White,
                            strokeWidth = 2.dp
                        )
                    } else {
                        Text("Deposit")
                    }
                }
            }

            // Info Text
            Card(
                modifier = Modifier.fillMaxWidth(),
                colors = CardDefaults.cardColors(
                    containerColor = Color.White.copy(alpha = 0.05f)
                ),
                shape = RoundedCornerShape(8.dp)
            ) {
                Text(
                    "💡 Funds will be added to the pod balance and can be used for payments and distributions.",
                    fontSize = 12.sp,
                    color = Color.White.copy(alpha = 0.6f),
                    modifier = Modifier.padding(12.dp),
                    lineHeight = 16.sp
                )
            }

            Spacer(modifier = Modifier.height(8.dp))
        }
    }
}

