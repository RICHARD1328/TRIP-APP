package com.tripdirect.ui.screens.profile

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.tripdirect.data.UserPreferencesManager
import com.tripdirect.data.network.ApiClient
import com.tripdirect.data.network.BackendUrlValidator
import com.tripdirect.data.network.ConnectionTestResult
import kotlinx.coroutines.launch

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ServerSettingsScreen(
    onNavigateBack: () -> Unit
) {
    val context = LocalContext.current
    val coroutineScope = rememberCoroutineScope()
    val preferencesManager = remember { UserPreferencesManager.getInstance(context) }
    val snackbarHostState = remember { SnackbarHostState() }

    val savedBackendUrl by preferencesManager.backendUrlFlow.collectAsState(
        initial = ApiClient.getBaseUrl()
    )

    var urlInput by remember { mutableStateOf(savedBackendUrl) }
    var validationError by remember { mutableStateOf<String?>(null) }
    var isTestingConnection by remember { mutableStateOf(false) }
    var testResult by remember { mutableStateOf<ConnectionTestResult?>(null) }

    // Keep urlInput in sync when savedBackendUrl updates
    LaunchedEffect(savedBackendUrl) {
        urlInput = savedBackendUrl
    }

    Scaffold(
        snackbarHost = { SnackbarHost(snackbarHostState) },
        topBar = {
            TopAppBar(
                title = { Text("Backend Server Settings", fontWeight = FontWeight.Bold) },
                navigationIcon = {
                    IconButton(onClick = onNavigateBack) {
                        Icon(Icons.AutoMirrored.Filled.ArrowBack, contentDescription = "Back")
                    }
                }
            )
        }
    ) { paddingValues ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues)
                .verticalScroll(rememberScrollState())
                .padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            // Header card
            Card(
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(16.dp),
                colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.primaryContainer.copy(alpha = 0.5f))
            ) {
                Row(
                    modifier = Modifier.padding(16.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Icon(
                        imageVector = Icons.Default.Dns,
                        contentDescription = null,
                        tint = MaterialTheme.colorScheme.primary,
                        modifier = Modifier.size(36.dp)
                    )
                    Spacer(modifier = Modifier.width(16.dp))
                    Column {
                        Text(
                            text = "Backend Server URL",
                            style = MaterialTheme.typography.titleMedium,
                            fontWeight = FontWeight.Bold,
                            color = MaterialTheme.colorScheme.onPrimaryContainer
                        )
                        Text(
                            text = "Configure API target for emulator, physical device on LAN, or remote server.",
                            style = MaterialTheme.typography.bodySmall,
                            color = MaterialTheme.colorScheme.onPrimaryContainer.copy(alpha = 0.8f)
                        )
                    }
                }
            }

            // Current Active URL Badge
            Surface(
                shape = RoundedCornerShape(12.dp),
                color = MaterialTheme.colorScheme.surfaceVariant,
                modifier = Modifier.fillMaxWidth()
            ) {
                Row(
                    modifier = Modifier.padding(horizontal = 14.dp, vertical = 10.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Icon(
                        imageVector = Icons.Default.Link,
                        contentDescription = null,
                        modifier = Modifier.size(18.dp),
                        tint = MaterialTheme.colorScheme.primary
                    )
                    Spacer(modifier = Modifier.width(8.dp))
                    Text(
                        text = "Active Base URL: ",
                        style = MaterialTheme.typography.labelMedium,
                        fontWeight = FontWeight.Bold
                    )
                    Text(
                        text = ApiClient.getBaseUrl(),
                        style = MaterialTheme.typography.labelMedium,
                        fontFamily = FontFamily.Monospace,
                        color = MaterialTheme.colorScheme.primary
                    )
                }
            }

            // Input TextField
            OutlinedTextField(
                value = urlInput,
                onValueChange = {
                    urlInput = it
                    validationError = null
                    testResult = null
                },
                label = { Text("Backend Base URL") },
                placeholder = { Text("http://10.0.2.2:8000/") },
                leadingIcon = {
                    Icon(Icons.Default.Cloud, contentDescription = null)
                },
                trailingIcon = {
                    if (urlInput.isNotBlank()) {
                        IconButton(onClick = { urlInput = "" }) {
                            Icon(Icons.Default.Clear, contentDescription = "Clear")
                        }
                    }
                },
                isError = validationError != null,
                supportingText = {
                    if (validationError != null) {
                        Text(
                            text = validationError ?: "",
                            color = MaterialTheme.colorScheme.error
                        )
                    } else {
                        Text("Include protocol ('http://' or 'https://') and optional port.")
                    }
                },
                singleLine = true,
                modifier = Modifier.fillMaxWidth()
            )

            // Preset Quick Selectors
            Text(
                text = "QUICK PRESETS",
                style = MaterialTheme.typography.labelSmall,
                fontWeight = FontWeight.Bold,
                color = MaterialTheme.colorScheme.primary
            )

            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                SuggestionChip(
                    onClick = {
                        urlInput = UserPreferencesManager.DEFAULT_BACKEND_URL
                        validationError = null
                    },
                    label = { Text("Emulator") },
                    icon = { Icon(Icons.Default.PhoneAndroid, contentDescription = null, modifier = Modifier.size(16.dp)) }
                )
                SuggestionChip(
                    onClick = {
                        urlInput = "http://127.0.0.1:8000/"
                        validationError = null
                    },
                    label = { Text("Localhost") },
                    icon = { Icon(Icons.Default.Computer, contentDescription = null, modifier = Modifier.size(16.dp)) }
                )
                SuggestionChip(
                    onClick = {
                        urlInput = "http://192.168.1.100:8000/"
                        validationError = null
                    },
                    label = { Text("LAN IP") },
                    icon = { Icon(Icons.Default.Wifi, contentDescription = null, modifier = Modifier.size(16.dp)) }
                )
            }

            // Action Buttons (Save & Reset)
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                Button(
                    onClick = {
                        when (val validation = BackendUrlValidator.validateAndNormalize(urlInput)) {
                            is BackendUrlValidator.ValidationResult.Valid -> {
                                val normalized = validation.normalizedUrl
                                urlInput = normalized
                                validationError = null
                                coroutineScope.launch {
                                    preferencesManager.saveBackendUrl(normalized)
                                    ApiClient.setBaseUrl(normalized)
                                    snackbarHostState.showSnackbar("✓ Backend URL saved: $normalized")
                                }
                            }
                            is BackendUrlValidator.ValidationResult.Invalid -> {
                                validationError = validation.errorMessage
                                coroutineScope.launch {
                                    snackbarHostState.showSnackbar("Invalid URL: ${validation.errorMessage}")
                                }
                            }
                        }
                    },
                    modifier = Modifier.weight(1f)
                ) {
                    Icon(Icons.Default.Save, contentDescription = null, modifier = Modifier.size(18.dp))
                    Spacer(modifier = Modifier.width(8.dp))
                    Text("Save URL")
                }

                OutlinedButton(
                    onClick = {
                        val defaultUrl = UserPreferencesManager.DEFAULT_BACKEND_URL
                        urlInput = defaultUrl
                        validationError = null
                        testResult = null
                        coroutineScope.launch {
                            preferencesManager.resetBackendUrl()
                            ApiClient.setBaseUrl(defaultUrl)
                            snackbarHostState.showSnackbar("Restored default: $defaultUrl")
                        }
                    },
                    modifier = Modifier.weight(1f)
                ) {
                    Icon(Icons.Default.RestartAlt, contentDescription = null, modifier = Modifier.size(18.dp))
                    Spacer(modifier = Modifier.width(8.dp))
                    Text("Reset Default")
                }
            }

            // Connection Test Action
            OutlinedButton(
                onClick = {
                    when (val validation = BackendUrlValidator.validateAndNormalize(urlInput)) {
                        is BackendUrlValidator.ValidationResult.Valid -> {
                            isTestingConnection = true
                            testResult = null
                            coroutineScope.launch {
                                val result = ApiClient.testConnection(validation.normalizedUrl)
                                isTestingConnection = false
                                testResult = result
                            }
                        }
                        is BackendUrlValidator.ValidationResult.Invalid -> {
                            validationError = validation.errorMessage
                        }
                    }
                },
                enabled = !isTestingConnection,
                modifier = Modifier.fillMaxWidth()
            ) {
                if (isTestingConnection) {
                    CircularProgressIndicator(
                        modifier = Modifier.size(16.dp),
                        strokeWidth = 2.dp
                    )
                    Spacer(modifier = Modifier.width(8.dp))
                    Text("Testing Connection...")
                } else {
                    Icon(Icons.Default.NetworkCheck, contentDescription = null, modifier = Modifier.size(18.dp))
                    Spacer(modifier = Modifier.width(8.dp))
                    Text("Test Connection (/api/v1/health)")
                }
            }

            // Test Result Banner
            testResult?.let { result ->
                when (result) {
                    is ConnectionTestResult.Success -> {
                        Card(
                            modifier = Modifier.fillMaxWidth(),
                            shape = RoundedCornerShape(12.dp),
                            colors = CardDefaults.cardColors(containerColor = Color(0xFFE8F5E9))
                        ) {
                            Row(
                                modifier = Modifier.padding(14.dp),
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Icon(
                                    imageVector = Icons.Default.CheckCircle,
                                    contentDescription = null,
                                    tint = Color(0xFF2E7D32),
                                    modifier = Modifier.size(24.dp)
                                )
                                Spacer(modifier = Modifier.width(12.dp))
                                Column {
                                    Text(
                                        text = "Connection Successful",
                                        fontWeight = FontWeight.Bold,
                                        color = Color(0xFF1B5E20)
                                    )
                                    Text(
                                        text = result.message,
                                        style = MaterialTheme.typography.bodySmall,
                                        color = Color(0xFF2E7D32)
                                    )
                                }
                            }
                        }
                    }
                    is ConnectionTestResult.Error -> {
                        Card(
                            modifier = Modifier.fillMaxWidth(),
                            shape = RoundedCornerShape(12.dp),
                            colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.errorContainer)
                        ) {
                            Row(
                                modifier = Modifier.padding(14.dp),
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Icon(
                                    imageVector = Icons.Default.ErrorOutline,
                                    contentDescription = null,
                                    tint = MaterialTheme.colorScheme.error,
                                    modifier = Modifier.size(24.dp)
                                )
                                Spacer(modifier = Modifier.width(12.dp))
                                Column {
                                    Text(
                                        text = "Connection Failed",
                                        fontWeight = FontWeight.Bold,
                                        color = MaterialTheme.colorScheme.onErrorContainer
                                    )
                                    Text(
                                        text = result.message,
                                        style = MaterialTheme.typography.bodySmall,
                                        color = MaterialTheme.colorScheme.onErrorContainer
                                    )
                                }
                            }
                        }
                    }
                }
            }

            // Developer Reference Guide
            Card(
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(14.dp),
                colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface)
            ) {
                Column(modifier = Modifier.padding(16.dp)) {
                    Text(
                        text = "ENVIRONMENT REFERENCE",
                        style = MaterialTheme.typography.labelSmall,
                        fontWeight = FontWeight.Bold,
                        color = MaterialTheme.colorScheme.primary
                    )
                    Spacer(modifier = Modifier.height(8.dp))
                    Text(
                        text = "• Android Emulator:\n  http://10.0.2.2:8000/ (routes to host machine localhost)",
                        style = MaterialTheme.typography.bodySmall,
                        fontFamily = FontFamily.Monospace
                    )
                    Spacer(modifier = Modifier.height(6.dp))
                    Text(
                        text = "• Physical Device (USB / Wi-Fi LAN):\n  http://<YOUR_PC_LAN_IP>:8000/ (e.g. http://192.168.1.50:8000/)",
                        style = MaterialTheme.typography.bodySmall,
                        fontFamily = FontFamily.Monospace
                    )
                    Spacer(modifier = Modifier.height(6.dp))
                    Text(
                        text = "• Remote / Staging Server:\n  https://api.yourdomain.com/",
                        style = MaterialTheme.typography.bodySmall,
                        fontFamily = FontFamily.Monospace
                    )
                }
            }
        }
    }
}
