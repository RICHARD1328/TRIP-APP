package com.tripdirect.ui.screens.safety

import android.content.Intent
import android.net.Uri
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Call
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.LocalPolice
import androidx.compose.material.icons.filled.MedicalServices
import androidx.compose.material.icons.filled.Place
import androidx.compose.material.icons.filled.Send
import androidx.compose.material.icons.filled.Warning
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.tripdirect.viewmodel.EmergencyViewModel

@Composable
fun SOSActiveScreen(
    viewModel: EmergencyViewModel,
    onNavigateBack: () -> Unit
) {
    val context = LocalContext.current
    val location by viewModel.location.collectAsState()

    Surface(
        modifier = Modifier.fillMaxSize(),
        color = MaterialTheme.colorScheme.errorContainer
    ) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(24.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.SpaceBetween
        ) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                IconButton(onClick = {
                    viewModel.cancelSos()
                    onNavigateBack()
                }) {
                    Icon(Icons.Default.Close, contentDescription = "Close SOS", tint = MaterialTheme.colorScheme.onErrorContainer)
                }
                Text("EMERGENCY MODE", style = MaterialTheme.typography.titleMedium, fontWeight = FontWeight.Bold, color = MaterialTheme.colorScheme.onErrorContainer)
                Spacer(modifier = Modifier.width(48.dp))
            }

            Column(horizontalAlignment = Alignment.CenterHorizontally) {
                Box(
                    modifier = Modifier
                        .size(100.dp)
                        .clip(CircleShape)
                        .background(MaterialTheme.colorScheme.error),
                    contentAlignment = Alignment.Center
                ) {
                    Icon(Icons.Default.Warning, contentDescription = null, modifier = Modifier.size(56.dp), tint = Color.White)
                }
                Spacer(modifier = Modifier.height(16.dp))
                Text("SOS ACTIVATED", style = MaterialTheme.typography.headlineLarge, fontWeight = FontWeight.Bold, color = MaterialTheme.colorScheme.onErrorContainer)
                Text("Rapid access to emergency services", style = MaterialTheme.typography.bodyMedium, color = MaterialTheme.colorScheme.onErrorContainer)

                Spacer(modifier = Modifier.height(16.dp))

                Card(
                    modifier = Modifier.fillMaxWidth(),
                    colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface)
                ) {
                    Column(modifier = Modifier.padding(16.dp), horizontalAlignment = Alignment.CenterHorizontally) {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Icon(Icons.Default.Place, contentDescription = null, tint = MaterialTheme.colorScheme.error)
                            Spacer(modifier = Modifier.width(8.dp))
                            Text(location?.locationName ?: "Ooty, Tamil Nadu", style = MaterialTheme.typography.titleMedium, fontWeight = FontWeight.Bold)
                        }
                        Text(location?.address ?: "City Center, Ooty", style = MaterialTheme.typography.bodySmall, color = MaterialTheme.colorScheme.onSurfaceVariant)
                    }
                }
            }

            Column(modifier = Modifier.fillMaxWidth(), verticalArrangement = Arrangement.spacedBy(12.dp)) {
                Button(
                    onClick = {
                        val intent = Intent(Intent.ACTION_DIAL, Uri.parse("tel:108"))
                        context.startActivity(intent)
                    },
                    modifier = Modifier.fillMaxWidth().height(52.dp),
                    colors = ButtonDefaults.buttonColors(containerColor = MaterialTheme.colorScheme.error)
                ) {
                    Icon(Icons.Default.MedicalServices, contentDescription = null)
                    Spacer(modifier = Modifier.width(8.dp))
                    Text("Call Ambulance (108)", fontWeight = FontWeight.Bold)
                }

                Button(
                    onClick = {
                        val intent = Intent(Intent.ACTION_DIAL, Uri.parse("tel:100"))
                        context.startActivity(intent)
                    },
                    modifier = Modifier.fillMaxWidth().height(52.dp),
                    colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF1565C0))
                ) {
                    Icon(Icons.Default.LocalPolice, contentDescription = null)
                    Spacer(modifier = Modifier.width(8.dp))
                    Text("Contact Police (100)", fontWeight = FontWeight.Bold)
                }

                OutlinedButton(
                    onClick = {
                        val shareIntent = Intent(Intent.ACTION_SEND).apply {
                            type = "text/plain"
                            putExtra(Intent.EXTRA_TEXT, viewModel.getShareableLocationText())
                        }
                        context.startActivity(Intent.createChooser(shareIntent, "Emergency Share Location"))
                    },
                    modifier = Modifier.fillMaxWidth().height(52.dp)
                ) {
                    Icon(Icons.Default.Send, contentDescription = null)
                    Spacer(modifier = Modifier.width(8.dp))
                    Text("Share Emergency Location")
                }

                TextButton(
                    onClick = {
                        viewModel.cancelSos()
                        onNavigateBack()
                    },
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Text("Cancel SOS Mode", color = MaterialTheme.colorScheme.onErrorContainer, fontWeight = FontWeight.Bold)
                }
            }
        }
    }
}
