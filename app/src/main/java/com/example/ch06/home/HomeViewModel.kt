package com.example.ch06.home

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.ch06.data.ArticleRepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

class HomeViewModel(
    private val repository: ArticleRepository
) : ViewModel() {

    private val _uiState = MutableStateFlow(HomeUiState(isLoading = true))
    val uiState: StateFlow<HomeUiState> = _uiState.asStateFlow()

    // TODO [T2.2] Simpan daftar LENGKAP hasil load di properti privat `allArticles`
    //   (BUKAN di HomeUiState: UI hanya boleh melihat hasil yang SUDAH difilter).
    //   Isi di refresh() saat sukses, lalu tampilkan hasil filter dengan query yang
    //   SAAT INI ada, karena query yang sudah diketik tidak boleh hilang saat
    //   refresh() dipanggil ulang.

    init {
        refresh()
    }

    fun refresh() {
        viewModelScope.launch {
            _uiState.update { it.copy(isLoading = true, errorMessage = null) }

            runCatching { repository.getArticles() }
                .onSuccess { articles ->
                    // TODO [T2.2] simpan `articles` ke allArticles + terapkan filter(query saat ini)
                    _uiState.update { it.copy(isLoading = false, articles = articles) }
                }
                .onFailure { error ->
                    _uiState.update {
                        it.copy(
                            isLoading = false,
                            errorMessage = error.message ?: "Gagal memuat artikel"
                        )
                    }
                }
        }
    }

    // TODO [T2.3] Buat `fun onQueryChange(newQuery: String)`: perbarui `query` dan
    //   `articles` di uiState. Tulis aturan filter sebagai fungsi privat
    //   `filter(source, query)`: artikel cocok jika JUDUL atau KATEGORI mengandung
    //   query, tidak peka huruf besar/kecil. Query kosong/blank = tampilkan semua.
}
