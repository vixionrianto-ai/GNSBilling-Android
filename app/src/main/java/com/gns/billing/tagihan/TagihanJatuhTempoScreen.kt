package com.gns.billing.tagihan

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavController
import java.text.NumberFormat
import java.util.Locale

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun TagihanJatuhTempoScreen(
    navController: NavController? = null,
    onBackClick: () -> Unit = { navController?.popBackStack() },
    viewModel: TagihanViewModel = viewModel()
) {
    val isLoading by viewModel.isLoading.collectAsState()
    val listJatuhTempo by viewModel.listJatuhTempo.collectAsState()

    LaunchedEffect(Unit) {
        viewModel.fetchTagihanJatuhTempo()
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Tagihan Jatuh Tempo") },
                navigationIcon = {
                    IconButton(onClick = onBackClick) {
                        Icon(
                            imageVector = Icons.Default.ArrowBack,
                            contentDescription = "Kembali"
                        )
                    }
                }
            )
        }
    ) { paddingValues ->
        Box(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues)
        ) {
            when {
                isLoading -> {
                    CircularProgressIndicator(modifier = Modifier.align(Alignment.Center))
                }

                listJatuhTempo.isEmpty() -> {
                    Text(
                        text = "Tidak ada tagihan jatuh tempo",
                        modifier = Modifier.align(Alignment.Center),
                        style = MaterialTheme.typography.bodyLarge
                    )
                }

                else -> {
                    LazyColumn(
                        modifier = Modifier
                            .fillMaxSize()
                            .padding(horizontal = 16.dp),
                        verticalArrangement = Arrangement.spacedBy(12.dp),
                        contentPadding = PaddingValues(vertical = 12.dp)
                    ) {
                        items(listJatuhTempo, key = { it.id }) { tagihan ->
                            TagihanJatuhTempoItem(
                                tagihan = tagihan,
                                onBayarClick = {
                                    val pelangganNama = tagihan.pelanggan_nama ?: "Pelanggan Umum"
                                    val totalServer = if (tagihan.sisa > 0.0) tagihan.sisa else tagihan.total
                                    navController?.navigate(
                                        "pembayaran_screen/${tagihan.id}/$pelangganNama/${tagihan.invoice_no}/$totalServer"
                                    )
                                }
                            )
                        }
                    }
                }
            }
        }
    }
}

@Composable
fun TagihanJatuhTempoItem(
    tagihan: Tagihan,
    onBayarClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    Card(
        modifier = modifier.fillMaxWidth(),
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp)
        ) {
            Text(
                text = tagihan.pelanggan_nama ?: "Pelanggan Umum",
                style = MaterialTheme.typography.titleMedium,
                fontWeight = FontWeight.Bold
            )

            Spacer(modifier = Modifier.height(4.dp))

            Text(
                text = "Invoice: ${tagihan.invoice_no}",
                style = MaterialTheme.typography.bodyMedium
            )

            Text(
                text = "Jatuh tempo: ${tagihan.tanggal_jatuh_tempo ?: "-"}",
                style = MaterialTheme.typography.bodyMedium
            )

            Spacer(modifier = Modifier.height(8.dp))

            Text(
                text = "Status: ${tagihan.status}",
                style = MaterialTheme.typography.bodySmall,
                color = MaterialTheme.colorScheme.error
            )

            Spacer(modifier = Modifier.height(8.dp))

            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = "Sisa: ${formatRupiahServer(tagihan.sisa)}",
                    style = MaterialTheme.typography.titleSmall,
                    fontWeight = FontWeight.SemiBold,
                    color = MaterialTheme.colorScheme.primary
                )

                Button(
                    onClick = onBayarClick,
                    contentPadding = PaddingValues(horizontal = 16.dp, vertical = 8.dp)
                ) {
                    Text("Bayar")
                }
            }
        }
    }
}

private fun formatRupiahServer(number: Double): String {
    return NumberFormat.getCurrencyInstance(Locale("in", "ID")).format(number)
}
