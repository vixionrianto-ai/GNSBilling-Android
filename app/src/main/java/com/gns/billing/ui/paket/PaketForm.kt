package com.gns.billing.ui.paket

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.gns.billing.model.PaketRequest
import com.gns.billing.model.Router
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.ui.text.font.FontWeight

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun PaketForm(
    routers: List<Router>,
    profiles: List<String>,
    loading: Boolean,
    initialNama: String = "",
    initialHarga: String = "",
    initialStatus: String = "Aktif",
    initialKeterangan: String = "",
    initialRouter: Router? = null,
    initialProfile: String = "",
    initialKecepatan: String = "",
    onRouterChanged: (Router) -> Unit,
    onSave: (PaketRequest) -> Unit
) {
    var namaPaket by rememberSaveable { mutableStateOf("") }
    var harga by rememberSaveable { mutableStateOf("") }
    var status by rememberSaveable { mutableStateOf("Aktif") }
    var keterangan by rememberSaveable { mutableStateOf("") }
    var selectedRouter by remember { mutableStateOf<Router?>(null) }
    var selectedProfile by rememberSaveable { mutableStateOf("") }
    var kecepatan by rememberSaveable { mutableStateOf("") }
    var routerExpanded by remember { mutableStateOf(false) }
    var profileExpanded by remember { mutableStateOf(false) }

    LaunchedEffect(
        initialNama,
        initialHarga,
        initialStatus,
        initialKeterangan,
        initialProfile,
        initialKecepatan,
        initialRouter
    ) {
        namaPaket = initialNama
        harga = if (initialHarga.isNotBlank()) {
            val cleanInit = initialHarga.filter { it.isDigit() }
            val parsedInit = cleanInit.toLongOrNull() ?: 0L
            java.text.DecimalFormat("#,###").format(parsedInit)
        } else {
            ""
        }
        status = initialStatus
        keterangan = initialKeterangan
        selectedRouter = initialRouter
        selectedProfile = initialProfile
        kecepatan = initialKecepatan
    }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .verticalScroll(rememberScrollState())
            .padding(16.dp)
    ) {
        OutlinedTextField(
            value = namaPaket,
            onValueChange = { namaPaket = it },
            label = { Text("Nama Paket") },
            shape = RoundedCornerShape(16.dp),
            singleLine = true,
            modifier = Modifier.fillMaxWidth()
        )

        Spacer(modifier = Modifier.height(16.dp))

        ExposedDropdownMenuBox(
            expanded = routerExpanded,
            onExpandedChange = { routerExpanded = !routerExpanded }
        ) {
            OutlinedTextField(
                value = selectedRouter?.nama_router ?: "",
                onValueChange = {},
                readOnly = true,
                label = { Text("Router") },
                trailingIcon = {
                    ExposedDropdownMenuDefaults.TrailingIcon(expanded = routerExpanded)
                },
                shape = RoundedCornerShape(16.dp),
                modifier = Modifier.menuAnchor().fillMaxWidth()
            )
            ExposedDropdownMenu(
                expanded = routerExpanded,
                onDismissRequest = { routerExpanded = false }
            ) {
                routers.forEach { router ->
                    DropdownMenuItem(
                        text = { Text(router.nama_router) },
                        onClick = {
                            selectedRouter = router
                            selectedProfile = ""
                            kecepatan = ""
                            routerExpanded = false
                            onRouterChanged(router)
                        }
                    )
                }
            }
        }

        Spacer(modifier = Modifier.height(16.dp))

        ExposedDropdownMenuBox(
            expanded = profileExpanded,
            onExpandedChange = {
                if (selectedRouter != null) profileExpanded = !profileExpanded
            }
        ) {
            OutlinedTextField(
                value = selectedProfile,
                onValueChange = {},
                readOnly = true,
                enabled = selectedRouter != null,
                label = { Text("PPP Profile") },
                trailingIcon = {
                    ExposedDropdownMenuDefaults.TrailingIcon(expanded = profileExpanded)
                },
                shape = RoundedCornerShape(16.dp),
                modifier = Modifier.menuAnchor().fillMaxWidth()
            )
            ExposedDropdownMenu(
                expanded = profileExpanded,
                onDismissRequest = { profileExpanded = false }
            ) {
                profiles.forEach { profile ->
                    DropdownMenuItem(
                        text = { Text(profile) },
                        onClick = {
                            selectedProfile = profile
                            profileExpanded = false
                            // Kecepatan ditentukan server dari profile MikroTik.
                            kecepatan = ""
                        }
                    )
                }
            }
        }

        Spacer(modifier = Modifier.height(16.dp))

        OutlinedTextField(
            value = kecepatan,
            onValueChange = {},
            readOnly = true,
            label = { Text("Kecepatan (ditentukan server)") },
            supportingText = { Text("Server akan mengisi kecepatan berdasarkan PPP Profile saat disimpan.") },
            shape = RoundedCornerShape(16.dp),
            singleLine = true,
            modifier = Modifier.fillMaxWidth()
        )

        Spacer(modifier = Modifier.height(16.dp))

        OutlinedTextField(
            value = harga,
            onValueChange = { input ->
                val cleanString = input.filter { it.isDigit() }
                harga = if (cleanString.isNotEmpty()) {
                    java.text.DecimalFormat("#,###").format(cleanString.toLongOrNull() ?: 0L)
                } else {
                    ""
                }
            },
            label = { Text("Harga") },
            shape = RoundedCornerShape(16.dp),
            singleLine = true,
            modifier = Modifier.fillMaxWidth()
        )

        Spacer(modifier = Modifier.height(16.dp))

        var statusExpanded by remember { mutableStateOf(false) }
        ExposedDropdownMenuBox(
            expanded = statusExpanded,
            onExpandedChange = { statusExpanded = !statusExpanded }
        ) {
            OutlinedTextField(
                value = status,
                onValueChange = {},
                readOnly = true,
                label = { Text("Status") },
                trailingIcon = {
                    ExposedDropdownMenuDefaults.TrailingIcon(expanded = statusExpanded)
                },
                shape = RoundedCornerShape(16.dp),
                modifier = Modifier.menuAnchor().fillMaxWidth()
            )
            ExposedDropdownMenu(
                expanded = statusExpanded,
                onDismissRequest = { statusExpanded = false }
            ) {
                listOf("Aktif", "Nonaktif").forEach { stat ->
                    DropdownMenuItem(
                        text = { Text(stat) },
                        onClick = {
                            status = stat
                            statusExpanded = false
                        }
                    )
                }
            }
        }

        Spacer(modifier = Modifier.height(16.dp))

        OutlinedTextField(
            value = keterangan,
            onValueChange = { keterangan = it },
            label = { Text("Keterangan") },
            shape = RoundedCornerShape(16.dp),
            minLines = 3,
            modifier = Modifier.fillMaxWidth()
        )

        Spacer(modifier = Modifier.height(24.dp))

        val validForm = namaPaket.isNotBlank() &&
                selectedRouter != null &&
                selectedProfile.isNotBlank() &&
                harga.isNotBlank()

        if (!validForm) {
            Text(
                text = "Lengkapi semua data yang wajib diisi.",
                color = MaterialTheme.colorScheme.error,
                style = MaterialTheme.typography.bodySmall
            )
            Spacer(modifier = Modifier.height(12.dp))
        }

        Button(
            onClick = {
                val router = selectedRouter ?: return@Button
                val cleanHarga = harga.replace(".", "").replace(",", "").toDoubleOrNull() ?: 0.0

                onSave(
                    PaketRequest(
                        router_id = router.id,
                        nama_paket = namaPaket.trim(),
                        profile_mikrotik = selectedProfile,
                        kecepatan = kecepatan.ifBlank { null },
                        harga = cleanHarga,
                        status = status,
                        keterangan = if (keterangan.isBlank()) null else keterangan.trim()
                    )
                )
            },
            enabled = validForm && !loading,
            shape = RoundedCornerShape(16.dp),
            modifier = Modifier.fillMaxWidth().height(50.dp)
        ) {
            if (loading) {
                CircularProgressIndicator(modifier = Modifier.size(20.dp), strokeWidth = 2.dp)
                Spacer(modifier = Modifier.width(8.dp))
                Text("Menyimpan...")
            } else {
                Text("Simpan Paket", fontWeight = FontWeight.Bold)
            }
        }

        Spacer(modifier = Modifier.height(12.dp))
    }
}
