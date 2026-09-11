package com.milo.app.data.repository

import android.content.Context
import com.milo.app.data.local.SecureStorageManager
import com.milo.app.domain.models.AuthState
import com.milo.app.domain.models.UserAccount
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import java.security.MessageDigest
import java.util.UUID

class AuthRepository(context: Context) {

    private val secureStorage = SecureStorageManager(context)
    private val _currentUser = MutableStateFlow<UserAccount?>(secureStorage.getUserSession())
    val currentUser: StateFlow<UserAccount?> = _currentUser.asStateFlow()

    private val _hasCompletedOnboarding = MutableStateFlow(secureStorage.hasCompletedOnboarding())
    val hasCompletedOnboarding: StateFlow<Boolean> = _hasCompletedOnboarding.asStateFlow()

    val authState: AuthState
        get() = when {
            _currentUser.value == null -> AuthState.LOGGED_OUT
            _currentUser.value?.isGuest == true -> AuthState.GUEST
            else -> AuthState.AUTHENTICATED
        }

    fun completeOnboarding() {
        secureStorage.setCompletedOnboarding(true)
        _hasCompletedOnboarding.value = true
    }

    fun login(email: String, pass: String): Result<UserAccount> {
        val trimmedEmail = email.trim().lowercase()
        if (trimmedEmail.isEmpty()) {
            return Result.failure(IllegalArgumentException("Please enter your name or email"))
        }
        if (pass.length < 4) {
            return Result.failure(IllegalArgumentException("Password must be at least 4 characters"))
        }

        val token = generateToken(trimmedEmail, pass)
        val existingRecord = secureStorage.getAccountRecord(trimmedEmail)

        if (existingRecord != null) {
            val (account, storedHash) = existingRecord
            if (storedHash == token) {
                secureStorage.saveUserSession(account)
                _currentUser.value = account
                return Result.success(account)
            } else {
                return Result.failure(IllegalArgumentException("Incorrect password for this account"))
            }
        }

        return Result.failure(IllegalArgumentException("No account found with this email or username. Please register first."))
    }

    fun register(username: String, email: String, pass: String): Result<UserAccount> {
        val trimmedName = username.trim()
        val trimmedEmail = email.trim().lowercase()
        if (trimmedName.isEmpty()) {
            return Result.failure(IllegalArgumentException("Please enter your name"))
        }
        if (trimmedEmail.isEmpty()) {
            return Result.failure(IllegalArgumentException("Please enter your email or username"))
        }
        if (pass.length < 4) {
            return Result.failure(IllegalArgumentException("Password must be at least 4 characters"))
        }

        val existingRecord = secureStorage.getAccountRecord(trimmedEmail)
        if (existingRecord != null) {
            return Result.failure(IllegalArgumentException("An account with this email/username already exists. Please sign in instead."))
        }

        val token = generateToken(trimmedEmail, pass)
        val account = UserAccount(
            id = "usr_" + UUID.nameUUIDFromBytes(trimmedEmail.toByteArray()).toString().substring(0, 8),
            username = trimmedName,
            email = trimmedEmail,
            authToken = token,
            isGuest = false,
            createdAtEpochMs = System.currentTimeMillis()
        )

        secureStorage.saveAccountRecord(account, token)
        secureStorage.saveUserSession(account)
        _currentUser.value = account
        return Result.success(account)
    }

    fun continueAsGuest(): UserAccount {
        val guest = UserAccount(
            id = "guest_local",
            username = "Explorer",
            email = "offline@milo.local",
            authToken = "guest_token_${System.currentTimeMillis()}",
            isGuest = true,
            createdAtEpochMs = System.currentTimeMillis()
        )
        secureStorage.saveUserSession(guest)
        _currentUser.value = guest
        return guest
    }

    fun logout() {
        secureStorage.clearSession()
        _currentUser.value = null
    }

    fun updateProfileName(newName: String) {
        val current = _currentUser.value ?: return
        val updated = current.copy(username = newName.trim())
        secureStorage.saveUserSession(updated)
        _currentUser.value = updated
    }

    private fun generateToken(email: String, pass: String): String {
        val combined = "$email:$pass:milo_cloud_salt_2026"
        val digest = MessageDigest.getInstance("SHA-256").digest(combined.toByteArray())
        return digest.joinToString("") { "%02x".format(it) }
    }
}
