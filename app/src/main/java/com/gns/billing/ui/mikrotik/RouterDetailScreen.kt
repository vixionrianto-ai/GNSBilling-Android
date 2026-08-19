package com.gns.billing.ui.mikrotik

import android.widget.Toast
import androidx.compose.foundation.layout.*
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
import com.gns.billing.model.PppProfile
import com.gns.billing.model.PppSecret
import com.gns.billing.viewmodel.RouterViewModel

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun RouterDetailScreen(routerId: Int, navController: NavController, viewModel: RouterViewModel = viewModel()) {
    val profiles by viewModel.profiles.collectAsState()
    val pppProfiles by viewModel.pppProfiles.collectAsState()
    val loading by viewModel.loading.collectAsState()
    val message by viewModel.connectionMessage.collectAsState()
    val context = LocalContext.current
    var secrets by remember { mutableStateOf<List<PppSecret>>(emptyList()) }
    var showAddSecret by remember { mutableStateOf(false) }
    var showAddProfile by remember { mutableStateOf(false) }
    var editSecret by remember { mutableStateOf<PppSecret?>(null) }
    var editProfile by remember { mutableStateOf<PppProfile?>(null) }

    fun refresh() {
        viewModel.loadProfiles(routerId)
        viewModel.loadPppProfiles(routerId)
        viewModel.loadSecrets(routerId) { secrets = it }
    }

    LaunchedEffect(routerId) { refresh() }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Kelola Router #$routerId") },
                navigationIcon = {
                    IconButton(onClick = { navController.popBackStack() }) {
                        Icon(Icons.AutoMirrored.Filled.ArrowBack, "Kembali")
                    }
                }
            )
        },
        floatingActionButton = {
            FloatingActionButton(onClick = { showAddSecret = true }) {
                Icon(Icons.Default.Add, "Tambah Secret")
            }
        }
    ) { padding ->
        Column(
            Modifier
                .fillMaxSize()
                .padding(padding)
                .padding(16.dp)
        ) {
            Card(Modifier.fillMaxWidth()) {
                Row(Modifier.padding(16.dp), verticalAlignment = Alignment.CenterVertically) {
                    Icon(Icons.Default.Router, null, Modifier.size(40.dp))
                    Spacer(Modifier.width(12.dp))
                    Text("Router #$routerId", fontWeight = FontWeight.Bold)
                }
            }
            Spacer(Modifier.height(10.dp))
            Button(
                onClick = { viewModel.testRouter(routerId) },
                enabled = !loading,
                modifier = Modifier.fillMaxWidth()
            ) {
                Text(if (loading) "Menguji..." else "Tes Koneksi MikroTik")
            }
            message?.let {
                Text(it, color = MaterialTheme.colorScheme.primary, modifier = Modifier.padding(vertical = 8.dp))
            }
            Text("PPP Secret", fontWeight = FontWeight.Bold, modifier = Modifier.padding(vertical = 8.dp))
            if (secrets.isEmpty()) {
                Text("Tidak ada Secret yang dikembalikan server.")
            } else {
                secrets.forEach { secret ->
                    SecretCard(
                        secret,
                        { editSecret = secret },
                        {
                            secret.id?.let { id ->
                                viewModel.deleteSecret(routerId, id) { ok, msg ->
                                    Toast.makeText(context, msg, Toast.LENGTH_SHORT).show()
                                    if (ok) refresh()
                                }
                            }
                        },
                        { disabled ->
                            secret.id?.let { id ->
                                if (disabled) {
                                    viewModel.enableSecret(routerId, id) { ok, msg ->
                                        Toast.makeText(context, msg, Toast.LENGTH_SHORT).show()
                                        if (ok) refresh()
                                    }
                                } else {
                                    viewModel.disableSecret(routerId, id) { ok, msg ->
                                        Toast.makeText(context, msg, Toast.LENGTH_SHORT).show()
                                        if (ok) refresh()
                                    }
                                }
                            }
                        }
                    )
                }
            }
            Spacer(Modifier.height(14.dp))
            Row(
                Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text("PPP Profile", fontWeight = FontWeight.Bold)
                IconButton(onClick = { showAddProfile = true }) {
                    Icon(Icons.Default.Add, "Tambah Profile")
                }
            }
            if (pppProfiles.isEmpty()) {
                Text("Tidak ada PPP Profile yang dikembalikan server.")
            } else {
                pppProfiles.forEach { profile ->
                    ProfileCard(
                        profile,
                        { editProfile = profile },
                        {
                            profile.id?.let { id ->
                                viewModel.deleteProfile(routerId, id) { ok, msg ->
                                    Toast.makeText(context, msg, Toast.LENGTH_SHORT).show()
                                    if (ok) refresh()
                                }
                            }
                        }
                    )
                }
            }
        }
    }

    if (showAddSecret) {
        AddSecretDialog(profiles, { showAddSecret = false }) { u, p, s, pr ->
            viewModel.createSecret(routerId, u, p, s, pr) { ok, msg ->
                Toast.makeText(context, msg, Toast.LENGTH_SHORT).show()
                if (ok) {
                    showAddSecret = false
                    refresh()
                }
            }
        }
    }

    editSecret?.let { s ->
        EditSecretDialog(s, profiles, { editSecret = null }) { u, p, sv, pr, d ->
            s.id?.let { id ->
                viewModel.updateSecret(routerId, id, u, p, sv, pr, d) { ok, msg ->
                    Toast.makeText(context, msg, Toast.LENGTH_SHORT).show()
                    if (ok) {
                        editSecret = null
                        refresh()
                    }
                }
            }
        }
    }

    if (showAddProfile) {
        ProfileDialog("Tambah PPP Profile", null, { showAddProfile = false }) { n, l, r, rate, one ->
            viewModel.createPppProfile(routerId, n, l, r, rate, one) { ok, msg ->
                Toast.makeText(context, msg, Toast.LENGTH_SHORT).show()
                if (ok) {
                    showAddProfile = false
                    refresh()
                }
            }
        }
    }

    editProfile?.let { p ->
        ProfileDialog("Edit PPP Profile", p, { editProfile = null }) { n, l, r, rate, one ->
            p.id?.let { id ->
                viewModel.updatePppProfile(routerId, id, n, l, r, rate, one) { ok, msg ->
                    Toast.makeText(context, msg, Toast.LENGTH_SHORT).show()
                    if (ok) {
                        editProfile = null
                        refresh()
                    }
                }
            }
        }
    }
}

@Composable
private fun SecretCard(
    s: PppSecret,
    onEdit: () -> Unit,
    onDelete: () -> Unit,
    onToggle: (Boolean) -> Unit
) {
    val d = s.disabled == "true" || s.disabled == "1"
    Card(Modifier.fillMaxWidth()) {
        Column(Modifier.padding(12.dp)) {
            Text(s.name ?: "-", fontWeight = FontWeight.Bold)
            Text("Service: ${s.service ?: "-"}")
            Text("Profile: ${s.profile ?: "-"}")
            Text(if (d) "Disabled" else "Enabled")
            Row {
                TextButton(onClick = onEdit) {
                    Icon(Icons.Default.Edit, null)
                    Text("Edit")
                }
                TextButton(onClick = { onToggle(d) }) {
                    Text(if (d) "Enable" else "Disable")
                }
                TextButton(onClick = onDelete) {
                    Icon(Icons.Default.Delete, null)
                    Text("Hapus")
                }
            }
        }
    }
}

@Composable
private fun ProfileCard(p: PppProfile, onEdit: () -> Unit, onDelete: () -> Unit) {
    Card(Modifier.fillMaxWidth()) {
        Column(Modifier.padding(12.dp)) {
            Text(p.name ?: "-", fontWeight = FontWeight.Bold)
            Text("Local: ${p.local_address ?: "-"}")
            Text("Remote: ${p.remote_address ?: "-"}")
            Text("Rate: ${p.rate_limit ?: "-"}")
            Text("Only One: ${p.only_one ?: "-"}")
            Row {
                TextButton(onClick = onEdit) {
                    Icon(Icons.Default.Edit, null)
                    Text("Edit")
                }
                TextButton(onClick = onDelete) {
                    Icon(Icons.Default.Delete, null)
                    Text("Hapus")
                }
            }
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AddSecretDialog(
    profiles: List<String>,
    onDismiss: () -> Unit,
    onSubmit: (String, String, String, String) -> Unit
) = SecretForm("Tambah Secret PPPoE", profiles, null, onDismiss) { u, p, s, pr, _ ->
    onSubmit(u, p, s, pr)
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun EditSecretDialog(
    s: PppSecret,
    profiles: List<String>,
    onDismiss: () -> Unit,
    onSubmit: (String, String, String, String, Boolean) -> Unit
) = SecretForm("Edit Secret PPPoE", profiles, s, onDismiss, onSubmit)

@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun SecretForm(
    title: String,
    profiles: List<String>,
    secret: PppSecret?,
    onDismiss: () -> Unit,
    onSubmit: (String, String, String, String, Boolean) -> Unit
) {
    var u by remember { mutableStateOf(secret?.name ?: "") }
    var p by remember { mutableStateOf("") }
    var sv by remember { mutableStateOf(secret?.service ?: "pppoe") }
    var pr by remember { mutableStateOf(secret?.profile ?: profiles.firstOrNull().orEmpty()) }
    var d by remember { mutableStateOf(secret?.disabled == "true" || secret?.disabled == "1") }

    AlertDialog(
        onDismissRequest = onDismiss,
        title = { Text(title) },
        text = {
            Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                OutlinedTextField(u, { u = it }, label = { Text("Username") }, singleLine = true)
                OutlinedTextField(
                    p,
                    { p = it },
                    label = { Text(if (secret == null) "Password" else "Password baru") },
                    visualTransformation = PasswordVisualTransformation(),
                    singleLine = true
                )
                OutlinedTextField(sv, { sv = it }, label = { Text("Service") }, singleLine = true)
                OutlinedTextField(pr, { pr = it }, label = { Text("Profile") }, singleLine = true)
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Checkbox(d, { d = it })
                    Text("Disabled")
                }
            }
        },
        confirmButton = {
            Button(onClick = {
                if (u.isNotBlank() && pr.isNotBlank() && (secret != null || p.isNotBlank())) {
                    onSubmit(u, p, sv, pr, d)
                }
            }) { Text("Simpan") }
        },
        dismissButton = { TextButton(onClick = onDismiss) { Text("Batal") } }
    )
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun ProfileDialog(
    title: String,
    profile: PppProfile?,
    onDismiss: () -> Unit,
    onSubmit: (String, String, String, String, String) -> Unit
) {
    var n by remember { mutableStateOf(profile?.name ?: "") }
    var l by remember { mutableStateOf(profile?.local_address ?: "") }
    var r by remember { mutableStateOf(profile?.remote_address ?: "") }
    var rate by remember { mutableStateOf(profile?.rate_limit ?: "") }
    var one by remember { mutableStateOf(profile?.only_one ?: "yes") }

    AlertDialog(
        onDismissRequest = onDismiss,
        title = { Text(title) },
        text = {
            Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                OutlinedTextField(n, { n = it }, label = { Text("Name") }, singleLine = true)
                OutlinedTextField(l, { l = it }, label = { Text("Local Address") }, singleLine = true)
                OutlinedTextField(r, { r = it }, label = { Text("Remote Address") }, singleLine = true)
                OutlinedTextField(rate, { rate = it }, label = { Text("Rate Limit") }, singleLine = true)
                OutlinedTextField(one, { one = it }, label = { Text("Only One") }, singleLine = true)
            }
        },
        confirmButton = {
            Button(onClick = {
                if (n.isNotBlank() && one.isNotBlank()) onSubmit(n, l, r, rate, one)
            }) { Text("Simpan") }
        },
        dismissButton = { TextButton(onClick = onDismiss) { Text("Batal") } }
    )
}
