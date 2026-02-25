package com.jaffetvr.syncbid.navigation

import androidx.compose.runtime.Composable
import androidx.navigation.NavHostController
import androidx.navigation.NavType
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.navigation
import androidx.navigation.navArgument
import com.jaffetvr.syncbid.features.admin.presentation.screens.AdminDashboardScreen
import com.jaffetvr.syncbid.features.admin.presentation.screens.CreateAuctionScreen
import com.jaffetvr.syncbid.features.admin.presentation.screens.InventoryScreen
import com.jaffetvr.syncbid.features.auth.presentation.screens.LoginScreen
import com.jaffetvr.syncbid.features.auth.presentation.screens.RegisterScreen
import com.jaffetvr.syncbid.features.users.presentation.screens.AuctionDetailScreen
import com.jaffetvr.syncbid.features.users.presentation.screens.DashboardScreen

// Route constants
object Routes {
    // Auth
    const val AUTH_GRAPH = "auth_graph"
    const val LOGIN = "login"
    const val REGISTER = "register"

    // Users
    const val USERS_GRAPH = "users_graph"
    const val DASHBOARD = "dashboard"
    const val AUCTION_DETAIL = "auction_detail/{auctionId}"
    fun auctionDetail(auctionId: String) = "auction_detail/$auctionId"

    // Admin
    const val ADMIN_GRAPH = "admin_graph"
    const val ADMIN_DASHBOARD = "admin_dashboard"
    const val CREATE_AUCTION = "create_auction"
    const val INVENTORY = "inventory"
}

@Composable
fun SyncBidNavGraph(
    navController: NavHostController,
    startDestination: String = Routes.AUTH_GRAPH
) {
    NavHost(
        navController = navController,
        startDestination = startDestination
    ) {
        // ─── Auth Graph ──────────────────────────────────────────
        navigation(
            startDestination = Routes.LOGIN,
            route = Routes.AUTH_GRAPH
        ) {
            composable(Routes.LOGIN) {
                LoginScreen(
                    onNavigateToDashboard = {
                        navController.navigate(Routes.USERS_GRAPH) {
                            popUpTo(Routes.AUTH_GRAPH) { inclusive = true }
                        }
                    },
                    onNavigateToRegister = {
                        navController.navigate(Routes.REGISTER)
                    }
                )
            }
            composable(Routes.REGISTER) {
                RegisterScreen(
                    onNavigateToDashboard = {
                        navController.navigate(Routes.USERS_GRAPH) {
                            popUpTo(Routes.AUTH_GRAPH) { inclusive = true }
                        }
                    },
                    onNavigateToLogin = {
                        navController.popBackStack()
                    }
                )
            }
        }

        // ─── Users Graph ─────────────────────────────────────────
        navigation(
            startDestination = Routes.DASHBOARD,
            route = Routes.USERS_GRAPH
        ) {
            composable(Routes.DASHBOARD) {
                DashboardScreen(
                    onNavigateToDetail = { auctionId ->
                        navController.navigate(Routes.auctionDetail(auctionId))
                    }
                )
            }
            composable(
                route = Routes.AUCTION_DETAIL,
                arguments = listOf(
                    navArgument("auctionId") { type = NavType.StringType }
                )
            ) {
                AuctionDetailScreen(
                    onNavigateBack = { navController.popBackStack() }
                )
            }
        }

        // ─── Admin Graph ─────────────────────────────────────────
        navigation(
            startDestination = Routes.ADMIN_DASHBOARD,
            route = Routes.ADMIN_GRAPH
        ) {
            composable(Routes.ADMIN_DASHBOARD) {
                AdminDashboardScreen(
                    onNavigateToCreateAuction = {
                        navController.navigate(Routes.CREATE_AUCTION)
                    },
                    onNavigateToInventory = {
                        navController.navigate(Routes.INVENTORY)
                    }
                )
            }
            composable(Routes.CREATE_AUCTION) {
                CreateAuctionScreen(
                    onBack = { navController.popBackStack() },
                    onSuccess = { navController.popBackStack() }
                )
            }
            composable(Routes.INVENTORY) {
                InventoryScreen(
                    onBack = { navController.popBackStack() }
                )
            }
        }
    }
}
