package com.example.data.model

import androidx.compose.runtime.Immutable
import java.util.UUID

@Immutable
data class CopilotConfig(
    val attachments: Boolean = true,
    val branchPicker: Boolean = true,
    val editMessage: Boolean = true,
    val actionBarCopy: Boolean = true,
    val actionBarReload: Boolean = true,
    val actionBarSpeak: Boolean = false,
    val actionBarFeedback: Boolean = true,
    val threadWelcome: Boolean = true,
    val suggestions: Boolean = true,
    val scrollToBottom: Boolean = true,
    val markdown: Boolean = true,
    val codeHighlightTheme: String = "github",
    val reasoning: Boolean = true,
    val sources: Boolean = true,
    val followUpSuggestions: Boolean = true,
    val avatar: Boolean = true,
    val typingIndicator: String = "dot",
    val loadingIndicator: String = "text",
    val loadingText: String = "Thinking..."
)

@Immutable
data class SourceReference(
    val id: String = UUID.randomUUID().toString(),
    val title: String,
    val url: String,
    val snippet: String? = null
)

@Immutable
data class AttachmentItem(
    val id: String = UUID.randomUUID().toString(),
    val name: String,
    val size: String,
    val mimeType: String = "text/plain",
    val previewContent: String? = null
)

enum class FeedbackState {
    NONE,
    THUMBS_UP,
    THUMBS_DOWN
}

enum class MessageSender {
    USER,
    ASSISTANT
}

@Immutable
data class MessageBranch(
    val id: String = UUID.randomUUID().toString(),
    val content: String,
    val reasoning: String? = null,
    val sources: List<SourceReference> = emptyList(),
    val followUps: List<String> = emptyList(),
    val feedback: FeedbackState = FeedbackState.NONE,
    val timestamp: String
)

@Immutable
data class ChatMessage(
    val id: String = UUID.randomUUID().toString(),
    val sender: MessageSender,
    val branches: List<MessageBranch>,
    val currentBranchIndex: Int = 0,
    val attachments: List<AttachmentItem> = emptyList()
) {
    val activeBranch: MessageBranch
        get() = branches.getOrNull(currentBranchIndex) ?: branches.firstOrNull() ?: MessageBranch(
            content = "",
            timestamp = ""
        )
}
