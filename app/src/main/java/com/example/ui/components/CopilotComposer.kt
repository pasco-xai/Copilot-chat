package com.example.ui.components

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.scaleIn
import androidx.compose.animation.scaleOut
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.horizontalScroll
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.heightIn
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.BasicTextField
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.Send
import androidx.compose.material.icons.filled.AutoAwesome
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.Stop
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.focus.onFocusChanged
import androidx.compose.ui.graphics.SolidColor
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.data.model.AttachmentItem
import com.example.ui.theme.CopilotTheme

@Composable
fun CopilotComposer(
    text: String,
    onTextChange: (String) -> Unit,
    onEnhancePrompt: () -> Unit,
    isGenerating: Boolean,
    onSend: () -> Unit,
    onStop: () -> Unit,
    modifier: Modifier = Modifier
) {
    var isFocused by remember { mutableStateOf(false) }

    Column(
        modifier = modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(14.dp))
            .background(CopilotTheme.PanelDark)
            .border(
                width = if (isFocused) 1.dp else 0.8.dp,
                color = if (isFocused) CopilotTheme.PurplePrimary else CopilotTheme.BorderSubtle,
                shape = RoundedCornerShape(14.dp)
            )
            .padding(10.dp),
        verticalArrangement = Arrangement.spacedBy(8.dp)
    ) {
        // Text input field with placeholder
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .heightIn(min = 40.dp, max = 140.dp)
        ) {
            BasicTextField(
                value = text,
                onValueChange = onTextChange,
                modifier = Modifier
                    .fillMaxWidth()
                    .onFocusChanged { isFocused = it.isFocused },
                singleLine = false,
                textStyle = TextStyle(
                    color = CopilotTheme.TextBright,
                    fontSize = 13.5.sp,
                    fontFamily = CopilotTheme.MonoFont,
                    lineHeight = 19.sp
                ),
                cursorBrush = SolidColor(CopilotTheme.PurpleLight),
                decorationBox = { innerTextField ->
                    if (text.isEmpty()) {
                        Text(
                            text = "Ask Copilot or type '/' for commands...",
                            color = CopilotTheme.TextFaint,
                            fontSize = 13.5.sp,
                            fontFamily = CopilotTheme.MonoFont
                        )
                    }
                    innerTextField()
                }
            )
        }

        // Action controls bar
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            // Left: Enhance prompt button & context hint
            Row(verticalAlignment = Alignment.CenterVertically) {
                IconButton(
                    onClick = onEnhancePrompt,
                    modifier = Modifier.size(32.dp)
                ) {
                    Icon(
                        imageVector = Icons.Default.AutoAwesome,
                        contentDescription = "Enhance Prompt",
                        tint = CopilotTheme.TextMuted,
                        modifier = Modifier.size(17.dp)
                    )
                }

                Text(
                    text = "Shift + Enter for new line",
                    color = CopilotTheme.TextFaint,
                    fontSize = 10.sp,
                    fontFamily = CopilotTheme.MonoFont
                )
            }

            // Right: Send or Stop generation button
            if (isGenerating) {
                IconButton(
                    onClick = onStop,
                    modifier = Modifier
                        .size(32.dp)
                        .clip(CircleShape)
                        .background(CopilotTheme.StatusDanger.copy(alpha = 0.15f))
                        .border(1.dp, CopilotTheme.StatusDanger, CircleShape)
                ) {
                    Icon(
                        imageVector = Icons.Default.Stop,
                        contentDescription = "Stoppen",
                        tint = CopilotTheme.StatusDanger,
                        modifier = Modifier.size(16.dp)
                    )
                }
            } else {
                val canSend = text.isNotBlank()
                IconButton(
                    onClick = {
                        if (canSend) onSend()
                    },
                    enabled = canSend,
                    modifier = Modifier
                        .size(32.dp)
                        .clip(CircleShape)
                        .background(
                            if (canSend) CopilotTheme.PurplePrimary else CopilotTheme.UserBubble
                        )
                ) {
                    Icon(
                        imageVector = Icons.AutoMirrored.Filled.Send,
                        contentDescription = "Senden",
                        tint = if (canSend) CopilotTheme.TextBright else CopilotTheme.TextFaint,
                        modifier = Modifier.size(15.dp)
                    )
                }
            }
        }
    }
}
