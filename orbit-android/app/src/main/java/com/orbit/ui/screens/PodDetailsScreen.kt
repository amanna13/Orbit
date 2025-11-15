package com.orbit.ui.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.orbit.ui.components.BarcodeScannerScreen
import com.orbit.ui.components.PodInfo
import com.orbit.ui.screens.ActionButton
import com.orbit.ui.screens.BottomSheetHandle
import com.orbit.ui.screens.DepositBottomSheet
import com.orbit.ui.screens.DistributeFundsBottomSheet
import com.orbit.ui.screens.ManageMembersBottomSheet
import com.orbit.ui.screens.PodDetailsTopBar
import com.orbit.ui.screens.QrCodeBottomSheet
import com.orbit.ui.screens.ScanPayBottomSheet
import com.orbit.ui.screens.SchedulePayoutBottomSheet
import com.orbit.ui.theme.Charcoal
import com.orbit.ui.theme.CustomRed
import com.orbit.ui.theme.DarkGray
import com.orbit.ui.theme.Poppins
import kotlinx.coroutines.launch

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun PodDetailsScreen(
    podInfo: PodInfo,
    onNavigateBack: () -> Unit = {},
    modifier: Modifier = Modifier
) {
    var showDepositSheet by remember { mutableStateOf(false) }
    var showScanPaySheet by remember { mutableStateOf(false) }
    var showScheduleSheet by remember { mutableStateOf(false) }
    var showDistributeSheet by remember { mutableStateOf(false) }
    var showMembersSheet by remember { mutableStateOf(false) }
    var showQrSheet by remember { mutableStateOf(false) }
    var showScanner by remember { mutableStateOf(false) }

    val sheetState = rememberModalBottomSheetState()
    val scope = rememberCoroutineScope()

    Box(
        modifier = modifier
            .fillMaxSize()
            .background(DarkGray)
    ) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .verticalScroll(rememberScrollState())
        ) {
            // Top Bar
            PodDetailsTopBar(onNavigateBack = onNavigateBack)

            // Pod Summary Card
            PodSummaryCard(
                podInfo = podInfo,
                onQrClick = { showQrSheet = true }
            )

            Spacer(modifier = Modifier.height(20.dp))

            // Actions Grid
            ActionsGrid(
                onDepositClick = { showDepositSheet = true },
                onScanPayClick = { showScanPaySheet = true },
                onScheduleClick = { showScheduleSheet = true },
                onDistributeClick = { showDistributeSheet = true },
                onMembersClick = { showMembersSheet = true },
                podColor = podInfo.colorTag
            )

            Spacer(modifier = Modifier.height(20.dp))
        }
    }

    // Bottom Sheets
    if (showDepositSheet) {
        DepositBottomSheet(
            podInfo = podInfo,
            sheetState = sheetState,
            onDismiss = { showDepositSheet = false },
            onDeposit = { amount ->
                // TODO: Wire backend call
                println("Depositing $amount to ${podInfo.podName}")
                scope.launch {
                    sheetState.hide()
                    showDepositSheet = false
                }
            }
        )
    }

    if (showScanPaySheet) {
        ScanPayBottomSheet(
            podInfo = podInfo,
            sheetState = sheetState,
            onDismiss = { showScanPaySheet = false },
            onScanClick = {
                showScanPaySheet = false
                showScanner = true
            }
        )
    }

    if (showScheduleSheet) {
        SchedulePayoutBottomSheet(
            podInfo = podInfo,
            sheetState = sheetState,
            onDismiss = { showScheduleSheet = false },
            onSchedule = { amount, interval, startDate, endDate ->
                // TODO: Wire backend Forte call
                println("Scheduling $amount $interval from $startDate to $endDate")
                scope.launch {
                    sheetState.hide()
                    showScheduleSheet = false
                }
            }
        )
    }

    if (showDistributeSheet) {
        DistributeFundsBottomSheet(
            podInfo = podInfo,
            sheetState = sheetState,
            onDismiss = { showDistributeSheet = false },
            onDistribute = { allocations ->
                // TODO: Wire backend call DistributePodFundsToMembers.cdc
                println("Distributing funds: $allocations")
                scope.launch {
                    sheetState.hide()
                    showDistributeSheet = false
                }
            }
        )
    }

    if (showMembersSheet) {
        ManageMembersBottomSheet(
            podInfo = podInfo,
            sheetState = sheetState,
            onDismiss = { showMembersSheet = false },
            onAddMember = { memberId ->
                // TODO: Wire backend call
                println("Adding member: $memberId")
            },
            onRemoveMember = { memberId ->
                // TODO: Wire backend call
                println("Removing member: $memberId")
            }
        )
    }

    if (showQrSheet) {
        QrCodeBottomSheet(
            podInfo = podInfo,
            sheetState = sheetState,
            onDismiss = { showQrSheet = false }
        )
    }

    // Scanner overlay
    if (showScanner) {
        BarcodeScannerScreen(
            onBarcodeScanned = { qrCode ->
                showScanner = false
                // TODO: Parse and process payment QR
                println("Scanned QR: $qrCode")
            },
            onClose = { showScanner = false }
        )
    }
}

@Composable
private fun PodDetailsTopBar(
    onNavigateBack: () -> Unit,
    modifier: Modifier = Modifier
) {
    Box(
        modifier = modifier
            .fillMaxWidth()
            .background(Charcoal)
            .statusBarsPadding()
            .padding(horizontal = 16.dp, vertical = 12.dp)
    ) {
        Row(
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(12.dp)
        ) {
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
                    tint = Color.White
                )
            }

            Text(
                text = "Pod Details",
                fontFamily = Poppins,
                fontSize = 20.sp,
                fontWeight = FontWeight.SemiBold,
                color = Color.White
            )
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun PodSummaryCard(
    podInfo: PodInfo,
    onQrClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    Card(
        modifier = modifier
            .fillMaxWidth()
            .padding(16.dp),
        shape = RoundedCornerShape(24.dp),
        elevation = CardDefaults.cardElevation(defaultElevation = 8.dp),
        colors = CardDefaults.cardColors(containerColor = Charcoal)
    ) {
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .background(
                    brush = Brush.verticalGradient(
                        colors = listOf(
                            podInfo.colorTag.copy(alpha = 0.5f),
                            podInfo.colorTag.copy(alpha = 0.2f),
                            Charcoal.copy(alpha = 0.95f)
                        ),
                        startY = 0f,
                        endY = 800f
                    )
                )
                .padding(24.dp)
        ) {
            Column(
                modifier = Modifier.fillMaxWidth(),
                verticalArrangement = Arrangement.spacedBy(16.dp)
            ) {
                // Header with emoji and QR button
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Box(
                        modifier = Modifier
                            .size(56.dp)
                            .clip(CircleShape)
                            .background(Color.White.copy(alpha = 0.15f)),
                        contentAlignment = Alignment.Center
                    ) {
                        Text(
                            text = podInfo.emoji,
                            fontSize = 28.sp
                        )
                    }

                    IconButton(
                        onClick = onQrClick,
                        modifier = Modifier
                            .size(48.dp)
                            .clip(CircleShape)
                            .background(CustomRed.copy(alpha = 0.9f))
                    ) {
                        Icon(
                            Icons.Filled.QrCode,
                            contentDescription = "Show QR Code",
                            tint = Color.White,
                            modifier = Modifier.size(24.dp)
                        )
                    }
                }

                // Pod Name
                Text(
                    text = podInfo.podName,
                    fontFamily = Poppins,
                    fontSize = 28.sp,
                    fontWeight = FontWeight.Bold,
                    color = Color.White
                )

                // Description (placeholder - can be added to PodInfo later)
                Text(
                    text = "Shared expenses and group payments made easy",
                    fontFamily = Poppins,
                    fontSize = 14.sp,
                    fontWeight = FontWeight.Normal,
                    color = Color.White.copy(alpha = 0.7f),
                    lineHeight = 20.sp
                )

                // Balance - Primary highlight
                Card(
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(16.dp),
                    colors = CardDefaults.cardColors(
                        containerColor = podInfo.colorTag.copy(alpha = 0.3f)
                    )
                ) {
                    Column(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(16.dp),
                        horizontalAlignment = Alignment.CenterHorizontally
                    ) {
                        Text(
                            text = "Pod Balance",
                            fontFamily = Poppins,
                            fontSize = 12.sp,
                            fontWeight = FontWeight.Medium,
                            color = Color.White.copy(alpha = 0.7f)
                        )
                        Spacer(modifier = Modifier.height(4.dp))
                        Text(
                            text = "${String.format("%.2f", podInfo.balance)} FLOW",
                            fontFamily = Poppins,
                            fontSize = 32.sp,
                            fontWeight = FontWeight.Bold,
                            color = Color.White
                        )
                    }
                }

                // Members and Creator info
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(12.dp)
                ) {
                    // Members count
                    Box(
                        modifier = Modifier
                            .weight(1f)
                            .clip(RoundedCornerShape(12.dp))
                            .background(Color.White.copy(alpha = 0.1f))
                            .padding(12.dp)
                    ) {
                        Row(
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.spacedBy(8.dp)
                        ) {
                            Icon(
                                Icons.Filled.Person,
                                contentDescription = "Members",
                                tint = podInfo.colorTag,
                                modifier = Modifier.size(20.dp)
                            )
                            Text(
                                text = "${podInfo.memberCount} members",
                                fontFamily = Poppins,
                                fontSize = 13.sp,
                                fontWeight = FontWeight.Medium,
                                color = Color.White
                            )
                        }
                    }

                }
            }
        }
    }
}

@Composable
private fun ActionsGrid(
    onDepositClick: () -> Unit,
    onScanPayClick: () -> Unit,
    onScheduleClick: () -> Unit,
    onDistributeClick: () -> Unit,
    onMembersClick: () -> Unit,
    podColor: Color,
    modifier: Modifier = Modifier
) {
    Card(
        modifier = modifier
            .fillMaxWidth()
            .padding(horizontal = 16.dp),
        shape = RoundedCornerShape(24.dp),
        elevation = CardDefaults.cardElevation(defaultElevation = 8.dp),
        colors = CardDefaults.cardColors(containerColor = Charcoal)
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(20.dp),
            verticalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            Text(
                text = "Actions",
                fontFamily = Poppins,
                fontSize = 18.sp,
                fontWeight = FontWeight.SemiBold,
                color = Color.White,
                modifier = Modifier.padding(bottom = 8.dp)
            )

            // Row 1
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                ActionButton(
                    icon = Icons.Filled.AddCircle,
                    label = "Deposit",
                    onClick = onDepositClick,
                    color = podColor,
                    modifier = Modifier.weight(1f)
                )
                ActionButton(
                    icon = Icons.Filled.QrCodeScanner,
                    label = "Scan & Pay",
                    onClick = onScanPayClick,
                    color = podColor,
                    modifier = Modifier.weight(1f)
                )
            }

            // Row 2
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                ActionButton(
                    icon = Icons.Filled.CalendarMonth,
                    label = "Schedule",
                    onClick = onScheduleClick,
                    color = podColor,
                    modifier = Modifier.weight(1f)
                )
                ActionButton(
                    icon = Icons.Filled.Share,
                    label = "Distribute",
                    onClick = onDistributeClick,
                    color = podColor,
                    modifier = Modifier.weight(1f)
                )
            }

            // Row 3
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                ActionButton(
                    icon = Icons.Filled.Group,
                    label = "Members",
                    onClick = onMembersClick,
                    color = podColor,
                    modifier = Modifier.weight(1f)
                )
                // Empty space for symmetry
                Spacer(modifier = Modifier.weight(1f))
            }
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun ActionButton(
    icon: ImageVector,
    label: String,
    onClick: () -> Unit,
    color: Color,
    modifier: Modifier = Modifier
) {
    Card(
        onClick = onClick,
        modifier = modifier.height(100.dp),
        shape = RoundedCornerShape(50.dp),
        elevation = CardDefaults.cardElevation(defaultElevation = 8.dp),
        colors = CardDefaults.cardColors(containerColor = Charcoal)

    ) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(12.dp),
            verticalArrangement = Arrangement.Center,
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Icon(
                imageVector = icon,
                contentDescription = label,
                tint = color,
                modifier = Modifier.size(32.dp)
            )
            Spacer(modifier = Modifier.height(8.dp))
            Text(
                text = label,
                fontFamily = Poppins,
                fontSize = 13.sp,
                fontWeight = FontWeight.Medium,
                color = Color.White,
                textAlign = TextAlign.Center
            )
        }
    }
}

// Bottom Sheets Implementation
@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun DepositBottomSheet(
    podInfo: PodInfo,
    sheetState: SheetState,
    onDismiss: () -> Unit,
    onDeposit: (Double) -> Unit
) {
    var amount by remember { mutableStateOf("") }

    ModalBottomSheet(
        onDismissRequest = onDismiss,
        sheetState = sheetState,
        containerColor = Charcoal,
        dragHandle = { BottomSheetHandle() }
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(24.dp),
            verticalArrangement = Arrangement.spacedBy(20.dp)
        ) {
            Text(
                text = "💰 Deposit Funds",
                fontFamily = Poppins,
                fontSize = 24.sp,
                fontWeight = FontWeight.Bold,
                color = Color.White
            )

            Text(
                text = "Add funds to ${podInfo.podName}",
                fontFamily = Poppins,
                fontSize = 14.sp,
                fontWeight = FontWeight.Normal,
                color = Color.White.copy(alpha = 0.7f)
            )

            OutlinedTextField(
                value = amount,
                onValueChange = { amount = it },
                label = { Text("Enter amount") },
                placeholder = { Text("₹0.00") },
                modifier = Modifier.fillMaxWidth(),
                colors = OutlinedTextFieldDefaults.colors(
                    focusedBorderColor = podInfo.colorTag,
                    unfocusedBorderColor = Color.White.copy(alpha = 0.3f),
                    focusedLabelColor = podInfo.colorTag,
                    unfocusedLabelColor = Color.White.copy(alpha = 0.7f),
                    focusedTextColor = Color.White,
                    unfocusedTextColor = Color.White
                )
            )

            Button(
                onClick = {
                    amount.toDoubleOrNull()?.let { onDeposit(it) }
                },
                modifier = Modifier
                    .fillMaxWidth()
                    .height(50.dp),
                colors = ButtonDefaults.buttonColors(
                    containerColor = CustomRed
                ),
                shape = RoundedCornerShape(12.dp),
                enabled = amount.toDoubleOrNull() != null && amount.toDoubleOrNull()!! > 0
            ) {
                Text(
                    text = "Deposit Now",
                    fontFamily = Poppins,
                    fontSize = 16.sp,
                    fontWeight = FontWeight.SemiBold
                )
            }

            Spacer(modifier = Modifier.height(20.dp))
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun ScanPayBottomSheet(
    podInfo: PodInfo,
    sheetState: SheetState,
    onDismiss: () -> Unit,
    onScanClick: () -> Unit
) {
    ModalBottomSheet(
        onDismissRequest = onDismiss,
        sheetState = sheetState,
        containerColor = Charcoal,
        dragHandle = { BottomSheetHandle() }
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(24.dp),
            verticalArrangement = Arrangement.spacedBy(20.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Text(
                text = "Scan & Pay",
                fontFamily = Poppins,
                fontSize = 24.sp,
                fontWeight = FontWeight.Bold,
                color = Color.White
            )

            Text(
                text = "Scan another pod's QR code to transfer funds instantly",
                fontFamily = Poppins,
                fontSize = 14.sp,
                fontWeight = FontWeight.Normal,
                color = Color.White.copy(alpha = 0.7f),
                textAlign = TextAlign.Center
            )

            Box(
                modifier = Modifier
                    .size(120.dp)
                    .clip(RoundedCornerShape(20.dp))
                    .background(podInfo.colorTag.copy(alpha = 0.2f)),
                contentAlignment = Alignment.Center
            ) {
                Icon(
                    Icons.Filled.QrCodeScanner,
                    contentDescription = "Scanner",
                    tint = podInfo.colorTag,
                    modifier = Modifier.size(60.dp)
                )
            }

            Button(
                onClick = onScanClick,
                modifier = Modifier
                    .fillMaxWidth()
                    .height(50.dp),
                colors = ButtonDefaults.buttonColors(
                    containerColor = CustomRed
                ),
                shape = RoundedCornerShape(12.dp)
            ) {
                Text(
                    text = "Open Scanner",
                    fontFamily = Poppins,
                    fontSize = 16.sp,
                    fontWeight = FontWeight.SemiBold
                )
            }

            Spacer(modifier = Modifier.height(20.dp))
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun SchedulePayoutBottomSheet(
    podInfo: PodInfo,
    sheetState: SheetState,
    onDismiss: () -> Unit,
    onSchedule: (amount: Double, interval: String, startDate: String?, endDate: String?) -> Unit
) {
    var amount by remember { mutableStateOf("") }
    var selectedInterval by remember { mutableStateOf("Weekly") }
    val intervals = listOf("Daily", "Weekly", "Monthly")

    ModalBottomSheet(
        onDismissRequest = onDismiss,
        sheetState = sheetState,
        containerColor = Charcoal,
        dragHandle = { BottomSheetHandle() }
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(24.dp)
                .verticalScroll(rememberScrollState()),
            verticalArrangement = Arrangement.spacedBy(20.dp)
        ) {
            Text(
                text = "Schedule Payout",
                fontFamily = Poppins,
                fontSize = 24.sp,
                fontWeight = FontWeight.Bold,
                color = Color.White
            )

            Text(
                text = "Set up automatic distributions from ${podInfo.podName}",
                fontFamily = Poppins,
                fontSize = 14.sp,
                fontWeight = FontWeight.Normal,
                color = Color.White.copy(alpha = 0.7f)
            )

            OutlinedTextField(
                value = amount,
                onValueChange = { amount = it },
                label = { Text("Amount to distribute") },
                placeholder = { Text("₹0.00") },
                modifier = Modifier.fillMaxWidth(),
                colors = OutlinedTextFieldDefaults.colors(
                    focusedBorderColor = podInfo.colorTag,
                    unfocusedBorderColor = Color.White.copy(alpha = 0.3f),
                    focusedLabelColor = podInfo.colorTag,
                    unfocusedLabelColor = Color.White.copy(alpha = 0.7f),
                    focusedTextColor = Color.White,
                    unfocusedTextColor = Color.White
                )
            )

            // Interval selector
            Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                Text(
                    text = "Frequency",
                    fontFamily = Poppins,
                    fontSize = 14.sp,
                    fontWeight = FontWeight.Medium,
                    color = Color.White.copy(alpha = 0.7f)
                )

                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    intervals.forEach { interval ->
                        FilterChip(
                            selected = selectedInterval == interval,
                            onClick = { selectedInterval = interval },
                            label = {
                                Text(
                                    text = interval,
                                    fontFamily = Poppins,
                                    fontSize = 13.sp
                                )
                            },
                            modifier = Modifier.weight(1f),
                            colors = FilterChipDefaults.filterChipColors(
                                selectedContainerColor = podInfo.colorTag.copy(alpha = 0.3f),
                                selectedLabelColor = Color.White,
                                containerColor = Color.White.copy(alpha = 0.1f),
                                labelColor = Color.White.copy(alpha = 0.7f)
                            )
                        )
                    }
                }
            }

            Button(
                onClick = {
                    amount.toDoubleOrNull()?.let {
                        onSchedule(it, selectedInterval, null, null)
                    }
                },
                modifier = Modifier
                    .fillMaxWidth()
                    .height(50.dp),
                colors = ButtonDefaults.buttonColors(
                    containerColor = CustomRed
                ),
                shape = RoundedCornerShape(12.dp),
                enabled = amount.toDoubleOrNull() != null && amount.toDoubleOrNull()!! > 0
            ) {
                Text(
                    text = "Schedule Payout",
                    fontFamily = Poppins,
                    fontSize = 16.sp,
                    fontWeight = FontWeight.SemiBold
                )
            }

            Spacer(modifier = Modifier.height(20.dp))
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun DistributeFundsBottomSheet(
    podInfo: PodInfo,
    sheetState: SheetState,
    onDismiss: () -> Unit,
    onDistribute: (Map<String, Float>) -> Unit
) {
    // Mock members data
    val members = remember {
        listOf(
            "Member 1" to 0.25f,
            "Member 2" to 0.25f,
            "Member 3" to 0.25f,
            "Member 4" to 0.25f
        )
    }
    val allocations = remember { mutableStateMapOf<String, Float>().apply {
        members.forEach { (name, allocation) -> this[name] = allocation }
    }}

    ModalBottomSheet(
        onDismissRequest = onDismiss,
        sheetState = sheetState,
        containerColor = Charcoal,
        dragHandle = { BottomSheetHandle() }
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(24.dp)
                .verticalScroll(rememberScrollState()),
            verticalArrangement = Arrangement.spacedBy(20.dp)
        ) {
            Text(
                text = "Distribute Funds",
                fontFamily = Poppins,
                fontSize = 24.sp,
                fontWeight = FontWeight.Bold,
                color = Color.White
            )

            Text(
                text = "Allocate ${podInfo.podName}'s funds to members",
                fontFamily = Poppins,
                fontSize = 14.sp,
                fontWeight = FontWeight.Normal,
                color = Color.White.copy(alpha = 0.7f)
            )

            // Member allocations
            members.forEach { (memberName, _) ->
                Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween
                    ) {
                        Text(
                            text = memberName,
                            fontFamily = Poppins,
                            fontSize = 14.sp,
                            fontWeight = FontWeight.Medium,
                            color = Color.White
                        )
                        Text(
                            text = "${(allocations[memberName] ?: 0f) * 100}%",
                            fontFamily = Poppins,
                            fontSize = 14.sp,
                            fontWeight = FontWeight.SemiBold,
                            color = podInfo.colorTag
                        )
                    }
                    Slider(
                        value = allocations[memberName] ?: 0f,
                        onValueChange = { allocations[memberName] = it },
                        modifier = Modifier.fillMaxWidth(),
                        colors = SliderDefaults.colors(
                            thumbColor = podInfo.colorTag,
                            activeTrackColor = podInfo.colorTag,
                            inactiveTrackColor = Color.White.copy(alpha = 0.2f)
                        )
                    )
                }
            }

            Button(
                onClick = { onDistribute(allocations) },
                modifier = Modifier
                    .fillMaxWidth()
                    .height(50.dp),
                colors = ButtonDefaults.buttonColors(
                    containerColor = CustomRed
                ),
                shape = RoundedCornerShape(12.dp)
            ) {
                Text(
                    text = "Distribute Now",
                    fontFamily = Poppins,
                    fontSize = 16.sp,
                    fontWeight = FontWeight.SemiBold
                )
            }

            Spacer(modifier = Modifier.height(20.dp))
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun ManageMembersBottomSheet(
    podInfo: PodInfo,
    sheetState: SheetState,
    onDismiss: () -> Unit,
    onAddMember: (String) -> Unit,
    onRemoveMember: (String) -> Unit
) {
    var newMemberId by remember { mutableStateOf("") }

    // Mock members
    val members = remember {
        listOf("Alice", "Bob", "Charlie", "David", "Eve")
    }

    ModalBottomSheet(
        onDismissRequest = onDismiss,
        sheetState = sheetState,
        containerColor = Charcoal,
        dragHandle = { BottomSheetHandle() }
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(24.dp)
                .verticalScroll(rememberScrollState()),
            verticalArrangement = Arrangement.spacedBy(20.dp)
        ) {
            Text(
                text = "Manage Members",
                fontFamily = Poppins,
                fontSize = 24.sp,
                fontWeight = FontWeight.Bold,
                color = Color.White
            )

            Text(
                text = "${podInfo.memberCount} members in ${podInfo.podName}",
                fontFamily = Poppins,
                fontSize = 14.sp,
                fontWeight = FontWeight.Normal,
                color = Color.White.copy(alpha = 0.7f)
            )

            // Add member section
            OutlinedTextField(
                value = newMemberId,
                onValueChange = { newMemberId = it },
                label = { Text("Add member by ID") },
                placeholder = { Text("Enter member ID") },
                modifier = Modifier.fillMaxWidth(),
                trailingIcon = {
                    IconButton(
                        onClick = {
                            if (newMemberId.isNotBlank()) {
                                onAddMember(newMemberId)
                                newMemberId = ""
                            }
                        }
                    ) {
                        Icon(Icons.Filled.Add, contentDescription = "Add", tint = podInfo.colorTag)
                    }
                },
                colors = OutlinedTextFieldDefaults.colors(
                    focusedBorderColor = podInfo.colorTag,
                    unfocusedBorderColor = Color.White.copy(alpha = 0.3f),
                    focusedLabelColor = podInfo.colorTag,
                    unfocusedLabelColor = Color.White.copy(alpha = 0.7f),
                    focusedTextColor = Color.White,
                    unfocusedTextColor = Color.White
                )
            )

            // Members list
            Text(
                text = "Current Members",
                fontFamily = Poppins,
                fontSize = 16.sp,
                fontWeight = FontWeight.SemiBold,
                color = Color.White
            )

            members.forEach { member ->
                Card(
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(12.dp),
                    colors = CardDefaults.cardColors(
                        containerColor = Color.White.copy(alpha = 0.1f)
                    )
                ) {
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(16.dp),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Row(
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.spacedBy(12.dp)
                        ) {
                            Box(
                                modifier = Modifier
                                    .size(40.dp)
                                    .clip(CircleShape)
                                    .background(podInfo.colorTag.copy(alpha = 0.3f)),
                                contentAlignment = Alignment.Center
                            ) {
                                Icon(
                                    Icons.Filled.Person,
                                    contentDescription = "Member",
                                    tint = Color.White,
                                    modifier = Modifier.size(20.dp)
                                )
                            }
                            Text(
                                text = member,
                                fontFamily = Poppins,
                                fontSize = 15.sp,
                                fontWeight = FontWeight.Medium,
                                color = Color.White
                            )
                        }

                        IconButton(onClick = { onRemoveMember(member) }) {
                            Icon(
                                Icons.Filled.Close,
                                contentDescription = "Remove",
                                tint = CustomRed,
                                modifier = Modifier.size(20.dp)
                            )
                        }
                    }
                }
            }

            Spacer(modifier = Modifier.height(20.dp))
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun QrCodeBottomSheet(
    podInfo: PodInfo,
    sheetState: SheetState,
    onDismiss: () -> Unit
) {
    ModalBottomSheet(
        onDismissRequest = onDismiss,
        sheetState = sheetState,
        containerColor = Charcoal,
        dragHandle = { BottomSheetHandle() }
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(24.dp),
            verticalArrangement = Arrangement.spacedBy(20.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Text(
                text = "Share Pod QR",
                fontFamily = Poppins,
                fontSize = 24.sp,
                fontWeight = FontWeight.Bold,
                color = Color.White
            )

            Text(
                text = "Let others scan this to join ${podInfo.podName}",
                fontFamily = Poppins,
                fontSize = 14.sp,
                fontWeight = FontWeight.Normal,
                color = Color.White.copy(alpha = 0.7f),
                textAlign = TextAlign.Center
            )

            // QR Code placeholder
            Box(
                modifier = Modifier
                    .size(250.dp)
                    .clip(RoundedCornerShape(20.dp))
                    .background(Color.White),
                contentAlignment = Alignment.Center
            ) {
                // TODO: Generate actual QR code with podInfo.podId
                Column(
                    horizontalAlignment = Alignment.CenterHorizontally,
                    verticalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    Icon(
                        Icons.Filled.QrCode,
                        contentDescription = "QR Code",
                        tint = Color.Black,
                        modifier = Modifier.size(120.dp)
                    )
                    Text(
                        text = podInfo.podId,
                        fontFamily = Poppins,
                        fontSize = 12.sp,
                        fontWeight = FontWeight.Medium,
                        color = Color.Black
                    )
                }
            }

            Text(
                text = "Pod ID: ${podInfo.podId}",
                fontFamily = Poppins,
                fontSize = 13.sp,
                fontWeight = FontWeight.Medium,
                color = Color.White.copy(alpha = 0.7f)
            )

            Spacer(modifier = Modifier.height(20.dp))
        }
    }
}

@Composable
private fun BottomSheetHandle() {
    Box(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 12.dp),
        contentAlignment = Alignment.Center
    ) {
        Box(
            modifier = Modifier
                .width(40.dp)
                .height(4.dp)
                .clip(RoundedCornerShape(2.dp))
                .background(Color.White.copy(alpha = 0.3f))
        )
    }
}

