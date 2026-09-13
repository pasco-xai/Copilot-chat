package com.example.ui.components

import android.widget.Toast
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.KeyboardArrowLeft
import androidx.compose.material.icons.automirrored.filled.KeyboardArrowRight
import androidx.compose.material.icons.filled.ContentCopy
import androidx.compose.material.icons.filled.Refresh
import androidx.compose.material.icons.filled.ThumbDown
import androidx.compose.material.icons.filled.ThumbUp
import androidx.compose.material.icons.outlined.ThumbDown
import androidx.compose.material.icons.outlined.ThumbUp
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.platform.LocalClipboardManager
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.AnnotatedString
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.data.model.FeedbackState
import com.example.ui.theme.CopilotTheme

@Composable
fun AssistantActionBar(
    totalBranches: Int,
    currentBranchIndex: Int,
    feedbackState: FeedbackState,
    onBranchPrev: () -> Unit,
    onBranchNext: () -> Unit,
    onCopy: () -> Unit,
    onReload: () -> Unit,
    onFeedback: (FeedbackState) -> Unit,
    modifier: Modifier = Modifier
) {
    Row(
        modifier = modifier.fillMaxWidth(),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.SpaceBetween
    ) {
        // Left: Branch picker (e.g. < 1 / 3 >)
        if (totalBranches > 1) {
            Row(
                modifier = Modifier
                    .clip(RoundedCornerShape(6.dp))
                    .background(CopilotTheme.UserBubble)
                    .border(0.5.dp, CopilotTheme.BorderSubtle, RoundedCornerShape(6.dp))
                    .padding(horizontal = 4.dp, vertical = 2.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                IconButton(
                    onClick = onBranchPrev,
                    enabled = currentBranchIndex > 0,
                    modifier = Modifier.size(24.dp)
                ) {
                    Icon(
                        imageVector = Icons.AutoMirrored.Filled.KeyboardArrowLeft,
                        contentDescription = "Vorherige Version",
                        tint = if (currentBranchIndex > 0) CopilotTheme.TextBright else CopilotTheme.TextFaint,
                        modifier = Modifier.size(16.dp)
                    )
                }

                Text(
                    text = "${currentBranchIndex + 1} / $totalBranches",
                    color = CopilotTheme.TextMuted,
                    fontSize = 11.sp,
                    fontFamily = CopilotTheme.MonoFont,
                    fontWeight = FontWeight.Medium,
                    modifier = Modifier.padding(horizontal = 4.dp)
                )

                IconButton(
                    onClick = onBranchNext,
                    enabled = currentBranchIndex < totalBranches - 1,
                    modifier = Modifier.size(24.dp)
                ) {
                    Icon(
                        imageVector = Icons.AutoMirrored.Filled.KeyboardArrowRight,
                        contentDescription = "Nächste Version",
                        tint = if (currentBranchIndex < totalBranches - 1) CopilotTheme.TextBright else CopilotTheme.TextFaint,
                        modifier = Modifier.size(16.dp)
                    )
                }
            }
        } else {
            Spacer(modifier = Modifier.width(4.dp))
        }

        // Right Action icons: Copy, Reload, Thumbs Up, Thumbs Down
        Row(
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(2.dp)
        ) {
            // Copy
            IconButton(
                onClick = onCopy,
                modifier = Modifier.size(28.dp)
            ) {
                Icon(
                    imageVector = Icons.Default.ContentCopy,
                    contentDescription = "Nachricht kopieren",
                    tint = CopilotTheme.TextMuted,
                    modifier = Modifier.size(14.dp)
                )
            }

            // Reload / Regenerate
            IconButton(
                onClick = onReload,
                modifier = Modifier.size(28.dp)
            ) {
                Icon(
                    imageVector = Icons.Default.Refresh,
                    contentDescription = "Neu generieren",
                    tint = CopilotTheme.TextMuted,
                    modifier = Modifier.size(14.dp)
                )
            }

            // Thumbs Up
            IconButton(
                onClick = {
                    val next = if (feedbackState == FeedbackState.THUMBS_UP) FeedbackState.NONE else FeedbackState.THUMBS_UP
                    onFeedback(next)
                },
                modifier = Modifier.size(28.dp)
            ) {
                Icon(
                    imageVector = if (feedbackState == FeedbackState.THUMBS_UP) Icons.Filled.ThumbUp else Icons.Outlined.ThumbUp,
                    contentDescription = "Hilfreich",
                    tint = if (feedbackState == FeedbackState.THUMBS_UP) CopilotTheme.StatusSuccess else CopilotTheme.TextMuted,
                    modifier = Modifier.size(14.dp)
                )
            }

            // Thumbs Down
            IconButton(
                onClick = {
                    val next = if (feedbackState == FeedbackState.THUMBS_DOWN) FeedbackState.NONE else FeedbackState.THUMBS_DOWN
                    onFeedback(next)
                },
                modifier = Modifier.size(28.dp)
            ) {
                Icon(
                    imageVector = if (feedbackState == FeedbackState.THUMBS_DOWN) Icons.Filled.ThumbDown else Icons.Outlined.ThumbDown,
                    contentDescription = "Nicht hilfreich",
                    tint = if (feedbackState == FeedbackState.THUMBS_DOWN) CopilotTheme.StatusDanger else CopilotTheme.TextMuted,
                    modifier = Modifier.size(14.dp)
                )
            }
        }
    }
}
