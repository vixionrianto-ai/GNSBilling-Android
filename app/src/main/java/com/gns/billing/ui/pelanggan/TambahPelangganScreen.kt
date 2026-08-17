package com.gns.billing.ui.pelanggan

import androidx.compose.foundation.layout.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavController
import com.gns.billing.viewmodel.PaketViewModel
import com.gns.billing.viewmodel.RouterViewModel
import com.gns.billing.viewmodel.PelangganViewModel

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun TambahPelangganScreen(
    navController: NavController
) {
    val pelangganViewModel: PelangganViewModel = viewModel()
    val routerViewModel: RouterViewModel = viewModel()
    val paketViewModel: PaketViewModel = viewModel()

    val routers by routerViewModel.router.collectAsState()
    val pakets by paketViewModel.paket.collectAsState()
    val loading by pelangganViewModel.loading.collectAsState()
    val success by pelangganViewModel.success.collectAsState()
    val error by pelangganViewModel.error.collectAsState()
    val routerLoading by routerViewModel.loading.collectAsState()
    val paketLoading by paketViewModel.loading.collectAsState()

    val snackbarHostState = remember { SnackbarHostState() }

    LaunchedEffect(Unit) {
        routerViewModel.loadRouter()
        paketViewModel.loadPaket()
    }

    LaunchedEffect(success) {
        if (success) {
            pelangganViewModel.clearState()
            navController.popBackStack()
        }
    }

    LaunchedEffect(error) {
        error?.let {
            snackbarHostState.showSnackbar(it)
            pelangganViewModel.clearState()
        }
    }

    Scaffold(
        snackbarHost = { SnackbarHost(snackbarHostState) },
        topBar = {
            TopAppBar(
                title = { Text("Tambah Pelanggan") },
                navigationIcon = {
                    IconButton(onClick = { navController.popBackStack() }) {
                        Icon(Icons.AutoMirrored.Filled.ArrowBack, contentDescription = "Kembali")
                    }
                }
            )
        }
    ) { paddingValues ->
        if (routerLoading || paketLoading) {
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(paddingValues),
                contentAlignment = Alignment.Center
            ) {
                CircularProgressIndicator()
            }
        } else {
            Box(modifier = Modifier.padding(paddingValues)) {
                PelangganForm(
                    routers = routers,
                    pakets = pakets,
                    loading = loading,
                    onSave = { request ->
                        pelangganViewModel.tambahPelanggan(request)
                    }
                )
            }
        }
    }
}