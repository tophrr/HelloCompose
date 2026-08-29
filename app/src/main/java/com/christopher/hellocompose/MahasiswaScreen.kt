package com.christopher.hellocompose

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.Card
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp

@Composable
fun MahasiswaScreen(modifier: Modifier = Modifier) {
    val hasil = remember { filterMahasiswa(daftarMahasiswa) }

    Column(
        modifier = modifier
            .fillMaxSize()
            .padding(24.dp),
        verticalArrangement = Arrangement.spacedBy(12.dp)
    ) {
        Text(
            text = "Mahasiswa dengan IPK >= 3.5",
            style = MaterialTheme.typography.headlineSmall
        )

        when (hasil) {
            is HasilFilter.Success -> {
                Text(
                    text = "Ditemukan ${hasil.data.size} mahasiswa (diurutkan berdasarkan nama)",
                    style = MaterialTheme.typography.bodyMedium,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
                LazyColumn(
                    modifier = Modifier.fillMaxSize(),
                    verticalArrangement = Arrangement.spacedBy(8.dp),
                    contentPadding = PaddingValues(bottom = 16.dp)
                ) {
                    items(hasil.data) { mahasiswa ->
                        MahasiswaCard(mahasiswa)
                    }
                }
            }

            is HasilFilter.Error -> {
                Text(
                    text = hasil.pesan,
                    style = MaterialTheme.typography.bodyLarge,
                    color = MaterialTheme.colorScheme.error
                )
            }
        }
    }
}

@Composable
private fun MahasiswaCard(mahasiswa: Mahasiswa) {
    Card(modifier = Modifier.fillMaxWidth()) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(4.dp)
        ) {
            Text(
                text = mahasiswa.nama,
                style = MaterialTheme.typography.titleMedium
            )
            Text(
                text = "NIM: ${mahasiswa.nim}",
                style = MaterialTheme.typography.bodyMedium
            )
            Text(
                text = "IPK: ${mahasiswa.ipk}",
                style = MaterialTheme.typography.bodyMedium,
                color = MaterialTheme.colorScheme.primary
            )
        }
    }
}
