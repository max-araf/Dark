package com.kazesu.manager.ui.theme

import android.content.Context
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.core.Animatable
import androidx.compose.animation.core.FastOutSlowInEasing
import androidx.compose.animation.core.tween
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.ColumnScope
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.navigationBarsPadding
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.statusBarsPadding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import android.widget.Toast
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material3.Icon
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Surface
import androidx.compose.material3.Switch
import androidx.compose.ui.platform.LocalContext
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import top.yukonga.miuix.kmp.shader.isRenderEffectSupported
import top.yukonga.miuix.kmp.blur.textureBlur
import top.yukonga.miuix.kmp.blur.BlurColors
import top.yukonga.miuix.kmp.blur.BlendColorEntry
import androidx.compose.ui.graphics.luminance
import androidx.compose.ui.graphics.lerp
import androidx.compose.ui.graphics.Shape
import androidx.compose.ui.draw.drawWithContent
import androidx.compose.ui.draw.drawBehind
import androidx.compose.foundation.border
import androidx.compose.ui.graphics.toArgb
import androidx.compose.ui.text.SpanStyle
import androidx.compose.ui.text.buildAnnotatedString
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.withStyle
import androidx.compose.ui.unit.dp
import androidx.compose.runtime.staticCompositionLocalOf
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.sp
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow

data class DarkNightPalette(
    val id: String,
    val name: String,
    val subtitle: String,
    val primaryGlow: Color,
    val secondaryGlow: Color,
    val deepBase: Color,
    val cardSurface: Color = Color(0xCC121620),
    val buttonStart: Color = primaryGlow,
    val buttonEnd: Color = secondaryGlow,
    val hexTop: String,
    val hexBottom: String,
)

object DarkNightPalettes {
    val DarkNightAura = DarkNightPalette(
        id = "darknight_aura",
        name = "DarkNight Aura",
        subtitle = "Electric royal blue & ice glow",
        primaryGlow = Color(0xFF2563FF),
        secondaryGlow = Color(0xFF8BB4FF),
        deepBase = Color(0xFF07090F),
        buttonStart = Color(0xFF1D6BFF),
        buttonEnd = Color(0xFF5D9BFF),
        hexTop = "#2563FF",
        hexBottom = "#07090F",
    )
    val RoyalViolet = DarkNightPalette(
        id = "royal_violet",
        name = "Royal Violet",
        subtitle = "Deep obsidian to electric violet",
        primaryGlow = Color(0xFF9303C5),
        secondaryGlow = Color(0xFFC855F5),
        deepBase = Color(0xFF02060E),
        buttonStart = Color(0xFF7B02A8),
        buttonEnd = Color(0xFFB43BE3),
        hexTop = "#9303C5",
        hexBottom = "#02060E",
    )
    val CrimsonNoir = DarkNightPalette(
        id = "crimson_noir",
        name = "Crimson Noir",
        subtitle = "Obsidian crimson stealth glow",
        primaryGlow = Color(0xFFC50337),
        secondaryGlow = Color(0xFFFF4D7D),
        deepBase = Color(0xFF02060E),
        buttonStart = Color(0xFFA8022E),
        buttonEnd = Color(0xFFE62E5C),
        hexTop = "#C50337",
        hexBottom = "#02060E",
    )
    val ForestNoir = DarkNightPalette(
        id = "forest_noir",
        name = "Forest Noir",
        subtitle = "Deep emerald night gradient",
        primaryGlow = Color(0xFF007D10),
        secondaryGlow = Color(0xFF26C948),
        deepBase = Color(0xFF02060E),
        buttonStart = Color(0xFF006B0E),
        buttonEnd = Color(0xFF1AA636),
        hexTop = "#007D10",
        hexBottom = "#02060E",
    )
    val GoldenBronze = DarkNightPalette(
        id = "golden_bronze",
        name = "Golden Bronze",
        subtitle = "Warm champagne gold & dark bronze",
        primaryGlow = Color(0xFFFFEB97),
        secondaryGlow = Color(0xFFC49A45),
        deepBase = Color(0xFF171008),
        cardSurface = Color(0xCC1E160D),
        buttonStart = Color(0xFF8C5B20),
        buttonEnd = Color(0xFFE5C158),
        hexTop = "#FFEB97",
        hexBottom = "#583714",
    )
    val CyberOcean = DarkNightPalette(
        id = "cyber_ocean",
        name = "Cyber Ocean",
        subtitle = "Midnight navy to neon cyan",
        primaryGlow = Color(0xFF00BBFF),
        secondaryGlow = Color(0xFF66E0FF),
        deepBase = Color(0xFF000221),
        buttonStart = Color(0xFF0088CC),
        buttonEnd = Color(0xFF00CCFF),
        hexTop = "#00BBFF",
        hexBottom = "#000221",
    )
    val AmberGlow = DarkNightPalette(
        id = "amber_glow",
        name = "Amber Glow",
        subtitle = "Solar amber & soft cream shimmer",
        primaryGlow = Color(0xFFFEB42F),
        secondaryGlow = Color(0xFFFFFDCD),
        deepBase = Color(0xFF0E0B06),
        cardSurface = Color(0xCC1C160D),
        buttonStart = Color(0xFFD98B0F),
        buttonEnd = Color(0xFFFEB42F),
        hexTop = "#FEB42F",
        hexBottom = "#FFFDCD",
    )
    val RoyalEmber = DarkNightPalette(
        id = "royal_ember",
        name = "Royal Ember",
        subtitle = "Teal emerald & deep abyssal pine",
        primaryGlow = Color(0xFF068562),
        secondaryGlow = Color(0xFF2CC49A),
        deepBase = Color(0xFF013F4A),
        cardSurface = Color(0xCC06262E),
        buttonStart = Color(0xFF04664B),
        buttonEnd = Color(0xFF0CA67B),
        hexTop = "#068562",
        hexBottom = "#013F4A",
    )
    val MintAurora = DarkNightPalette(
        id = "mint_aurora",
        name = "Mint Aurora",
        subtitle = "Vibrant mint emerald & ice pastel",
        primaryGlow = Color(0xFF07D0A4),
        secondaryGlow = Color(0xFFC2FCD4),
        deepBase = Color(0xFF04120F),
        cardSurface = Color(0xCC0C1E1A),
        buttonStart = Color(0xFF059E7C),
        buttonEnd = Color(0xFF07D0A4),
        hexTop = "#07D0A4",
        hexBottom = "#C2FCD4",
    )
    val RoyalCharcoal = DarkNightPalette(
        id = "royal_charcoal",
        name = "Royal Charcoal",
        subtitle = "Monochrome graphite & slate metal",
        primaryGlow = Color(0xFF4C4E51),
        secondaryGlow = Color(0xFF7B7E84),
        deepBase = Color(0xFF1E1F22),
        cardSurface = Color(0xCC24262A),
        buttonStart = Color(0xFF3A3C40),
        buttonEnd = Color(0xFF5D6066),
        hexTop = "#4C4E51",
        hexBottom = "#1E2023",
    )

    val all: List<DarkNightPalette> = listOf(
        DarkNightAura,
        RoyalViolet,
        CrimsonNoir,
        ForestNoir,
        GoldenBronze,
        CyberOcean,
        AmberGlow,
        RoyalEmber,
        MintAurora,
        RoyalCharcoal,
    )

    fun byId(id: String?): DarkNightPalette = all.firstOrNull { it.id == id } ?: DarkNightAura
}

enum class DarkNightBlurQuality(val id: String, val label: String, val radius: Float) {
    HIGH("high", "High (18dp)", 18f),
    LOW("low", "Low (10dp)", 10f),
    OFF("off", "Off", 0f);

    companion object {
        fun byId(id: String?): DarkNightBlurQuality = entries.firstOrNull { it.id == id } ?: HIGH
    }
}

fun contrastOn(bg: Color): Color = if (bg.luminance() > 0.45f) Color(0xFF0A0D14) else Color.White

enum class DarkNightNavStyle(val id: String) {
    BOTTOM_BAR("bottom_bar"),
    GLASS_SIDEBAR("glass_sidebar");

    companion object {
        fun byId(id: String?): DarkNightNavStyle = entries.firstOrNull { it.id == id } ?: BOTTOM_BAR
    }
}

/** Shared left clearance when Glass Sidebar Rail is active (80dp). */
val LocalNavRailPadding = staticCompositionLocalOf<Dp> { 0.dp }

data class DarkNightThemeState(
    val palette: DarkNightPalette = DarkNightPalettes.DarkNightAura,
    val auraEnabled: Boolean = true,
    val startupAnimEnabled: Boolean = true,
    val onboardingCompleted: Boolean = false,
    val navStyle: DarkNightNavStyle = DarkNightNavStyle.BOTTOM_BAR,
    val blurQuality: DarkNightBlurQuality = DarkNightBlurQuality.HIGH,
    val amoledBlack: Boolean = false,
)

object DarkNightThemeStore {
    private const val PREFS_NAME = "darknight_theme_prefs"
    private const val KEY_PALETTE = "palette_id"
    private const val KEY_AURA = "aura_enabled"
    private const val KEY_STARTUP_ANIM = "startup_anim_enabled"
    private const val KEY_ONBOARDING_DONE = "onboarding_done"
    private const val KEY_NAV_STYLE = "nav_style"
    private const val KEY_BLUR_QUALITY = "blur_quality"
    private const val KEY_AMOLED_BLACK = "amoled_black"

    private val _state = MutableStateFlow(DarkNightThemeState())
    val state: StateFlow<DarkNightThemeState> = _state.asStateFlow()

    @Volatile
    private var initialized = false

    fun init(context: Context) {
        if (initialized) return
        synchronized(this) {
            if (initialized) return
            val prefs = context.applicationContext.getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE)
            _state.value = DarkNightThemeState(
                palette = DarkNightPalettes.byId(prefs.getString(KEY_PALETTE, DarkNightPalettes.DarkNightAura.id)),
                auraEnabled = prefs.getBoolean(KEY_AURA, true),
                startupAnimEnabled = prefs.getBoolean(KEY_STARTUP_ANIM, true),
                onboardingCompleted = prefs.getBoolean(KEY_ONBOARDING_DONE, false),
                navStyle = DarkNightNavStyle.byId(prefs.getString(KEY_NAV_STYLE, DarkNightNavStyle.BOTTOM_BAR.id)),
                blurQuality = DarkNightBlurQuality.byId(prefs.getString(KEY_BLUR_QUALITY, DarkNightBlurQuality.HIGH.id)),
                amoledBlack = prefs.getBoolean(KEY_AMOLED_BLACK, false),
            )
            initialized = true
        }
    }

    fun selectPalette(context: Context, palette: DarkNightPalette) {
        init(context)
        context.applicationContext.getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE)
            .edit()
            .putString(KEY_PALETTE, palette.id)
            .apply()
        _state.value = _state.value.copy(palette = palette)
    }

    fun setAuraEnabled(context: Context, enabled: Boolean) {
        init(context)
        context.applicationContext.getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE)
            .edit()
            .putBoolean(KEY_AURA, enabled)
            .apply()
        _state.value = _state.value.copy(auraEnabled = enabled)
    }

    fun setStartupAnimEnabled(context: Context, enabled: Boolean) {
        init(context)
        context.applicationContext.getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE)
            .edit()
            .putBoolean(KEY_STARTUP_ANIM, enabled)
            .apply()
        _state.value = _state.value.copy(startupAnimEnabled = enabled)
    }

    fun setOnboardingCompleted(context: Context, completed: Boolean) {
        init(context)
        context.applicationContext.getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE)
            .edit()
            .putBoolean(KEY_ONBOARDING_DONE, completed)
            .apply()
        _state.value = _state.value.copy(onboardingCompleted = completed)
    }

    fun setNavStyle(context: Context, style: DarkNightNavStyle) {
        init(context)
        context.applicationContext.getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE)
            .edit()
            .putString(KEY_NAV_STYLE, style.id)
            .apply()
        _state.value = _state.value.copy(navStyle = style)
    }

    fun setBlurQuality(context: Context, quality: DarkNightBlurQuality) {
        init(context)
        context.applicationContext.getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE)
            .edit()
            .putString(KEY_BLUR_QUALITY, quality.id)
            .apply()
        _state.value = _state.value.copy(blurQuality = quality)
    }

    fun setAmoledBlack(context: Context, enabled: Boolean) {
        init(context)
        context.applicationContext.getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE)
            .edit()
            .putBoolean(KEY_AMOLED_BLACK, enabled)
            .apply()
        _state.value = _state.value.copy(amoledBlack = enabled)
    }

    fun activeKeyColorArgb(): Int = _state.value.palette.primaryGlow.toArgb()
}

/**
 * Luminous top-corner aura backdrop inspired by the Zentra dark UI:
 * plays a smooth one-shot bloom animation on start, then settles completely static.
 */
@Composable
fun DarkNightAuraBackdrop(
    palette: DarkNightPalette,
    animateOnStart: Boolean = true,
    modifier: Modifier = Modifier,
) {
    val progress = remember(palette.id, animateOnStart) {
        Animatable(if (animateOnStart) 0f else 1f)
    }
    LaunchedEffect(palette.id, animateOnStart) {
        if (animateOnStart) {
            progress.animateTo(
                targetValue = 1f,
                animationSpec = tween(durationMillis = 1250, easing = FastOutSlowInEasing),
            )
        }
    }
    val p = progress.value

    val themeState by DarkNightThemeStore.state.collectAsStateWithLifecycle()
    val amoled = themeState.amoledBlack
    val baseBg = if (amoled) Color.Black else palette.deepBase
    val blobMul = if (amoled) 0.70f else 1.0f

    Canvas(
        modifier = modifier
            .fillMaxSize()
            .background(baseBg)
    ) {
        val w = size.width
        val h = size.height

        // 1. Top-left primary glow (~420x330dp, accent @ 0.62)
        drawRect(
            brush = Brush.radialGradient(
                colors = listOf(
                    palette.primaryGlow.copy(alpha = 0.62f * p * blobMul),
                    palette.primaryGlow.copy(alpha = 0.26f * p * blobMul),
                    Color.Transparent,
                ),
                center = Offset(w * (0.16f + 0.10f * p), h * (0.04f + 0.06f * p)),
                radius = w * (0.90f + 0.18f * p),
            )
        )

        // 2. Upper-right secondary highlight mist
        drawRect(
            brush = Brush.radialGradient(
                colors = listOf(
                    palette.secondaryGlow.copy(alpha = 0.42f * p * blobMul),
                    palette.primaryGlow.copy(alpha = 0.14f * p * blobMul),
                    Color.Transparent,
                ),
                center = Offset(w * (0.88f - 0.12f * p), h * (0.05f + 0.06f * p)),
                radius = w * 0.65f,
            )
        )

        // 3. Right-mid blob (accent @ 0.34, ~300dp) so mid-screen glass cards refract color
        drawRect(
            brush = Brush.radialGradient(
                colors = listOf(
                    palette.primaryGlow.copy(alpha = 0.34f * p * blobMul),
                    Color.Transparent,
                ),
                center = Offset(w * 0.92f, h * 0.46f),
                radius = w * 0.72f,
            )
        )

        // 4. Left-lower blob (lite @ 0.20, ~260x220dp)
        drawRect(
            brush = Brush.radialGradient(
                colors = listOf(
                    palette.secondaryGlow.copy(alpha = 0.20f * p * blobMul),
                    Color.Transparent,
                ),
                center = Offset(w * 0.12f, h * 0.68f),
                radius = w * 0.62f,
            )
        )

        // 5. Bottom-right blob (accent @ 0.45, ~360x260dp)
        drawRect(
            brush = Brush.radialGradient(
                colors = listOf(
                    palette.primaryGlow.copy(alpha = 0.45f * p * blobMul),
                    Color.Transparent,
                ),
                center = Offset(w * 0.85f, h * 0.92f),
                radius = w * 0.82f,
            )
        )
    }
}

/**
 * Translucent obsidian glass card with hairline 1dp border matching the Zentra reference UI.
 */
/**
 * Global Liquid Frosted Glass modifier (FIX-A1):
 * - Fill: 135-deg White 13% -> White 4% (zero opaque solid gray)
 * - Real GPU textureBlur (18dp High / 10dp Low / Off) on API 31+ using shared app backdrop
 * - Palette-tinted 1dp gradient border: lite 40% -> 7% -> 16% (neutral palette capped at 30%)
 * - Top 1dp inner specular highlight (White @ 12%)
 * - Bright palettes auto-add dark 28% scrim for WCAG readability
 */
@Composable
fun Modifier.darkNightGlassCardSurface(
    shape: Shape = RoundedCornerShape(20.dp),
    isMonospaceDark: Boolean = false,
): Modifier {
    val context = LocalContext.current
    DarkNightThemeStore.init(context)
    val themeState by DarkNightThemeStore.state.collectAsStateWithLifecycle()
    val palette = themeState.palette
    val backdrop = LocalAppBackgroundBackdrop.current
    val blurQuality = themeState.blurQuality
    val hasRealBlur = blurQuality != DarkNightBlurQuality.OFF &&
        backdrop != null &&
        isRenderEffectSupported()

    val isNeutral = palette.id == DarkNightPalettes.RoyalCharcoal.id
    val isBright = palette.primaryGlow.luminance() > 0.45f
    val borderTint = if (isNeutral) lerp(palette.secondaryGlow, palette.primaryGlow, 0.4f) else palette.secondaryGlow
    val maxBorderAlpha = if (isNeutral) 0.30f else 0.40f
    val baseBg = if (themeState.amoledBlack) Color.Black else palette.deepBase

    val blurMod = if (hasRealBlur && backdrop != null) {
        this.textureBlur(
            backdrop = backdrop,
            shape = shape,
            blurRadius = blurQuality.radius,
            colors = BlurColors(
                blendColors = listOf(
                    BlendColorEntry(color = Color.White.copy(alpha = 0.04f)),
                ),
            ),
        )
    } else {
        this
    }

    return blurMod
        .clip(shape)
        .drawBehind {
            if (isMonospaceDark) {
                drawRect(Color.Black.copy(alpha = 0.36f))
            } else if (!hasRealBlur) {
                // API < 31 or Blur Off translucent fallback
                drawRect(baseBg.copy(alpha = 0.32f))
                drawRect(
                    brush = Brush.linearGradient(
                        colors = listOf(Color.White.copy(alpha = 0.12f), Color.White.copy(alpha = 0.04f)),
                        start = Offset(0f, 0f),
                        end = Offset(size.width, size.height),
                    )
                )
            } else {
                if (isBright) {
                    drawRect(Color.Black.copy(alpha = 0.28f))
                }
                drawRect(
                    brush = Brush.linearGradient(
                        colors = listOf(Color.White.copy(alpha = 0.13f), Color.White.copy(alpha = 0.04f)),
                        start = Offset(0f, 0f),
                        end = Offset(size.width, size.height),
                    )
                )
            }
        }
        .border(
            width = 1.dp,
            brush = Brush.linearGradient(
                colorStops = arrayOf(
                    0.00f to borderTint.copy(alpha = maxBorderAlpha),
                    0.55f to borderTint.copy(alpha = 0.07f),
                    1.00f to borderTint.copy(alpha = 0.16f),
                ),
                start = Offset(0f, 0f),
                end = Offset(480f, 480f),
            ),
            shape = shape,
        )
        .drawWithContent {
            drawContent()
            drawLine(
                color = Color.White.copy(alpha = 0.12f),
                start = Offset(size.width * 0.08f, 1.dp.toPx()),
                end = Offset(size.width * 0.92f, 1.dp.toPx()),
                strokeWidth = 1.dp.toPx(),
            )
        }
}

@Composable
fun DarkNightGlassCard(
    palette: DarkNightPalette,
    modifier: Modifier = Modifier,
    shape: Shape = RoundedCornerShape(20.dp),
    isMonospaceDark: Boolean = false,
    onClick: (() -> Unit)? = null,
    content: @Composable ColumnScope.() -> Unit,
) {
    Box(
        modifier = modifier
            .fillMaxWidth()
            .darkNightGlassCardSurface(shape = shape, isMonospaceDark = isMonospaceDark)
            .then(if (onClick != null) Modifier.clickable(onClick = onClick) else Modifier),
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(10.dp),
            content = content,
        )
    }
}

/**
 * Pill-shaped luminous gradient CTA button matching the Zentra reference UI.
 */
@Composable
fun DarkNightGradientButton(
    text: String,
    palette: DarkNightPalette,
    enabled: Boolean = true,
    onClick: () -> Unit,
    modifier: Modifier = Modifier,
) {
    val shape = RoundedCornerShape(999.dp)
    val brush = Brush.horizontalGradient(
        colors = listOf(
            palette.buttonStart.copy(alpha = if (enabled) 1f else 0.45f),
            palette.buttonEnd.copy(alpha = if (enabled) 1f else 0.45f),
        )
    )
    Box(
        modifier = modifier
            .fillMaxWidth()
            .height(50.dp)
            .clip(shape)
            .background(brush)
            .clickable(enabled = enabled, onClick = onClick),
        contentAlignment = Alignment.Center,
    ) {
        Text(
            text = text,
            color = Color.White,
            fontWeight = FontWeight.SemiBold,
            fontSize = 15.sp,
        )
    }
}

/**
 * First-launch Zentra-inspired animated onboarding overlay:
 * Smooth one-shot aura bloom on start -> settles static -> Next / Skip enters Manager.
 */
@Composable
fun DarkNightOnboardingOverlay(
    visible: Boolean,
    palette: DarkNightPalette,
    onFinish: () -> Unit,
) {
    AnimatedVisibility(
        visible = visible,
        enter = fadeIn(tween(350)),
        exit = fadeOut(tween(400)),
    ) {
        var step by remember { mutableIntStateOf(0) }
        val titles = listOf(
            "Own Your Kernel,\nShape " to "Your Device.",
            "Stealth Root &\nFull " to "Control.",
            "Smart Profiles &\nCustom " to "Themes.",
        )
        val subtitles = listOf(
            "GKI LKM & Non-GKI kernel root with SuSFS, KPM, and built-in Zygisk — all inside DarkNightSu.",
            "Game performance profiles, bypass charging, TrickyStore hub, and 3-strike bootloop rescue.",
            "Switch between 10 aura gradient palettes or explore & upload themes in the online Theme Store.",
        )

        Box(modifier = Modifier.fillMaxSize()) {
            DarkNightAuraBackdrop(palette = palette, animateOnStart = true)

            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .statusBarsPadding()
                    .navigationBarsPadding()
                    .padding(horizontal = 24.dp, vertical = 28.dp),
                verticalArrangement = Arrangement.Bottom,
            ) {
                // Brand badge
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(8.dp),
                ) {
                    Box(
                        modifier = Modifier
                            .size(28.dp)
                            .clip(RoundedCornerShape(8.dp))
                            .background(
                                Brush.linearGradient(listOf(palette.primaryGlow, palette.secondaryGlow))
                            ),
                        contentAlignment = Alignment.Center,
                    ) {
                        Text("#", color = Color.White, fontWeight = FontWeight.Bold, fontSize = 16.sp)
                    }
                    Text(
                        text = "DarkNightSu",
                        color = Color.White,
                        fontWeight = FontWeight.SemiBold,
                        fontSize = 16.sp,
                    )
                }

                Spacer(modifier = Modifier.height(16.dp))

                val (lead, accent) = titles[step]
                Text(
                    text = buildAnnotatedString {
                        withStyle(SpanStyle(color = Color.White, fontWeight = FontWeight.Bold)) {
                            append(lead)
                        }
                        withStyle(SpanStyle(color = palette.secondaryGlow, fontWeight = FontWeight.Bold)) {
                            append(accent)
                        }
                    },
                    fontSize = 32.sp,
                    lineHeight = 38.sp,
                )

                Spacer(modifier = Modifier.height(12.dp))

                Text(
                    text = subtitles[step],
                    color = Color.White.copy(alpha = 0.72f),
                    fontSize = 14.sp,
                    lineHeight = 20.sp,
                )

                Spacer(modifier = Modifier.height(20.dp))

                // Page indicator pills
                Row(horizontalArrangement = Arrangement.spacedBy(6.dp)) {
                    repeat(titles.size) { idx ->
                        val active = idx == step
                        Box(
                            modifier = Modifier
                                .height(5.dp)
                                .width(if (active) 24.dp else 6.dp)
                                .clip(CircleShape)
                                .background(
                                    if (active) palette.primaryGlow else Color.White.copy(alpha = 0.25f)
                                )
                        )
                    }
                }

                Spacer(modifier = Modifier.height(26.dp))

                DarkNightGradientButton(
                    text = if (step < titles.lastIndex) "Next" else "Get Started",
                    palette = palette,
                    onClick = {
                        if (step < titles.lastIndex) step++ else onFinish()
                    },
                )

                Spacer(modifier = Modifier.height(14.dp))

                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .clickable(onClick = onFinish)
                        .padding(vertical = 8.dp),
                    contentAlignment = Alignment.Center,
                ) {
                    Text(
                        text = "Skip",
                        color = Color.White.copy(alpha = 0.75f),
                        fontWeight = FontWeight.Medium,
                        fontSize = 14.sp,
                    )
                }
            }
        }
    }
}


/**
 * Embedded 10-Gradient Palette Picker + Aura & Startup Animation controls
 * shown directly inside the main Settings -> Theme -> Colors screen.
 */
@Composable
fun DarkNightGradientPaletteSection(
    modifier: Modifier = Modifier,
) {
    val context = LocalContext.current
    DarkNightThemeStore.init(context)
    val themeState by DarkNightThemeStore.state.collectAsStateWithLifecycle()
    val active = themeState.palette

    Column(
        modifier = modifier
            .fillMaxWidth()
            .padding(horizontal = 16.dp, vertical = 10.dp),
        verticalArrangement = Arrangement.spacedBy(12.dp),
    ) {
        DarkNightGlassCard(palette = active) {
            Text(
                text = "DarkNight Aura & Gradient Palettes",
                color = Color.White,
                fontWeight = FontWeight.Bold,
                fontSize = 16.sp,
            )
            Text(
                text = "Active: ${active.name} (${active.hexTop}) — applies across every screen",
                color = Color.White.copy(alpha = 0.72f),
                fontSize = 12.sp,
            )

            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically,
            ) {
                Column(modifier = Modifier.weight(1f)) {
                    Text("Global Aura & Frosted Glass", color = Color.White, fontWeight = FontWeight.Medium, fontSize = 14.sp)
                    Text("Luminous top gradient & translucent cards everywhere", color = Color.White.copy(alpha = 0.65f), fontSize = 11.sp)
                }
                Switch(
                    checked = themeState.auraEnabled,
                    onCheckedChange = { DarkNightThemeStore.setAuraEnabled(context, it) },
                )
            }

            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically,
            ) {
                Column(modifier = Modifier.weight(1f)) {
                    Text("Startup Visual Animation", color = Color.White, fontWeight = FontWeight.Medium, fontSize = 14.sp)
                    Text("Animates smoothly on launch, then settles static", color = Color.White.copy(alpha = 0.65f), fontSize = 11.sp)
                }
                Switch(
                    checked = themeState.startupAnimEnabled,
                    onCheckedChange = { DarkNightThemeStore.setStartupAnimEnabled(context, it) },
                )
            }

            // FIX-A7 Blur Quality & AMOLED Pure Black controls
            Column(verticalArrangement = Arrangement.spacedBy(6.dp)) {
                Text("Glass Blur Quality", color = Color.White, fontWeight = FontWeight.Medium, fontSize = 14.sp)
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(6.dp),
                ) {
                    DarkNightBlurQuality.entries.forEach { quality ->
                        val activeQ = themeState.blurQuality == quality
                        val pillShape = RoundedCornerShape(99.dp)
                        Surface(
                            modifier = Modifier
                                .weight(1f)
                                .clip(pillShape)
                                .clickable {
                                    DarkNightThemeStore.setBlurQuality(context, quality)
                                    DarkNightToastManager.show("Blur: ${quality.label}")
                                },
                            shape = pillShape,
                            color = if (activeQ) active.primaryGlow.copy(alpha = 0.28f) else Color.White.copy(alpha = 0.07f),
                            border = BorderStroke(
                                width = 1.dp,
                                color = if (activeQ) active.secondaryGlow.copy(alpha = 0.55f) else active.secondaryGlow.copy(alpha = 0.16f),
                            ),
                        ) {
                            Box(
                                modifier = Modifier.padding(vertical = 8.dp, horizontal = 4.dp),
                                contentAlignment = Alignment.Center,
                            ) {
                                Text(
                                    text = quality.label,
                                    color = Color.White,
                                    fontSize = 11.sp,
                                    fontWeight = if (activeQ) FontWeight.SemiBold else FontWeight.Normal,
                                    maxLines = 1,
                                )
                            }
                        }
                    }
                }
            }

            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically,
            ) {
                Column(modifier = Modifier.weight(1f)) {
                    Text("AMOLED Pure Black Base", color = Color.White, fontWeight = FontWeight.Medium, fontSize = 14.sp)
                    Text("Use #000000 backdrop with softer 0.7x aura blobs", color = Color.White.copy(alpha = 0.65f), fontSize = 11.sp)
                }
                Switch(
                    checked = themeState.amoledBlack,
                    onCheckedChange = { DarkNightThemeStore.setAmoledBlack(context, it) },
                )
            }

            // Phase 1 Navigation Style selector (Bottom bar Classic vs Glass Sidebar)
            Column(verticalArrangement = Arrangement.spacedBy(6.dp)) {
                Text("Navigation Style", color = Color.White, fontWeight = FontWeight.Medium, fontSize = 14.sp)
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(8.dp),
                ) {
                    listOf(
                        DarkNightNavStyle.BOTTOM_BAR to "Bottom bar (Classic)",
                        DarkNightNavStyle.GLASS_SIDEBAR to "Glass Sidebar",
                    ).forEach { (style, label) ->
                        val activeStyle = themeState.navStyle == style
                        val pillShape = RoundedCornerShape(14.dp)
                        Surface(
                            modifier = Modifier
                                .weight(1f)
                                .clip(pillShape)
                                .clickable {
                                    DarkNightThemeStore.setNavStyle(context, style)
                                    DarkNightToastManager.show("Navigation: $label")
                                },
                            shape = pillShape,
                            color = if (activeStyle) active.primaryGlow.copy(alpha = 0.28f) else Color(0x880D111A),
                            border = BorderStroke(
                                width = if (activeStyle) 1.5.dp else 1.dp,
                                color = if (activeStyle) active.primaryGlow else Color.White.copy(alpha = 0.16f),
                            ),
                        ) {
                            Box(
                                modifier = Modifier.padding(vertical = 10.dp, horizontal = 8.dp),
                                contentAlignment = Alignment.Center,
                            ) {
                                Text(
                                    text = label,
                                    color = Color.White,
                                    fontSize = 12.sp,
                                    fontWeight = if (activeStyle) FontWeight.SemiBold else FontWeight.Normal,
                                    maxLines = 1,
                                )
                            }
                        }
                    }
                }
            }

            DarkNightPalettes.all.chunked(2).forEach { pair ->
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(10.dp),
                ) {
                    pair.forEach { pal ->
                        val isSelected = pal.id == active.id
                        val shape = RoundedCornerShape(16.dp)
                        Surface(
                            modifier = Modifier
                                .weight(1f)
                                .clip(shape)
                                .clickable {
                                    DarkNightThemeStore.selectPalette(context, pal)
                                    DarkNightToastManager.show("Applied ${pal.name}")
                                },
                            shape = shape,
                            color = Color(0xBB0D111A),
                            border = BorderStroke(
                                width = if (isSelected) 2.dp else 1.dp,
                                color = if (isSelected) pal.primaryGlow else Color.White.copy(alpha = 0.14f),
                            ),
                        ) {
                            Column(
                                modifier = Modifier.padding(8.dp),
                                verticalArrangement = Arrangement.spacedBy(6.dp),
                            ) {
                                Row(
                                    modifier = Modifier
                                        .fillMaxWidth()
                                        .height(52.dp),
                                    horizontalArrangement = Arrangement.spacedBy(5.dp),
                                ) {
                                    Box(
                                        modifier = Modifier
                                            .weight(1f)
                                            .fillMaxSize()
                                            .clip(RoundedCornerShape(8.dp))
                                            .background(Brush.verticalGradient(listOf(pal.deepBase, pal.primaryGlow)))
                                    )
                                    Box(
                                        modifier = Modifier
                                            .weight(1f)
                                            .fillMaxSize()
                                            .clip(RoundedCornerShape(8.dp))
                                            .background(Brush.verticalGradient(listOf(pal.primaryGlow, pal.deepBase)))
                                    )
                                }
                                Row(
                                    modifier = Modifier.fillMaxWidth(),
                                    verticalAlignment = Alignment.CenterVertically,
                                    horizontalArrangement = Arrangement.SpaceBetween,
                                ) {
                                    Column(modifier = Modifier.weight(1f)) {
                                        Text(
                                            text = pal.name,
                                            color = Color.White,
                                            fontWeight = FontWeight.SemiBold,
                                            fontSize = 12.sp,
                                            maxLines = 1,
                                        )
                                        Text(
                                            text = pal.hexTop,
                                            color = Color.White.copy(alpha = 0.6f),
                                            fontSize = 10.sp,
                                            maxLines = 1,
                                        )
                                    }
                                    if (isSelected) {
                                        Icon(
                                            imageVector = Icons.Filled.CheckCircle,
                                            contentDescription = "Selected",
                                            tint = pal.primaryGlow,
                                            modifier = Modifier.size(16.dp),
                                        )
                                    } else {
                                        Box(
                                            modifier = Modifier
                                                .size(12.dp)
                                                .clip(CircleShape)
                                                .background(pal.primaryGlow)
                                        )
                                    }
                                }
                            }
                        }
                    }
                    if (pair.size == 1) Spacer(modifier = Modifier.weight(1f))
                }
            }

            OutlinedButton(
                onClick = {
                    DarkNightThemeStore.setOnboardingCompleted(context, false)
                    DarkNightToastManager.show("Startup intro will show on next launch")
                },
                modifier = Modifier.fillMaxWidth(),
                border = BorderStroke(1.dp, Color.White.copy(alpha = 0.22f)),
            ) {
                Text("Replay Startup Intro Screen", color = Color.White)
            }
        }
    }
}


/** Shared bottom bar clearance (bottom bar height + system navigation bar + margin). */
val LocalBottomBarPadding = staticCompositionLocalOf<Dp> { 0.dp }

/**
 * Opaque screen wrapper for detail destinations so previous screens in the NavDisplay
 * back stack never ghost/bleed behind Theme, Theme Store, Toolbox, DarkTune, etc.
 */
@Composable
fun DarkNightScreenBackground(
    modifier: Modifier = Modifier,
    content: @Composable () -> Unit,
) {
    val context = LocalContext.current
    DarkNightThemeStore.init(context)
    val state by DarkNightThemeStore.state.collectAsStateWithLifecycle()
    Box(
        modifier = modifier
            .fillMaxSize()
            .background(state.palette.deepBase)
    ) {
        if (state.auraEnabled) {
            DarkNightAuraBackdrop(
                palette = state.palette,
                animateOnStart = false,
            )
        }
        content()
    }
}

/**
 * Single-instance Glass Toast manager: new messages immediately replace the old one (no stacking).
 */
data class DarkNightToastMessage(val id: Long, val text: String)

object DarkNightToastManager {
    private val _current = MutableStateFlow<DarkNightToastMessage?>(null)
    val current: StateFlow<DarkNightToastMessage?> = _current.asStateFlow()

    fun show(text: String) {
        _current.value = DarkNightToastMessage(System.nanoTime(), text)
    }

    fun dismiss(id: Long) {
        if (_current.value?.id == id) {
            _current.value = null
        }
    }
}

@Composable
fun DarkNightGlassToastHost(
    palette: DarkNightPalette,
    modifier: Modifier = Modifier,
) {
    val toast by DarkNightToastManager.current.collectAsStateWithLifecycle()
    LaunchedEffect(toast?.id) {
        val id = toast?.id ?: return@LaunchedEffect
        delay(1800)
        DarkNightToastManager.dismiss(id)
    }

    Box(
        modifier = modifier
            .fillMaxSize()
            .navigationBarsPadding()
            .padding(bottom = 92.dp, start = 24.dp, end = 24.dp),
        contentAlignment = Alignment.BottomCenter,
    ) {
        AnimatedVisibility(
            visible = toast != null,
            enter = fadeIn(tween(180)),
            exit = fadeOut(tween(220)),
        ) {
            toast?.let { msg ->
                Surface(
                    shape = RoundedCornerShape(999.dp),
                    color = Color(0xE80A0E17),
                    border = BorderStroke(1.dp, palette.primaryGlow.copy(alpha = 0.65f)),
                ) {
                    Row(
                        modifier = Modifier.padding(horizontal = 18.dp, vertical = 10.dp),
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.spacedBy(8.dp),
                    ) {
                        Box(
                            modifier = Modifier
                                .size(8.dp)
                                .clip(CircleShape)
                                .background(palette.primaryGlow)
                        )
                        Text(
                            text = msg.text,
                            color = Color.White,
                            fontSize = 13.sp,
                            fontWeight = FontWeight.Medium,
                        )
                    }
                }
            }
        }
    }
}
