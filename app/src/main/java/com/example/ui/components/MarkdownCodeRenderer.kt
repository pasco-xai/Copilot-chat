package com.example.ui.components

import android.widget.Toast
import androidx.compose.animation.AnimatedContent
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.togetherWith
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.horizontalScroll
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.filled.ContentCopy
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalClipboardManager
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.AnnotatedString
import androidx.compose.ui.text.SpanStyle
import androidx.compose.ui.text.buildAnnotatedString
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.ui.theme.CopilotTheme
import kotlinx.coroutines.delay

@Composable
fun MarkdownCodeRenderer(
    content: String,
    modifier: Modifier = Modifier
) {
    val segments = remember(content) {
        parseMarkdownSegments(content)
    }

    Column(
        modifier = modifier.fillMaxWidth(),
        verticalArrangement = Arrangement.spacedBy(8.dp)
    ) {
        segments.forEach { segment ->
            when (segment) {
                is MarkdownSegment.CodeBlock -> {
                    CodeBlockView(
                        language = segment.language,
                        code = segment.code
                    )
                }
                is MarkdownSegment.Paragraph -> {
                    FormattedParagraphView(text = segment.text)
                }
            }
        }
    }
}

sealed class MarkdownSegment {
    data class CodeBlock(val language: String, val code: String) : MarkdownSegment()
    data class Paragraph(val text: String) : MarkdownSegment()
}

private fun parseMarkdownSegments(content: String): List<MarkdownSegment> {
    if (!content.contains("```")) {
        return listOf(MarkdownSegment.Paragraph(content))
    }

    val result = mutableListOf<MarkdownSegment>()
    val parts = content.split("```")

    parts.forEachIndexed { index, part ->
        if (index % 2 == 1) {
            // Code Block
            val lines = part.trim().lines()
            val firstLine = lines.firstOrNull()?.trim() ?: ""
            val hasLang = firstLine.isNotEmpty() && !firstLine.contains(" ") && firstLine.length < 20
            val lang = if (hasLang) firstLine else "kotlin"
            val codeBody = if (hasLang && lines.size > 1) {
                lines.drop(1).joinToString("\n")
            } else {
                part.trim()
            }
            result.add(MarkdownSegment.CodeBlock(language = lang, code = codeBody))
        } else {
            // Regular Text
            if (part.isNotBlank()) {
                result.add(MarkdownSegment.Paragraph(text = part.trim()))
            }
        }
    }
    return result
}

@Composable
fun CodeBlockView(
    language: String,
    code: String,
    modifier: Modifier = Modifier
) {
    val clipboardManager = LocalClipboardManager.current
    val context = LocalContext.current
    var copied by remember { mutableStateOf(false) }

    LaunchedEffect(copied) {
        if (copied) {
            delay(2000)
            copied = false
        }
    }

    val highlightedCode = remember(code) {
        highlightOneDarkPro(code)
    }

    Column(
        modifier = modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(8.dp))
            .background(CopilotTheme.CodeBg)
            .border(0.5.dp, CopilotTheme.BorderSubtle, RoundedCornerShape(8.dp))
    ) {
        // Code Block Header
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .background(CopilotTheme.CodeHeaderBg)
                .border(
                    width = 0.5.dp,
                    color = CopilotTheme.BorderSubtle,
                    shape = RoundedCornerShape(topStart = 8.dp, topEnd = 8.dp)
                )
                .padding(horizontal = 12.dp, vertical = 6.dp),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Row(verticalAlignment = Alignment.CenterVertically) {
                Box(
                    modifier = Modifier
                        .size(8.dp)
                        .clip(RoundedCornerShape(2.dp))
                        .background(CopilotTheme.PurplePrimary)
                )
                Spacer(modifier = Modifier.width(6.dp))
                Text(
                    text = language.lowercase(),
                    color = CopilotTheme.PurpleLight,
                    fontSize = 11.5.sp,
                    fontWeight = FontWeight.SemiBold,
                    fontFamily = CopilotTheme.MonoFont
                )
            }

            IconButton(
                onClick = {
                    clipboardManager.setText(AnnotatedString(code))
                    copied = true
                    Toast.makeText(context, "Code in Zwischenablage kopiert", Toast.LENGTH_SHORT).show()
                },
                modifier = Modifier.size(24.dp)
            ) {
                AnimatedContent(
                    targetState = copied,
                    transitionSpec = { fadeIn() togetherWith fadeOut() },
                    label = "CopyIcon"
                ) { isCopied ->
                    if (isCopied) {
                        Icon(
                            imageVector = Icons.Default.Check,
                            contentDescription = "Kopiert",
                            tint = CopilotTheme.StatusSuccess,
                            modifier = Modifier.size(14.dp)
                        )
                    } else {
                        Icon(
                            imageVector = Icons.Default.ContentCopy,
                            contentDescription = "Code kopieren",
                            tint = CopilotTheme.TextMuted,
                            modifier = Modifier.size(14.dp)
                        )
                    }
                }
            }
        }

        // Code Content with Horizontal Scroll
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .horizontalScroll(rememberScrollState())
                .padding(horizontal = 12.dp, vertical = 10.dp)
        ) {
            Text(
                text = highlightedCode,
                fontFamily = CopilotTheme.MonoFont,
                fontSize = 12.sp,
                lineHeight = 18.sp
            )
        }
    }
}

@Composable
fun FormattedParagraphView(
    text: String,
    modifier: Modifier = Modifier
) {
    val annotated = remember(text) {
        formatInlineMarkdown(text)
    }

    Text(
        text = annotated,
        color = CopilotTheme.TextMain,
        fontFamily = CopilotTheme.MonoFont,
        fontSize = 13.sp,
        lineHeight = 19.sp,
        modifier = modifier
    )
}

// Token-based One Dark Pro / GitHub Dark Syntax Highlighter
fun highlightOneDarkPro(code: String): AnnotatedString {
    val keywords = setOf(
        "fun", "val", "var", "if", "else", "when", "return", "class", "interface",
        "object", "package", "import", "true", "false", "null", "for", "while",
        "do", "try", "catch", "finally", "throw", "is", "in", "by", "suspend",
        "sealed", "data", "enum", "override", "companion", "private", "public",
        "internal", "protected", "inline", "crossinline", "noinline", "reified"
    )

    val types = setOf(
        "String", "Int", "Boolean", "Long", "Float", "Double", "List", "Map",
        "Set", "Flow", "StateFlow", "SharedFlow", "Modifier", "Composable",
        "CoroutineScope", "Job", "Unit", "Any", "ChatMessage", "CopilotConfig"
    )

    return buildAnnotatedString {
        val lines = code.split("\n")
        lines.forEachIndexed { lineIdx, line ->
            var i = 0
            val len = line.length

            while (i < len) {
                // Line comment
                if (i < len - 1 && line[i] == '/' && line[i + 1] == '/') {
                    pushStyle(SpanStyle(color = CopilotTheme.SyntaxComment))
                    append(line.substring(i))
                    pop()
                    i = len
                    break
                }

                // Annotation (@Composable)
                if (line[i] == '@') {
                    val start = i
                    i++
                    while (i < len && (line[i].isLetterOrDigit() || line[i] == '_')) {
                        i++
                    }
                    pushStyle(SpanStyle(color = CopilotTheme.SyntaxFunction, fontWeight = FontWeight.Bold))
                    append(line.substring(start, i))
                    pop()
                    continue
                }

                // String literal
                if (line[i] == '"') {
                    val start = i
                    i++
                    while (i < len && line[i] != '"') {
                        if (line[i] == '\\' && i + 1 < len) {
                            i++
                        }
                        i++
                    }
                    if (i < len) i++ // include closing quote
                    pushStyle(SpanStyle(color = CopilotTheme.SyntaxString))
                    append(line.substring(start, i))
                    pop()
                    continue
                }

                // Word (Keyword, Type, Function, Identifier)
                if (line[i].isLetter() || line[i] == '_') {
                    val start = i
                    while (i < len && (line[i].isLetterOrDigit() || line[i] == '_')) {
                        i++
                    }
                    val word = line.substring(start, i)

                    when {
                        word in keywords -> {
                            pushStyle(SpanStyle(color = CopilotTheme.SyntaxKeyword, fontWeight = FontWeight.SemiBold))
                            append(word)
                            pop()
                        }
                        word in types -> {
                            pushStyle(SpanStyle(color = CopilotTheme.SyntaxType))
                            append(word)
                            pop()
                        }
                        i < len && line[i] == '(' -> {
                            pushStyle(SpanStyle(color = CopilotTheme.SyntaxFunction))
                            append(word)
                            pop()
                        }
                        else -> {
                            pushStyle(SpanStyle(color = CopilotTheme.TextBright))
                            append(word)
                            pop()
                        }
                    }
                    continue
                }

                // Numbers
                if (line[i].isDigit()) {
                    val start = i
                    while (i < len && (line[i].isDigit() || line[i] == '.' || line[i] == 'L' || line[i] == 'f' || line[i] == 'x')) {
                        i++
                    }
                    pushStyle(SpanStyle(color = CopilotTheme.SyntaxNumber))
                    append(line.substring(start, i))
                    pop()
                    continue
                }

                // Operators and punctuation
                val ch = line[i]
                if (ch in "=+-*/%&|!<>?:.") {
                    pushStyle(SpanStyle(color = CopilotTheme.SyntaxOperator))
                    append(ch.toString())
                    pop()
                } else {
                    pushStyle(SpanStyle(color = CopilotTheme.TextMain))
                    append(ch.toString())
                    pop()
                }
                i++
            }

            if (lineIdx < lines.size - 1) {
                append("\n")
            }
        }
    }
}

fun formatInlineMarkdown(text: String): AnnotatedString {
    return buildAnnotatedString {
        var i = 0
        val len = text.length

        while (i < len) {
            // Bold (**text**)
            if (i < len - 1 && text[i] == '*' && text[i + 1] == '*') {
                val end = text.indexOf("**", i + 2)
                if (end != -1) {
                    val boldText = text.substring(i + 2, end)
                    pushStyle(SpanStyle(color = CopilotTheme.TextBright, fontWeight = FontWeight.Bold))
                    append(boldText)
                    pop()
                    i = end + 2
                    continue
                }
            }

            // Inline Code (`code`)
            if (text[i] == '`') {
                val end = text.indexOf('`', i + 1)
                if (end != -1) {
                    val codeText = text.substring(i + 1, end)
                    pushStyle(
                        SpanStyle(
                            color = CopilotTheme.PurpleLight,
                            background = CopilotTheme.UserBubble,
                            fontWeight = FontWeight.Medium
                        )
                    )
                    append(" $codeText ")
                    pop()
                    i = end + 1
                    continue
                }
            }

            append(text[i])
            i++
        }
    }
}
