package com.gns.billing.tagihan

import android.content.Intent
import android.net.Uri
import android.widget.Toast
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.Send
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavController
import com.gns.billing.utils.formatRupiah

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun DetailTagihanScreen(
    navController: NavController,
    tagihanId: Int,
    viewModel: TagihanViewModel = viewModel()
) {
    val detailResponse by viewModel.detailTagihan.collectAsState()
    val isLoading by viewModel.isLoading.collectAsState()
    val whatsappUrl by viewModel.whatsappUrl.collectAsState()
    val whatsappError by viewModel.whatsappError.collectAsState()
    val whatsappLoading by viewModel.whatsappLoading.collectAsState()
    val context = LocalContext.current

    LaunchedEffect(tagihanId) {
        viewModel.loadDetailTagihan(tagihanId)
    }

    LaunchedEffect(whatsappUrl) {
        val url = whatsappUrl
        if (!url.isNullOrBlank()) {
            try {
                context.startActivity(Intent(Intent.ACTION_VIEW, Uri.parse(url)))
            } catch (e: Exception) {
                Toast.makeText(context, "Gagal membuka WhatsApp", Toast.LENGTH_SHORT).show()
            } finally {
                viewModel.clearWhatsappUrl()
            }
        }
    }

    LaunchedEffect(whatsappError) {
        val error = whatsappError
        if (!error.isNullOrBlank()) {
            Toast.makeText(context, error, Toast.LENGTH_SHORT).show()
            viewModel.clearWhatsappError()
        }
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Detail Tagihan", fontWeight = FontWeight.Bold) },
                navigationIcon = {
                    IconButton(onClick = { navController.popBackStack() }) {
                        Icon(Icons.AutoMirrored.Filled.ArrowBack, contentDescription = "Kembali")
                    }
                }
            )
        }
    ) { paddingValues ->
        if (isLoading) {
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(paddingValues),
                contentAlignment = Alignment.Center
            ) {
                CircularProgressIndicator()
            }
        } else {
            val tagihan = detailResponse?.data
            if (tagihan != null) {
                Box(
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(paddingValues)
                ) {
                    Column(
                        modifier = Modifier
                            .fillMaxSize()
                            .padding(bottom = 90.dp)
                            .padding(16.dp)
                            .verticalScroll(rememberScrollState()),
                        verticalArrangement = Arrangement.spacedBy(16.dp)
                    ) {
                        Card(
                            modifier = Modifier.fillMaxWidth(),
                            elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
                        ) {
                            Row(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .padding(16.dp),
                                horizontalArrangement = Arrangement.SpaceBetween,
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Column {
                                    Text(
                                        text = "Nomor Invoice",
                                        style = MaterialTheme.typography.bodySmall,
                                        color = Color.Gray
                                    )
                                    Text(
                                        text = tagihan.invoice_no,
                                        style = MaterialTheme.typography.titleLarge,
                                        fontWeight = FontWeight.Bold
                                    )
                                }
                                val statusColor = if (tagihan.status.equals("Lunas", true)) Color(0xFF2E7D32) else Color(0xFFC62828)
                                Text(
                                    text = tagihan.status,
                                    color = Color.White,
                                    modifier = Modifier
                                        .background(statusColor, RoundedCornerShape(16.dp))
                                        .padding(horizontal = 12.dp, vertical = 6.dp),
                                    style = MaterialTheme.typography.labelMedium
                                )
                            }
                        }

                        Card(
                            modifier = Modifier.fillMaxWidth(),
                            elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
                        ) {
                            Column(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .padding(16.dp),
                                verticalArrangement = Arrangement.spacedBy(8.dp)
                            ) {
                                Text(
                                    text = "Informasi Pelanggan",
                                    fontWeight = FontWeight.Bold,
                                    color = MaterialTheme.colorScheme.primary
                                )
                                HorizontalDivider(color = Color.LightGray, thickness = 0.5.dp)
                                DetailInfoRow(label = "Nama Pelanggan", value = tagihan.pelanggan_nama ?: "-")
                                DetailInfoRow(label = "Periode", value = tagihan.periode)
                                DetailInfoRow(label = "Tanggal Tagihan", value = tagihan.tanggal_tagihan ?: "-")
                            }
                        }

                        Card(
                            modifier = Modifier.fillMaxWidth(),
                            elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
                        ) {
                            Column(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .padding(16.dp),
                                verticalArrangement = Arrangement.spacedBy(8.dp)
                            ) {
                                Text(
                                    text = "Rincian Biaya",
                                    fontWeight = FontWeight.Bold,
                                    color = MaterialTheme.colorScheme.primary
                                )
                                HorizontalDivider(color = Color.LightGray, thickness = 0.5.dp)
                                DetailInfoRow(label = "Total Tagihan", value = formatRupiah(tagihan.total))
                                DetailInfoRow(label = "Dibayar", value = formatRupiah(tagihan.dibayar))
                                DetailInfoRow(label = "Sisa Tagihan", value = formatRupiah(tagihan.sisa))
                            }
                        }

                        Button(
                            onClick = { viewModel.loadTagihanWhatsapp(tagihan.id) },
                            enabled = !whatsappLoading,
                            modifier = Modifier.fillMaxWidth(),
                            colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF25D366)),
                            shape = RoundedCornerShape(12.dp)
                        ) {
                            Icon(
                                imageVector = Icons.Default.Send,
                                contentDescription = "WhatsApp"
                            )
                            Spacer(Modifier.width(8.dp))
                            Text("WhatsApp")
                        }
                    }

                    // Tombol Proses Pembayaran mengikuti aturan server:
                    // hanya ditampilkan jika tagihan belum lunas.
                    val status = tagihan.status
                    if (!status.equals("Lunas", ignoreCase = true)) {
                        Surface(
                            modifier = Modifier
                                .align(Alignment.BottomCenter)
                                .fillMaxWidth()
                                .padding(horizontal = 16.dp, vertical = 12.dp),
                            color = Color.Transparent
                        ) {
                            Button(
                                onClick = {
                                    val nama = tagihan.pelanggan_nama ?: "Pelanggan Umum"
                                    val inv = tagihan.invoice_no
                                    val total = if (tagihan.sisa > 0.0) tagihan.sisa else tagihan.total

                                    navController.navigate("pembayaran_screen/$tagihanId/$nama/$inv/$total")
                                },
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .height(50.dp),
                                shape = RoundedCornerShape(12.dp)
                            ) {
                                Text("Proses Pembayaran", style = MaterialTheme.typography.titleMedium)
                            }
                        }
                    }
                }
            } else {
                Box(
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(paddingValues),
                    contentAlignment = Alignment.Center
                ) {
                    Text("Data detail tagihan tidak ditemukan", color = Color.Gray)
                }
            }
        }
    }
}

@Composable
fun DetailInfoRow(label: String, value: String) {
    Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.SpaceBetween
    ) {
        Text(text = label, style = MaterialTheme.typography.bodyMedium, color = Color.Gray)
        Text(text = value, style = MaterialTheme.typography.bodyMedium, fontWeight = FontWeight.Medium)
    }
}