package com.example.ch06.home

import com.example.ch06.data.Article

// Single source of truth untuk layar Home
data class HomeUiState(
    val isLoading: Boolean = false,
    val articles: List<Article> = emptyList(),
    // TODO [T2.1] Tambahkan field `query: String = ""` di sini. Teks pencarian yang
    //   sedang diketik hidup di UiState, bukan di composable.
    val errorMessage: String? = null
) {
    // TODO [T3.1] Tambahkan properti turunan `isEmptyResult: Boolean` (getter, BUKAN field
    //   baru): true hanya jika TIDAK loading, TIDAK error, dan `articles` kosong.
    //   Jangan membuat boolean baru yang harus disinkronkan manual.
}
