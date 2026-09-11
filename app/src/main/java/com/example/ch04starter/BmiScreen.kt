package com.example.ch04starter

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.Button
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Slider
import androidx.compose.material3.SnackbarDuration
import androidx.compose.material3.SnackbarHostState
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.derivedStateOf
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableFloatStateOf
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.example.ch04starter.ui.theme.Ch04StarterTheme

// BmiScreen untuk kalkulator BMI, tabel interpretasi, tombol Reset, dan Snackbar
// kategori yang muncul saat tombol hitung ditekan.

@Composable
fun BmiScreen(snackbarHostState: SnackbarHostState) {
    // rememberSaveable: bertahan saat rotasi layar
    var beratKg     by rememberSaveable { mutableFloatStateOf(60f) }
    var tinggiCm    by rememberSaveable { mutableFloatStateOf(165f) }
    var isDihitung  by rememberSaveable { mutableStateOf(false) }
    var hitungCount by rememberSaveable { mutableIntStateOf(0) }

    // derivedStateOf: hanya recompose saat nilai BMI benar-benar berubah
    val bmi by remember {
        derivedStateOf {
            val tinggiM = tinggiCm / 100f
            beratKg / (tinggiM * tinggiM)
        }
    }

    val (kategori, warna) = remember(bmi) {
        when {
            bmi < 18.5f -> "Berat Badan Kurang" to Color(0xFF1565C0)
            bmi < 25.0f -> "Berat Badan Normal" to Color(0xFF2E7D32)
            bmi < 30.0f -> "Kelebihan Berat"    to Color(0xFFE65100)
            else        -> "Obesitas"            to Color(0xFFC62828)
        }
    }

    // Snackbar dipicu lewat LaunchedEffect yang mengamati `hitungCount`, bukan
    // memanggil showSnackbar() langsung di dalam onClick.
    LaunchedEffect(hitungCount) {
        if (hitungCount > 0) {
            snackbarHostState.showSnackbar(
                message  = kategori,
                duration = SnackbarDuration.Short
            )
        }
    }

    Column(
        modifier            = Modifier
            .fillMaxSize()
            .verticalScroll(rememberScrollState())
            .padding(24.dp),
        verticalArrangement = Arrangement.spacedBy(20.dp)
    ) {
        Text(
            text  = "Kalkulator BMI",
            style = MaterialTheme.typography.headlineMedium
        )

        InputSlider(
            label         = "Berat Badan",
            value         = beratKg,
            unit          = "kg",
            range         = 30f..150f,
            onValueChange = { beratKg = it; isDihitung = false }
        )

        InputSlider(
            label         = "Tinggi Badan",
            value         = tinggiCm,
            unit          = "cm",
            range         = 100f..220f,
            onValueChange = { tinggiCm = it; isDihitung = false }
        )

        Row(horizontalArrangement = Arrangement.spacedBy(12.dp)) {
            Button(
                onClick  = {
                    isDihitung = true
                    hitungCount++
                },
                modifier = Modifier.weight(1f)
            ) {
                Text("Hitung BMI")
            }

            Button(
                onClick  = {
                    beratKg    = 60f
                    tinggiCm   = 165f
                    isDihitung = false
                },
                modifier = Modifier.weight(1f)
            ) {
                Text("Reset")
            }
        }

        // AnimatedVisibility: fade-in saat isDihitung = true
        AnimatedVisibility(visible = isDihitung) {
            Column(verticalArrangement = Arrangement.spacedBy(16.dp)) {
                Card(
                    modifier = Modifier.fillMaxWidth(),
                    colors   = CardDefaults.cardColors(
                        containerColor = warna.copy(alpha = 0.1f)
                    )
                ) {
                    Column(
                        modifier            = Modifier.padding(24.dp),
                        horizontalAlignment = Alignment.CenterHorizontally
                    ) {
                        Text(
                            text  = "${"%.1f".format(bmi)}",
                            style = MaterialTheme.typography.displayMedium,
                            color = warna
                        )
                        Text(
                            text  = kategori,
                            style = MaterialTheme.typography.titleMedium,
                            color = warna
                        )
                        Spacer(Modifier.height(8.dp))
                        Text(
                            text  = "Berat: ${"%.0f".format(beratKg)} kg  " +
                                    "Tinggi: ${"%.0f".format(tinggiCm)} cm",
                            style = MaterialTheme.typography.bodySmall,
                            color = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                    }
                }

                InterpretasiBmiTable()
            }
        }
    }
}

@Composable
fun InputSlider(
    label:         String,
    value:         Float,
    unit:          String,
    range:         ClosedFloatingPointRange<Float>,
    onValueChange: (Float) -> Unit
) {
    Column {
        Row(
            modifier              = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            Text(label, style = MaterialTheme.typography.bodyLarge)
            Text(
                text       = "${"%.0f".format(value)} $unit",
                style      = MaterialTheme.typography.bodyLarge,
                color      = MaterialTheme.colorScheme.primary,
                fontWeight = FontWeight.Bold
            )
        }
        Slider(
            value         = value,
            onValueChange = onValueChange,
            valueRange    = range,
            modifier      = Modifier.fillMaxWidth()
        )
    }
}

@Composable
fun InterpretasiBmiTable() {
    val baris = listOf(
        "Kurus"    to "< 18.5",
        "Normal"   to "18.5 – 24.9",
        "Gemuk"    to "25.0 – 29.9",
        "Obesitas" to "≥ 30.0"
    )
    Card(modifier = Modifier.fillMaxWidth()) {
        Column(
            modifier            = Modifier.padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            Text(
                text       = "Interpretasi BMI",
                style      = MaterialTheme.typography.titleMedium,
                fontWeight = FontWeight.Bold
            )
            baris.forEach { (nama, rentang) ->
                Row(
                    modifier              = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {
                    Text(nama, style = MaterialTheme.typography.bodyLarge)
                    Text(
                        text  = rentang,
                        style = MaterialTheme.typography.bodyLarge,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                }
            }
        }
    }
}

@Preview(showBackground = true, showSystemUi = true)
@Composable
fun BmiScreenPreview() {
    Ch04StarterTheme {
        BmiScreen(remember { SnackbarHostState() })
    }
}
