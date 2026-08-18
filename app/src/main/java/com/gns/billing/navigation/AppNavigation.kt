package com.gns.billing.navigation

import android.content.Context
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import com.gns.billing.core.SessionStore
import com.gns.billing.ui.DashboardScreen
import com.gns.billing.ui.LoginScreen
import com.gns.billing.ui.ModuleScreen

@Composable
fun AppNavigation() {
    val nav = rememberNavController()
    val context: Context = androidx.compose.ui.platform.LocalContext.current
    val session = remember { SessionStore(context) }
    val start = if (session.loggedIn()) "dashboard" else "login"

    NavHost(navController = nav, startDestination = start) {
        composable("login") { LoginScreen(nav) }
        composable("dashboard") { DashboardScreen(nav) }
        composable("pelanggan") { ModuleScreen("Pelanggan", nav) }
        composable("paket") { ModuleScreen("Paket Internet", nav) }
        composable("tagihan") { ModuleScreen("Tagihan", nav) }
        composable("pembayaran") { ModuleScreen("Pembayaran", nav) }
        composable("mikrotik") { ModuleScreen("MikroTik", nav) }
        composable("whatsapp") { ModuleScreen("Riwayat WhatsApp", nav) }
        composable("laporan") { ModuleScreen("Laporan", nav) }
        composable("pengaturan") { ModuleScreen("Pengaturan", nav) }
    }
}
