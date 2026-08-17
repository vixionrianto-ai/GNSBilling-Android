package com.gns.billing.navigation

import androidx.compose.foundation.layout.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.navigation.NavType
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import androidx.navigation.navArgument
import com.gns.billing.session.SessionManager
import com.gns.billing.session.SessionProvider
import com.gns.billing.tagihan.DetailTagihanScreen
import com.gns.billing.ui.dashboard.DashboardScreen
import com.gns.billing.ui.login.LoginScreen
import com.gns.billing.ui.paket.EditPaketScreen
import com.gns.billing.ui.paket.PaketScreen
import com.gns.billing.ui.paket.TambahPaketScreen
import com.gns.billing.ui.pelanggan.DetailPelangganScreen
import com.gns.billing.ui.pelanggan.EditPelangganScreen
import com.gns.billing.ui.pelanggan.PelangganScreen
import com.gns.billing.ui.pelanggan.TambahPelangganScreen
import com.gns.billing.tagihan.TagihanJatuhTempoScreen
import com.gns.billing.tagihan.TagihanScreen
import com.gns.billing.ui.mikrotik.MikroTikScreen
import com.gns.billing.ui.mikrotik.RouterDetailScreen
import com.gns.billing.ui.pembayaran.PembayaranScreen
import com.gns.billing.ui.pembayaran.RiwayatPembayaranScreen
import com.gns.billing.ui.pembayaran.PaymentDetailScreen
import com.gns.billing.ui.laporan.LaporanScreen // <-- TAMBAHAN IMPORT LAPORAN SCREEN

@OptIn(markerClass = [ExperimentalMaterial3Api::class])
@Composable
fun AppNavigation() {

    val navController = rememberNavController()
    val context = LocalContext.current
    val sessionManager = SessionManager(context)
    SessionProvider.token = sessionManager.getToken()

    val startDestination =
        if (sessionManager.isLoggedIn()) {
            "dashboard"
        } else {
            "login"
        }

    NavHost(
        navController = navController,
        startDestination = startDestination
    ) {

        composable("login") {
            LoginScreen(navController = navController)
        }

        composable("dashboard") {
            DashboardScreen(
                navController = navController
            )
        }

        composable("pelanggan") {
            PelangganScreen(
                navController = navController
            )
        }

        composable("tambah_pelanggan") {
            TambahPelangganScreen(
                navController = navController
            )
        }

        composable(
            route = "edit_pelanggan/{id}"
        ) { backStackEntry ->
            val id = backStackEntry.arguments
                ?.getString("id")
                ?.toIntOrNull() ?: return@composable

            EditPelangganScreen(
                navController = navController,
                pelangganId = id
            )
        }

        composable(
            route = "detail_pelanggan/{id}"
        ) { backStackEntry ->
            val id = backStackEntry.arguments
                ?.getString("id")
                ?.toIntOrNull() ?: return@composable

            DetailPelangganScreen(
                navController = navController,
                pelangganId = id
            )
        }

        composable("paket") {
            PaketScreen(
                navController = navController
            )
        }

        composable(
            route = "edit_paket/{id}"
        ) { backStackEntry ->
            val id = backStackEntry.arguments
                ?.getString("id")
                ?.toIntOrNull() ?: return@composable

            EditPaketScreen(
                navController = navController,
                paketId = id
            )
        }

        composable("tambah_paket") {
            TambahPaketScreen(
                navController = navController
            )
        }

        // --- RUTE TAGIHAN JATUH TEMPO ---
        composable("tagihan_jatuh_tempo") {
            TagihanJatuhTempoScreen(
                navController = navController
            )
        }

        // Rute menu Tagihan utama dari Dashboard
        composable("tagihan_semua") {
            TagihanScreen(
                navController = navController,
                pelangganId = 0
            )
        }

        // ======================================================================
        // MODUL PEMBAYARAN
        // ======================================================================

        // 1. Menu Utama Pembayaran (Daftar Histori & Filter)
        composable("menu_pembayaran") {
            RiwayatPembayaranScreen(
                navController = navController
            )
        }

        // 2. Layar Detail/Bukti Pembayaran
        composable(
            route = "detail_pembayaran/{paymentId}",
            arguments = listOf(
                navArgument("paymentId") { type = NavType.IntType }
            )
        ) { backStackEntry ->
            val paymentId = backStackEntry.arguments?.getInt("paymentId") ?: 0
            PaymentDetailScreen(
                navController = navController,
                paymentId = paymentId
            )
        }

        // 3. TAMBAHAN RUTE: Mengatasi agar "pembayaran_form" tidak force close
        composable(
            route = "pembayaran_form/{tagihanId}",
            arguments = listOf(
                navArgument("tagihanId") { type = NavType.IntType }
            )
        ) { backStackEntry ->
            val tagihanId = backStackEntry.arguments?.getInt("tagihanId") ?: 0
            PembayaranScreen(
                navController = navController,
                tagihanId = tagihanId,
                namaPelanggan = "",
                invoiceNo = "",
                totalTagihan = ""
            )
        }

        // 4. Form Input Pembayaran Berdasarkan Tagihan (Parameter Lengkap)
        composable(
            route = "pembayaran_screen/{tagihanId}/{namaPelanggan}/{invoiceNo}/{totalTagihan}",
            arguments = listOf(
                navArgument("tagihanId") { type = NavType.IntType },
                navArgument("namaPelanggan") { type = NavType.StringType },
                navArgument("invoiceNo") { type = NavType.StringType },
                navArgument("totalTagihan") { type = NavType.FloatType }
            )
        ) { backStackEntry ->
            val tagihanId = backStackEntry.arguments?.getInt("tagihanId") ?: 0
            val namaPelanggan = backStackEntry.arguments?.getString("namaPelanggan") ?: ""
            val invoiceNo = backStackEntry.arguments?.getString("invoiceNo") ?: ""
            val totalTagihan = backStackEntry.arguments?.getFloat("totalTagihan")?.toDouble() ?: 0.0

            PembayaranScreen(
                navController = navController,
                tagihanId = tagihanId,
                namaPelanggan = namaPelanggan,
                invoiceNo = invoiceNo,
                totalTagihan = totalTagihan.toString()
            )
        }

        // 5. Form Bayar di Awal / Express Payment (Cari Pelanggan Langsung)
        composable("pembayaran_express") {
            PembayaranScreen(
                navController = navController,
                tagihanId = 0,
                namaPelanggan = "",
                invoiceNo = "",
                totalTagihan = ""
            )
        }

        // ======================================================================

        composable(
            route = "tagihan/{id}"
        ) { backStackEntry ->
            val id = backStackEntry
                .arguments
                ?.getString("id")
                ?.toIntOrNull() ?: 0

            TagihanScreen(
                pelangganId = id,
                navController = navController
            )
        }

        composable(
            route = "detail_tagihan/{id}"
        ) { backStackEntry ->
            val id = backStackEntry.arguments
                ?.getString("id")
                ?.toIntOrNull() ?: 0

            DetailTagihanScreen(
                tagihanId = id,
                navController = navController
            )
        }

        // ======================================================================
        // MODUL MIKROTIK
        // ======================================================================
        composable("mikrotik") {
            MikroTikScreen(navController = navController)
        }

        composable(
            route = "router_detail/{routerId}",
            arguments = listOf(
                navArgument("routerId") { type = NavType.IntType }
            )
        ) { backStackEntry ->
            val routerId = backStackEntry.arguments?.getInt("routerId") ?: 0
            RouterDetailScreen(
                routerId = routerId,
                navController = navController
            )
        }

        // PERBAIKAN: Mengarahkan rute "laporan" ke LaporanScreen yang asli, bukan PlaceholderScreen
        composable("laporan") {
            LaporanScreen(navController = navController)
        }

    }
}