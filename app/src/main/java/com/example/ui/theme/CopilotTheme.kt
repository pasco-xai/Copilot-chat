package com.example.ui.theme

import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontFamily

object CopilotTheme {
    // Canvas & Containers (GitHub Dark Default)
    val CanvasDark = Color(0xFF0D1117)
    val PanelDark = Color(0xFF161B22)
    val PanelHeaderDark = Color(0xFF161B22)
    val UserBubble = Color(0xFF21262D)
    val UserBubbleBorder = Color(0xFF30363D)
    val BorderSubtle = Color(0xFF30363D)
    val BorderActive = Color(0xFF8957E5)
    val DividerDark = Color(0xFF21262D)

    // Text & Foregrounds
    val TextBright = Color(0xFFF0F6FC)
    val TextMain = Color(0xFFC9D1D9)
    val TextMuted = Color(0xFF8B949E)
    val TextFaint = Color(0xFF484F58)

    // Copilot Purple Accents
    val PurplePrimary = Color(0xFF8957E5)
    val PurpleLight = Color(0xFFA371F7)
    val PurpleDark = Color(0xFF6E40C9)
    val PurpleGlow = Color(0x338957E5)
    val PurpleBadgeBg = Color(0x1F8957E5)

    val PurpleGradient = Brush.linearGradient(
        listOf(
            Color(0xFF6E40C9),
            Color(0xFFA371F7)
        )
    )

    // Code & One Dark Pro / GitHub Syntax Tokens
    val CodeBg = Color(0xFF090D12)
    val CodeHeaderBg = Color(0xFF13171F)
    val SyntaxKeyword = Color(0xFFFF7B72)     // fun, val, var, class, if, else
    val SyntaxFunction = Color(0xFFD2A8FF)    // functions, Composable annotations
    val SyntaxString = Color(0xFFA5D6FF)      // string literals
    val SyntaxComment = Color(0xFF8B949E)     // comments
    val SyntaxNumber = Color(0xFF79C0FF)      // numeric literals
    val SyntaxType = Color(0xFFFFA657)        // Types, Classes
    val SyntaxOperator = Color(0xFFFF7B72)    // symbols

    // Status Colors
    val StatusSuccess = Color(0xFF3FB950)
    val StatusDanger = Color(0xFFF85149)
    val StatusWarning = Color(0xFFD29922)
    val LinkBlue = Color(0xFF58A6FF)

    // Font Family (Monospace as required by user prompt)
    val MonoFont = FontFamily.Monospace
}
