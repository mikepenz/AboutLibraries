import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.darkColorScheme
import androidx.compose.material3.lightColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.Color

private val DarkColorPalette = darkColorScheme(
    primary = StoryblokGreen,
    onPrimary = Color.White,
    secondary = StoryblokGreen,
    onSecondary = Color.White,
    error = Red200
)

private val LightColorPalette = lightColorScheme(
    primary = StoryblokGreen,
    onPrimary = Color.White,
    secondary = StoryblokGreen,
    onSecondary = Color.White,
    error = Red800
)

@Composable
fun SampleTheme(
    darkTheme: Boolean = isSystemInDarkTheme(),
    content: @Composable() () -> Unit,
) {
    val colors = if (darkTheme) {
        DarkColorPalette
    } else {
        LightColorPalette
    }

    MaterialTheme(
        colorScheme = colors,
        content = content
    )
}
