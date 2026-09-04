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
    onStartDownload: () -> Unit,
    onInstallNow: () -> Unit,
    onDismiss: () -> Unit
) {
    Dialog(onDismissRequest = onDismiss) {
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
                    emotion = if (updateState == UpdateState.DOWNLOADING) MiloEmotion.Curious else MiloEmotion.Happy,
                    size = 64.dp
                )

                Text(
                    text = "MILO ${manifest.versionName} IS HERE",
                    style = MaterialTheme.typography.headlineMedium.copy(fontWeight = FontWeight.Black),
                    color = MiloWhite
                )

                Text(
                    text = "“Got something new for you.”",
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
                            verticalArrangement = Arrangement.spacedBy(4.dp)
                        ) {
                            Text(text = "WHAT'S NEW:", style = MaterialTheme.typography.labelSmall, color = MiloZinc500)
                            manifest.releaseNotes.forEach { note ->
                                Text(text = "• $note", style = MaterialTheme.typography.bodyMedium.copy(fontSize = 12.sp), color = MiloZinc200)
                            }
                        }

                        Button(
                            onClick = onStartDownload,
                            modifier = Modifier.fillMaxWidth(),
                            colors = ButtonDefaults.buttonColors(containerColor = MiloWhite, contentColor = MiloBlack),
                            shape = RoundedCornerShape(14.dp)
                        ) {
                            Text(text = "UPDATE NOW", fontWeight = FontWeight.Bold)
                        }
                    }

                    UpdateState.DOWNLOADING -> {
                        Column(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalAlignment = Alignment.CenterHorizontally
                        ) {
                            Text(text = "Downloading APK...", style = MaterialTheme.typography.titleMedium, color = MiloWhite)
                            Spacer(modifier = Modifier.height(8.dp))
                            LinearProgressIndicator(
                                progress = { downloadProgress },
                                modifier = Modifier.fillMaxWidth().height(6.dp),
                                color = MiloWhite,
                                trackColor = MiloZinc800
                            )
                            Spacer(modifier = Modifier.height(6.dp))
                            Text(
                                text = "${(downloadProgress * 100).toInt()}% • ${downloadedMb.toInt()} MB / ${totalMb.toInt()} MB",
                                style = MaterialTheme.typography.labelSmall,
                                color = MiloZinc400
                            )
                        }
                    }

                    UpdateState.VERIFYING -> {
                        CircularProgressIndicator(color = MiloWhite)
                        Text(text = "Verifying SHA-256 Checksum & Safety...", style = MaterialTheme.typography.bodyMedium, color = MiloZinc400)
                    }

                    UpdateState.READY_TO_INSTALL -> {
                        Text(text = "Ready to install securely.", style = MaterialTheme.typography.titleMedium, color = MiloGreen)
                        Text(text = "All local data saved safely.", style = MaterialTheme.typography.labelSmall, color = MiloZinc400)

                        Button(
                            onClick = onInstallNow,
                            modifier = Modifier.fillMaxWidth(),
                            colors = ButtonDefaults.buttonColors(containerColor = MiloWhite, contentColor = MiloBlack),
                            shape = RoundedCornerShape(14.dp)
                        ) {
                            Text(text = "CONFIRM & INSTALL", fontWeight = FontWeight.Bold)
                        }
                    }

                    else -> {}
                }

                TextButton(onClick = onDismiss) {
                    Text(text = "Later", color = MiloZinc500)
                }
            }
        }
    }
}
