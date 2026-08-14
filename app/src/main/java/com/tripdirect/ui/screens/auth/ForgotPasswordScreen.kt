package com.tripdirect.ui.screens.auth

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.SnackbarHost
import androidx.compose.material3.SnackbarHostState
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.tripdirect.ui.components.AuthTextField
import com.tripdirect.ui.components.PrimaryButton
import com.tripdirect.ui.components.TripDirectLogo
import com.tripdirect.viewmodel.AuthViewModel
import kotlinx.coroutines.launch

@Composable
fun ForgotPasswordScreen(
    viewModel: AuthViewModel,
    onNavigateToLogin: () -> Unit
) {
    val context = LocalContext.current
    val email by viewModel.forgotPasswordEmail.collectAsState()
    val isLoading by viewModel.isForgotPasswordLoading.collectAsState()
    val success by viewModel.forgotPasswordSuccess.collectAsState()

    val snackbarHostState = remember { SnackbarHostState() }
    val scope = rememberCoroutineScope()

    LaunchedEffect(success) {
        if (success) {
            scope.launch {
                snackbarHostState.showSnackbar("Password recovery will be available soon.")
                viewModel.resetForgotPasswordState()
            }
        }
    }

    Scaffold(
        snackbarHost = { SnackbarHost(snackbarHostState) }
    ) { paddingValues ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues)
                .padding(horizontal = 24.dp)
                .verticalScroll(rememberScrollState()),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Spacer(modifier = Modifier.height(48.dp))

            TripDirectLogo()

            Spacer(modifier = Modifier.height(32.dp))

            Text(
                text = "Forgot Password",
                fontSize = 24.sp,
                fontWeight = FontWeight.Bold,
                color = MaterialTheme.colorScheme.onSurface
            )

            Text(
                text = "Enter your email address to request password recovery.",
                fontSize = 14.sp,
                color = MaterialTheme.colorScheme.onSurfaceVariant,
                textAlign = TextAlign.Center
            )

            Spacer(modifier = Modifier.height(32.dp))

            AuthTextField(
                value = email,
                onValueChange = viewModel::onForgotPasswordEmailChange,
                label = "Email",
                placeholder = "Enter your registered email",
                keyboardOptions = KeyboardOptions(
                    keyboardType = KeyboardType.Email,
                    imeAction = ImeAction.Done
                )
            )

            Spacer(modifier = Modifier.height(24.dp))

            PrimaryButton(
                text = "SEND RESET LINK",
                onClick = { viewModel.sendResetLink(context) },
                isLoading = isLoading
            )

            Spacer(modifier = Modifier.height(24.dp))

            TextButton(onClick = onNavigateToLogin) {
                Text("Back to Login")
            }

            Spacer(modifier = Modifier.height(24.dp))
        }
    }
}
