package com.jaffetvr.syncbid.core.navigation

import androidx.compose.runtime.Composable
import androidx.navigation.NavType
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import androidx.navigation.navArgument
import androidx.hilt.navigation.compose.hiltViewModel
import com.jaffetvr.syncbid.features.admin.presentation.screens.AdminDashboardScreen
import com.jaffetvr.syncbid.features.admin.presentation.screens.CreateAuctionScreen
import com.jaffetvr.syncbid.features.admin.presentation.screens.InventoryScreen
import com.jaffetvr.syncbid.features.auth.presentation.screens.LoginScreen
import com.jaffetvr.syncbid.features.auth.presentation.screens.RegisterScreen
import com.jaffetvr.syncbid.features.users.presentation.screens.AuctionDetailScreen
import com.jaffetvr.syncbid.features.users.presentation.screens.DashboardScreen

@Composable
fun AppNavigation() {
    val navController = rememberNavController()

    NavHost(
        navController = navController,
        startDestination = "login"
    ) {
        // ─── Auth ────────────────────────────────────────────────
        composable("login") {
            LoginScreen(
                onNavigateToDashboard = {
                    navController.navigate("dashboard") {
                        popUpTo("login") { inclusive = true }
                    }
                },
                onNavigateToRegister = {
                    navController.navigate("register")
                },
                viewModel = hiltViewModel()
            )
        }

        composable("register") {
            RegisterScreen(
                onNavigateToDashboard = {
                    navController.navigate("dashboard") {
                        popUpTo("login") { inclusive = true }
                    }
                },
                onNavigateToLogin = {
                    navController.popBackStack()
                },
                viewModel = hiltViewModel()
            )
        }

        // ─── Users ───────────────────────────────────────────────
        composable("dashboard") {
            DashboardScreen(
                onNavigateToDetail = { auctionId ->
                    navController.navigate("auction_detail/$auctionId")
                },
                viewModel = hiltViewModel()
            )
        }

        composable(
            route = "auction_detail/{auctionId}",
            arguments = listOf(
                navArgument("auctionId") { type = NavType.StringType }
            )
        ) {
            AuctionDetailScreen(
                onNavigateBack = { navController.popBackStack() },
                viewModel = hiltViewModel()
            )
        }

        // ─── Admin ───────────────────────────────────────────────
        composable("admin_dashboard") {
            AdminDashboardScreen(
                onNavigateToCreateAuction = {
                    navController.navigate("create_auction")
                },
                onNavigateToInventory = {
                    navController.navigate("inventory")
                },
                viewModel = hiltViewModel()
            )
        }

        composable("create_auction") {
            CreateAuctionScreen(
                onBack = { navController.popBackStack() },
                onSuccess = { navController.popBackStack() },
                viewModel = hiltViewModel()
            )
        }

        composable("inventory") {
            InventoryScreen(
                onBack = { navController.popBackStack() },
                viewModel = hiltViewModel()
            )
        }
    }
}
