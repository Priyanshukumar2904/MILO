package com.milo.app.ui.dialogs

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.window.Dialog
import com.milo.app.domain.models.MiloEmotion
import com.milo.app.domain.models.UpdateManifest
import com.milo.app.domain.models.UpdateState
import com.milo.app.ui.mascot.MiloCompanion
import com.milo.app.ui.theme.*

@Composable
fun UpdateExperienceDialog(
    updateState: UpdateState,
    manifest: UpdateManifest,
    downloadProgress: Float,
    downloadedMb: Float,
    totalMb: Float,
    errorMessage: String? = null,
    onStartDownload: () -> Unit,
    onInstallNow: () -> Unit,
    onDismiss: () -> Unit
) {
    Dialog(onDismissRequest = {
        if (updateState != UpdateState.DOWNLOADING && updateState != UpdateState.VERIFYING) {
            onDismiss()
        }
    }) {
        Card(
            modifier = Modifier
                .fillMaxWidth()
                .wrapContentHeight(),
            shape = RoundedCornerShape(32.dp),
            colors = CardDefaults.cardColors(containerColor = MiloCardDark)
        ) {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(24.dp),
                horizontalAlignment = Alignment.CenterHorizontally,
                verticalArrangement = Arrangement.spacedBy(14.dp)
            ) {
                MiloCompanion(
                    emotion = when (updateState) {
                        UpdateState.DOWNLOADING -> MiloEmotion.Curious
                        UpdateState.VERIFYING -> MiloEmotion.Calm
                        UpdateState.READY_TO_INSTALL, UpdateState.ANDROID_INSTALLER -> MiloEmotion.Celebrating
                        UpdateState.FAILED -> MiloEmotion.Sleepy
                        else -> MiloEmotion.Happy
                    },
                    size = 64.dp
                )

                Text(
                    text = "MILO ${manifest.versionName} IS HERE",
                    style = MaterialTheme.typography.headlineMedium.copy(fontWeight = FontWeight.Black),
                    color = MiloWhite
                )

                Text(
                    text = when (updateState) {
                        UpdateState.DOWNLOADING -> "“Fetching the latest improvements...”"
                        UpdateState.READY_TO_INSTALL, UpdateState.ANDROID_INSTALLER -> "“Almost there! Ready to upgrade.”"
                        UpdateState.FAILED -> "“Ran into a hiccup with the network.”"
                        else -> "“Got something new for you.”"
                    },
                    style = MaterialTheme.typography.bodyMedium,
                    color = MiloZinc400
                )

                // State handling
                when (updateState) {
                    UpdateState.UPDATE_AVAILABLE, UpdateState.IDLE -> {
                        Column(
                            modifier = Modifier
                                .fillMaxWidth()
                                .background(MiloBorderDark.copy(alpha = 0.5f), RoundedCornerShape(20.dp))
                                .padding(14.dp),
                            verticalArrangement = Arrangement.spacedBy(6.dp)
                        ) {
                            Text(text = "WHAT'S NEW:", style = MaterialTheme.typography.labelSmall, color = MiloZinc500)
                            manifest.releaseNotes.forEach { note ->
                                Text(
                                    text = "• $note",
                                    style = MaterialTheme.typography.bodyMedium.copy(fontSize = 12.sp),
                                    color = MiloZinc200
                                )
                            }
                        }

                        Text(
                            text = "✓ All your activities, habits, and scores will be preserved.",
                            style = MaterialTheme.typography.labelSmall,
                            color = MiloGreen
                        )

                        Button(
                            onClick = onStartDownload,
                            modifier = Modifier.fillMaxWidth(),
                            colors = ButtonDefaults.buttonColors(containerColor = MiloWhite, contentColor = MiloBlack),
                            shape = RoundedCornerShape(14.dp)
                        ) {
                            Text(text = "UPDATE & INSTALL", fontWeight = FontWeight.Bold)
                        }
                    }

                    UpdateState.DOWNLOADING -> {
                        Column(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalAlignment = Alignment.CenterHorizontally
                        ) {
                            Text(
                                text = "Downloading Update...",
                                style = MaterialTheme.typography.titleMedium,
                                color = MiloWhite
                            )
                            Spacer(modifier = Modifier.height(8.dp))
                            LinearProgressIndicator(
                                progress = { downloadProgress },
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .height(6.dp),
                                color = MiloWhite,
                                trackColor = MiloZinc800
                            )
                            Spacer(modifier = Modifier.height(6.dp))
                            Text(
                                text = "${(downloadProgress * 100).toInt()}% • ${"%.1f".format(downloadedMb)} MB / ${"%.1f".format(totalMb)} MB",
                                style = MaterialTheme.typography.labelSmall,
                                color = MiloZinc400
                            )
                            Spacer(modifier = Modifier.height(4.dp))
                            Text(
                                text = "Installer will open automatically when ready",
                                style = MaterialTheme.typography.labelSmall.copy(fontSize = 11.sp),
                                color = MiloZinc500
                            )
                        }
                    }

                    UpdateState.VERIFYING -> {
                        CircularProgressIndicator(color = MiloWhite)
                        Text(
                            text = "Verifying Package Safety & Checksum...",
                            style = MaterialTheme.typography.bodyMedium,
                            color = MiloZinc400
                        )
                    }

                    UpdateState.READY_TO_INSTALL, UpdateState.ANDROID_INSTALLER -> {
                        Column(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalAlignment = Alignment.CenterHorizontally,
                            verticalArrangement = Arrangement.spacedBy(6.dp)
                        ) {
                            Text(
                                text = "System Installer Opened",
                                style = MaterialTheme.typography.titleMedium,
                                color = MiloGreen
                            )
                            Text(
                                text = "Tap 'Update' in the Android prompt to complete.\nYour existing data will remain completely intact.",
                                style = MaterialTheme.typography.bodySmall,
                                color = MiloZinc400,
                                textAlign = androidx.compose.ui.text.style.TextAlign.Center
                            )
                        }

                        Button(
                            onClick = onInstallNow,
                            modifier = Modifier.fillMaxWidth(),
                            colors = ButtonDefaults.buttonColors(containerColor = MiloWhite, contentColor = MiloBlack),
                            shape = RoundedCornerShape(14.dp)
                        ) {
                            Text(text = "RE-OPEN INSTALLER", fontWeight = FontWeight.Bold)
                        }
                    }

                    UpdateState.FAILED -> {
                        Column(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalAlignment = Alignment.CenterHorizontally,
                            verticalArrangement = Arrangement.spacedBy(6.dp)
                        ) {
                            Text(
                                text = "Update Interrupted",
                                style = MaterialTheme.typography.titleMedium,
                                color = MaterialTheme.colorScheme.error
                            )
                            Text(
                                text = errorMessage ?: "Could not complete update. Please check your connection.",
                                style = MaterialTheme.typography.bodySmall,
                                color = MiloZinc400,
                                textAlign = androidx.compose.ui.text.style.TextAlign.Center
                            )
                        }

                        Button(
                            onClick = onStartDownload,
                            modifier = Modifier.fillMaxWidth(),
                            colors = ButtonDefaults.buttonColors(containerColor = MiloWhite, contentColor = MiloBlack),
                            shape = RoundedCornerShape(14.dp)
                        ) {
                            Text(text = "TRY AGAIN", fontWeight = FontWeight.Bold)
                        }
                    }

                    else -> {}
                }

                TextButton(onClick = onDismiss) {
                    Text(
                        text = if (updateState == UpdateState.DOWNLOADING || updateState == UpdateState.VERIFYING) "Dismiss" else "Later",
                        color = MiloZinc500
                    )
                }
            }
        }
    }
}
