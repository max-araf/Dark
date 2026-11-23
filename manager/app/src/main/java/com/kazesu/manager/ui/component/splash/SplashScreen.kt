package com.kazesu.manager.ui.component.splash

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.core.Animatable
import androidx.compose.animation.core.FastOutSlowInEasing
import androidx.compose.animation.core.LinearEasing
import androidx.compose.animation.core.tween
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.scaleOut
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.navigationBarsPadding
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.statusBarsPadding
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberUpdatedState
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.geometry.CornerRadius
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Rect
import androidx.compose.ui.geometry.RoundRect
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Path
import androidx.compose.ui.graphics.drawscope.DrawScope
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.graphics.drawscope.clipPath
import androidx.compose.ui.graphics.drawscope.withTransform
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.kazesu.manager.R
import com.kazesu.manager.ui.theme.DarkNightThemeStore
import kotlinx.coroutines.delay
import kotlin.math.PI
import kotlin.math.pow
import kotlin.math.sin
import kotlin.time.Duration.Companion.milliseconds

private const val EXIT_DURATION_MS = 320

private fun seg(t: Float, a: Float, b: Float): Float = ((t - a) / (b - a)).coerceIn(0f, 1f)
private fun easeOutCubic(x: Float): Float = 1f - (1f - x).pow(3)
private fun easeOutBack(x: Float): Float {
    val c1 = 1.1f
    val c3 = c1 + 1f
    return 1f + c3 * (x - 1f).pow(3) + c1 * (x - 1f).pow(2)
}
private fun lerpF(a: Float, b: Float, t: Float): Float = a + (b - a) * t

/**
 * PART 2.3: "Glass Assemble" animated logo Canvas composable ported directly from
 * darknight_logo_anim_keyframes.js (0..1000ms timeline, easeOutBack + easeOutCubic + shine sweep + ring pulse).
 */
@Composable
fun DarkNightLogoAnimated(
    progressMs: Float,
    modifier: Modifier = Modifier,
    size: Dp = 132.dp,
) {
    Canvas(modifier = modifier.size(size)) {
        val s = this.size.minDimension
        val u = s / 1000f // 1000x1000 design coordinate space
        val t = progressMs.coerceIn(0f, 1000f)

        val pOuter = seg(t, 0f, 340f)
        val outerOp = easeOutCubic(seg(t, 0f, 200f))
        val outerS = lerpF(0.70f, 1f, easeOutBack(pOuter))

        val pFrame = seg(t, 100f, 420f)
        val frameOp = easeOutCubic(pFrame)
        val frameS = lerpF(0.88f, 1f, easeOutCubic(pFrame))

        val pInner = seg(t, 200f, 520f)
        val innerOp = easeOutCubic(pInner)
        val innerS = lerpF(0.82f, 1f, easeOutCubic(pInner))

        val pTrb = seg(t, 300f, 640f)
        val trbOp = easeOutCubic(seg(t, 300f, 480f))
        val trbTx = lerpF(90f, 0f, easeOutBack(pTrb)) * u
        val trbTy = lerpF(-90f, 0f, easeOutBack(pTrb)) * u

        val pTrs = seg(t, 280f, 600f)
        val trsOp = easeOutCubic(seg(t, 280f, 440f))
        val trsTx = lerpF(120f, 0f, easeOutBack(pTrs)) * u
        val trsTy = lerpF(-120f, 0f, easeOutBack(pTrs)) * u

        val pBlb = seg(t, 330f, 670f)
        val blbOp = easeOutCubic(seg(t, 330f, 510f))
        val blbTx = lerpF(-90f, 0f, easeOutBack(pBlb)) * u
        val blbTy = lerpF(90f, 0f, easeOutBack(pBlb)) * u

        val pBls = seg(t, 310f, 630f)
        val blsOp = easeOutCubic(seg(t, 310f, 470f))
        val blsTx = lerpF(-120f, 0f, easeOutBack(pBls)) * u
        val blsTy = lerpF(120f, 0f, easeOutBack(pBls)) * u

        val pShine = seg(t, 620f, 1000f)
        val shineX = lerpF(-500f, 1400f, pShine) * u
        val shineOp = if (pShine <= 0f || pShine >= 1f) 0f else sin(PI.toFloat() * pShine)

        val pRing = seg(t, 700f, 1000f)
        val ringS = lerpF(1f, 1.38f, easeOutCubic(pRing))
        val ringOp = if (pRing <= 0f || pRing >= 1f) 0f else 0.55f * (1f - pRing)

        val pivot = Offset(s / 2f, s / 2f)

        // 1. Expanding Ring Pulse (700..1000ms)
        if (ringOp > 0.01f) {
            withTransform({ scale(ringS, ringS, pivot) }) {
                drawRoundRect(
                    color = Color(0xFF5CC8FF).copy(alpha = ringOp),
                    topLeft = Offset.Zero,
                    size = Size(s, s),
                    cornerRadius = CornerRadius(85f * u, 85f * u),
                    style = Stroke(width = 6f * u),
                )
            }
        }

        // 2. Outer Glass Squircle (0..340ms)
        if (outerOp > 0.01f) {
            withTransform({ scale(outerS, outerS, pivot) }) {
                drawRoundRect(
                    brush = Brush.linearGradient(
                        colorStops = arrayOf(
                            0.00f to Color(0xFF7ADCFF),
                            0.45f to Color(0xFF1E9EF5),
                            1.00f to Color(0xFF0C2F80),
                        ),
                        start = Offset.Zero,
                        end = Offset(s, s),
                    ),
                    cornerRadius = CornerRadius(85f * u, 85f * u),
                    alpha = outerOp,
                )
                drawRoundRect(
                    brush = Brush.radialGradient(
                        colors = listOf(Color(0xFF9BE8FF).copy(alpha = 0.80f), Color.Transparent),
                        center = Offset(0.18f * s, 0.15f * s),
                        radius = 0.60f * s,
                    ),
                    cornerRadius = CornerRadius(85f * u, 85f * u),
                    alpha = outerOp,
                )
            }
        }

        // 3. Glass Frame (100..420ms)
        if (frameOp > 0.01f) {
            withTransform({ scale(frameS, frameS, pivot) }) {
                drawRoundRect(
                    brush = Brush.linearGradient(
                        colorStops = arrayOf(
                            0.00f to Color.White.copy(alpha = 0.92f),
                            0.60f to Color(0xFFCFE8FF).copy(alpha = 0.55f),
                            1.00f to Color(0xFF7FB2E8).copy(alpha = 0.55f),
                        ),
                        start = Offset(83f * u, 65f * u),
                        end = Offset(917f * u, 936f * u),
                    ),
                    topLeft = Offset(83f * u, 65f * u),
                    size = Size(834f * u, 871f * u),
                    cornerRadius = CornerRadius(72f * u, 72f * u),
                    alpha = frameOp,
                )
                drawRoundRect(
                    color = Color.White.copy(alpha = 0.85f * frameOp),
                    topLeft = Offset(83f * u, 65f * u),
                    size = Size(834f * u, 871f * u),
                    cornerRadius = CornerRadius(72f * u, 72f * u),
                    style = Stroke(width = 4f * u),
                )
            }
        }

        // 4. Inner Blue Squircle (200..520ms)
        if (innerOp > 0.01f) {
            withTransform({ scale(innerS, innerS, pivot) }) {
                drawRoundRect(
                    brush = Brush.linearGradient(
                        colorStops = arrayOf(
                            0.00f to Color(0xFF6FD6FF),
                            0.50f to Color(0xFF1B9DF7),
                            1.00f to Color(0xFF0B3E9A),
                        ),
                        start = Offset(189f * u, 176f * u),
                        end = Offset(811f * u, 826f * u),
                    ),
                    topLeft = Offset(189f * u, 176f * u),
                    size = Size(622f * u, 650f * u),
                    cornerRadius = CornerRadius(50f * u, 50f * u),
                    alpha = innerOp,
                )
                drawRoundRect(
                    color = Color(0xFF5DB8F4).copy(alpha = innerOp),
                    topLeft = Offset(189f * u, 176f * u),
                    size = Size(622f * u, 650f * u),
                    cornerRadius = CornerRadius(50f * u, 50f * u),
                    style = Stroke(width = 9f * u),
                )
            }
        }

        fun DrawScope.drawPlate(x: Float, y: Float, w: Float, h: Float, r: Float, tx: Float, ty: Float, op: Float, isFront: Boolean) {
            if (op <= 0.01f) return
            withTransform({ translate(tx, ty) }) {
                val br = if (isFront) {
                    Brush.linearGradient(
                        listOf(Color.White, Color(0xFFA9D6F7)),
                        start = Offset(x, y),
                        end = Offset(x + w, y + h),
                    )
                } else {
                    Brush.linearGradient(
                        listOf(Color.White.copy(alpha = 0.55f), Color(0xFF8FC6F0).copy(alpha = 0.45f)),
                        start = Offset(x, y),
                        end = Offset(x + w, y + h),
                    )
                }
                drawRoundRect(
                    brush = br,
                    topLeft = Offset(x, y),
                    size = Size(w, h),
                    cornerRadius = CornerRadius(r, r),
                    alpha = op,
                )
            }
        }

        // 5. Back plates + Front squares sliding in from opposite corners
        drawPlate(517f * u, 119f * u, 349f * u, 363f * u, 26f * u, trbTx, trbTy, trbOp, isFront = false)
        drawPlate(134f * u, 524f * u, 350f * u, 358f * u, 26f * u, blbTx, blbTy, blbOp, isFront = false)
        drawPlate(517f * u, 196f * u, 275f * u, 286f * u, 24f * u, trsTx, trsTy, trsOp, isFront = true)
        drawPlate(209f * u, 524f * u, 275f * u, 281f * u, 24f * u, blsTx, blsTy, blsOp, isFront = true)

        // 6. Diagonal Shine Sweep clipped to outer squircle (620..1000ms)
        if (shineOp > 0.01f) {
            val clipP = Path().apply {
                addRoundRect(RoundRect(Rect(0f, 0f, s, s), CornerRadius(85f * u, 85f * u)))
            }
            clipPath(clipP) {
                withTransform({
                    translate(shineX, 0f)
                    rotate(-20f, Offset(0f, s / 2f))
                }) {
                    drawRect(
                        brush = Brush.horizontalGradient(
                            colorStops = arrayOf(
                                0.0f to Color.Transparent,
                                0.5f to Color.White.copy(alpha = 0.75f * shineOp),
                                1.0f to Color.Transparent,
                            ),
                            startX = -120f * u,
                            endX = 120f * u,
                        ),
                        topLeft = Offset(-120f * u, -150f * u),
                        size = Size(240f * u, s + 300f * u),
                    )
                }
            }
        }
    }
}

@Composable
fun SplashScreen(
    isReady: Boolean,
    minDurationMs: Long = 1000L,
    onDismissed: () -> Unit = {},
) {
    val context = LocalContext.current
    DarkNightThemeStore.init(context)
    val dnState by DarkNightThemeStore.state.collectAsStateWithLifecycle()
    val currentOnDismissed by rememberUpdatedState(onDismissed)

    var exitTriggered by remember { mutableStateOf(false) }
    var splashDismissed by remember { mutableStateOf(false) }

    val animEnabled = dnState.startupAnimEnabled
    val progressMs = remember { Animatable(if (animEnabled) 0f else 1000f) }
    val wordmarkProgress = remember { Animatable(if (animEnabled) 0f else 1f) }

    LaunchedEffect(Unit) {
        if (animEnabled) {
            progressMs.animateTo(1000f, animationSpec = tween(durationMillis = 1000, easing = LinearEasing))
            wordmarkProgress.animateTo(1f, animationSpec = tween(durationMillis = 400, easing = FastOutSlowInEasing))
            delay(260.milliseconds)
        } else {
            delay(300.milliseconds)
        }
        if (isReady) {
            exitTriggered = true
            delay((EXIT_DURATION_MS.toLong() + 20L).milliseconds)
            splashDismissed = true
            currentOnDismissed()
        }
    }

    LaunchedEffect(isReady) {
        if (isReady && wordmarkProgress.value >= 0.99f && !exitTriggered) {
            exitTriggered = true
            delay((EXIT_DURATION_MS.toLong() + 20L).milliseconds)
            splashDismissed = true
            currentOnDismissed()
        }
    }

    if (splashDismissed) return

    AnimatedVisibility(
        visible = !exitTriggered,
        enter = fadeIn(tween(120)),
        exit = fadeOut(tween(EXIT_DURATION_MS, easing = FastOutSlowInEasing)) +
            scaleOut(targetScale = 1.04f, animationSpec = tween(EXIT_DURATION_MS, easing = FastOutSlowInEasing)),
    ) {
        Box(
            modifier = Modifier
                .fillMaxSize()
                .background(Color(0xFF07090F))
                .pointerInput(Unit) {}
                .statusBarsPadding()
                .navigationBarsPadding(),
            contentAlignment = Alignment.Center,
        ) {
            // Soft radial glow behind logo
            Canvas(modifier = Modifier.size(320.dp)) {
                drawCircle(
                    brush = Brush.radialGradient(
                        listOf(dnState.palette.primaryGlow.copy(alpha = 0.35f * wordmarkProgress.value), Color.Transparent),
                        center = Offset(size.width / 2f, size.height / 2f),
                        radius = size.minDimension / 2f,
                    )
                )
            }

            Column(
                horizontalAlignment = Alignment.CenterHorizontally,
                verticalArrangement = Arrangement.Center,
                modifier = Modifier.padding(horizontal = 24.dp),
            ) {
                DarkNightLogoAnimated(
                    progressMs = progressMs.value,
                    size = 132.dp,
                )
                Spacer(modifier = Modifier.height(22.dp))
                val wm = wordmarkProgress.value
                Text(
                    text = stringResource(R.string.app_name),
                    color = Color.White,
                    fontSize = 26.sp,
                    fontWeight = FontWeight.Bold,
                    modifier = Modifier
                        .offset(y = (14f * (1f - wm)).dp)
                        .alpha(wm),
                )
                Spacer(modifier = Modifier.height(4.dp))
                Text(
                    text = stringResource(R.string.darknight_subtitle),
                    color = Color.White.copy(alpha = 0.60f),
                    fontSize = 13.sp,
                    modifier = Modifier
                        .offset(y = (10f * (1f - wm)).dp)
                        .alpha(wm),
                )
            }
        }
    }
}