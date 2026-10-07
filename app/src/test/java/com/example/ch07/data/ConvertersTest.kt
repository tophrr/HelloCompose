package com.example.ch07.data

import org.junit.Assert.assertEquals
import org.junit.Test

// Converter murni: List<String> <-> JSON. Tidak butuh Android/Room.
class ConvertersTest {
    private val converters = Converters()

    @Test fun `daftar tag bolak-balik lewat JSON`() =
        assertEquals(listOf("kuliah", "ide"), converters.toTags(converters.fromTags(listOf("kuliah", "ide"))))

    @Test fun `daftar kosong bolak-balik lewat JSON`() {
        assertEquals("[]", converters.fromTags(emptyList()))
        assertEquals(emptyList<String>(), converters.toTags("[]"))
    }
}
