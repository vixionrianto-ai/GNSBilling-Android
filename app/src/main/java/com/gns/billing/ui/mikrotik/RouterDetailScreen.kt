package com.gns.billing.ui.mikrotik

import android.widget.Toast
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.Edit
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
import com.gns.billing.model.PppSecret
import com.gns.billing.viewmodel.RouterViewModel

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun RouterDetailScreen(routerId: Int, navController: NavController, viewModel: RouterViewModel = viewModel()) {
    val profiles by viewModel.profiles.collectAsState()
    val loading by viewModel.loading.collectAsState()
    val message by viewModel.connectionMessage.collectAsState()
    val context = LocalContext.current
    var secrets by remember { mutableStateOf<List<PppSecret>>(emptyList()) }
    var showAdd by remember { mutableStateOf(false) }
    var editSecret by remember { mutableStateOf<PppSecret?>(null) }

    LaunchedEffect(routerId) {
        viewModel.loadProfiles(routerId)
        viewModel.loadSecrets(routerId) { secrets = it }
    }

    Scaffold(topBar = { TopAppBar(title = { Text("Kelola Router #$routerId") }, navigationIcon = { IconButton(onClick = { navController.popBackStack() }) { Icon(Icons.AutoMirrored.Filled.ArrowBack, "Kembali") } }) }, floatingActionButton = {
        FloatingActionButton(onClick = { showAdd = true }) { Icon(Icons.Default.Add, "Tambah Secret") }
    }) { padding ->
        Column(Modifier.fillMaxSize().padding(padding).padding(16.dp)) {
            Card(Modifier.fillMaxWidth()) { Row(Modifier.padding(16.dp), verticalAlignment = Alignment.CenterVertically) { Icon(Icons.Default.Router, null, Modifier.size(40.dp)); Spacer(Modifier.width(12.dp)); Text("Router #$routerId", fontWeight = FontWeight.Bold) } }
            Spacer(Modifier.height(10.dp))
            Button(onClick = { viewModel.testRouter(routerId) }, enabled = !loading, Modifier.fillMaxWidth()) { Text(if (loading) "Menguji..." else "Tes Koneksi MikroTik") }
            message?.let { Text(it, color = MaterialTheme.colorScheme.primary, modifier = Modifier.padding(vertical = 8.dp)) }
            Text("PPP Secret", fontWeight = FontWeight.Bold, modifier = Modifier.padding(vertical = 8.dp))
            if (secrets.isEmpty()) Text("Tidak ada Secret yang dikembalikan server.")
            else LazyColumn(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                items(secrets, key = { it.id ?: it.name.orEmpty() }) { secret ->
                    SecretCard(secret, onEdit = { editSecret = secret }, onDelete = {
                        secret.id?.let { id -> viewModel.deleteSecret(routerId, id) { ok, msg -> Toast.makeText(context, msg, Toast.LENGTH_SHORT).show(); if (ok) viewModel.loadSecrets(routerId) { secrets = it } } }
                    }, onToggle = { disabled ->
                        secret.id?.let { id -> if (disabled) viewModel.enableSecret(routerId, id) else viewModel.disableSecret(routerId, id) { ok, msg -> Toast.makeText(context, msg, Toast.LENGTH_SHORT).show(); if (ok) viewModel.loadSecrets(routerId) { secrets = it } } }
                    })
                }
            }
        }
    }

    if (showAdd) AddSecretDialog(profiles, { showAdd = false }) { u, p, s, profile -> viewModel.createSecret(routerId, u, p, s, profile) { ok, msg -> Toast.makeText(context, msg, Toast.LENGTH_SHORT).show(); if (ok) { showAdd = false; viewModel.loadSecrets(routerId) { secrets = it } } } }
    editSecret?.let { secret -> EditSecretDialog(secret, profiles, { editSecret = null }) { u, p, s, profile, disabled -> secret.id?.let { id -> viewModel.updateSecret(routerId, id, u, p, s, profile, disabled) { ok, msg -> Toast.makeText(context, msg, Toast.LENGTH_SHORT).show(); if (ok) { editSecret = null; viewModel.loadSecrets(routerId) { secrets = it } } } } } }
}

@Composable
private fun SecretCard(secret: PppSecret, onEdit: () -> Unit, onDelete: () -> Unit, onToggle: (Boolean) -> Unit) {
    val disabled = secret.disabled == "true" || secret.disabled == "1"
    Card(Modifier.fillMaxWidth()) { Column(Modifier.padding(12.dp)) {
        Text(secret.name ?: "-", fontWeight = FontWeight.Bold)
        Text("Service: ${secret.service ?: "-"}")
        Text("Profile: ${secret.profile ?: "-"}")
        Text(if (disabled) "Disabled" else "Enabled")
        Row(horizontalArrangement = Arrangement.spacedBy(6.dp)) {
            TextButton(onClick = onEdit) { Icon(Icons.Default.Edit, null); Text("Edit") }
            TextButton(onClick = { onToggle(disabled) }) { Text(if (disabled) "Enable" else "Disable") }
            TextButton(onClick = onDelete) { Icon(Icons.Default.Delete, null); Text("Hapus") }
        }
    } }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AddSecretDialog(profiles: List<String>, onDismiss: () -> Unit, onSubmit: (String, String, String, String) -> Unit) {
    SecretForm("Tambah Secret PPPoE", profiles, null, onDismiss, onSubmit = { u, p, s, pr, _ -> onSubmit(u, p, s, pr) })
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun EditSecretDialog(secret: PppSecret, profiles: List<String>, onDismiss: () -> Unit, onSubmit: (String, String, String, String, Boolean) -> Unit) {
    SecretForm("Edit Secret PPPoE", profiles, secret, onDismiss, onSubmit)
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun SecretForm(title: String, profiles: List<String>, secret: PppSecret?, onDismiss: () -> Unit, onSubmit: (String, String, String, String, Boolean) -> Unit) {
    var username by remember { mutableStateOf(secret?.name ?: "") }
    var password by remember { mutableStateOf("") }
    var service by remember { mutableStateOf(secret?.service ?: "pppoe") }
    var profile by remember { mutableStateOf(secret?.profile ?: profiles.firstOrNull().orEmpty()) }
    var disabled by remember { mutableStateOf(secret?.disabled == "true" || secret?.disabled == "1") }
    AlertDialog(onDismissRequest = onDismiss, title = { Text(title) }, text = { Column(verticalArrangement = Arrangement.spacedBy(10.dp)) {
        OutlinedTextField(username, { username = it }, label = { Text("Username") }, singleLine = true)
        OutlinedTextField(password, { password = it }, label = { Text(if (secret == null) "Password" else "Password baru (opsional)") }, visualTransformation = PasswordVisualTransformation(), singleLine = true)
        OutlinedTextField(service, { service = it }, label = { Text("Service") }, singleLine = true)
        OutlinedTextField(profile, { profile = it }, label = { Text("Profile") }, singleLine = true)
        Row(verticalAlignment = Alignment.CenterVertically) { Checkbox(disabled, { disabled = it }); Text("Disabled") }
    } }, confirmButton = { Button(onClick = { if (username.isNotBlank() && profile.isNotBlank() && (secret != null || password.isNotBlank())) onSubmit(username, password, service, profile, disabled) }) { Text("Simpan") } }, dismissButton = { TextButton(onClick = onDismiss) { Text("Batal") } })
}
