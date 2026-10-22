package kolt.crashlogs

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.text.selection.SelectionContainer
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp

/**
 * List → detail browser for [CrashLogs]. [onBack] is called from the list's back button
 * (omit to hide it). Plain Material3 so the module needs no other Kolt lib.
 */
@Composable
fun CrashLogsScreen(onBack: (() -> Unit)? = null) {
    var selected by remember { mutableStateOf<CrashEntry?>(null) }
    var query by remember { mutableStateOf("") }
    var items by remember { mutableStateOf(CrashLogs.all()) }

    selected?.let { entry ->
        CrashDetail(entry, onBack = { selected = null })
        return
    }

    Column(Modifier.fillMaxSize().padding(16.dp)) {
        Row(Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
            if (onBack != null) TextButton(onClick = onBack) { Text("Back") }
            TextButton(onClick = { CrashLogs.clear(); items = emptyList() }) { Text("Clear all") }
        }
        OutlinedTextField(
            value = query,
            onValueChange = { query = it; items = CrashLogs.search(it) },
            label = { Text("Search") },
            singleLine = true,
            modifier = Modifier.fillMaxWidth(),
        )
        if (items.isEmpty()) {
            Text("No crashes recorded", Modifier.padding(top = 16.dp), style = MaterialTheme.typography.bodyMedium)
        }
        LazyColumn {
            items(items, key = { it.id }) { item ->
                Column(
                    Modifier.fillMaxWidth().clickable { selected = CrashLogs.get(item.id) }.padding(vertical = 12.dp),
                ) {
                    Text(item.className.substringAfterLast('.'), style = MaterialTheme.typography.titleMedium)
                    Text(item.message ?: item.tag, maxLines = 2, overflow = TextOverflow.Ellipsis)
                    Text("${item.tag} · ${item.timeMillis}", style = MaterialTheme.typography.labelSmall)
                }
                HorizontalDivider()
            }
        }
    }
}

@Composable
private fun CrashDetail(entry: CrashEntry, onBack: () -> Unit) {
    Column(Modifier.fillMaxSize().padding(16.dp)) {
        TextButton(onClick = onBack) { Text("Back") }
        Text(entry.summary.className, style = MaterialTheme.typography.titleMedium)
        Text("${entry.summary.tag} · ${entry.summary.timeMillis}", style = MaterialTheme.typography.labelSmall)
        SelectionContainer(Modifier.verticalScroll(rememberScrollState())) {
            Text(entry.stackTrace, fontFamily = FontFamily.Monospace, style = MaterialTheme.typography.bodySmall)
        }
    }
}
