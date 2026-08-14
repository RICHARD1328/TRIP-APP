package com.tripdirect.ui.components

import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.AutoAwesome
import androidx.compose.material.icons.filled.Explore
import androidx.compose.material.icons.filled.Home
import androidx.compose.material.icons.filled.Luggage
import androidx.compose.material.icons.filled.Person
import androidx.compose.material3.Icon
import androidx.compose.material3.NavigationBar
import androidx.compose.material3.NavigationBarItem
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.vector.ImageVector

data class BottomNavItem(
    val title: String,
    val route: String,
    val icon: ImageVector
)

@Composable
fun TripDirectBottomNavigation(
    currentRoute: String?,
    onNavigate: (String) -> Unit
) {
    val items = listOf(
        BottomNavItem("Home", "home", Icons.Default.Home),
        BottomNavItem("Explore", "explore", Icons.Default.Explore),
        BottomNavItem("Trips", "trips", Icons.Default.Luggage),
        BottomNavItem("AI Planner", "planner", Icons.Default.AutoAwesome),
        BottomNavItem("Profile", "profile", Icons.Default.Person)
    )

    NavigationBar {
        items.forEach { item ->
            NavigationBarItem(
                icon = { Icon(imageVector = item.icon, contentDescription = item.title) },
                label = { Text(item.title) },
                selected = currentRoute == item.route,
                onClick = {
                    if (currentRoute != item.route) {
                        onNavigate(item.route)
                    }
                }
            )
        }
    }
}
