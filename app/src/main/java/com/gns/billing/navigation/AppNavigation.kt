package com.gns.billing.navigation

import androidx.compose.runtime.Composable
import androidx.compose.ui.platform.LocalContext
import androidx.navigation.NavType
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import androidx.navigation.navArgument
import com.gns.billing.session.SessionManager
import com.gns.billing.session.SessionProvider
import com.gns.billing.tagihan.DetailTagihanScreen
import com.gns.billing.tagihan.TagihanJatuhTempoScreen
import com.gns.billing.tagihan.TagihanScreen
import com.gns.billing.ui.dashboard.DashboardScreen
import com.gns.billing.ui.login.LoginScreen
import com.gns.billing.ui.mikrotik.MikroTikScreen
import com.gns.billing.ui.mikrotik.RouterDetailScreen
import com.gns.billing.ui.paket.EditPaketScreen
import com.gns.billing.ui.paket.PaketScreen
import com.gns.billing.ui.paket.TambahPaketScreen
import com.gns.billing.ui.pelanggan.DetailPelangganScreen
import com.gns.billing.ui.pelanggan.EditPelangganScreen
import com.gns.billing.ui.pelanggan.PelangganScreen
import com.gns.billing.ui.pelanggan.TambahPelangganScreen
import com.gns.billing.ui.pembayaran.PembayaranDetailScreen
import com.gns.billing.ui.pembayaran.PembayaranHistoryScreen
import com.gns.billing.ui.pembayaran.PembayaranScreen
import com.gns.billing.ui.laporan.LaporanScreen

@Composable
fun AppNavigation() {
    val navController = rememberNavController()
    val context = LocalContext.current
    val sessionManager = SessionManager(context)
    SessionProvider.token = sessionManager.getToken()
    val startDestination = if (sessionManager.isLoggedIn()) "dashboard" else "login"

    NavHost(navController = navController, startDestination = startDestination) {
        composable("login") { LoginScreen(navController) }
        composable("dashboard") { DashboardScreen(navController) }

        composable("pelanggan") { PelangganScreen(navController) }
        composable("tambah_pelanggan") { TambahPelangganScreen(navController) }
        composable("edit_pelanggan/{id}") { e ->
            e.arguments?.getString("id")?.toIntOrNull()?.let { EditPelangganScreen(navController, it) }
        }
        composable("detail_pelanggan/{id}") { e ->
            e.arguments?.getString("id")?.toIntOrNull()?.let { DetailPelangganScreen(it, navController) }
        }

        composable("paket") { PaketScreen(navController) }
        composable("tambah_paket") { TambahPaketScreen(navController) }
        composable("edit_paket/{id}") { e ->
            e.arguments?.getString("id")?.toIntOrNull()?.let { EditPaketScreen(navController, it) }
        }

        composable("tagihan_semua") { TagihanScreen(navController, 0) }
        composable("tagihan_jatuh_tempo") { TagihanJatuhTempoScreen(navController) }
        composable("tagihan/{id}") { e ->
            e.arguments?.getString("id")?.toIntOrNull()?.let { TagihanScreen(navController, it) }
        }
        composable("detail_tagihan/{id}") { e ->
            e.arguments?.getString("id")?.toIntOrNull()?.let { DetailTagihanScreen(navController, it) }
        }

        composable("menu_pembayaran") { PembayaranHistoryScreen(navController) }
        composable(
            "detail_pembayaran/{paymentId}",
            arguments = listOf(navArgument("paymentId") { type = NavType.IntType })
        ) { e ->
            PembayaranDetailScreen(navController, e.arguments?.getInt("paymentId") ?: 0)
        }
        composable(
            "pembayaran_form/{tagihanId}",
            arguments = listOf(navArgument("tagihanId") { type = NavType.IntType })
        ) { e ->
            PembayaranScreen(navController, e.arguments?.getInt("tagihanId") ?: 0)
        }

        composable("mikrotik") { MikroTikScreen(navController) }
        composable(
            "router_detail/{routerId}",
            arguments = listOf(navArgument("routerId") { type = NavType.IntType })
        ) { e ->
            RouterDetailScreen(e.arguments?.getInt("routerId") ?: 0, navController)
        }
        composable("laporan") { LaporanScreen(navController) }
    }
}
