package com.example.ui.components

import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.geometry.CornerRadius
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Path
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import com.example.ui.theme.CopilotTheme

@Composable
fun OfficialCopilotBadge(
    modifier: Modifier = Modifier,
    size: Dp = 28.dp
) {
    val cornerRadius = (size.value * 0.28f).dp
    val innerCornerRadius = (size.value * 0.24f).dp
    val badgeSize = size

    Box(
        modifier = modifier
            .size(badgeSize)
            .clip(RoundedCornerShape(cornerRadius))
            .background(CopilotTheme.PurpleGradient)
            .padding(1.dp),
        contentAlignment = Alignment.Center
    ) {
        Box(
            modifier = Modifier
                .fillMaxSize()
                .clip(RoundedCornerShape(innerCornerRadius))
                .background(CopilotTheme.PanelDark),
            contentAlignment = Alignment.Center
        ) {
            Canvas(modifier = Modifier.size((badgeSize.value * 0.72f).dp)) {
                val w = this.size.width
                val h = this.size.height

                val blueColor = Color(0xFF3B82F6)
                val lightBlue = Color(0xFF60A5FA)
                val purpleColor = Color(0xFF8B5CF6)
                val pinkColor = Color(0xFFEC4899)
                val greenColor = Color(0xFF22C55E)
                val darkFace = Color(0xFF0F172A)

                // 1. Purple Ear Pods (left & right)
                drawRoundRect(
                    color = purpleColor,
                    topLeft = Offset(w * 0.05f, h * 0.38f),
                    size = Size(w * 0.18f, h * 0.32f),
                    cornerRadius = CornerRadius(w * 0.08f, w * 0.08f)
                )
                drawRoundRect(
                    color = purpleColor,
                    topLeft = Offset(w * 0.77f, h * 0.38f),
                    size = Size(w * 0.18f, h * 0.32f),
                    cornerRadius = CornerRadius(w * 0.08f, w * 0.08f)
                )

                // 2. Pink top crest
                drawRoundRect(
                    color = pinkColor,
                    topLeft = Offset(w * 0.38f, h * 0.08f),
                    size = Size(w * 0.24f, h * 0.14f),
                    cornerRadius = CornerRadius(w * 0.06f, w * 0.06f)
                )

                // 3. Main Blue Visor Outline / Head Frame
                drawRoundRect(
                    color = blueColor,
                    topLeft = Offset(w * 0.18f, h * 0.18f),
                    size = Size(w * 0.64f, h * 0.68f),
                    cornerRadius = CornerRadius(w * 0.22f, w * 0.22f)
                )

                // 4. Dark inner face area
                drawRoundRect(
                    color = darkFace,
                    topLeft = Offset(w * 0.22f, h * 0.22f),
                    size = Size(w * 0.56f, h * 0.60f),
                    cornerRadius = CornerRadius(w * 0.18f, w * 0.18f)
                )

                // 5. Left and Right Eye Sockets (Dark)
                drawRoundRect(
                    color = Color(0xFF030712),
                    topLeft = Offset(w * 0.27f, h * 0.30f),
                    size = Size(w * 0.18f, h * 0.20f),
                    cornerRadius = CornerRadius(w * 0.06f, w * 0.06f)
                )
                drawRoundRect(
                    color = Color(0xFF030712),
                    topLeft = Offset(w * 0.55f, h * 0.30f),
                    size = Size(w * 0.18f, h * 0.20f),
                    cornerRadius = CornerRadius(w * 0.06f, w * 0.06f)
                )

                // 6. Lower face plate / mouth with two vertical glowing green bars
                drawRoundRect(
                    color = Color(0xFF1E293B),
                    topLeft = Offset(w * 0.28f, h * 0.55f),
                    size = Size(w * 0.44f, h * 0.22f),
                    cornerRadius = CornerRadius(w * 0.08f, w * 0.08f)
                )

                // Two vertical green battery/light indicator bars
                drawRoundRect(
                    color = greenColor,
                    topLeft = Offset(w * 0.38f, h * 0.59f),
                    size = Size(w * 0.08f, h * 0.14f),
                    cornerRadius = CornerRadius(w * 0.03f, w * 0.03f)
                )
                drawRoundRect(
                    color = greenColor,
                    topLeft = Offset(w * 0.54f, h * 0.59f),
                    size = Size(w * 0.08f, h * 0.14f),
                    cornerRadius = CornerRadius(w * 0.03f, w * 0.03f)
                )
            }
        }
    }
}


