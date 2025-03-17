import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp

@Composable
fun SettingsScreen(modifier: Modifier = Modifier) {
    var selectedLanguage by remember { mutableStateOf("English") }
    var selectedTheme by remember { mutableStateOf("Light") }
    var selectedFontSize by remember { mutableStateOf("Medium") }
    var notificationsEnabled by remember { mutableStateOf(true) }
    var darkModeEnabled by remember { mutableStateOf(false) }
    var dataSavingEnabled by remember { mutableStateOf(false) }
    var privateAccount by remember { mutableStateOf(false) }

    LazyColumn(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp),
        verticalArrangement = Arrangement.Top
    ) {
        item {
            Text(
                text = "Settings",
                style = MaterialTheme.typography.headlineLarge,
                modifier = Modifier.padding(bottom = 16.dp)
            )
            Divider()
        }

        item { Spacer(modifier = Modifier.height(16.dp)) }

        item {
            Text(text = "Language", style = MaterialTheme.typography.bodyLarge)
            DropdownSelector(
                options = listOf("English", "Hindi", "Spanish", "French"),
                selectedOption = selectedLanguage,
                onOptionSelected = { selectedLanguage = it }
            )
        }

        item { Spacer(modifier = Modifier.height(16.dp)); Divider() }

        item {
            Text(text = "Theme", style = MaterialTheme.typography.bodyLarge)
            DropdownSelector(
                options = listOf("Light", "Dark", "System Default"),
                selectedOption = selectedTheme,
                onOptionSelected = { selectedTheme = it }
            )
        }

        item { Spacer(modifier = Modifier.height(16.dp)); Divider() }

        item {
            Text(text = "Font Size", style = MaterialTheme.typography.bodyLarge)
            DropdownSelector(
                options = listOf("Small", "Medium", "Large"),
                selectedOption = selectedFontSize,
                onOptionSelected = { selectedFontSize = it }
            )
        }

        item { Spacer(modifier = Modifier.height(16.dp)); Divider() }

        items(
            listOf(
                "Enable Notifications" to notificationsEnabled,
                "Enable Dark Mode" to darkModeEnabled,
                "Enable Data Saving Mode" to dataSavingEnabled,
                "Set Account to Private" to privateAccount
            )
        ) { setting ->
            SettingToggle(
                title = setting.first,
                isChecked = setting.second,
                onCheckedChange = { isChecked ->
                    when (setting.first) {
                        "Enable Notifications" -> notificationsEnabled = isChecked
                        "Enable Dark Mode" -> darkModeEnabled = isChecked
                        "Enable Data Saving Mode" -> dataSavingEnabled = isChecked
                        "Set Account to Private" -> privateAccount = isChecked
                    }
                }
            )
        }

        item { Spacer(modifier = Modifier.height(24.dp)); Divider() }

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
