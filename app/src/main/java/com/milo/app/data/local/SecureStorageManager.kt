package com.milo.app.data.local

import android.content.Context
import android.content.SharedPreferences
import android.security.keystore.KeyGenParameterSpec
import android.security.keystore.KeyProperties
import android.util.Base64
import com.milo.app.domain.models.UserAccount
import org.json.JSONObject
import java.security.KeyStore
import javax.crypto.Cipher
import javax.crypto.KeyGenerator
import javax.crypto.SecretKey
import javax.crypto.spec.GCMParameterSpec

class SecureStorageManager(context: Context) {

    private val prefs: SharedPreferences = context.getSharedPreferences("milo_secure_vault", Context.MODE_PRIVATE)
    private val keyAlias = "milo_auth_master_key"
    private val androidKeyStore = "AndroidKeyStore"
    private val transformation = "AES/GCM/NoPadding"

    init {
        ensureMasterKey()
    }

    private fun ensureMasterKey() {
        try {
            val keyStore = KeyStore.getInstance(androidKeyStore).apply { load(null) }
            if (!keyStore.containsAlias(keyAlias)) {
                val keyGenerator = KeyGenerator.getInstance(KeyProperties.KEY_ALGORITHM_AES, androidKeyStore)
                val spec = KeyGenParameterSpec.Builder(
                    keyAlias,
                    KeyProperties.PURPOSE_ENCRYPT or KeyProperties.PURPOSE_DECRYPT
                )
                    .setBlockModes(KeyProperties.BLOCK_MODE_GCM)
                    .setEncryptionPaddings(KeyProperties.ENCRYPTION_PADDING_NONE)
                    .setRandomizedEncryptionRequired(true)
                    .build()
                keyGenerator.init(spec)
                keyGenerator.generateKey()
            }
        } catch (e: Exception) {
            // Log fallback if needed
        }
    }

    private fun getSecretKey(): SecretKey? {
        return try {
            val keyStore = KeyStore.getInstance(androidKeyStore).apply { load(null) }
            val entry = keyStore.getEntry(keyAlias, null) as? KeyStore.SecretKeyEntry
            entry?.secretKey
        } catch (e: Exception) {
            null
        }
    }

    private fun encrypt(plainText: String): String {
        return try {
            val key = getSecretKey() ?: return Base64.encodeToString(plainText.toByteArray(), Base64.NO_WRAP)
            val cipher = Cipher.getInstance(transformation)
            cipher.init(Cipher.ENCRYPT_MODE, key)
            val iv = cipher.iv
            val cipherText = cipher.doFinal(plainText.toByteArray(Charsets.UTF_8))
            val combined = ByteArray(iv.size + cipherText.size)
            System.arraycopy(iv, 0, combined, 0, iv.size)
            System.arraycopy(cipherText, 0, combined, iv.size, cipherText.size)
            Base64.encodeToString(combined, Base64.NO_WRAP)
        } catch (e: Exception) {
            Base64.encodeToString(plainText.toByteArray(), Base64.NO_WRAP)
        }
    }

    private fun decrypt(encryptedBase64: String): String? {
        return try {
            val combined = Base64.decode(encryptedBase64, Base64.NO_WRAP)
            val key = getSecretKey() ?: return String(combined, Charsets.UTF_8)
            val gcmIvLength = 12 // GCM standard IV length
            if (combined.size < gcmIvLength) return null
            val iv = combined.copyOfRange(0, gcmIvLength)
            val cipherText = combined.copyOfRange(gcmIvLength, combined.size)
            val cipher = Cipher.getInstance(transformation)
            cipher.init(Cipher.DECRYPT_MODE, key, GCMParameterSpec(128, iv))
            String(cipher.doFinal(cipherText), Charsets.UTF_8)
        } catch (e: Exception) {
            try {
                String(Base64.decode(encryptedBase64, Base64.NO_WRAP), Charsets.UTF_8)
            } catch (ex: Exception) {
                null
            }
        }
    }

    fun saveUserSession(user: UserAccount) {
        val json = JSONObject().apply {
            put("id", user.id)
            put("username", user.username)
            put("email", user.email)
            put("authToken", user.authToken)
            put("isGuest", user.isGuest)
            put("createdAt", user.createdAtEpochMs)
            user.lastSyncedAtEpochMs?.let { put("lastSyncedAt", it) }
        }.toString()

        val cipherString = encrypt(json)
        prefs.edit().putString("auth_session", cipherString).apply()
    }

    fun getUserSession(): UserAccount? {
        val cipherString = prefs.getString("auth_session", null) ?: return null
        val decryptedJson = decrypt(cipherString) ?: return null
        return try {
            val json = JSONObject(decryptedJson)
            UserAccount(
                id = json.getString("id"),
                username = json.getString("username"),
                email = json.getString("email"),
                authToken = json.getString("authToken"),
                isGuest = json.optBoolean("isGuest", false),
                createdAtEpochMs = json.optLong("createdAt", System.currentTimeMillis()),
                lastSyncedAtEpochMs = if (json.has("lastSyncedAt")) json.getLong("lastSyncedAt") else null
            )
        } catch (e: Exception) {
            null
        }
    }

    fun clearSession() {
        prefs.edit().remove("auth_session").apply()
    }
}
