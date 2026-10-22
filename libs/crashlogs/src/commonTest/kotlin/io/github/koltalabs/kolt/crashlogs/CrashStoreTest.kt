package io.github.koltalabs.kolt.crashlogs

import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertNotNull
import kotlin.test.assertNull
import kotlin.test.assertTrue

private class MemoryStorage : CrashStorage {
    val files = mutableMapOf<String, String>()

    override fun read(name: String) = files[name]

    override fun write(name: String, text: String) {
        files[name] = text
    }

    override fun delete(name: String) {
        files.remove(name)
    }
}

class CrashStoreTest {
    private val storage = MemoryStorage()

    @Test
    fun recordsAndReadsBackWithSpecialCharacters() {
        val store = CrashStore(storage, maxEntries = 10)
        val summary = assertNotNull(store.record("t\tag", IllegalStateException("a\tb\nc\\d")))

        val entry = assertNotNull(store.get(summary.id))
        assertEquals("t\tag", entry.summary.tag)
        assertEquals("a\tb\nc\\d", entry.summary.message)
        assertTrue(entry.stackTrace.contains("IllegalStateException"))
        assertEquals(listOf(summary), store.all())
    }

    @Test
    fun nullMessageRoundTrips() {
        val store = CrashStore(storage, maxEntries = 10)
        val summary = assertNotNull(store.record("x", RuntimeException()))
        assertNull(store.all().single().message)
        assertEquals(summary.id, store.all().single().id)
    }

    @Test
    fun evictsOldestPastMax() {
        val store = CrashStore(storage, maxEntries = 2)
        val first = assertNotNull(store.record("a", RuntimeException("1")))
        store.record("b", RuntimeException("2"))
        store.record("c", RuntimeException("3"))

        assertEquals(listOf("c", "b"), store.all().map { it.tag })
        assertNull(store.get(first.id))
        assertNull(storage.files["${first.id}.txt"])
    }

    @Test
    fun searchMatchesTagClassAndMessage() {
        val store = CrashStore(storage, maxEntries = 10)
        store.record("network", IllegalStateException("timeout"))
        store.record("db", RuntimeException("locked"))

        assertEquals(listOf("network"), store.search("TIMEOUT").map { it.tag })
        assertEquals(listOf("db"), store.search("runtime").map { it.tag })
        assertEquals(2, store.search("").size)
    }

    @Test
    fun clearRemovesEverything() {
        val store = CrashStore(storage, maxEntries = 10)
        store.record("a", RuntimeException())
        store.clear()

        assertTrue(store.all().isEmpty())
        assertEquals(setOf("index.txt"), storage.files.keys)
    }
}
