package com.example.ui.components

import androidx.compose.animation.core.animateDpAsState
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.spring
import androidx.compose.animation.core.Spring
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.interaction.collectIsPressedAsState
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Check
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp

// Neobrutalist Design System Color Tokens (Artistic Flair Theme)
object NeoColors {
    val Background = Color(0xFFFFE5EC)
    val BorderDark = Color(0xFF000000)
    val PrimaryPink = Color(0xFFFF4D6D)
    val PrimaryContainer = Color(0xFFFF7096)
    val PrimaryLight = Color(0xFFFFC0CB) // Soft light pink
    val PrimaryMuted = Color(0xFFFFF0F3) // Very soft pink
    val SecondaryMuted = Color(0xFFFF7096)
    val SecondaryLight = Color(0xFFFFD60A) // Yellow
    val AccentYellow = Color(0xFFFFD60A) // Yellow
    val AccentTurquoise = Color(0xFF90E0EF) // Sky Blue
    val CardBackground = Color(0xFFFFFFFF)
}

@Composable
fun NeoCard(
    modifier: Modifier = Modifier,
    backgroundColor: Color = NeoColors.CardBackground,
    borderColor: Color = NeoColors.BorderDark,
    borderWidth: Dp = 4.dp,
    shadowColor: Color = NeoColors.BorderDark,
    shadowOffset: Dp = 8.dp,
    cornerRadius: Dp = 12.dp,
    contentPadding: PaddingValues = PaddingValues(16.dp),
    onClick: (() -> Unit)? = null,
    content: @Composable ColumnScope.() -> Unit
) {
    val interactionSource = remember { MutableInteractionSource() }
    val isPressed by if (onClick != null) {
        interactionSource.collectIsPressedAsState()
    } else {
        remember { mutableStateOf(false) }
    }

    val scale by animateFloatAsState(
        targetValue = if (isPressed) 1.04f else 1.0f,
        animationSpec = spring(
            dampingRatio = Spring.DampingRatioMediumBouncy,
            stiffness = Spring.StiffnessMedium
        ),
        label = "CardScale"
    )

    val cardModifier = if (onClick != null) {
        modifier
            .graphicsLayer {
                scaleX = scale
                scaleY = scale
            }
            .clickable(
                interactionSource = interactionSource,
                indication = null,
                onClick = onClick
            )
    } else {
        modifier
    }

    Box(modifier = cardModifier.padding(bottom = shadowOffset, end = shadowOffset)) {
        // Shadow Layer (placed behind with offset)
        Box(
            modifier = Modifier
                .matchParentSize()
                .offset(x = shadowOffset, y = shadowOffset)
                .background(shadowColor, RoundedCornerShape(cornerRadius))
                .border(2.dp, NeoColors.BorderDark, RoundedCornerShape(cornerRadius))
        )
        // Content Layer
        Column(
            modifier = Modifier
                .background(backgroundColor, RoundedCornerShape(cornerRadius))
                .border(borderWidth, borderColor, RoundedCornerShape(cornerRadius))
                .padding(contentPadding)
        ) {
            content()
        }
    }
}

@Composable
fun NeoButton(
    onClick: () -> Unit,
    modifier: Modifier = Modifier,
    backgroundColor: Color = NeoColors.PrimaryPink,
    textColor: Color = Color.White,
    borderColor: Color = NeoColors.BorderDark,
    borderWidth: Dp = 4.dp,
    shadowColor: Color = NeoColors.BorderDark,
    shadowOffsetMax: Dp = 4.dp,
    cornerRadius: Dp = 12.dp,
    testTag: String = "",
    content: @Composable RowScope.() -> Unit
) {
    val interactionSource = remember { MutableInteractionSource() }
    val isPressed by interactionSource.collectIsPressedAsState()

    // Interactive translation and shadow depression like Neobrutalist Web active states
    val shadowOffset by animateDpAsState(targetValue = if (isPressed) 0.dp else shadowOffsetMax, label = "ShadowOffset")
    val translateOffset by animateDpAsState(targetValue = if (isPressed) shadowOffsetMax else 0.dp, label = "TranslateOffset")

    val scale by animateFloatAsState(
        targetValue = if (isPressed) 1.05f else 1.0f,
        animationSpec = spring(
            dampingRatio = Spring.DampingRatioMediumBouncy,
            stiffness = Spring.StiffnessMedium
        ),
        label = "ButtonScale"
    )

    Box(
        modifier = modifier
            .testTag(testTag)
            .graphicsLayer {
                scaleX = scale
                scaleY = scale
            }
            .clickable(
                interactionSource = interactionSource,
                indication = null, // No standard ripple, tactile neobrutalist offset behaves as feedback
                onClick = onClick
            )
            .padding(bottom = shadowOffsetMax, end = shadowOffsetMax)
    ) {
        // Shadow Layer (Behind)
        if (shadowOffset > 0.dp) {
            Box(
                modifier = Modifier
                    .matchParentSize()
                    .offset(x = shadowOffset, y = shadowOffset)
                    .background(shadowColor, RoundedCornerShape(cornerRadius))
            )
        }
        // Button Face Layer (Translates when pressed)
        Row(
            modifier = Modifier
                .offset(x = translateOffset, y = translateOffset)
                .background(backgroundColor, RoundedCornerShape(cornerRadius))
                .border(borderWidth, borderColor, RoundedCornerShape(cornerRadius))
                .padding(horizontal = 16.dp, vertical = 12.dp),
            horizontalArrangement = Arrangement.Center,
            verticalAlignment = Alignment.CenterVertically
        ) {
            CompositionLocalProvider(LocalContentColor provides textColor) {
                content()
            }
        }
    }
}

@Composable
fun NeoTextField(
    value: String,
    onValueChange: (String) -> Unit,
    placeholder: String,
    modifier: Modifier = Modifier,
    keyboardOptions: KeyboardOptions = KeyboardOptions.Default,
    singleLine: Boolean = true,
    shadowColor: Color = NeoColors.BorderDark,
    cornerRadius: Dp = 0.dp, // Default sharp Neobrutalist input
    leadingIcon: @Composable (() -> Unit)? = null
) {
    Box(
        modifier = modifier
            .padding(bottom = 4.dp, end = 4.dp)
            .background(Color.White, RoundedCornerShape(cornerRadius))
            .border(4.dp, NeoColors.BorderDark, RoundedCornerShape(cornerRadius))
    ) {
        Row(
            modifier = Modifier.fillMaxWidth(),
            verticalAlignment = Alignment.CenterVertically
        ) {
            if (leadingIcon != null) {
                Box(modifier = Modifier.padding(start = 12.dp)) {
                    leadingIcon()
                }
            }
            TextField(
                value = value,
                onValueChange = onValueChange,
                placeholder = {
                    Text(
                        text = placeholder,
                        fontFamily = FontFamily.SansSerif,
                        fontWeight = FontWeight.Bold,
                        color = NeoColors.BorderDark.copy(alpha = 0.5f)
                    )
                },
                modifier = Modifier.fillMaxWidth(),
                colors = TextFieldDefaults.colors(
                    focusedContainerColor = Color.Transparent,
                    unfocusedContainerColor = Color.Transparent,
                    focusedIndicatorColor = Color.Transparent,
                    unfocusedIndicatorColor = Color.Transparent,
                    disabledContainerColor = Color.Transparent,
                    focusedTextColor = NeoColors.BorderDark,
                    unfocusedTextColor = NeoColors.BorderDark
                ),
                keyboardOptions = keyboardOptions,
                singleLine = singleLine,
                textStyle = TextStyle(
                    fontFamily = FontFamily.SansSerif,
                    fontWeight = FontWeight.Bold,
                    fontSize = 16.sp,
                    color = NeoColors.BorderDark
                )
            )
        }
    }
}

@Composable
fun NeoCheckbox(
    checked: Boolean,
    onCheckedChange: (Boolean) -> Unit,
    modifier: Modifier = Modifier
) {
    val interactionSource = remember { MutableInteractionSource() }
    val isPressed by interactionSource.collectIsPressedAsState()

    val shadowOffset = if (checked || isPressed) 0.dp else 4.dp
    val translateOffset = if (checked || isPressed) 4.dp else 0.dp

    Box(
        modifier = modifier
            .clickable(
                interactionSource = interactionSource,
                indication = null,
                onClick = { onCheckedChange(!checked) }
            )
            .padding(bottom = 4.dp, end = 4.dp)
    ) {
        // Shadow (drawn only if not active/checked)
        if (shadowOffset > 0.dp) {
            Box(
                modifier = Modifier
                    .size(32.dp)
                    .offset(x = shadowOffset, y = shadowOffset)
                    .background(NeoColors.BorderDark, RoundedCornerShape(4.dp))
            )
        }
        // Checkbox frame
        Box(
            modifier = Modifier
                .offset(x = translateOffset, y = translateOffset)
                .size(32.dp)
                .background(
                    if (checked) NeoColors.AccentYellow else Color.White,
                    RoundedCornerShape(4.dp)
                )
                .border(4.dp, NeoColors.BorderDark, RoundedCornerShape(4.dp)),
            contentAlignment = Alignment.Center
        ) {
            if (checked) {
                Icon(
                    imageVector = Icons.Default.Check,
                    contentDescription = "Checked",
                    tint = NeoColors.BorderDark,
                    modifier = Modifier.size(20.dp)
                )
            }
        }
    }
}
