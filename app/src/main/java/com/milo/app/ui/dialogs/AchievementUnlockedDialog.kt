package com.milo.app.ui.dialogs

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.window.Dialog
import com.milo.app.domain.models.Achievement
import com.milo.app.domain.models.MiloEmotion
import com.milo.app.ui.mascot.MiloCompanion
import com.milo.app.ui.theme.*

@Composable
fun AchievementUnlockedDialog(
    achievement: Achievement,
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
                MiloCompanion(emotion = MiloEmotion.Celebrating, size = 72.dp)

                Text(
                    text = "ACHIEVEMENT UNLOCKED",
                    style = MaterialTheme.typography.labelSmall,
                    color = MiloZinc500
                )

                Text(
                    text = achievement.title,
                    style = MaterialTheme.typography.headlineLarge,
                    color = MiloWhite
                )

                Text(
                    text = achievement.description,
                    style = MaterialTheme.typography.bodyMedium,
                    color = MiloZinc400
                )

                if (achievement.previousStat != null && achievement.currentStat != null) {
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .background(MiloBorderDark.copy(alpha = 0.5f), RoundedCornerShape(16.dp))
                            .padding(14.dp),
                        horizontalArrangement = Arrangement.SpaceAround
                    ) {
                        Column(horizontalAlignment = Alignment.CenterHorizontally) {
                            Text(text = "Previous", style = MaterialTheme.typography.labelSmall, color = MiloZinc500)
                            Text(text = achievement.previousStat, style = MaterialTheme.typography.titleMedium, color = MiloZinc400)
                        }
                        Column(horizontalAlignment = Alignment.CenterHorizontally) {
                            Text(text = "Current Best", style = MaterialTheme.typography.labelSmall, color = MiloGreen)
                            Text(text = achievement.currentStat, style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.Bold), color = MiloWhite)
                        }
                    }
                }

                Text(
                    text = "“That's progress.”",
                    style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.Bold),
                    color = MiloWhite
                )

                Button(
                    onClick = onDismiss,
                    colors = ButtonDefaults.buttonColors(containerColor = MiloWhite, contentColor = MiloBlack),
                    shape = RoundedCornerShape(14.dp),
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Text(text = "Keep Going", fontWeight = FontWeight.Bold)
                }
            }
        }
    }
}
