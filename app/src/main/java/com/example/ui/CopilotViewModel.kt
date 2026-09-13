package com.example.ui

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.data.api.GeminiApiClient
import com.example.data.model.AttachmentItem
import com.example.data.model.ChatMessage
import com.example.data.model.FeedbackState
import com.example.data.model.MessageBranch
import com.example.data.model.MessageSender
import com.example.data.model.SourceReference
import kotlinx.coroutines.Job
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale
import java.util.UUID

class CopilotViewModel : ViewModel() {

    private val _messages = MutableStateFlow<List<ChatMessage>>(emptyList())
    val messages: StateFlow<List<ChatMessage>> = _messages.asStateFlow()

    private val _inputText = MutableStateFlow("")
    val inputText: StateFlow<String> = _inputText.asStateFlow()

    private val _isGenerating = MutableStateFlow(false)
    val isGenerating: StateFlow<Boolean> = _isGenerating.asStateFlow()

    private val _activeReasoning = MutableStateFlow<String?>(null)
    val activeReasoning: StateFlow<String?> = _activeReasoning.asStateFlow()

    private val _selectedModel = MutableStateFlow(GeminiApiClient.MODEL_PRO_THINKING)
    val selectedModel: StateFlow<String> = _selectedModel.asStateFlow()

    private val _thinkingEnabled = MutableStateFlow(true)
    val thinkingEnabled: StateFlow<Boolean> = _thinkingEnabled.asStateFlow()

    private val _searchEnabled = MutableStateFlow(false)
    val searchEnabled: StateFlow<Boolean> = _searchEnabled.asStateFlow()

    private val _pendingAttachments = MutableStateFlow<List<AttachmentItem>>(emptyList())
    val pendingAttachments: StateFlow<List<AttachmentItem>> = _pendingAttachments.asStateFlow()

    private val _editingMessageId = MutableStateFlow<String?>(null)
    val editingMessageId: StateFlow<String?> = _editingMessageId.asStateFlow()

    private val _editingText = MutableStateFlow("")
    val editingText: StateFlow<String> = _editingText.asStateFlow()

    private var currentStreamJob: Job? = null
    @Volatile
    private var isCancelledFlag = false

    private fun currentTime(): String {
        return SimpleDateFormat("HH:mm", Locale.getDefault()).format(Date())
    }

    fun onInputTextChange(newText: String) {
        _inputText.value = newText
    }

    fun setModel(model: String) {
        _selectedModel.value = model
    }

    fun toggleThinking() {
        _thinkingEnabled.value = !_thinkingEnabled.value
    }

    fun toggleSearch() {
        _searchEnabled.value = !_searchEnabled.value
    }

    fun addSampleAttachment() {
        val count = _pendingAttachments.value.size + 1
        val sample = AttachmentItem(
            name = "CopilotListState.kt",
            size = "${count * 1.4} KB",
            mimeType = "text/x-kotlin",
            previewContent = """
                // Jetpack Compose 120 FPS List State Definition
                val chatListState = rememberLazyListState()
                val isAtBottom by remember {
                    derivedStateOf { chatListState.firstVisibleItemIndex == 0 }
                }
            """.trimIndent()
        )
        _pendingAttachments.value = _pendingAttachments.value + sample
    }

    fun removeAttachment(id: String) {
        _pendingAttachments.value = _pendingAttachments.value.filter { it.id != id }
    }

    fun sendMessage(content: String? = null) {
        val textToSend = content ?: _inputText.value
        if (textToSend.isBlank() && _pendingAttachments.value.isEmpty()) return
        if (_isGenerating.value) return

        val attachmentsToSend = _pendingAttachments.value
        _inputText.value = ""
        _pendingAttachments.value = emptyList()

        val userMessage = ChatMessage(
            sender = MessageSender.USER,
            branches = listOf(
                MessageBranch(
                    content = textToSend,
                    timestamp = currentTime()
                )
            ),
            attachments = attachmentsToSend
        )

        val assistantMessageId = UUID.randomUUID().toString()
        val assistantPlaceholder = ChatMessage(
            id = assistantMessageId,
            sender = MessageSender.ASSISTANT,
            branches = listOf(
                MessageBranch(
                    content = "",
                    reasoning = null,
                    timestamp = currentTime()
                )
            )
        )

        _messages.value = _messages.value + userMessage + assistantPlaceholder

        executeGeneration(
            prompt = textToSend,
            attachments = attachmentsToSend,
            assistantMessageId = assistantMessageId,
            isNewBranch = false
        )
    }

    fun reloadMessage(assistantMessageId: String) {
        if (_isGenerating.value) return
        val currentList = _messages.value
        val msgIndex = currentList.indexOfFirst { it.id == assistantMessageId }
        if (msgIndex <= 0) return

        // Find the previous user prompt
        val prevUserMsg = currentList.subList(0, msgIndex).lastOrNull { it.sender == MessageSender.USER }
        val prompt = prevUserMsg?.activeBranch?.content ?: "Erneut generieren"
        val attachments = prevUserMsg?.attachments ?: emptyList()

        // Create a new branch on the assistant message
        val targetMsg = currentList[msgIndex]
        val newBranch = MessageBranch(
            content = "",
            reasoning = null,
            timestamp = currentTime()
        )
        val updatedBranches = targetMsg.branches + newBranch
        val updatedMsg = targetMsg.copy(
            branches = updatedBranches,
            currentBranchIndex = updatedBranches.size - 1
        )

        _messages.value = currentList.toMutableList().also { it[msgIndex] = updatedMsg }

        executeGeneration(
            prompt = prompt,
            attachments = attachments,
            assistantMessageId = assistantMessageId,
            isNewBranch = true
        )
    }

    private fun executeGeneration(
        prompt: String,
        attachments: List<AttachmentItem>,
        assistantMessageId: String,
        isNewBranch: Boolean
    ) {
        isCancelledFlag = false
        _isGenerating.value = true
        _activeReasoning.value = null

        val historyList = _messages.value
            .filter { it.id != assistantMessageId && it.activeBranch.content.isNotBlank() }
            .map {
                (if (it.sender == MessageSender.USER) "user" else "model") to it.activeBranch.content
            }

        currentStreamJob = viewModelScope.launch {
            try {
                val contentBuffer = StringBuilder()
                val reasoningBuffer = StringBuilder()

                GeminiApiClient.streamGenerate(
                    model = _selectedModel.value,
                    prompt = prompt,
                    attachments = attachments,
                    history = historyList,
                    useThinking = _thinkingEnabled.value,
                    useSearch = _searchEnabled.value,
                    onThoughtChunk = { chunk ->
                        reasoningBuffer.append(chunk)
                        _activeReasoning.value = reasoningBuffer.toString()
                        updateAssistantBranch(assistantMessageId) { branch ->
                            branch.copy(reasoning = reasoningBuffer.toString())
                        }
                    },
                    onContentChunk = { chunk ->
                        contentBuffer.append(chunk)
                        updateAssistantBranch(assistantMessageId) { branch ->
                            branch.copy(
                                content = contentBuffer.toString(),
                                reasoning = reasoningBuffer.toString().ifBlank { null }
                            )
                        }
                    },
                    onSources = { sources ->
                        updateAssistantBranch(assistantMessageId) { branch ->
                            branch.copy(sources = sources)
                        }
                    },
                    onFollowUps = { followUps ->
                        updateAssistantBranch(assistantMessageId) { branch ->
                            branch.copy(followUps = followUps)
                        }
                    },
                    isCancelled = { isCancelledFlag }
                )
            } finally {
                _isGenerating.value = false
                _activeReasoning.value = null
            }
        }
    }

    private fun updateAssistantBranch(
        messageId: String,
        transform: (MessageBranch) -> MessageBranch
    ) {
        _messages.value = _messages.value.map { msg ->
            if (msg.id == messageId) {
                val branchIdx = msg.currentBranchIndex.coerceIn(0, (msg.branches.size - 1).coerceAtLeast(0))
                val currentBranch = msg.branches.getOrNull(branchIdx) ?: return@map msg
                val updatedBranch = transform(currentBranch)
                val newBranches = msg.branches.toMutableList().also { it[branchIdx] = updatedBranch }
                msg.copy(branches = newBranches)
            } else {
                msg
            }
        }
    }

    fun stopGeneration() {
        isCancelledFlag = true
        currentStreamJob?.cancel()
        _isGenerating.value = false
        _activeReasoning.value = null
    }

    fun switchBranch(messageId: String, newIndex: Int) {
        _messages.value = _messages.value.map { msg ->
            if (msg.id == messageId && newIndex in msg.branches.indices) {
                msg.copy(currentBranchIndex = newIndex)
            } else {
                msg
            }
        }
    }

    fun updateFeedback(messageId: String, feedback: FeedbackState) {
        updateAssistantBranch(messageId) { branch ->
            branch.copy(feedback = feedback)
        }
    }

    fun startEditing(messageId: String) {
        val msg = _messages.value.find { it.id == messageId } ?: return
        _editingMessageId.value = messageId
        _editingText.value = msg.activeBranch.content
    }

    fun onEditingTextChange(newText: String) {
        _editingText.value = newText
    }

    fun cancelEditing() {
        _editingMessageId.value = null
        _editingText.value = ""
    }

    fun saveEdit(messageId: String) {
        val newText = _editingText.value.trim()
        if (newText.isEmpty()) return

        val currentList = _messages.value
        val msgIndex = currentList.indexOfFirst { it.id == messageId }
        if (msgIndex == -1) return

        val originalMsg = currentList[msgIndex]
        val newBranch = MessageBranch(
            content = newText,
            timestamp = currentTime()
        )
        val updatedBranches = originalMsg.branches + newBranch
        val updatedUserMsg = originalMsg.copy(
            branches = updatedBranches,
            currentBranchIndex = updatedBranches.size - 1
        )

        // Remove subsequent messages and trigger regeneration
        val truncatedList = currentList.subList(0, msgIndex + 1).toMutableList()
        truncatedList[msgIndex] = updatedUserMsg

        val assistantMessageId = UUID.randomUUID().toString()
        val assistantPlaceholder = ChatMessage(
            id = assistantMessageId,
            sender = MessageSender.ASSISTANT,
            branches = listOf(
                MessageBranch(
                    content = "",
                    reasoning = null,
                    timestamp = currentTime()
                )
            )
        )
        truncatedList.add(assistantPlaceholder)
        _messages.value = truncatedList

        _editingMessageId.value = null
        _editingText.value = ""

        executeGeneration(
            prompt = newText,
            attachments = updatedUserMsg.attachments,
            assistantMessageId = assistantMessageId,
            isNewBranch = false
        )
    }

    fun clearChat() {
        stopGeneration()
        _messages.value = emptyList()
    }

    fun enhancePrompt(text: String) {
        if (text.isBlank()) return
        _inputText.value = "Enhance this prompt: $text"
    }
}
