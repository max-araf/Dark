package com.kazesu.manager.ui.component

import android.os.Build
import android.system.Os
import androidx.activity.compose.BackHandler
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.core.animateDpAsState
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.spring
import androidx.compose.animation.core.tween
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.gestures.detectHorizontalDragGestures
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.BoxWithConstraints
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.defaultMinSize
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.navigationBarsPadding
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.statusBarsPadding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.KeyboardArrowRight
import androidx.compose.material.icons.filled.DeveloperMode
import androidx.compose.material.icons.filled.Description
import androidx.compose.material.icons.filled.Extension
import androidx.compose.material.icons.filled.Home
import androidx.compose.material.icons.filled.Info
import androidx.compose.material.icons.filled.Memory
import androidx.compose.material.icons.filled.Palette
import androidx.compose.material.icons.filled.RestartAlt
import androidx.compose.material.icons.filled.Security
import androidx.compose.material.icons.filled.Settings
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Icon
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.drawBehind
import androidx.compose.ui.draw.drawWithContent
import androidx.compose.ui.draw.scale
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Shape
import androidx.compose.ui.graphics.luminance
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.kazesu.manager.R
import com.kazesu.manager.ui.theme.DarkNightPalette
import com.kazesu.manager.ui.theme.DarkNightToastManager
import com.kazesu.manager.ui.util.getSELinuxStatusRaw
import com.kazesu.manager.ui.util.reboot
import top.yukonga.miuix.kmp.blur.BlendColorEntry
import top.yukonga.miuix.kmp.blur.BlurColors
import top.yukonga.miuix.kmp.blur.LayerBackdrop
import top.yukonga.miuix.kmp.blur.textureBlur
import top.yukonga.miuix.kmp.shader.isRenderEffectSupported

/**
 * Reusable DarkNight Liquid Glass surface modifier matching darknight_glass_nav.png:
 * - Real GPU textureBlur (26f) on API 31+ when backdrop is available, higher-opacity fallback on API <31
 * - 165-deg dark-to-accent gradient + bottom-right radial highlight + diagonal sheen + 1dp gradient rim
 */
@Composable
fun Modifier.darkNightGlass(
    shape: Shape,
    palette: DarkNightPalette,
    backdrop: LayerBackdrop? = null,
): Modifier {
    val accent = palette.primaryGlow
    val lite = palette.secondaryGlow
    val hasRealBlur = backdrop != null && isRenderEffectSupported()
    val alphaBoost = if (hasRealBlur) 0f else 0.15f
    // Bright palettes (Amber Glow, Mint Aurora, Golden Bronze) need deeper dark scrim for legibility
    val isBrightAccent = accent.luminance() > 0.45f
    val topDarkAlpha = (if (isBrightAccent) 0.90f else 0.80f + alphaBoost).coerceAtMost(0.96f)
    val midDarkAlpha = (if (isBrightAccent) 0.62f else 0.44f + alphaBoost).coerceAtMost(0.88f)
    val accentMidAlpha = (if (isBrightAccent) 0.28f else 0.36f + alphaBoost * 0.5f).coerceAtMost(0.75f)
    val accentEndAlpha = (if (isBrightAccent) 0.48f else 0.64f + alphaBoost * 0.5f).coerceAtMost(0.88f)

    val baseBlur = if (hasRealBlur && backdrop != null) {
        this.textureBlur(
            backdrop = backdrop,
            shape = shape,
            blurRadius = 26f,
            colors = BlurColors(
                blendColors = listOf(
                    BlendColorEntry(color = Color(0xFF040710).copy(alpha = 0.20f)),
                ),
            ),
        )
    } else {
        this
    }

    return baseBlur
        .clip(shape)
        .drawBehind {
            // 165-degree dark -> tint -> accent gradient
            drawRect(
                brush = Brush.linearGradient(
                    colorStops = arrayOf(
                        0.00f to Color(0xFF040710).copy(alpha = topDarkAlpha),
                        0.26f to Color(0xFF040710).copy(alpha = midDarkAlpha),
                        0.60f to accent.copy(alpha = accentMidAlpha),
                        1.00f to accent.copy(alpha = accentEndAlpha),
                    ),
                    start = Offset(0f, 0f),
                    end = Offset(size.width * 0.35f, size.height),
                )
            )
            // Bottom-right radial highlight
            drawRect(
                brush = Brush.radialGradient(
                    colors = listOf(lite.copy(alpha = 0.28f), Color.Transparent),
                    center = Offset(size.width * 0.88f, size.height * 0.88f),
                    radius = size.width * 1.1f,
                )
            )
            // Diagonal glass sheen overlay (top-left -> 46%)
            drawRect(
                brush = Brush.linearGradient(
                    colorStops = arrayOf(
                        0.00f to Color.White.copy(alpha = 0.16f),
                        0.46f to Color.Transparent,
                    ),
                    start = Offset(0f, 0f),
                    end = Offset(size.width * 0.8f, size.height * 0.45f),
                )
            )
        }
        .border(
            width = 1.dp,
            brush = Brush.linearGradient(
                colors = listOf(lite.copy(alpha = 0.55f), lite.copy(alpha = 0.15f)),
                start = Offset(0f, 0f),
                end = Offset(400f, 900f),
            ),
            shape = shape,
        )
        .drawWithContent {
            drawContent()
            // 1dp inner top specular highlight
            drawLine(
                color = Color.White.copy(alpha = 0.22f),
                start = Offset(size.width * 0.12f, 1.dp.toPx()),
                end = Offset(size.width * 0.88f, 1.dp.toPx()),
                strokeWidth = 1.dp.toPx(),
            )
        }
}

private data class SidebarMenuItem(
    val id: String,
    val labelRes: Int,
    val iconRes: Int,
    val badgeCount: Int = 0,
    val onClick: () -> Unit,
)

@Composable
fun DarkNightGlassSidebarOverlay(
    palette: DarkNightPalette,
    selectedPage: Int,
    moduleBadgeCount: Int,
    backdrop: LayerBackdrop?,
    animationsEnabled: Boolean = true,
    onSelectPage: (Int) -> Unit,
    onOpenDarkTune: () -> Unit,
    onOpenToolbox: () -> Unit,
    onOpenThemeStudio: () -> Unit,
    onOpenSendLogs: () -> Unit,
    onOpenAbout: () -> Unit,
    modifier: Modifier = Modifier,
) {
    var expanded by remember { mutableStateOf(false) }
    var showRebootDialog by remember { mutableStateOf(false) }

    BackHandler(enabled = expanded) {
        expanded = false
    }

    val kernelRelease = remember {
        runCatching { Os.uname().release.substringBefore("-") }.getOrDefault("Kernel")
    }
    val selinuxLabel = remember {
        runCatching { getSELinuxStatusRaw() }.getOrDefault("Enforcing")
    }
    val deviceModel = remember { Build.MODEL.ifBlank { "Android Device" } }

    val menuItems = listOf(
        SidebarMenuItem("home", R.string.home, R.drawable.ic_dn_home) { onSelectPage(0) },
        SidebarMenuItem("superuser", R.string.superuser, R.drawable.ic_dn_superuser) { onSelectPage(1) },
        SidebarMenuItem("module", R.string.module, R.drawable.ic_dn_module, badgeCount = moduleBadgeCount) { onSelectPage(2) },
        SidebarMenuItem("darktune", R.string.kernel_tuning, R.drawable.ic_dn_darktune) { onOpenDarkTune() },
        SidebarMenuItem("toolbox", R.string.darknight_toolbox_title, R.drawable.ic_dn_toolbox) { onOpenToolbox() },
    )

    val systemItems = listOf(
        SidebarMenuItem("theme", R.string.darknight_nav_theme_studio, R.drawable.ic_dn_theme) { onOpenThemeStudio() },
        SidebarMenuItem("logs", R.string.send_log, R.drawable.ic_dn_logs) { onOpenSendLogs() },
        SidebarMenuItem("settings", R.string.settings, R.drawable.ic_dn_settings) { onSelectPage(3) },
        SidebarMenuItem("about", R.string.about, R.drawable.ic_dn_about) { onOpenAbout() },
        SidebarMenuItem("reboot", R.string.darknight_nav_reboot_options, R.drawable.ic_dn_reboot) { showRebootDialog = true },
    )

    val selectedId = when (selectedPage) {
        0 -> "home"
        1 -> "superuser"
        2 -> "module"
        3 -> "settings"
        else -> "home"
    }

    BoxWithConstraints(modifier = modifier.fillMaxSize()) {
        val expandedWidth = (maxWidth * 0.80f).coerceAtMost(300.dp)
        val sidebarWidth: Dp by animateDpAsState(
            targetValue = if (expanded) expandedWidth else 56.dp,
            animationSpec = if (animationsEnabled) {
                spring(dampingRatio = 0.80f, stiffness = 380f)
            } else {
                tween(0)
            },
            label = "sidebar_width",
        )
        val cornerRadius: Dp by animateDpAsState(
            targetValue = if (expanded) 32.dp else 28.dp,
            animationSpec = if (animationsEnabled) tween(220) else tween(0),
            label = "sidebar_corner",
        )
        val scrimAlpha by animateFloatAsState(
            targetValue = if (expanded) 0.34f else 0f,
            animationSpec = if (animationsEnabled) tween(220) else tween(0),
            label = "sidebar_scrim",
        )

        // Tap-to-dismiss dark scrim behind expanded drawer
        if (scrimAlpha > 0.01f) {
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .background(Color.Black.copy(alpha = scrimAlpha))
                    .clickable { expanded = false }
            )
        }

        val shape = RoundedCornerShape(cornerRadius)
        Box(
            modifier = Modifier
                .align(Alignment.CenterStart)
                .statusBarsPadding()
                .navigationBarsPadding()
                .padding(
                    start = if (expanded) 12.dp else 10.dp,
                    top = if (expanded) 6.dp else 8.dp,
                    bottom = if (expanded) 6.dp else 8.dp,
                )
                .width(sidebarWidth)
                .fillMaxHeight()
                .darkNightGlass(shape = shape, palette = palette, backdrop = backdrop)
                .pointerInput(expanded) {
                    detectHorizontalDragGestures { _, dragAmount ->
                        if (!expanded && dragAmount > 18f) expanded = true
                        if (expanded && dragAmount < -18f) expanded = false
                    }
                }
        ) {
            if (!expanded) {
                // COLLAPSED RAIL (Panel 1 of darknight_glass_nav.png)
                Column(
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(top = 14.dp, bottom = 10.dp),
                    horizontalAlignment = Alignment.CenterHorizontally,
                ) {
                    // App Logo (36dp fixed square, radius 11dp) -> tap expands drawer
                    Image(
                        painter = painterResource(R.drawable.ic_launcher_darknight_full),
                        contentDescription = stringResource(R.string.app_name),
                        modifier = Modifier
                            .size(36.dp)
                            .clip(RoundedCornerShape(11.dp))
                            .clickable { expanded = true },
                        contentScale = ContentScale.Crop,
                    )

                    Spacer(modifier = Modifier.height(10.dp))
                    GlassDivider(width = 28.dp)
                    Spacer(modifier = Modifier.height(10.dp))

                    // 5 Main Menu items
                    Column(
                        verticalArrangement = Arrangement.spacedBy(6.dp),
                        horizontalAlignment = Alignment.CenterHorizontally,
                    ) {
                        menuItems.forEach { item ->
                            RailIconButton(
                                item = item,
                                selected = item.id == selectedId,
                                palette = palette,
                                onClick = { item.onClick() },
                            )
                        }
                    }

                    Spacer(modifier = Modifier.weight(1f))
                    GlassDivider(width = 28.dp)
                    Spacer(modifier = Modifier.height(10.dp))

                    // Bottom shortcuts: Theme, Settings
                    Column(
                        verticalArrangement = Arrangement.spacedBy(6.dp),
                        horizontalAlignment = Alignment.CenterHorizontally,
                    ) {
                        RailIconButton(
                            item = systemItems[0],
                            selected = false,
                            palette = palette,
                            onClick = { systemItems[0].onClick() },
                        )
                        RailIconButton(
                            item = systemItems[2],
                            selected = selectedId == "settings",
                            palette = palette,
                            onClick = { systemItems[2].onClick() },
                        )
                    }

                    Spacer(modifier = Modifier.height(10.dp))

                    // Bottom avatar/logo circle (38dp) -> About
                    Box(
                        modifier = Modifier
                            .size(38.dp)
                            .clip(CircleShape)
                            .background(Color(0x99040710))
                            .border(1.dp, palette.secondaryGlow.copy(alpha = 0.45f), CircleShape)
                            .clickable { onOpenAbout() },
                        contentAlignment = Alignment.Center,
                    ) {
                        Text(
                            text = "#",
                            color = palette.secondaryGlow,
                            fontWeight = FontWeight.Bold,
                            fontSize = 18.sp,
                        )
                    }
                }
            } else {
                // EXPANDED DRAWER (Panels 2 & 3 of darknight_glass_nav.png)
                Column(
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(horizontal = 14.dp, vertical = 16.dp),
                ) {
                    // Header
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .clickable { expanded = false }
                            .padding(horizontal = 4.dp, vertical = 4.dp),
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.spacedBy(12.dp),
                    ) {
                        Image(
                            painter = painterResource(R.drawable.ic_launcher_darknight_full),
                            contentDescription = stringResource(R.string.app_name),
                            modifier = Modifier
                                .size(44.dp)
                                .clip(RoundedCornerShape(14.dp)),
                            contentScale = ContentScale.Crop,
                        )
                        Column(modifier = Modifier.weight(1f)) {
                            Text(
                                text = stringResource(R.string.app_name),
                                color = Color.White,
                                fontSize = 21.sp,
                                fontWeight = FontWeight.Bold,
                                maxLines = 1,
                                overflow = TextOverflow.Ellipsis,
                            )
                            Text(
                                text = stringResource(R.string.darknight_subtitle),
                                color = Color.White.copy(alpha = 0.65f),
                                fontSize = 11.sp,
                                maxLines = 1,
                                overflow = TextOverflow.Ellipsis,
                            )
                        }
                    }

                    Spacer(modifier = Modifier.height(12.dp))
                    GlassDivider(modifier = Modifier.fillMaxWidth())
                    Spacer(modifier = Modifier.height(10.dp))

                    Column(
                        modifier = Modifier
                            .weight(1f)
                            .verticalScroll(rememberScrollState()),
                        verticalArrangement = Arrangement.spacedBy(4.dp),
                    ) {
                        Text(
                            text = stringResource(R.string.darknight_nav_section_menu),
                            color = Color.White.copy(alpha = 0.50f),
                            fontSize = 11.sp,
                            fontWeight = FontWeight.SemiBold,
                            letterSpacing = 1.6.sp,
                            modifier = Modifier.padding(horizontal = 10.dp, vertical = 4.dp),
                        )

                        menuItems.forEach { item ->
                            DrawerMenuRow(
                                item = item,
                                selected = item.id == selectedId,
                                palette = palette,
                                onClick = {
                                    expanded = false
                                    item.onClick()
                                },
                            )
                        }

                        Spacer(modifier = Modifier.height(10.dp))
                        GlassDivider(modifier = Modifier.fillMaxWidth())
                        Spacer(modifier = Modifier.height(10.dp))

                        Text(
                            text = stringResource(R.string.darknight_nav_section_system),
                            color = Color.White.copy(alpha = 0.50f),
                            fontSize = 11.sp,
                            fontWeight = FontWeight.SemiBold,
                            letterSpacing = 1.6.sp,
                            modifier = Modifier.padding(horizontal = 10.dp, vertical = 4.dp),
                        )

                        systemItems.forEach { item ->
                            DrawerMenuRow(
                                item = item,
                                selected = item.id == selectedId,
                                palette = palette,
                                onClick = {
                                    expanded = false
                                    item.onClick()
                                },
                            )
                        }
                    }

                    Spacer(modifier = Modifier.height(10.dp))

                    // Footer Device + Kernel card -> About
                    Surface(
                        modifier = Modifier
                            .fillMaxWidth()
                            .clip(RoundedCornerShape(18.dp))
                            .clickable {
                                expanded = false
                                onOpenAbout()
                            },
                        shape = RoundedCornerShape(18.dp),
                        color = Color(0x66040710),
                        border = BorderStroke(1.dp, Color.White.copy(alpha = 0.14f)),
                    ) {
                        Row(
                            modifier = Modifier.padding(horizontal = 12.dp, vertical = 10.dp),
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.spacedBy(10.dp),
                        ) {
                            Box(
                                modifier = Modifier
                                    .size(34.dp)
                                    .clip(CircleShape)
                                    .background(palette.primaryGlow.copy(alpha = 0.25f))
                                    .border(1.dp, palette.secondaryGlow.copy(alpha = 0.5f), CircleShape),
                                contentAlignment = Alignment.Center,
                            ) {
                                Text("#", color = Color.White, fontWeight = FontWeight.Bold, fontSize = 16.sp)
                            }
                            Column(modifier = Modifier.weight(1f)) {
                                Text(
                                    text = deviceModel,
                                    color = Color.White,
                                    fontWeight = FontWeight.SemiBold,
                                    fontSize = 13.sp,
                                    maxLines = 1,
                                    overflow = TextOverflow.Ellipsis,
                                )
                                Text(
                                    text = "Kernel $kernelRelease • SELinux $selinuxLabel",
                                    color = Color.White.copy(alpha = 0.62f),
                                    fontSize = 10.sp,
                                    maxLines = 1,
                                    overflow = TextOverflow.Ellipsis,
                                )
                            }
                            Icon(
                                painter = painterResource(R.drawable.ic_dn_chevron),
                                contentDescription = null,
                                tint = Color.White.copy(alpha = 0.65f),
                                modifier = Modifier.size(18.dp),
                            )
                        }
                    }
                }
            }
        }
    }

    if (showRebootDialog) {
        AlertDialog(
            onDismissRequest = { showRebootDialog = false },
            title = { Text(stringResource(R.string.darknight_nav_reboot_options)) },
            text = {
                Column(verticalArrangement = Arrangement.spacedBy(6.dp)) {
                    listOf(
                        stringResource(R.string.reboot) to "",
                        stringResource(R.string.reboot_recovery) to "recovery",
                        stringResource(R.string.reboot_bootloader) to "bootloader",
                        stringResource(R.string.reboot_download) to "download",
                    ).forEach { (label, reason) ->
                        TextButton(
                            onClick = {
                                showRebootDialog = false
                                DarkNightToastManager.show(label)
                                reboot(reason)
                            },
                            modifier = Modifier.fillMaxWidth(),
                        ) {
                            Text(label)
                        }
                    }
                }
            },
            confirmButton = {
                TextButton(onClick = { showRebootDialog = false }) {
                    Text(stringResource(android.R.string.cancel))
                }
            },
        )
    }
}

@Composable
private fun RailIconButton(
    item: SidebarMenuItem,
    selected: Boolean,
    palette: DarkNightPalette,
    onClick: () -> Unit,
) {
    val pillShape = RoundedCornerShape(15.dp)
    Box(
        modifier = Modifier
            .fillMaxWidth()
            .height(42.dp),
        contentAlignment = Alignment.Center,
    ) {
        // Left edge 3.5dp x 24dp white indicator bar on selected item
        if (selected) {
            Box(
                modifier = Modifier
                    .align(Alignment.CenterStart)
                    .width(3.5.dp)
                    .height(24.dp)
                    .clip(RoundedCornerShape(topEnd = 4.dp, bottomEnd = 4.dp))
                    .background(Color.White)
            )
        }

        Box(
            modifier = Modifier
                .size(42.dp)
                .clip(pillShape)
                .then(
                    if (selected) {
                        Modifier
                            .background(
                                Brush.verticalGradient(
                                    listOf(Color.White.copy(alpha = 0.26f), Color.White.copy(alpha = 0.08f))
                                )
                            )
                            .border(1.dp, Color.White.copy(alpha = 0.28f), pillShape)
                    } else {
                        Modifier
                    }
                )
                .clickable(onClick = onClick),
            contentAlignment = Alignment.Center,
        ) {
            Box(contentAlignment = Alignment.Center) {
                Icon(
                    painter = painterResource(item.iconRes),
                    contentDescription = stringResource(item.labelRes),
                    tint = if (selected) Color.White else Color.White.copy(alpha = 0.68f),
                    modifier = Modifier.size(22.dp),
                )
                if (item.badgeCount > 0) {
                    Box(
                        modifier = Modifier
                            .align(Alignment.TopEnd)
                            .offset(x = 6.dp, y = (-6).dp)
                            .defaultMinSize(minWidth = 16.dp, minHeight = 16.dp)
                            .clip(CircleShape)
                            .background(palette.primaryGlow)
                            .border(1.5.dp, Color(0xFF07090F), CircleShape)
                            .padding(horizontal = 3.dp),
                        contentAlignment = Alignment.Center,
                    ) {
                        Text(
                            text = if (item.badgeCount > 99) "99+" else item.badgeCount.toString(),
                            color = Color.White,
                            fontSize = 9.5.sp,
                            fontWeight = FontWeight.Bold,
                            maxLines = 1,
                        )
                    }
                }
            }
        }
    }
}

@Composable
private fun DrawerMenuRow(
    item: SidebarMenuItem,
    selected: Boolean,
    palette: DarkNightPalette,
    onClick: () -> Unit,
) {
    val pillShape = RoundedCornerShape(16.dp)
    Box(
        modifier = Modifier
            .fillMaxWidth()
            .height(46.dp),
        contentAlignment = Alignment.CenterStart,
    ) {
        if (selected) {
            Box(
                modifier = Modifier
                    .align(Alignment.CenterStart)
                    .offset(x = (-14).dp)
                    .width(3.5.dp)
                    .height(24.dp)
                    .clip(RoundedCornerShape(topEnd = 4.dp, bottomEnd = 4.dp))
                    .background(Color.White)
            )
        }

        Row(
            modifier = Modifier
                .fillMaxSize()
                .clip(pillShape)
                .then(
                    if (selected) {
                        Modifier
                            .background(
                                Brush.horizontalGradient(
                                    listOf(Color.White.copy(alpha = 0.24f), Color.White.copy(alpha = 0.08f))
                                )
                            )
                            .border(1.dp, Color.White.copy(alpha = 0.25f), pillShape)
                    } else {
                        Modifier
                    }
                )
                .clickable(onClick = onClick)
                .padding(horizontal = 12.dp),
            verticalAlignment = Alignment.CenterVertically,
        ) {
            Icon(
                painter = painterResource(item.iconRes),
                contentDescription = null,
                tint = if (selected) Color.White else Color.White.copy(alpha = 0.68f),
                modifier = Modifier.size(22.dp),
            )
            Spacer(modifier = Modifier.width(14.dp))
            Text(
                text = stringResource(item.labelRes),
                color = if (selected) Color.White else Color.White.copy(alpha = 0.88f),
                fontSize = 15.sp,
                fontWeight = if (selected) FontWeight.SemiBold else FontWeight.Normal,
                maxLines = 1,
                overflow = TextOverflow.Ellipsis,
                modifier = Modifier.weight(1f),
            )
            if (selected) {
                Icon(
                    painter = painterResource(R.drawable.ic_dn_chevron),
                    contentDescription = null,
                    tint = Color.White.copy(alpha = 0.85f),
                    modifier = Modifier.size(18.dp),
                )
            } else if (item.badgeCount > 0) {
                Box(
                    modifier = Modifier
                        .defaultMinSize(minWidth = 22.dp, minHeight = 22.dp)
                        .clip(CircleShape)
                        .background(palette.primaryGlow)
                        .border(1.5.dp, Color(0xFF07090F), CircleShape)
                        .padding(horizontal = 5.dp),
                    contentAlignment = Alignment.Center,
                ) {
                    Text(
                        text = if (item.badgeCount > 99) "99" else item.badgeCount.toString(),
                        color = Color.White,
                        fontSize = 10.sp,
                        fontWeight = FontWeight.Bold,
                    )
                }
            }
        }
    }
}

@Composable
private fun GlassDivider(
    modifier: Modifier = Modifier,
    width: Dp? = null,
) {
    Box(
        modifier = modifier
            .then(if (width != null) Modifier.width(width) else Modifier)
            .height(1.dp)
            .background(
                Brush.horizontalGradient(
                    listOf(Color.Transparent, Color.White.copy(alpha = 0.24f), Color.Transparent)
                )
            )
    )
}
