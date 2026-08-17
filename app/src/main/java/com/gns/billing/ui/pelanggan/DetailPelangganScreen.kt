package com.gns.billing.ui.pelanggan

import android.content.Context
import android.content.Intent
import android.net.Uri
import android.widget.Toast
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavController
import com.gns.billing.ui.components.MikroTikActionDialog
import com.gns.billing.viewmodel.PelangganViewModel

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun DetailPelangganScreen(
    pelangganId: Int,
    navController: NavController,
    viewModel: PelangganViewModel = viewModel()
) {
    val detail by viewModel.detailPelanggan.collectAsState()
    val loading by viewModel.loading.collectAsState()
    val error by viewModel.error.collectAsState()
    var showMikroTikDialog by remember { mutableStateOf(false) }
    val context = LocalContext.current

    LaunchedEffect(pelangganId) {
        viewModel.getDetailPelanggan(pelangganId)
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Detail Pelanggan") },
                navigationIcon = {
                    IconButton(onClick = { navController.popBackStack() }) {
                        Icon(Icons.AutoMirrored.Filled.ArrowBack, contentDescription = "Kembali")
                    }
                }
            )
        }
    ) { padding ->
        if (loading) {
            Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                CircularProgressIndicator()
            }
        } else if (error != null) {
            Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                Text(text = "Error: $error", color = MaterialTheme.colorScheme.error)
            }
        } else {
            detail?.let { p ->
                Column(
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(padding)
                        .padding(16.dp)
                        .verticalScroll(rememberScrollState()),
                    verticalArrangement = Arrangement.spacedBy(16.dp)
                ) {
                    Card(
                        modifier = Modifier.fillMaxWidth(),
                        colors = CardDefaults.cardColors(
                            containerColor = MaterialTheme.colorScheme.primaryContainer
                        )
                    ) {
                        Column(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(16.dp),
                            horizontalAlignment = Alignment.CenterHorizontally
                        ) {
                            val initial = p.nama.take(2).uppercase()
                            Box(
                                modifier = Modifier
                                    .size(70.dp)
                                    .clip(CircleShape)
                                    .background(MaterialTheme.colorScheme.primary),
                                contentAlignment = Alignment.Center
                            ) {
                                Text(
                                    text = initial,
                                    color = Color.White,
                                    fontWeight = FontWeight.Bold,
                                    fontSize = 24.sp
                                )
                            }

                            Spacer(modifier = Modifier.height(12.dp))

                            Text(
                                text = p.nama,
                                style = MaterialTheme.typography.headlineSmall,
                                fontWeight = FontWeight.Bold
                            )
                            Text(
                                text = "Kode: ${p.kode_pelanggan ?: "-"}",
                                style = MaterialTheme.typography.bodyMedium,
                                color = MaterialTheme.colorScheme.onPrimaryContainer.copy(alpha = 0.8f)
                            )

                            Spacer(modifier = Modifier.height(8.dp))

                            Surface(
                                shape = MaterialTheme.shapes.small,
                                color = if (p.status.equals("Aktif", ignoreCase = true)) Color(0xFF4CAF50) else Color(0xFFF44336)
                            ) {
                                Text(
                                    text = p.status,
                                    modifier = Modifier.padding(horizontal = 12.dp, vertical = 4.dp),
                                    color = Color.White,
                                    style = MaterialTheme.typography.labelLarge,
                                    fontWeight = FontWeight.Bold
                                )
                            }
                        }
                    }

                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        Button(
                            onClick = {
                                val url = p.whatsapp_url
                                if (url.isNullOrBlank()) {
                                    Toast.makeText(
                                        context,
                                        "Nomor WhatsApp tidak tersedia",
                                        Toast.LENGTH_SHORT
                                    ).show()
                                } else {
                                    openWhatsApp(context, url)
                                }
                            },
                            modifier = Modifier.weight(1f),
                            colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF25D366))
                        ) {
                            Icon(Icons.Default.Send, contentDescription = "WhatsApp", modifier = Modifier.size(18.dp))
                            Spacer(Modifier.width(8.dp))
                            Text("WhatsApp")
                        }

                        OutlinedButton(
                            onClick = {
                                p.no_hp?.let { noHp ->
                                    openDialer(context, noHp)
                                } ?: Toast.makeText(context, "Nomor HP tidak tersedia", Toast.LENGTH_SHORT).show()
                            },
                            modifier = Modifier.weight(1f)
                        ) {
                            Icon(Icons.Default.Phone, contentDescription = "Telepon", modifier = Modifier.size(18.dp))
                            Spacer(Modifier.width(8.dp))
                            Text("Telepon")
                        }
                    }

                    InfoSection(title = "Informasi Kontak") {
                        InfoRow(Icons.Default.Phone, "No. HP", p.no_hp ?: "-")
                        InfoRow(Icons.Default.LocationOn, "Alamat", p.alamat ?: "-")
                    }

                    InfoSection(title = "Informasi Koneksi") {
                        InfoRow(Icons.Default.Router, "Router", p.router?.nama_router ?: "-")
                        InfoRow(Icons.Default.Settings, "Paket", p.paket?.nama_paket ?: "-")
                        InfoRow(Icons.Default.Person, "Username PPPoE", p.username_pppoe ?: "-")
                        InfoRow(Icons.Default.Numbers, "IP Address", p.ip_address ?: "-")
                        InfoRow(Icons.Default.Dns, "MAC Address", p.mac_address ?: "-")
                    }

                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        Button(
                            onClick = { navController.navigate("tagihan/${p.id}") },
                            modifier = Modifier.weight(1f)
                        ) {
                            Icon(Icons.Default.Receipt, contentDescription = null)
                            Spacer(Modifier.width(8.dp))
                            Text("Tagihan")
                        }
                        OutlinedButton(
                            onClick = { navController.navigate("edit_pelanggan/${p.id}") },
                            modifier = Modifier.weight(1f)
                        ) {
                            Icon(Icons.Default.Edit, contentDescription = null)
                            Spacer(Modifier.width(8.dp))
                            Text("Edit")
                        }
                    }

                    Button(
                        onClick = { showMikroTikDialog = true },
                        modifier = Modifier.fillMaxWidth(),
                        colors = ButtonDefaults.buttonColors(containerColor = MaterialTheme.colorScheme.secondary)
                    ) {
                        Icon(Icons.Default.Router, contentDescription = null)
                        Spacer(Modifier.width(8.dp))
                        Text("Kelola Aksi MikroTik")
                    }
                }
            }
        }
    }

    if (showMikroTikDialog && detail != null) {
        MikroTikActionDialog(
            pelangganId = detail!!.id,
            pelangganNama = detail!!.nama,
            onDismiss = { showMikroTikDialog = false }
        )
    }
}

private fun openWhatsApp(context: Context, url: String) {
    try {
        val intent = Intent(Intent.ACTION_VIEW, Uri.parse(url))
        context.startActivity(intent)
    } catch (e: Exception) {
        Toast.makeText(context, "Gagal membuka WhatsApp", Toast.LENGTH_SHORT).show()
    }
}

private fun openDialer(context: Context, phone: String) {
    try {
        val intent = Intent(Intent.ACTION_DIAL).apply {
            data = Uri.parse("tel:$phone")
        }
        context.startActivity(intent)
    } catch (e: Exception) {
        Toast.makeText(context, "Aplikasi telepon tidak ditemukan", Toast.LENGTH_SHORT).show()
    }
}

@Composable
fun InfoSection(title: String, content: @Composable ColumnScope.() -> Unit) {
    Column {
        Text(
            text = title,
            style = MaterialTheme.typography.titleMedium,
            fontWeight = FontWeight.Bold,
            color = MaterialTheme.colorScheme.primary
        )
        Spacer(modifier = Modifier.height(8.dp))
        Card(
            modifier = Modifier.fillMaxWidth(),
            elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
        ) {
            Column(modifier = Modifier.padding(12.dp), verticalArrangement = Arrangement.spacedBy(8.dp)) {
                content()
            }
        }
    }
}

@Composable
fun InfoRow(icon: ImageVector, label: String, value: String) {
    Row(
        modifier = Modifier.fillMaxWidth(),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Icon(
            imageVector = icon,
            contentDescription = null,
            tint = MaterialTheme.colorScheme.secondary,
            modifier = Modifier.size(20.dp)
        )
        Spacer(modifier = Modifier.width(12.dp))
        Column {
            Text(text = label, style = MaterialTheme.typography.labelSmall)
            Text(text = value, style = MaterialTheme.typography.bodyLarge)
        }
    }
}
