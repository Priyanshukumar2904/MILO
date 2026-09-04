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
    SCHEDULE("Schedule", Icons.Default.CalendarMonth),
    INSIGHTS("Insights", Icons.Default.AutoAwesome),
    HABITS("Habits", Icons.Default.CheckCircle),
    PROFILE("Profile", Icons.Default.Person)
}

@Composable
fun MiloBottomNav(
    currentTab: MiloNavTab,
    onTabSelected: (MiloNavTab) -> Unit
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .background(MiloBlack.copy(alpha = 0.95f))
            .padding(vertical = 8.dp, horizontal = 12.dp),
        horizontalArrangement = Arrangement.SpaceAround,
        verticalAlignment = Alignment.CenterVertically
    ) {
        MiloNavTab.values().forEach { tab ->
            val isSelected = (tab == currentTab)
            Column(
                horizontalAlignment = Alignment.CenterHorizontally,
                modifier = Modifier
                    .clip(RoundedCornerShape(16.dp))
                    .clickable { onTabSelected(tab) }
                    .padding(horizontal = 12.dp, vertical = 6.dp)
            ) {
                Icon(
                    imageVector = tab.icon,
                    contentDescription = tab.label,
                    tint = if (isSelected) MiloWhite else MiloZinc500,
                    modifier = Modifier.size(20.dp)
                )

                Spacer(modifier = Modifier.height(2.dp))

                Text(
                    text = tab.label,
                    style = MaterialTheme.typography.labelSmall.copy(fontSize = 9.sp),
                    color = if (isSelected) MiloWhite else MiloZinc500
                )

                if (isSelected) {
                    Box(
                        modifier = Modifier
                            .padding(top = 2.dp)
                            .size(3.dp)
                            .background(MiloWhite, CircleShape)
                    )
                }
            }
        }
    }
}
