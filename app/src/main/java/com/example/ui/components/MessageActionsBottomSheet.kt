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
import androidx.compose.foundation.layout.navigationBarsPadding
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.ContentCopy
import androidx.compose.material.icons.filled.DeleteOutline
import androidx.compose.material.icons.filled.Edit
import androidx.compose.material.icons.filled.Person
import androidx.compose.material.icons.filled.Refresh
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.ModalBottomSheet
import androidx.compose.material3.SheetState
import androidx.compose.material3.Text
import androidx.compose.material3.rememberModalBottomSheetState
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.data.model.ChatMessage
import com.example.data.model.MessageSender
import com.example.ui.theme.CopilotTheme

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun MessageActionsBottomSheet(
    message: ChatMessage,
    onDismiss: () -> Unit,
    onRegenerate: () -> Unit,
    onEdit: () -> Unit,
    onDelete: () -> Unit,
    onCopy: () -> Unit,
    sheetState: SheetState = rememberModalBottomSheetState(skipPartiallyExpanded = true)
) {
    val isUser = message.sender == MessageSender.USER
    val previewText = message.activeBranch.content.ifBlank {
        if (isUser) "(Empty prompt)" else "(Generating response...)"
    }

    ModalBottomSheet(
        onDismissRequest = onDismiss,
        sheetState = sheetState,
        containerColor = CopilotTheme.PanelDark,
        dragHandle = {
            Box(
                modifier = Modifier
                    .padding(vertical = 10.dp)
                    .width(36.dp)
                    .height(4.dp)
                    .clip(CircleShape)
                    .background(CopilotTheme.BorderSubtle)
            )
        },
        shape = RoundedCornerShape(topStart = 18.dp, topEnd = 18.dp)
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .navigationBarsPadding()
                .padding(horizontal = 16.dp, vertical = 4.dp),
            verticalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            // Header Row: Avatar, Sender info, Close button
            Row(
                modifier = Modifier.fillMaxWidth(),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(10.dp)
                ) {
                    if (isUser) {
                        Box(
                            modifier = Modifier
                                .size(28.dp)
                                .clip(CircleShape)
                                .background(CopilotTheme.UserBubble)
                                .border(0.8.dp, CopilotTheme.BorderSubtle, CircleShape),
                            contentAlignment = Alignment.Center
                        ) {
                            Icon(
                                imageVector = Icons.Default.Person,
                                contentDescription = "User",
                                tint = CopilotTheme.PurpleLight,
                                modifier = Modifier.size(16.dp)
                            )
                        }
                    } else {
                        OfficialCopilotBadge(size = 28.dp)
                    }

                    Column {
                        Text(
                            text = "Message Actions",
                            color = CopilotTheme.TextBright,
                            fontSize = 14.sp,
                            fontWeight = FontWeight.Bold,
                            fontFamily = CopilotTheme.MonoFont
                        )
                        Text(
                            text = "${if (isUser) "You" else "Claude Copilot"} • ${message.activeBranch.timestamp}",
                            color = CopilotTheme.TextMuted,
                            fontSize = 11.sp,
                            fontFamily = CopilotTheme.MonoFont
                        )
                    }
                }

                IconButton(
                    onClick = onDismiss,
                    modifier = Modifier
                        .size(32.dp)
                        .testTag("close_action_sheet")
                ) {
                    Icon(
                        imageVector = Icons.Default.Close,
                        contentDescription = "Close",
                        tint = CopilotTheme.TextMuted,
                        modifier = Modifier.size(18.dp)
                    )
                }
            }

            // Message Preview Box
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .clip(RoundedCornerShape(8.dp))
                    .background(CopilotTheme.CodeBg)
                    .border(0.8.dp, CopilotTheme.BorderSubtle, RoundedCornerShape(8.dp))
                    .padding(horizontal = 12.dp, vertical = 8.dp)
            ) {
                Text(
                    text = previewText,
                    color = CopilotTheme.TextMain,
                    fontSize = 12.sp,
                    lineHeight = 16.sp,
                    fontFamily = CopilotTheme.MonoFont,
                    maxLines = 3,
                    overflow = TextOverflow.Ellipsis
                )
            }

            HorizontalDivider(
                thickness = 0.5.dp,
                color = CopilotTheme.BorderSubtle,
                modifier = Modifier.padding(vertical = 2.dp)
            )

            // Action: Regenerate
            MessageActionItem(
                icon = Icons.Default.Refresh,
                iconTint = CopilotTheme.PurpleLight,
                title = "Regenerate",
                subtitle = if (isUser) "Regenerate response for this prompt" else "Generate a new response branch",
                testTag = "action_regenerate",
                onClick = onRegenerate
            )

            // Action: Edit
            MessageActionItem(
                icon = Icons.Default.Edit,
                iconTint = CopilotTheme.LinkBlue,
                title = "Edit Message",
                subtitle = if (isUser) "Edit prompt and update chat" else "Edit response content",
                testTag = "action_edit",
                onClick = onEdit
            )

            // Action: Copy
            MessageActionItem(
                icon = Icons.Default.ContentCopy,
                iconTint = CopilotTheme.TextMain,
                title = "Copy Text",
                subtitle = "Copy message content to clipboard",
                testTag = "action_copy",
                onClick = onCopy
            )

            // Action: Delete
            MessageActionItem(
                icon = Icons.Default.DeleteOutline,
                iconTint = CopilotTheme.StatusDanger,
                title = "Delete Message",
                subtitle = "Remove this message from the conversation",
                testTag = "action_delete",
                isDestructive = true,
                onClick = onDelete
            )

            Spacer(modifier = Modifier.height(8.dp))
        }
    }
}

@Composable
private fun MessageActionItem(
    icon: ImageVector,
    iconTint: Color,
    title: String,
    subtitle: String,
    testTag: String,
    onClick: () -> Unit,
    isDestructive: Boolean = false
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(10.dp))
            .clickable(onClick = onClick)
            .testTag(testTag)
            .padding(horizontal = 10.dp, vertical = 9.dp),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.spacedBy(14.dp)
    ) {
        Box(
            modifier = Modifier
                .size(36.dp)
                .clip(RoundedCornerShape(8.dp))
                .background(
                    if (isDestructive) CopilotTheme.StatusDanger.copy(alpha = 0.12f)
                    else iconTint.copy(alpha = 0.12f)
                ),
            contentAlignment = Alignment.Center
        ) {
            Icon(
                imageVector = icon,
                contentDescription = title,
                tint = iconTint,
                modifier = Modifier.size(18.dp)
            )
        }

        Column(modifier = Modifier.weight(1f)) {
            Text(
                text = title,
                color = if (isDestructive) CopilotTheme.StatusDanger else CopilotTheme.TextBright,
                fontSize = 13.5.sp,
                fontWeight = FontWeight.SemiBold,
                fontFamily = CopilotTheme.MonoFont
            )
            Text(
                text = subtitle,
                color = CopilotTheme.TextMuted,
                fontSize = 11.sp,
                fontFamily = CopilotTheme.MonoFont
            )
        }
    }
}
