package com.gns.billing.ui

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import com.gns.billing.core.Api
import com.gns.billing.core.LoginRequest
import com.gns.billing.core.SessionStore
import kotlinx.coroutines.launch

@Composable
fun LoginScreen(nav: NavController) {
    val context = LocalContext.current
    val session = remember { SessionStore(context) }
    val scope = rememberCoroutineScope()
    var email by remember { mutableStateOf("") }
    var password by remember { mutableStateOf("") }
    var busy by remember { mutableStateOf(false) }
    var error by remember { mutableStateOf<String?>(null) }

    Column(Modifier.fillMaxSize().padding(24.dp), verticalArrangement = Arrangement.Center) {
        Text("GNS Billing", style = MaterialTheme.typography.headlineMedium, fontWeight = FontWeight.Bold)
        Text("Login operator", modifier = Modifier.padding(top = 4.dp, bottom = 24.dp))
        OutlinedTextField(email, { email = it }, Modifier.fillMaxWidth(), label = { Text("Email") }, singleLine = true)
        Spacer(Modifier.height(12.dp))
        OutlinedTextField(password, { password = it }, Modifier.fillMaxWidth(), label = { Text("Password") }, singleLine = true)
        Spacer(Modifier.height(16.dp))
        error?.let { Text(it, color = MaterialTheme.colorScheme.error) }
        Spacer(Modifier.height(8.dp))
        Button(
            enabled = !busy,
            onClick = {
                scope.launch {
                    busy = true; error = null
                    try {
                        val response = Api.service.login(LoginRequest(email.trim(), password))
                        if (response.success && ((response.data?.token ?: response.token).isNullOrBlank().not())) {
                            session.save(response)
                            nav.navigate("dashboard") { popUpTo("login") { inclusive = true } }
                        } else error = response.message ?: "Login gagal."
                    } catch (e: Exception) { error = e.message ?: "Tidak dapat terhubung ke server." }
                    finally { busy = false }
                }
            },
            Modifier.fillMaxWidth()
        ) { if (busy) CircularProgressIndicator(Modifier.size(20.dp)) else Text("Masuk") }
    }
}

data class MenuItem(val title: String, val route: String, val icon: androidx.compose.ui.graphics.vector.ImageVector)

@Composable
fun DashboardScreen(nav: NavController) {
    val context = LocalContext.current
    val session = remember { SessionStore(context) }
    val menus = listOf(
        MenuItem("Pelanggan", "pelanggan", Icons.Default.People),
        MenuItem("Paket", "paket", Icons.Default.Inventory2),
        MenuItem("Tagihan", "tagihan", Icons.Default.ReceiptLong),
        MenuItem("Pembayaran", "pembayaran", Icons.Default.Payments),
        MenuItem("MikroTik", "mikrotik", Icons.Default.Router),
        MenuItem("WhatsApp", "whatsapp", Icons.Default.Message),
        MenuItem("Laporan", "laporan", Icons.Default.BarChart),
        MenuItem("Pengaturan", "pengaturan", Icons.Default.Settings)
    )
    Scaffold(topBar = {
        TopAppBar(title = { Text("GNS Billing") }, actions = {
            TextButton(onClick = { session.clear(); nav.navigate("login") { popUpTo(0) { inclusive = true } } }) { Text("Keluar") }
        })
    }) { padding ->
        LazyVerticalGrid(
            columns = GridCells.Fixed(2),
            modifier = Modifier.fillMaxSize().padding(padding).padding(16.dp),
            horizontalArrangement = Arrangement.spacedBy(12.dp),
            verticalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            items(menus) { item ->
                Card(onClick = { nav.navigate(item.route) }, shape = RoundedCornerShape(16.dp)) {
                    Column(Modifier.padding(18.dp), horizontalAlignment = Alignment.CenterHorizontally) {
                        Icon(item.icon, contentDescription = null, modifier = Modifier.size(34.dp))
                        Spacer(Modifier.height(8.dp))
                        Text(item.title, fontWeight = FontWeight.SemiBold)
                    }
                }
            }
        }
    }
}

@Composable
fun ModuleScreen(title: String, nav: NavController) {
    Scaffold(topBar = {
        TopAppBar(title = { Text(title) }, navigationIcon = {
            IconButton(onClick = { nav.popBackStack() }) { Icon(Icons.AutoMirrored.Filled.ArrowBack, null) }
        })
    }) { padding ->
        Box(Modifier.fillMaxSize().padding(padding), contentAlignment = Alignment.Center) {
            Text("Modul $title disiapkan sebagai native Android baru.")
        }
    }
}
