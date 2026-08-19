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
    routers: List<Router>, profiles: List<String>, loading: Boolean,
    initialNama: String = "", initialHarga: String = "", initialStatus: String = "aktif",
    initialKeterangan: String = "", initialRouter: Router? = null,
    initialProfile: String = "", initialKecepatan: String = "",
    onRouterChanged: (Router) -> Unit, onSave: (PaketRequest) -> Unit
) {
    var namaPaket by rememberSaveable { mutableStateOf(initialNama) }
    var harga by rememberSaveable { mutableStateOf(initialHarga) }
    var status by rememberSaveable { mutableStateOf(initialStatus) }
    var keterangan by rememberSaveable { mutableStateOf(initialKeterangan) }
    var selectedRouter by remember { mutableStateOf(initialRouter) }
    var selectedProfile by rememberSaveable { mutableStateOf(initialProfile) }
    var kecepatan by rememberSaveable { mutableStateOf(initialKecepatan) }
    var routerExpanded by remember { mutableStateOf(false) }
    var profileExpanded by remember { mutableStateOf(false) }
    var statusExpanded by remember { mutableStateOf(false) }

    LaunchedEffect(initialNama, initialHarga, initialStatus, initialKeterangan, initialRouter, initialProfile, initialKecepatan) {
        namaPaket = initialNama
        harga = initialHarga
        status = initialStatus
        keterangan = initialKeterangan
        selectedRouter = initialRouter
        selectedProfile = initialProfile
        kecepatan = initialKecepatan
    }

    fun speedFromProfile(profile: String): String {
        val match = Regex("^C(\\d+)$", RegexOption.IGNORE_CASE).find(profile.trim())
        return if (match != null) "${match.groupValues[1]} Mbps" else ""
    }

    Column(Modifier.fillMaxSize().verticalScroll(rememberScrollState()).padding(16.dp)) {
        OutlinedTextField(namaPaket, { namaPaket = it }, label = { Text("Nama Paket") }, shape = RoundedCornerShape(16.dp), singleLine = true, modifier = Modifier.fillMaxWidth())
        Spacer(Modifier.height(16.dp))

        ExposedDropdownMenuBox(routerExpanded, { routerExpanded = !routerExpanded }) {
            OutlinedTextField(selectedRouter?.nama_router ?: "", {}, readOnly = true, label = { Text("Router") }, trailingIcon = { ExposedDropdownMenuDefaults.TrailingIcon(routerExpanded) }, modifier = Modifier.menuAnchor().fillMaxWidth())
            ExposedDropdownMenu(routerExpanded, { routerExpanded = false }) {
                routers.forEach { router -> DropdownMenuItem(text = { Text(router.nama_router) }, onClick = {
                    selectedRouter = router; selectedProfile = ""; kecepatan = ""; routerExpanded = false; onRouterChanged(router)
                }) }
            }
        }
        Spacer(Modifier.height(16.dp))

        ExposedDropdownMenuBox(profileExpanded, { if (selectedRouter != null) profileExpanded = !profileExpanded }) {
            OutlinedTextField(selectedProfile, {}, readOnly = true, enabled = selectedRouter != null, label = { Text("PPP Profile MikroTik") }, trailingIcon = { ExposedDropdownMenuDefaults.TrailingIcon(profileExpanded) }, modifier = Modifier.menuAnchor().fillMaxWidth())
            ExposedDropdownMenu(profileExpanded, { profileExpanded = false }) {
                profiles.forEach { profile -> DropdownMenuItem(text = { Text(profile) }, onClick = {
                    selectedProfile = profile
                    // Sama dengan website: C20 -> 20 Mbps; profile lain tidak dipaksakan.
                    kecepatan = speedFromProfile(profile)
                    profileExpanded = false
                }) }
            }
        }
        Spacer(Modifier.height(16.dp))

        OutlinedTextField(kecepatan, {}, readOnly = true, label = { Text("Kecepatan") }, supportingText = { Text("Otomatis dari PPP Profile, sama seperti website.") }, modifier = Modifier.fillMaxWidth(), singleLine = true)
        Spacer(Modifier.height(16.dp))

        OutlinedTextField(harga, { input ->
            val clean = input.filter(Char::isDigit)
            harga = if (clean.isNotEmpty()) java.text.DecimalFormat("#,###").format(clean.toLongOrNull() ?: 0L) else ""
        }, label = { Text("Harga") }, modifier = Modifier.fillMaxWidth(), singleLine = true)
        Spacer(Modifier.height(16.dp))

        ExposedDropdownMenuBox(statusExpanded, { statusExpanded = !statusExpanded }) {
            OutlinedTextField(status, {}, readOnly = true, label = { Text("Status") }, trailingIcon = { ExposedDropdownMenuDefaults.TrailingIcon(statusExpanded) }, modifier = Modifier.menuAnchor().fillMaxWidth())
            ExposedDropdownMenu(statusExpanded, { statusExpanded = false }) {
                listOf("aktif", "nonaktif").forEach { value -> DropdownMenuItem(text = { Text(if (value == "aktif") "Aktif" else "Nonaktif") }, onClick = { status = value; statusExpanded = false }) }
            }
        }
        Spacer(Modifier.height(16.dp))

        OutlinedTextField(keterangan, { keterangan = it }, label = { Text("Keterangan") }, minLines = 3, modifier = Modifier.fillMaxWidth())
        Spacer(Modifier.height(24.dp))

        val valid = namaPaket.isNotBlank() && selectedRouter != null && selectedProfile.isNotBlank() && kecepatan.isNotBlank() && harga.isNotBlank()
        if (!valid) Text("Lengkapi Router, Profile, Kecepatan, Nama Paket, dan Harga.", color = MaterialTheme.colorScheme.error, style = MaterialTheme.typography.bodySmall)
        Spacer(Modifier.height(12.dp))
        Button(onClick = {
            val router = selectedRouter ?: return@Button
            val cleanHarga = harga.filter(Char::isDigit).toDoubleOrNull() ?: 0.0
            onSave(PaketRequest(router.id, namaPaket.trim(), selectedProfile, kecepatan, cleanHarga, status, keterangan.trim().ifBlank { null }))
        }, enabled = valid && !loading, modifier = Modifier.fillMaxWidth().height(50.dp)) {
            if (loading) CircularProgressIndicator(Modifier.size(20.dp), strokeWidth = 2.dp) else Text("Simpan Paket", fontWeight = FontWeight.Bold)
        }
    }
}
