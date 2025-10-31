package com.orbit.ui.components

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.core.tween
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.scaleIn
import androidx.compose.animation.scaleOut
import androidx.compose.animation.slideInHorizontally
import androidx.compose.animation.slideOutHorizontally
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.Close
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.material3.Surface
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
import androidx.compose.ui.text.style.LineHeightStyle
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.window.Dialog
import androidx.compose.ui.window.DialogProperties
import com.airbnb.lottie.compose.LottieAnimation
import com.airbnb.lottie.compose.LottieCompositionSpec
import com.airbnb.lottie.compose.LottieConstants
import com.airbnb.lottie.compose.rememberLottieComposition
import com.orbit.R
import com.orbit.ui.theme.Charcoal
import com.orbit.ui.theme.CherryRed
import com.orbit.ui.theme.CustomRed
import com.orbit.ui.theme.Poppins

enum class DialogState {
    INITIAL,
    CREATE_POD,
    SUCCESS,
    JOIN_POD,
    JOIN_SUCCESS,
    CLOSED
}

@Composable
fun PodDialogFlow(
    showDialog: Boolean,
    onDismiss: () -> Unit,
    onCreatePod: (String) -> Unit = {},
    onJoinPod: (String) -> Unit = {}, // Now receives the QR code
    onInviteOthers: () -> Unit = {},
    onViewPods: () -> Unit = {}
) {
    var dialogState by remember { mutableStateOf(DialogState.INITIAL) }
    var podName by remember { mutableStateOf("") }
    var scannedPodCode by remember { mutableStateOf("") }

    if (showDialog) {
        when (dialogState) {
            DialogState.INITIAL -> {
                InitialPodDialog(
                    onDismiss = {
                        dialogState = DialogState.CLOSED
                        onDismiss()
                    },
                    onCreatePod = {
                        dialogState = DialogState.CREATE_POD
                    },
                    onJoinPod = {
                        dialogState = DialogState.JOIN_POD
                    }
                )
            }
            DialogState.CREATE_POD -> {
                CreatePodDialog(
                    podName = podName,
                    onPodNameChange = { podName = it },
                    onBack = {
                        dialogState = DialogState.INITIAL
                        podName = ""
                    },
                    onSubmit = {
                        // Call the network callback here later
                        onCreatePod(podName)
                        dialogState = DialogState.SUCCESS
                    }
                )
            }
            DialogState.SUCCESS -> {
                SuccessDialog(
                    podName = podName,
                    onDismiss = {
                        dialogState = DialogState.CLOSED
                        podName = ""
                        onDismiss()
                    },
                    onInviteOthers = {
                        onInviteOthers()
                        dialogState = DialogState.CLOSED
                        podName = ""
                        onDismiss()
                    },
                    onViewPods = {
                        onViewPods()
                        dialogState = DialogState.CLOSED
                        podName = ""
                        onDismiss()
                    }
                )
            }
            DialogState.JOIN_POD -> {
                JoinPodDialog(
                    onBack = {
                        dialogState = DialogState.INITIAL
                        scannedPodCode = ""
                    },
                    onCodeScanned = { code ->
                        scannedPodCode = code
                        // TODO: Wire network call to verify pod exists
                        onJoinPod(code)
                        dialogState = DialogState.JOIN_SUCCESS
                    }
                )
            }
            DialogState.JOIN_SUCCESS -> {
                JoinSuccessDialog(
                    podCode = scannedPodCode,
                    onDismiss = {
                        dialogState = DialogState.CLOSED
                        scannedPodCode = ""
                        onDismiss()
                    },
                    onGetStarted = {
                        onViewPods()
                        dialogState = DialogState.CLOSED
                        scannedPodCode = ""
                        onDismiss()
                    }
                )
            }
            DialogState.CLOSED -> {
                // Dialog closed, reset state
                dialogState = DialogState.INITIAL
                podName = ""
                scannedPodCode = ""
            }
        }
    } else {
        // Reset state when dialog is dismissed
        dialogState = DialogState.INITIAL
        podName = ""
        scannedPodCode = ""
    }
}

@Composable
private fun InitialPodDialog(
    onDismiss: () -> Unit,
    onCreatePod: () -> Unit,
    onJoinPod: () -> Unit
) {
    Dialog(
        onDismissRequest = onDismiss,
        properties = DialogProperties(
            dismissOnBackPress = true,
            dismissOnClickOutside = true
        )
    ) {
        AnimatedVisibility(
            visible = true,
            enter = scaleIn(
                animationSpec = tween(300),
                initialScale = 0.8f
            ) + fadeIn(animationSpec = tween(300)),
            exit = scaleOut(
                animationSpec = tween(200),
                targetScale = 0.8f
            ) + fadeOut(animationSpec = tween(200))
        ) {
            Surface(
                shape = RoundedCornerShape(24.dp),
                color = Charcoal,
                tonalElevation = 8.dp,
            ) {
                Column(
                    modifier = Modifier.padding(24.dp),
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    Text(
                        "Choose an Action",
                        fontFamily = Poppins,
                        fontSize = 22.sp,
                        fontWeight = FontWeight.SemiBold,
                        color = Color.White
                    )

                    Spacer(modifier = Modifier.height(24.dp))

                    Button(
                        colors = ButtonDefaults.buttonColors(
                            containerColor = CustomRed,
                            contentColor = Color.White
                        ),
                        onClick = onCreatePod,
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(56.dp),
                        shape = RoundedCornerShape(12.dp)
                    ) {
                        Text(
                            "Create Pod",
                            fontFamily = Poppins,
                            fontSize = 16.sp,
                            fontWeight = FontWeight.Medium
                        )
                    }

                    Spacer(modifier = Modifier.height(12.dp))

                    Button(
                        colors = ButtonDefaults.buttonColors(
                            containerColor = Color.White,
                            contentColor = Charcoal
                        ),
                        onClick = onJoinPod,
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(56.dp),
                        shape = RoundedCornerShape(12.dp)
                    ) {
                        Text(
                            "Join Pod",
                            fontFamily = Poppins,
                            fontSize = 16.sp,
                            fontWeight = FontWeight.Medium
                        )
                    }
                }
            }
        }
    }
}

@Composable
private fun CreatePodDialog(
    podName: String,
    onPodNameChange: (String) -> Unit,
    onBack: () -> Unit,
    onSubmit: () -> Unit
) {
    Dialog(
        onDismissRequest = onBack,
        properties = DialogProperties(
            dismissOnBackPress = true,
            dismissOnClickOutside = false
        )
    ) {
        AnimatedVisibility(
            visible = true,
            enter = slideInHorizontally(
                animationSpec = tween(400),
                initialOffsetX = { it }
            ) + fadeIn(animationSpec = tween(400)),
            exit = slideOutHorizontally(
                animationSpec = tween(300),
                targetOffsetX = { it }
            ) + fadeOut(animationSpec = tween(300))
        ) {
            Surface(
                shape = RoundedCornerShape(24.dp),
                color = Charcoal,
                tonalElevation = 8.dp,
//                modifier = Modifier.padding(16.dp)
            ) {
                Column(
                    modifier = Modifier.padding(24.dp)
                ) {
                    // Header with back button
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.SpaceBetween
                    ) {
                        IconButton(
                            onClick = onBack,
                            modifier = Modifier
                                .size(40.dp)
                                .clip(CircleShape)
                                .background(Color.White.copy(alpha = 0.1f))
                        ) {
                            Icon(
                                Icons.Filled.ArrowBack,
                                contentDescription = "Back",
                                tint = Color.White
                            )
                        }

                        Text(
                            "Create New Pod",
                            fontFamily = Poppins,
                            fontSize = 20.sp,
                            fontWeight = FontWeight.SemiBold,
                            color = Color.White,
                            modifier = Modifier,
                            textAlign = TextAlign.Center
                        )

                        Spacer(modifier = Modifier.width(40.dp))
                    }

                    Spacer(modifier = Modifier.height(32.dp))

                    // Pod name input
                    Text(
                        "Pod Name",
                        fontFamily = Poppins,
                        fontSize = 14.sp,
                        fontWeight = FontWeight.Medium,
                        color = Color.White.copy(alpha = 0.7f)
                    )

                    Spacer(modifier = Modifier.height(8.dp))

                    OutlinedTextField(
                        value = podName,
                        onValueChange = onPodNameChange,
                        modifier = Modifier.fillMaxWidth(),
                        placeholder = {
                            Text(
                                "Enter pod name",
                                fontFamily = Poppins,
                                color = Color.White.copy(alpha = 0.4f)
                            )
                        },
                        colors = OutlinedTextFieldDefaults.colors(
                            focusedBorderColor = CherryRed,
                            unfocusedBorderColor = Color.White.copy(alpha = 0.3f),
                            focusedTextColor = Color.White,
                            unfocusedTextColor = Color.White,
                            cursorColor = CherryRed
                        ),
                        shape = RoundedCornerShape(12.dp),
                        textStyle = MaterialTheme.typography.bodyLarge.copy(
                            fontFamily = Poppins,
                            fontWeight = FontWeight.Normal
                        ),
                        singleLine = true
                    )

                    Spacer(modifier = Modifier.height(32.dp))

                    // Submit button
                    Button(
                        colors = ButtonDefaults.buttonColors(
                            containerColor = CustomRed,
                            contentColor = Color.White,
                            disabledContainerColor = CustomRed.copy(alpha = 0.5f),
                            disabledContentColor = Color.White.copy(alpha = 0.5f)
                        ),
                        onClick = onSubmit,
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(56.dp),
                        shape = RoundedCornerShape(12.dp),
                        enabled = podName.isNotBlank()
                    ) {
                        Text(
                            "Create Pod",
                            fontFamily = Poppins,
                            fontSize = 16.sp,
                            fontWeight = FontWeight.SemiBold
                        )
                    }
                }
            }
        }
    }
}

@Composable
private fun SuccessDialog(
    podName: String,
    onDismiss: () -> Unit,
    onInviteOthers: () -> Unit,
    onViewPods: () -> Unit
) {
    val composition by rememberLottieComposition(
        LottieCompositionSpec.RawRes(R.raw.giving_five)
    )

    Dialog(
        onDismissRequest = onDismiss,
        properties = DialogProperties(
            dismissOnBackPress = true,
            dismissOnClickOutside = false
        )
    ) {
        AnimatedVisibility(
            visible = true,
            enter = scaleIn(
                animationSpec = tween(500),
                initialScale = 0.3f
            ) + fadeIn(animationSpec = tween(500)),
            exit = scaleOut(
                animationSpec = tween(300),
                targetScale = 0.8f
            ) + fadeOut(animationSpec = tween(300))
        ) {
            Surface(
                shape = RoundedCornerShape(24.dp),
                color = Charcoal,
                tonalElevation = 8.dp,
//                modifier = Modifier.padding(16.dp)
            ) {
                Box {
                    Column(
                        modifier = Modifier.padding(24.dp),
                        horizontalAlignment = Alignment.CenterHorizontally
                    ) {
                        // Close button
                        IconButton(
                            onClick = onDismiss,
                            modifier = Modifier
                                .align(Alignment.End)
                                .size(36.dp)
                                .clip(CircleShape)
                                .background(Color.White.copy(alpha = 0.1f))
                        ) {
                            Icon(
                                Icons.Filled.Close,
                                contentDescription = "Close",
                                tint = Color.White,
                                modifier = Modifier.size(20.dp)
                            )
                        }


                        // Lottie Animation
                        LottieAnimation(
                            composition = composition,
                            iterations = LottieConstants.IterateForever,
                            modifier = Modifier.size(220.dp).padding(0.dp)
                        )


                        // Success message
                        Text(
                            "Pod Created Successfully!",
                            fontFamily = Poppins,
                            fontSize = 22.sp,
                            fontWeight = FontWeight.SemiBold,
                            color = Color.White,
                            textAlign = TextAlign.Center
                        )

                        Spacer(modifier = Modifier.height(8.dp))

                        Text(
                            "\"$podName\" is ready to go !",
                            fontFamily = Poppins,
                            fontSize = 14.sp,
                            fontWeight = FontWeight.Normal,
                            color = Color.White.copy(alpha = 0.7f),
                            textAlign = TextAlign.Center
                        )

                        Spacer(modifier = Modifier.height(32.dp))

                        // Action buttons
                        Button(
                            colors = ButtonDefaults.buttonColors(
                                containerColor = CustomRed,
                                contentColor = Color.White
                            ),
                            onClick = onInviteOthers,
                            modifier = Modifier
                                .fillMaxWidth()
                                .height(56.dp),
                            shape = RoundedCornerShape(12.dp)
                        ) {
                            Text(
                                "Invite Others",
                                fontFamily = Poppins,
                                fontSize = 16.sp,
                                fontWeight = FontWeight.Medium
                            )
                        }

                        Spacer(modifier = Modifier.height(12.dp))

                        Button(
                            colors = ButtonDefaults.buttonColors(
                                containerColor = Color.White,
                                contentColor = Charcoal
                            ),
                            onClick = onViewPods,
                            modifier = Modifier
                                .fillMaxWidth()
                                .height(56.dp),
                            shape = RoundedCornerShape(12.dp)
                        ) {
                            Text(
                                "View Pods",
                                fontFamily = Poppins,
                                fontSize = 16.sp,
                                fontWeight = FontWeight.Medium
                            )
                        }
                    }
                }
            }
        }
    }
}

@Composable
private fun JoinPodDialog(
    onBack: () -> Unit,
    onCodeScanned: (String) -> Unit
) {
    var showScanner by remember { mutableStateOf(false) }
    var errorMessage by remember { mutableStateOf<String?>(null) }
    val composition by rememberLottieComposition(
        LottieCompositionSpec.RawRes(R.raw.qr_code_scan)
    )

    Dialog(
        onDismissRequest = onBack,
        properties = DialogProperties(
            dismissOnBackPress = true,
            dismissOnClickOutside = false,
            usePlatformDefaultWidth = false
        )
    ) {
        AnimatedVisibility(
            visible = true,
            enter = slideInHorizontally(
                animationSpec = tween(400),
                initialOffsetX = { it }
            ) + fadeIn(animationSpec = tween(400)),
            exit = slideOutHorizontally(
                animationSpec = tween(300),
                targetOffsetX = { it }
            ) + fadeOut(animationSpec = tween(300))
        ) {
            Surface(
                shape = RoundedCornerShape(24.dp),
                color = Charcoal,
                tonalElevation = 8.dp,
                modifier = Modifier
                    .fillMaxWidth(0.9f)
                    .fillMaxHeight(0.65f)
            ) {
                if (showScanner) {
                    // Embedded camera scanner
                    Box(
                        modifier = Modifier
                            .fillMaxSize()
                            .padding(24.dp)
                    ) {
                        Box(
                            modifier = Modifier
                                .fillMaxWidth()
                                .fillMaxHeight(0.65f)
                                .align(Alignment.Center)
                                .clip(RoundedCornerShape(16.dp))
                        ) {
                            BarcodeScannerView(
                                onBarcodeScanned = { code ->
                                    showScanner = false
                                    onCodeScanned(code)
                                },
                                onError = { error ->
                                    errorMessage = error
                                    showScanner = false
                                }
                            )
                        }

                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            verticalAlignment = Alignment.CenterVertically,
                        ) {
                            IconButton(
                                onClick = onBack,
                                modifier = Modifier
                                    .size(40.dp)
                                    .clip(CircleShape)
                                    .background(Color.White.copy(alpha = 0.1f))
                            ) {
                                Icon(
                                    Icons.Filled.ArrowBack,
                                    contentDescription = "Back",
                                    tint = Color.White
                                )
                            }

                            Text(
                                "Point at QR Code",
                                fontFamily = Poppins,
                                fontSize = 20.sp,
                                fontWeight = FontWeight.SemiBold, textAlign = TextAlign.Center,
                                color = Color.White, modifier = Modifier.fillMaxWidth()
                            )
                        }
                        // Instructions below camera
                        Text(
                            text = "Align QR code within the frame",
                            fontFamily = Poppins,
                            fontSize = 14.sp,
                            fontWeight = FontWeight.Normal,
                            color = Color.White.copy(alpha = 0.8f),
                            textAlign = TextAlign.Center,
                            modifier = Modifier
                                .align(Alignment.BottomCenter)
                                .padding(bottom = 8.dp)
                        )

                        // Back button overlay

                    }
                } else {
                    // Initial join pod screen
                    Column(
                        modifier = Modifier.padding(24.dp),
                        horizontalAlignment = Alignment.CenterHorizontally
                    ) {
                        // Header with back button
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.SpaceBetween
                        ) {
                            IconButton(
                                onClick = onBack,
                                modifier = Modifier
                                    .size(40.dp)
                                    .clip(CircleShape)
                                    .background(Color.White.copy(alpha = 0.1f))
                            ) {
                                Icon(
                                    Icons.Filled.ArrowBack,
                                    contentDescription = "Back",
                                    tint = Color.White
                                )
                            }

                            Text(
                                "Join a Pod",
                                fontFamily = Poppins,
                                fontSize = 20.sp,
                                fontWeight = FontWeight.SemiBold,
                                color = Color.White,
                                textAlign = TextAlign.Center
                            )

                            Spacer(modifier = Modifier.width(40.dp))
                        }

                        Spacer(modifier = Modifier.height(32.dp))

                        // Scanner icon/illustration

                            LottieAnimation(
                                composition = composition,
                                iterations = LottieConstants.IterateForever,
                                modifier = Modifier
                                    .size(240.dp)
                                    .padding(0.dp)
                            )


                        Spacer(modifier = Modifier.height(12.dp))

                        Text(
                            "Scan QR Code",
                            fontFamily = Poppins,
                            fontSize = 18.sp,
                            fontWeight = FontWeight.SemiBold,
                            color = Color.White,
                            textAlign = TextAlign.Center
                        )

                        Spacer(modifier = Modifier.height(12.dp))

                        Text(
                            "Scan a pod's QR code to join",
                            fontFamily = Poppins,
                            fontSize = 14.sp,
                            fontWeight = FontWeight.Normal,
                            color = Color.White.copy(alpha = 0.7f),
                            textAlign = TextAlign.Center
                        )

                        // Error message if any
                        if (errorMessage != null) {
                            Spacer(modifier = Modifier.height(16.dp))
                            Text(
                                errorMessage!!,
                                fontFamily = Poppins,
                                fontSize = 12.sp,
                                fontWeight = FontWeight.Normal,
                                color = CustomRed,
                                textAlign = TextAlign.Center
                            )
                        }

                        Spacer(modifier = Modifier.height(32.dp))

                        // Scan button
                        Button(
                            colors = ButtonDefaults.buttonColors(
                                containerColor = CustomRed,
                                contentColor = Color.White
                            ),
                            onClick = {
                                errorMessage = null
                                showScanner = true
                            },
                            modifier = Modifier
                                .fillMaxWidth()
                            .height(56.dp),
                            shape = RoundedCornerShape(12.dp)
                        ) {
                            Text(
                                "Open Scanner",
                                fontFamily = Poppins,
                                fontSize = 16.sp,
                                fontWeight = FontWeight.SemiBold
                            )
                        }
                    }
                }
            }
        }
    }
}

@Composable
private fun JoinSuccessDialog(
    podCode: String,
    onDismiss: () -> Unit,
    onGetStarted: () -> Unit
) {
    val composition by rememberLottieComposition(
        LottieCompositionSpec.RawRes(R.raw.giving_five)
    )

    Dialog(
        onDismissRequest = onDismiss,
        properties = DialogProperties(
            dismissOnBackPress = true,
            dismissOnClickOutside = false
        )
    ) {
        AnimatedVisibility(
            visible = true,
            enter = scaleIn(
                animationSpec = tween(500),
                initialScale = 0.3f
            ) + fadeIn(animationSpec = tween(500)),
            exit = scaleOut(
                animationSpec = tween(300),
                targetScale = 0.8f
            ) + fadeOut(animationSpec = tween(300))
        ) {
            Surface(
                shape = RoundedCornerShape(24.dp),
                color = Charcoal,
                tonalElevation = 8.dp
            ) {
                Box {
                    Column(
                        modifier = Modifier.padding(24.dp),
                        horizontalAlignment = Alignment.CenterHorizontally
                    ) {
                        // Close button
                        IconButton(
                            onClick = onDismiss,
                            modifier = Modifier
                                .align(Alignment.End)
                                .size(36.dp)
                                .clip(CircleShape)
                                .background(Color.White.copy(alpha = 0.1f))
                        ) {
                            Icon(
                                Icons.Filled.Close,
                                contentDescription = "Close",
                                tint = Color.White,
                                modifier = Modifier.size(20.dp)
                            )
                        }

                        // Lottie Animation
                        LottieAnimation(
                            composition = composition,
                            iterations = LottieConstants.IterateForever,
                            modifier = Modifier
                                .size(220.dp)
                                .padding(0.dp)
                        )

                        // Success message
                        Text(
                            "Welcome Aboard! 🎉",
                            fontFamily = Poppins,
                            fontSize = 22.sp,
                            fontWeight = FontWeight.SemiBold,
                            color = Color.White,
                            textAlign = TextAlign.Center
                        )

                        Spacer(modifier = Modifier.height(8.dp))

                        Text(
                            "You've successfully joined the pod!",
                            fontFamily = Poppins,
                            fontSize = 14.sp,
                            fontWeight = FontWeight.Normal,
                            color = Color.White.copy(alpha = 0.7f),
                            textAlign = TextAlign.Center
                        )

                        Spacer(modifier = Modifier.height(32.dp))

                        // Get Started button
                        Button(
                            colors = ButtonDefaults.buttonColors(
                                containerColor = CustomRed,
                                contentColor = Color.White
                            ),
                            onClick = onGetStarted,
                            modifier = Modifier
                                .fillMaxWidth()
                                .height(56.dp),
                            shape = RoundedCornerShape(12.dp)
                        ) {
                            Text(
                                "Let's Dive In! 🚀",
                                fontFamily = Poppins,
                                fontSize = 16.sp,
                                fontWeight = FontWeight.Medium
                            )
                        }
                    }
                }
            }
        }
    }
}
