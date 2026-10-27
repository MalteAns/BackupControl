package de.malteans.backup_control.core.data.settings

/**
 * Encrypts and decrypts the API token.
 *
 * Implementations are platform specific:
 * - Android: AES-GCM with a key from the Android Keystore, ciphertext is stored in the DataStore.
 * - Desktop: AES-GCM with a key from a user-only key file, ciphertext is stored in the DataStore.
 * - iOS: the token is stored directly in the iOS Keychain; the DataStore only keeps a marker value.
 */
interface ApiTokenCipher {
    /** Returns the encrypted value (base64) for the given plain text. Blank input yields blank output. */
    fun encrypt(plainText: String): String

    /** Returns the plain text for the given encrypted value (base64). Blank input yields blank output. */
    fun decrypt(cipherText: String): String
}
