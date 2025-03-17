import androidx.compose.foundation.layout.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController

@Composable
fun SettingsScreen(navController: NavController? = null) {
    var selectedLanguage by remember { mutableStateOf("English") }
    var selectedTheme by remember { mutableStateOf("Light") }
    var selectedFontSize by remember { mutableStateOf("Medium") }
    var notificationsEnabled by remember { mutableStateOf(true) }
    var darkModeEnabled by remember { mutableStateOf(false) }
    var dataSavingEnabled by remember { mutableStateOf(false) }
    var privateAccount by remember { mutableStateOf(false) }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp),
        verticalArrangement = Arrangement.Top
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(bottom = 16.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            navController?.let {
                IconButton(onClick = { it.popBackStack() }) {
                    Icon(Icons.Filled.ArrowBack, contentDescription = "Back")
                }
            }
            Text(
                text = "Settings",
                style = MaterialTheme.typography.headlineLarge,
                modifier = Modifier.padding(start = 8.dp)
            )
        }

        Divider()

        Spacer(modifier = Modifier.height(16.dp))

        Text(text = "Language", style = MaterialTheme.typography.bodyLarge)
        DropdownSelector(
            options = listOf("English", "Hindi", "Spanish", "French"),
            selectedOption = selectedLanguage,
            onOptionSelected = { selectedLanguage = it }
        )

        Spacer(modifier = Modifier.height(24.dp))
        Divider()
        Spacer(modifier = Modifier.height(24.dp))

        Text(text = "Theme", style = MaterialTheme.typography.bodyLarge)
        DropdownSelector(
            options = listOf("Light", "Dark", "System Default"),
            selectedOption = selectedTheme,
            onOptionSelected = { selectedTheme = it }
        )

        Spacer(modifier = Modifier.height(24.dp))
        Divider()
        Spacer(modifier = Modifier.height(24.dp))

        Text(text = "Font Size", style = MaterialTheme.typography.bodyLarge)
        DropdownSelector(
            options = listOf("Small", "Medium", "Large"),
            selectedOption = selectedFontSize,
            onOptionSelected = { selectedFontSize = it }
        )

        Spacer(modifier = Modifier.height(24.dp))
        Divider()
        Spacer(modifier = Modifier.height(24.dp))

        SettingToggle(
            title = "Enable Notifications",
            isChecked = notificationsEnabled,
            onCheckedChange = { notificationsEnabled = it }
        )

        Spacer(modifier = Modifier.height(16.dp))

        SettingToggle(
            title = "Enable Dark Mode",
            isChecked = darkModeEnabled,
            onCheckedChange = { darkModeEnabled = it }
        )

        Spacer(modifier = Modifier.height(16.dp))

        SettingToggle(
            title = "Enable Data Saving Mode",
            isChecked = dataSavingEnabled,
            onCheckedChange = { dataSavingEnabled = it }
        )

        Spacer(modifier = Modifier.height(16.dp))

        SettingToggle(
            title = "Set Account to Private",
            isChecked = privateAccount,
            onCheckedChange = { privateAccount = it }
        )

        Spacer(modifier = Modifier.height(24.dp))
        Divider()
        Spacer(modifier = Modifier.height(24.dp))

        Button(
            onClick = { /* Implement Logout Functionality */ },
            colors = ButtonDefaults.buttonColors(MaterialTheme.colorScheme.error),
            modifier = Modifier.fillMaxWidth()
        ) {
            Text(text = "Logout", color = MaterialTheme.colorScheme.onError)
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