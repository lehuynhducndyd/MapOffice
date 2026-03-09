package com.example.mapoffice

import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.runtime.Composable
import androidx.navigation.NavHostController
import androidx.navigation.NavType
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.navArgument
import androidx.navigation.toRoute
import com.example.mapoffice.screens.canvas.FloorEditor
import com.example.mapoffice.screens.group.DetailGroupScreen
import com.example.mapoffice.screens.group.GroupScreen
import com.example.mapoffice.screens.main.DetailRoomScreen
import com.example.mapoffice.screens.main.MainScreen
import com.example.mapoffice.screens.main.PriceScreen
import com.example.mapoffice.screens.product.ProductScreen
import kotlinx.serialization.Serializable

sealed class Screen(val route: String) {
    object Main : Screen(route = "room")
    object RoomDetail : Screen(route = "room/detail")
    object Group : Screen(route = "group")
    object GroupDetail : Screen(route = "group/detail")
    object Canvas : Screen(route = "canvas")

    object Product : Screen(route = "product")
    object PriceScreen : Screen(route = "room/detail/price")
}

@Serializable
data class CanvasScreen(val roomId: Int)

@Serializable
data class DetailRoomScreen(val roomId: Int)

@Serializable
data class PriceScreen(val roomId: Int)


@Serializable
data class DetailGroupScreen(val groupId: Int)

@Composable
fun AppNavGraph(
    navController: NavHostController,
    padding: PaddingValues = PaddingValues.Zero
) {
    NavHost(navController = navController, startDestination = Screen.Main.route) {
        composable(Screen.Main.route) {
            MainScreen(navController, padding = padding)
        }
        composable(Screen.Group.route) {
            GroupScreen(navController, padding = padding)
        }
        composable(Screen.Product.route) {
            ProductScreen(navController, padding = padding)
        }
        composable(
            route = "${Screen.Canvas.route}/{roomId}",
            arguments = listOf(
                navArgument("roomId") {
                    type = NavType.IntType
                    nullable = false
                    defaultValue = 0
                }
            )
        ) { backStackEntry ->
            val arg = backStackEntry.toRoute<CanvasScreen>()
            FloorEditor(roomId = arg.roomId, navController = navController)
        }
        composable(
            route = "${Screen.RoomDetail.route}/{roomId}",
            arguments = listOf(
                navArgument("roomId") {
                    type = NavType.IntType
                    nullable = false
                    defaultValue = 0
                }
            )
        ) { backStackEntry ->
            val arg = backStackEntry.toRoute<DetailRoomScreen>()
            DetailRoomScreen(roomId = arg.roomId, navController = navController, padding = padding)
        }
        composable(
            route = "${Screen.GroupDetail.route}/{groupId}",
            arguments = listOf(
                navArgument("groupId") {
                    type = NavType.IntType
                    nullable = false
                    defaultValue = 0
                }
            )
        ) { backStackEntry ->
            val arg = backStackEntry.toRoute<DetailGroupScreen>()
            DetailGroupScreen(
                groupId = arg.groupId,
                navController = navController,
                padding = padding
            )
        }
        composable(
            route = "${Screen.PriceScreen.route}/{roomId}",
            arguments = listOf(
                navArgument("roomId") {
                    type = NavType.IntType
                    nullable = false
                    defaultValue = 0
                }
            )
        ) { backStackEntry ->
            val arg = backStackEntry.toRoute<PriceScreen>()
            PriceScreen(roomId = arg.roomId, navController = navController, padding = padding)
        }

    }
}