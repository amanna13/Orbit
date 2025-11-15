package com.orbit.navigation

import android.net.Uri
import androidx.compose.animation.core.tween
import androidx.compose.animation.slideInHorizontally
import androidx.compose.animation.slideOutHorizontally
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavHostController
import androidx.navigation.NavType
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import androidx.navigation.navArgument
import androidx.navigation.navDeepLink
import com.orbit.ui.screens.HomeScreen
import com.orbit.ui.screens.PodDetailsScreen
import com.orbit.ui.screens.PodsScreen
import com.orbit.ui.screens.SignInScreen
import com.orbit.ui.theme.*
import com.orbit.ui.components.PodInfo
import com.orbit.viewmodel.AuthViewModel

@Composable
fun OrbitNavigation(
    innerPaddingValues: PaddingValues,
    navController: NavHostController = rememberNavController(),
    deepLinkUri: Uri? = null
) {
    // Always start with SignInScreen - it will handle session checking and routing internally
    val startDestination = OrbitDestinations.SIGN_IN

    val authViewModel: AuthViewModel = hiltViewModel()
    val userInfo by authViewModel.userInfo.collectAsState()

    NavHost(
        navController = navController, startDestination = startDestination
    ) {
        composable(
            route = OrbitDestinations.SIGN_IN + "?shouldLogout={shouldLogout}",
            arguments = listOf(
                navArgument("shouldLogout") {
                    type = NavType.BoolType
                    defaultValue = false
                }
            ),
            deepLinks = listOf(navDeepLink { uriPattern = "com.loopr.app://auth" })
        ) { backStackEntry ->
            val shouldLogout = backStackEntry.arguments?.getBoolean("shouldLogout") ?: false
            SignInScreen(
                deepLinkUri = deepLinkUri,
                onAuthenticationSuccess = {
                    navController.navigate(OrbitDestinations.HOME) {
                        popUpTo(OrbitDestinations.SIGN_IN) {
                            inclusive = true
                        }
                        launchSingleTop = true
                    }
                },
                authViewModel = authViewModel,
                shouldLogout = shouldLogout
            )
        }


        composable(
            route = OrbitDestinations.HOME,
            enterTransition = {
                slideInHorizontally(
                    initialOffsetX = { -it },
                    animationSpec = tween(400)
                )
            },
            exitTransition = {
                slideOutHorizontally(
                    targetOffsetX = { -it },
                    animationSpec = tween(400)
                )
            }
        ) {
            HomeScreen(
                onNavigateToPods = {
                    navController.navigate(OrbitDestinations.PODS)
                }
            )
        }

        composable(
            route = OrbitDestinations.PODS,
            enterTransition = {
                slideInHorizontally(
                    initialOffsetX = { it },
                    animationSpec = tween(400)
                )
            },
            exitTransition = {
                slideOutHorizontally(
                    targetOffsetX = { it },
                    animationSpec = tween(400)
                )
            }
        ) {
            PodsScreen(
                onNavigateBack = {
                    navController.popBackStack()
                },
                onPodClick = { podInfo ->
                    // Navigate to pod details
                    // For now, we'll create a temporary route with podId
                    navController.navigate("${OrbitDestinations.POD_DETAILS}/${podInfo.podId}")
                }
            )
        }

        composable(
            route = "${OrbitDestinations.POD_DETAILS}/{podId}",
            arguments = listOf(
                navArgument("podId") {
                    type = NavType.StringType
                }
            ),
            enterTransition = {
                slideInHorizontally(
                    initialOffsetX = { it },
                    animationSpec = tween(400)
                )
            },
            exitTransition = {
                slideOutHorizontally(
                    targetOffsetX = { it },
                    animationSpec = tween(400)
                )
            }
        ) { backStackEntry ->
            val podId = backStackEntry.arguments?.getString("podId") ?: ""

            // TODO: Fetch pod details from backend/viewmodel
            // For now, using mock data
            val podColors = listOf(
                DeepPurple, VividBlue, TealAccent, LimeGreen, AmberOrange,
                CyanBlue, IndigoDeep, PinkVivid, EmeraldGreen, CoralOrange
            )
            val smileyEmojis = listOf(
                "😊", "😎", "🤩", "😇", "🥳", "😄", "😁", "🙂", "😉", "😌"
            )

            val mockPodInfo = PodInfo(
                podId = podId,
                podName = "Team Alpha",
                memberCount = 8,
                balance = 12500.0,
                colorTag = podColors[0],
                emoji = smileyEmojis[0]
            )

            PodDetailsScreen(
                podInfo = mockPodInfo,
                onNavigateBack = {
                    navController.popBackStack()
                }
            )
        }

//        composable(route = LooprDestinations.QR_SCANNER) {
//            ScannerScreen(
//                onQrCodeScanned = {},
//                onNavigateToHome = {
//                    navController.navigate(LooprDestinations.HOME) {
//                        popUpTo(LooprDestinations.QR_SCANNER) {
//                            inclusive = true
//                        }
//                        launchSingleTop = true
//                    }
//                }
//            )
//        }

    }
}
