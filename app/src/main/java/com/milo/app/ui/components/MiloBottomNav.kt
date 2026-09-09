package com.milo.app.ui.components

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.milo.app.ui.theme.*

enum class MiloNavTab(val label: String, val icon: ImageVector) {
    TODAY("Today", Icons.Default.WbSunny),
    PLANNER("Planner", Icons.Default.CalendarMonth),
    INSIGHTS("Insights", Icons.Default.AutoAwesome),
    ACCOUNT("Account", Icons.Default.Person)
}

@Composable
fun MiloBottomNav(
    currentTab: MiloNavTab,
    onTabSelected: (MiloNavTab) -> Unit
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .background(MiloBlack.copy(alpha = 0.96f))
            .padding(vertical = 10.dp, horizontal = 16.dp),
        horizontalArrangement = Arrangement.SpaceAround,
        verticalAlignment = Alignment.CenterVertically
    ) {
        MiloNavTab.entries.forEach { tab ->
            val isSelected = (tab == currentTab)
            Column(
                horizontalAlignment = Alignment.CenterHorizontally,
                modifier = Modifier
                    .clip(RoundedCornerShape(16.dp))
                    .clickable { onTabSelected(tab) }
                    .padding(horizontal = 14.dp, vertical = 6.dp)
            ) {
                Icon(
                    imageVector = tab.icon,
                    contentDescription = tab.label,
                    tint = if (isSelected) MiloWhite else MiloZinc500,
                    modifier = Modifier.size(22.dp)
                )

                Spacer(modifier = Modifier.height(4.dp))

                Text(
                    text = tab.label,
                    style = MaterialTheme.typography.labelSmall.copy(
                        fontSize = 12.sp,
                        fontWeight = if (isSelected) androidx.compose.ui.text.font.FontWeight.Bold else androidx.compose.ui.text.font.FontWeight.Medium
                    ),
                    color = if (isSelected) MiloWhite else MiloZinc500
                )

                if (isSelected) {
                    Box(
                        modifier = Modifier
                            .padding(top = 3.dp)
                            .size(4.dp)
                            .background(MiloWhite, CircleShape)
                    )
                } else {
                    Spacer(modifier = Modifier.height(7.dp))
                }
            }
        }
    }
}
