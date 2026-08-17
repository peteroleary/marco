package com.example.ui.screens

import androidx.compose.animation.AnimatedContent
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.slideInHorizontally
import androidx.compose.animation.slideOutHorizontally
import androidx.compose.animation.togetherWith
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
import com.example.viewmodel.TravelViewModel

enum class AppDestination {
    CHAT,
    SETTINGS,
    WALLET,
    ITINERARY,
    SAFETY_MAP,
    MEMORIES,
    VENDOR_CALL,
    PREFERENCES,
    PLAN_TRIP
}

@Composable
fun MainAppScreen(
    viewModel: TravelViewModel = viewModel()
) {
    var currentDestination by remember { mutableStateOf(AppDestination.CHAT) }
    var vendorCallTarget by remember { mutableStateOf("Grand Champions Front Desk" to "Verify heated pool dates and ADA chair lift access") }

    Surface(
        modifier = Modifier.fillMaxSize(),
        color = MaterialTheme.colorScheme.background
    ) {
        AnimatedContent(
            targetState = currentDestination,
            transitionSpec = {
                if (targetState != AppDestination.CHAT) {
                    (slideInHorizontally { width -> width } + fadeIn()).togetherWith(
                        slideOutHorizontally { width -> -width } + fadeOut()
                    )
                } else {
                    (slideInHorizontally { width -> -width } + fadeIn()).togetherWith(
                        slideOutHorizontally { width -> width } + fadeOut()
                    )
                }
            },
            label = "screen_transition"
        ) { destination ->
            when (destination) {
                AppDestination.CHAT -> {
                    ConciergeChatScreen(
                        viewModel = viewModel,
                        modifier = Modifier.fillMaxSize(),
                        onOpenSettings = { currentDestination = AppDestination.SETTINGS },
                        onOpenWallet = { currentDestination = AppDestination.WALLET },
                        onOpenItinerary = { currentDestination = AppDestination.ITINERARY },
                        onOpenSafetyMap = { currentDestination = AppDestination.SAFETY_MAP },
                        onOpenMemories = { currentDestination = AppDestination.MEMORIES },
                        onOpenVendorCall = { vendor, question ->
                            vendorCallTarget = vendor to question
                            currentDestination = AppDestination.VENDOR_CALL
                        },
                        onOpenPreferences = { currentDestination = AppDestination.PREFERENCES },
                        onOpenPlanTrip = { currentDestination = AppDestination.PLAN_TRIP }
                    )
                }
                AppDestination.SETTINGS -> {
                    SettingsScreen(
                        viewModel = viewModel,
                        onNavigateBack = { currentDestination = AppDestination.CHAT }
                    )
                }
                AppDestination.WALLET -> {
                    WalletRewardsScreen(
                        viewModel = viewModel,
                        onNavigateBack = { currentDestination = AppDestination.CHAT }
                    )
                }
                AppDestination.ITINERARY -> {
                    ItineraryDetailScreen(
                        viewModel = viewModel,
                        onOpenPlanDialog = { currentDestination = AppDestination.PLAN_TRIP },
                        onNavigateToVendorCall = { vendor, question ->
                            vendorCallTarget = vendor to question
                            currentDestination = AppDestination.VENDOR_CALL
                        },
                        onNavigateBack = { currentDestination = AppDestination.CHAT }
                    )
                }
                AppDestination.SAFETY_MAP -> {
                    OfflineMapSafetyScreen(
                        viewModel = viewModel,
                        onNavigateBack = { currentDestination = AppDestination.CHAT }
                    )
                }
                AppDestination.MEMORIES -> {
                    GroupMemoriesScreen(
                        viewModel = viewModel,
                        onNavigateBack = { currentDestination = AppDestination.CHAT }
                    )
                }
                AppDestination.VENDOR_CALL -> {
                    VendorCallScreen(
                        viewModel = viewModel,
                        initialVendor = vendorCallTarget.first,
                        initialQuestion = vendorCallTarget.second,
                        onNavigateBack = { currentDestination = AppDestination.CHAT }
                    )
                }
                AppDestination.PREFERENCES -> {
                    TravelerPreferenceScreen(
                        viewModel = viewModel,
                        onNavigateBack = { currentDestination = AppDestination.CHAT },
                        onSelectProactiveTrip = { _ ->
                            currentDestination = AppDestination.PLAN_TRIP
                        }
                    )
                }
                AppDestination.PLAN_TRIP -> {
                    PlanTripScreen(
                        viewModel = viewModel,
                        onNavigateBack = { currentDestination = AppDestination.CHAT },
                        onTripCreated = { _ ->
                            currentDestination = AppDestination.ITINERARY
                        }
                    )
                }
            }
        }
    }
}
