package com.orbit.navigation

import android.net.Uri
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
import com.orbit.ui.screens.SignInScreen
import com.orbit.viewmodel.AuthViewModel

@Composable
fun OrbitNavigation(
    innerPaddingValues: PaddingValues,
    navController: NavHostController = rememberNavController(),
    deepLinkUri: Uri? = null
) {
    // Always start with SignInScreen - it will handle session checking and routing internally
    val startDestination = OrbitDestinations.HOME

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


        composable(OrbitDestinations.HOME) {
            HomeScreen()
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
