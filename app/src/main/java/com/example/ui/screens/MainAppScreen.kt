package com.example.ui.screens

import androidx.compose.animation.AnimatedContentTransitionScope
import androidx.compose.animation.core.tween
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavType
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import androidx.navigation.navArgument
import com.example.viewmodel.TravelViewModel

object AppRoutes {
    const val CHAT = "chat"
    const val SETTINGS = "settings"
    const val WALLET = "wallet"
    const val LOYALTY_MANAGEMENT = "loyalty_management"
    const val ITINERARY = "itinerary"
    const val SAFETY_MAP = "safety_map"
    const val MEMORIES = "memories"
    const val VENDOR_CALL = "vendor_call"
    const val PREFERENCES = "preferences"
    const val PLAN_TRIP = "plan_trip"
}

@Composable
fun MainAppScreen(
    viewModel: TravelViewModel = viewModel()
) {
    val navController = rememberNavController()
    var vendorCallTarget by remember {
        mutableStateOf("Grand Champions Front Desk" to "Verify heated pool dates and ADA chair lift access")
    }

    Surface(
        modifier = Modifier.fillMaxSize(),
        color = MaterialTheme.colorScheme.background
    ) {
        NavHost(
            navController = navController,
            startDestination = AppRoutes.CHAT,
            modifier = Modifier.fillMaxSize(),
            enterTransition = {
                slideIntoContainer(
                    towards = AnimatedContentTransitionScope.SlideDirection.Left,
                    animationSpec = tween(350)
                ) + fadeIn(animationSpec = tween(350))
            },
            exitTransition = {
                slideOutOfContainer(
                    towards = AnimatedContentTransitionScope.SlideDirection.Left,
                    animationSpec = tween(350)
                ) + fadeOut(animationSpec = tween(350))
            },
            popEnterTransition = {
                slideIntoContainer(
                    towards = AnimatedContentTransitionScope.SlideDirection.Right,
                    animationSpec = tween(350)
                ) + fadeIn(animationSpec = tween(350))
            },
            popExitTransition = {
                slideOutOfContainer(
                    towards = AnimatedContentTransitionScope.SlideDirection.Right,
                    animationSpec = tween(350)
                ) + fadeOut(animationSpec = tween(350))
            }
        ) {
            // Main Chat Screen
            composable(route = AppRoutes.CHAT) {
                ConciergeChatScreen(
                    viewModel = viewModel,
                    modifier = Modifier.fillMaxSize(),
                    onOpenSettings = { navController.navigate(AppRoutes.SETTINGS) },
                    onOpenWallet = { navController.navigate(AppRoutes.WALLET) },
                    onOpenItinerary = { navController.navigate(AppRoutes.ITINERARY) },
                    onOpenSafetyMap = { navController.navigate(AppRoutes.SAFETY_MAP) },
                    onOpenMemories = { navController.navigate(AppRoutes.MEMORIES) },
                    onOpenVendorCall = { vendor, question ->
                        vendorCallTarget = vendor to question
                        navController.navigate(AppRoutes.VENDOR_CALL)
                    },
                    onOpenPreferences = { navController.navigate(AppRoutes.PREFERENCES) },
                    onOpenPlanTrip = { navController.navigate(AppRoutes.PLAN_TRIP) }
                )
            }

            // Settings Screen
            composable(route = AppRoutes.SETTINGS) {
                SettingsScreen(
                    viewModel = viewModel,
                    onNavigateBack = { navController.popBackStack() }
                )
            }

            // Wallet & Rewards Screen
            composable(route = AppRoutes.WALLET) {
                WalletRewardsScreen(
                    viewModel = viewModel,
                    modifier = Modifier.fillMaxSize(),
                    onNavigateBack = { navController.popBackStack() }
                )
            }

            // Loyalty Account Management
            composable(route = AppRoutes.LOYALTY_MANAGEMENT) {
                WalletRewardsScreen(
                    viewModel = viewModel,
                    modifier = Modifier.fillMaxSize(),
                    onNavigateBack = { navController.popBackStack() }
                )
            }

            // Detailed Itinerary Screen
            composable(route = AppRoutes.ITINERARY) {
                ItineraryDetailScreen(
                    viewModel = viewModel,
                    onOpenPlanDialog = { navController.navigate(AppRoutes.PLAN_TRIP) },
                    onNavigateToVendorCall = { vendor, question ->
                        vendorCallTarget = vendor to question
                        navController.navigate(AppRoutes.VENDOR_CALL)
                    },
                    onNavigateBack = { navController.popBackStack() }
                )
            }

            // Offline Safety & GPS Map Screen
            composable(route = AppRoutes.SAFETY_MAP) {
                OfflineMapSafetyScreen(
                    viewModel = viewModel,
                    modifier = Modifier.fillMaxSize(),
                    onNavigateBack = { navController.popBackStack() }
                )
            }

            // Group Memories & Story Reel Screen
            composable(route = AppRoutes.MEMORIES) {
                GroupMemoriesScreen(
                    viewModel = viewModel,
                    modifier = Modifier.fillMaxSize(),
                    onNavigateBack = { navController.popBackStack() }
                )
            }

            // AI Vendor Telephony Call Screen
            composable(route = AppRoutes.VENDOR_CALL) {
                VendorCallScreen(
                    viewModel = viewModel,
                    initialVendor = vendorCallTarget.first,
                    initialQuestion = vendorCallTarget.second,
                    modifier = Modifier.fillMaxSize(),
                    onNavigateBack = { navController.popBackStack() }
                )
            }

            // Traveler DNA & Preferences Studio Screen
            composable(route = AppRoutes.PREFERENCES) {
                TravelerPreferenceScreen(
                    viewModel = viewModel,
                    modifier = Modifier.fillMaxSize(),
                    onNavigateBack = { navController.popBackStack() },
                    onSelectProactiveTrip = { _ ->
                        navController.navigate(AppRoutes.PLAN_TRIP)
                    }
                )
            }

            // AI Custom Trip Builder Screen
            composable(route = AppRoutes.PLAN_TRIP) {
                PlanTripScreen(
                    viewModel = viewModel,
                    modifier = Modifier.fillMaxSize(),
                    onNavigateBack = { navController.popBackStack() },
                    onTripCreated = { _ ->
                        navController.navigate(AppRoutes.ITINERARY) {
                            popUpTo(AppRoutes.CHAT)
                        }
                    }
                )
            }
        }
    }
}
