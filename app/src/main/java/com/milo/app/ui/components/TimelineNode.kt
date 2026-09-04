package com.milo.app.ui.components

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.filled.PlayArrow
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.milo.app.domain.models.Activity
import com.milo.app.domain.models.ActivityPriority
import com.milo.app.domain.models.ActivityStatus
import com.milo.app.ui.theme.*

@Composable
fun TimelineNode(
    activity: Activity,
    isLast: Boolean = false,
    onToggleStatus: () -> Unit,
    onStartTimer: () -> Unit,
    onSelect: () -> Unit
) {
    val isCompleted = activity.status == ActivityStatus.Completed
    val diff = (if (activity.actualDurationMinutes > 0) activity.actualDurationMinutes else 0) - activity.plannedDurationMinutes

    Row(
        modifier = Modifier
            .fillMaxWidth()
            .clickable { onSelect() }
            .padding(vertical = 4.dp),
        verticalAlignment = Alignment.Top
    ) {
        // Time Column
        Column(
            modifier = Modifier.width(52.dp),
            horizontalAlignment = Alignment.End
        ) {
            Text(
                text = activity.startTime.toString(),
                style = MaterialTheme.typography.titleMedium.copy(fontSize = 12.sp, fontWeight = FontWeight.Bold),
                color = MiloWhite
            )
            Text(
                text = "${activity.plannedDurationMinutes}m",
                style = MaterialTheme.typography.labelSmall.copy(fontSize = 9.sp),
                color = MiloZinc500
            )
        }

        Spacer(modifier = Modifier.width(10.dp))

        // Node & Spine
        Column(
            horizontalAlignment = Alignment.CenterHorizontally,
            modifier = Modifier.width(20.dp)
        ) {
            Box(
                modifier = Modifier
                    .size(20.dp)
                    .clip(CircleShape)
                    .background(if (isCompleted) MiloWhite else MiloCardDark)
                    .border(1.dp, if (isCompleted) MiloWhite else MiloZinc700, CircleShape)
                    .clickable { onToggleStatus() },
                contentAlignment = Alignment.Center
            ) {
                if (isCompleted) {
                    Icon(
                        imageVector = Icons.Default.Check,
                        contentDescription = "Completed",
                        tint = MiloBlack,
                        modifier = Modifier.size(12.dp)
                    )
                }
            }

            if (!isLast) {
                Box(
                    modifier = Modifier
                        .width(1.5.dp)
                        .height(56.dp)
                        .background(MiloBorderDark)
                )
            }
        }

        Spacer(modifier = Modifier.width(10.dp))

        // Card
        Row(
            modifier = Modifier
                .weight(1f)
                .background(MiloCardDark, RoundedCornerShape(18.dp))
                .border(1.dp, MiloBorderDark, RoundedCornerShape(18.dp))
                .padding(12.dp),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Column(modifier = Modifier.weight(1f)) {
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(6.dp)
                ) {
                    Text(
                        text = activity.title,
                        style = MaterialTheme.typography.titleMedium.copy(
                            fontWeight = FontWeight.SemiBold,
                            fontSize = 13.sp
                        ),
                        color = if (isCompleted) MiloZinc500 else MiloWhite,
                        maxLines = 1
                    )
                }

                Spacer(modifier = Modifier.height(2.dp))

                Row(
                    horizontalArrangement = Arrangement.spacedBy(8.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(
                        text = activity.category.name,
                        style = MaterialTheme.typography.labelSmall.copy(fontSize = 9.sp),
                        color = MiloZinc500
                    )
                    if (activity.actualDurationMinutes > 0 && diff != 0) {
                        Text(
                            text = if (diff > 0) "+${diff}m" else "${diff}m",
                            style = MaterialTheme.typography.labelSmall.copy(
                                fontSize = 9.sp,
                                fontWeight = FontWeight.Bold
                            ),
                            color = if (diff > 0) MiloGreen else MiloAmber
                        )
                    }
                }
            }

            if (!isCompleted) {
                IconButton(
                    onClick = onStartTimer,
                    modifier = Modifier
                        .size(28.dp)
                        .background(MiloZinc800, CircleShape)
                ) {
                    Icon(
                        imageVector = Icons.Default.PlayArrow,
                        contentDescription = "Start Timer",
                        tint = MiloWhite,
                        modifier = Modifier.size(16.dp)
                    )
                }
            }
        }
    }
}
