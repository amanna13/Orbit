package com.orbit.ui.components

import android.Manifest
import android.util.Log
import androidx.camera.core.CameraSelector
import androidx.camera.core.ImageAnalysis
import androidx.camera.core.ImageProxy
import androidx.camera.core.Preview
import androidx.camera.lifecycle.ProcessCameraProvider
import androidx.camera.view.PreviewView
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Close
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalLifecycleOwner
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.viewinterop.AndroidView
import androidx.core.content.ContextCompat
import com.google.accompanist.permissions.ExperimentalPermissionsApi
import com.google.accompanist.permissions.isGranted
import com.google.accompanist.permissions.rememberPermissionState
import com.google.accompanist.permissions.shouldShowRationale
import com.google.mlkit.vision.barcode.BarcodeScanning
import com.google.mlkit.vision.barcode.common.Barcode
import com.google.mlkit.vision.common.InputImage
import com.orbit.ui.theme.Charcoal
import com.orbit.ui.theme.CustomRed
import com.orbit.ui.theme.Poppins
import java.util.concurrent.Executors

@OptIn(ExperimentalPermissionsApi::class)
@Composable
fun BarcodeScannerScreen(
    onBarcodeScanned: (String) -> Unit,
    onClose: () -> Unit
) {
    val cameraPermissionState = rememberPermissionState(Manifest.permission.CAMERA)

    LaunchedEffect(Unit) {
        if (!cameraPermissionState.status.isGranted) {
            cameraPermissionState.launchPermissionRequest()
        }
    }

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(Color.Black)
    ) {
        when {
            cameraPermissionState.status.isGranted -> {
                CameraPreview(
                    onBarcodeScanned = onBarcodeScanned,
                    onClose = onClose
                )
            }
            cameraPermissionState.status.shouldShowRationale -> {
                // Permission denied, show rationale
                PermissionRationale(
                    onRequestPermission = { cameraPermissionState.launchPermissionRequest() },
                    onClose = onClose
                )
            }
            else -> {
                // Permission denied permanently or first time
                PermissionRationale(
                    onRequestPermission = { cameraPermissionState.launchPermissionRequest() },
                    onClose = onClose
                )
            }
        }
    }
}

@Composable
private fun CameraPreview(
    onBarcodeScanned: (String) -> Unit,
    onClose: () -> Unit
) {
    val context = LocalContext.current
    val lifecycleOwner = LocalLifecycleOwner.current
    val cameraProviderFuture = remember { ProcessCameraProvider.getInstance(context) }
    var hasScanned by remember { mutableStateOf(false) }

    DisposableEffect(Unit) {
        onDispose {
            cameraProviderFuture.get().unbindAll()
        }
    }

    Box(modifier = Modifier.fillMaxSize()) {
        AndroidView(
            factory = { ctx ->
                val previewView = PreviewView(ctx)
                val executor = ContextCompat.getMainExecutor(ctx)

                cameraProviderFuture.addListener({
                    val cameraProvider = cameraProviderFuture.get()

                    val preview = Preview.Builder().build().also {
                        it.setSurfaceProvider(previewView.surfaceProvider)
                    }

                    val imageAnalysis = ImageAnalysis.Builder()
                        .setBackpressureStrategy(ImageAnalysis.STRATEGY_KEEP_ONLY_LATEST)
                        .build()
                        .also {
                            it.setAnalyzer(Executors.newSingleThreadExecutor()) { imageProxy ->
                                if (!hasScanned) {
                                    processImageProxy(imageProxy) { barcode ->
                                        hasScanned = true
                                        onBarcodeScanned(barcode)
                                    }
                                } else {
                                    imageProxy.close()
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
                        Log.e("CameraPreview", "Use case binding failed", e)
                    }
                }, executor)

                previewView
            },
            modifier = Modifier.fillMaxSize()
        )

        // Overlay with scanning frame
        ScannerOverlay()

        // Close button
        IconButton(
            onClick = onClose,
            modifier = Modifier
                .align(Alignment.TopEnd)
                .padding(16.dp)
                .size(48.dp)
                .clip(RoundedCornerShape(24.dp))
                .background(Color.Black.copy(alpha = 0.5f))
        ) {
            Icon(
                Icons.Filled.Close,
                contentDescription = "Close",
                tint = Color.White,
                modifier = Modifier.size(24.dp)
            )
        }

        // Instructions
        Text(
            text = "Scan the Pod QR Code",
            fontFamily = Poppins,
            fontSize = 18.sp,
            fontWeight = FontWeight.SemiBold,
            color = Color.White,
            modifier = Modifier
                .align(Alignment.TopCenter)
                .padding(top = 80.dp)
        )
    }
}

@Composable
private fun ScannerOverlay() {
    Box(
        modifier = Modifier.fillMaxSize(),
        contentAlignment = Alignment.Center
    ) {
        Box(
            modifier = Modifier
                .size(280.dp)
                .clip(RoundedCornerShape(24.dp))
                .background(Color.Transparent)
                .padding(4.dp)
        ) {
            // Corner indicators
            // Top-left
            Box(
                modifier = Modifier
                    .align(Alignment.TopStart)
                    .size(40.dp, 4.dp)
                    .background(CustomRed)
            )
            Box(
                modifier = Modifier
                    .align(Alignment.TopStart)
                    .size(4.dp, 40.dp)
                    .background(CustomRed)
            )

            // Top-right
            Box(
                modifier = Modifier
                    .align(Alignment.TopEnd)
                    .size(40.dp, 4.dp)
                    .background(CustomRed)
            )
            Box(
                modifier = Modifier
                    .align(Alignment.TopEnd)
                    .size(4.dp, 40.dp)
                    .background(CustomRed)
            )

            // Bottom-left
            Box(
                modifier = Modifier
                    .align(Alignment.BottomStart)
                    .size(40.dp, 4.dp)
                    .background(CustomRed)
            )
            Box(
                modifier = Modifier
                    .align(Alignment.BottomStart)
                    .size(4.dp, 40.dp)
                    .background(CustomRed)
            )

            // Bottom-right
            Box(
                modifier = Modifier
                    .align(Alignment.BottomEnd)
                    .size(40.dp, 4.dp)
                    .background(CustomRed)
            )
            Box(
                modifier = Modifier
                    .align(Alignment.BottomEnd)
                    .size(4.dp, 40.dp)
                    .background(CustomRed)
            )
        }
    }
}

@Composable
private fun PermissionRationale(
    onRequestPermission: () -> Unit,
    onClose: () -> Unit
) {
    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(Charcoal),
        contentAlignment = Alignment.Center
    ) {
        androidx.compose.foundation.layout.Column(
            horizontalAlignment = Alignment.CenterHorizontally,
            modifier = Modifier.padding(32.dp)
        ) {
            Text(
                text = "📸",
                fontSize = 80.sp
            )

            androidx.compose.foundation.layout.Spacer(modifier = Modifier.size(24.dp))

            Text(
                text = "Camera Permission Required",
                fontFamily = Poppins,
                fontSize = 22.sp,
                fontWeight = FontWeight.SemiBold,
                color = Color.White
            )

            androidx.compose.foundation.layout.Spacer(modifier = Modifier.size(16.dp))

            Text(
                text = "We need camera access to scan QR codes and join pods",
                fontFamily = Poppins,
                fontSize = 14.sp,
                fontWeight = FontWeight.Normal,
                color = Color.White.copy(alpha = 0.7f),
                modifier = Modifier.padding(horizontal = 16.dp)
            )

            androidx.compose.foundation.layout.Spacer(modifier = Modifier.size(32.dp))

            androidx.compose.material3.Button(
                onClick = onRequestPermission,
                colors = androidx.compose.material3.ButtonDefaults.buttonColors(
                    containerColor = CustomRed,
                    contentColor = Color.White
                ),
                modifier = Modifier
                    .padding(horizontal = 32.dp),
                shape = RoundedCornerShape(12.dp)
            ) {
                Text(
                    text = "Grant Permission",
                    fontFamily = Poppins,
                    fontSize = 16.sp,
                    fontWeight = FontWeight.Medium,
                    modifier = Modifier.padding(vertical = 8.dp, horizontal = 24.dp)
                )
            }

            androidx.compose.foundation.layout.Spacer(modifier = Modifier.size(16.dp))

            androidx.compose.material3.TextButton(onClick = onClose) {
                Text(
                    text = "Cancel",
                    fontFamily = Poppins,
                    fontSize = 14.sp,
                    fontWeight = FontWeight.Normal,
                    color = Color.White.copy(alpha = 0.7f)
                )
            }
        }
    }
}

@androidx.annotation.OptIn(androidx.camera.core.ExperimentalGetImage::class)
private fun processImageProxy(
    imageProxy: ImageProxy,
    onBarcodeScanned: (String) -> Unit
) {
    val mediaImage = imageProxy.image
    if (mediaImage != null) {
        val image = InputImage.fromMediaImage(mediaImage, imageProxy.imageInfo.rotationDegrees)
        val scanner = BarcodeScanning.getClient()

        scanner.process(image)
            .addOnSuccessListener { barcodes ->
                for (barcode in barcodes) {
                    when (barcode.valueType) {
                        Barcode.TYPE_TEXT, Barcode.TYPE_URL -> {
                            val rawValue = barcode.rawValue
                            if (rawValue != null && rawValue.startsWith("flowpods://join?code=")) {
                                val code = rawValue.substringAfter("flowpods://join?code=")
                                if (code.isNotBlank()) {
                                    onBarcodeScanned(code)
                                }
                            }
                        }
                    }
                }
            }
            .addOnFailureListener {
                Log.e("BarcodeScanner", "Barcode scanning failed", it)
            }
            .addOnCompleteListener {
                imageProxy.close()
            }
    } else {
        imageProxy.close()
    }
}

@OptIn(ExperimentalPermissionsApi::class)
@Composable
fun BarcodeScannerView(
    onBarcodeScanned: (String) -> Unit,
    onError: (String) -> Unit
) {
    val cameraPermissionState = rememberPermissionState(Manifest.permission.CAMERA)

    LaunchedEffect(Unit) {
        if (!cameraPermissionState.status.isGranted) {
            cameraPermissionState.launchPermissionRequest()
        }
    }

    Box(
        modifier = Modifier
            .background(Color.Black)
    ) {
        when {
            cameraPermissionState.status.isGranted -> {
                EmbeddedCameraPreview(
                    onBarcodeScanned = onBarcodeScanned
                )
            }
            else -> {
                // Show compact permission request
                CompactPermissionRequest(
                    onRequestPermission = { cameraPermissionState.launchPermissionRequest() },
                    onError = { onError("Camera permission is required to scan QR codes") }
                )
            }
        }
    }
}

@Composable
private fun EmbeddedCameraPreview(
    onBarcodeScanned: (String) -> Unit
) {
    val context = LocalContext.current
    val lifecycleOwner = LocalLifecycleOwner.current
    val cameraProviderFuture = remember { ProcessCameraProvider.getInstance(context) }
    var hasScanned by remember { mutableStateOf(false) }

    DisposableEffect(Unit) {
        onDispose {
            cameraProviderFuture.get().unbindAll()
        }
    }

    Box(modifier = Modifier) {
        AndroidView(
            factory = { ctx ->
                val previewView = PreviewView(ctx)
                val executor = ContextCompat.getMainExecutor(ctx)

                cameraProviderFuture.addListener({
                    val cameraProvider = cameraProviderFuture.get()

                    val preview = Preview.Builder().build().also {
                        it.setSurfaceProvider(previewView.surfaceProvider)
                    }

                    val imageAnalysis = ImageAnalysis.Builder()
                        .setBackpressureStrategy(ImageAnalysis.STRATEGY_KEEP_ONLY_LATEST)
                        .build()
                        .also {
                            it.setAnalyzer(Executors.newSingleThreadExecutor()) { imageProxy ->
                                if (!hasScanned) {
                                    processImageProxy(imageProxy) { barcode ->
                                        hasScanned = true
                                        onBarcodeScanned(barcode)
                                    }
                                } else {
                                    imageProxy.close()
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
                        Log.e("CameraPreview", "Use case binding failed", e)
                    }
                }, executor)

                previewView
            },
            modifier = Modifier.fillMaxSize()
        )

        // Compact overlay with scanning frame
        CompactScannerOverlay()


    }
}

@Composable
private fun CompactScannerOverlay() {
    Box(
        modifier = Modifier.fillMaxSize(),
        contentAlignment = Alignment.Center
    ) {
        Box(
            modifier = Modifier
                .size(180.dp)
                .clip(RoundedCornerShape(12.dp))
                .background(Color.Transparent)
                .padding(4.dp)
        ) {
            // Corner indicators - smaller for embedded view
            // Top-left
            Box(
                modifier = Modifier
                    .align(Alignment.TopStart)
                    .size(28.dp, 3.dp)
                    .background(CustomRed)
            )
            Box(
                modifier = Modifier
                    .align(Alignment.TopStart)
                    .size(3.dp, 28.dp)
                    .background(CustomRed)
            )

            // Top-right
            Box(
                modifier = Modifier
                    .align(Alignment.TopEnd)
                    .size(28.dp, 3.dp)
                    .background(CustomRed)
            )
            Box(
                modifier = Modifier
                    .align(Alignment.TopEnd)
                    .size(3.dp, 28.dp)
                    .background(CustomRed)
            )

            // Bottom-left
            Box(
                modifier = Modifier
                    .align(Alignment.BottomStart)
                    .size(28.dp, 3.dp)
                    .background(CustomRed)
            )
            Box(
                modifier = Modifier
                    .align(Alignment.BottomStart)
                    .size(4.dp, 32.dp)
                    .background(CustomRed)
            )

            // Bottom-right
            Box(
                modifier = Modifier
                    .align(Alignment.BottomEnd)
                    .size(32.dp, 4.dp)
                    .background(CustomRed)
            )
            Box(
                modifier = Modifier
                    .align(Alignment.BottomEnd)
                    .size(4.dp, 32.dp)
                    .background(CustomRed)
            )
        }
    }
}

@Composable
private fun CompactPermissionRequest(
    onRequestPermission: () -> Unit,
    onError: () -> Unit
) {
    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(Charcoal),
        contentAlignment = Alignment.Center
    ) {
        Column(
            horizontalAlignment = Alignment.CenterHorizontally,
            modifier = Modifier.padding(24.dp)
        ) {
            Text(
                text = "📸",
                fontSize = 48.sp
            )

            Spacer(modifier = Modifier.size(16.dp))

            Text(
                text = "Camera Needed",
                fontFamily = Poppins,
                fontSize = 18.sp,
                fontWeight = FontWeight.SemiBold,
                color = Color.White
            )

            Spacer(modifier = Modifier.size(8.dp))

            Text(
                text = "Allow camera access to scan QR codes",
                fontFamily = Poppins,
                fontSize = 13.sp,
                fontWeight = FontWeight.Normal,
                color = Color.White.copy(alpha = 0.7f),
                textAlign = TextAlign.Center
            )

            Spacer(modifier = Modifier.size(20.dp))

            androidx.compose.material3.Button(
                onClick = onRequestPermission,
                colors = androidx.compose.material3.ButtonDefaults.buttonColors(
                    containerColor = CustomRed,
                    contentColor = Color.White
                ),
                shape = RoundedCornerShape(12.dp)
            ) {
                Text(
                    text = "Allow Camera",
                    fontFamily = Poppins,
                    fontSize = 14.sp,
                    fontWeight = FontWeight.Medium,
                    modifier = Modifier.padding(horizontal = 16.dp, vertical = 4.dp)
                )
            }
        }
    }
}

