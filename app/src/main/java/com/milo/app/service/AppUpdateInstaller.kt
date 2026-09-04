package com.milo.app.service

import android.content.Context
import android.content.Intent
import android.net.Uri
import android.os.Build
import android.provider.Settings
import androidx.core.content.FileProvider
import com.milo.app.domain.engine.UpdateVerifier
import java.io.File

class AppUpdateInstaller(private val context: Context) {

    private val verifier = UpdateVerifier()

    /**
     * Checks if app has permission to install packages from unknown sources.
     */
    fun canInstallPackages(): Boolean {
        return if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            context.packageManager.canRequestPackageInstalls()
        } else {
            true
        }
    }

    /**
     * Guides user to permission settings if not authorized.
     */
    fun getSettingsPermissionIntent(): Intent {
        return if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            Intent(Settings.ACTION_MANAGE_UNKNOWN_APP_SOURCES).apply {
                data = Uri.parse("package:${context.packageName}")
                addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
            }
        } else {
            Intent(Settings.ACTION_SECURITY_SETTINGS).apply {
                addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
            }
        }
    }

    /**
     * Securely installs downloaded APK after verifying SHA-256 checksum (Section 53, 54, 56).
     */
    fun installApk(apkFile: File, expectedSha256: String, targetVersionCode: Int): Boolean {
        // 1. Rigorous SHA-256 verification
        val isVerified = verifier.verifyApk(
            apkFile = apkFile,
            expectedSha256 = expectedSha256,
            expectedMinVersionCode = 10000,
            targetVersionCode = targetVersionCode
        )
        if (!isVerified) return false

        // 2. Obtain content URI via FileProvider
        val apkUri: Uri = FileProvider.getUriForFile(
            context,
            "${context.packageName}.fileprovider",
            apkFile
        )

        // 3. Invoke standard Android package installer intent
        val installIntent = Intent(Intent.ACTION_VIEW).apply {
            setDataAndType(apkUri, "application/vnd.android.package-archive")
            addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION)
            addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
        }

        context.startActivity(installIntent)
        return true
    }
}
