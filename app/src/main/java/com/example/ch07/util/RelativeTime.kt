package com.example.ch07.util

// Fungsi murni (tanpa Android): selisih `now - then` (negatif dianggap 0)
// diubah menjadi teks relatif berbahasa Indonesia.
fun formatRelativeTime(now: Long, then: Long): String {
    val minute = 60_000L
    val hour = 60 * minute
    val day = 24 * hour
    val diff = (now - then).coerceAtLeast(0L)

    return when {
        diff < minute -> "baru saja"
        diff < hour -> "${diff / minute} menit lalu"
        diff < day -> "${diff / hour} jam lalu"
        diff < 30 * day -> "${diff / day} hari lalu"
        diff < 365 * day -> "${diff / (30 * day)} bulan lalu"
        else -> "${diff / (365 * day)} tahun lalu"
    }
}
