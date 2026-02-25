package com.jaffetvr.syncbid.features.users.presentation.components

import androidx.compose.animation.core.*
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.scale
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.jaffetvr.syncbid.core.ui.theme.*

@Composable
fun LiveStrip(
    liveCount: Int,
    modifier: Modifier = Modifier
) {
    val infiniteTransition = rememberInfiniteTransition(label = "pulse")
    val scale by infiniteTransition.animateFloat(
        initialValue = 1f,
        targetValue = 1.4f,
        animationSpec = infiniteRepeatable(
            animation = tween(1000, easing = EaseInOutSine),
            repeatMode = RepeatMode.Reverse
        ),
        label = "pulse-scale"
    )

    Row(
        modifier = modifier
            .fillMaxWidth()
            .background(GreenSubtle, RoundedCornerShape(8.dp))
            .border(1.dp, GreenBorder, RoundedCornerShape(8.dp))
            .padding(horizontal = 12.dp, vertical = 8.dp),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.spacedBy(8.dp)
    ) {
        // Punto animado de "live"
        Box(
            modifier = Modifier
                .size(6.dp)
                .scale(scale)
                .background(Green, CircleShape)
        )
        Text(
            text = "Tiempo real · StateFlow activo",
            fontSize = 11.sp,
            fontWeight = FontWeight.Medium,
            color = Green
        )
        Spacer(modifier = Modifier.weight(1f))
        Text(
            text = "$liveCount activas",
            style = androidx.compose.material3.MaterialTheme.typography.displaySmall,
            color = White30
        )
    }
}

private val EaseInOutSine = CubicBezierEasing(0.37f, 0f, 0.63f, 1f)
