package com.milo.app.service

import android.content.Context
import android.content.Intent
import android.net.Uri
import android.os.Build
import android.provider.Settings
import androidx.core.content.FileProvider
import com.milo.app.domain.engine.UpdateVerifier
import com.milo.app.domain.models.UpdateManifest
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import org.json.JSONObject
import java.io.File
import java.io.FileOutputStream
import java.net.HttpURLConnection
import java.net.URL

class AppUpdateInstaller(private val context: Context) {

    private val verifier = UpdateVerifier()

    fun verifyApk(apkFile: File, expectedSha256: String, targetVersionCode: Int): Boolean {
        return verifier.verifyApk(
            apkFile = apkFile,
            expectedSha256 = expectedSha256,
            expectedMinVersionCode = 10,
            targetVersionCode = targetVersionCode
        )
    }

    fun canInstallPackages(): Boolean {
        return if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            context.packageManager.canRequestPackageInstalls()
        } else {
            true
        }
    }

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

    suspend fun fetchUpdateManifest(urlStr: String): Result<UpdateManifest> = withContext(Dispatchers.IO) {
        try {
            val url = URL(urlStr)
            val conn = (url.openConnection() as HttpURLConnection).apply {
                connectTimeout = 8000
                readTimeout = 8000
                requestMethod = "GET"
                setRequestProperty("User-Agent", "MILO-Android-Client")
            }

            if (conn.responseCode != HttpURLConnection.HTTP_OK) {
                return@withContext Result.failure(Exception("HTTP Error ${conn.responseCode}"))
            }

            val text = conn.inputStream.bufferedReader().use { it.readText() }
            val json = JSONObject(text)

            val changelogList = mutableListOf<String>()
            val clArray = json.optJSONArray("changelog")
            if (clArray != null) {
                for (i in 0 until clArray.length()) {
                    changelogList.add(clArray.getString(i))
                }
            }

            val manifest = UpdateManifest(
                versionName = json.getString("versionName"),
                versionCode = json.getInt("versionCode"),
                apkUrl = json.getString("downloadUrl"),
                sha256 = json.getString("sha256"),
                isMandatory = json.optBoolean("criticalUpdate", false),
                minimumSupportedVersionCode = json.optInt("minSupportedVersionCode", 10),
                releaseDate = json.optString("releaseDate", "Today"),
                releaseNotes = changelogList,
                fileSizeBytes = json.optLong("fileSizeBytes", 0L)
            )
            Result.success(manifest)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    suspend fun downloadApk(
        downloadUrl: String,
        destinationFile: File,
        onProgress: (progress: Float, downloadedMb: Float, totalMb: Float) -> Unit
    ): Result<File> = withContext(Dispatchers.IO) {
        try {
            destinationFile.parentFile?.mkdirs()
            if (destinationFile.exists()) destinationFile.delete()

            var currentUrl = downloadUrl
            var currentConn: HttpURLConnection
            var redirects = 0

            while (true) {
                currentConn = (URL(currentUrl).openConnection() as HttpURLConnection).apply {
                    connectTimeout = 15000
                    readTimeout = 15000
                    instanceFollowRedirects = false
                    requestMethod = "GET"
                    setRequestProperty("User-Agent", "MILO-Android-Client")
                }

                val status = currentConn.responseCode
                if (status in 300..399 && redirects < 5) {
                    val location = currentConn.getHeaderField("Location")
                    currentConn.disconnect()
                    currentUrl = location
                    redirects++
                } else if (status == HttpURLConnection.HTTP_OK) {
                    break
                } else {
                    return@withContext Result.failure(Exception("Download failed with HTTP $status"))
                }
            }

            val totalBytes = currentConn.contentLengthLong.coerceAtLeast(1L)
            val totalMb = totalBytes / (1024f * 1024f)

            currentConn.inputStream.use { input ->
                FileOutputStream(destinationFile).use { output ->
                    val buffer = ByteArray(32 * 1024)
                    var bytesRead: Int
                    var totalRead = 0L

                    while (input.read(buffer).also { bytesRead = it } != -1) {
                        output.write(buffer, 0, bytesRead)
                        totalRead += bytesRead
                        val progress = (totalRead.toFloat() / totalBytes).coerceIn(0f, 1f)
                        val downloadedMb = totalRead / (1024f * 1024f)
                        onProgress(progress, downloadedMb, totalMb)
                    }
                    output.flush()
                }
            }

            Result.success(destinationFile)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    fun installApk(apkFile: File, expectedSha256: String, targetVersionCode: Int): Boolean {
        return try {
            val isVerified = verifier.verifyApk(
                apkFile = apkFile,
                expectedSha256 = expectedSha256,
                expectedMinVersionCode = 10,
                targetVersionCode = targetVersionCode
            )
            if (!isVerified) return false

            val apkUri: Uri = FileProvider.getUriForFile(
                context,
                "${context.packageName}.fileprovider",
                apkFile
            )

            val installIntent = Intent(Intent.ACTION_VIEW).apply {
                setDataAndType(apkUri, "application/vnd.android.package-archive")
                addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION)
                addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
            }

            context.startActivity(installIntent)
            true
        } catch (e: Exception) {
            e.printStackTrace()
            false
        }
    }
}
