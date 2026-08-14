package com.tripdirect.ui.screens.auth

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
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
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.tripdirect.ui.components.AuthTextField
import com.tripdirect.ui.components.PrimaryButton
import com.tripdirect.ui.components.SocialLoginButton
import com.tripdirect.ui.components.TripDirectLogo
import com.tripdirect.viewmodel.AuthViewModel
import kotlinx.coroutines.launch

@Composable
fun LoginScreen(
    viewModel: AuthViewModel,
    onNavigateToRegister: () -> Unit,
    onNavigateToHome: () -> Unit,
    onNavigateToForgotPassword: () -> Unit
) {
    val context = LocalContext.current
    val email by viewModel.loginEmail.collectAsState()
    val password by viewModel.loginPassword.collectAsState()
    val isLoading by viewModel.isLoginLoading.collectAsState()
    val error by viewModel.loginError.collectAsState()
    val success by viewModel.loginSuccess.collectAsState()

    val snackbarHostState = remember { SnackbarHostState() }
    val scope = rememberCoroutineScope()

    LaunchedEffect(success) {
        if (success) {
            viewModel.resetLoginState()
            onNavigateToHome()
        }
    }

    LaunchedEffect(error) {
        error?.let {
            if (it == "Invalid email or password" || it == "No account found. Please create an account first.") {
                scope.launch { snackbarHostState.showSnackbar(it) }
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
                text = "Welcome Back \uD83D\uDC4B",
                fontSize = 24.sp,
                fontWeight = FontWeight.Bold,
                color = MaterialTheme.colorScheme.onSurface
            )

            Text(
                text = "Continue your journey",
                fontSize = 14.sp,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )

            Spacer(modifier = Modifier.height(32.dp))

            AuthTextField(
                value = email,
                onValueChange = viewModel::onLoginEmailChange,
                label = "Email",
                placeholder = "Enter your email",
                keyboardOptions = KeyboardOptions(
                    keyboardType = KeyboardType.Email,
                    imeAction = ImeAction.Next
                ),
                errorMessage = if (error != null && error!!.contains("email", ignoreCase = true) && error != "Invalid email or password" && error != "No account found. Please create an account first.") error else null
            )

            Spacer(modifier = Modifier.height(16.dp))

            AuthTextField(
                value = password,
                onValueChange = viewModel::onLoginPasswordChange,
                label = "Password",
                placeholder = "Enter your password",
                isPassword = true,
                keyboardOptions = KeyboardOptions(
                    keyboardType = KeyboardType.Password,
                    imeAction = ImeAction.Done
                ),
                errorMessage = if (error != null && error!!.contains("password", ignoreCase = true) && error != "Invalid email or password" && error != "No account found. Please create an account first.") error else null
            )

            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.End
            ) {
                TextButton(onClick = onNavigateToForgotPassword) {
                    Text("Forgot Password?")
                }
            }

            Spacer(modifier = Modifier.height(16.dp))

            PrimaryButton(
                text = "LOGIN",
                onClick = { viewModel.login(context) },
                isLoading = isLoading
            )

            Spacer(modifier = Modifier.height(24.dp))

            Row(
                verticalAlignment = Alignment.CenterVertically,
                modifier = Modifier.fillMaxWidth()
            ) {
                Spacer(modifier = Modifier.weight(1f).height(1.dp))
                Text(
                    text = "── OR ──",
                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                    modifier = Modifier.padding(horizontal = 8.dp)
                )
                Spacer(modifier = Modifier.weight(1f).height(1.dp))
            }

            Spacer(modifier = Modifier.height(24.dp))

            SocialLoginButton(
                text = "Continue with Google",
                onClick = {
                    scope.launch {
                        snackbarHostState.showSnackbar("Google Sign-In is not configured yet.")
                    }
                }
            )

            Spacer(modifier = Modifier.height(32.dp))

            Row(
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.Center
            ) {
                Text(
                    text = "Don't have an account?",
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
                TextButton(onClick = onNavigateToRegister) {
                    Text("Create Account")
                }
            }

            Spacer(modifier = Modifier.height(24.dp))
        }
    }
}
