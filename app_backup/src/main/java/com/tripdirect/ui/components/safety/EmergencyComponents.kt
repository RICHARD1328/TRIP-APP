package com.tripdirect.ui.components.safety

import android.content.Context
import android.content.Intent
import android.net.Uri
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.gestures.detectTapGestures
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.tripdirect.data.model.EmergencyContact
import com.tripdirect.data.model.EmergencyService
import com.tripdirect.data.model.EmergencyServiceType
import com.tripdirect.data.model.SafetyStatus
import com.tripdirect.data.model.SafetyTip
import com.tripdirect.data.model.UserLocation
import kotlinx.coroutines.delay

@Composable
fun SafetyHeader(status: SafetyStatus, location: UserLocation?) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 16.dp, vertical = 8.dp),
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.primaryContainer)
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Column(modifier = Modifier.weight(1f)) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Icon(Icons.Default.Shield, contentDescription = null, tint = MaterialTheme.colorScheme.primary)
                    Spacer(modifier = Modifier.width(8.dp))
                    Text(
                        text = "Travel Safety Center",
                        style = MaterialTheme.typography.titleLarge,
                        fontWeight = FontWeight.Bold,
                        color = MaterialTheme.colorScheme.onPrimaryContainer
                    )
                }
                Spacer(modifier = Modifier.height(4.dp))
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Icon(Icons.Default.Place, contentDescription = null, modifier = Modifier.size(16.dp), tint = MaterialTheme.colorScheme.onPrimaryContainer.copy(alpha = 0.8f))
                    Spacer(modifier = Modifier.width(4.dp))
                    Text(
                        text = location?.locationName ?: "Ooty, Tamil Nadu",
                        style = MaterialTheme.typography.bodyMedium,
                        color = MaterialTheme.colorScheme.onPrimaryContainer.copy(alpha = 0.8f)
                    )
                }
            }

            Surface(
                shape = RoundedCornerShape(16.dp),
                color = Color(0xFFE8F5E9),
                contentColor = Color(0xFF2E7D32)
            ) {
                Row(modifier = Modifier.padding(horizontal = 10.dp, vertical = 6.dp), verticalAlignment = Alignment.CenterVertically) {
                    Icon(Icons.Default.CheckCircle, contentDescription = null, modifier = Modifier.size(16.dp))
                    Spacer(modifier = Modifier.width(4.dp))
                    Text(status.statusText, style = MaterialTheme.typography.labelMedium, fontWeight = FontWeight.Bold)
                }
            }
        }
    }
}

@Composable
fun SOSButton(onTriggerSos: () -> Unit) {
    var isHolding by remember { mutableStateOf(false) }
    var holdProgress by remember { mutableFloatStateOf(0f) }

    LaunchedEffect(isHolding) {
        if (isHolding) {
            holdProgress = 0f
            while (holdProgress < 1f && isHolding) {
                delay(50)
                holdProgress += 0.05f
            }
            if (holdProgress >= 1f) {
                onTriggerSos()
                isHolding = false
                holdProgress = 0f
            }
        } else {
            holdProgress = 0f
        }
    }

    Card(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 16.dp, vertical = 8.dp),
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.errorContainer.copy(alpha = 0.3f))
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(20.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Text("GET EMERGENCY HELP", style = MaterialTheme.typography.labelLarge, fontWeight = FontWeight.Bold, color = MaterialTheme.colorScheme.error)
            Spacer(modifier = Modifier.height(12.dp))

            Box(
                modifier = Modifier
                    .size(110.dp)
                    .clip(CircleShape)
                    .background(if (isHolding) Color(0xFFB71C1C) else MaterialTheme.colorScheme.error)
                    .pointerInput(Unit) {
                        detectTapGestures(
                            onPress = {
                                isHolding = true
                                tryAwaitRelease()
                                isHolding = false
                            }
                        )
                    },
                contentAlignment = Alignment.Center
            ) {
                Column(horizontalAlignment = Alignment.CenterHorizontally) {
                    Icon(Icons.Default.Warning, contentDescription = "SOS", modifier = Modifier.size(36.dp), tint = Color.White)
                    Text("HOLD FOR SOS", style = MaterialTheme.typography.labelSmall, fontWeight = FontWeight.Bold, color = Color.White)
                }
            }

            if (isHolding) {
                Spacer(modifier = Modifier.height(8.dp))
                LinearProgressIndicator(
                    progress = { holdProgress },
                    modifier = Modifier.width(160.dp).height(6.dp).clip(RoundedCornerShape(3.dp)),
                    color = MaterialTheme.colorScheme.error
                )
                Spacer(modifier = Modifier.height(4.dp))
                Text("Keep holding for SOS...", style = MaterialTheme.typography.labelSmall, color = MaterialTheme.colorScheme.error)
            } else {
                Spacer(modifier = Modifier.height(8.dp))
                Text("Press and hold 2 seconds to activate SOS", style = MaterialTheme.typography.labelSmall, color = MaterialTheme.colorScheme.onSurfaceVariant)
            }
        }
    }
}

private data class QuickActionItem(
    val number: String,
    val name: String,
    val icon: androidx.compose.ui.graphics.vector.ImageVector,
    val color: Color
)

@Composable
fun QuickEmergencyActions(context: Context) {
    val actions = listOf(
        QuickActionItem("108", "Ambulance", Icons.Default.MedicalServices, Color(0xFFC62828)),
        QuickActionItem("100", "Police", Icons.Default.LocalPolice, Color(0xFF1565C0)),
        QuickActionItem("101", "Fire", Icons.Default.LocalFireDepartment, Color(0xFFD84315)),
        QuickActionItem("1800-425-4646", "Tourist Help", Icons.Default.SupportAgent, Color(0xFF2E7D32))
    )

    Column(modifier = Modifier.fillMaxWidth().padding(horizontal = 16.dp, vertical = 8.dp)) {
        Text("Quick Emergency Services", style = MaterialTheme.typography.titleMedium, fontWeight = FontWeight.Bold)
        Spacer(modifier = Modifier.height(8.dp))
        Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(8.dp)) {
            actions.forEach { item ->
                Card(
                    modifier = Modifier
                        .weight(1f)
                        .clickable {
                            val intent = Intent(Intent.ACTION_DIAL, Uri.parse("tel:${item.number}"))
                            context.startActivity(intent)
                        },
                    colors = CardDefaults.cardColors(containerColor = item.color.copy(alpha = 0.1f))
                ) {
                    Column(
                        modifier = Modifier.padding(12.dp),
                        horizontalAlignment = Alignment.CenterHorizontally
                    ) {
                        Icon(item.icon, contentDescription = null, tint = item.color, modifier = Modifier.size(24.dp))
                        Spacer(modifier = Modifier.height(6.dp))
                        Text(item.name, style = MaterialTheme.typography.labelMedium, fontWeight = FontWeight.Bold, color = item.color)
                        Text(item.number, style = MaterialTheme.typography.labelSmall, color = MaterialTheme.colorScheme.onSurfaceVariant)
                    }
                }
            }
        }
    }
}


@Composable
fun NearbyServiceCard(
    service: EmergencyService,
    onCallClick: () -> Unit,
    onDirectionsClick: () -> Unit
) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 16.dp, vertical = 4.dp),
        elevation = CardDefaults.cardElevation(defaultElevation = 1.dp)
    ) {
        Column(modifier = Modifier.padding(14.dp)) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Row(verticalAlignment = Alignment.CenterVertically, modifier = Modifier.weight(1f)) {
                    val icon = when (service.type) {
                        EmergencyServiceType.AMBULANCE -> Icons.Default.MedicalServices
                        EmergencyServiceType.POLICE -> Icons.Default.LocalPolice
                        EmergencyServiceType.FIRE -> Icons.Default.LocalFireDepartment
                        EmergencyServiceType.HOSPITAL -> Icons.Default.LocalHospital
                        EmergencyServiceType.PHARMACY -> Icons.Default.Medication
                        EmergencyServiceType.TOURIST_HELP -> Icons.Default.SupportAgent
                    }
                    Icon(icon, contentDescription = null, modifier = Modifier.size(20.dp), tint = MaterialTheme.colorScheme.primary)
                    Spacer(modifier = Modifier.width(8.dp))
                    Text(service.name, style = MaterialTheme.typography.titleMedium, fontWeight = FontWeight.Bold)
                }
                AssistChip(
                    onClick = {},
                    label = { Text("${String.format("%.1f", service.distanceKm)} km") }
                )
            }

            Text(service.address, style = MaterialTheme.typography.bodySmall, color = MaterialTheme.colorScheme.onSurfaceVariant, modifier = Modifier.padding(top = 4.dp))

            Spacer(modifier = Modifier.height(10.dp))

            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                OutlinedButton(
                    onClick = onDirectionsClick,
                    modifier = Modifier.weight(1f)
                ) {
                    Icon(Icons.Default.Navigation, contentDescription = null, modifier = Modifier.size(16.dp))
                    Spacer(modifier = Modifier.width(4.dp))
                    Text("Directions")
                }
                Button(
                    onClick = onCallClick,
                    modifier = Modifier.weight(1f)
                ) {
                    Icon(Icons.Default.Call, contentDescription = null, modifier = Modifier.size(16.dp))
                    Spacer(modifier = Modifier.width(4.dp))
                    Text("Call")
                }
            }
        }
    }
}

@Composable
fun LocationShareCard(onShareLocation: () -> Unit) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 16.dp, vertical = 8.dp),
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.tertiaryContainer)
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Icon(Icons.Default.Share, contentDescription = null, modifier = Modifier.size(32.dp), tint = MaterialTheme.colorScheme.onTertiaryContainer)
            Spacer(modifier = Modifier.width(16.dp))
            Column(modifier = Modifier.weight(1f)) {
                Text("Share My Location", style = MaterialTheme.typography.titleMedium, fontWeight = FontWeight.Bold, color = MaterialTheme.colorScheme.onTertiaryContainer)
                Text("Send your current location & GPS coords to a trusted person.", style = MaterialTheme.typography.bodySmall, color = MaterialTheme.colorScheme.onTertiaryContainer)
                Spacer(modifier = Modifier.height(8.dp))
                Button(onClick = onShareLocation) {
                    Icon(Icons.Default.Send, contentDescription = null, modifier = Modifier.size(16.dp))
                    Spacer(modifier = Modifier.width(4.dp))
                    Text("Share Location")
                }
            }
        }
    }
}

@Composable
fun SafetyTipCard(tip: SafetyTip) {
    var expanded by remember { mutableStateOf(false) }

    Card(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 16.dp, vertical = 4.dp)
            .clickable { expanded = !expanded }
    ) {
        Column(modifier = Modifier.padding(14.dp)) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(tip.title, style = MaterialTheme.typography.titleMedium, fontWeight = FontWeight.Bold, modifier = Modifier.weight(1f))
                AssistChip(onClick = {}, label = { Text(tip.category) })
            }
            AnimatedVisibility(visible = expanded) {
                Column(modifier = Modifier.padding(top = 8.dp)) {
                    HorizontalDivider()
                    Spacer(modifier = Modifier.height(6.dp))
                    Text(tip.description, style = MaterialTheme.typography.bodyMedium)
                }
            }
        }
    }
}

@Composable
fun EmergencyConfirmationDialog(
    onDismiss: () -> Unit,
    onConfirm: () -> Unit
) {
    AlertDialog(
        onDismissRequest = onDismiss,
        title = {
            Row(verticalAlignment = Alignment.CenterVertically) {
                Icon(Icons.Default.Warning, contentDescription = null, tint = MaterialTheme.colorScheme.error)
                Spacer(modifier = Modifier.width(8.dp))
                Text("🚨 Emergency SOS")
            }
        },
        text = {
            Text("Are you sure you need emergency assistance? Your active location and contacts will be opened for rapid communication.")
        },
        confirmButton = {
            Button(
                onClick = {
                    onConfirm()
                    onDismiss()
                },
                colors = ButtonDefaults.buttonColors(containerColor = MaterialTheme.colorScheme.error)
            ) {
                Text("Confirm SOS")
            }
        },
        dismissButton = {
            TextButton(onClick = onDismiss) {
                Text("Cancel")
            }
        }
    )
}

@Composable
fun AddEmergencyContactDialog(
    onDismiss: () -> Unit,
    onSave: (name: String, phone: String, relationship: String) -> Unit
) {
    var name by remember { mutableStateOf("") }
    var phone by remember { mutableStateOf("") }
    var relationship by remember { mutableStateOf("Family") }

    AlertDialog(
        onDismissRequest = onDismiss,
        title = { Text("Add Emergency Contact") },
        text = {
            Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                OutlinedTextField(
                    value = name,
                    onValueChange = { name = it },
                    label = { Text("Contact Name") },
                    singleLine = true,
                    modifier = Modifier.fillMaxWidth()
                )
                OutlinedTextField(
                    value = phone,
                    onValueChange = { phone = it },
                    label = { Text("Phone Number") },
                    singleLine = true,
                    modifier = Modifier.fillMaxWidth()
                )
                OutlinedTextField(
                    value = relationship,
                    onValueChange = { relationship = it },
                    label = { Text("Relationship (e.g. Family, Friend)") },
                    singleLine = true,
                    modifier = Modifier.fillMaxWidth()
                )
            }
        },
        confirmButton = {
            Button(
                onClick = {
                    if (name.isNotBlank() && phone.isNotBlank()) {
                        onSave(name, phone, relationship)
                        onDismiss()
                    }
                }
            ) {
                Text("Save Contact")
            }
        },
        dismissButton = {
            TextButton(onClick = onDismiss) {
                Text("Cancel")
            }
        }
    )
}
