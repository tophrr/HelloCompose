package com.example.ch03

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.example.ch03.ui.theme.Ch03Theme
import android.content.res.Configuration

data class Mahasiswa(val nama: String, val nim: String, val ipk: Double)

val dummyMahasiswa = listOf(
    Mahasiswa("Maurice White", "01082240001", 3.85),
    Mahasiswa("Philip Bailey", "01082240002", 3.40),
    Mahasiswa("Verdine White", "01082240003", 3.92),
    Mahasiswa("Ralph Johnson", "01082240004", 2.95),
    Mahasiswa("Larry Dunn", "01082240005", 3.75),
    Mahasiswa("Al McKay", "01082240006", 3.50),
    Mahasiswa("Fred White", "01082240007", 3.88),
    Mahasiswa("Johnny Graham", "01082240008", 2.80),
    Mahasiswa("Andrew Woolfolk", "01082240009", 3.65),
    Mahasiswa("Sonny Emory", "01082240010", 3.20),
    Mahasiswa("Sheldon Reynolds", "01082240011", 3.55),
    Mahasiswa("Don Myrick", "01082240012", 3.70),
    Mahasiswa("Louis Satterfield", "01082240013", 3.05),
    Mahasiswa("Rahmlee Michael Davis", "01082240014", 3.60),
    Mahasiswa("Michael Harris", "01082240015", 3.30),
    Mahasiswa("Gary Bias", "01082240016", 3.80),
    Mahasiswa("Vance Taylor", "01082240017", 2.90),
    Mahasiswa("Morris Pleasure", "01082240018", 3.45),
    Mahasiswa("Myron McKinnon", "01082240019", 3.15),
    Mahasiswa("Rinaldo Stewart", "01082240020", 3.25)
)

@Composable
fun StudentListScreen() {
    DaftarMahasiswa(mahasiswaList = dummyMahasiswa)
}

@Composable
fun DaftarMahasiswa(
    mahasiswaList: List<Mahasiswa>,
    modifier: Modifier = Modifier
) {
    LazyColumn(
        modifier = modifier.fillMaxSize(),
        contentPadding = PaddingValues(16.dp),
        verticalArrangement = Arrangement.spacedBy(8.dp)
    ) {
        item {
            Column(modifier = Modifier.padding(bottom = 8.dp)) {
                Text(
                    text = "Daftar Mahasiswa",
                    style = MaterialTheme.typography.titleLarge
                )
                Text(
                    text = "Jurusan Informatika",
                    style = MaterialTheme.typography.bodySmall,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
            }
        }
        items(
            items = mahasiswaList,
            key = { it.nim }
        ) { mahasiswa ->
            MahasiswaCard(mahasiswa)
        }
        item {
            Text(
                text = "Total ${mahasiswaList.size} mahasiswa",
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(vertical = 12.dp),
                style = MaterialTheme.typography.bodySmall,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )
        }
    }
}

@Composable
fun MahasiswaCard(mahasiswa: Mahasiswa) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
    ) {
        Row(
            modifier = Modifier.padding(16.dp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            Column {
                Text(
                    text = mahasiswa.nama,
                    style = MaterialTheme.typography.titleMedium
                )
                Text(
                    text = mahasiswa.nim,
                    style = MaterialTheme.typography.bodySmall,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
            }
            Text(
                text = "IPK ${mahasiswa.ipk}",
                style = MaterialTheme.typography.labelLarge,
                color = if (mahasiswa.ipk >= 3.5) {
                    MaterialTheme.colorScheme.primary
                } else {
                    MaterialTheme.colorScheme.onSurface
                }
            )
        }
    }
}

@Preview(showBackground = true, showSystemUi = true, uiMode = Configuration.UI_MODE_NIGHT_NO)
@Composable
fun StudentListScreenPreview() {
    Ch03Theme {
        StudentListScreen()
    }
}

@Preview(showBackground = true, showSystemUi = true, uiMode = Configuration.UI_MODE_NIGHT_YES)
@Composable
fun StudentListScreenPreviewDark() {
    Ch03Theme(darkTheme = true) {
        StudentListScreen()
    }
}