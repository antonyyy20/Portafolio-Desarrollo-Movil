package com.example.frontend.ui.splash

import androidx.compose.animation.core.FastOutSlowInEasing
import androidx.compose.animation.core.LinearEasing
import androidx.compose.animation.core.RepeatMode
import androidx.compose.animation.core.animateFloat
import androidx.compose.animation.core.infiniteRepeatable
import androidx.compose.animation.core.rememberInfiniteTransition
import androidx.compose.animation.core.tween
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.BoxWithConstraints
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.heightIn
import androidx.compose.foundation.layout.navigationBarsPadding
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.statusBarsPadding
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Celebration
import androidx.compose.material.icons.filled.ConfirmationNumber
import androidx.compose.material.icons.filled.Language
import androidx.compose.material.icons.filled.LocationOn
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.blur
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.clipToBounds
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Path
import androidx.compose.ui.graphics.drawscope.Fill
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.frontend.R
import com.example.frontend.ui.components.CoinbasePrimaryButton
import com.example.frontend.ui.theme.CoinbaseAccentYellow
import com.example.frontend.ui.theme.CoinbaseCanvas
import com.example.frontend.ui.theme.CoinbaseInk
import com.example.frontend.ui.theme.CoinbasePrimary
import com.example.frontend.ui.theme.CoinbasePrimaryActive
import com.example.frontend.ui.theme.CoinbaseSpacing
import kotlin.math.cos
import kotlin.math.sin

private val WelcomePink = Color(0xFFFF8AD4)
private val WelcomePeach = Color(0xFFFFB36B)
private val WelcomeSky = Color(0xFF8EC5FF)
private val WelcomeLilac = Color(0xFFB57CFF)

@Composable
fun WelcomeScreen(
    onGetStarted: () -> Unit,
    modifier: Modifier = Modifier
) {
    BoxWithConstraints(
        modifier = modifier
            .fillMaxSize()
            .background(CoinbaseCanvas)
    ) {
        val introScale = minOf(
            (maxWidth.value - 32f) / 360f,
            (maxHeight.value - 340f) / 520f,
            1f
        ).coerceIn(0.62f, 1f)

        WelcomePastelBackground(scale = introScale)

        Column(
            modifier = Modifier
                .fillMaxSize()
                .clipToBounds()
                .statusBarsPadding()
                .navigationBarsPadding(),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Spacer(Modifier.height(CoinbaseSpacing.md))

            StaggeredIntroItem(delayMillis = 0) {
                QuickvntOrbitalHero(
                    modifier = Modifier
                        .fillMaxWidth()
                        .heightIn(max = (240 * introScale).dp)
                )
            }

            Spacer(Modifier.height(CoinbaseSpacing.md))

            StaggeredIntroItem(delayMillis = 200) {
                WelcomeBrandTitle(scale = introScale)
            }

            Spacer(Modifier.height(CoinbaseSpacing.xs))

            StaggeredIntroItem(delayMillis = 340) {
                Text(
                    text = "Eventos increíbles",
                    style = MaterialTheme.typography.headlineLarge.copy(
                        fontSize = (30 * introScale).sp,
                        fontWeight = FontWeight.Bold,
                        letterSpacing = (-0.8).sp
                    ),
                    color = CoinbaseInk,
                    textAlign = TextAlign.Center
                )
            }

            StaggeredIntroItem(delayMillis = 440) {
                Text(
                    text = "Comienza aquí",
                    style = TextStyle(
                        brush = Brush.linearGradient(
                            colors = listOf(
                                CoinbasePrimary,
                                CoinbasePrimaryActive,
                                CoinbaseAccentYellow
                            )
                        ),
                        fontSize = (32 * introScale).sp,
                        fontWeight = FontWeight.Bold,
                        letterSpacing = (-0.6).sp,
                        lineHeight = (36 * introScale).sp
                    ),
                    textAlign = TextAlign.Center
                )
            }

            Spacer(Modifier.weight(1f))

            StaggeredIntroItem(
                delayMillis = 620,
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = CoinbaseSpacing.lg)
            ) {
                CoinbasePrimaryButton(
                    text = "Comenzar",
                    onClick = onGetStarted,
                    large = true,
                    modifier = Modifier.padding(bottom = CoinbaseSpacing.lg)
                )
            }
        }
    }
}

@Composable
private fun WelcomePastelBackground(scale: Float = 1f) {
    Box(modifier = Modifier.fillMaxSize()) {
        Box(
            modifier = Modifier
                .size((240 * scale).dp)
                .offset(x = (-50 * scale).dp, y = (30 * scale).dp)
                .blur((60 * scale).dp)
                .background(
                    Brush.radialGradient(
                        colors = listOf(
                            WelcomeSky.copy(alpha = 0.55f),
                            Color.Transparent
                        )
                    ),
                    CircleShape
                )
        )
        Box(
            modifier = Modifier
                .size((220 * scale).dp)
                .align(Alignment.TopCenter)
                .offset(y = (90 * scale).dp)
                .blur((55 * scale).dp)
                .background(
                    Brush.radialGradient(
                        colors = listOf(
                            WelcomePink.copy(alpha = 0.42f),
                            Color.Transparent
                        )
                    ),
                    CircleShape
                )
        )
        Box(
            modifier = Modifier
                .size((230 * scale).dp)
                .align(Alignment.TopEnd)
                .offset(x = (28 * scale).dp, y = (130 * scale).dp)
                .blur((60 * scale).dp)
                .background(
                    Brush.radialGradient(
                        colors = listOf(
                            CoinbaseAccentYellow.copy(alpha = 0.38f),
                            Color.Transparent
                        )
                    ),
                    CircleShape
                )
        )
        Box(
            modifier = Modifier
                .size((200 * scale).dp)
                .align(Alignment.CenterStart)
                .offset(x = (-22 * scale).dp, y = (60 * scale).dp)
                .blur((48 * scale).dp)
                .background(
                    Brush.radialGradient(
                        colors = listOf(
                            CoinbasePrimary.copy(alpha = 0.18f),
                            Color.Transparent
                        )
                    ),
                    CircleShape
                )
        )
    }
}

@Composable
private fun WelcomeBrandTitle(scale: Float = 1f) {
    Row(
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.Center
    ) {
        Text(
            text = "Quickvnt",
            style = MaterialTheme.typography.displaySmall.copy(
                fontSize = (40 * scale).sp,
                fontWeight = FontWeight.Bold,
                letterSpacing = (-1.2).sp,
                lineHeight = (44 * scale).sp
            ),
            color = CoinbaseInk
        )
        Spacer(Modifier.size(CoinbaseSpacing.sm))
        Image(
            painter = painterResource(R.drawable.ic_splash_logo),
            contentDescription = null,
            modifier = Modifier.size((36 * scale).dp),
            contentScale = ContentScale.Fit
        )
    }
}

@Composable
internal fun QuickvntSparkLogo(modifier: Modifier = Modifier) {
    Image(
        painter = painterResource(R.drawable.ic_splash_logo),
        contentDescription = "Quickvnt",
        modifier = modifier,
        contentScale = ContentScale.Fit
    )
}

@Composable
internal fun QuickvntOrbitalHero(modifier: Modifier = Modifier) {
    val infiniteTransition = rememberInfiniteTransition(label = "orbitalHero")

    val outerOrbit by infiniteTransition.animateFloat(
        initialValue = 0f,
        targetValue = 360f,
        animationSpec = infiniteRepeatable(
            animation = tween(32000, easing = LinearEasing),
            repeatMode = RepeatMode.Restart
        ),
        label = "outerOrbit"
    )
    val innerOrbit by infiniteTransition.animateFloat(
        initialValue = 360f,
        targetValue = 0f,
        animationSpec = infiniteRepeatable(
            animation = tween(24000, easing = LinearEasing),
            repeatMode = RepeatMode.Restart
        ),
        label = "innerOrbit"
    )
    val ringAlpha by infiniteTransition.animateFloat(
        initialValue = 0.55f,
        targetValue = 0.95f,
        animationSpec = infiniteRepeatable(
            animation = tween(2000, easing = FastOutSlowInEasing),
            repeatMode = RepeatMode.Reverse
        ),
        label = "ringAlpha"
    )
    val centerPulse by infiniteTransition.animateFloat(
        initialValue = 0.94f,
        targetValue = 1.06f,
        animationSpec = infiniteRepeatable(
            animation = tween(1500, easing = FastOutSlowInEasing),
            repeatMode = RepeatMode.Reverse
        ),
        label = "centerPulse"
    )
    val starRotation by infiniteTransition.animateFloat(
        initialValue = -6f,
        targetValue = 6f,
        animationSpec = infiniteRepeatable(
            animation = tween(2200, easing = FastOutSlowInEasing),
            repeatMode = RepeatMode.Reverse
        ),
        label = "starRotation"
    )

    BoxWithConstraints(
        modifier = modifier.fillMaxWidth(),
        contentAlignment = Alignment.Center
    ) {
        val designDiameter = 280f
        val widthBudget = (maxWidth.value - 24f).coerceAtLeast(180f)
        val heightBudget = if (maxHeight != Dp.Infinity) {
            maxHeight.value.coerceAtLeast(160f)
        } else {
            designDiameter
        }
        val heroScale = minOf(
            widthBudget / designDiameter,
            heightBudget / designDiameter,
            1f
        ).coerceIn(0.58f, 1f)

        Box(
            modifier = Modifier
                .size(designDiameter.dp)
                .graphicsLayer {
                    scaleX = heroScale
                    scaleY = heroScale
                },
            contentAlignment = Alignment.Center
        ) {
        Canvas(modifier = Modifier.size(280.dp)) {
            val stroke = Stroke(width = 1.2f)
            listOf(0.30f, 0.46f, 0.62f, 0.78f).forEachIndexed { index, ratio ->
                val radius = size.minDimension * ratio * 0.5f
                drawCircle(
                    color = Color.White.copy(alpha = ringAlpha * (0.82f + index * 0.04f)),
                    radius = radius,
                    center = center,
                    style = stroke
                )
            }
        }

        Box(
            modifier = Modifier
                .size(280.dp)
                .graphicsLayer { rotationZ = outerOrbit },
            contentAlignment = Alignment.Center
        ) {
            OrbitalCalendarChip(
                counterRotation = -outerOrbit,
                modifier = Modifier.orbitOffset(angleDeg = 270f, radius = 125.dp)
            )
            OrbitalTicketChip(
                counterRotation = -outerOrbit,
                modifier = Modifier.orbitOffset(angleDeg = 200f, radius = 120.dp)
            )
            OrbitalLocationChip(
                counterRotation = -outerOrbit,
                modifier = Modifier.orbitOffset(angleDeg = 330f, radius = 117.dp)
            )
            OrbitalPartyChip(
                counterRotation = -outerOrbit,
                modifier = Modifier.orbitOffset(angleDeg = 55f, radius = 127.dp)
            )
            OrbitalGlobeChip(
                counterRotation = -outerOrbit,
                modifier = Modifier.orbitOffset(angleDeg = 125f, radius = 123.dp)
            )
        }

        Box(
            modifier = Modifier
                .size(212.dp)
                .graphicsLayer { rotationZ = innerOrbit },
            contentAlignment = Alignment.Center
        ) {
            AvatarBubble(
                emoji = "😉",
                counterRotation = -innerOrbit,
                modifier = Modifier.orbitOffset(angleDeg = 310f, radius = 91.dp)
            )
            AvatarBubble(
                emoji = "🥳",
                counterRotation = -innerOrbit,
                modifier = Modifier.orbitOffset(angleDeg = 20f, radius = 86.dp)
            )
            AvatarBubble(
                emoji = "😊",
                counterRotation = -innerOrbit,
                modifier = Modifier.orbitOffset(angleDeg = 145f, radius = 93.dp)
            )
            AvatarBubble(
                emoji = "🤩",
                counterRotation = -innerOrbit,
                modifier = Modifier.orbitOffset(angleDeg = 230f, radius = 88.dp)
            )
        }

        Box(
            modifier = Modifier
                .size(92.dp)
                .graphicsLayer {
                    scaleX = centerPulse
                    scaleY = centerPulse
                }
                .blur(22.dp)
                .background(
                    Brush.radialGradient(
                        colors = listOf(
                            CoinbaseAccentYellow.copy(alpha = 0.55f),
                            WelcomePink.copy(alpha = 0.45f),
                            WelcomeLilac.copy(alpha = 0.2f),
                            Color.Transparent
                        )
                    ),
                    CircleShape
                )
        )

        Box(
            modifier = Modifier
                .size(60.dp)
                .graphicsLayer {
                    scaleX = centerPulse
                    scaleY = centerPulse
                    rotationZ = starRotation
                },
            contentAlignment = Alignment.Center
        ) {
            GradientSparkle(modifier = Modifier.size(44.dp))
        }
        }
    }
}

private fun Modifier.orbitOffset(angleDeg: Float, radius: Dp): Modifier {
    val rad = Math.toRadians(angleDeg.toDouble())
    val x = (cos(rad) * radius.value).dp
    val y = (sin(rad) * radius.value).dp
    return this.offset(x = x, y = y)
}

@Composable
private fun GradientSparkle(modifier: Modifier = Modifier) {
    Canvas(modifier = modifier) {
        val cx = size.width / 2f
        val cy = size.height / 2f
        val outer = size.minDimension * 0.46f
        val inner = outer * 0.28f

        val path = Path().apply {
            moveTo(cx, cy - outer)
            lineTo(cx + inner, cy - inner)
            lineTo(cx + outer, cy)
            lineTo(cx + inner, cy + inner)
            lineTo(cx, cy + outer)
            lineTo(cx - inner, cy + inner)
            lineTo(cx - outer, cy)
            lineTo(cx - inner, cy - inner)
            close()
        }

        drawPath(
            path = path,
            brush = Brush.linearGradient(
                colors = listOf(
                    CoinbaseAccentYellow,
                    WelcomePeach,
                    WelcomePink,
                    WelcomeLilac,
                    CoinbasePrimary
                ),
                start = Offset(0f, 0f),
                end = Offset(size.width, size.height)
            ),
            style = Fill
        )
    }
}

@Composable
private fun AvatarBubble(
    emoji: String,
    counterRotation: Float,
    modifier: Modifier = Modifier
) {
    FloatingIntroItem(phaseOffset = emoji.hashCode() and 7, modifier = modifier) {
        Box(
            modifier = Modifier
                .graphicsLayer { rotationZ = counterRotation }
                .size(48.dp)
                .shadow(8.dp, CircleShape, spotColor = Color.Black.copy(alpha = 0.08f))
                .clip(CircleShape)
                .background(Color.White),
            contentAlignment = Alignment.Center
        ) {
            Text(text = emoji, fontSize = 22.sp)
        }
    }
}

@Composable
private fun OrbitalCalendarChip(counterRotation: Float, modifier: Modifier = Modifier) {
    FloatingIntroItem(phaseOffset = 0, modifier = modifier) {
        Box(
            modifier = Modifier
                .graphicsLayer { rotationZ = counterRotation }
                .size(50.dp)
                .shadow(10.dp, RoundedCornerShape(16.dp), spotColor = Color.Black.copy(alpha = 0.1f))
                .clip(RoundedCornerShape(16.dp))
                .background(Brush.linearGradient(listOf(Color(0xFF5B8CFF), Color(0xFF3D6FE8)))),
            contentAlignment = Alignment.Center
        ) {
            Column(horizontalAlignment = Alignment.CenterHorizontally) {
                Text(
                    text = "21",
                    color = Color.White,
                    fontSize = 18.sp,
                    fontWeight = FontWeight.Bold,
                    lineHeight = 18.sp
                )
            }
        }
    }
}

@Composable
private fun OrbitalTicketChip(counterRotation: Float, modifier: Modifier = Modifier) {
    FloatingIntroItem(phaseOffset = 1, modifier = modifier) {
        FeatureChip(
            counterRotation = counterRotation,
            gradient = Brush.linearGradient(listOf(Color(0xFF6FA8FF), Color(0xFF4E86F0))),
            icon = Icons.Default.ConfirmationNumber,
            iconTint = Color.White
        )
    }
}

@Composable
private fun OrbitalLocationChip(counterRotation: Float, modifier: Modifier = Modifier) {
    FloatingIntroItem(phaseOffset = 2, modifier = modifier) {
        FeatureChip(
            counterRotation = counterRotation,
            gradient = Brush.linearGradient(listOf(WelcomeLilac, WelcomePink)),
            icon = Icons.Default.LocationOn,
            iconTint = Color.White
        )
    }
}

@Composable
private fun OrbitalPartyChip(counterRotation: Float, modifier: Modifier = Modifier) {
    FloatingIntroItem(phaseOffset = 3, modifier = modifier) {
        FeatureChip(
            counterRotation = counterRotation,
            gradient = Brush.linearGradient(listOf(WelcomePink, WelcomePeach)),
            icon = Icons.Default.Celebration,
            iconTint = Color.White
        )
    }
}

@Composable
private fun OrbitalGlobeChip(counterRotation: Float, modifier: Modifier = Modifier) {
    FloatingIntroItem(phaseOffset = 4, modifier = modifier) {
        FeatureChip(
            counterRotation = counterRotation,
            gradient = Brush.linearGradient(listOf(WelcomeLilac, CoinbasePrimaryActive)),
            icon = Icons.Default.Language,
            iconTint = Color.White
        )
    }
}

@Composable
private fun FeatureChip(
    counterRotation: Float,
    gradient: Brush,
    icon: ImageVector,
    iconTint: Color
) {
    Box(
        modifier = Modifier
            .graphicsLayer { rotationZ = counterRotation }
            .size(50.dp)
            .shadow(10.dp, RoundedCornerShape(16.dp), spotColor = Color.Black.copy(alpha = 0.1f))
            .clip(RoundedCornerShape(16.dp))
            .background(gradient),
        contentAlignment = Alignment.Center
    ) {
        Icon(
            imageVector = icon,
            contentDescription = null,
            tint = iconTint,
            modifier = Modifier.size(24.dp)
        )
    }
}

@Composable
private fun FloatingIntroItem(
    phaseOffset: Int,
    modifier: Modifier = Modifier,
    content: @Composable () -> Unit
) {
    val infiniteTransition = rememberInfiniteTransition(label = "float-$phaseOffset")
    val floatOffset by infiniteTransition.animateFloat(
        initialValue = -6f,
        targetValue = 6f,
        animationSpec = infiniteRepeatable(
            animation = tween(1500 + phaseOffset * 160, easing = FastOutSlowInEasing),
            repeatMode = RepeatMode.Reverse
        ),
        label = "floatOffset"
    )

    Box(
        modifier = modifier.offset(y = floatOffset.dp),
        contentAlignment = Alignment.Center
    ) {
        content()
    }
}
