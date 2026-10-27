package de.malteans.backup_control.core.data.settings

import java.io.File
import java.util.*
import javax.crypto.Cipher
import javax.crypto.KeyGenerator
import javax.crypto.SecretKey
import javax.crypto.spec.GCMParameterSpec
import javax.crypto.spec.SecretKeySpec

/**
 * AES-256-GCM encryption with a key stored in a user-only key file:
 * `~/.backup-control/api-token.key`.
 * The returned ciphertext (base64) contains the iv prepended to the encrypted bytes.
 *
 * Trade-off: plain JVM has no OS keystore on all desktop platforms, so the key file
 * binds the ciphertext to the local user account only.
 */
class DesktopApiTokenCipher(
    keyDirectory: File = File(System.getProperty("user.home"), ".backup-control"),
) : ApiTokenCipher {
    companion object {
        private const val KEY_FILE_NAME = "api-token.key"
        private const val TRANSFORMATION = "AES/GCM/NoPadding"
        private const val IV_SIZE_BYTES = 12
        private const val GCM_TAG_BITS = 128
        private const val KEY_SIZE_BITS = 256
    }

    private val keyDir: File = keyDirectory
    private val keyFile: File = File(keyDir, KEY_FILE_NAME)

    private fun getOrCreateSecretKey(): SecretKey {
        if (keyFile.exists()) {
            val keyBytes = keyFile.readBytes()
            if (keyBytes.size == KEY_SIZE_BITS / 8) {
                return SecretKeySpec(keyBytes, "AES")
            }
        }

        keyDir.mkdirs()
        val keyGenerator = KeyGenerator.getInstance("AES")
        keyGenerator.init(KEY_SIZE_BITS)
        val keyBytes = keyGenerator.generateKey().encoded

        keyFile.apply {
            createNewFile()
            setReadable(false, false)
            setWritable(false, false)
            setExecutable(false, false)
            setReadable(true, true)
            setWritable(true, true)
            writeBytes(keyBytes)
        }
        return SecretKeySpec(keyBytes, "AES")
    }

    override fun encrypt(plainText: String): String {
        if (plainText.isBlank()) return ""
        val cipher = Cipher.getInstance(TRANSFORMATION)
        cipher.init(Cipher.ENCRYPT_MODE, getOrCreateSecretKey())
        val encrypted = cipher.iv + cipher.doFinal(plainText.toByteArray(Charsets.UTF_8))
        return Base64.getEncoder().encodeToString(encrypted)
    }

    override fun decrypt(cipherText: String): String {
        if (cipherText.isBlank()) return ""
        val data = Base64.getDecoder().decode(cipherText)
        require(data.size > IV_SIZE_BYTES) { "Encrypted token is too short" }
        val iv = data.copyOfRange(0, IV_SIZE_BYTES)
        val encrypted = data.copyOfRange(IV_SIZE_BYTES, data.size)
        val cipher = Cipher.getInstance(TRANSFORMATION)
        cipher.init(Cipher.DECRYPT_MODE, getOrCreateSecretKey(), GCMParameterSpec(GCM_TAG_BITS, iv))
        return String(cipher.doFinal(encrypted), Charsets.UTF_8)
    }
}
