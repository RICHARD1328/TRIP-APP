package com.tripdirect.ui.screens.splash

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import com.tripdirect.ui.components.TripDirectLogo
import com.tripdirect.viewmodel.AuthViewModel
import kotlinx.coroutines.delay

@Composable
fun SplashScreen(
    viewModel: AuthViewModel,
    onNavigateToLogin: () -> Unit,
    onNavigateToHome: () -> Unit
) {
    val context = LocalContext.current

    LaunchedEffect(Unit) {
        delay(1200)
        viewModel.checkAuthState(context) { isLoggedIn ->
            if (isLoggedIn) {
                onNavigateToHome()
            } else {
                onNavigateToLogin()
            }
        }
    }

    Box(
        modifier = Modifier.fillMaxSize(),
        contentAlignment = Alignment.Center
    ) {
        TripDirectLogo()
    }
}
