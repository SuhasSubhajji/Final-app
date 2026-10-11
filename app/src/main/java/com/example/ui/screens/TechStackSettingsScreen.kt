package com.example.ui.screens

import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.example.data.repository.TechStackRepository
import kotlinx.coroutines.launch

@Composable
fun TechStackSettingsScreen(repository: TechStackRepository) {
    val scope = rememberCoroutineScope()
    val techStack by repository.techStackFlow.collectAsState(initial = emptySet())
    var text by remember { mutableStateOf(techStack.joinToString(", ")) }

    Column(modifier = Modifier.padding(16.dp)) {
        Text("Relevant Tech Stack (comma separated)", style = MaterialTheme.typography.titleMedium)
        OutlinedTextField(
            value = text,
            onValueChange = { text = it },
            modifier = Modifier.fillMaxWidth()
        )
        Button(onClick = {
            scope.launch {
                repository.updateTechStack(text.split(",").map { it.trim() }.filter { it.isNotEmpty() }.toSet())
            }
        }) {
            Text("Save")
        }
    }
}
