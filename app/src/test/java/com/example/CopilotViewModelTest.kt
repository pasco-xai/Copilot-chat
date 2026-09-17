package com.example

import com.example.data.model.FeedbackState
import com.example.data.model.MessageSender
import com.example.ui.CopilotViewModel
import org.junit.Assert.assertEquals
import org.junit.Assert.assertNotNull
import org.junit.Assert.assertTrue
import org.junit.Before
import org.junit.Test
import org.junit.runner.RunWith
import org.robolectric.RobolectricTestRunner
import org.robolectric.annotation.Config

@RunWith(RobolectricTestRunner::class)
@Config(sdk = [36])
class CopilotViewModelTest {

    private lateinit var viewModel: CopilotViewModel

    @Before
    fun setup() {
        viewModel = CopilotViewModel()
    }

    @Test
    fun `initial state is empty`() {
        assertTrue(viewModel.messages.value.isEmpty())
        assertEquals("", viewModel.inputText.value)
        assertTrue(viewModel.pendingAttachments.value.isEmpty())
    }

    @Test
    fun `add sample attachment updates pending attachments`() {
        viewModel.addSampleAttachment()
        assertEquals(1, viewModel.pendingAttachments.value.size)
        assertEquals("CopilotListState.kt", viewModel.pendingAttachments.value.first().name)
    }

    @Test
    fun `sendMessage creates user message and assistant placeholder`() {
        viewModel.onInputTextChange("Explain Compose stability")
        viewModel.sendMessage()

        val messages = viewModel.messages.value
        assertEquals(2, messages.size)
        assertEquals(MessageSender.USER, messages[0].sender)
        assertEquals("Explain Compose stability", messages[0].activeBranch.content)
        assertEquals(MessageSender.ASSISTANT, messages[1].sender)
    }

    @Test
    fun `branching and feedback state management`() {
        viewModel.onInputTextChange("Test prompt")
        viewModel.sendMessage()

        val assistantId = viewModel.messages.value.last().id
        viewModel.updateFeedback(assistantId, FeedbackState.THUMBS_UP)

        val updatedAssistant = viewModel.messages.value.last()
        assertEquals(FeedbackState.THUMBS_UP, updatedAssistant.activeBranch.feedback)
    }

    @Test
    fun `litert-lm model selection and offline hardware settings`() {
        viewModel.setModel(com.example.data.api.GeminiApiClient.MODEL_GEMMA_LITERTLM)
        assertEquals(com.example.data.api.GeminiApiClient.MODEL_GEMMA_LITERTLM, viewModel.selectedModel.value)

        viewModel.setHardwareBackend(com.example.data.litert.LiteRtLmEngineManager.HardwareBackend.GPU_OPENCL)
        assertEquals(com.example.data.litert.LiteRtLmEngineManager.HardwareBackend.GPU_OPENCL, viewModel.hardwareBackend.value)

        viewModel.setKvCacheLimit(2048)
        assertEquals(2048, viewModel.kvCacheLimit.value)
    }

    @Test
    fun `litert-lm benchmark execution formats progress string safely`() = kotlinx.coroutines.test.runTest {
        var lastProgress = ""
        val result = com.example.data.litert.LiteRtLmEngineManager.runBenchmark { progress ->
            lastProgress = progress
        }
        assertNotNull(result)
        assertTrue(result.decodeSpeedTokPerSec > 0f)
        assertTrue(lastProgress.contains("Decoding: 100%"))
    }
}
