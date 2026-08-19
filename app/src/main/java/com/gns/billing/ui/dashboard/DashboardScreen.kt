package com.gns.billing.ui.dashboard

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.Logout
import androidx.compose.material.icons.filled.AssignmentLate
import androidx.compose.material.icons.filled.BarChart
import androidx.compose.material.icons.filled.Dashboard
import androidx.compose.material.icons.filled.Inventory2
import androidx.compose.material.icons.filled.People
import androidx.compose.material.icons.filled.Payments
import androidx.compose.material.icons.filled.ReceiptLong
import androidx.compose.material.icons.filled.Router
import androidx.compose.material3.Button
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.NavigationBar
import androidx.compose.material3.NavigationBarItem
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
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

private data class DashboardMenu(
    val title: String,
    val route: String,
    val icon: ImageVector,
    val color: Color
)

private data class BottomDestination(
    val title: String,
    val route: String,
    val icon: ImageVector
)

@OptIn(androidx.compose.material3.ExperimentalMaterial3Api::class)
@Composable
fun DashboardScreen(
    navController: NavHostController,
    dashboardViewModel: DashboardViewModel = viewModel()
) {
    val context = androidx.compose.ui.platform.LocalContext.current
    val session = SessionManager(context)
    val dashboardRes by dashboardViewModel.dashboard.collectAsState()
    val isLoading by dashboardViewModel.isLoading.collectAsState()
    val error by dashboardViewModel.error.collectAsState()

    LaunchedEffect(Unit) {
        dashboardViewModel.loadDashboard()
    }

    val quickActions = listOf(
        DashboardMenu("Pelanggan", "pelanggan", Icons.Default.People, Color(0xFF2563EB)),
        DashboardMenu("Paket", "paket", Icons.Default.Inventory2, Color(0xFF7C3AED)),
        DashboardMenu("Tagihan", "tagihan_semua", Icons.Default.ReceiptLong, Color(0xFFD97706)),
        DashboardMenu("Pembayaran", "menu_pembayaran", Icons.Default.Payments, Color(0xFF059669)),
        DashboardMenu("Jatuh Tempo", "tagihan_jatuh_tempo", Icons.Default.AssignmentLate, Color(0xFFDC2626)),
        DashboardMenu("MikroTik", "mikrotik", Icons.Default.Router, Color(0xFFDB2777)),
        DashboardMenu("Laporan", "laporan", Icons.Default.BarChart, Color(0xFF4F46E5))
    )

    val bottomDestinations = listOf(
        BottomDestination("Dashboard", "dashboard", Icons.Default.Dashboard),
        BottomDestination("Pelanggan", "pelanggan", Icons.Default.People),
        BottomDestination("Tagihan", "tagihan_semua", Icons.Default.ReceiptLong),
        BottomDestination("Bayar", "menu_pembayaran", Icons.Default.Payments)
    )

    Scaffold(
        topBar = {
            TopAppBar(
                title = {
                    Column {
                        Text(
                            text = "GNS Billing",
                            fontWeight = FontWeight.Bold
                        )
                        Text(
                            text = "Panel Operasional",
                            style = MaterialTheme.typography.labelSmall,
                            color = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                    }
                },
                actions = {
                    IconButton(
                        onClick = {
                            session.logout()
                            navController.navigate("login") {
                                popUpTo(0) { inclusive = true }
                            }
                        }
                    ) {
                        Icon(
                            imageVector = Icons.AutoMirrored.Filled.Logout,
                            contentDescription = "Keluar",
                            tint = MaterialTheme.colorScheme.error
                        )
                    }
                }
            )
        },
        bottomBar = {
            NavigationBar {
                bottomDestinations.forEach { destination ->
                    NavigationBarItem(
                        selected = destination.route == "dashboard",
                        onClick = {
                            if (destination.route != "dashboard") {
                                navController.navigate(destination.route) {
                                    launchSingleTop = true
                                    restoreState = true
                                }
                            }
                        },
                        icon = {
                            Icon(destination.icon, contentDescription = destination.title)
                        },
                        label = { Text(destination.title) }
                    )
                }
            }
        }
    ) { padding ->
        Box(
            modifier = Modifier
                .fillMaxSize()
                .padding(padding)
        ) {
            when {
                isLoading && dashboardRes == null -> {
                    CircularProgressIndicator(modifier = Modifier.align(Alignment.Center))
                }

                error != null && dashboardRes == null -> {
                    Column(
                        modifier = Modifier
                            .align(Alignment.Center)
                            .padding(32.dp),
                        horizontalAlignment = Alignment.CenterHorizontally,
                        verticalArrangement = Arrangement.spacedBy(12.dp)
                    ) {
                        Text(
                            text = "Dashboard tidak dapat dimuat",
                            style = MaterialTheme.typography.titleMedium,
                            fontWeight = FontWeight.Bold
                        )
                        Text(
                            text = error ?: "Terjadi kesalahan",
                            color = MaterialTheme.colorScheme.error,
                            textAlign = TextAlign.Center,
                            style = MaterialTheme.typography.bodySmall
                        )
                        Button(onClick = { dashboardViewModel.loadDashboard() }) {
                            Text("Coba Lagi")
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
                            Column(
                                modifier = Modifier.padding(top = 16.dp),
                                verticalArrangement = Arrangement.spacedBy(4.dp)
                            ) {
                                Text(
                                    text = "Halo, ${session.getName()}",
                                    style = MaterialTheme.typography.headlineSmall,
                                    fontWeight = FontWeight.Bold
                                )
                                Text(
                                    text = "Ringkasan operasional hari ini",
                                    style = MaterialTheme.typography.bodyMedium,
                                    color = MaterialTheme.colorScheme.onSurfaceVariant
                                )
                            }
                        }

                        item {
                            dashboardRes?.data?.let { data ->
                                val piutang = data.totalPiutang
                                    ?.replace(Regex("[^0-9.]"), "")
                                    ?.toDoubleOrNull() ?: 0.0
                                val pendapatan = data.pendapatanBulanIni
                                    ?.replace(Regex("[^0-9.]"), "")
                                    ?.toDoubleOrNull() ?: 0.0

                                Column(verticalArrangement = Arrangement.spacedBy(10.dp)) {
                                    Row(
                                        modifier = Modifier.fillMaxWidth(),
                                        horizontalArrangement = Arrangement.spacedBy(10.dp)
                                    ) {
                                        KpiCard(
                                            title = "Pelanggan",
                                            value = data.totalPelanggan.toString(),
                                            modifier = Modifier.weight(1f)
                                        )
                                        KpiCard(
                                            title = "Lunas",
                                            value = data.tagihanLunas.toString(),
                                            modifier = Modifier.weight(1f)
                                        )
                                    }
                                    Row(
                                        modifier = Modifier.fillMaxWidth(),
                                        horizontalArrangement = Arrangement.spacedBy(10.dp)
                                    ) {
                                        KpiCard(
                                            title = "Piutang",
                                            value = formatRupiah(piutang),
                                            modifier = Modifier.weight(1f)
                                        )
                                        KpiCard(
                                            title = "Pendapatan Bulan",
                                            value = formatRupiah(pendapatan),
                                            modifier = Modifier.weight(1f)
                                        )
                                    }
                                }
                            }
                        }

                        item {
                            Text(
                                text = "Menu Operasional",
                                style = MaterialTheme.typography.titleMedium,
                                fontWeight = FontWeight.Bold
                            )
                        }

                        item {
                            LazyVerticalGrid(
                                columns = GridCells.Fixed(2),
                                modifier = Modifier.height(360.dp),
                                horizontalArrangement = Arrangement.spacedBy(12.dp),
                                verticalArrangement = Arrangement.spacedBy(12.dp)
                            ) {
                                items(quickActions) { action ->
                                    Card(
                                        modifier = Modifier
                                            .fillMaxWidth()
                                            .clickable { navController.navigate(action.route) },
                                        shape = RoundedCornerShape(20.dp),
                                        colors = CardDefaults.cardColors(
                                            containerColor = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.55f)
                                        )
                                    ) {
                                        Column(
                                            modifier = Modifier
                                                .fillMaxWidth()
                                                .padding(18.dp),
                                            horizontalAlignment = Alignment.CenterHorizontally,
                                            verticalArrangement = Arrangement.spacedBy(8.dp)
                                        ) {
                                            Icon(
                                                imageVector = action.icon,
                                                contentDescription = null,
                                                tint = action.color,
                                                modifier = Modifier.size(28.dp)
                                            )
                                            Text(
                                                text = action.title,
                                                fontSize = 12.sp,
                                                fontWeight = FontWeight.SemiBold,
                                                textAlign = TextAlign.Center
                                            )
                                        }
                                    }
                                }
                            }
                        }

                        item {
                            Spacer(modifier = Modifier.height(8.dp))
                        }
                    }
                }
            }
        }
    }
}
