package com.milo.app.ui.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.milo.app.domain.models.Achievement
import com.milo.app.domain.models.PersonalRecord
import com.milo.app.ui.mascot.MiloCompanion
import com.milo.app.ui.theme.*

@Composable
fun ProfileScreen(
    records: List<PersonalRecord>,
    achievements: List<Achievement>,
    onCheckUpdate: () -> Unit,
    onResetData: () -> Unit
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
                Text(text = "ACCOUNT & IDENTITY", style = MaterialTheme.typography.labelSmall, color = MiloZinc500)
                Text(text = "Profile & Records", style = MaterialTheme.typography.headlineLarge, color = MiloWhite)
            }
        }

        // User Identity Card
        item {
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .background(MiloCardDark, RoundedCornerShape(24.dp))
                    .padding(20.dp),
                verticalArrangement = Arrangement.Center,
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                Column {
                    Text(text = "Priyanshu", style = MaterialTheme.typography.headlineMedium, color = MiloWhite)
                    Text(text = "Operating since August 2026", style = MaterialTheme.typography.labelSmall, color = MiloZinc500)
                }
                MiloCompanion(size = 56.dp)
            }
        }

        // Personal Records
        item {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .background(MiloCardDark, RoundedCornerShape(24.dp))
                    .padding(18.dp)
            ) {
                Text(text = "PERSONAL RECORDS", style = MaterialTheme.typography.labelSmall, color = MiloZinc500)
                Spacer(modifier = Modifier.height(10.dp))

                records.forEach { r ->
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(vertical = 6.dp),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Column {
                            Text(text = r.title, style = MaterialTheme.typography.titleMedium, color = MiloWhite)
                            Text(text = "Achieved ${r.dateAchieved}", style = MaterialTheme.typography.labelSmall, color = MiloZinc500)
                        }
                        Text(text = r.valueDisplay, style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.Bold), color = MiloWhite)
                    }
                }
            }
        }

        // Achievements Unlocked
        item {
            Text(text = "UNLOCKED ACHIEVEMENTS", style = MaterialTheme.typography.labelSmall, color = MiloZinc500)
        }

        items(achievements) { a ->
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .background(MiloCardDark, RoundedCornerShape(20.dp))
                    .padding(16.dp),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Column(modifier = Modifier.weight(1f)) {
                    Text(text = a.title, style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.Bold), color = MiloWhite)
                    Text(text = a.description, style = MaterialTheme.typography.bodyMedium.copy(fontSize = 11.sp), color = MiloZinc400)
                }
                Box(
                    modifier = Modifier
                        .background(MiloZinc800, RoundedCornerShape(10.dp))
                        .padding(horizontal = 8.dp, vertical = 4.dp)
                ) {
                    Text(text = "Unlocked", style = MaterialTheme.typography.labelSmall, color = MiloWhite)
                }
            }
        }

        // Update Checking & System
        item {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .background(MiloCardDark, RoundedCornerShape(24.dp))
                    .padding(18.dp),
                verticalArrangement = Arrangement.spacedBy(10.dp)
            ) {
                Text(text = "SYSTEM & DISTRIBUTION", style = MaterialTheme.typography.labelSmall, color = MiloZinc500)

                Button(
                    onClick = onCheckUpdate,
                    modifier = Modifier.fillMaxWidth(),
                    colors = ButtonDefaults.buttonColors(containerColor = MiloWhite, contentColor = MiloBlack),
                    shape = RoundedCornerShape(14.dp)
                ) {
                    Text(text = "Check for Updates (v1.4.0)", fontWeight = FontWeight.Bold)
                }

                OutlinedButton(
                    onClick = onResetData,
                    modifier = Modifier.fillMaxWidth(),
                    colors = ButtonDefaults.outlinedButtonColors(contentColor = MiloZinc400),
                    shape = RoundedCornerShape(14.dp)
                ) {
                    Text(text = "Reload 30-Day Historical Data")
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
                        style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.Bold, lineHeight = 20.sp),
                        color = MiloWhite
                    )
                    Spacer(modifier = Modifier.height(4.dp))
                    Text(text = "Progress > Perfection • Consistency > Intensity", style = MaterialTheme.typography.labelSmall, color = MiloZinc500)
                }
            }
            Spacer(modifier = Modifier.height(72.dp))
        }
    }
}
