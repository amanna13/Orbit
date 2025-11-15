package com.orbit.ui.components.sheets

import android.Manifest
import androidx.camera.core.*
import androidx.camera.lifecycle.ProcessCameraProvider
import androidx.camera.view.PreviewView
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.*
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
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalLifecycleOwner
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.viewinterop.AndroidView
import com.google.accompanist.permissions.ExperimentalPermissionsApi
import com.google.accompanist.permissions.isGranted
import com.google.accompanist.permissions.rememberPermissionState
import com.google.mlkit.vision.barcode.BarcodeScanning
import com.google.mlkit.vision.barcode.common.Barcode
import com.google.mlkit.vision.common.InputImage
import com.orbit.ui.theme.Charcoal
import com.orbit.ui.theme.CustomRed
import com.orbit.ui.theme.Poppins
import java.util.concurrent.ExecutorService
import java.util.concurrent.Executors

/**
 * Scan & Pay Bottom Sheet
 * Scans QR code and processes payment
 *
 * Backend API: POST /api/forte/immediate-payment
 * Body: { payerPodID, receiverPodID, amount }
 */
@OptIn(ExperimentalMaterial3Api::class, ExperimentalPermissionsApi::class)
@Composable
fun SheetScanPay(
    podId: Long,
    podName: String,
    currentBalance: Double,
    onDismiss: () -> Unit,
    onPay: (qrCode: String, amount: Double) -> Unit,
    isLoading: Boolean = false
) {
    var scannedCode by remember { mutableStateOf<String?>(null) }
    var amount by remember { mutableStateOf("") }
    var showAmountInput by remember { mutableStateOf(false) }
    var showError by remember { mutableStateOf(false) }
    var errorMessage by remember { mutableStateOf("") }

    val cameraPermissionState = rememberPermissionState(Manifest.permission.CAMERA)

    LaunchedEffect(Unit) {
        if (!cameraPermissionState.status.isGranted) {
            cameraPermissionState.launchPermissionRequest()
        }
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
                        if (showAmountInput) "Enter Amount" else "Scan to Pay",
                        fontSize = 24.sp,
                        fontWeight = FontWeight.Bold,
                        color = Color.White,
                        fontFamily = Poppins
                    )
                    Text(
                        if (showAmountInput) "Paying from $podName" else "Scan merchant QR code",
                        fontSize = 14.sp,
                        color = Color.White.copy(alpha = 0.7f),
                        fontFamily = Poppins
                    )
                }

                Icon(
                    if (showAmountInput) Icons.Filled.AttachMoney else Icons.Filled.QrCodeScanner,
                    contentDescription = null,
                    tint = CustomRed,
                    modifier = Modifier.size(32.dp)
                )
            }

            if (!showAmountInput) {
                // Camera Preview
                if (cameraPermissionState.status.isGranted) {
                    CameraPreview(
                        onQrCodeScanned = { code ->
                            scannedCode = code
                            showAmountInput = true
                        }
                    )
                } else {
                    // Permission not granted
                    Card(
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(300.dp),
                        colors = CardDefaults.cardColors(
                            containerColor = Color.White.copy(alpha = 0.05f)
                        )
                    ) {
                        Column(
                            modifier = Modifier
                                .fillMaxSize()
                                .padding(24.dp),
                            horizontalAlignment = Alignment.CenterHorizontally,
                            verticalArrangement = Arrangement.spacedBy(16.dp, Alignment.CenterVertically)
                        ) {
                            Icon(
                                Icons.Filled.CameraAlt,
                                contentDescription = null,
                                tint = Color.White.copy(alpha = 0.5f),
                                modifier = Modifier.size(64.dp)
                            )
                            Text(
                                "Camera Permission Required",
                                fontSize = 18.sp,
                                fontWeight = FontWeight.Bold,
                                color = Color.White
                            )
                            Text(
                                "Please grant camera permission to scan QR codes",
                                fontSize = 14.sp,
                                color = Color.White.copy(alpha = 0.7f),
                                textAlign = androidx.compose.ui.text.style.TextAlign.Center
                            )
                            Button(
                                onClick = { cameraPermissionState.launchPermissionRequest() }
                            ) {
                                Text("Grant Permission")
                            }
                        }
                    }
                }
            } else {
                // Amount Input Section
                // Scanned Code Display
                Card(
                    modifier = Modifier.fillMaxWidth(),
                    colors = CardDefaults.cardColors(
                        containerColor = Color.White.copy(alpha = 0.05f)
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
                                "Merchant Code",
                                fontSize = 12.sp,
                                color = Color.White.copy(alpha = 0.7f)
                            )
                            Text(
                                scannedCode?.take(20) ?: "Unknown",
                                fontSize = 14.sp,
                                fontWeight = FontWeight.Medium,
                                color = Color.White,
                                fontFamily = Poppins
                            )
                        }
                        IconButton(onClick = { showAmountInput = false }) {
                            Icon(
                                Icons.Filled.Edit,
                                contentDescription = "Rescan",
                                tint = CustomRed
                            )
                        }
                    }
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
                        horizontalArrangement = Arrangement.SpaceBetween
                    ) {
                        Column {
                            Text(
                                "Available Balance",
                                fontSize = 12.sp,
                                color = Color.White.copy(alpha = 0.7f)
                            )
                            Text(
                                "$${String.format("%.2f", currentBalance)}",
                                fontSize = 20.sp,
                                fontWeight = FontWeight.Bold,
                                color = CustomRed,
                                fontFamily = Poppins
                            )
                        }

                        if (amount.toDoubleOrNull() != null && amount.toDouble() > 0) {
                            Column(horizontalAlignment = Alignment.End) {
                                Text(
                                    "After Payment",
                                    fontSize = 12.sp,
                                    color = Color.White.copy(alpha = 0.7f)
                                )
                                Text(
                                    "$${String.format("%.2f", currentBalance - amount.toDouble())}",
                                    fontSize = 16.sp,
                                    fontWeight = FontWeight.Bold,
                                    color = if ((currentBalance - amount.toDouble()) < 0)
                                        Color.Red else Color.White,
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
                    label = { Text("Payment Amount") },
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
                        }
                    },
                    colors = OutlinedTextFieldDefaults.colors(
                        focusedBorderColor = CustomRed,
                        focusedLabelColor = CustomRed,
                        cursorColor = CustomRed
                    )
                )
            }

            HorizontalDivider(color = Color.White.copy(alpha = 0.1f))

            // Action Buttons
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                OutlinedButton(
                    onClick = {
                        if (showAmountInput) {
                            showAmountInput = false
                            amount = ""
                        } else {
                            onDismiss()
                        }
                    },
                    modifier = Modifier.weight(1f),
                    enabled = !isLoading
                ) {
                    Text(if (showAmountInput) "Back" else "Cancel")
                }

                if (showAmountInput) {
                    Button(
                        onClick = {
                            val payAmount = amount.toDoubleOrNull()
                            when {
                                payAmount == null || payAmount <= 0 -> {
                                    showError = true
                                    errorMessage = "Please enter a valid amount"
                                }
                                payAmount > currentBalance -> {
                                    showError = true
                                    errorMessage = "Insufficient balance"
                                }
                                scannedCode == null -> {
                                    showError = true
                                    errorMessage = "Invalid QR code"
                                }
                                else -> {
                                    onPay(scannedCode!!, payAmount)
                                }
                            }
                        },
                        modifier = Modifier.weight(1f),
                        enabled = amount.toDoubleOrNull()?.let { it > 0 && it <= currentBalance } == true && !isLoading,
                        colors = ButtonDefaults.buttonColors(containerColor = CustomRed)
                    ) {
                        if (isLoading) {
                            CircularProgressIndicator(
                                modifier = Modifier.size(20.dp),
                                color = Color.White,
                                strokeWidth = 2.dp
                            )
                        } else {
                            Text("Pay Now")
                        }
                    }
                }
            }

            Spacer(modifier = Modifier.height(8.dp))
        }
    }
}

/**
 * Camera Preview for QR scanning
 */
@Composable
private fun CameraPreview(
    onQrCodeScanned: (String) -> Unit,
    modifier: Modifier = Modifier
) {
    val context = LocalContext.current
    val lifecycleOwner = LocalLifecycleOwner.current
    val cameraProviderFuture = remember { ProcessCameraProvider.getInstance(context) }
    val cameraExecutor: ExecutorService = remember { Executors.newSingleThreadExecutor() }

    var hasScanned by remember { mutableStateOf(false) }

    DisposableEffect(Unit) {
        onDispose {
            cameraExecutor.shutdown()
        }
    }

    Box(
        modifier = modifier
            .fillMaxWidth()
            .height(300.dp)
            .clip(RoundedCornerShape(16.dp))
            .background(Color.Black)
    ) {
        AndroidView(
            factory = { ctx ->
                val previewView = PreviewView(ctx)
                val cameraProvider = cameraProviderFuture.get()

                val preview = Preview.Builder().build().also {
                    it.setSurfaceProvider(previewView.surfaceProvider)
                }

                val imageAnalysis = ImageAnalysis.Builder()
                    .setBackpressureStrategy(ImageAnalysis.STRATEGY_KEEP_ONLY_LATEST)
                    .build()
                    .also {
                        it.setAnalyzer(cameraExecutor) @androidx.camera.core.ExperimentalGetImage { imageProxy ->
                            processImageProxy(imageProxy, hasScanned) { qrCode ->
                                if (!hasScanned) {
                                    hasScanned = true
                                    onQrCodeScanned(qrCode)
                                }
                            }
                        }
                    }

                val cameraSelector = CameraSelector.DEFAULT_BACK_CAMERA

                try {
                    cameraProvider.unbindAll()
                    cameraProvider.bindToLifecycle(
                        lifecycleOwner,
                        cameraSelector,
                        preview,
                        imageAnalysis
                    )
                } catch (e: Exception) {
                    e.printStackTrace()
                }

                previewView
            },
            modifier = Modifier.fillMaxSize()
        )

        // Scan frame overlay
        Box(
            modifier = Modifier
                .fillMaxSize()
                .padding(48.dp),
            contentAlignment = Alignment.Center
        ) {
            Box(
                modifier = Modifier
                    .size(250.dp)
                    .border(3.dp, CustomRed, RoundedCornerShape(16.dp))
            )
        }

        // Instructions
        Text(
            "Position QR code within frame",
            modifier = Modifier
                .align(Alignment.BottomCenter)
                .padding(16.dp)
                .background(Color.Black.copy(alpha = 0.7f), RoundedCornerShape(8.dp))
                .padding(horizontal = 16.dp, vertical = 8.dp),
            color = Color.White,
            fontSize = 14.sp
        )
    }
}

/**
 * Process camera image for QR code detection
 */
@androidx.camera.core.ExperimentalGetImage
private fun processImageProxy(
    imageProxy: ImageProxy,
    hasScanned: Boolean,
    onQrCodeDetected: (String) -> Unit
) {
    val mediaImage = imageProxy.image
    if (mediaImage != null && !hasScanned) {
        val image = InputImage.fromMediaImage(mediaImage, imageProxy.imageInfo.rotationDegrees)
        val scanner = BarcodeScanning.getClient()

        scanner.process(image)
            .addOnSuccessListener { barcodes ->
                for (barcode in barcodes) {
                    when (barcode.valueType) {
                        Barcode.TYPE_TEXT,
                        Barcode.TYPE_URL -> {
                            barcode.rawValue?.let { onQrCodeDetected(it) }
                        }
                    }
                }
            }
            .addOnCompleteListener {
                imageProxy.close()
            }
    } else {
        imageProxy.close()
    }
}

