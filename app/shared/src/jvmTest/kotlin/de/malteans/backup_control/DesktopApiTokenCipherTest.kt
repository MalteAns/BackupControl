package de.malteans.backup_control

import de.malteans.backup_control.core.data.settings.ApiTokenCipher
import de.malteans.backup_control.core.data.settings.DesktopApiTokenCipher
import java.nio.file.Files
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertNotEquals
import kotlin.test.assertTrue

class DesktopApiTokenCipherTest {

    private fun newCipher(): ApiTokenCipher {
        val keyDir = Files.createTempDirectory("backup-control-test").toFile()
        return DesktopApiTokenCipher(keyDirectory = keyDir)
    }

    @Test
    fun encryptDecryptRoundTrip() {
        val cipher = newCipher()
        val token = "super-secret-api-token-1234"

        val encrypted = cipher.encrypt(token)

        assertNotEquals(token, encrypted)
        assertEquals(token, cipher.decrypt(encrypted))
    }

    @Test
    fun newKeyDirectoryCannotDecryptOldCiphertext() {
        val token = "super-secret-api-token-1234"
        val encrypted = newCipher().encrypt(token)

        val result = runCatching { newCipher().decrypt(encrypted) }

        assertTrue(result.isFailure)
    }

    @Test
    fun blankValuesPassThrough() {
        val cipher = newCipher()

        assertEquals("", cipher.encrypt(""))
        assertEquals("", cipher.encrypt("   "))
        assertEquals("", cipher.decrypt(""))
    }
}
