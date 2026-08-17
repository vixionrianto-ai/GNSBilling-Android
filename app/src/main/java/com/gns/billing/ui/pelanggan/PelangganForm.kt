package com.gns.billing.ui.pelanggan

import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.interaction.collectIsPressedAsState
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.gns.billing.model.Paket
import com.gns.billing.model.PelangganRequest
import com.gns.billing.model.Router
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale
import java.util.TimeZone

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun PelangganForm(
    routers: List<Router>,
    pakets: List<Paket>,
    loading: Boolean,
    initialNama: String = "",
    initialNoHp: String = "",
    initialAlamat: String = "",
    initialPaket: Paket? = null,
    initialRouter: Router? = null,
    initialUsername: String = "",
    initialPassword: String = "",
    initialIp: String = "",
    initialMac: String = "",
    initialTanggalPasang: String = "",
    initialTanggalAktif: String = "",
    initialStatus: String = "Aktif",
    initialKeterangan: String = "",
    initialIsolationDefault: Boolean = true,
    initialIsolationLimit: Int = 2,
    onSave: (PelangganRequest) -> Unit
) {
    var nama by remember(initialNama) { mutableStateOf(initialNama) }
    var noHp by remember(initialNoHp) { mutableStateOf(initialNoHp) }
    var alamat by remember(initialAlamat) { mutableStateOf(initialAlamat) }
    var router by remember(initialRouter) { mutableStateOf(initialRouter) }
    var paket by remember(initialPaket) { mutableStateOf(initialPaket) }
    var username by remember(initialUsername) { mutableStateOf(initialUsername) }
    var password by remember(initialPassword) { mutableStateOf(initialPassword) }
    var ipAddress by remember(initialIp) { mutableStateOf(initialIp) }
    var macAddress by remember(initialMac) { mutableStateOf(initialMac) }
    var tanggalPasang by remember(initialTanggalPasang) { mutableStateOf(initialTanggalPasang) }
    var tanggalAktif by remember(initialTanggalAktif) { mutableStateOf(initialTanggalAktif) }
    var status by remember(initialStatus) { mutableStateOf(initialStatus) }
    var keterangan by remember(initialKeterangan) { mutableStateOf(initialKeterangan) }
    var useDefaultIsolation by remember(initialIsolationDefault) { mutableStateOf(initialIsolationDefault) }
    var isolationLimit by remember(initialIsolationLimit) { mutableStateOf(initialIsolationLimit.toString()) }

    LaunchedEffect(initialPaket, initialRouter, initialNama, initialNoHp, initialAlamat, initialUsername, initialPassword, initialIp, initialMac, initialTanggalPasang, initialTanggalAktif, initialStatus, initialKeterangan, initialIsolationDefault, initialIsolationLimit) {
        if (initialPaket != null) paket = initialPaket
        if (initialRouter != null) router = initialRouter
        nama = initialNama
        noHp = initialNoHp
        alamat = initialAlamat
        username = initialUsername
        password = initialPassword
        ipAddress = initialIp
        macAddress = initialMac
        tanggalPasang = initialTanggalPasang
        tanggalAktif = initialTanggalAktif
        status = initialStatus
        keterangan = initialKeterangan
        useDefaultIsolation = initialIsolationDefault
        isolationLimit = initialIsolationLimit.toString()
    }

    var paketExpanded by remember { mutableStateOf(false) }
    var routerExpanded by remember { mutableStateOf(false) }
    var statusExpanded by remember { mutableStateOf(false) }
    var showDatePickerPasang by remember { mutableStateOf(false) }
    var showDatePickerAktif by remember { mutableStateOf(false) }

    val datePickerStatePasang = rememberDatePickerState()
    val datePickerStateAktif = rememberDatePickerState()
    val dateFormatUtc = remember {
        SimpleDateFormat("yyyy-MM-dd", Locale.getDefault()).apply {
            timeZone = TimeZone.getTimeZone("UTC")
        }
    }
    val scrollState = rememberScrollState()

    if (showDatePickerPasang) {
        DatePickerDialog(
            onDismissRequest = { showDatePickerPasang = false },
            confirmButton = {
                TextButton(onClick = {
                    datePickerStatePasang.selectedDateMillis?.let { tanggalPasang = dateFormatUtc.format(Date(it)) }
                    showDatePickerPasang = false
                }) { Text("Pilih") }
            },
            dismissButton = { TextButton(onClick = { showDatePickerPasang = false }) { Text("Batal") } }
        ) { DatePicker(state = datePickerStatePasang) }
    }

    if (showDatePickerAktif) {
        DatePickerDialog(
            onDismissRequest = { showDatePickerAktif = false },
            confirmButton = {
                TextButton(onClick = {
                    datePickerStateAktif.selectedDateMillis?.let { tanggalAktif = dateFormatUtc.format(Date(it)) }
                    showDatePickerAktif = false
                }) { Text("Pilih") }
            },
            dismissButton = { TextButton(onClick = { showDatePickerAktif = false }) { Text("Batal") } }
        ) { DatePicker(state = datePickerStateAktif) }
    }

    Column(
        modifier = Modifier.fillMaxSize().verticalScroll(scrollState).padding(16.dp),
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        FormSection(title = "Data Pelanggan", icon = Icons.Default.Person) {
            ElegantTextField(nama, { nama = it }, "Nama Pelanggan *", Icons.Default.Badge)
            ElegantTextField(noHp, { noHp = it }, "Nomor HP / WhatsApp *", Icons.Default.Phone, placeholder = "Contoh: 081234567890")
            ElegantTextField(alamat, { alamat = it }, "Alamat Pemasangan", Icons.Default.LocationOn, singleLine = false, minLines = 2)
        }

        FormSection(title = "Layanan Internet", icon = Icons.Default.Language) {
            ExposedDropdownMenuBox(
                expanded = paketExpanded,
                onExpandedChange = { paketExpanded = !paketExpanded }
            ) {
                ElegantTextField(
                    value = paket?.nama_paket ?: "",
                    onValueChange = {},
                    label = "Paket Internet *",
                    icon = Icons.Default.Layers,
                    readOnly = true,
                    trailingIcon = { ExposedDropdownMenuDefaults.TrailingIcon(expanded = paketExpanded) },
                    modifier = Modifier.menuAnchor()
                )
                ExposedDropdownMenu(paketExpanded, { paketExpanded = false }) {
                    pakets.forEach { item ->
                        DropdownMenuItem(
                            text = { Text(item.nama_paket) },
                            onClick = { paket = item; paketExpanded = false }
                        )
                    }
                }
            }

            ExposedDropdownMenuBox(
                expanded = routerExpanded,
                onExpandedChange = { routerExpanded = !routerExpanded }
            ) {
                ElegantTextField(
                    value = router?.nama_router ?: "",
                    onValueChange = {},
                    label = "Router MikroTik *",
                    icon = Icons.Default.Router,
                    readOnly = true,
                    trailingIcon = { ExposedDropdownMenuDefaults.TrailingIcon(expanded = routerExpanded) },
                    modifier = Modifier.menuAnchor()
                )
                ExposedDropdownMenu(routerExpanded, { routerExpanded = false }) {
                    routers.forEach { item ->
                        DropdownMenuItem(
                            text = { Text(item.nama_router) },
                            onClick = { router = item; routerExpanded = false }
                        )
                    }
                }
            }

            ExposedDropdownMenuBox(
                expanded = statusExpanded,
                onExpandedChange = { statusExpanded = !statusExpanded }
            ) {
                ElegantTextField(
                    value = status,
                    onValueChange = {},
                    label = "Status Layanan *",
                    icon = Icons.Default.Info,
                    readOnly = true,
                    trailingIcon = { ExposedDropdownMenuDefaults.TrailingIcon(expanded = statusExpanded) },
                    modifier = Modifier.menuAnchor()
                )
                ExposedDropdownMenu(statusExpanded, { statusExpanded = false }) {
                    listOf("Aktif", "Isolir", "Nonaktif").forEach { item ->
                        DropdownMenuItem(
                            text = { Text(item) },
                            onClick = { status = item; statusExpanded = false }
                        )
                    }
                }
            }
        }

        FormSection(title = "Konfigurasi Teknis (PPPoE / IP)", icon = Icons.Default.Settings) {
            ElegantTextField(username, { username = it }, "Username PPPoE", Icons.Default.AccountCircle)
            ElegantTextField(password, { password = it }, "Password PPPoE", Icons.Default.Password)
            ElegantTextField(ipAddress, { ipAddress = it }, "IP Address (Opsional)", Icons.Default.Dns, placeholder = "Contoh: 192.168.10.2")
            ElegantTextField(macAddress, { macAddress = it }, "MAC Address Router/Modem (Opsional)", Icons.Default.Pin, placeholder = "AA:BB:CC:DD:EE:FF")
        }

        FormSection(title = "Aktivasi & Penagihan", icon = Icons.Default.Event) {
            val interactionSourcePasang = remember { MutableInteractionSource() }
            val isPressedPasang by interactionSourcePasang.collectIsPressedAsState()
            LaunchedEffect(isPressedPasang) { if (isPressedPasang) showDatePickerPasang = true }
            ElegantTextField(tanggalPasang, { tanggalPasang = it }, "Tanggal Pasang", Icons.Default.CalendarToday, readOnly = true, interactionSource = interactionSourcePasang)

            val interactionSourceAktif = remember { MutableInteractionSource() }
            val isPressedAktif by interactionSourceAktif.collectIsPressedAsState()
            LaunchedEffect(isPressedAktif) { if (isPressedAktif) showDatePickerAktif = true }
            ElegantTextField(tanggalAktif, { tanggalAktif = it }, "Tanggal Aktif", Icons.Default.EventAvailable, readOnly = true, interactionSource = interactionSourceAktif)

            Text("Batas Waktu Isolir", style = MaterialTheme.typography.labelLarge, fontWeight = FontWeight.Bold, color = MaterialTheme.colorScheme.primary)
            Row(verticalAlignment = Alignment.CenterVertically) {
                RadioButton(selected = useDefaultIsolation, onClick = { useDefaultIsolation = true })
                Text("Default Sistem", modifier = Modifier.clickable { useDefaultIsolation = true })
                Spacer(Modifier.width(16.dp))
                RadioButton(selected = !useDefaultIsolation, onClick = { useDefaultIsolation = false })
                Text("Batas Khusus", modifier = Modifier.clickable { useDefaultIsolation = false })
            }
            if (!useDefaultIsolation) {
                ElegantTextField(isolationLimit, { isolationLimit = it }, "Periode Isolir (Hari)", Icons.Default.Timer)
            }
        }

        FormSection(title = "Catatan Tambahan", icon = Icons.Default.MoreHoriz) {
            ElegantTextField(keterangan, { keterangan = it }, "Keterangan / Catatan Teknis", Icons.Default.Description, singleLine = false, minLines = 3)
        }

        val validForm = nama.trim().isNotBlank() && noHp.trim().isNotBlank() && paket != null && router != null

        Button(
            onClick = {
                if (!validForm) return@Button
                onSave(
                    PelangganRequest(
                        nama = nama.trim(),
                        no_hp = noHp.trim(),
                        alamat = alamat.trim(),
                        paket_id = paket!!.id,
                        router_id = router!!.id,
                        status = status,
                        username_pppoe = username.trim(),
                        password_pppoe = password.trim(),
                        ip_address = ipAddress.trim().ifBlank { null },
                        mac_address = macAddress.trim().ifBlank { null },
                        tanggal_pasang = tanggalPasang.ifBlank { null },
                        tanggal_aktif = tanggalAktif.ifBlank { null },
                        keterangan = keterangan.trim().ifBlank { null },
                        isolation_use_default = useDefaultIsolation,
                        isolation_period_limit = if (useDefaultIsolation) null else isolationLimit.toIntOrNull()
                    )
                )
            },
            enabled = !loading && validForm,
            modifier = Modifier.fillMaxWidth().height(56.dp),
            shape = MaterialTheme.shapes.medium
        ) {
            if (loading) {
                CircularProgressIndicator(modifier = Modifier.size(24.dp), color = MaterialTheme.colorScheme.onPrimary, strokeWidth = 2.5.dp)
                Spacer(Modifier.width(12.dp))
                Text("Memproses...")
            } else {
                Text("Simpan Data Pelanggan", style = MaterialTheme.typography.titleMedium, fontWeight = FontWeight.Bold)
            }
        }

        Spacer(modifier = Modifier.height(24.dp))
    }
}

@Composable
fun FormSection(title: String, icon: ImageVector, content: @Composable ColumnScope.() -> Unit) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp),
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
        shape = MaterialTheme.shapes.medium
    ) {
        Column(modifier = Modifier.padding(16.dp), verticalArrangement = Arrangement.spacedBy(12.dp)) {
            Row(verticalAlignment = Alignment.CenterVertically) {
                Icon(icon, contentDescription = null, tint = MaterialTheme.colorScheme.primary, modifier = Modifier.size(20.dp))
                Spacer(Modifier.width(8.dp))
                Text(title, style = MaterialTheme.typography.titleSmall, fontWeight = FontWeight.Bold, color = MaterialTheme.colorScheme.primary)
            }
            HorizontalDivider(color = MaterialTheme.colorScheme.outlineVariant)
            content()
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ElegantTextField(
    value: String,
    onValueChange: (String) -> Unit,
    label: String,
    icon: ImageVector,
    modifier: Modifier = Modifier,
    placeholder: String? = null,
    singleLine: Boolean = true,
    minLines: Int = 1,
    readOnly: Boolean = false,
    trailingIcon: @Composable (() -> Unit)? = null,
    interactionSource: MutableInteractionSource = remember { MutableInteractionSource() }
) {
    OutlinedTextField(
        value = value,
        onValueChange = onValueChange,
        label = { Text(label) },
        leadingIcon = { Icon(icon, contentDescription = null, tint = MaterialTheme.colorScheme.primary) },
        trailingIcon = trailingIcon,
        modifier = modifier.fillMaxWidth(),
        placeholder = placeholder?.let { { Text(it) } },
        singleLine = singleLine,
        minLines = minLines,
        readOnly = readOnly,
        interactionSource = interactionSource,
        textStyle = LocalTextStyle.current.copy(color = MaterialTheme.colorScheme.onSurface),
        shape = MaterialTheme.shapes.small,
        colors = OutlinedTextFieldDefaults.colors(
            focusedTextColor = MaterialTheme.colorScheme.onSurface,
            unfocusedTextColor = MaterialTheme.colorScheme.onSurface,
            focusedBorderColor = MaterialTheme.colorScheme.primary,
            unfocusedBorderColor = MaterialTheme.colorScheme.outline,
            focusedLabelColor = MaterialTheme.colorScheme.primary,
            unfocusedLabelColor = MaterialTheme.colorScheme.onSurfaceVariant,
            cursorColor = MaterialTheme.colorScheme.primary
        )
    )
}
