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
import androidx.compose.material3.Surface
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
import androidx.compose.ui.graphics.toArgb
import androidx.compose.ui.text.SpanStyle
import androidx.compose.ui.text.buildAnnotatedString
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.withStyle
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
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

data class DarkNightThemeState(
    val palette: DarkNightPalette = DarkNightPalettes.DarkNightAura,
    val auraEnabled: Boolean = true,
    val startupAnimEnabled: Boolean = true,
    val onboardingCompleted: Boolean = false,
)

object DarkNightThemeStore {
    private const val PREFS_NAME = "darknight_theme_prefs"
    private const val KEY_PALETTE = "palette_id"
    private const val KEY_AURA = "aura_enabled"
    private const val KEY_STARTUP_ANIM = "startup_anim_enabled"
    private const val KEY_ONBOARDING_DONE = "onboarding_done"

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

    Canvas(
        modifier = modifier
            .fillMaxSize()
            .background(palette.deepBase)
    ) {
        val w = size.width
        val h = size.height

        // Primary upper-left / top-center luminous bloom that glides into place and settles static
        val primaryCenter = Offset(
            x = w * (0.18f + 0.14f * p),
            y = h * (0.04f + 0.08f * p),
        )
        drawRect(
            brush = Brush.radialGradient(
                colors = listOf(
                    palette.primaryGlow.copy(alpha = 0.78f * p),
                    palette.primaryGlow.copy(alpha = 0.34f * p),
                    Color.Transparent,
                ),
                center = primaryCenter,
                radius = w * (0.85f + 0.25f * p),
            )
        )

        // Secondary soft highlight mist on upper-right
        val highlightCenter = Offset(
            x = w * (0.88f - 0.18f * p),
            y = h * (0.02f + 0.10f * p),
        )
        drawRect(
            brush = Brush.radialGradient(
                colors = listOf(
                    palette.secondaryGlow.copy(alpha = 0.58f * p),
                    palette.primaryGlow.copy(alpha = 0.20f * p),
                    Color.Transparent,
                ),
                center = highlightCenter,
                radius = w * (0.68f + 0.18f * p),
            )
        )

        // Deep obsidian vertical fade to keep lower cards ultra-legible
        drawRect(
            brush = Brush.verticalGradient(
                colors = listOf(
                    Color.Transparent,
                    palette.deepBase.copy(alpha = 0.65f),
                    palette.deepBase,
                ),
                startY = h * 0.28f,
                endY = h * 0.78f,
            )
        )
    }
}

/**
 * Translucent obsidian glass card with hairline 1dp border matching the Zentra reference UI.
 */
@Composable
fun DarkNightGlassCard(
    palette: DarkNightPalette,
    modifier: Modifier = Modifier,
    onClick: (() -> Unit)? = null,
    content: @Composable ColumnScope.() -> Unit,
) {
    val shape = RoundedCornerShape(22.dp)
    Surface(
        modifier = modifier
            .fillMaxWidth()
            .clip(shape)
            .then(if (onClick != null) Modifier.clickable(onClick = onClick) else Modifier),
        shape = shape,
        color = palette.cardSurface,
        border = BorderStroke(1.dp, Color.White.copy(alpha = 0.11f)),
    ) {
        Column(
            modifier = Modifier.padding(16.dp),
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
