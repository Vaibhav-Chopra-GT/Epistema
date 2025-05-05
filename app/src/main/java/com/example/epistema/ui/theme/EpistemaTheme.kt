import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.darkColorScheme
import androidx.compose.material3.lightColorScheme
import androidx.compose.runtime.Composable

@Composable
fun EpistemaTheme(
    darkTheme: Boolean = isSystemInDarkTheme(),
    themeOverride: String? = null,
    fontSize: String = "Medium",
    content: @Composable () -> Unit
) {
    val currentTheme = when (themeOverride) {
        "Dark" -> true
        "Light" -> false
        else -> darkTheme
    }

    val fontSizeMultiplier = when (fontSize) {
        "Small" -> 0.9f
        "Large" -> 1.5f
        else -> 1.0f
    }

    val typography = MaterialTheme.typography.copy(
        bodyLarge = MaterialTheme.typography.bodyLarge.copy(
            fontSize = MaterialTheme.typography.bodyLarge.fontSize * fontSizeMultiplier,
            lineHeight = MaterialTheme.typography.bodyLarge.lineHeight * fontSizeMultiplier
        ),
        bodyMedium = MaterialTheme.typography.bodyMedium.copy(
            fontSize = MaterialTheme.typography.bodyMedium.fontSize * fontSizeMultiplier,
            lineHeight = MaterialTheme.typography.bodyMedium.lineHeight * fontSizeMultiplier
        ),
        // Add other text styles as needed
    )

    MaterialTheme(
        colorScheme = if (currentTheme) darkColorScheme() else lightColorScheme(),
        typography = typography,
        content = content
    )
}
