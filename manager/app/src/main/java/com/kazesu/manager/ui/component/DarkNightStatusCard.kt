package com.kazesu.manager.ui.component

import androidx.compose.animation.animateColorAsState
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.tween
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.interaction.collectIsPressedAsState
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.GenericShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.drawWithContent
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Matrix
import androidx.compose.ui.graphics.Path
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.graphics.lerp
import androidx.compose.ui.graphics.luminance
import androidx.compose.ui.graphics.vector.PathParser
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.SpanStyle
import androidx.compose.ui.text.buildAnnotatedString
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.text.withStyle
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import coil.compose.AsyncImage
import coil.request.ImageRequest
import com.kazesu.manager.R
import com.kazesu.manager.ui.theme.DarkNightThemeStore
import com.kazesu.manager.ui.theme.LocalDarkNightGlass
import top.yukonga.miuix.kmp.blur.BlendColorEntry
import top.yukonga.miuix.kmp.blur.BlurColors
import top.yukonga.miuix.kmp.blur.layerBackdrop
import top.yukonga.miuix.kmp.blur.rememberLayerBackdrop
import top.yukonga.miuix.kmp.blur.textureBlur
import top.yukonga.miuix.kmp.shader.isRenderEffectSupported
import java.io.File

enum class DarkNightStatusKind {
    WORKING,
    LIMITED,
    NOT_INSTALLED,
}

private const val NOTCH_CARD_PATH =
    "M0 30Q0 0 30 0H322Q352 0 352 30V148Q352 178 322 178H222C186 178 186 232 150 232H30Q0 232 0 202Z"

private const val MOUNTAIN_FAR_PATH =
    "M0 122L28 114L52 102L70 92L84 102L98 98L120 110L140 82L156 64L168 54L180 66L196 78L208 74L226 92L240 88L255 80L270 94L290 102L336 110V172H0Z"
private const val SNOWCAP_1 = "M168 54L156 64L162 62L166 68L170 62L176 68L180 66Z"
private const val SNOWCAP_2 = "M70 92L62 99L68 97L72 101L78 96Z"
private const val SNOWCAP_3 = "M255 80L249 87L254 85L258 89L263 85Z"
private const val MOUNTAIN_MID_PATH =
    "M0 140L30 130L60 136L100 122L140 134L180 120L220 132L260 124L300 134L336 128V172H0Z"
private const val MOUNTAIN_NEAR_PATH =
    "M0 152Q80 142 170 150T336 148V172H0Z"

private fun buildScaledPath(pathData: String, targetW: Float, targetH: Float, srcW: Float, srcH: Float): Path {
    val raw = PathParser().parsePathString(pathData).toPath()
    val matrix = Matrix().apply { scale(targetW / srcW, targetH / srcH) }
    raw.transform(matrix)
    return raw
}

@Composable
private fun rememberStatusAccent(kind: DarkNightStatusKind): Color {
    val glass = LocalDarkNightGlass.current
    val target = when (kind) {
        DarkNightStatusKind.WORKING -> glass.accent
        DarkNightStatusKind.LIMITED -> Color(0xFFFFB84D)
        DarkNightStatusKind.NOT_INSTALLED -> Color(0xFFFF5A36)
    }
    val animated by animateColorAsState(
        targetValue = target,
        animationSpec = tween(300),
        label = "dn_status_accent",
    )
    return animated
}

/**
 * PART 2.2 — MATERIAL Home Status Card (Glass Morphism notched squircle + separate pill + 4 palette shards).
 */
@Composable
fun DarkNightStatusCardMaterial(
    kind: DarkNightStatusKind,
    versionLabel: String,
    pillTag: String,
    onClick: () -> Unit,
    modifier: Modifier = Modifier,
) {
    val context = LocalContext.current
    DarkNightThemeStore.init(context)
    val dnState by DarkNightThemeStore.state.collectAsStateWithLifecycle()
    val glass = LocalDarkNightGlass.current
    val a = rememberStatusAccent(kind)
    val l = lerp(a, Color.White, 0.38f)
    val d = lerp(a, Color.Black, 0.45f)
    val ad = lerp(a, Color.Black, if (a.luminance() > 0.50f) 0.55f else 0.28f)

    val line1 = when (kind) {
        DarkNightStatusKind.WORKING -> stringResource(R.string.dn_status_mat_working_l1)
        DarkNightStatusKind.LIMITED -> stringResource(R.string.dn_status_mat_limited_l1)
        DarkNightStatusKind.NOT_INSTALLED -> stringResource(R.string.dn_status_mat_notinstalled_l1)
    }
    val line2 = when (kind) {
        DarkNightStatusKind.WORKING -> stringResource(R.string.dn_status_mat_working_l2)
        DarkNightStatusKind.LIMITED -> stringResource(R.string.dn_status_mat_limited_l2)
        DarkNightStatusKind.NOT_INSTALLED -> stringResource(R.string.dn_status_mat_notinstalled_l2)
    }
    val bottomTitle = when (kind) {
        DarkNightStatusKind.WORKING -> stringResource(R.string.dn_status_version_label)
        DarkNightStatusKind.LIMITED -> "Non-GKI"
        DarkNightStatusKind.NOT_INSTALLED -> stringResource(R.string.dn_status_tap_card)
    }
    val bottomSub = when (kind) {
        DarkNightStatusKind.WORKING -> versionLabel
        DarkNightStatusKind.LIMITED -> "no DN kernel"
        DarkNightStatusKind.NOT_INSTALLED -> stringResource(R.string.dn_status_to_install)
    }

    val interactionSource = remember { MutableInteractionSource() }
    val isPressed by interactionSource.collectIsPressedAsState()
    val pressScale by animateFloatAsState(
        targetValue = if (isPressed) 0.98f else 1f,
        animationSpec = tween(120),
        label = "dn_mat_card_scale",
    )

    val cardBackdrop = rememberLayerBackdrop()
    val useRealBlur = dnState.glassOn && isRenderEffectSupported()

    val notchedShape = remember {
        GenericShape { size, _ ->
            addPath(buildScaledPath(NOTCH_CARD_PATH, size.width, size.height, 352f, 232f))
        }
    }

    Box(
        modifier = modifier
            .fillMaxWidth()
            .height(236.dp)
            .graphicsLayer {
                scaleX = pressScale
                scaleY = pressScale
            }
            .clickable(
                interactionSource = interactionSource,
                indication = null,
                onClick = onClick,
            ),
    ) {
        // 1. Backdrop Layer: 4 tilted palette-tinted shards peeking behind the frosted card
        Canvas(
            modifier = Modifier
                .fillMaxSize()
                .layerBackdrop(cardBackdrop)
        ) {
            val w = size.width
            val h = size.height
            fun poly(pts: List<Pair<Float, Float>>): Path = Path().apply {
                moveTo(w * pts[0].first, h * pts[0].second)
                for (i in 1 until pts.size) lineTo(w * pts[i].first, h * pts[i].second)
                close()
            }
            val shardBrush = if (glass.isDark) {
                Brush.linearGradient(
                    colorStops = arrayOf(
                        0.0f to l.copy(alpha = 0.85f),
                        0.6f to a.copy(alpha = 0.78f),
                        1.0f to d.copy(alpha = 0.70f),
                    ),
                    start = Offset(0f, 0f),
                    end = Offset(w, h),
                )
            } else {
                Brush.linearGradient(
                    colors = listOf(l.copy(alpha = 0.82f), a.copy(alpha = 0.72f)),
                    start = Offset(0f, 0f),
                    end = Offset(w, h),
                )
            }
            // Top-left shard
            drawPath(poly(listOf(0.00f to 0.40f, 0.38f to 0.00f, 0.62f to 0.22f, 0.46f to 0.70f)), shardBrush)
            // Top-right shard
            drawPath(poly(listOf(0.30f to 0.00f, 1.00f to 0.20f, 0.80f to 1.00f, 0.00f to 0.60f)), shardBrush, alpha = 0.80f)
            // Bottom-right shard
            drawPath(poly(listOf(0.00f to 0.30f, 0.70f to 0.00f, 1.00f to 0.70f, 0.40f to 1.00f)), shardBrush, alpha = 0.72f)
            // Bottom-left shard
            drawPath(poly(listOf(0.20f to 0.00f, 1.00f to 0.40f, 0.60f to 1.00f, 0.00f to 0.70f)), shardBrush, alpha = 0.65f)
        }

        // 2. Frosted Notched Squircle Glass Card
        val cardFill = if (glass.isDark) {
            val boost = if (useRealBlur) 0f else 0.15f
            Brush.linearGradient(
                colorStops = arrayOf(
                    0.00f to Color.White.copy(alpha = (0.20f + boost).coerceAtMost(0.45f)),
                    0.70f to Color.White.copy(alpha = (0.06f + boost).coerceAtMost(0.28f)),
                    1.00f to Color.White.copy(alpha = (0.10f + boost).coerceAtMost(0.32f)),
                )
            )
        } else {
            val boost = if (useRealBlur) 0f else 0.12f
            Brush.linearGradient(
                colorStops = arrayOf(
                    0.00f to Color.White.copy(alpha = (0.78f + boost).coerceAtMost(0.92f)),
                    0.70f to Color.White.copy(alpha = (0.40f + boost).coerceAtMost(0.62f)),
                    1.00f to Color.White.copy(alpha = (0.55f + boost).coerceAtMost(0.75f)),
                )
            )
        }

        val blurMod = if (useRealBlur) {
            Modifier.textureBlur(
                backdrop = cardBackdrop,
                shape = notchedShape,
                blurRadius = 18f,
                colors = BlurColors(
                    blendColors = listOf(
                        BlendColorEntry(
                            color = if (glass.isDark) Color.White.copy(alpha = 0.05f) else Color.White.copy(alpha = 0.25f)
                        )
                    )
                ),
            )
        } else {
            Modifier
        }

        Box(
            modifier = Modifier
                .fillMaxSize()
                .padding(vertical = 2.dp)
                .then(
                    if (!glass.isDark) {
                        Modifier.shadow(14.dp, notchedShape, ambientColor = ad.copy(alpha = 0.30f), spotColor = ad.copy(alpha = 0.30f))
                    } else {
                        Modifier
                    }
                )
                .then(blurMod)
                .clip(notchedShape)
                .background(cardFill)
                .drawWithContent {
                    drawContent()
                    // 1.2dp gradient rim along notched path
                    val p = buildScaledPath(NOTCH_CARD_PATH, size.width, size.height, 352f, 232f)
                    drawPath(
                        path = p,
                        brush = Brush.linearGradient(
                            colorStops = arrayOf(
                                0.00f to Color.White.copy(alpha = 0.95f),
                                0.45f to Color.White.copy(alpha = 0.16f),
                                1.00f to Color.White.copy(alpha = 0.55f),
                            )
                        ),
                        style = Stroke(width = if (useRealBlur) 1.2.dp.toPx() else 1.dp.toPx()),
                    )
                    if (isPressed) {
                        drawRect(glass.lite.copy(alpha = 0.18f))
                    }
                }
                .padding(horizontal = 22.dp, vertical = 20.dp),
        ) {
            // Top-right brand chip
            Row(
                modifier = Modifier.align(Alignment.TopEnd),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(6.dp),
            ) {
                Box(
                    modifier = Modifier
                        .size(16.dp)
                        .border(1.5.dp, glass.content.copy(alpha = 0.85f), CircleShape),
                    contentAlignment = Alignment.Center,
                ) {
                    Box(
                        modifier = Modifier
                            .size(5.dp)
                            .clip(CircleShape)
                            .background(a)
                    )
                }
                Text(
                    text = "DarkNightSu",
                    color = glass.content,
                    fontSize = 13.sp,
                    fontWeight = FontWeight.Bold,
                )
            }

            // Main 2-line title
            Column(modifier = Modifier.align(Alignment.TopStart).padding(top = 6.dp)) {
                Text(
                    text = line1,
                    color = glass.content,
                    fontSize = 34.sp,
                    fontWeight = FontWeight.Bold,
                    letterSpacing = (-0.8).sp,
                    lineHeight = 36.sp,
                    maxLines = 1,
                )
                Text(
                    text = line2,
                    color = if (glass.isDark) l else ad,
                    fontSize = 34.sp,
                    fontWeight = FontWeight.Bold,
                    letterSpacing = (-0.8).sp,
                    lineHeight = 36.sp,
                    maxLines = 1,
                )
            }

            // Bottom-left version / subtitle
            Column(modifier = Modifier.align(Alignment.BottomStart)) {
                Text(
                    text = bottomTitle,
                    color = glass.content.copy(alpha = 0.88f),
                    fontSize = 13.sp,
                    fontWeight = FontWeight.SemiBold,
                    maxLines = 1,
                )
                Text(
                    text = bottomSub,
                    color = glass.content.copy(alpha = 0.78f),
                    fontSize = 11.5.sp,
                    fontFamily = FontFamily.Monospace,
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis,
                )
            }
        }

        // 3. Separate Frosted Glass Pill tucked inside the bottom-right notch
        val pillShape = RoundedCornerShape(20.dp)
        val pillBlurMod = if (useRealBlur) {
            Modifier.textureBlur(
                backdrop = cardBackdrop,
                shape = pillShape,
                blurRadius = 18f,
                colors = BlurColors(
                    blendColors = listOf(
                        BlendColorEntry(
                            color = if (glass.isDark) Color.White.copy(alpha = 0.06f) else Color.White.copy(alpha = 0.30f)
                        )
                    )
                ),
            )
        } else {
            Modifier
        }

        Box(
            modifier = Modifier
                .align(Alignment.BottomEnd)
                .width(152.dp)
                .height(46.dp)
                .then(
                    if (!glass.isDark) {
                        Modifier.shadow(10.dp, pillShape, ambientColor = ad.copy(alpha = 0.30f), spotColor = ad.copy(alpha = 0.30f))
                    } else {
                        Modifier
                    }
                )
                .then(pillBlurMod)
                .clip(pillShape)
                .background(cardFill)
                .border(
                    width = 1.2.dp,
                    brush = Brush.linearGradient(
                        colorStops = arrayOf(
                            0.00f to Color.White.copy(alpha = 0.90f),
                            0.50f to Color.White.copy(alpha = 0.18f),
                            1.00f to Color.White.copy(alpha = 0.50f),
                        )
                    ),
                    shape = pillShape,
                ),
            contentAlignment = Alignment.Center,
        ) {
            Row(
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(8.dp),
                modifier = Modifier.padding(horizontal = 12.dp),
            ) {
                Box(
                    modifier = Modifier
                        .size(7.dp)
                        .clip(CircleShape)
                        .background(a)
                )
                Text(
                    text = pillTag,
                    color = glass.content,
                    fontSize = 12.sp,
                    fontWeight = FontWeight.ExtraBold,
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis,
                )
            }
        }
    }
}

/**
 * PART 2.3 — MIUIX Home Status Card (Squircle 42dp + Palette-generated Mountain Image Panel + 2-tone sentence).
 */
@Composable
fun DarkNightStatusCardMiuix(
    kind: DarkNightStatusKind,
    versionLabel: String,
    chipLabel: String,
    homeCardImagePath: String,
    onClick: () -> Unit,
    modifier: Modifier = Modifier,
) {
    val context = LocalContext.current
    val glass = LocalDarkNightGlass.current
    val a = rememberStatusAccent(kind)
    val l = lerp(a, Color.White, 0.38f)

    val cardShape = RoundedCornerShape(42.dp)
    val panelShape = RoundedCornerShape(
        topStart = 32.dp,
        topEnd = 32.dp,
        bottomStart = 58.dp,
        bottomEnd = 58.dp,
    )

    val upperLabel = when (kind) {
        DarkNightStatusKind.WORKING -> "KERNEL · $versionLabel"
        DarkNightStatusKind.LIMITED -> stringResource(R.string.dn_status_miuix_limited_label)
        DarkNightStatusKind.NOT_INSTALLED -> stringResource(R.string.dn_status_miuix_notinstalled_label)
    }

    val primaryText = if (glass.isDark) Color.White else Color(0xFF0B1020)
    val mutedText = primaryText.copy(alpha = 0.42f)

    val sentence = when (kind) {
        DarkNightStatusKind.WORKING -> buildAnnotatedString {
            withStyle(SpanStyle(color = mutedText, fontWeight = FontWeight.SemiBold)) { append("Your kernel is ") }
            withStyle(SpanStyle(color = primaryText, fontWeight = FontWeight.Bold)) { append("working") }
            withStyle(SpanStyle(color = mutedText, fontWeight = FontWeight.SemiBold)) { append(": root, modules and SuSFS are ") }
            withStyle(SpanStyle(color = primaryText, fontWeight = FontWeight.Bold)) { append("ready") }
            withStyle(SpanStyle(color = a, fontWeight = FontWeight.ExtraBold)) { append(".") }
        }
        DarkNightStatusKind.LIMITED -> buildAnnotatedString {
            withStyle(SpanStyle(color = mutedText, fontWeight = FontWeight.SemiBold)) { append("Root works, but the ") }
            withStyle(SpanStyle(color = primaryText, fontWeight = FontWeight.Bold)) { append("DarkNight kernel") }
            withStyle(SpanStyle(color = mutedText, fontWeight = FontWeight.SemiBold)) { append(" is missing: tap to ") }
            withStyle(SpanStyle(color = primaryText, fontWeight = FontWeight.Bold)) { append("install") }
            withStyle(SpanStyle(color = a, fontWeight = FontWeight.ExtraBold)) { append(".") }
        }
        DarkNightStatusKind.NOT_INSTALLED -> buildAnnotatedString {
            withStyle(SpanStyle(color = primaryText, fontWeight = FontWeight.Bold)) { append("No root") }
            withStyle(SpanStyle(color = mutedText, fontWeight = FontWeight.SemiBold)) { append(" detected: tap here to ") }
            withStyle(SpanStyle(color = primaryText, fontWeight = FontWeight.Bold)) { append("install") }
            withStyle(SpanStyle(color = mutedText, fontWeight = FontWeight.SemiBold)) { append(" the kernel") }
            withStyle(SpanStyle(color = a, fontWeight = FontWeight.ExtraBold)) { append(".") }
        }
    }

    val cardBg = if (glass.isDark) Color(0xFF12151C) else Color.White
    val outerShadowColor = if (glass.isDark) a.copy(alpha = 0.55f) else Color.Black.copy(alpha = 0.18f)

    Box(
        modifier = modifier
            .fillMaxWidth()
            .height(296.dp)
            .shadow(
                elevation = if (glass.isDark) 24.dp else 22.dp,
                shape = cardShape,
                ambientColor = outerShadowColor,
                spotColor = outerShadowColor,
            )
            .clip(cardShape)
            .background(cardBg)
            .then(
                if (glass.isDark) {
                    Modifier.border(1.dp, Color.White.copy(alpha = 0.08f), cardShape)
                } else {
                    Modifier.border(5.dp, Color.White, cardShape)
                }
            )
            .clickable(onClick = onClick)
            .padding(8.dp),
    ) {
        Column(modifier = Modifier.fillMaxSize()) {
            // Top 172dp Image Panel (User custom image OR code-drawn palette mountain scene)
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(172.dp)
                    .clip(panelShape)
                    .drawWithContent {
                        drawContent()
                        // Top 3dp white specular rim
                        drawLine(
                            color = Color.White.copy(alpha = 0.28f),
                            start = Offset(0f, 1.5.dp.toPx()),
                            end = Offset(size.width, 1.5.dp.toPx()),
                            strokeWidth = 3.dp.toPx(),
                        )
                        // Bottom 26dp inner vignette
                        drawRect(
                            brush = Brush.verticalGradient(
                                colors = listOf(Color.Transparent, Color.Black.copy(alpha = 0.45f)),
                                startY = size.height - 26.dp.toPx(),
                                endY = size.height,
                            )
                        )
                    }
                    .border(1.dp, Color.White.copy(alpha = 0.12f), panelShape),
            ) {
                if (homeCardImagePath.isNotEmpty() && File(homeCardImagePath).exists()) {
                    AsyncImage(
                        model = ImageRequest.Builder(context).data(File(homeCardImagePath)).crossfade(true).build(),
                        contentDescription = null,
                        contentScale = ContentScale.Crop,
                        modifier = Modifier.fillMaxSize(),
                    )
                } else {
                    // Original code-drawn mountain scene tinted dynamically by active palette accent `a`
                    Canvas(modifier = Modifier.fillMaxSize()) {
                        val w = size.width
                        val h = size.height
                        val sx = w / 336f
                        val sy = h / 172f

                        val skyTop = lerp(a, Color.Black, 0.62f)
                        val skyMid = lerp(lerp(a, Color.Black, 0.35f), a, 0.35f)
                        val skyBot = lerp(a, Color.Black, 0.10f)
                        val farColor = lerp(lerp(a, Color.Black, 0.55f), a, 0.28f)
                        val midColor = lerp(a, Color.Black, 0.72f)
                        val nearColor = lerp(a, Color.Black, 0.86f)
                        val mistColor = lerp(a, Color.White, 0.15f)

                        // Sky
                        drawRect(
                            brush = Brush.verticalGradient(
                                colorStops = arrayOf(
                                    0.00f to skyTop,
                                    0.62f to skyMid,
                                    1.00f to skyBot,
                                )
                            )
                        )

                        // 6 Stars
                        val stars = listOf(
                            Triple(44f, 28f, 1.0f),
                            Triple(96f, 46f, 0.8f),
                            Triple(262f, 30f, 1.1f),
                            Triple(302f, 58f, 0.8f),
                            Triple(214f, 20f, 0.8f),
                            Triple(140f, 22f, 0.7f),
                        )
                        stars.forEach { (cx, cy, r) ->
                            drawCircle(
                                color = Color.White.copy(alpha = 0.85f),
                                radius = r * sx.coerceAtLeast(1f),
                                center = Offset(cx * sx, cy * sy),
                            )
                        }

                        // Far mountain range
                        drawPath(buildScaledPath(MOUNTAIN_FAR_PATH, w, h, 336f, 172f), color = farColor)

                        // Snowcaps
                        drawPath(buildScaledPath(SNOWCAP_1, w, h, 336f, 172f), color = Color.White.copy(alpha = 0.78f))
                        drawPath(buildScaledPath(SNOWCAP_2, w, h, 336f, 172f), color = Color.White.copy(alpha = 0.70f))
                        drawPath(buildScaledPath(SNOWCAP_3, w, h, 336f, 172f), color = Color.White.copy(alpha = 0.72f))

                        // Mid ridge
                        drawPath(buildScaledPath(MOUNTAIN_MID_PATH, w, h, 336f, 172f), color = midColor)

                        // Mist band (y=120..154)
                        drawRect(
                            brush = Brush.verticalGradient(
                                colorStops = arrayOf(
                                    0.00f to mistColor.copy(alpha = 0f),
                                    0.60f to mistColor.copy(alpha = 0.38f),
                                    1.00f to mistColor.copy(alpha = 0f),
                                ),
                                startY = 120f * sy,
                                endY = 154f * sy,
                            ),
                            topLeft = Offset(0f, 120f * sy),
                            size = Size(w, 34f * sy),
                        )

                        // Near ground
                        drawPath(buildScaledPath(MOUNTAIN_NEAR_PATH, w, h, 336f, 172f), color = nearColor)
                    }
                }

                // Top-left frosted status pill over image
                Row(
                    modifier = Modifier
                        .align(Alignment.TopStart)
                        .padding(14.dp)
                        .clip(CircleShape)
                        .background(Color.White.copy(alpha = 0.18f))
                        .border(1.dp, Color.White.copy(alpha = 0.30f), CircleShape)
                        .padding(horizontal = 10.dp, vertical = 5.dp),
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(6.dp),
                ) {
                    Box(
                        modifier = Modifier
                            .size(6.dp)
                            .clip(CircleShape)
                            .background(l)
                    )
                    Text(
                        text = chipLabel,
                        color = Color.White,
                        fontSize = 11.5.sp,
                        fontWeight = FontWeight.ExtraBold,
                        maxLines = 1,
                    )
                }
            }

            // Text below image panel
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(start = 16.dp, end = 16.dp, top = 16.dp),
            ) {
                Text(
                    text = upperLabel,
                    color = primaryText.copy(alpha = if (glass.isDark) 0.42f else 0.34f),
                    fontSize = 14.sp,
                    fontWeight = FontWeight.ExtraBold,
                    letterSpacing = 0.8.sp,
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis,
                )
                Spacer(modifier = Modifier.height(6.dp))
                Text(
                    text = sentence,
                    fontSize = 18.5.sp,
                    lineHeight = 23.sp,
                    letterSpacing = (-0.3).sp,
                    maxLines = 2,
                    overflow = TextOverflow.Ellipsis,
                )
            }
        }
    }
}
