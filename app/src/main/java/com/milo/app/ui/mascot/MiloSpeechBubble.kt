package com.milo.app.ui.mascot

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.milo.app.domain.models.MiloEmotion
import com.milo.app.ui.theme.MiloBorderDark
import com.milo.app.ui.theme.MiloCardDark
import com.milo.app.ui.theme.MiloZinc400

@Composable
fun MiloSpeechBubble(
    modifier: Modifier = Modifier,
    quote: String,
    subtext: String? = null,
    emotion: MiloEmotion = MiloEmotion.Calm
) {
    Row(
        modifier = modifier
            .fillMaxWidth()
            .background(MiloCardDark, RoundedCornerShape(24.dp))
            .border(1.dp, MiloBorderDark, RoundedCornerShape(24.dp))
            .padding(16.dp),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.spacedBy(14.dp)
    ) {
        MiloCompanion(
            emotion = emotion,
            size = 48.dp
        )

        Column(modifier = Modifier.weight(1f)) {
            Text(
                text = "“$quote”",
                style = MaterialTheme.typography.titleMedium.copy(
                    fontWeight = FontWeight.Bold,
                    lineHeight = 18.sp
                ),
                color = MaterialTheme.colorScheme.onSurface
            )
            if (subtext != null) {
                Spacer(modifier = Modifier.height(2.dp))
                Text(
                    text = subtext,
                    style = MaterialTheme.typography.bodyMedium.copy(
                        fontSize = 11.sp,
                        color = MiloZinc400
                    )
                )
            }
        }
    }
}
