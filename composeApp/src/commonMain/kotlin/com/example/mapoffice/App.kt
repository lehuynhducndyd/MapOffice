package com.example.mapoffice

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.slideInHorizontally
import androidx.compose.animation.slideInVertically
import androidx.compose.animation.slideOutHorizontally
import androidx.compose.animation.slideOutVertically
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.NavigationBar
import androidx.compose.material3.NavigationBarItem
import androidx.compose.material3.NavigationBarItemDefaults
import androidx.compose.material3.NavigationRail
import androidx.compose.material3.NavigationRailItem
import androidx.compose.material3.NavigationRailItemDefaults
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.windowsizeclass.ExperimentalMaterial3WindowSizeClassApi
import androidx.compose.material3.windowsizeclass.WindowWidthSizeClass
import androidx.compose.material3.windowsizeclass.calculateWindowSizeClass
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.navigation.compose.currentBackStackEntryAsState
import androidx.navigation.compose.rememberNavController
import com.example.mapoffice.theme.MapOfficeTheme
import mapoffice.composeapp.generated.resources.Res
import mapoffice.composeapp.generated.resources.ic_oui_community_outline
import mapoffice.composeapp.generated.resources.ic_oui_home_outline
import mapoffice.composeapp.generated.resources.other_device_outline
import org.jetbrains.compose.resources.painterResource


@OptIn(ExperimentalMaterial3WindowSizeClassApi::class)
@Composable
@Preview
fun App() {
    MapOfficeTheme {
        val navController = rememberNavController()
        val navBackStackEntry by navController.currentBackStackEntryAsState()
        val currentRoute = navBackStackEntry?.destination?.route
        val windowSizeClass = calculateWindowSizeClass()
        val isHomeTab = currentRoute == "room" || currentRoute?.startsWith("room") == true
        val showNav = currentRoute == "room" ||
                currentRoute == "group" || currentRoute == "product" ||
                (currentRoute?.startsWith("room/detail/price") == true && windowSizeClass.widthSizeClass != WindowWidthSizeClass.Compact)
        if (windowSizeClass.widthSizeClass == WindowWidthSizeClass.Compact) {
            Scaffold(
                bottomBar = {
                    AnimatedVisibility(
                        visible = showNav,
                        enter = slideInVertically(initialOffsetY = { it }) + fadeIn(), // Trượt từ dưới lên
                        exit = slideOutVertically(targetOffsetY = { it }) + fadeOut()
                    ) {
                        NavigationBar(
                            containerColor = MaterialTheme.colorScheme.surfaceVariant
                        ) {
                            // Nút Home
                            NavigationBarItem(
                                icon = {
                                    Icon(
                                        painterResource(Res.drawable.ic_oui_home_outline),
                                        "Home"
                                    )
                                },
                                label = { Text("Room") },
                                selected = isHomeTab,
                                onClick = {
                                    navController.navigate("room") {
                                        popUpTo("room") { saveState = true }
                                        launchSingleTop = true
                                        restoreState = true
                                    }
                                },
                                colors = NavigationBarItemDefaults.colors(
                                    selectedIconColor = MaterialTheme.colorScheme.onSurface,
                                    selectedTextColor = MaterialTheme.colorScheme.primary,
                                    indicatorColor = MaterialTheme.colorScheme.secondary,
                                )
                            )
                            // Nút Profile
                            NavigationBarItem(
                                icon = {
                                    Icon(
                                        painterResource(Res.drawable.ic_oui_community_outline),
                                        "Group"
                                    )
                                },
                                label = { Text("Group") },
                                selected = currentRoute == "group",
                                onClick = {
                                    navController.navigate("group") {
                                        popUpTo("room") { saveState = true }
                                        launchSingleTop = true
                                        restoreState = true
                                    }
                                },
                                colors = NavigationBarItemDefaults.colors(
                                    selectedIconColor = MaterialTheme.colorScheme.onSurface,
                                    selectedTextColor = MaterialTheme.colorScheme.primary,
                                    indicatorColor = MaterialTheme.colorScheme.secondary,
                                )
                            )
                            NavigationBarItem(
                                icon = {
                                    Icon(
                                        painterResource(Res.drawable.other_device_outline),
                                        "Product"
                                    )
                                },
                                label = { Text("Object") },
                                selected = currentRoute == "product",
                                onClick = {
                                    navController.navigate("product") {
                                        popUpTo("room") { saveState = true }
                                        launchSingleTop = true
                                        restoreState = true
                                    }
                                },
                                colors = NavigationBarItemDefaults.colors(
                                    selectedIconColor = MaterialTheme.colorScheme.onSurface,
                                    selectedTextColor = MaterialTheme.colorScheme.primary,
                                    indicatorColor = MaterialTheme.colorScheme.secondary,
                                )
                            )
                        }
                    }
                }
            ) { innerPadding ->
                AppNavGraph(
                    navController = navController,
                    padding = innerPadding
                )
            }
        } else {
            Row(modifier = Modifier.fillMaxSize()) {

                // --- PHẦN TRÁI: Navigation Rail ---

                AnimatedVisibility(
                    visible = showNav,
                    enter = slideInHorizontally(initialOffsetX = { -it }) + fadeIn(), // Trượt từ trái ra
                    exit = slideOutHorizontally(targetOffsetX = { -it }) + fadeOut()
                ) {
                    NavigationRail(
                        containerColor = MaterialTheme.colorScheme.surfaceVariant,
                        header = {
                            Spacer(modifier = Modifier.height(50.dp))
                        }
                    ) {

                        // Nút Home
                        NavigationRailItem(
                            icon = {
                                Icon(
                                    painterResource(Res.drawable.ic_oui_home_outline),
                                    "Home"
                                )
                            },
                            label = { Text("Room") },
                            selected = isHomeTab,
                            onClick = {
                                navController.navigate("room") {
                                    popUpTo("room") { saveState = true }
                                    launchSingleTop = true
                                    restoreState = true
                                }
                            },
                            colors = NavigationRailItemDefaults.colors(
                                selectedIconColor = MaterialTheme.colorScheme.onSurface,
                                selectedTextColor = MaterialTheme.colorScheme.primary,
                                indicatorColor = MaterialTheme.colorScheme.secondary,
                            )
                        )
                        // Nút Profile
                        NavigationRailItem(
                            icon = {
                                Icon(
                                    painterResource(Res.drawable.ic_oui_community_outline),
                                    "Group"
                                )
                            },
                            label = { Text("Group") },
                            selected = currentRoute == "group",
                            onClick = {
                                navController.navigate("group") {
                                    popUpTo("room") { saveState = true }
                                    launchSingleTop = true
                                    restoreState = true
                                }
                            },
                            colors = NavigationRailItemDefaults.colors(
                                selectedIconColor = MaterialTheme.colorScheme.onSurface,
                                selectedTextColor = MaterialTheme.colorScheme.primary,
                                indicatorColor = MaterialTheme.colorScheme.secondary,
                            )
                        )
                        NavigationRailItem(
                            icon = {
                                Icon(
                                    painterResource(Res.drawable.other_device_outline),
                                    "Product"
                                )
                            },
                            label = { Text("Object") },
                            selected = currentRoute == "product",
                            onClick = {
                                navController.navigate("product") {
                                    popUpTo("room") { saveState = true }
                                    launchSingleTop = true
                                    restoreState = true
                                }
                            },
                            colors = NavigationRailItemDefaults.colors(
                                selectedIconColor = MaterialTheme.colorScheme.onSurface,
                                selectedTextColor = MaterialTheme.colorScheme.primary,
                                indicatorColor = MaterialTheme.colorScheme.secondary,
                            )
                        )
                    }
                }

                Scaffold(modifier = Modifier.weight(1f)) { innerPadding ->
                    AppNavGraph(
                        navController = navController,
                    )
                }
            }
        }
    }
}