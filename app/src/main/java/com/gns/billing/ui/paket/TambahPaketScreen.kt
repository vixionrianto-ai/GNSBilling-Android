package com.gns.billing.ui.paket

import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavHostController
import com.gns.billing.viewmodel.PaketViewModel
import com.gns.billing.viewmodel.RouterViewModel

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun TambahPaketScreen(
    navController: NavHostController,
    paketViewModel: PaketViewModel = viewModel(),
    routerViewModel: RouterViewModel = viewModel()
) {
    val routers by routerViewModel.router.collectAsState()
    val profiles by routerViewModel.profiles.collectAsState()

    val loading by paketViewModel.loading.collectAsState()
    val success by paketViewModel.success.collectAsState()
    val error by paketViewModel.error.collectAsState()

    LaunchedEffect(Unit) {
        routerViewModel.loadRouter()
    }

    LaunchedEffect(success) {
        if (success) {
            navController.popBackStack()
        }
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = {
                    Text(
                        "Tambah Paket Internet",
                        fontWeight = FontWeight.Bold
                    )
                },
                navigationIcon = {
                    IconButton(onClick = { navController.popBackStack() }) {
                        Icon(
                            imageVector = Icons.AutoMirrored.Filled.ArrowBack,
                            contentDescription = "Kembali"
                        )
                    }
                }
            )
        }
    ) { padding ->
        Surface(
            modifier = Modifier
                .fillMaxSize()
                .padding(padding)
        ) {
            PaketForm(
                routers = routers,
                profiles = profiles,
                loading = loading,
                initialNama = "",
                initialHarga = "",
                initialStatus = "Aktif",
                initialKeterangan = "",
                initialRouter = null,
                initialProfile = "",
                initialKecepatan = "",
                onRouterChanged = { router ->
                    routerViewModel.loadProfiles(router.id)
                },
                onSave = { request ->
                    paketViewModel.tambahPaket(request)
                }
            )
        }
    }

    error?.let { err ->
        LaunchedEffect(err) {
            println(err)
        }
    }
}