package com.gns.billing.ui.pelanggan

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavController
import com.gns.billing.viewmodel.PaketViewModel
import com.gns.billing.viewmodel.PelangganViewModel
import com.gns.billing.viewmodel.RouterViewModel

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun EditPelangganScreen(
    navController: NavController,
    pelangganId: Int
) {
    val pelangganVM: PelangganViewModel = viewModel()
    val routerVM: RouterViewModel = viewModel()
    val paketVM: PaketViewModel = viewModel()

    val detail by pelangganVM.detailPelanggan.collectAsState()
    val routers by routerVM.router.collectAsState()
    val pakets by paketVM.paket.collectAsState()
    val loading by pelangganVM.loading.collectAsState()
    val success by pelangganVM.success.collectAsState()
    val error by pelangganVM.error.collectAsState()

    val snackbarHostState = remember { SnackbarHostState() }
    var showDismissDialog by remember { mutableStateOf(false) }

    LaunchedEffect(Unit) {
        routerVM.loadRouter()
        paketVM.loadPaket()
        pelangganVM.getDetailPelanggan(pelangganId)
    }

    LaunchedEffect(success) {
        if (success) {
            pelangganVM.clearState()
            navController.popBackStack()
        }
    }

    LaunchedEffect(error) {
        error?.let {
            snackbarHostState.showSnackbar(it)
            pelangganVM.clearState()
        }
    }

    Scaffold(
        snackbarHost = { SnackbarHost(snackbarHostState) },
        topBar = {
            TopAppBar(
                title = { Text("Edit Pelanggan") },
                navigationIcon = {
                    IconButton(onClick = { showDismissDialog = true }) {
                        Icon(Icons.AutoMirrored.Filled.ArrowBack, contentDescription = "Kembali")
                    }
                }
            )
        }
    ) { paddingValues ->
        if (detail == null) {
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(paddingValues),
                contentAlignment = Alignment.Center
            ) {
                CircularProgressIndicator()
            }
        } else {
            val router = routers.firstOrNull { it.id == detail!!.router_id }
                ?: detail!!.router
                ?: routers.firstOrNull()

            val paket = pakets.firstOrNull { it.id == detail!!.paket_id }
                ?: detail!!.paket
                ?: pakets.firstOrNull()

            Box(modifier = Modifier.padding(paddingValues)) {
                PelangganForm(
                    routers = routers,
                    pakets = pakets,
                    loading = loading,
                    initialNama = detail!!.nama,
                    initialNoHp = detail!!.no_hp ?: "",
                    initialAlamat = detail!!.alamat ?: "",
                    initialRouter = router,
                    initialPaket = paket,
                    initialUsername = detail!!.username_pppoe ?: "",
                    initialPassword = detail!!.password_pppoe ?: "",
                    initialIp = detail!!.ip_address ?: "",
                    initialMac = detail!!.mac_address ?: "",
                    initialStatus = detail!!.status,
                    initialKode = detail!!.kode_pelanggan,
                    // --- PERBAIKAN: KONEKSI KAN SELURUH DATA BAGIAN BAWAH ---
                    initialTanggalPasang = detail!!.tanggal_pasang ?: "",
                    initialTanggalAktif = detail!!.tanggal_aktif ?: "",
                    initialKeterangan = detail!!.keterangan ?: "",
                    initialIsolationDefault = detail!!.isolation_use_default ?: true,
                    initialIsolationLimit = detail!!.isolation_period_limit ?: 2,
                    onSave = { updatedRequest ->
                        pelangganVM.updatePelanggan(pelangganId, updatedRequest)
                    }
                )
            }
        }
    }

    if (showDismissDialog) {
        AlertDialog(
            onDismissRequest = { showDismissDialog = false },
            title = { Text("Batalkan Perubahan?") },
            text = { Text("Jika Anda kembali sekarang, perubahan data yang belum disimpan akan hilang.") },
            confirmButton = {
                TextButton(
                    onClick = {
                        showDismissDialog = false
                        navController.popBackStack()
                    }
                ) {
                    Text("Ya, Kembali", color = MaterialTheme.colorScheme.error)
                }
            },
            dismissButton = {
                TextButton(onClick = { showDismissDialog = false }) {
                    Text("Lanjut Edit")
                }
            }
        )
    }
}