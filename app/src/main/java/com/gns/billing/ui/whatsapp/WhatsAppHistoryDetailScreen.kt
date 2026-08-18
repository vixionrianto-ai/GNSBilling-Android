package com.gns.billing.ui.whatsapp

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavHostController
import com.gns.billing.model.WhatsAppLogItem
import com.gns.billing.repository.WhatsAppRepository
import kotlinx.coroutines.launch
import androidx.compose.runtime.rememberCoroutineScope

@Composable
fun WhatsAppHistoryDetailScreen(
    navController: NavHostController,
    logId: Int
) {
    val repository = remember { WhatsAppRepository() }
    val scope = rememberCoroutineScope()
    val loading = remember { mutableStateOf(true) }
    val error = remember { mutableStateOf<String?>(null) }
    val data = remember { mutableStateOf<WhatsAppLogItem?>(null) }

    LaunchedEffect(logId) {
        scope.launch {
            try {
                val response = repository.getDetail(logId)
                if (response.success) {
                    data.value = response.data
                    error.value = null
                } else {
                    error.value = response.message ?: "Gagal mengambil detail riwayat."
                }
            } catch (e: Exception) {
                error.value = e.message ?: "Gagal mengambil detail riwayat."
            } finally {
                loading.value = false
            }
        }
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Detail Riwayat WhatsApp") },
                navigationIcon = {
                    IconButton(onClick = { navController.popBackStack() }) {
                        Icon(Icons.AutoMirrored.Filled.ArrowBack, contentDescription = "Kembali")
                    }
                }
            )
        }
    ) { padding ->
        when {
            loading.value -> CircularProgressIndicator(modifier = Modifier.padding(padding))
            error.value != null -> Text(error.value.orEmpty(), color = MaterialTheme.colorScheme.error, modifier = Modifier.padding(24.dp))
            data.value != null -> DetailContent(data.value!!, Modifier.padding(padding))
            else -> Text("Data tidak ditemukan.", modifier = Modifier.padding(24.dp))
        }
    }
}

@Composable
private fun DetailContent(log: WhatsAppLogItem, modifier: Modifier = Modifier) {
    Column(
        modifier = modifier
            .fillMaxSize()
            .padding(16.dp)
            .verticalScroll(rememberScrollState()),
        verticalArrangement = Arrangement.spacedBy(10.dp)
    ) {
        DetailRow("Pelanggan", log.pelanggan?.nama ?: "-")
        DetailRow("Kode Pelanggan", log.pelanggan?.kodePelanggan ?: "-")
        DetailRow("Nomor", log.nomor ?: "-")
        DetailRow("Jenis", log.jenis ?: "-")
        DetailRow("Provider", log.provider ?: "-")
        DetailRow("Status", log.status ?: "-")
        DetailRow("Waktu", log.sentAt ?: "-")
        DetailRow("Invoice", log.tagihan?.invoiceNo ?: "-")
        DetailRow("Periode", log.tagihan?.periode ?: "-")
        Text("Pesan", fontWeight = FontWeight.Bold)
        Text(log.pesan.orEmpty())
        Text("Response", fontWeight = FontWeight.Bold)
        Text(log.response.orEmpty())
    }
}

@Composable
private fun DetailRow(label: String, value: String) {
    Column {
        Text(label, style = MaterialTheme.typography.labelMedium, color = MaterialTheme.colorScheme.primary)
        Text(value, style = MaterialTheme.typography.bodyLarge)
    }
}