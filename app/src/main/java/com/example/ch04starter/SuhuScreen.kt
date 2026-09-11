package com.example.ch04starter

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.derivedStateOf
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.example.ch04starter.ui.theme.Ch04StarterTheme

// ============================================================================
// TODO Pertemuan 4 — SuhuScreen (soal #2 di Tugas Pertemuan 4)
//
// Kalkulator konversi suhu Celsius <-> Fahrenheit <-> Kelvin, memakai
// `derivedStateOf` — sama seperti pola `bmi` di BmiScreen, tapi di sini
// hasil derivasinya dipakai sebagai TAMPILAN saja (read-only), bukan input.
//
// Rumus yang perlu diimplementasikan:
//   F = C * 9/5 + 32
//   K = C + 273.15
//
// [ ] 2a. Field "Celsius" di bawah masih kosong logikanya — parse teks
//         input jadi Float (hati-hati input tidak valid / kosong!)
// [ ] 2b. Hitung `fahrenheit` dan `kelvin` dari `celsius` memakai
//         `derivedStateOf`, lalu tampilkan di kedua Text di bawah field
// [ ] (Tantangan, opsional) Jadikan ketiga field bisa DIKETIK bebas —
//         Fahrenheit atau Kelvin pun boleh jadi sumber input. Hati-hati:
//         kalau ketiganya saling mengisi satu sama lain secara langsung,
//         bisa terjadi UPDATE MELINGKAR (circular update). Coba pikirkan
//         cara melacak "field mana yang sedang aktif diketik" sebagai
//         satu-satunya sumber kebenaran (single source of truth, ingat
//         slide UDF), baru dua field lain murni derived dari situ.
// ============================================================================

enum class SuhuField { CELSIUS, FAHRENHEIT, KELVIN }

@Composable
fun SuhuScreen() {
    // Single source of truth: hanya field yang sedang aktif diketik yang
    // menyimpan teks mentah; dua field lain murni nilai turunan.
    var activeField by rememberSaveable { mutableStateOf(SuhuField.CELSIUS) }
    var activeText  by rememberSaveable { mutableStateOf("0") }

    // Derivasikan Celsius dari field yang aktif (parsing aman).
    val celsius by remember {
        derivedStateOf {
            val v = activeText.toFloatOrNull() ?: 0f
            when (activeField) {
                SuhuField.CELSIUS    -> v
                SuhuField.FAHRENHEIT -> (v - 32f) * 5f / 9f
                SuhuField.KELVIN     -> v - 273.15f
            }
        }
    }

    val fahrenheit by remember { derivedStateOf { celsius * 9f / 5f + 32f } }
    val kelvin by remember { derivedStateOf { celsius + 273.15f } }

    val celsiusDisplay    = if (activeField == SuhuField.CELSIUS) activeText else "%.1f".format(celsius)
    val fahrenheitDisplay = if (activeField == SuhuField.FAHRENHEIT) activeText else "%.1f".format(fahrenheit)
    val kelvinDisplay     = if (activeField == SuhuField.KELVIN) activeText else "%.1f".format(kelvin)

    Column(
        modifier            = Modifier
            .fillMaxSize()
            .padding(24.dp),
        verticalArrangement = Arrangement.spacedBy(20.dp)
    ) {
        Text(
            text  = "Konversi Suhu",
            style = MaterialTheme.typography.headlineMedium
        )

        OutlinedTextField(
            value         = celsiusDisplay,
            onValueChange = { activeField = SuhuField.CELSIUS; activeText = it },
            label         = { Text("Celsius (°C)") },
            keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
            modifier      = Modifier.fillMaxWidth()
        )

        OutlinedTextField(
            value         = fahrenheitDisplay,
            onValueChange = { activeField = SuhuField.FAHRENHEIT; activeText = it },
            label         = { Text("Fahrenheit (°F)") },
            keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
            modifier      = Modifier.fillMaxWidth()
        )

        OutlinedTextField(
            value         = kelvinDisplay,
            onValueChange = { activeField = SuhuField.KELVIN; activeText = it },
            label         = { Text("Kelvin (K)") },
            keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
            modifier      = Modifier.fillMaxWidth()
        )
    }
}

@Preview(showBackground = true, showSystemUi = true)
@Composable
fun SuhuScreenPreview() {
    Ch04StarterTheme { SuhuScreen() }
}
