package com.orbit.ui.screens

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import com.orbit.data.models.Resource
import com.orbit.viewmodel.PodsViewModel

/**
 * Test screen for verifying API connection
 * Displays pods list fetched from backend
 *
 * Usage: Add this to Navigation and call from a test button
 */
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ApiTestScreen(
    viewModel: PodsViewModel = hiltViewModel()
) {
    val podsState by viewModel.podsState.collectAsState()

    // Fetch pods on screen load
    LaunchedEffect(Unit) {
        viewModel.getPods()
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("API Test Screen") }
            )
        }
    ) { padding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(padding)
                .padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            // Test Buttons
            Text("API Connection Test", style = MaterialTheme.typography.headlineSmall)

            Row(
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                Button(
                    onClick = { viewModel.testApiConnection() },
                    modifier = Modifier.weight(1f)
                ) {
                    Text("Test Connection")
                }

                Button(
                    onClick = {
                        viewModel.createPod(
                            name = "Test Pod",
                            description = "API Test"
                        )
                    },
                    modifier = Modifier.weight(1f)
                ) {
                    Text("Create Test Pod")
                }
            }

            HorizontalDivider()

            // Results Display
            when (podsState) {
                is Resource.Loading -> {
                    Box(
                        modifier = Modifier.fillMaxSize(),
                        contentAlignment = Alignment.Center
                    ) {
                        Column(
                            horizontalAlignment = Alignment.CenterHorizontally,
                            verticalArrangement = Arrangement.spacedBy(8.dp)
                        ) {
                            CircularProgressIndicator()
                            Text("Loading pods...")
                        }
                    }
                }

                is Resource.Success -> {
                    val pods = (podsState as Resource.Success).data ?: emptyList()

                    Text(
                        "Success: ${pods.size} pods found",
                        color = MaterialTheme.colorScheme.primary,
                        style = MaterialTheme.typography.titleMedium
                    )

                    if (pods.isEmpty()) {
                        Card(
                            modifier = Modifier.fillMaxWidth()
                        ) {
                            Text(
                                "No pods found. Try creating one!",
                                modifier = Modifier.padding(16.dp)
                            )
                        }
                    } else {
                        LazyColumn(
                            verticalArrangement = Arrangement.spacedBy(8.dp)
                        ) {
                            items(pods) { pod ->
                                Card(
                                    modifier = Modifier.fillMaxWidth()
                                ) {
                                    Column(
                                        modifier = Modifier.padding(16.dp),
                                        verticalArrangement = Arrangement.spacedBy(4.dp)
                                    ) {
                                        Text(
                                            pod.name,
                                            style = MaterialTheme.typography.titleMedium
                                        )
                                        Text("ID: ${pod.id}")
                                        Text("Balance: $${pod.balance}")
                                        Text("Members: ${pod.memberCount}")
                                        pod.joinCode?.let {
                                            Text("Join Code: $it")
                                        }
                                    }
                                }
                            }
                        }
                    }
                }

                is Resource.Error -> {
                    val error = (podsState as Resource.Error).message ?: "Unknown error"

                    Card(
                        modifier = Modifier.fillMaxWidth(),
                        colors = CardDefaults.cardColors(
                            containerColor = MaterialTheme.colorScheme.errorContainer
                        )
                    ) {
                        Column(
                            modifier = Modifier.padding(16.dp),
                            verticalArrangement = Arrangement.spacedBy(8.dp)
                        ) {
                            Text(
                                "❌ Error",
                                color = MaterialTheme.colorScheme.error,
                                style = MaterialTheme.typography.titleMedium
                            )
                            Text(
                                error,
                                color = MaterialTheme.colorScheme.onErrorContainer
                            )

                            Text(
                                "\nTroubleshooting:",
                                style = MaterialTheme.typography.labelLarge
                            )
                            Text("1. Make sure backend is running on port 3000")
                            Text("2. Check Logcat for detailed errors")
                            Text("3. Verify BASE_URL in BuildConfig")
                            Text("4. Check network connection")
                        }
                    }
                }
            }
        }
    }
}

/**
 * Compact version - just for quick testing
 * Add to any existing screen
 */
@Composable
fun QuickApiTest(
    viewModel: PodsViewModel = hiltViewModel()
) {
    val podsState by viewModel.podsState.collectAsState()

    Column(
        modifier = Modifier.padding(16.dp),
        verticalArrangement = Arrangement.spacedBy(8.dp)
    ) {
        Button(
            onClick = { viewModel.testApiConnection() },
            modifier = Modifier.fillMaxWidth()
        ) {
            Text("🧪 Test API Connection")
        }

        when (podsState) {
            is Resource.Loading -> {
                LinearProgressIndicator(modifier = Modifier.fillMaxWidth())
            }
            is Resource.Success -> {
                val count = (podsState as Resource.Success).data?.size ?: 0
                Text("API Connected: $count pods found", color = MaterialTheme.colorScheme.primary)
            }
            is Resource.Error -> {
                Text("API Error: Check Logcat", color = MaterialTheme.colorScheme.error)
            }
        }
    }
}

