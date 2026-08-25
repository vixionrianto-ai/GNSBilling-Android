package com.gns.billing.ui.components

import androidx.compose.foundation.layout.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Lock
import androidx.compose.material.icons.filled.LockOpen
import androidx.compose.material.icons.filled.Refresh
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.gns.billing.viewmodel.MikroTikActionViewModel

@Composable
fun MikroTikActionDialog(
    routerId: Int,
    secret: String,
    pelangganNama: String,
    onDismiss: () -> Unit,
    viewModel: MikroTikActionViewModel = viewModel()
) {
    val uiState by viewModel.uiState.collectAsState()

    AlertDialog(
        onDismissRequest = { if (!uiState.isLoading) onDismiss() },
        title = {
            Text(
                text = "Aksi MikroTik: $pelangganNama",
                fontWeight = FontWeight.Bold,
                style = MaterialTheme.typography.titleMedium
            )
        },
        text = {
            Column(
                modifier = Modifier.fillMaxWidth(),
                verticalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                when {
                    uiState.isLoading -> {
                        Row(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(16.dp),
                            horizontalArrangement = Arrangement.Center,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            CircularProgressIndicator(modifier = Modifier.size(24.dp))
                            Spacer(modifier = Modifier.width(12.dp))
                            Text("Memproses perintah ke router...")
                        }
                    }
                    uiState.successMessage != null -> {
                        Text(
                            text = uiState.successMessage!!,
                            color = MaterialTheme.colorScheme.primary,
                            fontWeight = FontWeight.Medium
                        )
                    }
                    uiState.errorMessage != null -> {
                        Text(
                            text = uiState.errorMessage!!,
                            color = MaterialTheme.colorScheme.error,
                            fontWeight = FontWeight.Medium
                        )
                    }
                    else -> {
                        Text("Pilih tindakan router untuk pelanggan ini:")

                        Button(
                            onClick = { viewModel.enableSecret(routerId, secret) },
                            modifier = Modifier.fillMaxWidth(),
                            colors = ButtonDefaults.buttonColors(
                                containerColor = MaterialTheme.colorScheme.primary
                            )
                        ) {
                            Icon(Icons.Default.LockOpen, contentDescription = null)
                            Spacer(modifier = Modifier.width(8.dp))
                            Text("Buka Isolir (Enable Secret)")
                        }

                        OutlinedButton(
                            onClick = { viewModel.disableSecret(routerId, secret) },
                            modifier = Modifier.fillMaxWidth(),
                            colors = ButtonDefaults.outlinedButtonColors(
                                contentColor = MaterialTheme.colorScheme.error
                            )
                        ) {
                            Icon(Icons.Default.Lock, contentDescription = null)
                            Spacer(modifier = Modifier.width(8.dp))
                            Text("Isolir Pelanggan (Disable)")
                        }

                        OutlinedButton(
                            onClick = { viewModel.disconnectSecret(routerId, secret) },
                            modifier = Modifier.fillMaxWidth()
                        ) {
                            Icon(Icons.Default.Refresh, contentDescription = null)
                            Spacer(modifier = Modifier.width(8.dp))
                            Text("Putus Sesi Aktif (Disconnect)")
                        }
                    }
                }
            }
        },
        confirmButton = {
            if (!uiState.isLoading) {
                TextButton(
                    onClick = {
                        viewModel.resetState()
                        onDismiss()
                    }
                ) {
                    Text(if (uiState.successMessage != null) "Tutup" else "Batal")
                }
            }
        }
    )
}
