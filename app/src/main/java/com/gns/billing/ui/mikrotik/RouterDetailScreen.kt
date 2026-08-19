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
    var showAddSecretDialog by remember { mutableStateOf(false) }

    LaunchedEffect(routerId) { viewModel.loadProfiles(routerId) }

    Scaffold(topBar = {
        TopAppBar(
            title = { Text("Kelola Router #$routerId") },
            navigationIcon = { IconButton(onClick = { navController.popBackStack() }) { Icon(Icons.AutoMirrored.Filled.ArrowBack, "Kembali") } }
        )
    }, floatingActionButton = {
        FloatingActionButton(onClick = { showAddSecretDialog = true }) { Icon(Icons.Default.Add, "Tambah Secret") }
    }) { paddingValues ->
        Column(Modifier.fillMaxSize().padding(paddingValues).padding(16.dp), verticalArrangement = Arrangement.spacedBy(12.dp)) {
            Card(Modifier.fillMaxWidth()) {
                Row(Modifier.fillMaxWidth().padding(16.dp), verticalAlignment = Alignment.CenterVertically) {
                    Icon(Icons.Default.Router, null, tint = MaterialTheme.colorScheme.primary, modifier = Modifier.size(40.dp))
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
            connectionMessage?.let {
                Text(it, color = MaterialTheme.colorScheme.primary)
            }
            Text("PPP Profile dari MikroTik", fontWeight = FontWeight.Bold)
            if (profiles.isEmpty()) Text("Tidak ada profile yang dikembalikan server.")
            else profiles.forEach { Text("• $it", style = MaterialTheme.typography.bodyMedium) }
            Button(onClick = { showAddSecretDialog = true }, modifier = Modifier.fillMaxWidth()) {
                Icon(Icons.Default.Add, null); Spacer(Modifier.width(8.dp)); Text("Buat Secret PPPoE Baru")
            }
        }
    }

    if (showAddSecretDialog) {
        AddSecretDialog(profiles, { showAddSecretDialog = false }) { username, password, profile ->
            viewModel.createSecret(routerId, username, password, profile) { success, message ->
                Toast.makeText(context, message, Toast.LENGTH_LONG).show()
                if (success) showAddSecretDialog = false
            }
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AddSecretDialog(profiles: List<String>, onDismiss: () -> Unit, onSubmit: (String, String, String) -> Unit) {
    var username by remember { mutableStateOf("") }
    var password by remember { mutableStateOf("") }
    var selectedProfile by remember { mutableStateOf(profiles.firstOrNull() ?: "") }
    var expanded by remember { mutableStateOf(false) }

    AlertDialog(onDismissRequest = onDismiss, title = { Text("Tambah Secret PPPoE") }, text = {
        Column(Modifier.fillMaxWidth(), verticalArrangement = Arrangement.spacedBy(12.dp)) {
            OutlinedTextField(username, { username = it }, label = { Text("Username PPPoE") }, modifier = Modifier.fillMaxWidth(), singleLine = true)
            OutlinedTextField(password, { password = it }, label = { Text("Password") }, visualTransformation = PasswordVisualTransformation(), modifier = Modifier.fillMaxWidth(), singleLine = true)
            ExposedDropdownMenuBox(expanded, { expanded = !expanded }) {
                OutlinedTextField(selectedProfile, {}, readOnly = true, label = { Text("Pilih Profil Paket") }, trailingIcon = { ExposedDropdownMenuDefaults.TrailingIcon(expanded) }, modifier = Modifier.fillMaxWidth().menuAnchor())
                ExposedDropdownMenu(expanded, { expanded = false }) {
                    profiles.forEach { profile -> DropdownMenuItem(text = { Text(profile) }, onClick = { selectedProfile = profile; expanded = false }) }
                }
            }
        }
    }, confirmButton = {
        Button(onClick = { if (username.isNotBlank() && password.isNotBlank() && selectedProfile.isNotBlank()) onSubmit(username, password, selectedProfile) }) { Text("Simpan Secret") }
    }, dismissButton = { TextButton(onClick = onDismiss) { Text("Batal") } })
}
