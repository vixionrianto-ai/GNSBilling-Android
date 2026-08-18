package com.gns.billing.navigation

import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
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
import com.gns.billing.ui.laporan.LaporanScreen
import com.gns.billing.ui.mikrotik.MikroTikScreen
import com.gns.billing.ui.mikrotik.RouterDetailScreen
import com.gns.billing.ui.paket.EditPaketScreen
import com.gns.billing.ui.paket.PaketScreen
import com.gns.billing.ui.paket.TambahPaketScreen
import com.gns.billing.ui.pelanggan.DetailPelangganScreen
import com.gns.billing.ui.pelanggan.EditPelangganScreen
import com.gns.billing.ui.pelanggan.PelangganScreen
import com.gns.billing.ui.pelanggan.TambahPelangganScreen
import com.gns.billing.ui.pembayaran.PembayaranScreen
import com.gns.billing.ui.pembayaran.PaymentDetailScreen
import com.gns.billing.ui.pembayaran.RiwayatPembayaranScreen
import com.gns.billing.ui.whatsapp.WhatsAppHistoryDetailScreen
import com.gns.billing.ui.whatsapp.WhatsAppHistoryScreen

@OptIn(markerClass = [ExperimentalMaterial3Api::class])
@Composable
fun AppNavigation() {
    val navController = rememberNavController()
    val context = LocalContext.current
    val sessionManager = SessionManager(context)
    SessionProvider.token = sessionManager.getToken()

    val startDestination = if (sessionManager.isLoggedIn()) "dashboard" else "login"

    NavHost(navController = navController, startDestination = startDestination) {
        composable("login") { LoginScreen(navController = navController) }
        composable("dashboard") { DashboardScreen(navController = navController) }

        composable("pelanggan") { PelangganScreen(navController = navController) }
        composable("tambah_pelanggan") { TambahPelangganScreen(navController = navController) }
        composable("edit_pelanggan/{id}") { entry ->
            val id = entry.arguments?.getString("id")?.toIntOrNull() ?: return@composable
            EditPelangganScreen(navController = navController, pelangganId = id)
        }
        composable("detail_pelanggan/{id}") { entry ->
            val id = entry.arguments?.getString("id")?.toIntOrNull() ?: return@composable
            DetailPelangganScreen(navController = navController, pelangganId = id)
        }

        composable("paket") { PaketScreen(navController = navController) }
        composable("tambah_paket") { TambahPaketScreen(navController = navController) }
        composable("edit_paket/{id}") { entry ->
            val id = entry.arguments?.getString("id")?.toIntOrNull() ?: return@composable
            EditPaketScreen(navController = navController, paketId = id)
        }

        composable("tagihan_jatuh_tempo") { TagihanJatuhTempoScreen(navController = navController) }
        composable("tagihan_semua") { TagihanScreen(navController = navController, pelangganId = 0) }
        composable("tagihan/{id}") { entry ->
            val id = entry.arguments?.getString("id")?.toIntOrNull() ?: 0
            TagihanScreen(pelangganId = id, navController = navController)
        }
        composable("detail_tagihan/{id}") { entry ->
            val id = entry.arguments?.getString("id")?.toIntOrNull() ?: 0
            DetailTagihanScreen(tagihanId = id, navController = navController)
        }

        composable("menu_pembayaran") { RiwayatPembayaranScreen(navController = navController) }
        composable("detail_pembayaran/{paymentId}", arguments = listOf(navArgument("paymentId") { type = NavType.IntType })) { entry ->
            PaymentDetailScreen(
                navController = navController,
                paymentId = entry.arguments?.getInt("paymentId") ?: 0
            )
        }
        composable("pembayaran_form/{tagihanId}", arguments = listOf(navArgument("tagihanId") { type = NavType.IntType })) { entry ->
            PembayaranScreen(
                navController = navController,
                tagihanId = entry.arguments?.getInt("tagihanId") ?: 0,
                namaPelanggan = "",
                invoiceNo = "",
                totalTagihan = ""
            )
        }
        composable("pembayaran_express") {
            PembayaranScreen(navController = navController, tagihanId = 0, namaPelanggan = "", invoiceNo = "", totalTagihan = "")
        }
        composable("pembayaran_screen/{tagihanId}/{namaPelanggan}/{invoiceNo}/{totalTagihan}", arguments = listOf(
            navArgument("tagihanId") { type = NavType.IntType },
            navArgument("namaPelanggan") { type = NavType.StringType },
            navArgument("invoiceNo") { type = NavType.StringType },
            navArgument("totalTagihan") { type = NavType.FloatType }
        )) { entry ->
            PembayaranScreen(
                navController = navController,
                tagihanId = entry.arguments?.getInt("tagihanId") ?: 0,
                namaPelanggan = entry.arguments?.getString("namaPelanggan") ?: "",
                invoiceNo = entry.arguments?.getString("invoiceNo") ?: "",
                totalTagihan = (entry.arguments?.getFloat("totalTagihan")?.toDouble() ?: 0.0).toString()
            )
        }

        composable("mikrotik") { MikroTikScreen(navController = navController) }
        composable("router_detail/{routerId}", arguments = listOf(navArgument("routerId") { type = NavType.IntType })) { entry ->
            RouterDetailScreen(
                routerId = entry.arguments?.getInt("routerId") ?: 0,
                navController = navController
            )
        }

        composable("laporan") { LaporanScreen(navController = navController) }
        composable("whatsapp_history") { WhatsAppHistoryScreen(navController = navController) }
        composable("whatsapp_history_detail/{id}", arguments = listOf(navArgument("id") { type = NavType.IntType })) { entry ->
            WhatsAppHistoryDetailScreen(
                navController = navController,
                logId = entry.arguments?.getInt("id") ?: 0
            )
        }
    }
}