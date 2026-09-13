package com.example.ui

import android.widget.Toast
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.scaleIn
import androidx.compose.animation.scaleOut
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.ExperimentalLayoutApi
import androidx.compose.foundation.layout.FlowRow
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.ime
import androidx.compose.foundation.layout.imePadding
import androidx.compose.foundation.layout.navigationBars
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.statusBars
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.layout.widthIn
import androidx.compose.foundation.layout.windowInsetsPadding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.BasicTextField
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.DeleteOutline
import androidx.compose.material.icons.filled.Edit
import androidx.compose.material.icons.filled.KeyboardArrowDown
import androidx.compose.material.icons.filled.Language
import androidx.compose.material.icons.filled.Person
import androidx.compose.material.icons.filled.Psychology
import androidx.compose.material.icons.filled.SmartToy
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.DropdownMenu
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.derivedStateOf
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.SolidColor
import androidx.compose.ui.platform.LocalClipboardManager
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.AnnotatedString
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.data.api.GeminiApiClient
import com.example.data.model.ChatMessage
import com.example.data.model.MessageSender
import com.example.ui.components.AssistantActionBar
import com.example.ui.components.CopilotComposer
import com.example.ui.components.MarkdownCodeRenderer
import com.example.ui.components.OfficialCopilotBadge
import com.example.ui.components.ReasoningAccordion
import com.example.ui.components.SourcesListView
import com.example.ui.components.WelcomeThreadView
import com.example.ui.theme.CopilotTheme
import kotlinx.coroutines.launch

@OptIn(ExperimentalLayoutApi::class)
@Composable
fun CopilotChatScreen(
    viewModel: CopilotViewModel = viewModel(),
    modifier: Modifier = Modifier
) {
    val messages by viewModel.messages.collectAsStateWithLifecycle()
    val inputText by viewModel.inputText.collectAsStateWithLifecycle()
    val isGenerating by viewModel.isGenerating.collectAsStateWithLifecycle()
    val activeReasoning by viewModel.activeReasoning.collectAsStateWithLifecycle()
    val selectedModel by viewModel.selectedModel.collectAsStateWithLifecycle()
    val thinkingEnabled by viewModel.thinkingEnabled.collectAsStateWithLifecycle()
    val searchEnabled by viewModel.searchEnabled.collectAsStateWithLifecycle()
    val pendingAttachments by viewModel.pendingAttachments.collectAsStateWithLifecycle()
    val editingMessageId by viewModel.editingMessageId.collectAsStateWithLifecycle()
    val editingText by viewModel.editingText.collectAsStateWithLifecycle()

    val listState = rememberLazyListState()
    val coroutineScope = rememberCoroutineScope()
    val clipboardManager = LocalClipboardManager.current
    val context = LocalContext.current

    // Auto-scroll to bottom on message update
    LaunchedEffect(messages.size, messages.lastOrNull()?.activeBranch?.content?.length) {
        if (messages.isNotEmpty()) {
            listState.animateScrollToItem(messages.size - 1)
        }
    }

    // Scroll to bottom button visibility (120 FPS derived state)
    val showScrollToBottom by remember {
        derivedStateOf {
            val totalItems = listState.layoutInfo.totalItemsCount
            if (totalItems <= 1) false
            else {
                val lastVisibleItemIndex = listState.layoutInfo.visibleItemsInfo.lastOrNull()?.index ?: 0
                lastVisibleItemIndex < totalItems - 2
            }
        }
    }

    Scaffold(
        modifier = modifier
            .fillMaxSize()
            .background(CopilotTheme.CanvasDark),
        containerColor = CopilotTheme.CanvasDark,
        contentWindowInsets = WindowInsets(0, 0, 0, 0)
    ) { innerPadding ->
        Box(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding)
                .windowInsetsPadding(WindowInsets.statusBars)
        ) {
            // Constrain center viewport to max 56rem (896dp) as required by prompt
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .widthIn(max = 896.dp)
                    .align(Alignment.TopCenter)
            ) {
                // Top Copilot App Bar
                CopilotHeader(
                    thinkingEnabled = thinkingEnabled,
                    searchEnabled = searchEnabled,
                    onToggleThinking = { viewModel.toggleThinking() },
                    onToggleSearch = { viewModel.toggleSearch() }
                )

                // Message Viewport / Chat thread
                Box(
                    modifier = Modifier
                        .weight(1f)
                        .fillMaxWidth()
                ) {
                    if (messages.isEmpty()) {
                        WelcomeThreadView(
                            onSelectSuggestion = { suggestion ->
                                viewModel.sendMessage(suggestion)
                            },
                            modifier = Modifier.fillMaxSize()
                        )
                    } else {
                        LazyColumn(
                            state = listState,
                            modifier = Modifier
                                .fillMaxSize()
                                .padding(horizontal = 14.dp),
                            verticalArrangement = Arrangement.spacedBy(16.dp)
                        ) {
                            item(key = "header_spacer") {
                                Spacer(modifier = Modifier.height(6.dp))
                            }

                            items(
                                items = messages,
                                key = { it.id }
                            ) { message ->
                                ChatMessageRow(
                                    message = message,
                                    isCurrentlyGenerating = isGenerating && message.id == messages.lastOrNull()?.id,
                                    activeReasoning = if (message.id == messages.lastOrNull()?.id) activeReasoning else null,
                                    isEditing = message.id == editingMessageId,
                                    editingText = editingText,
                                    onEditingTextChange = { viewModel.onEditingTextChange(it) },
                                    onSaveEdit = { viewModel.saveEdit(message.id) },
                                    onCancelEdit = { viewModel.cancelEditing() },
                                    onStartEdit = { viewModel.startEditing(message.id) },
                                    onBranchPrev = {
                                        viewModel.switchBranch(message.id, message.currentBranchIndex - 1)
                                    },
                                    onBranchNext = {
                                        viewModel.switchBranch(message.id, message.currentBranchIndex + 1)
                                    },
                                    onCopy = {
                                        clipboardManager.setText(AnnotatedString(message.activeBranch.content))
                                        Toast.makeText(context, "Kopiert", Toast.LENGTH_SHORT).show()
                                    },
                                    onReload = {
                                        viewModel.reloadMessage(message.id)
                                    },
                                    onFeedback = { feedback ->
                                        viewModel.updateFeedback(message.id, feedback)
                                    },
                                    onSelectFollowUp = { followUp ->
                                        viewModel.sendMessage(followUp)
                                    }
                                )
                            }

                            item(key = "footer_spacer") {
                                Spacer(modifier = Modifier.height(12.dp))
                            }
                        }
                    }

                    // Floating Scroll-to-Bottom Button
                    androidx.compose.animation.AnimatedVisibility(
                        visible = showScrollToBottom,
                        enter = scaleIn() + fadeIn(),
                        exit = scaleOut() + fadeOut(),
                        modifier = Modifier
                            .align(Alignment.BottomEnd)
                            .padding(end = 16.dp, bottom = 12.dp)
                    ) {
                        FloatingActionButton(
                            onClick = {
                                coroutineScope.launch {
                                    if (messages.isNotEmpty()) {
                                        listState.animateScrollToItem(messages.size - 1)
                                    }
                                }
                            },
                            containerColor = CopilotTheme.PanelDark,
                            contentColor = CopilotTheme.PurpleLight,
                            shape = CircleShape,
                            modifier = Modifier
                                .size(38.dp)
                                .border(0.8.dp, CopilotTheme.BorderSubtle, CircleShape)
                        ) {
                            Icon(
                                imageVector = Icons.Default.KeyboardArrowDown,
                                contentDescription = "Nach unten scrollen",
                                modifier = Modifier.size(20.dp)
                            )
                        }
                    }
                }

                // Bottom Composer with Keyboard IME and Navigation Bar Inset Handling
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .imePadding()
                        .windowInsetsPadding(WindowInsets.navigationBars)
                        .padding(horizontal = 14.dp, vertical = 8.dp)
                ) {
                    CopilotComposer(
                        text = inputText,
                        onTextChange = { viewModel.onInputTextChange(it) },
                        onEnhancePrompt = {
                            viewModel.enhancePrompt(inputText)
                        },
                        isGenerating = isGenerating,
                        onSend = { viewModel.sendMessage() },
                        onStop = { viewModel.stopGeneration() }
                    )
                }
            }
        }
    }
}

@Composable
fun CopilotHeader(
    thinkingEnabled: Boolean,
    searchEnabled: Boolean,
    onToggleThinking: () -> Unit,
    onToggleSearch: () -> Unit,
    modifier: Modifier = Modifier
) {
    Row(
        modifier = modifier
            .fillMaxWidth()
            .background(CopilotTheme.PanelHeaderDark)
            .border(0.5.dp, CopilotTheme.BorderSubtle)
            .padding(horizontal = 14.dp, vertical = 9.dp),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.SpaceBetween
    ) {
        // Left: Logo
        Row(verticalAlignment = Alignment.CenterVertically) {
            OfficialCopilotBadge(size = 26.dp)

            Spacer(modifier = Modifier.width(8.dp))

            Text(
                text = "Copilot",
                color = CopilotTheme.TextBright,
                fontSize = 15.sp,
                fontWeight = FontWeight.Bold,
                fontFamily = CopilotTheme.MonoFont
            )
        }

        // Right controls: Thinking toggle, Search Grounding toggle
        Row(
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(4.dp)
        ) {
            // Thinking Toggle Button
            IconButton(
                onClick = onToggleThinking,
                modifier = Modifier
                    .size(28.dp)
                    .clip(CircleShape)
                    .background(if (thinkingEnabled) CopilotTheme.PurpleBadgeBg else Color.Transparent)
            ) {
                Icon(
                    imageVector = Icons.Default.Psychology,
                    contentDescription = "Thinking Mode",
                    tint = if (thinkingEnabled) CopilotTheme.PurpleLight else CopilotTheme.TextFaint,
                    modifier = Modifier.size(16.dp)
                )
            }

            // Search Grounding Toggle Button
            IconButton(
                onClick = onToggleSearch,
                modifier = Modifier
                    .size(28.dp)
                    .clip(CircleShape)
                    .background(if (searchEnabled) CopilotTheme.PurpleBadgeBg else Color.Transparent)
            ) {
                Icon(
                    imageVector = Icons.Default.Language,
                    contentDescription = "Search Grounding",
                    tint = if (searchEnabled) CopilotTheme.LinkBlue else CopilotTheme.TextFaint,
                    modifier = Modifier.size(16.dp)
                )
            }
        }
    }
}

@OptIn(ExperimentalLayoutApi::class)
@Composable
fun ChatMessageRow(
    message: ChatMessage,
    isCurrentlyGenerating: Boolean,
    activeReasoning: String?,
    isEditing: Boolean,
    editingText: String,
    onEditingTextChange: (String) -> Unit,
    onSaveEdit: () -> Unit,
    onCancelEdit: () -> Unit,
    onStartEdit: () -> Unit,
    onBranchPrev: () -> Unit,
    onBranchNext: () -> Unit,
    onCopy: () -> Unit,
    onReload: () -> Unit,
    onFeedback: (com.example.data.model.FeedbackState) -> Unit,
    onSelectFollowUp: (String) -> Unit,
    modifier: Modifier = Modifier
) {
    val branch = message.activeBranch

    if (message.sender == MessageSender.USER) {
        // User Message: Rounded bubble aligned right
        Row(
            modifier = modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.End
        ) {
            Column(
                horizontalAlignment = Alignment.End,
                modifier = Modifier.fillMaxWidth(0.88f)
            ) {
                if (isEditing) {
                    // In-place edit card
                    Column(
                        modifier = Modifier
                            .fillMaxWidth()
                            .clip(RoundedCornerShape(12.dp))
                            .background(CopilotTheme.UserBubble)
                            .border(1.dp, CopilotTheme.PurplePrimary, RoundedCornerShape(12.dp))
                            .padding(10.dp)
                    ) {
                        BasicTextField(
                            value = editingText,
                            onValueChange = onEditingTextChange,
                            textStyle = TextStyle(
                                color = CopilotTheme.TextBright,
                                fontSize = 13.sp,
                                fontFamily = CopilotTheme.MonoFont,
                                lineHeight = 19.sp
                            ),
                            cursorBrush = SolidColor(CopilotTheme.PurpleLight),
                            modifier = Modifier.fillMaxWidth()
                        )

                        Spacer(modifier = Modifier.height(8.dp))

                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.End,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            OutlinedButton(
                                onClick = onCancelEdit,
                                colors = ButtonDefaults.outlinedButtonColors(contentColor = CopilotTheme.TextMuted),
                                border = androidx.compose.foundation.BorderStroke(0.5.dp, CopilotTheme.BorderSubtle),
                                shape = RoundedCornerShape(6.dp),
                                modifier = Modifier.height(28.dp)
                            ) {
                                Text("Cancel", fontSize = 11.sp, fontFamily = CopilotTheme.MonoFont)
                            }

                            Spacer(modifier = Modifier.width(6.dp))

                            Button(
                                onClick = onSaveEdit,
                                colors = ButtonDefaults.buttonColors(containerColor = CopilotTheme.PurplePrimary),
                                shape = RoundedCornerShape(6.dp),
                                modifier = Modifier.height(28.dp)
                            ) {
                                Text("Save & Submit", fontSize = 11.sp, fontFamily = CopilotTheme.MonoFont)
                            }
                        }
                    }
                } else {
                    // Regular User Bubble
                    Box(
                        modifier = Modifier
                            .clip(
                                RoundedCornerShape(
                                    topStart = 14.dp,
                                    topEnd = 14.dp,
                                    bottomStart = 14.dp,
                                    bottomEnd = 3.dp
                                )
                            )
                            .background(CopilotTheme.UserBubble)
                            .border(
                                width = 0.6.dp,
                                color = CopilotTheme.UserBubbleBorder,
                                shape = RoundedCornerShape(
                                    topStart = 14.dp,
                                    topEnd = 14.dp,
                                    bottomStart = 14.dp,
                                    bottomEnd = 3.dp
                                )
                            )
                            .clickable { onStartEdit() }
                            .padding(horizontal = 14.dp, vertical = 10.dp)
                    ) {
                        Column {
                            Text(
                                text = branch.content,
                                color = CopilotTheme.TextBright,
                                fontSize = 13.5.sp,
                                lineHeight = 19.sp,
                                fontFamily = CopilotTheme.MonoFont
                            )

                            Spacer(modifier = Modifier.height(4.dp))

                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                horizontalArrangement = Arrangement.SpaceBetween,
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Row(verticalAlignment = Alignment.CenterVertically) {
                                    Icon(
                                        imageVector = Icons.Default.Edit,
                                        contentDescription = "Bearbeiten",
                                        tint = CopilotTheme.TextFaint,
                                        modifier = Modifier.size(11.dp)
                                    )
                                    Spacer(modifier = Modifier.width(3.dp))
                                    Text(
                                        text = "edit",
                                        color = CopilotTheme.TextFaint,
                                        fontSize = 10.sp,
                                        fontFamily = CopilotTheme.MonoFont
                                    )
                                }

                                Text(
                                    text = branch.timestamp,
                                    color = CopilotTheme.TextFaint,
                                    fontSize = 10.sp,
                                    fontFamily = CopilotTheme.MonoFont
                                )
                            }
                        }
                    }
                }
            }
        }
    } else {
        // Assistant Message: Aligned left with Copilot avatar, Reasoning, Markdown, Citations & Action bar
        Row(
            modifier = modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.Start
        ) {
            OfficialCopilotBadge(
                size = 28.dp,
                modifier = Modifier.padding(top = 2.dp)
            )

            Spacer(modifier = Modifier.width(10.dp))

            Column(
                modifier = Modifier.weight(1f),
                verticalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                // Header row: Copilot name and timestamp
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(6.dp)
                ) {
                    Text(
                        text = "GitHub Copilot",
                        color = CopilotTheme.TextBright,
                        fontSize = 12.5.sp,
                        fontWeight = FontWeight.SemiBold,
                        fontFamily = CopilotTheme.MonoFont
                    )
                    Text(
                        text = branch.timestamp,
                        color = CopilotTheme.TextFaint,
                        fontSize = 10.sp,
                        fontFamily = CopilotTheme.MonoFont
                    )
                }

                // Reasoning Accordion (if reasoning exists or active thinking)
                val reasoningToShow = activeReasoning ?: branch.reasoning
                if (!reasoningToShow.isNullOrBlank()) {
                    ReasoningAccordion(
                        reasoningText = reasoningToShow,
                        isThinkingActive = isCurrentlyGenerating && branch.content.isBlank()
                    )
                }

                // Main Markdown & Code Output
                if (branch.content.isNotBlank()) {
                    MarkdownCodeRenderer(content = branch.content)
                } else if (isCurrentlyGenerating) {
                    // Typing indicator
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.spacedBy(4.dp),
                        modifier = Modifier.padding(vertical = 4.dp)
                    ) {
                        Box(
                            modifier = Modifier
                                .size(6.dp)
                                .clip(CircleShape)
                                .background(CopilotTheme.PurpleLight)
                        )
                        Text(
                            text = "Thinking...",
                            color = CopilotTheme.PurpleLight,
                            fontSize = 12.sp,
                            fontFamily = CopilotTheme.MonoFont
                        )
                    }
                }

                // Citations / Sources list
                if (branch.sources.isNotEmpty()) {
                    SourcesListView(sources = branch.sources)
                }

                // Follow-up suggestion pills
                if (branch.followUps.isNotEmpty() && !isCurrentlyGenerating) {
                    Column(verticalArrangement = Arrangement.spacedBy(4.dp)) {
                        Text(
                            text = "FOLLOW-UPS",
                            color = CopilotTheme.TextFaint,
                            fontSize = 10.sp,
                            fontWeight = FontWeight.Bold,
                            fontFamily = CopilotTheme.MonoFont,
                            letterSpacing = 1.sp
                        )
                        FlowRow(
                            horizontalArrangement = Arrangement.spacedBy(6.dp),
                            verticalArrangement = Arrangement.spacedBy(6.dp)
                        ) {
                            branch.followUps.forEach { suggestion ->
                                Row(
                                    modifier = Modifier
                                        .clip(RoundedCornerShape(12.dp))
                                        .background(CopilotTheme.PanelDark)
                                        .border(0.6.dp, CopilotTheme.PurplePrimary.copy(alpha = 0.5f), RoundedCornerShape(12.dp))
                                    .clickable { onSelectFollowUp(suggestion) }
                                    .padding(horizontal = 10.dp, vertical = 5.dp),
                                    verticalAlignment = Alignment.CenterVertically
                                ) {
                                    Text(
                                        text = "✦ $suggestion",
                                        color = CopilotTheme.PurpleLight,
                                        fontSize = 11.sp,
                                        fontFamily = CopilotTheme.MonoFont
                                    )
                                }
                            }
                        }
                    }
                }

                // Action Bar (Branching, Copy, Reload, Feedback)
                if (branch.content.isNotBlank() && !isCurrentlyGenerating) {
                    AssistantActionBar(
                        totalBranches = message.branches.size,
                        currentBranchIndex = message.currentBranchIndex,
                        feedbackState = branch.feedback,
                        onBranchPrev = onBranchPrev,
                        onBranchNext = onBranchNext,
                        onCopy = onCopy,
                        onReload = onReload,
                        onFeedback = onFeedback
                    )
                }
            }
        }
    }
}
