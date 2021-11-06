import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.material.MaterialTheme
import androidx.compose.material.darkColors
import androidx.compose.material.lightColors
import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.Color

private val DarkColorPalette = darkColors(
    primary = StoryblokGreen,
    primaryVariant = StoryblokGreenDark,
    onPrimary = Color.White,
    secondary = StoryblokGreen,
    onSecondary = Color.White,
    error = Red200
)

private val LightColorPalette = lightColors(
    primary = StoryblokGreen,
    primaryVariant = StoryblokGreenDark,
    onPrimary = Color.White,
    secondary = StoryblokGreen,
    secondaryVariant = StoryblokGreenDark,
    onSecondary = Color.White,
    error = Red800
)

@Composable
fun SampleTheme(
    darkTheme: Boolean = isSystemInDarkTheme(),
    content: @Composable() () -> Unit
) {
    val colors = if (darkTheme) {
        DarkColorPalette
    } else {
        LightColorPalette
    }

    MaterialTheme(
        colors = colors,
        content = content
    )
}
