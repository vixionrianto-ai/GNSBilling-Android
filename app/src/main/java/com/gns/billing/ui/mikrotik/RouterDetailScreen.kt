package com.gns.billing.ui.mikrotik

import android.widget.Toast
import androidx.compose.foundation.layout.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Router
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavController
import com.gns.billing.viewmodel.RouterViewModel

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun RouterDetailScreen(routerId: Int, navController: NavController, viewModel: RouterViewModel = viewModel()) {
    val profiles by viewModel.profiles.collectAsState()
    val isLoading by viewModel.loading.collectAsState()
    val connectionMessage by viewModel.connectionMessage.collectAsState()
    val context = LocalContext.current
    var showAddSecret by remember { mutableStateOf(false) }

    LaunchedEffect(routerId) { viewModel.loadProfiles(routerId) }

    Scaffold(topBar = {
        TopAppBar(title = { Text("Kelola Router #$routerId") }, navigationIcon = {
            IconButton(onClick = { navController.popBackStack() }) { Icon(Icons.AutoMirrored.Filled.ArrowBack, "Kembali") }
        })
    }, floatingActionButton = {
        FloatingActionButton(onClick = { showAddSecret = true }) { Icon(Icons.Default.Add, "Tambah Secret") }
    }) { padding ->
        Column(Modifier.fillMaxSize().padding(padding).padding(16.dp), verticalArrangement = Arrangement.spacedBy(12.dp)) {
            Card(Modifier.fillMaxWidth()) {
                Row(Modifier.fillMaxWidth().padding(16.dp), verticalAlignment = Alignment.CenterVertically) {
                    Icon(Icons.Default.Router, null, modifier = Modifier.size(40.dp))
                    Spacer(Modifier.width(16.dp))
                    Column(Modifier.weight(1f)) {
                        Text("Router MikroTik #$routerId", fontWeight = FontWeight.Bold)
                        Text("Status ditentukan oleh server GNS", style = MaterialTheme.typography.bodySmall)
                    }
                }
            }
            Button(onClick = { viewModel.testRouter(routerId) }, enabled = !isLoading, modifier = Modifier.fillMaxWidth()) {
                Text(if (isLoading) "Menguji..." else "Tes Koneksi MikroTik")
            }
            connectionMessage?.let { Text(it, color = MaterialTheme.colorScheme.primary) }
            Text("PPP Profile dari MikroTik", fontWeight = FontWeight.Bold)
            if (profiles.isEmpty()) Text("Tidak ada profile yang dikembalikan server.")
            else profiles.forEach { Text("• $it") }
            Button(onClick = { showAddSecret = true }, modifier = Modifier.fillMaxWidth()) {
                Icon(Icons.Default.Add, null); Spacer(Modifier.width(8.dp)); Text("Buat Secret PPPoE Baru")
            }
        }
    }

    if (showAddSecret) {
        AddSecretDialog(profiles, { showAddSecret = false }) { username, password, service, profile ->
            viewModel.createSecret(routerId, username, password, service, profile) { success, message ->
                Toast.makeText(context, message, Toast.LENGTH_LONG).show()
                if (success) showAddSecret = false
            }
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AddSecretDialog(profiles: List<String>, onDismiss: () -> Unit, onSubmit: (String, String, String, String) -> Unit) {
    var username by remember { mutableStateOf("") }
    var password by remember { mutableStateOf("") }
    var service by remember { mutableStateOf("pppoe") }
    var selectedProfile by remember { mutableStateOf(profiles.firstOrNull() ?: "") }
    var expandedService by remember { mutableStateOf(false) }
    var expandedProfile by remember { mutableStateOf(false) }

    AlertDialog(onDismissRequest = onDismiss, title = { Text("Tambah Secret PPPoE") }, text = {
        Column(Modifier.fillMaxWidth(), verticalArrangement = Arrangement.spacedBy(12.dp)) {
            OutlinedTextField(username, { username = it }, label = { Text("Username PPPoE") }, modifier = Modifier.fillMaxWidth(), singleLine = true)
            OutlinedTextField(password, { password = it }, label = { Text("Password") }, visualTransformation = PasswordVisualTransformation(), modifier = Modifier.fillMaxWidth(), singleLine = true)
            ExposedDropdownMenuBox(expandedService, { expandedService = !expandedService }) {
                OutlinedTextField(service, {}, readOnly = true, label = { Text("Service") }, trailingIcon = { ExposedDropdownMenuDefaults.TrailingIcon(expandedService) }, modifier = Modifier.fillMaxWidth().menuAnchor())
                ExposedDropdownMenu(expandedService, { expandedService = false }) {
                    listOf("pppoe", "pptp", "l2tp", "ovpn", "any").forEach { value -> DropdownMenuItem(text = { Text(value) }, onClick = { service = value; expandedService = false }) }
                }
            }
            ExposedDropdownMenuBox(expandedProfile, { expandedProfile = !expandedProfile }) {
                OutlinedTextField(selectedProfile, {}, readOnly = true, label = { Text("Profile") }, trailingIcon = { ExposedDropdownMenuDefaults.TrailingIcon(expandedProfile) }, modifier = Modifier.fillMaxWidth().menuAnchor())
                ExposedDropdownMenu(expandedProfile, { expandedProfile = false }) {
                    profiles.forEach { profile -> DropdownMenuItem(text = { Text(profile) }, onClick = { selectedProfile = profile; expandedProfile = false }) }
                }
            }
        }
    }, confirmButton = {
        Button(onClick = { if (username.isNotBlank() && password.isNotBlank() && selectedProfile.isNotBlank()) onSubmit(username, password, service, selectedProfile) }) { Text("Simpan Secret") }
    }, dismissButton = { TextButton(onClick = onDismiss) { Text("Batal") } })
}
