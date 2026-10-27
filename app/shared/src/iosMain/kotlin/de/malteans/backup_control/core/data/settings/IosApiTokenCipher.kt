package de.malteans.backup_control.core.data.settings

import kotlinx.cinterop.*
import platform.CoreFoundation.*
import platform.Foundation.*
import platform.Security.*
import platform.darwin.OSStatus

/**
 * Stores the API token directly in the iOS Keychain as a generic password
 * (the platform secure storage). The value passed to [encrypt] is stored and an
 * opaque marker is returned so the repository can uniformly persist "something is stored".
 */
@OptIn(ExperimentalForeignApi::class, UnsafeNumber::class)
class IosApiTokenCipher : ApiTokenCipher {
    companion object {
        private const val SERVICE = "de.malteans.backup_control"
        private const val ACCOUNT = "api_token"
        private const val KEYCHAIN_MARKER = "keychain:api_token"
        private const val NO_ERROR: OSStatus = 0
    }

    private val defaultProperties: Map<CFStringRef?, CFTypeRef?> = mapOf(
        kSecClass to kSecClassGenericPassword,
        kSecAttrService to CFBridgingRetain(SERVICE.toNSString()),
        kSecAttrAccount to CFBridgingRetain(ACCOUNT.toNSString()),
    )

    override fun encrypt(plainText: String): String {
        if (plainText.isBlank()) return ""
        val value = plainText.toNSString().dataUsingEncoding(NSUTF8StringEncoding)

        val cfValue = CFBridgingRetain(value)
        val status = keychainOperation(
            kSecValueData to cfValue,
        ) { query ->
            SecItemDelete(query)
            SecItemAdd(query, null)
        }
        CFBridgingRelease(cfValue)

        check(status == NO_ERROR) { "Could not store API token in Keychain ($status)" }
        return KEYCHAIN_MARKER
    }

    override fun decrypt(cipherText: String): String {
        if (cipherText.isBlank()) return ""
        if (cipherText != KEYCHAIN_MARKER) error("Unknown encrypted token format")

        // Points to a single CFTypeRef-sized slot in which SecItemCopyMatching writes the result.
        val resultSlot = LongArray(1)
        resultSlot.usePinned { pinned ->
            val status = keychainOperation(
                kSecReturnData to kCFBooleanTrue,
                kSecMatchLimit to kSecMatchLimitOne,
            ) { query ->
                SecItemCopyMatching(query, pinned.addressOf(0).reinterpret())
            }
            if (status != NO_ERROR) error("API token not found in Keychain ($status)")
        }

        val data = CFBridgingRelease(resultSlot[0].toCPointer()) as? NSData
        return data?.let { NSString.create(it, NSUTF8StringEncoding)?.toKString() } ?: ""
    }

    private inline fun keychainOperation(
        vararg input: Pair<CFStringRef?, CFTypeRef?>,
        crossinline operation: (query: CFDictionaryRef?) -> OSStatus,
    ): OSStatus = memScoped {
        val map = defaultProperties + mapOf(*input)
        val keys = allocArrayOf(*map.keys.toTypedArray())
        val values = allocArrayOf(*map.values.toTypedArray())
        val query = CFDictionaryCreate(
            kCFAllocatorDefault,
            keys.reinterpret(),
            values.reinterpret(),
            map.size.convert(),
            null,
            null,
        )
        operation(query)
    }
}

// Toll-free bridged casts, see https://github.com/russhwolf/multiplatform-settings KeychainSettings
@Suppress("CAST_NEVER_SUCCEEDS")
private fun String.toNSString(): NSString = this as NSString

@Suppress("CAST_NEVER_SUCCEEDS")
private fun NSString.toKString(): String = this as String
