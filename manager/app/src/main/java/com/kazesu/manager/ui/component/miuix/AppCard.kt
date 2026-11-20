package com.kazesu.manager.ui.component.miuix

import androidx.compose.foundation.layout.ColumnScope
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.Dp
import androidx.compose.foundation.shape.RoundedCornerShape
import com.kazesu.manager.ui.theme.LocalBackgroundImagePath
import com.kazesu.manager.ui.theme.backgroundAwareCardColors
import com.kazesu.manager.ui.theme.darkNightGlassCardSurface
import top.yukonga.miuix.kmp.basic.Card
import top.yukonga.miuix.kmp.basic.CardColors
import top.yukonga.miuix.kmp.basic.CardDefaults
import top.yukonga.miuix.kmp.utils.PressFeedbackType

/**
 * A miuix [Card] whose default colors follow the custom background's card opacity.
 *
 * Explicit [colors] are forwarded unchanged.
 */
@Composable
fun AppCard(
    modifier: Modifier = Modifier,
    cornerRadius: Dp = CardDefaults.CornerRadius,
    insideMargin: PaddingValues = CardDefaults.InsideMargin,
    colors: CardColors = backgroundAwareCardColors(),
    content: @Composable ColumnScope.() -> Unit,
) {
    val isDarkNightGlass = LocalBackgroundImagePath.current == "darknight://aura"
    val glassMod = if (isDarkNightGlass) Modifier.darkNightGlassCardSurface(RoundedCornerShape(cornerRadius)) else Modifier
    Card(
        modifier = modifier.then(glassMod),
        cornerRadius = cornerRadius,
        insideMargin = insideMargin,
        colors = colors,
        content = content,
    )
}

@Composable
fun AppCard(
    modifier: Modifier = Modifier,
    cornerRadius: Dp = CardDefaults.CornerRadius,
    insideMargin: PaddingValues = CardDefaults.InsideMargin,
    colors: CardColors = backgroundAwareCardColors(),
    pressFeedbackType: PressFeedbackType = PressFeedbackType.None,
    showIndication: Boolean = false,
    holdDownState: Boolean = false,
    onClick: (() -> Unit)? = null,
    onLongPress: (() -> Unit)? = null,
    content: @Composable ColumnScope.() -> Unit,
) {
    val isDarkNightGlass = LocalBackgroundImagePath.current == "darknight://aura"
    val glassMod = if (isDarkNightGlass) Modifier.darkNightGlassCardSurface(RoundedCornerShape(cornerRadius)) else Modifier
    Card(
        modifier = modifier.then(glassMod),
        cornerRadius = cornerRadius,
        insideMargin = insideMargin,
        colors = colors,
        pressFeedbackType = pressFeedbackType,
        showIndication = showIndication,
        holdDownState = holdDownState,
        onClick = onClick,
        onLongPress = onLongPress,
        content = content,
    )
}
