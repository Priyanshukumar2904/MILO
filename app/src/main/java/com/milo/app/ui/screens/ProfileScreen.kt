package com.milo.app.ui.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowForward
import androidx.compose.material.icons.filled.CloudSync
import androidx.compose.material.icons.filled.Lock
import androidx.compose.material.icons.filled.SystemUpdate
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.milo.app.domain.models.UserAccount
import com.milo.app.ui.mascot.MiloCompanion
import com.milo.app.ui.theme.*

@Composable
fun ProfileScreen(
    currentUser: UserAccount?,
    isSyncing: Boolean,
    syncMessage: String?,
    appVersionName: String = "1.5.0",
    onOpenAuth: () -> Unit,
    onOpenTutorial: () -> Unit = {},
    onLogout: () -> Unit,
    onSyncNow: () -> Unit,
    onCheckUpdate: () -> Unit
) {
    LazyColumn(
        modifier = Modifier
            .fillMaxSize()
            .background(MiloBlack)
            .padding(horizontal = 20.dp),
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        item {
            Spacer(modifier = Modifier.height(10.dp))
            Column {
                Text(
                    text = "ACCOUNT & SYSTEM",
                    style = MaterialTheme.typography.labelSmall,
                    color = MiloZinc500
                )
                Text(
                    text = "Account & Settings",
                    style = MaterialTheme.typography.headlineLarge,
                    color = MiloWhite
                )
            }
        }

        // User Identity Card
        item {
            Card(
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(24.dp),
                colors = CardDefaults.cardColors(containerColor = MiloCardDark)
            ) {
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(20.dp),
                    verticalArrangement = Arrangement.spacedBy(14.dp)
                ) {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.SpaceBetween
                    ) {
                        Column(modifier = Modifier.weight(1f)) {
                            Text(
                                text = currentUser?.username ?: "Explorer",
                                style = MaterialTheme.typography.headlineMedium,
                                color = MiloWhite
                            )
                            Spacer(modifier = Modifier.height(2.dp))
                            Text(
                                text = currentUser?.email ?: "Guest Mode (Local Only)",
                                style = MaterialTheme.typography.bodyMedium,
                                color = MiloZinc400
                            )
                            Spacer(modifier = Modifier.height(6.dp))
                            Box(
                                modifier = Modifier
                                    .background(
                                        if (currentUser?.isGuest == false) MiloGreen.copy(alpha = 0.2f) else MiloZinc800,
                                        RoundedCornerShape(8.dp)
                                    )
                                    .padding(horizontal = 10.dp, vertical = 4.dp)
                            ) {
                                Text(
                                    text = if (currentUser?.isGuest == false) "✓ Hardware KeyStore Secured" else "Offline Vault (On-Device)",
                                    style = MaterialTheme.typography.labelSmall,
                                    color = if (currentUser?.isGuest == false) MiloGreen else MiloZinc300
                                )
                            }
                        }
                        MiloCompanion(size = 56.dp)
                    }

                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.spacedBy(10.dp)
                    ) {
                        Button(
                            onClick = onOpenAuth,
                            modifier = Modifier.weight(1f),
                            colors = ButtonDefaults.buttonColors(containerColor = MiloWhite, contentColor = MiloBlack),
                            shape = RoundedCornerShape(12.dp)
                        ) {
                            Text(
                                text = if (currentUser == null || currentUser.isGuest) "Sign In / Register" else "Switch Account",
                                fontWeight = FontWeight.Bold,
                                style = MaterialTheme.typography.labelMedium
                            )
                        }

                        if (currentUser != null && !currentUser.isGuest) {
                            OutlinedButton(
                                onClick = onLogout,
                                colors = ButtonDefaults.outlinedButtonColors(contentColor = MiloZinc400),
                                shape = RoundedCornerShape(12.dp)
                            ) {
                                Text(
                                    text = "Log Out",
                                    style = MaterialTheme.typography.labelMedium
                                )
                            }
                        }
                    }
                }
            }
        }

        // Guide & Tutorial Section
        item {
            Card(
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(24.dp),
                colors = CardDefaults.cardColors(containerColor = MiloCardDark)
            ) {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(18.dp),
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.spacedBy(14.dp),
                        modifier = Modifier.weight(1f)
                    ) {
                        MiloCompanion(size = 38.dp)
                        Column {
                            Text(
                                text = "Milo Guide & Tutorial",
                                style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.Bold),
                                color = MiloWhite
                            )
                            Spacer(modifier = Modifier.height(2.dp))
                            Text(
                                text = "Review focus principles, workflows, and on-device privacy.",
                                style = MaterialTheme.typography.bodySmall,
                                color = MiloZinc400
                            )
                        }
                    }
                    IconButton(
                        onClick = onOpenTutorial,
                        modifier = Modifier
                            .background(MiloZinc800, CircleShape)
                            .size(36.dp)
                    ) {
                        Icon(
                            imageVector = Icons.AutoMirrored.Filled.ArrowForward,
                            contentDescription = "View Tutorial",
                            tint = MiloWhite,
                            modifier = Modifier.size(18.dp)
                        )
                    }
                }
            }
        }

        // Cloud Backup & Sync Section
        item {
            Card(
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(24.dp),
                colors = CardDefaults.cardColors(containerColor = MiloCardDark)
            ) {
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(20.dp),
                    verticalArrangement = Arrangement.spacedBy(12.dp)
                ) {
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.spacedBy(10.dp)
                    ) {
                        Icon(
                            imageVector = Icons.Default.Lock,
                            contentDescription = "Local Storage",
                            tint = MiloWhite,
                            modifier = Modifier.size(22.dp)
                        )
                        Text(
                            text = "DATA SOVEREIGNTY & PRIVACY",
                            style = MaterialTheme.typography.labelSmall,
                            color = MiloZinc400
                        )
                    }

                    Text(
                        text = "100% On-Device Local Storage",
                        style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.Bold),
                        color = MiloWhite
                    )

                    Text(
                        text = "All your activities, habits, scores, and reflections are stored exclusively in your device's local Room database. Zero personal data leaves your phone.",
                        style = MaterialTheme.typography.bodyMedium,
                        color = MiloZinc400
                    )

                    if (syncMessage != null) {
                        Text(
                            text = syncMessage,
                            style = MaterialTheme.typography.bodySmall,
                            color = MiloGreen
                        )
                    }

                    Button(
                        onClick = onSyncNow,
                        modifier = Modifier.fillMaxWidth(),
                        colors = ButtonDefaults.buttonColors(containerColor = MiloZinc800, contentColor = MiloWhite),
                        shape = RoundedCornerShape(12.dp),
                        enabled = !isSyncing
                    ) {
                        if (isSyncing) {
                            CircularProgressIndicator(
                                modifier = Modifier.size(18.dp),
                                color = MiloWhite,
                                strokeWidth = 2.dp
                            )
                            Spacer(modifier = Modifier.width(8.dp))
                            Text("Syncing...", style = MaterialTheme.typography.labelMedium)
                        } else {
                            Text("Sync Now", style = MaterialTheme.typography.labelMedium, fontWeight = FontWeight.Bold)
                        }
                    }
                }
            }
        }

        // Software Updates & System Section
        item {
            Card(
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(24.dp),
                colors = CardDefaults.cardColors(containerColor = MiloCardDark)
            ) {
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(20.dp),
                    verticalArrangement = Arrangement.spacedBy(12.dp)
                ) {
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.spacedBy(10.dp)
                    ) {
                        Icon(
                            imageVector = Icons.Default.SystemUpdate,
                            contentDescription = "Updates",
                            tint = MiloWhite,
                            modifier = Modifier.size(22.dp)
                        )
                        Text(
                            text = "SOFTWARE UPDATES & DISTRIBUTION",
                            style = MaterialTheme.typography.labelSmall,
                            color = MiloZinc400
                        )
                    }

                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Column {
                            Text(
                                text = "MILO Version $appVersionName",
                                style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.Bold),
                                color = MiloWhite
                            )
                            Text(
                                text = "Official Signed GitHub Distribution",
                                style = MaterialTheme.typography.bodySmall,
                                color = MiloZinc500
                            )
                        }
                    }

                    Button(
                        onClick = onCheckUpdate,
                        modifier = Modifier.fillMaxWidth(),
                        colors = ButtonDefaults.buttonColors(containerColor = MiloWhite, contentColor = MiloBlack),
                        shape = RoundedCornerShape(14.dp)
                    ) {
                        Text(
                            text = "Check for Updates",
                            fontWeight = FontWeight.Bold,
                            style = MaterialTheme.typography.labelLarge
                        )
                    }
                }
            }
        }

        // Security & Hardware Vault
        item {
            Card(
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(24.dp),
                colors = CardDefaults.cardColors(containerColor = MiloCardDark)
            ) {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(18.dp),
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(14.dp)
                ) {
                    Icon(
                        imageVector = Icons.Default.Lock,
                        contentDescription = "Security",
                        tint = MiloGreen,
                        modifier = Modifier.size(24.dp)
                    )
                    Column {
                        Text(
                            text = "Hardware-Backed Security Vault",
                            style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.Bold),
                            color = MiloWhite
                        )
                        Spacer(modifier = Modifier.height(2.dp))
                        Text(
                            text = "Session keys encrypted via Android KeyStore AES-GCM. Zero unencrypted credentials stored.",
                            style = MaterialTheme.typography.bodySmall,
                            color = MiloZinc400
                        )
                    }
                }
            }
        }

        // Philosophy Card
        item {
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .background(MiloCardDark, RoundedCornerShape(24.dp))
                    .padding(20.dp)
            ) {
                Column(horizontalAlignment = Alignment.CenterHorizontally) {
                    Text(
                        text = "“You don't need to become a completely different person overnight. Just become a little better than yesterday.”",
                        style = MaterialTheme.typography.titleMedium.copy(
                            fontWeight = FontWeight.Bold,
                            lineHeight = 22.sp
                        ),
                        color = MiloWhite
                    )
                    Spacer(modifier = Modifier.height(6.dp))
                    Text(
                        text = "Progress > Perfection • Consistency > Intensity",
                        style = MaterialTheme.typography.labelSmall,
                        color = MiloZinc500
                    )
                }
            }
            Spacer(modifier = Modifier.height(80.dp))
        }
    }
}
