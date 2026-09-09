package com.example.ch03

import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.tooling.preview.Preview
import com.example.ch03.ui.theme.Ch03Theme

data class Mahasiswa(val nama: String, val nim: String, val ipk: Double)

// TODO 1: Ganti daftar kosong dengan minimal 10 data mahasiswa dari README.
val dummyMahasiswa = emptyList<Mahasiswa>()

@Composable
fun StudentListScreen() {
    // TODO 2: Panggil DaftarMahasiswa dengan dummyMahasiswa.
    Text("Lengkapi StudentListScreen.kt sesuai README")
}

@Composable
fun DaftarMahasiswa(mahasiswaList: List<Mahasiswa>) {
    // TODO 3: Buat LazyColumn dan tampilkan setiap item dengan MahasiswaCard.
    Text("Jumlah mahasiswa: ${mahasiswaList.size}")
}

@Composable
fun MahasiswaCard(mahasiswa: Mahasiswa) {
    // TODO 4: Buat Card berisi nama, NIM, dan IPK.
    Text("${mahasiswa.nama} - ${mahasiswa.nim} - ${mahasiswa.ipk}")
}

@Preview(showBackground = true, showSystemUi = true)
@Composable
fun StudentListScreenPreview() {
    Ch03Theme {
        StudentListScreen()
    }
}
