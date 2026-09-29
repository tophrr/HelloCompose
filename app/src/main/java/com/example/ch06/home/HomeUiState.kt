package com.example.ch06.home

import com.example.ch06.data.Article

// Single source of truth untuk layar Home
data class HomeUiState(
    val isLoading: Boolean = false,
    val articles: List<Article> = emptyList(),
    val query: String = "",
    val errorMessage: String? = null
) {
    // TODO [T3.1] Tambahkan properti turunan `isEmptyResult: Boolean` (getter, BUKAN field
    //   baru): true hanya jika TIDAK loading, TIDAK error, dan `articles` kosong.
    //   Jangan membuat boolean baru yang harus disinkronkan manual.
}
