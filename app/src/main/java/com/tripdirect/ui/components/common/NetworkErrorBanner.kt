package com.tripdirect.ui.components.common

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.expandVertically
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.shrinkVertically
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.CloudOff
import androidx.compose.material.icons.filled.Refresh
import androidx.compose.material.icons.filled.WarningAmber
import androidx.compose.material.icons.filled.WifiOff
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.tripdirect.util.NetworkErrorState
import com.tripdirect.util.NetworkErrorType

/**
 * Reusable Network/Offline Error Banner matching TripDirect Material 3 design system.
 *
 * Visual layout:
 * ┌────────────────────────────────────────────┐
 * │ ⚠ No internet connection                  │
 * │ Check your connection and try again.      │
 * │                              [Retry]       │
 * └────────────────────────────────────────────┘
 *
 * Ensures:
 * - Clear visibility and readability
 * - Accessible Retry action
 * - Duplicate retry prevention while retrying
 * - Distinguishes between device offline and server/API error styling
 */
@Composable
fun NetworkErrorBanner(
    title: String = "No internet connection",
    message: String = "Check your connection and try again.",
    isOffline: Boolean = true,
    isRetrying: Boolean = false,
    onRetry: (() -> Unit)? = null,
    modifier: Modifier = Modifier,
    icon: ImageVector? = null
) {
    val displayIcon = icon ?: if (isOffline) Icons.Default.WifiOff else Icons.Default.WarningAmber
    val containerColor = if (isOffline) {
        MaterialTheme.colorScheme.errorContainer.copy(alpha = 0.95f)
    } else {
        MaterialTheme.colorScheme.tertiaryContainer.copy(alpha = 0.95f)
    }
    val contentColor = if (isOffline) {
        MaterialTheme.colorScheme.onErrorContainer
    } else {
        MaterialTheme.colorScheme.onTertiaryContainer
    }
    val buttonBgColor = if (isOffline) {
        MaterialTheme.colorScheme.error
    } else {
        MaterialTheme.colorScheme.tertiary
    }
    val buttonContentColor = if (isOffline) {
        MaterialTheme.colorScheme.onError
    } else {
        MaterialTheme.colorScheme.onTertiary
    }

    Surface(
        modifier = modifier
            .fillMaxWidth()
            .padding(horizontal = 16.dp, vertical = 6.dp),
        shape = RoundedCornerShape(12.dp),
        color = containerColor,
        tonalElevation = 2.dp,
        shadowElevation = 2.dp
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 14.dp, vertical = 10.dp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            Row(
                modifier = Modifier.weight(1f),
                verticalAlignment = Alignment.CenterVertically
            ) {
                Icon(
                    imageVector = displayIcon,
                    contentDescription = if (isOffline) "Offline warning" else "Network error",
                    tint = contentColor,
                    modifier = Modifier.size(26.dp)
                )

                Spacer(modifier = Modifier.width(12.dp))

                Column {
                    Text(
                        text = title,
                        style = MaterialTheme.typography.titleSmall,
                        fontWeight = FontWeight.Bold,
                        color = contentColor
                    )
                    Spacer(modifier = Modifier.height(2.dp))
                    Text(
                        text = message,
                        style = MaterialTheme.typography.bodySmall,
                        color = contentColor.copy(alpha = 0.9f)
                    )
                }
            }

            if (onRetry != null) {
                Spacer(modifier = Modifier.width(8.dp))
                Button(
                    onClick = onRetry,
                    enabled = !isRetrying,
                    colors = ButtonDefaults.buttonColors(
                        containerColor = buttonBgColor,
                        contentColor = buttonContentColor,
                        disabledContainerColor = buttonBgColor.copy(alpha = 0.6f),
                        disabledContentColor = buttonContentColor.copy(alpha = 0.6f)
                    ),
                    contentPadding = PaddingValues(horizontal = 12.dp, vertical = 6.dp),
                    shape = RoundedCornerShape(8.dp)
                ) {
                    if (isRetrying) {
                        CircularProgressIndicator(
                            modifier = Modifier.size(16.dp),
                            strokeWidth = 2.dp,
                            color = buttonContentColor
                        )
                    } else {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Icon(
                                imageVector = Icons.Default.Refresh,
                                contentDescription = "Retry",
                                modifier = Modifier.size(14.dp)
                            )
                            Spacer(modifier = Modifier.width(4.dp))
                            Text(
                                text = "Retry",
                                fontSize = 12.sp,
                                fontWeight = FontWeight.Bold
                            )
                        }
                    }
                }
            }
        }
    }
}

/**
 * Convenience overload accepting a [NetworkErrorState].
 */
@Composable
fun NetworkErrorBanner(
    errorState: NetworkErrorState?,
    isRetrying: Boolean = false,
    onRetry: (() -> Unit)? = null,
    modifier: Modifier = Modifier
) {
    if (errorState == null) return
    val title = when (errorState.type) {
        NetworkErrorType.DEVICE_OFFLINE -> "No internet connection"
        NetworkErrorType.NETWORK_TIMEOUT -> "Connection Timeout"
        NetworkErrorType.SERVER_UNAVAILABLE, NetworkErrorType.SERVER_ERROR -> "Service Unavailable"
        NetworkErrorType.CLIENT_ERROR -> "Request Issue"
        else -> "Network Error"
    }

    NetworkErrorBanner(
        title = title,
        message = errorState.message,
        isOffline = errorState.isOffline,
        isRetrying = isRetrying,
        onRetry = onRetry,
        modifier = modifier
    )
}

/**
 * Reusable full-card or centered network error display for empty/failed screen states.
 */
@Composable
fun NetworkErrorCard(
    title: String = "No internet connection",
    message: String = "Check your connection and try again.",
    isOffline: Boolean = true,
    isRetrying: Boolean = false,
    onRetry: (() -> Unit)? = null,
    modifier: Modifier = Modifier
) {
    Card(
        modifier = modifier
            .fillMaxWidth()
            .padding(16.dp),
        shape = RoundedCornerShape(16.dp),
        colors = CardDefaults.cardColors(
            containerColor = if (isOffline) {
                MaterialTheme.colorScheme.errorContainer.copy(alpha = 0.4f)
            } else {
                MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.6f)
            }
        )
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(24.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center
        ) {
            Icon(
                imageVector = if (isOffline) Icons.Default.WifiOff else Icons.Default.CloudOff,
                contentDescription = null,
                tint = if (isOffline) MaterialTheme.colorScheme.error else MaterialTheme.colorScheme.primary,
                modifier = Modifier.size(48.dp)
            )

            Spacer(modifier = Modifier.height(16.dp))

            Text(
                text = title,
                style = MaterialTheme.typography.titleMedium,
                fontWeight = FontWeight.Bold,
                color = MaterialTheme.colorScheme.onSurface,
                textAlign = TextAlign.Center
            )

            Spacer(modifier = Modifier.height(8.dp))

            Text(
                text = message,
                style = MaterialTheme.typography.bodyMedium,
                color = MaterialTheme.colorScheme.onSurfaceVariant,
                textAlign = TextAlign.Center
            )

            if (onRetry != null) {
                Spacer(modifier = Modifier.height(20.dp))
                Button(
                    onClick = onRetry,
                    enabled = !isRetrying,
                    shape = RoundedCornerShape(10.dp)
                ) {
                    if (isRetrying) {
                        CircularProgressIndicator(
                            modifier = Modifier.size(18.dp),
                            strokeWidth = 2.dp,
                            color = MaterialTheme.colorScheme.onPrimary
                        )
                    } else {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Icon(
                                imageVector = Icons.Default.Refresh,
                                contentDescription = "Retry",
                                modifier = Modifier.size(16.dp)
                            )
                            Spacer(modifier = Modifier.width(6.dp))
                            Text("Retry", fontWeight = FontWeight.Bold)
                        }
                    }
                }
            }
        }
    }
}
