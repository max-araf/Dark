package com.kazesu.manager.ui.component

import androidx.compose.animation.animateColorAsState
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.spring
import androidx.compose.animation.core.tween
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.defaultMinSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.selection.toggleable
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Check
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.draw.clip
import androidx.compose.ui.geometry.CornerRadius
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.graphics.lerp
import androidx.compose.ui.graphics.toArgb
import androidx.compose.ui.hapticfeedback.HapticFeedbackType
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalHapticFeedback
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.semantics.Role
import androidx.compose.ui.semantics.semantics
import androidx.compose.ui.semantics.stateDescription
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.drawText
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.rememberTextMeasurer
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.kazesu.manager.R
import com.kazesu.manager.ui.theme.DarkNightGlassCard
import com.kazesu.manager.ui.theme.DarkNightThemeStore
import com.kazesu.manager.ui.theme.DarkNightToggleConfig
import com.kazesu.manager.ui.theme.LocalDarkNightGlass
import com.kazesu.manager.ui.theme.LocalDarkNightToggle
import com.kazesu.manager.ui.theme.ToggleColorMode
import com.kazesu.manager.ui.theme.ToggleStyle
import com.kazesu.manager.ui.theme.contrastOn

private val CustomToggleSwatches = listOf(
    Color(0xFF2563FF), // Aura
    Color(0xFFC50337), // Crimson
    Color(0xFF07D0A4), // Mint
    Color(0xFFFFB84D), // Gold
    Color(0xFF9303C5), // Violet
    Color(0xFFFF6A1A), // Neon Orange
)

/**
 * PART 4: Unified 8-style Toggle Switch ported directly from darknight_toggles_reference.html.
 * Zero maskFilter / RenderEffect on Canvas (low-end Galaxy A13 safe); 48dp accessible touch target.
 */
@Composable
fun DarkNightToggle(
    checked: Boolean,
    onCheckedChange: ((Boolean) -> Unit)?,
    modifier: Modifier = Modifier,
    enabled: Boolean = true,
    overrideConfig: DarkNightToggleConfig? = null,
) {
    val context = LocalContext.current
    DarkNightThemeStore.init(context)
    val dnState by DarkNightThemeStore.state.collectAsStateWithLifecycle()
    val glass = LocalDarkNightGlass.current
    val config = overrideConfig ?: LocalDarkNightToggle.current
    val style = config.style

    val targetAccent = when (config.colorMode) {
        ToggleColorMode.FOLLOW_PALETTE -> glass.accent
        ToggleColorMode.ORIGINAL -> if (style == ToggleStyle.RING && !glass.isDark) Color(0xFF0B1020) else style.originalColor
        ToggleColorMode.CUSTOM -> config.customColor
    }
    val a by animateColorAsState(targetAccent, tween(300), label = "tg_accent")
    val l = lerp(a, Color.White, 0.38f)
    val d = lerp(a, Color.Black, 0.45f)

    val progress by animateFloatAsState(
        targetValue = if (checked) 1f else 0f,
        animationSpec = if (dnState.startupAnimEnabled) {
            spring(dampingRatio = 0.72f, stiffness = 480f)
        } else {
            tween(160)
        },
        label = "tg_progress",
    )

    val sizeScale = when (config.size) {
        0 -> 0.85f
        2 -> 1.15f
        else -> 1.0f
    }
    val visualH = 26.dp * sizeScale
    val visualW = visualH * style.ratio
    val textMeasurer = rememberTextMeasurer()

    val toggleMod = if (onCheckedChange != null) {
        Modifier
            .defaultMinSize(minWidth = 48.dp, minHeight = 48.dp)
            .semantics { stateDescription = if (checked) "On" else "Off" }
            .toggleable(
                value = checked,
                enabled = enabled,
                role = Role.Switch,
                interactionSource = remember { MutableInteractionSource() },
                indication = null,
                onValueChange = onCheckedChange,
            )
    } else {
        Modifier
    }

    Box(
        modifier = modifier
            .then(toggleMod)
            .alpha(if (enabled) 1f else 0.38f),
        contentAlignment = Alignment.Center,
    ) {
        Canvas(modifier = Modifier.size(width = visualW, height = visualH)) {
            val h = size.height
            val w = size.width
            val em = h
            val rTrack = CornerRadius(h / 2f, h / 2f)

            when (style) {
                ToggleStyle.NEON -> {
                    // Black recessed track + 0.05em white rim
                    drawRoundRect(
                        brush = Brush.verticalGradient(listOf(Color(0xFF050506), Color(0xFF17171B))),
                        cornerRadius = rTrack,
                    )
                    drawRoundRect(
                        color = Color.White.copy(alpha = 0.22f),
                        cornerRadius = rTrack,
                        style = Stroke(width = 0.05f * em),
                    )
                    // ON/OFF label
                    val lbl = if (progress > 0.5f) "ON" else "OFF"
                    val lay = textMeasurer.measure(
                        lbl,
                        TextStyle(
                            color = Color.White.copy(alpha = if (progress > 0.5f) 0.82f else 0.45f),
                            fontSize = (0.30f * em).toSp(),
                            fontWeight = FontWeight.ExtraBold,
                        ),
                    )
                    val lx = if (progress > 0.5f) 0.28f * em else w - 0.26f * em - lay.size.width
                    drawText(lay, topLeft = Offset(lx, (h - lay.size.height) / 2f))

                    // Glow + Orb when ON
                    if (progress > 0.05f) {
                        val gc = Offset(0.95f * em, h / 2f)
                        drawCircle(a.copy(alpha = 0.35f * progress), radius = 0.65f * em, center = gc)
                        drawCircle(
                            brush = Brush.radialGradient(listOf(l, a), center = Offset(1.02f * em, 0.45f * em), radius = 0.42f * em),
                            radius = 0.36f * em,
                            center = Offset(1.05f * em, h / 2f),
                            alpha = progress,
                        )
                    }
                    // Frosted Glass Thumb (1.5em x 1.0em)
                    val tw = 1.46f * em
                    val th = 0.96f * em
                    val tx = 0.02f * em + (w - tw - 0.04f * em) * progress
                    val ty = (h - th) / 2f
                    val thBrush = if (progress > 0.5f) {
                        Brush.linearGradient(
                            listOf(lerp(l, Color.White, 0.45f).copy(alpha = 0.85f), a.copy(alpha = 0.55f), l.copy(alpha = 0.65f)),
                            start = Offset(tx, ty),
                            end = Offset(tx + tw, ty + th),
                        )
                    } else {
                        Brush.linearGradient(
                            listOf(Color.White.copy(alpha = 0.58f), Color.White.copy(alpha = 0.20f), Color.White.copy(alpha = 0.36f)),
                            start = Offset(tx, ty),
                            end = Offset(tx + tw, ty + th),
                        )
                    }
                    drawRoundRect(
                        brush = thBrush,
                        topLeft = Offset(tx, ty),
                        size = Size(tw, th),
                        cornerRadius = CornerRadius(th / 2f, th / 2f),
                    )
                    drawRoundRect(
                        color = Color.White.copy(alpha = 0.58f),
                        topLeft = Offset(tx, ty),
                        size = Size(tw, th),
                        cornerRadius = CornerRadius(th / 2f, th / 2f),
                        style = Stroke(width = 0.03f * em),
                    )
                    // Dot grid inside left of thumb
                    for (gx in 0 until 4) {
                        for (gy in 0 until 3) {
                            drawCircle(
                                color = Color.White.copy(alpha = 0.80f),
                                radius = 0.028f * em,
                                center = Offset(tx + (0.28f + gx * 0.14f) * em, ty + (0.30f + gy * 0.17f) * em),
                            )
                        }
                    }
                    // Lens circle inside right of thumb
                    val lc = Offset(tx + tw - 0.48f * em, ty + th / 2f)
                    drawCircle(Color.White.copy(alpha = 0.28f), radius = 0.36f * em, center = lc)
                    drawCircle(Color.White.copy(alpha = 0.55f), radius = 0.36f * em, center = lc, style = Stroke(0.025f * em))
                }

                ToggleStyle.TACTILE -> {
                    val trkTop = lerp(Color(0xFF3B3B3D), a, progress)
                    val trkBot = lerp(Color(0xFF5A5A5C), l, progress)
                    drawRoundRect(brush = Brush.verticalGradient(listOf(trkTop, trkBot)), cornerRadius = rTrack)
                    drawRoundRect(color = Color(0xFFD9D9DC), cornerRadius = rTrack, style = Stroke(0.05f * em))
                    if (progress < 0.5f) {
                        drawCircle(
                            color = Color.White.copy(alpha = 0.30f),
                            radius = 0.13f * em,
                            center = Offset(w - 0.33f * em, h / 2f),
                            style = Stroke(0.035f * em),
                        )
                    } else {
                        drawRoundRect(
                            color = Color.Black.copy(alpha = 0.22f),
                            topLeft = Offset(0.20f * em, 0.30f * em),
                            size = Size(0.05f * em, 0.40f * em),
                            cornerRadius = CornerRadius(0.03f * em, 0.03f * em),
                        )
                    }
                    val rBall = 0.43f * em
                    val cx = 0.07f * em + rBall + (w - 0.14f * em - 2f * rBall) * progress
                    drawCircle(
                        brush = Brush.radialGradient(
                            listOf(Color.White, Color(0xFFE7E7E9), Color(0xFFCFD0D4)),
                            center = Offset(cx - 0.10f * em, h * 0.35f),
                            radius = rBall * 1.4f,
                        ),
                        radius = rBall,
                        center = Offset(cx, h / 2f),
                    )
                }

                ToggleStyle.HARDWARE -> {
                    drawRoundRect(color = Color(0xFF1A1C20), cornerRadius = rTrack)
                    drawRoundRect(color = Color(0xFF30333A), cornerRadius = rTrack, style = Stroke(0.045f * em))
                    val tw = 1.62f * em
                    val th = 0.86f * em
                    val tx = 0.07f * em + (w - tw - 0.14f * em) * progress
                    val ty = (h - th) / 2f
                    drawRoundRect(
                        brush = Brush.verticalGradient(listOf(Color(0xFF363940), Color(0xFF25282D))),
                        topLeft = Offset(tx, ty),
                        size = Size(tw, th),
                        cornerRadius = CornerRadius(th / 2f, th / 2f),
                    )
                    // 3 grip grooves
                    for (g in 0 until 3) {
                        drawRoundRect(
                            color = Color(0xFF14161A),
                            topLeft = Offset(tx + (0.28f + g * 0.16f) * em, ty + 0.22f * em),
                            size = Size(0.075f * em, 0.42f * em),
                            cornerRadius = CornerRadius(0.03f * em, 0.03f * em),
                        )
                    }
                    // LED slot (glows with 2-pass alpha halo when ON)
                    val ledX = tx + tw - 0.36f * em
                    val ledY = ty + 0.21f * em
                    if (progress > 0.1f) {
                        drawRoundRect(
                            color = a.copy(alpha = 0.28f * progress),
                            topLeft = Offset(ledX - 0.10f * em, ledY - 0.10f * em),
                            size = Size(0.31f * em, 0.64f * em),
                            cornerRadius = CornerRadius(0.15f * em, 0.15f * em),
                        )
                    }
                    drawRoundRect(
                        color = lerp(Color(0xFFB8BAC0), l, progress),
                        topLeft = Offset(ledX, ledY),
                        size = Size(0.11f * em, 0.44f * em),
                        cornerRadius = CornerRadius(0.055f * em, 0.055f * em),
                    )
                }

                ToggleStyle.CLASSIC -> {
                    val offTop = if (glass.isDark) Color(0xFF2D3037) else Color(0xFFCFD1D6)
                    val offBot = if (glass.isDark) Color(0xFF3B3F48) else Color(0xFFE3E5E9)
                    val trkTop = lerp(offTop, a, progress)
                    val trkBot = lerp(offBot, l, progress)
                    drawRoundRect(brush = Brush.verticalGradient(listOf(trkTop, trkBot)), cornerRadius = rTrack)
                    val lbl = if (progress > 0.5f) "ON" else "OFF"
                    val lay = textMeasurer.measure(
                        lbl,
                        TextStyle(
                            color = if (progress > 0.5f) contrastOn(a).copy(alpha = 0.85f) else Color.White.copy(alpha = 0.92f),
                            fontSize = (0.34f * em).toSp(),
                            fontWeight = FontWeight.Bold,
                        ),
                    )
                    val lx = if (progress > 0.5f) 0.24f * em else w - 0.20f * em - lay.size.width
                    drawText(lay, topLeft = Offset(lx, (h - lay.size.height) / 2f))
                    val rBall = 0.43f * em
                    val cx = 0.07f * em + rBall + (w - 0.14f * em - 2f * rBall) * progress
                    drawCircle(
                        brush = Brush.radialGradient(
                            listOf(Color.White, Color(0xFFF0F0F2), Color(0xFFE1E2E6)),
                            center = Offset(cx - 0.08f * em, h * 0.30f),
                            radius = rBall * 1.4f,
                        ),
                        radius = rBall,
                        center = Offset(cx, h / 2f),
                    )
                }

                ToggleStyle.RING -> {
                    val offStroke = if (glass.isDark) Color.White.copy(alpha = 0.24f) else Color(0xFF0B1020).copy(alpha = 0.20f)
                    val ringColor = lerp(offStroke, a, progress)
                    val sw = 0.22f * em
                    val pad = sw / 2f
                    if (progress > 0.2f && glass.isDark) {
                        drawRoundRect(
                            color = a.copy(alpha = 0.24f * progress),
                            topLeft = Offset(pad, pad),
                            size = Size(w - sw, h - sw),
                            cornerRadius = CornerRadius((h - sw) / 2f, (h - sw) / 2f),
                            style = Stroke(width = sw * 1.45f),
                        )
                    }
                    drawRoundRect(
                        color = ringColor,
                        topLeft = Offset(pad, pad),
                        size = Size(w - sw, h - sw),
                        cornerRadius = CornerRadius((h - sw) / 2f, (h - sw) / 2f),
                        style = Stroke(width = sw),
                    )
                    val rBall = 0.18f * em
                    val cx = 0.46f * em + (w - 0.92f * em) * progress
                    drawCircle(
                        brush = Brush.radialGradient(
                            listOf(Color(0xFFD8D8DC), Color(0xFF8A8B90), Color(0xFF5C5D62)),
                            center = Offset(cx - 0.04f * em, h * 0.42f),
                            radius = rBall * 1.6f,
                        ),
                        radius = rBall,
                        center = Offset(cx, h / 2f),
                    )
                }

                ToggleStyle.ORB -> {
                    drawRoundRect(
                        brush = Brush.verticalGradient(listOf(Color(0xFF0E0E10), Color(0xFF1A1A1D))),
                        cornerRadius = rTrack,
                    )
                    drawRoundRect(color = Color.White.copy(alpha = 0.28f), cornerRadius = rTrack, style = Stroke(0.035f * em))
                    val rOrb = 0.39f * em
                    val cx = 0.11f * em + rOrb
                    if (progress > 0.05f) {
                        drawCircle(a.copy(alpha = 0.32f * progress), radius = 0.56f * em, center = Offset(cx + 0.15f * em, h / 2f))
                    }
                    val orbBrush = if (progress > 0.5f) {
                        Brush.radialGradient(listOf(l, a, d), center = Offset(cx - 0.08f * em, h * 0.38f), radius = rOrb * 1.3f)
                    } else {
                        Brush.radialGradient(listOf(Color(0xFF3A3A3E), Color(0xFF1E1E21)), center = Offset(cx, h * 0.40f), radius = rOrb * 1.3f)
                    }
                    drawCircle(brush = orbBrush, radius = rOrb, center = Offset(cx, h / 2f))
                    val lbl = if (progress > 0.5f) "ON" else "OFF"
                    val lay = textMeasurer.measure(
                        lbl,
                        TextStyle(
                            color = Color.White.copy(alpha = if (progress > 0.5f) 0.78f else 0.32f),
                            fontSize = (0.30f * em).toSp(),
                            fontWeight = FontWeight.Black,
                        ),
                    )
                    drawText(lay, topLeft = Offset(w - 0.22f * em - lay.size.width, (h - lay.size.height) / 2f))
                }

                ToggleStyle.DOTS -> {
                    drawRoundRect(
                        brush = Brush.verticalGradient(listOf(Color(0xFF14171C), Color(0xFF1F2329))),
                        cornerRadius = rTrack,
                    )
                    drawRoundRect(color = Color(0xFF0B0D11), cornerRadius = rTrack, style = Stroke(0.04f * em))
                    // Neon ON slot on right when ON
                    val slotW = 0.96f * em
                    val slotH = 0.84f * em
                    val slotX = if (progress > 0.5f) w - slotW - 0.08f * em else 0.08f * em
                    val slotY = (h - slotH) / 2f
                    if (progress > 0.5f) {
                        drawRoundRect(
                            color = a.copy(alpha = 0.28f),
                            topLeft = Offset(slotX - 0.06f * em, slotY - 0.06f * em),
                            size = Size(slotW + 0.12f * em, slotH + 0.12f * em),
                            cornerRadius = CornerRadius(slotH / 2f, slotH / 2f),
                        )
                        drawRoundRect(
                            brush = Brush.verticalGradient(listOf(l, a)),
                            topLeft = Offset(slotX, slotY),
                            size = Size(slotW, slotH),
                            cornerRadius = CornerRadius(slotH / 2f, slotH / 2f),
                        )
                    } else {
                        drawRoundRect(
                            color = Color(0xFF101216),
                            topLeft = Offset(slotX, slotY),
                            size = Size(slotW, slotH),
                            cornerRadius = CornerRadius(slotH / 2f, slotH / 2f),
                        )
                    }
                    val lbl = if (progress > 0.5f) "ON" else "OFF"
                    val lay = textMeasurer.measure(
                        lbl,
                        TextStyle(
                            color = if (progress > 0.5f) Color.White else Color(0xFF505665),
                            fontSize = (0.31f * em).toSp(),
                            fontWeight = FontWeight.Black,
                        ),
                    )
                    drawText(lay, topLeft = Offset(slotX + (slotW - lay.size.width) / 2f, (h - lay.size.height) / 2f))
                    // Dark thumb with 4x3 dot grid (left when ON, right when OFF)
                    val tw = 1.30f * em
                    val th = 0.86f * em
                    val tx = (w - tw - 0.07f * em) * (1f - progress) + 0.07f * em * progress
                    val ty = (h - th) / 2f
                    drawRoundRect(
                        brush = Brush.verticalGradient(listOf(Color(0xFF3A3E46), Color(0xFF252830))),
                        topLeft = Offset(tx, ty),
                        size = Size(tw, th),
                        cornerRadius = CornerRadius(th / 2f, th / 2f),
                    )
                    for (gx in 0 until 4) {
                        for (gy in 0 until 3) {
                            drawCircle(
                                color = Color(0xFF13151A),
                                radius = 0.03f * em,
                                center = Offset(tx + (0.36f + gx * 0.16f) * em, ty + (0.28f + gy * 0.15f) * em),
                            )
                        }
                    }
                }

                ToggleStyle.LENS -> {
                    drawRoundRect(
                        brush = Brush.verticalGradient(listOf(Color(0xFF15181F), Color(0xFF0E1016))),
                        cornerRadius = rTrack,
                    )
                    if (progress > 0.05f) {
                        drawRoundRect(
                            brush = Brush.radialGradient(
                                listOf(a.copy(alpha = 0.85f * progress), a.copy(alpha = 0.25f * progress), Color.Transparent),
                                center = Offset(w * 0.5f, h * 0.5f),
                                radius = w * 0.65f,
                            ),
                            cornerRadius = rTrack,
                        )
                    }
                    drawRoundRect(color = Color.White.copy(alpha = 0.30f), cornerRadius = rTrack, style = Stroke(0.035f * em))
                    val lbl = if (progress > 0.5f) "ON" else "OFF"
                    val lay = textMeasurer.measure(
                        lbl,
                        TextStyle(
                            color = Color.White.copy(alpha = if (progress > 0.5f) 0.95f else 0.42f),
                            fontSize = (0.30f * em).toSp(),
                            fontWeight = FontWeight.Bold,
                        ),
                    )
                    val lx = if (progress > 0.5f) 0.28f * em else w - 0.26f * em - lay.size.width
                    drawText(lay, topLeft = Offset(lx, (h - lay.size.height) / 2f))
                    // Refractive glass lens thumb
                    val rLens = 0.46f * em
                    val cx = 0.04f * em + rLens + (w - 0.08f * em - 2f * rLens) * progress
                    drawCircle(
                        brush = Brush.radialGradient(
                            colorStops = arrayOf(
                                0.00f to Color.White.copy(alpha = 0.72f),
                                0.42f to Color.White.copy(alpha = 0.16f),
                                0.72f to Color.White.copy(alpha = 0.06f),
                                1.00f to l.copy(alpha = 0.45f * progress + 0.15f),
                            ),
                            center = Offset(cx - 0.16f * em, h * 0.30f),
                            radius = rLens * 1.35f,
                        ),
                        radius = rLens,
                        center = Offset(cx, h / 2f),
                    )
                    drawCircle(Color.White.copy(alpha = 0.58f), radius = rLens, center = Offset(cx, h / 2f), style = Stroke(0.03f * em))
                }
            }
        }
    }
}

/**
 * PART 4.4: Settings -> Theme -> Toggle Style Picker (2x4 tile grid, Color mode, Size, Live Preview).
 */
@Composable
fun DarkNightToggleStyleSection(modifier: Modifier = Modifier) {
    val context = LocalContext.current
    val haptic = LocalHapticFeedback.current
    DarkNightThemeStore.init(context)
    val themeState by DarkNightThemeStore.state.collectAsStateWithLifecycle()
    val glass = LocalDarkNightGlass.current
    val activeStyle = themeState.toggleStyle
    val colorMode = themeState.toggleColorMode
    val toggleSize = themeState.toggleSize

    var previewOn by remember { mutableStateOf(true) }
    var previewOff by remember { mutableStateOf(false) }

    DarkNightGlassCard(palette = themeState.palette, useBlur = false, modifier = modifier) {
        Text(
            text = stringResource(R.string.toggle_style_title),
            color = glass.content,
            fontWeight = FontWeight.Bold,
            fontSize = 16.sp,
        )
        Text(
            text = stringResource(R.string.toggle_style_subtitle),
            color = glass.contentSecondary,
            fontSize = 12.sp,
        )

        // 2x4 Live Tile Grid
        ToggleStyle.entries.chunked(2).forEach { pair ->
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(10.dp),
            ) {
                pair.forEach { st ->
                    val isSelected = st == activeStyle
                    val tileShape = RoundedCornerShape(16.dp)
                    Surface(
                        modifier = Modifier
                            .weight(1f)
                            .clip(tileShape)
                            .clickable {
                                haptic.performHapticFeedback(HapticFeedbackType.TextHandleMove)
                                DarkNightThemeStore.setToggleStyle(context, st)
                            },
                        shape = tileShape,
                        color = if (glass.isDark) Color.White.copy(alpha = 0.05f) else Color.White.copy(alpha = 0.65f),
                        border = BorderStroke(
                            width = if (isSelected) 2.dp else 1.dp,
                            color = if (isSelected) glass.accent else glass.content.copy(alpha = 0.14f),
                        ),
                    ) {
                        Box(modifier = Modifier.padding(10.dp)) {
                            if (isSelected) {
                                Box(
                                    modifier = Modifier
                                        .align(Alignment.TopEnd)
                                        .size(18.dp)
                                        .clip(CircleShape)
                                        .background(glass.accent),
                                    contentAlignment = Alignment.Center,
                                ) {
                                    Icon(
                                        imageVector = Icons.Filled.Check,
                                        contentDescription = null,
                                        tint = contrastOn(glass.accent),
                                        modifier = Modifier.size(12.dp),
                                    )
                                }
                            }
                            Column(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .padding(vertical = 4.dp),
                                horizontalAlignment = Alignment.CenterHorizontally,
                                verticalArrangement = Arrangement.spacedBy(8.dp),
                            ) {
                                DarkNightToggle(
                                    checked = true,
                                    onCheckedChange = null,
                                    overrideConfig = themeState.toggleConfig.copy(style = st, size = 0),
                                )
                                Text(
                                    text = st.displayName,
                                    color = glass.content,
                                    fontSize = 11.5.sp,
                                    fontWeight = FontWeight.SemiBold,
                                    maxLines = 1,
                                )
                            }
                        }
                    }
                }
            }
        }

        // Color Mode
        Text(
            text = stringResource(R.string.toggle_color_mode),
            color = glass.contentSecondary,
            fontSize = 11.sp,
            fontWeight = FontWeight.Bold,
        )
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(8.dp),
        ) {
            listOf(
                ToggleColorMode.FOLLOW_PALETTE to stringResource(R.string.toggle_color_follow),
                ToggleColorMode.ORIGINAL to stringResource(R.string.toggle_color_original),
                ToggleColorMode.CUSTOM to stringResource(R.string.toggle_color_custom),
            ).forEach { (mode, label) ->
                DarkNightChip(
                    label = label,
                    selected = colorMode == mode,
                    onClick = { DarkNightThemeStore.setToggleColorMode(context, mode) },
                    modifier = Modifier.weight(1f),
                )
            }
        }

        if (colorMode == ToggleColorMode.CUSTOM) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(10.dp),
                verticalAlignment = Alignment.CenterVertically,
            ) {
                CustomToggleSwatches.forEach { sw ->
                    val activeSw = sw.toArgb() == themeState.toggleCustomColorArgb
                    Box(
                        modifier = Modifier
                            .size(30.dp)
                            .clip(CircleShape)
                            .background(sw)
                            .border(
                                width = if (activeSw) 2.5.dp else 1.dp,
                                color = if (activeSw) glass.content else glass.content.copy(alpha = 0.25f),
                                shape = CircleShape,
                            )
                            .clickable { DarkNightThemeStore.setToggleCustomColor(context, sw.toArgb()) },
                    )
                }
            }
        }

        // Size selector
        Text(
            text = stringResource(R.string.toggle_size_title),
            color = glass.contentSecondary,
            fontSize = 11.sp,
            fontWeight = FontWeight.Bold,
        )
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(8.dp),
        ) {
            listOf(
                0 to stringResource(R.string.toggle_size_small),
                1 to stringResource(R.string.toggle_size_medium),
                2 to stringResource(R.string.toggle_size_large),
            ).forEach { (sz, label) ->
                DarkNightChip(
                    label = label,
                    selected = toggleSize == sz,
                    onClick = { DarkNightThemeStore.setToggleSize(context, sz) },
                    modifier = Modifier.weight(1f),
                )
            }
        }

        // Live Preview Card (ON / OFF / Disabled)
        HorizontalDivider(color = glass.content.copy(alpha = 0.12f))
        Text(
            text = stringResource(R.string.toggle_live_preview),
            color = glass.contentSecondary,
            fontSize = 11.sp,
            fontWeight = FontWeight.Bold,
        )
        CompositionLocalProvider(LocalDarkNightToggle provides themeState.toggleConfig) {
            Column(verticalArrangement = Arrangement.spacedBy(6.dp)) {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .clickable { previewOn = !previewOn },
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically,
                ) {
                    Column(modifier = Modifier.weight(1f)) {
                        Text("Bootloop protection", color = glass.content, fontSize = 13.5.sp, fontWeight = FontWeight.Medium)
                        Text("Always on", color = glass.contentSecondary, fontSize = 11.sp)
                    }
                    DarkNightToggle(checked = previewOn, onCheckedChange = { previewOn = it })
                }
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .clickable { previewOff = !previewOff },
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically,
                ) {
                    Column(modifier = Modifier.weight(1f)) {
                        Text("Show side info bar", color = glass.content, fontSize = 13.5.sp, fontWeight = FontWeight.Medium)
                        Text("CPU · battery · temp", color = glass.contentSecondary, fontSize = 11.sp)
                    }
                    DarkNightToggle(checked = previewOff, onCheckedChange = { previewOff = it })
                }
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically,
                ) {
                    Text(
                        "Disabled option",
                        color = glass.contentSecondary,
                        fontSize = 13.5.sp,
                        modifier = Modifier.weight(1f),
                    )
                    DarkNightToggle(checked = true, onCheckedChange = {}, enabled = false)
                }
            }
        }
    }
}
