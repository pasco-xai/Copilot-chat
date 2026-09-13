package com.example.ui.components

import androidx.compose.foundation.Image
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
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import com.example.R
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
            Image(
                painter = painterResource(id = R.drawable.ic_app_logo),
                contentDescription = "App Logo",
                modifier = Modifier.size((badgeSize.value * 0.72f).dp)
            )
        }
    }
}


