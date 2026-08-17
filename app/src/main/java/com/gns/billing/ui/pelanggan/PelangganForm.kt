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
    initialKode: String = "",
    initialKeterangan: String = "",
    initialIsolationDefault: Boolean = true,
    initialIsolationLimit: Int = 2,
    onSave: (PelangganRequest) -> Unit
) {
    val todayDateString = remember {
        SimpleDateFormat("yyyy-MM-dd", Locale.getDefault()).format(Date())
    }

    var kode by remember(initialKode) { mutableStateOf(initialKode) }
    var nama by remember(initialNama) { mutableStateOf(initialNama) }
    var noHp by remember(initialNoHp) { mutableStateOf(initialNoHp) }
    var alamat by remember(initialAlamat) { mutableStateOf(initialAlamat) }
    var router by remember(initialRouter) { mutableStateOf(initialRouter) }
    var paket by remember(initialPaket) { mutableStateOf(initialPaket) }
    var username by remember(initialUsername) { mutableStateOf(initialUsername) }
    var password by remember(initialPassword) { mutableStateOf(initialPassword) }
    var ipAddress by remember(initialIp) { mutableStateOf(initialIp) }
    var macAddress by remember(initialMac) { mutableStateOf(initialMac) }

    var tanggalPasang by remember(initialTanggalPasang) {
        mutableStateOf(if (initialTanggalPasang.isNotBlank()) initialTanggalPasang else todayDateString)
    }
    var tanggalAktif by remember(initialTanggalAktif) {
        mutableStateOf(if (initialTanggalAktif.isNotBlank()) initialTanggalAktif else todayDateString)
    }

    var status by remember(initialStatus) { mutableStateOf(initialStatus) }
    var keterangan by remember(initialKeterangan) { mutableStateOf(initialKeterangan) }
    var useDefaultIsolation by remember(initialIsolationDefault) { mutableStateOf(initialIsolationDefault) }
    var isolationLimit by remember(initialIsolationLimit) { mutableStateOf(initialIsolationLimit.toString()) }

    // --- SINKRONISASI DROPDOWN ---
    LaunchedEffect(initialPaket, initialRouter, pakets, routers) {
        if (initialPaket != null) {
            paket = initialPaket
        } else if (paket == null && pakets.isNotEmpty()) {
            paket = pakets.firstOrNull()
        }

        if (initialRouter != null) {
            router = initialRouter
        } else if (router == null && routers.isNotEmpty()) {
            router = routers.firstOrNull()
        }
    }

    // --- SINKRONISASI SELURUH KOLOM TEKS (ATAS SAMPAI BAWAH) ---
    LaunchedEffect(
        initialNama, initialNoHp, initialAlamat, initialUsername,
        initialIp, initialMac, initialStatus, initialKode,
        initialTanggalPasang, initialTanggalAktif, initialKeterangan,
        initialIsolationDefault, initialIsolationLimit
    ) {
        if (nama.isBlank() && initialNama.isNotBlank()) nama = initialNama
        if (noHp.isBlank() && initialNoHp.isNotBlank()) noHp = initialNoHp
        if (alamat.isBlank() && initialAlamat.isNotBlank()) alamat = initialAlamat
        if (username.isBlank() && initialUsername.isNotBlank()) username = initialUsername
        if (ipAddress.isBlank() && initialIp.isNotBlank()) ipAddress = initialIp
        if (macAddress.isBlank() && initialMac.isNotBlank()) macAddress = initialMac
        if (initialStatus.isNotBlank()) status = initialStatus
        if (kode.isBlank() && initialKode.isNotBlank()) kode = initialKode

        // --- AUTO-FILL KOLOM BAGIAN BAWAH ---
        if (initialTanggalPasang.isNotBlank()) tanggalPasang = initialTanggalPasang
        if (initialTanggalAktif.isNotBlank()) tanggalAktif = initialTanggalAktif
        if (initialKeterangan.isNotBlank()) keterangan = initialKeterangan
        useDefaultIsolation = initialIsolationDefault
        isolationLimit = initialIsolationLimit.toString()
    }

    var paketExpanded by remember { mutableStateOf(false) }
    var routerExpanded by remember { mutableStateOf(false) }
    var statusExpanded by remember { mutableStateOf(false) }

    var showDatePickerPasang by remember { mutableStateOf(false) }
    var showDatePickerAktif by remember { mutableStateOf(false) }

    val datePickerStatePasang = rememberDatePickerState(
        initialSelectedDateMillis = System.currentTimeMillis()
    )
    val datePickerStateAktif = rememberDatePickerState(
        initialSelectedDateMillis = System.currentTimeMillis()
    )

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
                    datePickerStatePasang.selectedDateMillis?.let {
                        tanggalPasang = dateFormatUtc.format(Date(it))
                    }
                    showDatePickerPasang = false
                }) { Text("Pilih") }
            },
            dismissButton = {
                TextButton(onClick = { showDatePickerPasang = false }) { Text("Batal") }
            }
        ) {
            DatePicker(state = datePickerStatePasang)
        }
    }

    if (showDatePickerAktif) {
        DatePickerDialog(
            onDismissRequest = { showDatePickerAktif = false },
            confirmButton = {
                TextButton(onClick = {
                    datePickerStateAktif.selectedDateMillis?.let {
                        tanggalAktif = dateFormatUtc.format(Date(it))
                    }
                    showDatePickerAktif = false
                }) { Text("Pilih") }
            },
            dismissButton = {
                TextButton(onClick = { showDatePickerAktif = false }) { Text("Batal") }
            }
        ) {
            DatePicker(state = datePickerStateAktif)
        }
    }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .verticalScroll(scrollState)
            .padding(16.dp),
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        FormSection(title = "Data Pelanggan", icon = Icons.Default.Person) {
            ElegantTextField(
                value = nama,
                onValueChange = { nama = it },
                label = "Nama Pelanggan *",
                icon = Icons.Default.Badge
            )
            ElegantTextField(
                value = noHp,
                onValueChange = { noHp = it },
                label = "Nomor HP / WhatsApp *",
                icon = Icons.Default.Phone,
                placeholder = "Contoh: 081234567890"
            )
            ElegantTextField(
                value = alamat,
                onValueChange = { alamat = it },
                label = "Alamat Pemasangan",
                icon = Icons.Default.LocationOn,
                singleLine = false,
                minLines = 2
            )
        }

        FormSection(title = "Layanan Internet", icon = Icons.Default.Language) {
            ExposedDropdownMenuBox(
                expanded = paketExpanded,
                onExpandedChange = { paketExpanded = !paketExpanded }
            ) {
                ElegantTextField(
                    value = paket?.nama_paket ?: "",
                    onValueChange = {},
                    readOnly = true,
                    label = "Paket Internet *",
                    icon = Icons.Default.Layers,
                    trailingIcon = { ExposedDropdownMenuDefaults.TrailingIcon(expanded = paketExpanded) },
                    modifier = Modifier.menuAnchor()
                )
                ExposedDropdownMenu(
                    expanded = paketExpanded,
                    onDismissRequest = { paketExpanded = false }
                ) {
                    pakets.forEach { item ->
                        DropdownMenuItem(
                            text = { Text(item.nama_paket) },
                            onClick = {
                                paket = item
                                paketExpanded = false
                            }
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
                    readOnly = true,
                    label = "Router MikroTik *",
                    icon = Icons.Default.Router,
                    trailingIcon = { ExposedDropdownMenuDefaults.TrailingIcon(expanded = routerExpanded) },
                    modifier = Modifier.menuAnchor()
                )
                ExposedDropdownMenu(
                    expanded = routerExpanded,
                    onDismissRequest = { routerExpanded = false }
                ) {
                    routers.forEach { item ->
                        DropdownMenuItem(
                            text = { Text(item.nama_router) },
                            onClick = {
                                router = item
                                routerExpanded = false
                            }
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
                    readOnly = true,
                    label = "Status Layanan *",
                    icon = Icons.Default.Info,
                    trailingIcon = { ExposedDropdownMenuDefaults.TrailingIcon(expanded = statusExpanded) },
                    modifier = Modifier.menuAnchor()
                )
                ExposedDropdownMenu(
                    expanded = statusExpanded,
                    onDismissRequest = { statusExpanded = false }
                ) {
                    listOf("Aktif", "Isolir", "Nonaktif").forEach { item ->
                        DropdownMenuItem(
                            text = { Text(item) },
                            onClick = {
                                status = item
                                statusExpanded = false
                            }
                        )
                    }
                }
            }
        }

        FormSection(title = "Konfigurasi Teknis (PPPoE / IP)", icon = Icons.Default.Settings) {
            ElegantTextField(
                value = username,
                onValueChange = { username = it },
                label = "Username PPPoE",
                icon = Icons.Default.AccountCircle
            )
            ElegantTextField(
                value = password,
                onValueChange = { password = it },
                label = "Password PPPoE",
                icon = Icons.Default.Password
            )
            ElegantTextField(
                value = ipAddress,
                onValueChange = { ipAddress = it },
                label = "IP Address (Opsional)",
                icon = Icons.Default.Dns,
                placeholder = "Contoh: 192.168.10.2"
            )
            ElegantTextField(
                value = macAddress,
                onValueChange = { macAddress = it },
                label = "MAC Address Router/Modem (Opsional)",
                icon = Icons.Default.Pin,
                placeholder = "AA:BB:CC:DD:EE:FF"
            )
        }

        FormSection(title = "Aktivasi & Penagihan", icon = Icons.Default.Event) {
            val interactionSourcePasang = remember { MutableInteractionSource() }
            val isPressedPasang by interactionSourcePasang.collectIsPressedAsState()

            LaunchedEffect(isPressedPasang) {
                if (isPressedPasang) showDatePickerPasang = true
            }

            ElegantTextField(
                value = tanggalPasang,
                onValueChange = { tanggalPasang = it },
                label = "Tanggal Pasang",
                icon = Icons.Default.CalendarToday,
                readOnly = true,
                interactionSource = interactionSourcePasang
            )

            val interactionSourceAktif = remember { MutableInteractionSource() }
            val isPressedAktif by interactionSourceAktif.collectIsPressedAsState()

            LaunchedEffect(isPressedAktif) {
                if (isPressedAktif) showDatePickerAktif = true
            }

            ElegantTextField(
                value = tanggalAktif,
                onValueChange = { tanggalAktif = it },
                label = "Tanggal Aktif",
                icon = Icons.Default.EventAvailable,
                readOnly = true,
                interactionSource = interactionSourceAktif
            )

            Text(
                text = "Batas Waktu Isolir",
                style = MaterialTheme.typography.labelLarge,
                fontWeight = FontWeight.Bold,
                color = MaterialTheme.colorScheme.primary
            )
            Row(verticalAlignment = Alignment.CenterVertically) {
                RadioButton(
                    selected = useDefaultIsolation,
                    onClick = { useDefaultIsolation = true }
                )
                Text(
                    text = "Default Sistem",
                    modifier = Modifier.clickable { useDefaultIsolation = true }
                )
                Spacer(Modifier.width(16.dp))
                RadioButton(
                    selected = !useDefaultIsolation,
                    onClick = { useDefaultIsolation = false }
                )
                Text(
                    text = "Batas Khusus",
                    modifier = Modifier.clickable { useDefaultIsolation = false }
                )
            }

            if (!useDefaultIsolation) {
                ElegantTextField(
                    value = isolationLimit,
                    onValueChange = { isolationLimit = it },
                    label = "Periode Isolir (Hari)",
                    icon = Icons.Default.Timer
                )
            }
        }

        FormSection(title = "Catatan Tambahan", icon = Icons.Default.MoreHoriz) {
            ElegantTextField(
                value = keterangan,
                onValueChange = { keterangan = it },
                label = "Keterangan / Catatan Teknis",
                icon = Icons.Default.Description,
                singleLine = false,
                minLines = 3
            )
        }

        val validForm = nama.trim().isNotBlank() &&
                noHp.trim().isNotBlank() &&
                paket != null &&
                router != null

        Button(
            onClick = {
                if (!validForm) return@Button

                val finalKode = if (kode.trim().isNotBlank()) {
                    kode.trim()
                } else {
                    "PLG-" + SimpleDateFormat("yyMMddHHmmss", Locale.getDefault()).format(Date())
                }

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
                        kode_pelanggan = finalKode,
                        keterangan = keterangan.trim().ifBlank { null },
                        isolation_use_default = useDefaultIsolation,
                        isolation_period_limit = if (useDefaultIsolation) null else isolationLimit.toIntOrNull()
                    )
                )
            },
            enabled = !loading && validForm,
            modifier = Modifier
                .fillMaxWidth()
                .height(56.dp),
            shape = MaterialTheme.shapes.medium
        ) {
            if (loading) {
                CircularProgressIndicator(
                    modifier = Modifier.size(24.dp),
                    color = MaterialTheme.colorScheme.onPrimary,
                    strokeWidth = 2.5.dp
                )
                Spacer(Modifier.width(12.dp))
                Text("Memproses...")
            } else {
                Text(
                    text = "Simpan Data Pelanggan",
                    style = MaterialTheme.typography.titleMedium,
                    fontWeight = FontWeight.Bold
                )
            }
        }

        Spacer(modifier = Modifier.height(24.dp))
    }
}

@Composable
fun FormSection(
    title: String,
    icon: ImageVector,
    content: @Composable ColumnScope.() -> Unit
) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp),
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
        shape = MaterialTheme.shapes.medium
    ) {
        Column(
            modifier = Modifier.padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            Row(verticalAlignment = Alignment.CenterVertically) {
                Icon(
                    imageVector = icon,
                    contentDescription = null,
                    tint = MaterialTheme.colorScheme.primary,
                    modifier = Modifier.size(20.dp)
                )
                Spacer(Modifier.width(8.dp))
                Text(
                    text = title,
                    style = MaterialTheme.typography.titleSmall,
                    fontWeight = FontWeight.Bold,
                    color = MaterialTheme.colorScheme.primary
                )
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
        leadingIcon = {
            Icon(
                imageVector = icon,
                contentDescription = null,
                tint = MaterialTheme.colorScheme.primary
            )
        },
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