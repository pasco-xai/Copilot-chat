package com.example.ui.components

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowForward
import androidx.compose.material.icons.filled.AutoAwesome
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.ui.theme.CopilotTheme

@Composable
fun WelcomeThreadView(
    onSelectSuggestion: (String) -> Unit,
    modifier: Modifier = Modifier
) {
    val suggestions = listOf(
        "Optimiere Jetpack Compose LazyColumn auf stabile 120 FPS",
        "Erstelle eine Coroutine Flow Pipeline mit Exponential Backoff",
        "Implementiere eine Room Offline-First Synchronisation",
        "Schreibe Robolectric Unit Tests für Chat-Branching"
    )

    Column(
        modifier = modifier
            .fillMaxWidth()
            .padding(horizontal = 16.dp, vertical = 24.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center
    ) {
        // Copilot Badge with subtle purple glow
        OfficialCopilotBadge(size = 56.dp)

        Spacer(modifier = Modifier.height(16.dp))

        Text(
            text = "Claude Copilot",
            color = CopilotTheme.TextBright,
            fontSize = 20.sp,
            fontWeight = FontWeight.Bold,
            fontFamily = CopilotTheme.MonoFont
        )

        Spacer(modifier = Modifier.height(6.dp))

        Text(
            text = "Your AI pair programmer. Fast, concise, and context-aware.",
            color = CopilotTheme.TextMuted,
            fontSize = 12.5.sp,
            textAlign = TextAlign.Center,
            fontFamily = CopilotTheme.MonoFont,
            modifier = Modifier.padding(horizontal = 24.dp)
        )

        Spacer(modifier = Modifier.height(28.dp))

        // Suggestions Header
        Row(
            modifier = Modifier.fillMaxWidth(),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Icon(
                imageVector = Icons.Default.AutoAwesome,
                contentDescription = null,
                tint = CopilotTheme.PurpleLight,
                modifier = Modifier.size(14.dp)
            )
            Spacer(modifier = Modifier.width(6.dp))
            Text(
                text = "SUGGESTIONS",
                color = CopilotTheme.TextFaint,
                fontSize = 10.5.sp,
                fontWeight = FontWeight.Bold,
                fontFamily = CopilotTheme.MonoFont,
                letterSpacing = 1.sp
            )
        }

        Spacer(modifier = Modifier.height(10.dp))

        Column(
            modifier = Modifier.fillMaxWidth(),
            verticalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            suggestions.forEach { suggestion ->
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .clip(RoundedCornerShape(10.dp))
                        .background(CopilotTheme.PanelDark)
                        .border(0.5.dp, CopilotTheme.BorderSubtle, RoundedCornerShape(10.dp))
                        .clickable { onSelectSuggestion(suggestion) }
                        .padding(horizontal = 14.dp, vertical = 12.dp),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(
                        text = suggestion,
                        color = CopilotTheme.TextMain,
                        fontSize = 12.5.sp,
                        fontFamily = CopilotTheme.MonoFont,
                        modifier = Modifier.weight(1f)
                    )
                    Spacer(modifier = Modifier.width(8.dp))
                    Icon(
                        imageVector = Icons.AutoMirrored.Filled.ArrowForward,
                        contentDescription = "Ausführen",
                        tint = CopilotTheme.PurpleLight,
                        modifier = Modifier.size(15.dp)
                    )
                }
            }
        }
    }
}
