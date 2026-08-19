# GNS Billing Android — Native Rebuild

## Tujuan

Android dibangun ulang sebagai aplikasi native Kotlin + Jetpack Compose. Website Laravel GNS menjadi sumber kebenaran fitur dan alur bisnis.

Tidak menggunakan WebView dan tidak menjadikan tampilan Blade sebagai UI Android.

## Feature parity

### Authentication
- Login
- Logout
- Profile
- Update profile
- Password change/reset flows yang relevan untuk aplikasi

### Dashboard
- Total pelanggan
- Pelanggan aktif
- Pelanggan nonaktif
- Tagihan belum bayar
- Tagihan lunas
- Pendapatan hari ini
- Pendapatan bulan ini
- Total router
- Router aktif
- Grafik pendapatan
- Grafik status pelanggan
- Pembayaran terakhir
- Tagihan jatuh tempo
- Quick actions
- Informasi sistem dan waktu server

### Router / MikroTik
- Daftar router
- Tambah router
- Detail/edit router
- Hapus router
- Test koneksi
- PPP Secret
- Tambah secret
- Edit secret
- Hapus secret
- Enable/disable secret
- PPP Profile
- Tambah profile
- Edit profile
- Hapus profile
- Profile speed/limit information

### Paket
- Daftar paket
- Tambah paket
- Edit paket
- Hapus paket
- Pemilihan router/profile dan data terkait

### Pelanggan
- Daftar pelanggan
- Search/filter/status
- Tambah pelanggan
- Edit pelanggan
- Detail pelanggan
- Hapus pelanggan
- Sinkronisasi pelanggan
- Informasi paket/router/PPP
- Status pelanggan
- Operasi isolir/buka isolir/disconnect jika tersedia pada website/backend

### Tagihan
- Daftar semua tagihan
- Filter/status
- Detail tagihan
- Hapus tagihan
- Generate tagihan harian
- Tagihan jatuh tempo
- Riwayat/status pembayaran
- Perhitungan nominal, denda, dibayar, sisa, dan status harus mengikuti backend

### Pembayaran
- Daftar pembayaran
- Detail pembayaran
- Input pembayaran dari tagihan
- Pembayaran baru/express
- Riwayat pembayaran
- Filter/search/metode/status
- Invoice
- PDF/receipt yang dapat dibuka atau dibagikan dari Android

## Arsitektur

```text
Laravel GNS
   |
   | REST JSON API
   v
Android Native
   |
   +-- data/network
   +-- data/model
   +-- data/repository
   +-- domain/usecase
   +-- feature/auth
   +-- feature/dashboard
   +-- feature/router
   +-- feature/paket
   +-- feature/pelanggan
   +-- feature/tagihan
   +-- feature/pembayaran
   +-- feature/profile
   +-- navigation
   +-- ui/theme
```

## Prinsip

1. Tidak meng-copy Blade ke Android.
2. Tidak menggunakan WebView.
3. UI Android dibuat khusus untuk layar sentuh.
4. Business rules tetap berada di Laravel agar website dan Android menghasilkan data yang sama.
5. Semua operasi Android harus memiliki endpoint API yang jelas.
6. Setiap modul harus selesai UI + state + API + error/loading + validasi sebelum dianggap selesai.
7. Build Android harus diverifikasi setelah setiap kelompok modul besar.
