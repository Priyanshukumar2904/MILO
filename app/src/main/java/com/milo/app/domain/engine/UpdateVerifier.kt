package com.milo.app.domain.engine

import java.io.File
import java.io.FileInputStream
import java.security.MessageDigest

class UpdateVerifier {

    /**
     * Calculates SHA-256 checksum of local file.
     */
    fun calculateSha256(file: File): String {
        val digest = MessageDigest.getInstance("SHA-256")
        FileInputStream(file).use { fis ->
            val buffer = ByteArray(8192)
            var bytesRead: Int
            while (fis.read(buffer).also { bytesRead = it } != -1) {
                digest.update(buffer, 0, bytesRead)
            }
        }
        return digest.digest().joinToString("") { "%02x".format(it) }
    }

    /**
     * Verifies APK integrity against expected hash from trusted manifest.
     */
    fun verifyApk(apkFile: File, expectedSha256: String, expectedMinVersionCode: Int, targetVersionCode: Int): Boolean {
        if (!apkFile.exists() || apkFile.length() == 0L) return false
        if (targetVersionCode < expectedMinVersionCode) return false

        val calculated = calculateSha256(apkFile)
        return calculated.equals(expectedSha256.trim(), ignoreCase = true)
    }
}
