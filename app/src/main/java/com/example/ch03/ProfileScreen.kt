package com.example.ch03

import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.tooling.preview.Preview
import com.example.ch03.ui.theme.Ch03Theme

@Composable
fun ProfileScreen() {
    // TODO 1: Buat Column yang memenuhi layar dan dapat di-scroll.
    Text("Lengkapi ProfileScreen.kt sesuai README")
}

@Composable
fun StatItem(label: String, value: String) {
    // TODO 2: Tampilkan value dan label dalam Column.
    Text("$value - $label")
}

@Composable
fun InfoRow(text: String) {
    // TODO 3: Tampilkan ikon dan text dalam Row.
    Text(text)
}

@Preview(showBackground = true, showSystemUi = true)
@Composable
fun ProfileScreenPreview() {
    Ch03Theme {
        ProfileScreen()
    }
}
