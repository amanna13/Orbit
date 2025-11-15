package com.orbit.ui.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Color.Companion.DarkGray
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.navigation.compose.hiltViewModel
import com.orbit.data.models.ErrorType
import com.orbit.data.models.Member
import com.orbit.data.models.Pod
import com.orbit.data.models.Resource
import com.orbit.data.models.TransactionItem
import com.orbit.ui.components.sheets.SheetDeposit
import com.orbit.ui.components.sheets.SheetDistribute
import com.orbit.ui.components.sheets.SheetScanPay
import com.orbit.ui.components.sheets.SheetSchedulePayout
import com.orbit.ui.theme.Charcoal
import com.orbit.ui.theme.CustomRed
import com.orbit.ui.theme.CyanBlue
import com.orbit.ui.theme.IndigoDeep
import com.orbit.ui.theme.Gatians
import com.orbit.ui.theme.Poppins
import com.orbit.viewmodel.PodDetailViewModel
import com.orbit.viewmodel.PodListViewModel

/**
 * Pod Detail Screen
 * Shows comprehensive pod information and actions
 */
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun PodDetailScreen(
    podId: Long,
    onNavigateBack: () -> Unit,
    onLeavePod: () -> Unit = {},
    viewModel: PodDetailViewModel = hiltViewModel(),
    podListViewModel: PodListViewModel = hiltViewModel()
) {
    // Get wallet address from PodListViewModel
    val walletAddress by podListViewModel.walletAddress.collectAsState()

    // Set wallet address in PodDetailViewModel when it becomes available
    LaunchedEffect(walletAddress) {
        walletAddress?.let { address ->
            viewModel.setWalletAddress(address)
        }
    }

    // Load pod details on first composition
    LaunchedEffect(podId) {
        viewModel.loadPodDetails(podId)
    }

    // Collect states
    val podState by viewModel.podState.collectAsState()
    val membersState by viewModel.membersState.collectAsState()
    val activityState by viewModel.activityState.collectAsState()

    // Bottom sheet states
    val showDepositSheet by viewModel.showDepositSheet.collectAsState()
    val showScanPaySheet by viewModel.showScanPaySheet.collectAsState()
    val showScheduleSheet by viewModel.showScheduleSheet.collectAsState()
    val showDistributeSheet by viewModel.showDistributeSheet.collectAsState()
    val showLeaveSheet by viewModel.showLeaveSheet.collectAsState()

    // Loading states for actions
    val depositLoading by viewModel.depositLoading.collectAsState()
    val scanPayLoading by viewModel.scanPayLoading.collectAsState()
    val scheduleLoading by viewModel.scheduleLoading.collectAsState()
    val distributeLoading by viewModel.distributeLoading.collectAsState()

    Scaffold(
        topBar = {
            TopAppBar(
                title = {
                    Text(
                        when (podState) {
                            is Resource.Success -> (podState as Resource.Success).data?.name ?: "Pod Details"
                            else -> "Pod Details"
                        },
                        fontFamily = Poppins,
                        fontWeight = FontWeight.Medium
                    )
                },
                navigationIcon = {
                    IconButton(onClick = onNavigateBack) {
                        Icon(Icons.Filled.ArrowBack, "Back")
                    }
                },
                actions = {
                    IconButton(onClick = { viewModel.refresh() }) {
                        Icon(Icons.Filled.Refresh, "Refresh")
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = Charcoal,
                    titleContentColor = Color.White,
                    navigationIconContentColor = Color.White,
                    actionIconContentColor = Color.White
                )
            )
        },
        containerColor = com.orbit.ui.theme.DarkGray
    ) { padding ->
        Box(
            modifier = Modifier
                .fillMaxSize()
                .padding(padding)
        ) {
            when (podState) {
                is Resource.Loading -> {
                    PodDetailLoadingState()
                }
                is Resource.Success -> {
                    val pod = (podState as Resource.Success).data
                    if (pod != null) {
                        PodDetailContent(
                            pod = pod,
                            membersState = membersState,
                            activityState = activityState,
                            onDeposit = { viewModel.showDepositSheet() },
                            onScanPay = { viewModel.showScanPaySheet() },
                            onSchedule = { viewModel.showScheduleSheet() },
                            onDistribute = { viewModel.showDistributeSheet() },
                            onInvite = { /* TODO */ },
                            onLeave = { viewModel.showLeaveSheet() }
                        )
                    }
                }
                is Resource.Error -> {
                    ErrorState(
                        message = (podState as Resource.Error).message ?: "Failed to load pod",
                        onRetry = { viewModel.refresh() }
                    )
                }
            }
        }
    }

    // Bottom Sheets - Using comprehensive implementations from sheets package
    val pod = (podState as? Resource.Success)?.data

    if (showDepositSheet && pod != null) {
        SheetDeposit(
            podName = pod.name,
            currentBalance = pod.balance,
            onDismiss = { viewModel.hideDepositSheet() },
            onDeposit = { amount -> viewModel.deposit(amount) },
            isLoading = depositLoading
        )
    }

    if (showScanPaySheet && pod != null) {
        SheetScanPay(
            podId = pod.id,
            podName = pod.name,
            currentBalance = pod.balance,
            onDismiss = { viewModel.hideScanPaySheet() },
            onPay = { qrCode, amount -> viewModel.scanAndPay(qrCode, amount) },
            isLoading = scanPayLoading
        )
    }

    if (showScheduleSheet && pod != null) {
        SheetSchedulePayout(
            podId = pod.id,
            podName = pod.name,
            currentBalance = pod.balance,
            onDismiss = { viewModel.hideScheduleSheet() },
            onSchedule = { amount, freq, start, end ->
                viewModel.schedulePayout(amount, freq, start, end)
            },
            isLoading = scheduleLoading
        )
    }

    if (showDistributeSheet && pod != null) {
        SheetDistribute(
            podId = pod.id,
            podName = pod.name,
            currentBalance = pod.balance,
            membersState = membersState,
            onDismiss = { viewModel.hideDistributeSheet() },
            onDistribute = { mode, customAmounts ->
                viewModel.distributeNow(mode, customAmounts)
            },
            isLoading = distributeLoading
        )
    }

    if (showLeaveSheet) {
        LeavePodBottomSheet(
            onDismiss = { viewModel.hideLeaveSheet() },
            onLeave = {
                viewModel.leavePod(onSuccess = onLeavePod)
            }
        )
    }
}

/**
 * Main content for pod details
 */
@Composable
fun PodDetailContent(
    pod: Pod,
    membersState: Resource<List<Member>>,
    activityState: Resource<List<TransactionItem>>,
    onDeposit: () -> Unit,
    onScanPay: () -> Unit,
    onSchedule: () -> Unit,
    onDistribute: () -> Unit,
    onInvite: () -> Unit,
    onLeave: () -> Unit,
    modifier: Modifier = Modifier
) {
    LazyColumn(
        modifier = modifier.fillMaxSize(),
        contentPadding = PaddingValues(16.dp),
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        // Summary Card
        item {
            PodSummaryCard(pod)
        }

        // Members Section
        item {
            MembersSection(membersState)
        }

        // Actions Grid
        item {
            ActionsGrid(
                onDeposit = onDeposit,
                onScanPay = onScanPay,
                onSchedule = onSchedule,
                onDistribute = onDistribute,
                onInvite = onInvite,
                onLeave = onLeave
            )
        }

        // Activity Feed
        item {
            ActivitySection(activityState)
        }
    }
}

/**
 * Summary card showing pod details
 */
@Composable
fun PodSummaryCard(pod: Pod, modifier: Modifier = Modifier) {
    Card(
        modifier = modifier.fillMaxWidth(),
        shape = RoundedCornerShape(20.dp),
        colors = CardDefaults.cardColors(containerColor = Charcoal)
    ) {
        Column(
            modifier = Modifier.padding(20.dp),
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            // Pod Name & Emoji
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(12.dp)
                ) {
                    Text(
                        pod.emoji ?: "😊",
                        fontSize = 40.sp
                    )
                    Column {
                        Text(
                            pod.name,
                            fontSize = 24.sp,
                            fontWeight = FontWeight.Bold,
                            color = Color.White,
                            fontFamily = Gatians
                        )
                        pod.description?.let {
                            Text(
                                it,
                                fontSize = 14.sp,
                                color = Color.White.copy(alpha = 0.7f),
                                fontFamily = Poppins
                            )
                        }
                    }
                }
            }

            HorizontalDivider(color = Color.White.copy(alpha = 0.1f))

            // Balance
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Column {
                    Text(
                        "Total Balance",
                        fontSize = 14.sp,
                        color = Color.White.copy(alpha = 0.7f),
                        fontFamily = Poppins
                    )
                    Text(
                        "$${String.format("%.2f", pod.balance)}",
                        fontSize = 32.sp,
                        fontWeight = FontWeight.Bold,
                        color = CustomRed,
                        fontFamily = Gatians
                    )
                }

                // Stats
                Row(
                    horizontalArrangement = Arrangement.spacedBy(16.dp)
                ) {
                    StatItem(
                        icon = Icons.Filled.Person,
                        value = pod.memberCount.toString(),
                        label = "Members"
                    )
                }
            }

            // Join Code (if available)
            pod.joinCode?.let { code ->
                Card(
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(12.dp),
                    colors = CardDefaults.cardColors(
                        containerColor = CustomRed.copy(alpha = 0.1f)
                    )
                ) {
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(12.dp),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Column {
                            Text(
                                "Join Code",
                                fontSize = 12.sp,
                                color = Color.White.copy(alpha = 0.7f)
                            )
                            Text(
                                code,
                                fontSize = 18.sp,
                                fontWeight = FontWeight.Bold,
                                color = CustomRed,
                                fontFamily = Poppins
                            )
                        }
                        Icon(
                            Icons.Filled.Share,
                            contentDescription = "Share",
                            tint = CustomRed
                        )
                    }
                }
            }
        }
    }
}

/**
 * Stat item for summary card
 */
@Composable
fun StatItem(
    icon: ImageVector,
    value: String,
    label: String,
    modifier: Modifier = Modifier
) {
    Column(
        modifier = modifier,
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.spacedBy(4.dp)
    ) {
        Icon(
            icon,
            contentDescription = label,
            tint = Color.White.copy(alpha = 0.7f),
            modifier = Modifier.size(24.dp)
        )
        Text(
            value,
            fontSize = 20.sp,
            fontWeight = FontWeight.Bold,
            color = Color.White
        )
        Text(
            label,
            fontSize = 12.sp,
            color = Color.White.copy(alpha = 0.7f)
        )
    }
}

/**
 * Members section showing horizontal list of members
 */
@Composable
fun MembersSection(
    membersState: Resource<List<Member>>,
    modifier: Modifier = Modifier
) {
    Column(
        modifier = modifier.fillMaxWidth(),
        verticalArrangement = Arrangement.spacedBy(12.dp)
    ) {
        Text(
            "Members",
            fontSize = 20.sp,
            fontWeight = FontWeight.Bold,
            color = Color.White,
            fontFamily = Gatians
        )

        when (membersState) {
            is Resource.Loading -> {
                CircularProgressIndicator(
                    modifier = Modifier.size(24.dp),
                    color = CustomRed
                )
            }
            is Resource.Success -> {
                val members = membersState.data ?: emptyList()
                if (members.isEmpty()) {
                    Text(
                        "No members yet",
                        color = Color.White.copy(alpha = 0.5f),
                        fontSize = 14.sp
                    )
                } else {
                    LazyRow(
                        horizontalArrangement = Arrangement.spacedBy(12.dp)
                    ) {
                        items(members) { member ->
                            MemberCard(member)
                        }
                    }
                }
            }
            is Resource.Error -> {
                Text(
                    "Failed to load members",
                    color = Color.White.copy(alpha = 0.5f),
                    fontSize = 14.sp
                )
            }
        }
    }
}

/**
 * Member card item
 */
@Composable
fun MemberCard(member: Member, modifier: Modifier = Modifier) {
    Card(
        modifier = modifier.width(120.dp),
        shape = RoundedCornerShape(16.dp),
        colors = CardDefaults.cardColors(containerColor = Charcoal)
    ) {
        Column(
            modifier = Modifier.padding(12.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            // Avatar
            Box(
                modifier = Modifier
                    .size(48.dp)
                    .clip(CircleShape)
                    .background(CustomRed.copy(alpha = 0.2f))
                    .border(2.dp, CustomRed.copy(alpha = 0.5f), CircleShape),
                contentAlignment = Alignment.Center
            ) {
                Text(
                    member.displayName?.firstOrNull()?.uppercase() ?: "?",
                    fontSize = 20.sp,
                    fontWeight = FontWeight.Bold,
                    color = CustomRed
                )
            }

            // Name
            Text(
                member.displayName ?: "Unknown",
                fontSize = 12.sp,
                fontWeight = FontWeight.Medium,
                color = Color.White,
                textAlign = TextAlign.Center,
                maxLines = 1
            )

            // Role badge
            Text(
                member.role,
                fontSize = 10.sp,
                color = Color.White.copy(alpha = 0.7f),
                modifier = Modifier
                    .background(
                        when (member.role) {
                            "CREATOR" -> CustomRed.copy(alpha = 0.3f)
                            "ADMIN" -> CyanBlue.copy(alpha = 0.3f)
                            else -> Color.White.copy(alpha = 0.1f)
                        },
                        RoundedCornerShape(6.dp)
                    )
                    .padding(horizontal = 8.dp, vertical = 2.dp)
            )

            // Contribution
            member.contributed?.let {
                Text(
                    "$${String.format("%.0f", it)}",
                    fontSize = 11.sp,
                    color = Color.White.copy(alpha = 0.7f)
                )
            }
        }
    }
}

/**
 * Actions grid with 2x3 layout
 */
@Composable
fun ActionsGrid(
    onDeposit: () -> Unit,
    onScanPay: () -> Unit,
    onSchedule: () -> Unit,
    onDistribute: () -> Unit,
    onInvite: () -> Unit,
    onLeave: () -> Unit,
    modifier: Modifier = Modifier
) {
    Column(
        modifier = modifier.fillMaxWidth(),
        verticalArrangement = Arrangement.spacedBy(12.dp)
    ) {
        Text(
            "Quick Actions",
            fontSize = 20.sp,
            fontWeight = FontWeight.Bold,
            color = Color.White,
            fontFamily = Gatians
        )

        Column(
            verticalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            // Row 1
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                ActionCard(
                    icon = Icons.Filled.AccountBalance,
                    title = "Deposit",
                    subtitle = "Add funds",
                    color = CustomRed,
                    onClick = onDeposit,
                    modifier = Modifier.weight(1f)
                )
                ActionCard(
                    icon = Icons.Filled.QrCodeScanner,
                    title = "Scan & Pay",
                    subtitle = "Quick payment",
                    color = CyanBlue,
                    onClick = onScanPay,
                    modifier = Modifier.weight(1f)
                )
            }

            // Row 2
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                ActionCard(
                    icon = Icons.Filled.Schedule,
                    title = "Schedule",
                    subtitle = "Auto payout",
                    color = IndigoDeep,
                    onClick = onSchedule,
                    modifier = Modifier.weight(1f)
                )
                ActionCard(
                    icon = Icons.Filled.Money,
                    title = "Distribute",
                    subtitle = "To members",
                    color = Color(0xFF4CAF50),
                    onClick = onDistribute,
                    modifier = Modifier.weight(1f)
                )
            }

            // Row 3
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                ActionCard(
                    icon = Icons.Filled.PersonAdd,
                    title = "Invite",
                    subtitle = "Add members",
                    color = Color(0xFFFF9800),
                    onClick = onInvite,
                    modifier = Modifier.weight(1f)
                )
                ActionCard(
                    icon = Icons.Filled.ExitToApp,
                    title = "Leave Pod",
                    subtitle = "Exit group",
                    color = Color(0xFF9E9E9E),
                    onClick = onLeave,
                    modifier = Modifier.weight(1f)
                )
            }
        }
    }
}

/**
 * Action card button
 */
@Composable
fun ActionCard(
    icon: ImageVector,
    title: String,
    subtitle: String,
    color: Color,
    onClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    Card(
        modifier = modifier
            .height(100.dp)
            .clickable(onClick = onClick),
        shape = RoundedCornerShape(16.dp),
        colors = CardDefaults.cardColors(
            containerColor = color.copy(alpha = 0.15f)
        )
    ) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(12.dp),
            verticalArrangement = Arrangement.SpaceBetween
        ) {
            Icon(
                icon,
                contentDescription = title,
                tint = color,
                modifier = Modifier.size(28.dp)
            )

            Column {
                Text(
                    title,
                    fontSize = 14.sp,
                    fontWeight = FontWeight.Bold,
                    color = Color.White
                )
                Text(
                    subtitle,
                    fontSize = 11.sp,
                    color = Color.White.copy(alpha = 0.7f)
                )
            }
        }
    }
}

/**
 * Activity feed section
 */
@Composable
fun ActivitySection(
    activityState: Resource<List<TransactionItem>>,
    modifier: Modifier = Modifier
) {
    Column(
        modifier = modifier.fillMaxWidth(),
        verticalArrangement = Arrangement.spacedBy(12.dp)
    ) {
        Text(
            "Recent Activity",
            fontSize = 20.sp,
            fontWeight = FontWeight.Bold,
            color = Color.White,
            fontFamily = Gatians
        )

        when (activityState) {
            is Resource.Loading -> {
                CircularProgressIndicator(
                    modifier = Modifier.size(24.dp),
                    color = CustomRed
                )
            }
            is Resource.Success -> {
                @Suppress("UNCHECKED_CAST")
                val activities: List<TransactionItem> = (activityState.data as? List<TransactionItem>) ?: emptyList()
                if (activities.isEmpty()) {
                    Card(
                        modifier = Modifier.fillMaxWidth(),
                        shape = RoundedCornerShape(16.dp),
                        colors = CardDefaults.cardColors(containerColor = Charcoal)
                    ) {
                        Box(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(24.dp),
                            contentAlignment = Alignment.Center
                        ) {
                            Text(
                                "No activity yet",
                                color = Color.White.copy(alpha = 0.5f),
                                fontSize = 14.sp
                            )
                        }
                    }
                } else {
                    // Display transaction list
                    Column(
                        verticalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        activities.forEach { activity ->
                            TransactionListItem(transaction = activity)
                        }
                    }
                }
            }
            is Resource.Error -> {
                Card(
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(16.dp),
                    colors = CardDefaults.cardColors(containerColor = Charcoal)
                ) {
                    Box(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(24.dp),
                        contentAlignment = Alignment.Center
                    ) {
                        Column(
                            horizontalAlignment = Alignment.CenterHorizontally,
                            verticalArrangement = Arrangement.spacedBy(8.dp)
                        ) {
                            Text(
                                "Failed to load activity",
                                color = Color.White.copy(alpha = 0.7f),
                                fontSize = 14.sp
                            )
                            Text(
                                activityState.message ?: "Unknown error",
                                color = Color.White.copy(alpha = 0.5f),
                                fontSize = 12.sp
                            )
                        }
                    }
                }
            }
        }
    }
}

/**
 * Individual transaction list item
 */
@Composable
fun TransactionListItem(
    transaction: com.orbit.data.models.TransactionItem,
    modifier: Modifier = Modifier
) {
    Card(
        modifier = modifier.fillMaxWidth(),
        shape = RoundedCornerShape(12.dp),
        colors = CardDefaults.cardColors(containerColor = Charcoal)
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            // Left: Icon + Type/Description
            Row(
                horizontalArrangement = Arrangement.spacedBy(12.dp),
                verticalAlignment = Alignment.CenterVertically,
                modifier = Modifier.weight(1f)
            ) {
                // Transaction type icon
                Box(
                    modifier = Modifier
                        .size(40.dp)
                        .clip(CircleShape)
                        .background(getTransactionColor(transaction.type).copy(alpha = 0.2f)),
                    contentAlignment = Alignment.Center
                ) {
                    Icon(
                        imageVector = getTransactionIcon(transaction.type),
                        contentDescription = transaction.type.value,
                        tint = getTransactionColor(transaction.type),
                        modifier = Modifier.size(20.dp)
                    )
                }

                // Type and details
                Column {
                    Text(
                        text = getTransactionTitle(transaction.type),
                        fontSize = 14.sp,
                        fontWeight = FontWeight.Medium,
                        color = Color.White
                    )
                    Text(
                        text = formatTransactionTimestamp(transaction.timestamp),
                        fontSize = 12.sp,
                        color = Color.White.copy(alpha = 0.6f)
                    )
                }
            }

            // Right: Amount + Status
            Column(
                horizontalAlignment = Alignment.End
            ) {
                Text(
                    text = formatTransactionAmount(transaction.amount, transaction.type),
                    fontSize = 16.sp,
                    fontWeight = FontWeight.Bold,
                    color = getAmountColor(transaction.type)
                )
                TransactionStatusBadge(status = transaction.status)
            }
        }
    }
}

/**
 * Transaction status badge
 */
@Composable
fun TransactionStatusBadge(
    status: com.orbit.data.models.TransactionStatus,
    modifier: Modifier = Modifier
) {
    val (bgColor, textColor, text) = when (status) {
        com.orbit.data.models.TransactionStatus.COMPLETED ->
            Triple(Color(0xFF4CAF50).copy(alpha = 0.2f), Color(0xFF4CAF50), "Completed")
        com.orbit.data.models.TransactionStatus.PENDING ->
            Triple(Color(0xFFFFA726).copy(alpha = 0.2f), Color(0xFFFFA726), "Pending")
        com.orbit.data.models.TransactionStatus.PROCESSING ->
            Triple(Color(0xFF42A5F5).copy(alpha = 0.2f), Color(0xFF42A5F5), "Processing")
        com.orbit.data.models.TransactionStatus.FAILED ->
            Triple(Color(0xFFEF5350).copy(alpha = 0.2f), Color(0xFFEF5350), "Failed")
        com.orbit.data.models.TransactionStatus.CANCELLED ->
            Triple(Color(0xFF9E9E9E).copy(alpha = 0.2f), Color(0xFF9E9E9E), "Cancelled")
        com.orbit.data.models.TransactionStatus.REFUNDED ->
            Triple(Color(0xFF9C27B0).copy(alpha = 0.2f), Color(0xFF9C27B0), "Refunded")
    }

    Surface(
        shape = RoundedCornerShape(4.dp),
        color = bgColor,
        modifier = modifier
    ) {
        Text(
            text = text,
            fontSize = 10.sp,
            fontWeight = FontWeight.Medium,
            color = textColor,
            modifier = Modifier.padding(horizontal = 6.dp, vertical = 2.dp)
        )
    }
}

// Helper functions
private fun getTransactionIcon(type: com.orbit.data.models.TransactionType): androidx.compose.ui.graphics.vector.ImageVector {
    return when (type) {
        com.orbit.data.models.TransactionType.DEPOSIT -> Icons.Filled.Add
        com.orbit.data.models.TransactionType.WITHDRAWAL -> Icons.Filled.KeyboardArrowDown
        com.orbit.data.models.TransactionType.TRANSFER -> Icons.Filled.Send
        com.orbit.data.models.TransactionType.DISTRIBUTION -> Icons.Filled.Share
        com.orbit.data.models.TransactionType.SCHEDULED_PAYOUT -> Icons.Filled.DateRange
        com.orbit.data.models.TransactionType.REFUND -> Icons.Filled.Refresh
        com.orbit.data.models.TransactionType.FEE -> Icons.Filled.Info
    }
}

private fun getTransactionColor(type: com.orbit.data.models.TransactionType): Color {
    return when (type) {
        com.orbit.data.models.TransactionType.DEPOSIT -> Color(0xFF4CAF50)
        com.orbit.data.models.TransactionType.WITHDRAWAL -> Color(0xFFEF5350)
        com.orbit.data.models.TransactionType.TRANSFER -> Color(0xFF42A5F5)
        com.orbit.data.models.TransactionType.DISTRIBUTION -> Color(0xFFAB47BC)
        com.orbit.data.models.TransactionType.SCHEDULED_PAYOUT -> Color(0xFFFF9800)
        com.orbit.data.models.TransactionType.REFUND -> Color(0xFF26C6DA)
        com.orbit.data.models.TransactionType.FEE -> Color(0xFF78909C)
    }
}

private fun getAmountColor(type: com.orbit.data.models.TransactionType): Color {
    return when (type) {
        com.orbit.data.models.TransactionType.DEPOSIT -> Color(0xFF4CAF50)
        com.orbit.data.models.TransactionType.WITHDRAWAL,
        com.orbit.data.models.TransactionType.DISTRIBUTION,
        com.orbit.data.models.TransactionType.SCHEDULED_PAYOUT,
        com.orbit.data.models.TransactionType.FEE -> Color(0xFFEF5350)
        else -> Color.White
    }
}

private fun getTransactionTitle(type: com.orbit.data.models.TransactionType): String {
    return when (type) {
        com.orbit.data.models.TransactionType.DEPOSIT -> "Deposit"
        com.orbit.data.models.TransactionType.WITHDRAWAL -> "Withdrawal"
        com.orbit.data.models.TransactionType.TRANSFER -> "Transfer"
        com.orbit.data.models.TransactionType.DISTRIBUTION -> "Distribution"
        com.orbit.data.models.TransactionType.SCHEDULED_PAYOUT -> "Scheduled Payout"
        com.orbit.data.models.TransactionType.REFUND -> "Refund"
        com.orbit.data.models.TransactionType.FEE -> "Fee"
    }
}

private fun formatTransactionAmount(amount: Double, type: com.orbit.data.models.TransactionType): String {
    val prefix = when (type) {
        com.orbit.data.models.TransactionType.DEPOSIT -> "+"
        com.orbit.data.models.TransactionType.WITHDRAWAL,
        com.orbit.data.models.TransactionType.DISTRIBUTION,
        com.orbit.data.models.TransactionType.SCHEDULED_PAYOUT,
        com.orbit.data.models.TransactionType.FEE -> "-"
        else -> ""
    }
    return "$prefix${"$%.2f".format(amount)}"
}

private fun formatTransactionTimestamp(timestamp: String): String {
    return try {
        // Parse ISO 8601 timestamp and format to relative time
        val instant = java.time.Instant.parse(timestamp)
        val now = java.time.Instant.now()
        val duration = java.time.Duration.between(instant, now)

        when {
            duration.toMinutes() < 1 -> "Just now"
            duration.toMinutes() < 60 -> "${duration.toMinutes()}m ago"
            duration.toHours() < 24 -> "${duration.toHours()}h ago"
            duration.toDays() < 7 -> "${duration.toDays()}d ago"
            else -> {
                val formatter = java.time.format.DateTimeFormatter.ofPattern("MMM dd, yyyy")
                java.time.LocalDateTime.ofInstant(instant, java.time.ZoneId.systemDefault()).format(formatter)
            }
        }
    } catch (e: Exception) {
        timestamp.substringBefore("T")
    }
}

/**
 * Loading state
 */
@Composable
private fun PodDetailLoadingState(modifier: Modifier = Modifier) {
    Box(
        modifier = modifier.fillMaxSize(),
        contentAlignment = Alignment.Center
    ) {
        CircularProgressIndicator(color = CustomRed)
    }
}

/**
 * Error state
 */
@Composable
fun ErrorState(
    message: String,
    onRetry: () -> Unit,
    modifier: Modifier = Modifier
) {
    Box(
        modifier = modifier.fillMaxSize(),
        contentAlignment = Alignment.Center
    ) {
        Column(
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            Text(
                "⚠️",
                fontSize = 64.sp
            )
            Text(
                message,
                color = Color.White,
                textAlign = TextAlign.Center
            )
            Button(onClick = onRetry) {
                Text("Retry")
            }
        }
    }
}

/**
 * Leave Pod Bottom Sheet - Simple confirmation dialog
 * (Keeping this one as it's just a simple confirmation, not a complex form)
 */
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun LeavePodBottomSheet(
    onDismiss: () -> Unit,
    onLeave: () -> Unit
) {
    ModalBottomSheet(
        onDismissRequest = onDismiss,
        containerColor = Charcoal
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(24.dp),
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            Text(
                "Leave Pod",
                fontSize = 24.sp,
                fontWeight = FontWeight.Bold,
                color = Color.White
            )

            Text(
                "Are you sure you want to leave this pod? This action cannot be undone.",
                color = Color.White.copy(alpha = 0.7f)
            )

            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                OutlinedButton(
                    onClick = onDismiss,
                    modifier = Modifier.weight(1f)
                ) {
                    Text("Cancel")
                }
                Button(
                    onClick = onLeave,
                    modifier = Modifier.weight(1f),
                    colors = ButtonDefaults.buttonColors(
                        containerColor = Color.Red
                    )
                ) {
                    Text("Leave")
                }
            }
        }
    }
}

