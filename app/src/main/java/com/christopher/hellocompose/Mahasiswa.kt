package com.christopher.hellocompose

data class Mahasiswa(
    val nim: String,
    val nama: String,
    val ipk: Double
)

sealed class HasilFilter {
    data class Success(val data: List<Mahasiswa>) : HasilFilter()
    data class Error(val pesan: String) : HasilFilter()
}

val daftarMahasiswa: List<Mahasiswa> = listOf(
    Mahasiswa("01082240011", "Christopher M. M. Gijoh", 3.85),
    Mahasiswa("01082240012", "Andi Saputra", 3.20),
    Mahasiswa("01082240013", "Budi Santoso", 3.72),
    Mahasiswa("01082240014", "Citra Lestari", 3.95),
    Mahasiswa("01082240015", "Dewi Anggraini", 3.48)
)

fun filterMahasiswa(data: List<Mahasiswa>): HasilFilter {
    return try {
        val hasil = data
            .filter { it.ipk >= 3.5 }
            .sortedBy { it.nama }
        if (hasil.isEmpty()) {
            HasilFilter.Error("Tidak ada mahasiswa dengan IPK >= 3.5")
        } else {
            HasilFilter.Success(hasil)
        }
    } catch (e: Exception) {
        HasilFilter.Error(e.message ?: "Terjadi kesalahan saat memfilter data")
    }
}
