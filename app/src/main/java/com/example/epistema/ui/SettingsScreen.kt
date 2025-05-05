import android.app.Application
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.epistema.EpistemaApp
import com.example.epistema.localization.StringResources
import com.example.epistema.viewmodels.GlobalStateViewModel

@Composable
fun SettingsScreen(
    modifier: Modifier = Modifier,
    viewModel: GlobalStateViewModel = (LocalContext.current.applicationContext as EpistemaApp).globalStateViewModel) {
    val theme by viewModel.appTheme.collectAsState()
    val fontSize by viewModel.fontSize.collectAsState()
    val language by viewModel.appLanguage.collectAsState()

    LazyColumn(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp),
        verticalArrangement = Arrangement.Top
    ) {
        item {
            Text(StringResources.getString("settings_title"), style = MaterialTheme.typography.headlineLarge)
            Divider()
        }

        item {
            Text(StringResources.getString("language"), style = MaterialTheme.typography.bodyLarge)
            DropdownSelector(
                options = listOf("English", "Hindi", "Spanish", "French"),
                selectedOption = language,
                onOptionSelected = { viewModel.setAppLanguage(it) }
            )
            Divider()
        }

        item {
            Text(StringResources.getString("theme"), style = MaterialTheme.typography.bodyLarge)
            DropdownSelector(
                options = listOf("Light", "Dark"),
                selectedOption = theme,
                onOptionSelected = { viewModel.setAppTheme(it) }
            )
            Divider()
        }

        item {
            Text(StringResources.getString("font_size"), style = MaterialTheme.typography.bodyLarge)
            DropdownSelector(
                options = listOf("Small", "Medium", "Large"),
                selectedOption = fontSize,
                onOptionSelected = { viewModel.setFontSize(it) }
            )
            Divider()
        }
    }
}

@Composable
fun DropdownSelector(
    options: List<String>,
    selectedOption: String,
    onOptionSelected: (String) -> Unit
) {
    var expanded by remember { mutableStateOf(false) }

    Box(modifier = Modifier.fillMaxWidth()) {
        Button(onClick = { expanded = true }, modifier = Modifier.fillMaxWidth()) {
            Text(selectedOption)
        }
        DropdownMenu(
            expanded = expanded,
            onDismissRequest = { expanded = false }
        ) {
            options.forEach { option ->
                DropdownMenuItem(
                    text = { Text(option) },
                    onClick = {
                        onOptionSelected(option)
                        expanded = false
                    }
                )
            }
        }
    }
}

@Composable
fun SettingToggle(title: String, isChecked: Boolean, onCheckedChange: (Boolean) -> Unit) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(8.dp),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.SpaceBetween
    ) {
        Text(text = title, style = MaterialTheme.typography.bodyLarge)
        Switch(checked = isChecked, onCheckedChange = onCheckedChange)
    }
}

@Preview(showBackground = true)
@Composable
fun PreviewSettingsScreen() {
    SettingsScreen()
}
