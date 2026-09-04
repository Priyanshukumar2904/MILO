package com.milo.app.domain.models

data class UpdateManifest(
    val versionName: String,
    val versionCode: Int,
    val apkUrl: String,
    val sha256: String,
    val isMandatory: Boolean = false,
    val minimumSupportedVersionCode: Int = 10000,
    val releaseDate: String,
    val releaseNotes: List<String>
)

enum class UpdateState {
    IDLE,
    CHECKING,
    UPDATE_AVAILABLE,
    USER_APPROVED,
    DOWNLOADING,
    VERIFYING,
    READY_TO_INSTALL,
    ANDROID_INSTALLER,
    FAILED,
    CANCELLED
}
