package com.example.ch07.ui.list

import com.example.ch07.data.NoteEntity
import com.example.ch07.testutil.FakeNoteRepository
import com.example.ch07.testutil.MainDispatcherRule
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.launch
import kotlinx.coroutines.test.TestScope
import kotlinx.coroutines.test.advanceTimeBy
import kotlinx.coroutines.test.runCurrent
import kotlinx.coroutines.test.runTest
import org.junit.Assert.assertEquals
import org.junit.Assert.assertFalse
import org.junit.Assert.assertNull
import org.junit.Assert.assertTrue
import org.junit.Rule
import org.junit.Test

// Tes perilaku DASAR (sudah hijau sejak awal). Pakai argumen bernama saja,
// supaya tetap valid setelah kamu menambah kolom baru di NoteEntity.
// Saat mengerjakan Tugas 2 dan 4, tambahkan tes serupa untuk undo hapus dan
// filter tag (contohnya ada di praktikum/ch07).
@OptIn(ExperimentalCoroutinesApi::class)
class NoteListViewModelTest {

    @get:Rule val mainRule = MainDispatcherRule()

    private fun note(id: Int, title: String, content: String = "", pinned: Boolean = false,
                     tags: List<String> = emptyList()) =
        NoteEntity(id = id, title = title, content = content, createdAt = id.toLong(),
            isPinned = pinned, tags = tags)

    // uiState memakai stateIn(WhileSubscribed): harus ada yang mengumpulkan
    private fun TestScope.start(vm: NoteListViewModel) {
        backgroundScope.launch { vm.uiState.collect { } }
        runCurrent()
    }

    @Test fun `catatan yang disematkan tampil paling atas`() = runTest {
        val vm = NoteListViewModel(FakeNoteRepository(listOf(note(1, "A"), note(2, "B", pinned = true))))
        start(vm)

        assertEquals(listOf("B", "A"), vm.uiState.value.notes.map { it.title })
        assertFalse(vm.uiState.value.isLoading)
    }

    @Test fun `pencarian ditunda 300 ms lalu memfilter judul atau isi`() = runTest {
        val vm = NoteListViewModel(FakeNoteRepository(listOf(
            note(1, "Belanja", "susu"), note(2, "Kuliah", "bab Room"), note(3, "Ide", "aplikasi")
        )))
        start(vm)

        vm.onQueryChange("room")
        runCurrent()
        assertEquals("teks langsung tampil di TextField", "room", vm.uiState.value.query)
        advanceTimeBy(299); runCurrent()
        assertEquals("belum difilter sebelum 300 ms", 3, vm.uiState.value.notes.size)

        advanceTimeBy(2); runCurrent()
        assertEquals(listOf("Kuliah"), vm.uiState.value.notes.map { it.title })
        assertTrue(vm.uiState.value.isFiltering)

        vm.onQueryChange("")           // kosong = tanpa jeda, semua tampil lagi
        runCurrent()
        assertEquals(3, vm.uiState.value.notes.size)
    }

    @Test fun `hapus menghilangkan catatan dari daftar`() = runTest {
        val target = note(1, "A")
        val repo = FakeNoteRepository(listOf(target, note(2, "B")))
        val vm = NoteListViewModel(repo)
        start(vm)

        vm.deleteNote(target); runCurrent()

        assertEquals(listOf("B"), vm.uiState.value.notes.map { it.title })
    }

    @Test fun `togglePin menyematkan lalu melepas`() = runTest {
        val n = note(1, "A")
        val repo = FakeNoteRepository(listOf(n))
        val vm = NoteListViewModel(repo)
        start(vm)

        vm.togglePin(n); runCurrent()
        assertTrue(repo.current.single().isPinned)

        vm.togglePin(repo.current.single()); runCurrent()
        assertFalse(repo.current.single().isPinned)
    }

    @Test fun `hapus lalu undo mengembalikan catatan yang sama termasuk id dan pin`() = runTest {
        val pinned = note(2, "B", "isi B", pinned = true)
        val repo = FakeNoteRepository(listOf(note(1, "A"), pinned))
        val vm = NoteListViewModel(repo)
        start(vm)

        vm.deleteNote(pinned); runCurrent()
        assertEquals(listOf("A"), vm.uiState.value.notes.map { it.title })
        assertEquals(pinned, vm.uiState.value.recentlyDeleted)

        vm.undoDelete(); runCurrent()

        assertEquals(pinned, repo.current.first { it.id == 2 })
        assertEquals(listOf("B", "A"), vm.uiState.value.notes.map { it.title })
        assertNull(vm.uiState.value.recentlyDeleted)
    }

    @Test fun `snackbar hilang tanpa undo membuat catatan tetap terhapus`() = runTest {
        val target = note(1, "A")
        val repo = FakeNoteRepository(listOf(target, note(2, "B")))
        val vm = NoteListViewModel(repo)
        start(vm)

        vm.deleteNote(target); runCurrent()
        vm.onUndoDismissed(); runCurrent()

        assertNull(vm.uiState.value.recentlyDeleted)
        assertTrue(repo.current.none { it.id == 1 })
        assertEquals(listOf("B"), vm.uiState.value.notes.map { it.title })
    }

    @Test fun `menyematkan tidak mengubah updatedAt`() = runTest {
        val n = NoteEntity(id = 1, title = "A", content = "", createdAt = 10L, updatedAt = 10L)
        val repo = FakeNoteRepository(listOf(n))
        val vm = NoteListViewModel(repo)
        start(vm)

        vm.togglePin(n); runCurrent()

        assertTrue(repo.current.single().isPinned)
        assertEquals(10L, repo.current.single().updatedAt)
    }

    @Test fun `availableTags mengumpulkan dan mengurutkan tag unik`() = runTest {
        val repo = FakeNoteRepository(listOf(
            note(1, "A", tags = listOf("kuliah", "ide")),
            note(2, "B", tags = listOf("ide", "belanja"))
        ))
        val vm = NoteListViewModel(repo)
        start(vm)

        assertEquals(listOf("belanja", "ide", "kuliah"), vm.uiState.value.availableTags)
    }

    @Test fun `ketuk tag memfilter dan ketuk lagi melepas`() = runTest {
        val repo = FakeNoteRepository(listOf(
            note(1, "A", tags = listOf("kuliah")),
            note(2, "B", tags = listOf("ide"))
        ))
        val vm = NoteListViewModel(repo)
        start(vm)

        vm.onTagSelected("kuliah"); runCurrent()
        assertEquals(listOf("A"), vm.uiState.value.notes.map { it.title })
        assertTrue(vm.uiState.value.isFiltering)

        vm.onTagSelected("kuliah"); runCurrent()
        assertEquals(2, vm.uiState.value.notes.size)
        assertFalse(vm.uiState.value.isFiltering)
    }

    @Test fun `filter tag bekerja bersama pencarian`() = runTest {
        val repo = FakeNoteRepository(listOf(
            note(1, "Kuliah Room", tags = listOf("kuliah")),
            note(2, "Kuliah Fisika", tags = listOf("fisika")),
            note(3, "Belanja", tags = listOf("belanja"))
        ))
        val vm = NoteListViewModel(repo)
        start(vm)

        vm.onQueryChange("kuliah")
        runCurrent()
        advanceTimeBy(301); runCurrent()
        assertEquals(2, vm.uiState.value.notes.size)

        vm.onTagSelected("kuliah"); runCurrent()
        assertEquals(listOf("Kuliah Room"), vm.uiState.value.notes.map { it.title })
    }

    @Test fun `tag yang tak dipakai lagi otomatis dilepas`() = runTest {
        val only = note(1, "A", tags = listOf("kuliah"))
        val repo = FakeNoteRepository(listOf(only, note(2, "B")))
        val vm = NoteListViewModel(repo)
        start(vm)

        vm.onTagSelected("kuliah"); runCurrent()
        assertEquals(listOf("A"), vm.uiState.value.notes.map { it.title })

        vm.deleteNote(only); runCurrent()

        assertNull(vm.uiState.value.selectedTag)
        assertEquals(listOf("B"), vm.uiState.value.notes.map { it.title })
    }
}
