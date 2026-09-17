package com.example.ui

import android.net.Uri
import android.widget.Toast
import androidx.activity.compose.BackHandler
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.ExperimentalLayoutApi
import androidx.compose.foundation.layout.FlowRow
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.statusBars
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.layout.widthIn
import androidx.compose.foundation.layout.windowInsetsPadding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.Architecture
import androidx.compose.material.icons.filled.Bolt
import androidx.compose.material.icons.filled.BugReport
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.filled.CloudOff
import androidx.compose.material.icons.filled.Code
import androidx.compose.material.icons.filled.DeleteOutline
import androidx.compose.material.icons.filled.Engineering
import androidx.compose.material.icons.filled.FlashOn
import androidx.compose.material.icons.filled.Language
import androidx.compose.material.icons.filled.Memory
import androidx.compose.material.icons.filled.PlayArrow
import androidx.compose.material.icons.filled.Psychology
import androidx.compose.material.icons.filled.RestartAlt
import androidx.compose.material.icons.filled.School
import androidx.compose.material.icons.filled.SmartToy
import androidx.compose.material.icons.filled.Speed
import androidx.compose.material.icons.filled.Storage
import androidx.compose.material.icons.filled.Tune
import androidx.compose.material.icons.outlined.Info
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.FilterChip
import androidx.compose.material3.FilterChipDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Slider
import androidx.compose.material3.SliderDefaults
import androidx.compose.material3.Switch
import androidx.compose.material3.SwitchDefaults
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.example.data.api.GeminiApiClient
import com.example.data.litert.LiteRtLmEngineManager
import com.example.ui.theme.CopilotTheme

@OptIn(ExperimentalLayoutApi::class)
@Composable
fun SettingsScreen(
    viewModel: CopilotViewModel,
    onNavigateBack: () -> Unit,
    modifier: Modifier = Modifier
) {
    BackHandler(onBack = onNavigateBack)

    val thinkingEnabled by viewModel.thinkingEnabled.collectAsStateWithLifecycle()
    val searchEnabled by viewModel.searchEnabled.collectAsStateWithLifecycle()
    val selectedModel by viewModel.selectedModel.collectAsStateWithLifecycle()
    val temperature by viewModel.temperature.collectAsStateWithLifecycle()
    val thinkingBudget by viewModel.thinkingBudget.collectAsStateWithLifecycle()
    val selectedPersona by viewModel.selectedPersona.collectAsStateWithLifecycle()
    val customSystemPrompt by viewModel.customSystemPrompt.collectAsStateWithLifecycle()
    val codeStyle by viewModel.codeStyle.collectAsStateWithLifecycle()
    val includeUnitTests by viewModel.includeUnitTests.collectAsStateWithLifecycle()
    val maxOutputTokens by viewModel.maxOutputTokens.collectAsStateWithLifecycle()

    // LiteRT-LM Gemma 4 States
    val hardwareBackend by viewModel.hardwareBackend.collectAsStateWithLifecycle()
    val kvCacheLimit by viewModel.kvCacheLimit.collectAsStateWithLifecycle()
    val modelInstalled by viewModel.modelInstalled.collectAsStateWithLifecycle()
    val lastBenchmark by viewModel.lastBenchmark.collectAsStateWithLifecycle()
    val isBenchmarking by viewModel.isBenchmarking.collectAsStateWithLifecycle()
    val benchmarkProgress by viewModel.benchmarkProgress.collectAsStateWithLifecycle()
    val selectedModelPath by viewModel.selectedModelPath.collectAsStateWithLifecycle()
    val selectedModelName by viewModel.selectedModelName.collectAsStateWithLifecycle()

    val context = LocalContext.current
    var showClearDialog by remember { mutableStateOf(false) }
    val scrollState = rememberScrollState()

    val filePickerLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.OpenDocument()
    ) { uri ->
        if (uri != null) {
            try {
                context.contentResolver.takePersistableUriPermission(
                    uri,
                    android.content.Intent.FLAG_GRANT_READ_URI_PERMISSION
                )
            } catch (_: Exception) {}
            val fileName = uri.lastPathSegment?.substringAfterLast('/') ?: "gemma-4-E2B-it-litert-lm.litertlm"
            viewModel.setSelectedModel(uri.toString(), fileName)
            Toast.makeText(context, "Selected model: $fileName", Toast.LENGTH_SHORT).show()
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
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .widthIn(max = 896.dp)
                    .align(Alignment.TopCenter)
            ) {
                // Top Settings Header
                SettingsTopBar(
                    onNavigateBack = onNavigateBack,
                    onResetDefaults = {
                        viewModel.resetAiSettings()
                        Toast.makeText(context, "AI settings reset to defaults", Toast.LENGTH_SHORT).show()
                    }
                )

                // Settings Content
                Column(
                    modifier = Modifier
                        .fillMaxSize()
                        .verticalScroll(scrollState)
                        .padding(horizontal = 16.dp, vertical = 14.dp),
                    verticalArrangement = Arrangement.spacedBy(20.dp)
                ) {
                    // ==========================================
                    // 1. AI MODEL & ARCHITECTURE
                    // ==========================================
                    SettingsSectionTitle(
                        title = "AI Model & Engine",
                        caption = "Select generation model and reasoning depth"
                    )

                    Column(verticalArrangement = Arrangement.spacedBy(10.dp)) {
                        ModelSelectionCard(
                            title = "Claude 3.5 Sonnet / Gemini Pro",
                            tag = "Thinking Engine",
                            description = "Deep architectural reasoning, complex algorithm refactoring, and multi-file code synthesis.",
                            icon = Icons.Default.SmartToy,
                            isSelected = selectedModel == GeminiApiClient.MODEL_PRO_THINKING,
                            onClick = { viewModel.setModel(GeminiApiClient.MODEL_PRO_THINKING) },
                            testTag = "model_pro_option"
                        )

                        ModelSelectionCard(
                            title = "Claude 3.5 Haiku / Gemini Flash",
                            tag = "Low Latency",
                            description = "Sub-second response streaming, ideal for rapid prototyping, quick questions, and short fixes.",
                            icon = Icons.Default.Bolt,
                            isSelected = selectedModel == GeminiApiClient.MODEL_FLASH,
                            onClick = { viewModel.setModel(GeminiApiClient.MODEL_FLASH) },
                            testTag = "model_flash_option"
                        )

                        ModelSelectionCard(
                            title = "Gemma 4 E2B (LiteRT-LM)",
                            tag = "100% Offline • Edge GPU",
                            description = "Google LiteRT-LM runtime with OpenCL GPU hardware acceleration. Air-gapped on-device inference, zero network calls, absolute privacy.",
                            icon = Icons.Default.Memory,
                            isSelected = selectedModel == GeminiApiClient.MODEL_GEMMA_LITERTLM,
                            onClick = { viewModel.setModel(GeminiApiClient.MODEL_GEMMA_LITERTLM) },
                            testTag = "model_gemma_litertlm_option"
                        )
                    }

                    // Reasoning Depth / Thinking Budget (Active when Pro Thinking is used)
                    Card(
                        colors = CardDefaults.cardColors(containerColor = CopilotTheme.PanelDark),
                        border = BorderStroke(0.5.dp, CopilotTheme.BorderSubtle),
                        shape = RoundedCornerShape(12.dp),
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        Column(modifier = Modifier.padding(14.dp)) {
                            Row(
                                verticalAlignment = Alignment.CenterVertically,
                                horizontalArrangement = Arrangement.SpaceBetween,
                                modifier = Modifier.fillMaxWidth()
                            ) {
                                Row(verticalAlignment = Alignment.CenterVertically) {
                                    Icon(
                                        imageVector = Icons.Default.Speed,
                                        contentDescription = null,
                                        tint = CopilotTheme.PurpleLight,
                                        modifier = Modifier.size(18.dp)
                                    )
                                    Spacer(modifier = Modifier.width(8.dp))
                                    Text(
                                        text = "Reasoning Depth (Thinking Budget)",
                                        color = CopilotTheme.TextBright,
                                        fontSize = 13.5.sp,
                                        fontWeight = FontWeight.SemiBold
                                    )
                                }

                                Box(
                                    modifier = Modifier
                                        .clip(RoundedCornerShape(4.dp))
                                        .background(CopilotTheme.PurpleBadgeBg)
                                        .padding(horizontal = 6.dp, vertical = 2.dp)
                                ) {
                                    Text(
                                        text = when (thinkingBudget) {
                                            "HIGH" -> "16K Tokens"
                                            "MEDIUM" -> "8K Tokens"
                                            else -> "2K Tokens"
                                        },
                                        color = CopilotTheme.PurpleLight,
                                        fontSize = 10.5.sp,
                                        fontWeight = FontWeight.Bold,
                                        fontFamily = CopilotTheme.MonoFont
                                    )
                                }
                            }

                            Spacer(modifier = Modifier.height(4.dp))
                            Text(
                                text = "Adjusts token ceiling allocated specifically for the internal chain-of-thought solver.",
                                color = CopilotTheme.TextMuted,
                                fontSize = 11.5.sp
                            )

                            Spacer(modifier = Modifier.height(10.dp))
                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                horizontalArrangement = Arrangement.spacedBy(8.dp)
                            ) {
                                listOf(
                                    Triple("LOW", "Fast", "2K"),
                                    Triple("MEDIUM", "Standard", "8K"),
                                    Triple("HIGH", "Extended", "16K")
                                ).forEach { (id, label, tokens) ->
                                    val isBudgetSelected = thinkingBudget == id
                                    Box(
                                        modifier = Modifier
                                            .weight(1f)
                                            .clip(RoundedCornerShape(8.dp))
                                            .border(
                                                1.dp,
                                                if (isBudgetSelected) CopilotTheme.PurplePrimary else CopilotTheme.BorderSubtle,
                                                RoundedCornerShape(8.dp)
                                            )
                                            .background(if (isBudgetSelected) CopilotTheme.PurpleBadgeBg else CopilotTheme.UserBubble)
                                            .clickable { viewModel.setThinkingBudget(id) }
                                            .padding(vertical = 8.dp),
                                        contentAlignment = Alignment.Center
                                    ) {
                                        Column(horizontalAlignment = Alignment.CenterHorizontally) {
                                            Text(
                                                text = label,
                                                color = if (isBudgetSelected) CopilotTheme.PurpleLight else CopilotTheme.TextMain,
                                                fontSize = 12.sp,
                                                fontWeight = FontWeight.SemiBold
                                            )
                                            Text(
                                                text = tokens,
                                                color = CopilotTheme.TextMuted,
                                                fontSize = 10.sp,
                                                fontFamily = CopilotTheme.MonoFont
                                            )
                                        }
                                    }
                                }
                            }
                        }
                    }

                    // Toggles: Thinking Mode & Search Grounding
                    SettingsToggleCard(
                        icon = Icons.Default.Psychology,
                        iconTint = CopilotTheme.PurpleLight,
                        iconBg = CopilotTheme.PurpleBadgeBg,
                        title = "Thinking Mode",
                        description = "Displays step-by-step reasoning and collapsible thought process in chat responses.",
                        checked = thinkingEnabled,
                        onCheckedChange = { viewModel.toggleThinking() },
                        testTag = "thinking_mode_switch"
                    )

                    SettingsToggleCard(
                        icon = Icons.Default.Language,
                        iconTint = CopilotTheme.LinkBlue,
                        iconBg = CopilotTheme.PurpleBadgeBg,
                        title = "Search Grounding",
                        description = "Retrieves live web references, official documentation, and source citations.",
                        checked = searchEnabled,
                        onCheckedChange = { viewModel.toggleSearch() },
                        testTag = "search_grounding_switch"
                    )

                    Spacer(modifier = Modifier.height(2.dp))

                    // ==========================================
                    // LITERTLM & GEMMA 4 ON-DEVICE ENGINE HUB
                    // ==========================================
                    SettingsSectionTitle(
                        title = "LiteRT-LM & Gemma 4 Edge Hub",
                        caption = "Air-gapped on-device inference & hardware acceleration"
                    )

                    Card(
                        colors = CardDefaults.cardColors(containerColor = CopilotTheme.PanelDark),
                        border = BorderStroke(0.6.dp, Color(0xFF10B981).copy(alpha = 0.4f)),
                        shape = RoundedCornerShape(12.dp),
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        Column(modifier = Modifier.padding(14.dp), verticalArrangement = Arrangement.spacedBy(12.dp)) {
                            // Header: Model badge and online/offline status
                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                horizontalArrangement = Arrangement.SpaceBetween,
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Row(verticalAlignment = Alignment.CenterVertically) {
                                    Box(
                                        modifier = Modifier
                                            .size(34.dp)
                                            .clip(RoundedCornerShape(8.dp))
                                            .background(Color(0xFF0F241C)),
                                        contentAlignment = Alignment.Center
                                    ) {
                                        Icon(
                                            imageVector = Icons.Default.Memory,
                                            contentDescription = null,
                                            tint = Color(0xFF10B981),
                                            modifier = Modifier.size(20.dp)
                                        )
                                    }
                                    Spacer(modifier = Modifier.width(10.dp))
                                    Column {
                                        Text(
                                            text = "gemma-4-E2B-it-litert-lm",
                                            color = CopilotTheme.TextBright,
                                            fontSize = 13.5.sp,
                                            fontWeight = FontWeight.Bold,
                                            fontFamily = CopilotTheme.MonoFont
                                        )
                                        Text(
                                            text = "Google LiteRT-LM (W4A16 INT4 + MTP)",
                                            color = CopilotTheme.TextMuted,
                                            fontSize = 11.sp
                                        )
                                    }
                                }

                                Box(
                                    modifier = Modifier
                                        .clip(RoundedCornerShape(6.dp))
                                        .background(Color(0xFF0F241C))
                                        .border(0.6.dp, Color(0xFF10B981), RoundedCornerShape(6.dp))
                                        .padding(horizontal = 8.dp, vertical = 3.dp)
                                ) {
                                    Row(verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.spacedBy(5.dp)) {
                                        Box(
                                            modifier = Modifier
                                                .size(6.dp)
                                                .clip(CircleShape)
                                                .background(Color(0xFF10B981))
                                        )
                                        Text(
                                            text = "100% Offline",
                                            color = Color(0xFF34D399),
                                            fontSize = 10.5.sp,
                                            fontWeight = FontWeight.Bold,
                                            fontFamily = CopilotTheme.MonoFont
                                        )
                                    }
                                }
                            }

                            // Local .litertlm File Selection Component
                            Box(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .clip(RoundedCornerShape(8.dp))
                                    .background(CopilotTheme.CanvasDark)
                                    .border(0.5.dp, Color(0xFF10B981).copy(alpha = 0.5f), RoundedCornerShape(8.dp))
                                    .clickable {
                                        filePickerLauncher.launch(arrayOf("*/*"))
                                    }
                                    .padding(12.dp)
                            ) {
                                Row(
                                    modifier = Modifier.fillMaxWidth(),
                                    horizontalArrangement = Arrangement.SpaceBetween,
                                    verticalAlignment = Alignment.CenterVertically
                                ) {
                                    Row(
                                        verticalAlignment = Alignment.CenterVertically,
                                        modifier = Modifier.weight(1f)
                                    ) {
                                        Icon(
                                            imageVector = Icons.Default.Storage,
                                            contentDescription = null,
                                            tint = Color(0xFF34D399),
                                            modifier = Modifier.size(20.dp)
                                        )
                                        Spacer(modifier = Modifier.width(10.dp))
                                        Column {
                                            Text(
                                                text = selectedModelName ?: "gemma-4-E2B-it-litert-lm.litertlm",
                                                color = CopilotTheme.TextBright,
                                                fontSize = 12.sp,
                                                fontWeight = FontWeight.Bold,
                                                fontFamily = CopilotTheme.MonoFont,
                                                maxLines = 1
                                            )
                                            Text(
                                                text = if (selectedModelPath != null) "Local Storage (.litertlm)" else "Tap to browse local device storage",
                                                color = Color(0xFF10B981),
                                                fontSize = 10.sp,
                                                fontFamily = CopilotTheme.MonoFont
                                            )
                                        }
                                    }
                                    Box(
                                        modifier = Modifier
                                            .clip(RoundedCornerShape(6.dp))
                                            .background(Color(0xFF0F241C))
                                            .border(0.5.dp, Color(0xFF10B981), RoundedCornerShape(6.dp))
                                            .padding(horizontal = 10.dp, vertical = 6.dp)
                                    ) {
                                        Text(
                                            text = "Browse File",
                                            color = Color(0xFF34D399),
                                            fontSize = 11.sp,
                                            fontWeight = FontWeight.SemiBold,
                                            fontFamily = CopilotTheme.MonoFont
                                        )
                                    }
                                }
                            }

                            // Model Specifications Pill Grid
                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                horizontalArrangement = Arrangement.spacedBy(8.dp)
                            ) {
                                listOf(
                                    "Weight Size" to "2.58 GB",
                                    "Context KV" to "${kvCacheLimit} Tok",
                                    "Network" to "Air-Gapped"
                                ).forEach { (label, value) ->
                                    Box(
                                        modifier = Modifier
                                            .weight(1f)
                                            .clip(RoundedCornerShape(8.dp))
                                            .background(CopilotTheme.CanvasDark)
                                            .border(0.5.dp, CopilotTheme.BorderSubtle, RoundedCornerShape(8.dp))
                                            .padding(vertical = 8.dp, horizontal = 6.dp),
                                        contentAlignment = Alignment.Center
                                    ) {
                                        Column(horizontalAlignment = Alignment.CenterHorizontally) {
                                            Text(
                                                text = label,
                                                color = CopilotTheme.TextFaint,
                                                fontSize = 10.sp,
                                                fontFamily = CopilotTheme.MonoFont
                                            )
                                            Text(
                                                text = value,
                                                color = CopilotTheme.TextBright,
                                                fontSize = 12.sp,
                                                fontWeight = FontWeight.SemiBold,
                                                fontFamily = CopilotTheme.MonoFont
                                            )
                                        }
                                    }
                                }
                            }

                            // Hardware Backend Selection (GPU OpenCL / NPU / CPU NEON)
                            Column(verticalArrangement = Arrangement.spacedBy(6.dp)) {
                                Text(
                                    text = "HARDWARE INFERENCE BACKEND",
                                    color = CopilotTheme.TextFaint,
                                    fontSize = 10.sp,
                                    fontWeight = FontWeight.Bold,
                                    fontFamily = CopilotTheme.MonoFont,
                                    letterSpacing = 0.8.sp
                                )
                                Row(
                                    modifier = Modifier.fillMaxWidth(),
                                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                                ) {
                                    listOf(
                                        Triple(LiteRtLmEngineManager.HardwareBackend.GPU_OPENCL, "GPU", "OpenCL (Fast)"),
                                        Triple(LiteRtLmEngineManager.HardwareBackend.NPU, "NPU", "Hexagon APU"),
                                        Triple(LiteRtLmEngineManager.HardwareBackend.CPU_NEON, "CPU", "ARM NEON")
                                    ).forEach { (backend, title, desc) ->
                                        val isSelected = hardwareBackend == backend
                                        Box(
                                            modifier = Modifier
                                                .weight(1f)
                                                .clip(RoundedCornerShape(8.dp))
                                                .background(if (isSelected) Color(0xFF0F241C) else CopilotTheme.CanvasDark)
                                                .border(
                                                    1.dp,
                                                    if (isSelected) Color(0xFF10B981) else CopilotTheme.BorderSubtle,
                                                    RoundedCornerShape(8.dp)
                                                )
                                                .clickable { viewModel.setHardwareBackend(backend) }
                                                .padding(vertical = 8.dp),
                                            contentAlignment = Alignment.Center
                                        ) {
                                            Column(horizontalAlignment = Alignment.CenterHorizontally) {
                                                Text(
                                                    text = title,
                                                    color = if (isSelected) Color(0xFF34D399) else CopilotTheme.TextBright,
                                                    fontSize = 12.5.sp,
                                                    fontWeight = FontWeight.Bold,
                                                    fontFamily = CopilotTheme.MonoFont
                                                )
                                                Text(
                                                    text = desc,
                                                    color = if (isSelected) Color(0xFF10B981) else CopilotTheme.TextMuted,
                                                    fontSize = 9.5.sp,
                                                    fontFamily = CopilotTheme.MonoFont
                                                )
                                            }
                                        }
                                    }
                                }
                            }

                            // KV Cache Limit
                            Column(verticalArrangement = Arrangement.spacedBy(6.dp)) {
                                Text(
                                    text = "KV CACHE CONTEXT ALLOCATION",
                                    color = CopilotTheme.TextFaint,
                                    fontSize = 10.sp,
                                    fontWeight = FontWeight.Bold,
                                    fontFamily = CopilotTheme.MonoFont,
                                    letterSpacing = 0.8.sp
                                )
                                Row(
                                    modifier = Modifier.fillMaxWidth(),
                                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                                ) {
                                    listOf(1024, 2048, 4096).forEach { tokens ->
                                        val isSelected = kvCacheLimit == tokens
                                        Box(
                                            modifier = Modifier
                                                .weight(1f)
                                                .clip(RoundedCornerShape(8.dp))
                                                .background(if (isSelected) Color(0xFF0F241C) else CopilotTheme.CanvasDark)
                                                .border(
                                                    1.dp,
                                                    if (isSelected) Color(0xFF10B981) else CopilotTheme.BorderSubtle,
                                                    RoundedCornerShape(8.dp)
                                                )
                                                .clickable { viewModel.setKvCacheLimit(tokens) }
                                                .padding(vertical = 7.dp),
                                            contentAlignment = Alignment.Center
                                        ) {
                                            Text(
                                                text = "$tokens Tokens",
                                                color = if (isSelected) Color(0xFF34D399) else CopilotTheme.TextMuted,
                                                fontSize = 11.sp,
                                                fontWeight = FontWeight.SemiBold,
                                                fontFamily = CopilotTheme.MonoFont
                                            )
                                        }
                                    }
                                }
                            }

                            // Live On-Device Benchmark Card
                            Box(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .clip(RoundedCornerShape(10.dp))
                                    .background(CopilotTheme.CanvasDark)
                                    .border(0.6.dp, CopilotTheme.BorderSubtle, RoundedCornerShape(10.dp))
                                    .padding(12.dp)
                            ) {
                                Column(verticalArrangement = Arrangement.spacedBy(10.dp)) {
                                    Row(
                                        modifier = Modifier.fillMaxWidth(),
                                        horizontalArrangement = Arrangement.SpaceBetween,
                                        verticalAlignment = Alignment.CenterVertically
                                    ) {
                                        Column(modifier = Modifier.weight(1f)) {
                                            Text(
                                                text = "Offline Hardware Diagnostics",
                                                color = CopilotTheme.TextBright,
                                                fontSize = 13.sp,
                                                fontWeight = FontWeight.SemiBold
                                            )
                                            Text(
                                                text = "Measures decode throughput & TTFT latency",
                                                color = CopilotTheme.TextMuted,
                                                fontSize = 11.sp
                                            )
                                        }

                                        Spacer(modifier = Modifier.width(8.dp))

                                        Button(
                                            onClick = { viewModel.runOfflineBenchmark() },
                                            enabled = !isBenchmarking,
                                            colors = ButtonDefaults.buttonColors(
                                                containerColor = Color(0xFF10B981),
                                                contentColor = Color.Black
                                            ),
                                            shape = RoundedCornerShape(8.dp),
                                            contentPadding = PaddingValues(horizontal = 12.dp, vertical = 6.dp),
                                            modifier = Modifier.height(32.dp)
                                        ) {
                                            if (isBenchmarking) {
                                                CircularProgressIndicator(
                                                    modifier = Modifier.size(14.dp),
                                                    strokeWidth = 2.dp,
                                                    color = Color.Black
                                                )
                                            } else {
                                                Icon(
                                                    imageVector = Icons.Default.PlayArrow,
                                                    contentDescription = null,
                                                    modifier = Modifier.size(16.dp)
                                                )
                                                Spacer(modifier = Modifier.width(4.dp))
                                                Text(
                                                    text = "Benchmark",
                                                    fontSize = 11.5.sp,
                                                    fontWeight = FontWeight.Bold,
                                                    fontFamily = CopilotTheme.MonoFont
                                                )
                                            }
                                        }
                                    }

                                    if (isBenchmarking) {
                                        Row(
                                            modifier = Modifier
                                                .fillMaxWidth()
                                                .clip(RoundedCornerShape(6.dp))
                                                .background(Color(0xFF0F241C))
                                                .padding(horizontal = 10.dp, vertical = 6.dp),
                                            verticalAlignment = Alignment.CenterVertically,
                                            horizontalArrangement = Arrangement.spacedBy(8.dp)
                                        ) {
                                            CircularProgressIndicator(
                                                modifier = Modifier.size(12.dp),
                                                strokeWidth = 2.dp,
                                                color = Color(0xFF10B981)
                                            )
                                            Text(
                                                text = benchmarkProgress,
                                                color = Color(0xFF34D399),
                                                fontSize = 11.sp,
                                                fontFamily = CopilotTheme.MonoFont
                                            )
                                        }
                                    }

                                    val result = lastBenchmark
                                    if (result != null) {
                                        Row(
                                            modifier = Modifier.fillMaxWidth(),
                                            horizontalArrangement = Arrangement.spacedBy(8.dp)
                                        ) {
                                            listOf(
                                                "Decode Speed" to "%.1f tok/s".format(result.decodeSpeedTokPerSec),
                                                "TTFT Latency" to "${result.timeToFirstTokenMs} ms",
                                                "Peak Memory" to "${result.memoryUsedMb} MB"
                                            ).forEach { (label, value) ->
                                                Box(
                                                    modifier = Modifier
                                                        .weight(1f)
                                                        .clip(RoundedCornerShape(6.dp))
                                                        .background(CopilotTheme.UserBubble)
                                                        .padding(vertical = 6.dp, horizontal = 4.dp),
                                                    contentAlignment = Alignment.Center
                                                ) {
                                                    Column(horizontalAlignment = Alignment.CenterHorizontally) {
                                                        Text(
                                                            text = label,
                                                            color = CopilotTheme.TextFaint,
                                                            fontSize = 9.sp,
                                                            fontFamily = CopilotTheme.MonoFont
                                                        )
                                                        Text(
                                                            text = value,
                                                            color = Color(0xFF34D399),
                                                            fontSize = 11.5.sp,
                                                            fontWeight = FontWeight.Bold,
                                                            fontFamily = CopilotTheme.MonoFont
                                                        )
                                                    }
                                                }
                                            }
                                        }
                                    }
                                }
                            }
                        }
                    }

                    Spacer(modifier = Modifier.height(2.dp))

                    // ==========================================
                    // 2. AI GENERATION PARAMETERS
                    // ==========================================
                    SettingsSectionTitle(
                        title = "Generation Parameters",
                        caption = "Precision, temperature, and output constraints"
                    )

                    // Temperature / Precision Slider Card
                    Card(
                        colors = CardDefaults.cardColors(containerColor = CopilotTheme.PanelDark),
                        border = BorderStroke(0.5.dp, CopilotTheme.BorderSubtle),
                        shape = RoundedCornerShape(12.dp),
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        Column(modifier = Modifier.padding(14.dp)) {
                            Row(
                                verticalAlignment = Alignment.CenterVertically,
                                horizontalArrangement = Arrangement.SpaceBetween,
                                modifier = Modifier.fillMaxWidth()
                            ) {
                                Row(verticalAlignment = Alignment.CenterVertically) {
                                    Icon(
                                        imageVector = Icons.Default.Tune,
                                        contentDescription = null,
                                        tint = CopilotTheme.PurpleLight,
                                        modifier = Modifier.size(18.dp)
                                    )
                                    Spacer(modifier = Modifier.width(8.dp))
                                    Text(
                                        text = "Code Precision (Temperature)",
                                        color = CopilotTheme.TextBright,
                                        fontSize = 13.5.sp,
                                        fontWeight = FontWeight.SemiBold
                                    )
                                }

                                val tempLabel = when {
                                    temperature <= 0.05f -> "Deterministic"
                                    temperature <= 0.35f -> "Precise"
                                    temperature <= 0.65f -> "Balanced"
                                    else -> "Creative"
                                }

                                Box(
                                    modifier = Modifier
                                        .clip(RoundedCornerShape(4.dp))
                                        .background(CopilotTheme.PurpleBadgeBg)
                                        .padding(horizontal = 6.dp, vertical = 2.dp)
                                ) {
                                    Text(
                                        text = "%.1f • %s".format(temperature, tempLabel),
                                        color = CopilotTheme.PurpleLight,
                                        fontSize = 10.5.sp,
                                        fontWeight = FontWeight.Bold,
                                        fontFamily = CopilotTheme.MonoFont
                                    )
                                }
                            }

                            Spacer(modifier = Modifier.height(4.dp))
                            Text(
                                text = "Lower values produce deterministic, predictable code; higher values generate diverse, creative architectures.",
                                color = CopilotTheme.TextMuted,
                                fontSize = 11.5.sp
                            )

                            Spacer(modifier = Modifier.height(8.dp))
                            Slider(
                                value = temperature,
                                onValueChange = { viewModel.setTemperature(it) },
                                valueRange = 0.0f..1.0f,
                                steps = 9,
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .testTag("temperature_slider"),
                                colors = SliderDefaults.colors(
                                    thumbColor = CopilotTheme.PurplePrimary,
                                    activeTrackColor = CopilotTheme.PurplePrimary,
                                    inactiveTrackColor = CopilotTheme.UserBubble
                                )
                            )

                            // Quick Presets Row
                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                horizontalArrangement = Arrangement.spacedBy(6.dp)
                            ) {
                                listOf(
                                    0.0f to "0.0 Bugfix",
                                    0.2f to "0.2 Standard",
                                    0.7f to "0.7 Creative"
                                ).forEach { (valFloat, label) ->
                                    val isSelected = kotlin.math.abs(temperature - valFloat) < 0.05f
                                    Box(
                                        modifier = Modifier
                                            .weight(1f)
                                            .clip(RoundedCornerShape(6.dp))
                                            .background(if (isSelected) CopilotTheme.PurpleBadgeBg else CopilotTheme.UserBubble)
                                            .border(
                                                0.5.dp,
                                                if (isSelected) CopilotTheme.PurplePrimary else CopilotTheme.BorderSubtle,
                                                RoundedCornerShape(6.dp)
                                            )
                                            .clickable { viewModel.setTemperature(valFloat) }
                                            .padding(vertical = 5.dp),
                                        contentAlignment = Alignment.Center
                                    ) {
                                        Text(
                                            text = label,
                                            color = if (isSelected) CopilotTheme.PurpleLight else CopilotTheme.TextMuted,
                                            fontSize = 11.sp,
                                            fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Normal
                                        )
                                    }
                                }
                            }
                        }
                    }

                    // Max Output Tokens Card
                    Card(
                        colors = CardDefaults.cardColors(containerColor = CopilotTheme.PanelDark),
                        border = BorderStroke(0.5.dp, CopilotTheme.BorderSubtle),
                        shape = RoundedCornerShape(12.dp),
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        Column(modifier = Modifier.padding(14.dp)) {
                            Row(
                                verticalAlignment = Alignment.CenterVertically,
                                horizontalArrangement = Arrangement.SpaceBetween,
                                modifier = Modifier.fillMaxWidth()
                            ) {
                                Row(verticalAlignment = Alignment.CenterVertically) {
                                    Icon(
                                        imageVector = Icons.Default.Code,
                                        contentDescription = null,
                                        tint = CopilotTheme.PurpleLight,
                                        modifier = Modifier.size(18.dp)
                                    )
                                    Spacer(modifier = Modifier.width(8.dp))
                                    Text(
                                        text = "Max Output Tokens",
                                        color = CopilotTheme.TextBright,
                                        fontSize = 13.5.sp,
                                        fontWeight = FontWeight.SemiBold
                                    )
                                }

                                Text(
                                    text = "$maxOutputTokens Tokens",
                                    color = CopilotTheme.PurpleLight,
                                    fontSize = 11.sp,
                                    fontFamily = CopilotTheme.MonoFont,
                                    fontWeight = FontWeight.Bold
                                )
                            }

                            Spacer(modifier = Modifier.height(4.dp))
                            Text(
                                text = "Sets the maximum length of generated source files and code explanations.",
                                color = CopilotTheme.TextMuted,
                                fontSize = 11.5.sp
                            )

                            Spacer(modifier = Modifier.height(10.dp))
                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                horizontalArrangement = Arrangement.spacedBy(8.dp)
                            ) {
                                listOf(2048, 4096, 8192).forEach { tokens ->
                                    val isTokensSelected = maxOutputTokens == tokens
                                    Box(
                                        modifier = Modifier
                                            .weight(1f)
                                            .clip(RoundedCornerShape(8.dp))
                                            .border(
                                                1.dp,
                                                if (isTokensSelected) CopilotTheme.PurplePrimary else CopilotTheme.BorderSubtle,
                                                RoundedCornerShape(8.dp)
                                            )
                                            .background(if (isTokensSelected) CopilotTheme.PurpleBadgeBg else CopilotTheme.UserBubble)
                                            .clickable { viewModel.setMaxOutputTokens(tokens) }
                                            .padding(vertical = 8.dp),
                                        contentAlignment = Alignment.Center
                                    ) {
                                        Text(
                                            text = "$tokens",
                                            color = if (isTokensSelected) CopilotTheme.PurpleLight else CopilotTheme.TextMain,
                                            fontSize = 12.sp,
                                            fontWeight = FontWeight.SemiBold,
                                            fontFamily = CopilotTheme.MonoFont
                                        )
                                    }
                                }
                            }
                        }
                    }

                    Spacer(modifier = Modifier.height(2.dp))

                    // ==========================================
                    // 3. AI PERSONA & SYSTEM INSTRUCTIONS
                    // ==========================================
                    SettingsSectionTitle(
                        title = "AI Persona & System Prompt",
                        caption = "Customize developer expertise, role, and custom guidelines"
                    )

                    // Persona Selection
                    Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                        listOf(
                            Triple("Senior Architect", "Modularity, clean decoupled architecture, enterprise patterns", Icons.Default.Architecture),
                            Triple("Concise Minimalist", "Direct code snippets with zero conversational filler", Icons.Default.FlashOn),
                            Triple("Android Specialist", "Jetpack Compose, Kotlin Coroutines, Flow, 120 FPS performance", Icons.Default.Engineering),
                            Triple("Mentor & Explainer", "Step-by-step reasoning, trade-offs, and pitfall analysis", Icons.Default.School)
                        ).forEach { (pName, pDesc, pIcon) ->
                            val isPersonaSelected = selectedPersona == pName
                            Card(
                                colors = CardDefaults.cardColors(
                                    containerColor = if (isPersonaSelected) CopilotTheme.PanelDark else CopilotTheme.CanvasDark
                                ),
                                border = BorderStroke(
                                    if (isPersonaSelected) 1.5.dp else 0.5.dp,
                                    if (isPersonaSelected) CopilotTheme.PurplePrimary else CopilotTheme.BorderSubtle
                                ),
                                shape = RoundedCornerShape(10.dp),
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .clickable { viewModel.setPersona(pName) }
                                    .testTag("persona_${pName.lowercase().replace(" ", "_")}")
                            ) {
                                Row(
                                    modifier = Modifier
                                        .fillMaxWidth()
                                        .padding(12.dp),
                                    verticalAlignment = Alignment.CenterVertically
                                ) {
                                    Box(
                                        modifier = Modifier
                                            .size(34.dp)
                                            .clip(RoundedCornerShape(8.dp))
                                            .background(if (isPersonaSelected) CopilotTheme.PurpleBadgeBg else CopilotTheme.UserBubble),
                                        contentAlignment = Alignment.Center
                                    ) {
                                        Icon(
                                            imageVector = pIcon,
                                            contentDescription = null,
                                            tint = if (isPersonaSelected) CopilotTheme.PurpleLight else CopilotTheme.TextMuted,
                                            modifier = Modifier.size(18.dp)
                                        )
                                    }

                                    Spacer(modifier = Modifier.width(12.dp))

                                    Column(modifier = Modifier.weight(1f)) {
                                        Text(
                                            text = pName,
                                            color = CopilotTheme.TextBright,
                                            fontSize = 13.5.sp,
                                            fontWeight = FontWeight.SemiBold
                                        )
                                        Text(
                                            text = pDesc,
                                            color = CopilotTheme.TextMuted,
                                            fontSize = 11.5.sp
                                        )
                                    }

                                    if (isPersonaSelected) {
                                        Box(
                                            modifier = Modifier
                                                .size(18.dp)
                                                .clip(CircleShape)
                                                .background(CopilotTheme.PurplePrimary),
                                            contentAlignment = Alignment.Center
                                        ) {
                                            Icon(
                                                imageVector = Icons.Default.Check,
                                                contentDescription = null,
                                                tint = Color.White,
                                                modifier = Modifier.size(12.dp)
                                            )
                                        }
                                    }
                                }
                            }
                        }
                    }

                    // Custom System Instructions Field
                    Card(
                        colors = CardDefaults.cardColors(containerColor = CopilotTheme.PanelDark),
                        border = BorderStroke(0.5.dp, CopilotTheme.BorderSubtle),
                        shape = RoundedCornerShape(12.dp),
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        Column(modifier = Modifier.padding(14.dp)) {
                            Row(
                                verticalAlignment = Alignment.CenterVertically,
                                horizontalArrangement = Arrangement.SpaceBetween,
                                modifier = Modifier.fillMaxWidth()
                            ) {
                                Text(
                                    text = "Custom Developer Instructions",
                                    color = CopilotTheme.TextBright,
                                    fontSize = 13.5.sp,
                                    fontWeight = FontWeight.SemiBold
                                )

                                if (customSystemPrompt.isNotBlank()) {
                                    Text(
                                        text = "Clear",
                                        color = CopilotTheme.StatusDanger,
                                        fontSize = 12.sp,
                                        modifier = Modifier
                                            .clickable { viewModel.setCustomSystemPrompt("") }
                                            .padding(4.dp)
                                    )
                                }
                            }

                            Spacer(modifier = Modifier.height(4.dp))
                            Text(
                                text = "Instructions appended to the AI system prompt on every generation request.",
                                color = CopilotTheme.TextMuted,
                                fontSize = 11.5.sp
                            )

                            Spacer(modifier = Modifier.height(10.dp))
                            OutlinedTextField(
                                value = customSystemPrompt,
                                onValueChange = { viewModel.setCustomSystemPrompt(it) },
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .height(110.dp)
                                    .testTag("custom_instructions_field"),
                                textStyle = TextStyle(
                                    color = CopilotTheme.TextBright,
                                    fontSize = 12.5.sp,
                                    fontFamily = CopilotTheme.MonoFont
                                ),
                                placeholder = {
                                    Text(
                                        text = "e.g. Always write production-ready Kotlin 2.0 with coroutines, no conversational filler, and use Compose M3 guidelines.",
                                        color = CopilotTheme.TextFaint,
                                        fontSize = 12.sp
                                    )
                                },
                                shape = RoundedCornerShape(8.dp),
                                colors = OutlinedTextFieldDefaults.colors(
                                    focusedContainerColor = CopilotTheme.CanvasDark,
                                    unfocusedContainerColor = CopilotTheme.CanvasDark,
                                    focusedBorderColor = CopilotTheme.PurplePrimary,
                                    unfocusedBorderColor = CopilotTheme.BorderSubtle,
                                    cursorColor = CopilotTheme.PurpleLight
                                )
                            )

                            Spacer(modifier = Modifier.height(8.dp))
                            Text(
                                text = "Quick suggestion tags:",
                                color = CopilotTheme.TextMuted,
                                fontSize = 11.sp
                            )

                            Spacer(modifier = Modifier.height(4.dp))
                            FlowRow(
                                modifier = Modifier.fillMaxWidth(),
                                horizontalArrangement = Arrangement.spacedBy(6.dp),
                                verticalArrangement = Arrangement.spacedBy(6.dp)
                            ) {
                                listOf(
                                    "+ Unit Tests",
                                    "+ Production Ready",
                                    "+ Concise Code",
                                    "+ Coroutines & Flow"
                                ).forEach { tag ->
                                    Box(
                                        modifier = Modifier
                                            .clip(RoundedCornerShape(6.dp))
                                            .background(CopilotTheme.UserBubble)
                                            .border(0.5.dp, CopilotTheme.BorderSubtle, RoundedCornerShape(6.dp))
                                            .clickable {
                                                val clean = tag.removePrefix("+ ")
                                                val updated = if (customSystemPrompt.isBlank()) clean else "$customSystemPrompt, $clean"
                                                viewModel.setCustomSystemPrompt(updated)
                                            }
                                            .padding(horizontal = 8.dp, vertical = 4.dp)
                                    ) {
                                        Text(
                                            text = tag,
                                            color = CopilotTheme.PurpleLight,
                                            fontSize = 10.5.sp,
                                            fontWeight = FontWeight.Medium
                                        )
                                    }
                                }
                            }
                        }
                    }

                    Spacer(modifier = Modifier.height(2.dp))

                    // ==========================================
                    // 4. CODE OUTPUT PREFERENCES
                    // ==========================================
                    SettingsSectionTitle(
                        title = "Code Output Preferences",
                        caption = "Automated test suites and explanation density"
                    )

                    SettingsToggleCard(
                        icon = Icons.Default.BugReport,
                        iconTint = CopilotTheme.StatusWarning,
                        iconBg = Color(0x22D29922),
                        title = "Auto-Generate Unit Tests",
                        description = "Automatically appends ready-to-run unit test suites and edge-case verifications for generated code.",
                        checked = includeUnitTests,
                        onCheckedChange = { viewModel.toggleIncludeUnitTests() },
                        testTag = "include_unit_tests_switch"
                    )

                    // Code Style Detail
                    Card(
                        colors = CardDefaults.cardColors(containerColor = CopilotTheme.PanelDark),
                        border = BorderStroke(0.5.dp, CopilotTheme.BorderSubtle),
                        shape = RoundedCornerShape(12.dp),
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        Column(modifier = Modifier.padding(14.dp)) {
                            Text(
                                text = "Explanation Style",
                                color = CopilotTheme.TextBright,
                                fontSize = 13.5.sp,
                                fontWeight = FontWeight.SemiBold
                            )
                            Spacer(modifier = Modifier.height(2.dp))
                            Text(
                                text = "Controls the depth of markdown explanations accompanying code blocks.",
                                color = CopilotTheme.TextMuted,
                                fontSize = 11.5.sp
                            )

                            Spacer(modifier = Modifier.height(10.dp))
                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                horizontalArrangement = Arrangement.spacedBy(8.dp)
                            ) {
                                listOf("Code Only", "Balanced", "Detailed").forEach { style ->
                                    val isSelected = codeStyle == style
                                    Box(
                                        modifier = Modifier
                                            .weight(1f)
                                            .clip(RoundedCornerShape(8.dp))
                                            .border(
                                                1.dp,
                                                if (isSelected) CopilotTheme.PurplePrimary else CopilotTheme.BorderSubtle,
                                                RoundedCornerShape(8.dp)
                                            )
                                            .background(if (isSelected) CopilotTheme.PurpleBadgeBg else CopilotTheme.UserBubble)
                                            .clickable { viewModel.setCodeStyle(style) }
                                            .padding(vertical = 8.dp),
                                        contentAlignment = Alignment.Center
                                    ) {
                                        Text(
                                            text = style,
                                            color = if (isSelected) CopilotTheme.PurpleLight else CopilotTheme.TextMain,
                                            fontSize = 11.5.sp,
                                            fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Normal
                                        )
                                    }
                                }
                            }
                        }
                    }

                    Spacer(modifier = Modifier.height(2.dp))

                    // ==========================================
                    // 5. CHAT SESSION MANAGEMENT
                    // ==========================================
                    SettingsSectionTitle(
                        title = "Chat Session",
                        caption = "Manage active cache and conversation memory"
                    )

                    Card(
                        colors = CardDefaults.cardColors(containerColor = CopilotTheme.PanelDark),
                        border = BorderStroke(0.5.dp, CopilotTheme.BorderSubtle),
                        shape = RoundedCornerShape(12.dp),
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        Row(
                            modifier = Modifier
                                .fillMaxWidth()
                                .clickable { showClearDialog = true }
                                .padding(16.dp)
                                .testTag("clear_chat_button"),
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.SpaceBetween
                        ) {
                            Row(
                                verticalAlignment = Alignment.CenterVertically,
                                modifier = Modifier.weight(1f)
                            ) {
                                Box(
                                    modifier = Modifier
                                        .size(36.dp)
                                        .clip(RoundedCornerShape(8.dp))
                                        .background(Color(0x1AF85149)),
                                    contentAlignment = Alignment.Center
                                ) {
                                    Icon(
                                        imageVector = Icons.Default.DeleteOutline,
                                        contentDescription = "Clear Chat",
                                        tint = CopilotTheme.StatusDanger,
                                        modifier = Modifier.size(20.dp)
                                    )
                                }

                                Spacer(modifier = Modifier.width(12.dp))

                                Column {
                                    Text(
                                        text = "Clear Conversation History",
                                        color = CopilotTheme.StatusDanger,
                                        fontSize = 14.sp,
                                        fontWeight = FontWeight.SemiBold
                                    )
                                    Text(
                                        text = "Removes all prompt threads, branches, and generated code blocks",
                                        color = CopilotTheme.TextMuted,
                                        fontSize = 12.sp
                                    )
                                }
                            }
                        }
                    }

                    Spacer(modifier = Modifier.height(2.dp))

                    // ==========================================
                    // 6. ENGINE SPECIFICATION & INFO
                    // ==========================================
                    SettingsSectionTitle(
                        title = "About",
                        caption = "Environment and system architecture"
                    )

                    Card(
                        colors = CardDefaults.cardColors(containerColor = CopilotTheme.PanelDark),
                        border = BorderStroke(0.5.dp, CopilotTheme.BorderSubtle),
                        shape = RoundedCornerShape(12.dp),
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        Column(
                            modifier = Modifier.padding(16.dp),
                            verticalArrangement = Arrangement.spacedBy(10.dp)
                        ) {
                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                horizontalArrangement = Arrangement.SpaceBetween,
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Row(verticalAlignment = Alignment.CenterVertically) {
                                    Icon(
                                        imageVector = Icons.Outlined.Info,
                                        contentDescription = null,
                                        tint = CopilotTheme.TextMuted,
                                        modifier = Modifier.size(16.dp)
                                    )
                                    Spacer(modifier = Modifier.width(8.dp))
                                    Text(
                                        text = "Application",
                                        color = CopilotTheme.TextMain,
                                        fontSize = 13.sp
                                    )
                                }
                                Text(
                                    text = "Claude Copilot",
                                    color = CopilotTheme.TextBright,
                                    fontSize = 13.sp,
                                    fontWeight = FontWeight.SemiBold,
                                    fontFamily = CopilotTheme.MonoFont
                                )
                            }

                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                horizontalArrangement = Arrangement.SpaceBetween,
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Row(verticalAlignment = Alignment.CenterVertically) {
                                    Box(
                                        modifier = Modifier
                                            .size(8.dp)
                                            .clip(CircleShape)
                                            .background(CopilotTheme.StatusSuccess)
                                    )
                                    Spacer(modifier = Modifier.width(8.dp))
                                    Text(
                                        text = "Syntax Engine",
                                        color = CopilotTheme.TextMain,
                                        fontSize = 13.sp
                                    )
                                }
                                Text(
                                    text = "One Dark Pro / Dynamic",
                                    color = CopilotTheme.PurpleLight,
                                    fontSize = 13.sp,
                                    fontWeight = FontWeight.Medium,
                                    fontFamily = CopilotTheme.MonoFont
                                )
                            }
                        }
                    }

                    Spacer(modifier = Modifier.height(24.dp))
                }
            }
        }
    }

    if (showClearDialog) {
        AlertDialog(
            onDismissRequest = { showClearDialog = false },
            containerColor = CopilotTheme.PanelDark,
            title = {
                Text(
                    text = "Clear All Messages?",
                    color = CopilotTheme.TextBright,
                    fontWeight = FontWeight.Bold,
                    fontSize = 16.sp
                )
            },
            text = {
                Text(
                    text = "This will delete all current conversation messages, reasoning histories, and generated code snippets.",
                    color = CopilotTheme.TextMain,
                    fontSize = 13.5.sp
                )
            },
            confirmButton = {
                TextButton(
                    onClick = {
                        viewModel.clearChat()
                        showClearDialog = false
                        Toast.makeText(context, "Conversation cleared", Toast.LENGTH_SHORT).show()
                    }
                ) {
                    Text("Clear All", color = CopilotTheme.StatusDanger, fontWeight = FontWeight.Bold)
                }
            },
            dismissButton = {
                TextButton(onClick = { showClearDialog = false }) {
                    Text("Cancel", color = CopilotTheme.TextMuted)
                }
            }
        )
    }
}

@Composable
private fun SettingsTopBar(
    onNavigateBack: () -> Unit,
    onResetDefaults: () -> Unit,
    modifier: Modifier = Modifier
) {
    Row(
        modifier = modifier
            .fillMaxWidth()
            .background(CopilotTheme.PanelHeaderDark)
            .border(0.5.dp, CopilotTheme.BorderSubtle)
            .padding(horizontal = 8.dp, vertical = 8.dp),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.SpaceBetween
    ) {
        Row(verticalAlignment = Alignment.CenterVertically) {
            IconButton(
                onClick = onNavigateBack,
                modifier = Modifier
                    .size(40.dp)
                    .testTag("settings_back_button")
            ) {
                Icon(
                    imageVector = Icons.AutoMirrored.Filled.ArrowBack,
                    contentDescription = "Back to Chat",
                    tint = CopilotTheme.TextBright,
                    modifier = Modifier.size(20.dp)
                )
            }

            Spacer(modifier = Modifier.width(6.dp))

            Column {
                Text(
                    text = "Settings",
                    color = CopilotTheme.TextBright,
                    fontSize = 16.sp,
                    fontWeight = FontWeight.Bold,
                    fontFamily = CopilotTheme.MonoFont
                )
                Text(
                    text = "AI Configuration & Preferences",
                    color = CopilotTheme.TextMuted,
                    fontSize = 11.sp
                )
            }
        }

        IconButton(
            onClick = onResetDefaults,
            modifier = Modifier
                .size(38.dp)
                .testTag("reset_ai_settings_button")
        ) {
            Icon(
                imageVector = Icons.Default.RestartAlt,
                contentDescription = "Reset AI Defaults",
                tint = CopilotTheme.TextMuted,
                modifier = Modifier.size(20.dp)
            )
        }
    }
}

@Composable
private fun SettingsSectionTitle(
    title: String,
    caption: String,
    modifier: Modifier = Modifier
) {
    Column(modifier = modifier.fillMaxWidth()) {
        Text(
            text = title,
            color = CopilotTheme.TextBright,
            fontSize = 14.sp,
            fontWeight = FontWeight.Bold
        )
        Text(
            text = caption,
            color = CopilotTheme.TextMuted,
            fontSize = 11.5.sp
        )
    }
}

@Composable
private fun SettingsToggleCard(
    icon: ImageVector,
    iconTint: Color,
    iconBg: Color,
    title: String,
    description: String,
    checked: Boolean,
    onCheckedChange: (Boolean) -> Unit,
    testTag: String,
    modifier: Modifier = Modifier
) {
    Card(
        colors = CardDefaults.cardColors(containerColor = CopilotTheme.PanelDark),
        border = BorderStroke(0.5.dp, CopilotTheme.BorderSubtle),
        shape = RoundedCornerShape(12.dp),
        modifier = modifier.fillMaxWidth()
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(14.dp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            Row(
                verticalAlignment = Alignment.Top,
                modifier = Modifier.weight(1f)
            ) {
                Box(
                    modifier = Modifier
                        .size(36.dp)
                        .clip(RoundedCornerShape(8.dp))
                        .background(iconBg),
                    contentAlignment = Alignment.Center
                ) {
                    Icon(
                        imageVector = icon,
                        contentDescription = null,
                        tint = iconTint,
                        modifier = Modifier.size(20.dp)
                    )
                }

                Spacer(modifier = Modifier.width(12.dp))

                Column(modifier = Modifier.padding(end = 8.dp)) {
                    Text(
                        text = title,
                        color = CopilotTheme.TextBright,
                        fontSize = 14.sp,
                        fontWeight = FontWeight.SemiBold
                    )
                    Spacer(modifier = Modifier.height(2.dp))
                    Text(
                        text = description,
                        color = CopilotTheme.TextMuted,
                        fontSize = 12.sp,
                        lineHeight = 16.sp
                    )
                }
            }

            Switch(
                checked = checked,
                onCheckedChange = onCheckedChange,
                modifier = Modifier.testTag(testTag),
                colors = SwitchDefaults.colors(
                    checkedThumbColor = Color.White,
                    checkedTrackColor = CopilotTheme.PurplePrimary,
                    uncheckedThumbColor = CopilotTheme.TextMuted,
                    uncheckedTrackColor = CopilotTheme.UserBubble
                )
            )
        }
    }
}

@Composable
private fun ModelSelectionCard(
    title: String,
    tag: String,
    description: String,
    icon: ImageVector,
    isSelected: Boolean,
    onClick: () -> Unit,
    testTag: String,
    modifier: Modifier = Modifier
) {
    val borderColor = if (isSelected) CopilotTheme.PurplePrimary else CopilotTheme.BorderSubtle
    val bgColor = if (isSelected) CopilotTheme.PanelDark else CopilotTheme.CanvasDark

    Card(
        colors = CardDefaults.cardColors(containerColor = bgColor),
        border = BorderStroke(if (isSelected) 1.5.dp else 0.5.dp, borderColor),
        shape = RoundedCornerShape(12.dp),
        modifier = modifier
            .fillMaxWidth()
            .clickable(onClick = onClick)
            .testTag(testTag)
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(14.dp),
            verticalAlignment = Alignment.Top,
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            Row(
                verticalAlignment = Alignment.Top,
                modifier = Modifier.weight(1f)
            ) {
                Box(
                    modifier = Modifier
                        .size(36.dp)
                        .clip(RoundedCornerShape(8.dp))
                        .background(if (isSelected) CopilotTheme.PurpleBadgeBg else CopilotTheme.UserBubble),
                    contentAlignment = Alignment.Center
                ) {
                    Icon(
                        imageVector = icon,
                        contentDescription = null,
                        tint = if (isSelected) CopilotTheme.PurpleLight else CopilotTheme.TextMuted,
                        modifier = Modifier.size(20.dp)
                    )
                }

                Spacer(modifier = Modifier.width(12.dp))

                Column(modifier = Modifier.padding(end = 8.dp)) {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Text(
                            text = title,
                            color = CopilotTheme.TextBright,
                            fontSize = 13.5.sp,
                            fontWeight = FontWeight.SemiBold
                        )
                        Spacer(modifier = Modifier.width(6.dp))
                        Box(
                            modifier = Modifier
                                .clip(RoundedCornerShape(4.dp))
                                .background(if (isSelected) CopilotTheme.PurpleBadgeBg else CopilotTheme.UserBubble)
                                .padding(horizontal = 6.dp, vertical = 2.dp)
                        ) {
                            Text(
                                text = tag,
                                color = if (isSelected) CopilotTheme.PurpleLight else CopilotTheme.TextMuted,
                                fontSize = 10.sp,
                                fontWeight = FontWeight.Bold
                            )
                        }
                    }
                    Spacer(modifier = Modifier.height(3.dp))
                    Text(
                        text = description,
                        color = CopilotTheme.TextMuted,
                        fontSize = 11.5.sp,
                        lineHeight = 15.sp
                    )
                }
            }

            Box(
                modifier = Modifier
                    .size(20.dp)
                    .clip(CircleShape)
                    .border(
                        1.5.dp,
                        if (isSelected) CopilotTheme.PurplePrimary else CopilotTheme.TextFaint,
                        CircleShape
                    )
                    .background(if (isSelected) CopilotTheme.PurplePrimary else Color.Transparent),
                contentAlignment = Alignment.Center
            ) {
                if (isSelected) {
                    Icon(
                        imageVector = Icons.Default.Check,
                        contentDescription = null,
                        tint = Color.White,
                        modifier = Modifier.size(12.dp)
                    )
                }
            }
        }
    }
}
