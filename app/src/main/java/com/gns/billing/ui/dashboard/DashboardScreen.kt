package com.gns.billing.ui.dashboard

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.Logout
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavHostController
import com.gns.billing.session.SessionManager
import com.gns.billing.ui.component.KpiCard
import com.gns.billing.utils.formatRupiah
import com.gns.billing.viewmodel.DashboardViewModel
import com.gns.billing.viewmodel.ProfileViewModel

data class DashboardMenu(
    val title: String,
    val route: String,
    val icon: ImageVector,
    val color: Color
)

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun DashboardScreen(
    navController: NavHostController,
    profileViewModel: ProfileViewModel = viewModel(),
    dashboardViewModel: DashboardViewModel = viewModel()
) {
    val context = LocalContext.current
    val session = SessionManager(context)
    val dashboardRes by dashboardViewModel.dashboard.collectAsState()
    val isLoading by dashboardViewModel.isLoading.collectAsState()
    val error by dashboardViewModel.error.collectAsState()

    LaunchedEffect(Unit) {
        dashboardViewModel.loadDashboard()
    }

    val menu = listOf(
        DashboardMenu("Pelanggan", "pelanggan", Icons.Default.People, Color(0xFF3B82F6)),
        DashboardMenu("Paket", "paket", Icons.Default.Inventory2, Color(0xFF8B5CF6)),
        DashboardMenu("Tagihan", "tagihan_semua", Icons.Default.ReceiptLong, Color(0xFFF59E0B)),
        DashboardMenu("Pembayaran", "menu_pembayaran", Icons.Default.Payments, Color(0xFF10B981)),
        DashboardMenu("Jatuh Tempo", "tagihan_jatuh_tempo", Icons.Default.AssignmentLate, Color(0xFFEF4444)),
        DashboardMenu("MikroTik", "mikrotik", Icons.Default.Router, Color(0xFFEC4899)),
        DashboardMenu("Laporan", "laporan", Icons.Default.BarChart, Color(0xFF6366F1))
    )

    Scaffold(
        topBar = {
            TopAppBar(
                title = {
                    Text(
                        "GNS Enterprise",
                        style = MaterialTheme.typography.titleLarge,
                        fontWeight = FontWeight.Bold
                    )
                },
                actions = {
                    IconButton(onClick = {
                        session.logout()
                        navController.navigate("login") {
                            popUpTo(0) { inclusive = true }
                        }
                    }) {
                        Icon(
                            imageVector = Icons.AutoMirrored.Filled.Logout,
                            contentDescription = "Logout",
                            tint = MaterialTheme.colorScheme.error
                        )
                    }
                }
            )
        }
    ) { padding ->
        Box(modifier = Modifier.fillMaxSize().padding(padding)) {
            when {
                isLoading && dashboardRes == null -> {
                    CircularProgressIndicator(modifier = Modifier.align(Alignment.Center))
                }
                error != null && dashboardRes == null -> {
                    Column(
                        modifier = Modifier.align(Alignment.Center).padding(32.dp),
                        horizontalAlignment = Alignment.CenterHorizontally
                    ) {
                        Text(
                            text = "Gagal Memuat Dashboard",
                            style = MaterialTheme.typography.titleMedium,
                            fontWeight = FontWeight.Bold
                        )
                        Text(
                            text = error ?: "Terjadi kesalahan",
                            color = MaterialTheme.colorScheme.error,
                            textAlign = TextAlign.Center,
                            style = MaterialTheme.typography.bodySmall
                        )
                        Spacer(modifier = Modifier.height(24.dp))
                        Row(horizontalArrangement = Arrangement.spacedBy(12.dp)) {
                            Button(onClick = { dashboardViewModel.loadDashboard() }) {
                                Text("Coba Lagi")
                            }
                            OutlinedButton(onClick = {
                                session.logout()
                                navController.navigate("login") {
                                    popUpTo(0) { inclusive = true }
                                }
                            }) {
                                Text("Login Ulang")
                            }
                        }
                    }
                }
                else -> {
                    LazyColumn(
                        modifier = Modifier
                            .fillMaxSize()
                            .padding(horizontal = 16.dp),
                        verticalArrangement = Arrangement.spacedBy(16.dp)
                    ) {
                        item {
                            Text(
                                text = "Halo, ${session.getName()}",
                                style = MaterialTheme.typography.titleMedium,
                                fontWeight = FontWeight.Bold,
                                modifier = Modifier.padding(top = 16.dp)
                            )
                        }

                        item {
                            dashboardRes?.data?.let { d ->
                                // Konversi nilai string/angka menjadi Double lalu diformat ke format Rupiah
                                val piutangDouble = d.totalPiutang?.replace(Regex("[^0-9.]"), "")?.toDoubleOrNull() ?: 0.0
                                val pendapatanDouble = d.pendapatanBulanIni?.replace(Regex("[^0-9.]"), "")?.toDoubleOrNull() ?: 0.0

                                LazyVerticalGrid(
                                    columns = GridCells.Fixed(2),
                                    modifier = Modifier.height(260.dp),
                                    horizontalArrangement = Arrangement.spacedBy(8.dp),
                                    verticalArrangement = Arrangement.spacedBy(8.dp)
                                ) {
                                    item { KpiCard("Total Pelanggan", d.totalPelanggan.toString()) }
                                    item { KpiCard("Piutang", formatRupiah(piutangDouble)) }
                                    item { KpiCard("Pendapatan Bulan", formatRupiah(pendapatanDouble)) }
                                    item { KpiCard("Lunas", d.tagihanLunas.toString()) }
                                }
                            }
                        }

                        item {
                            Text(
                                text = "Aksi Cepat",
                                fontSize = 14.sp,
                                fontWeight = FontWeight.Bold
                            )
                        }

                        item {
                            LazyVerticalGrid(
                                columns = GridCells.Fixed(3),
                                modifier = Modifier.height(280.dp),
                                horizontalArrangement = Arrangement.spacedBy(12.dp),
                                verticalArrangement = Arrangement.spacedBy(12.dp)
                            ) {
                                items(menu) { item ->
                                    Card(
                                        modifier = Modifier
                                            .fillMaxWidth()
                                            .clickable {
                                                navController.navigate(item.route)
                                            },
                                        shape = RoundedCornerShape(12.dp),
                                        colors = CardDefaults.cardColors(
                                            containerColor = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.5f)
                                        )
                                    ) {
                                        Column(
                                            modifier = Modifier.padding(12.dp),
                                            horizontalAlignment = Alignment.CenterHorizontally
                                        ) {
                                            Icon(
                                                imageVector = item.icon,
                                                contentDescription = null,
                                                tint = item.color,
                                                modifier = Modifier.size(24.dp)
                                            )
                                            Spacer(modifier = Modifier.height(4.dp))
                                            Text(
                                                text = item.title,
                                                fontSize = 11.sp,
                                                fontWeight = FontWeight.Bold,
                                                textAlign = TextAlign.Center
                                            )
                                        }
                                    }
                                }
                            }
                        }

                        item {
                            Spacer(modifier = Modifier.height(16.dp))
                        }
                    }
                }
            }
        }
    }
}