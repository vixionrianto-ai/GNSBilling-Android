package com.gns.billing.ui.paket

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavController
import com.gns.billing.model.Router
import com.gns.billing.viewmodel.PaketViewModel
import com.gns.billing.viewmodel.RouterViewModel

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun EditPaketScreen(
    navController: NavController,
    paketId: Int
) {

    val paketViewModel: PaketViewModel = viewModel()
    val routerViewModel: RouterViewModel = viewModel()

    val detail by paketViewModel.detailPaket.collectAsState()
    val routers by routerViewModel.router.collectAsState()
    val profiles by routerViewModel.profiles.collectAsState()

    val loading by paketViewModel.loading.collectAsState()
    val success by paketViewModel.success.collectAsState()

    LaunchedEffect(Unit) {
        routerViewModel.loadRouter()
        paketViewModel.getDetailPaket(paketId)
    }

    val selectedRouter = remember(detail, routers) {
        routers.firstOrNull {
            it.id == detail?.router_id
        }
    }

    LaunchedEffect(selectedRouter) {
        selectedRouter?.let {
            routerViewModel.loadProfiles(it.id)
        }
    }

    LaunchedEffect(success) {
        if (success) {
            paketViewModel.clearState()
            navController.popBackStack()
        }
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = {
                    Text(
                        "Edit Paket Internet",
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
        if (detail == null) {
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(padding),
                contentAlignment = Alignment.Center
            ) {
                CircularProgressIndicator()
            }
        } else {
            Surface(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(padding)
            ) {
                PaketForm(
                    routers = routers,
                    profiles = profiles,
                    loading = loading,
                    initialNama = detail!!.nama_paket,
                    initialHarga = detail!!.harga.toString(),
                    initialStatus = detail!!.status,
                    initialKeterangan = detail!!.keterangan ?: "",
                    initialRouter = selectedRouter,
                    initialProfile = detail!!.profile_mikrotik ?: "",
                    initialKecepatan = detail!!.kecepatan ?: "",
                    onRouterChanged = {
                        routerViewModel.loadProfiles(it.id)
                    },
                    onSave = { request ->
                        paketViewModel.updatePaket(
                            paketId,
                            request
                        )
                    }
                )
            }
        }
    }
}