package com.example.ch07.ui.list

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.ch07.data.NoteEntity
import com.example.ch07.data.NoteRepository
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.FlowPreview
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.debounce
import kotlinx.coroutines.flow.distinctUntilChanged
import kotlinx.coroutines.flow.flatMapLatest
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch

data class NoteListUiState(
    val isLoading: Boolean = false,
    val notes: List<NoteEntity> = emptyList(),
    val query: String = "",
    // Catatan yang baru dihapus, ditahan untuk Snackbar "Batalkan"
    val recentlyDeleted: NoteEntity? = null,
    // Semua tag yang dipakai catatan (untuk baris filter chip)
    val availableTags: List<String> = emptyList(),
    // Tag yang sedang dipilih, null = "Semua"
    val selectedTag: String? = null,
    val errorMessage: String? = null
) {
    // Turunan: sedang mencari/memfilter atau tidak (menentukan pesan kosong).
    val isFiltering: Boolean
        get() = query.isNotBlank() || selectedTag != null
}

@OptIn(FlowPreview::class, ExperimentalCoroutinesApi::class)
class NoteListViewModel(
    private val repository: NoteRepository
) : ViewModel() {

    // Teks yang sedang diketik (langsung tampil di TextField)
    private val queryText = MutableStateFlow("")
    private val loadError = MutableStateFlow<String?>(null)
    // Catatan yang baru dihapus (untuk "Batalkan"), null = tidak ada.
    private val recentlyDeleted = MutableStateFlow<NoteEntity?>(null)
    // Tag yang sedang difilter, null = "Semua".
    private val selectedTag = MutableStateFlow<String?>(null)

    init {
        // Tag yang tidak lagi dipakai catatan mana pun otomatis dilepas,
        // supaya chip yang dipilih tidak menggantung.
        viewModelScope.launch {
            repository.getAllNotes().collect { notes ->
                val tags = notes.flatMap { it.tags }.toSet()
                if (selectedTag.value !in tags) selectedTag.value = null
            }
        }
    }

    // Query ke database ditunda 300 ms setelah pengguna berhenti mengetik
    // (debounce), dan hanya query TERBARU yang diamati (flatMapLatest).
    // Query kosong tidak ditunda: daftar penuh tampil segera.
    private val searchResults: Flow<List<NoteEntity>> = queryText
        .debounce { query -> if (query.isBlank()) 0L else 300L }
        .distinctUntilChanged()
        .flatMapLatest { query ->
            if (query.isBlank()) repository.getAllNotes()
            else repository.searchNotes(query)
        }
        .catch { e ->
            loadError.value = e.message ?: "Gagal memuat catatan"
            emit(emptyList())
        }

    // Query dan tag digabung lebih dulu agar combine cukup 5 sumber
    private val queryAndTag: Flow<Pair<String, String?>> =
        combine(queryText, selectedTag) { query, tag -> query to tag }

    // UiState DITURUNKAN dari beberapa sumber (pola Chapter 6).
    // - daftar dari searchResults sudah difilter oleh query (lewat DAO);
    // - filter tag diterapkan di memori pada hasil itu;
    // - availableTags diambil dari SEMUA catatan, bukan hanya hasil pencarian.
    val uiState: StateFlow<NoteListUiState> = combine(
        searchResults, queryAndTag, loadError, recentlyDeleted, repository.getAllNotes()
    ) { results, (query, tag), error, recentlyDeleted, allNotes ->
        NoteListUiState(
            notes = if (tag == null) results else results.filter { tag in it.tags },
            query = query,
            recentlyDeleted = recentlyDeleted,
            availableTags = allNotes.flatMap { it.tags }.distinct().sorted(),
            selectedTag = tag,
            errorMessage = error
        )
    }.stateIn(
        scope = viewModelScope,
        started = SharingStarted.WhileSubscribed(5_000),
        initialValue = NoteListUiState(isLoading = true)
    )

    fun onQueryChange(newQuery: String) {
        queryText.value = newQuery
    }

    // Setelah menghapus, tahan catatan agar bisa dibatalkan lewat Snackbar.
    fun deleteNote(note: NoteEntity) {
        viewModelScope.launch {
            repository.deleteNote(note)
            recentlyDeleted.value = note
        }
    }

    // Masukkan kembali catatan yang sama (id sama -> REPLACE, sehingga id, pin,
    // dan tag ikut kembali) lalu bersihkan state undo
    fun undoDelete() {
        val note = recentlyDeleted.value ?: return
        recentlyDeleted.value = null
        viewModelScope.launch { repository.insertNote(note) }
    }

    // Snackbar hilang tanpa dibatalkan: catatan tetap terhapus
    fun onUndoDismissed() {
        recentlyDeleted.value = null
    }

    // Ketuk tag yang sedang aktif = lepas filter, null = "Semua"
    fun onTagSelected(tag: String?) {
        selectedTag.value = if (tag == selectedTag.value) null else tag
    }

    // Menyematkan tidak mengubah updatedAt (bukan perubahan isi catatan)
    fun togglePin(note: NoteEntity) {
        viewModelScope.launch {
            repository.updateNote(note.copy(isPinned = !note.isPinned))
        }
    }
}
