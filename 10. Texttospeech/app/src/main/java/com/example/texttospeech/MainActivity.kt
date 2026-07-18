package com.example.texttospeech

import android.os.Bundle
import android.speech.tts.TextToSpeech
import android.speech.tts.Voice
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Button
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.ExposedDropdownMenuAnchorType
import androidx.compose.material3.ExposedDropdownMenuBox
import androidx.compose.material3.ExposedDropdownMenuDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.example.texttospeech.ui.theme.TexttospeechTheme
import java.util.Locale

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            TexttospeechTheme {
                TextToSpeechScreen()
            }
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun TextToSpeechScreen() {
    val context = LocalContext.current
    var tts by remember { mutableStateOf<TextToSpeech?>(null) }
    var isTtsReady by remember { mutableStateOf(false) }
    var text by rememberSaveable { mutableStateOf("") }
    var selectedVoice by remember { mutableStateOf<Voice?>(null) }
    var voiceMenuExpanded by remember { mutableStateOf(false) }

    DisposableEffect(context) {
        lateinit var engine: TextToSpeech
        engine = TextToSpeech(context) { status ->
            if (status == TextToSpeech.SUCCESS) {
                tts = engine
                isTtsReady = true
            }
        }
        onDispose {
            engine.stop()
            engine.shutdown()
        }
    }

    val voices = remember(isTtsReady, tts) {
        tts?.voices
            ?.sortedWith(compareBy({ it.locale?.displayName ?: "" }, { it.name }))
            ?: emptyList()
    }

    LaunchedEffect(voices) {
        if (selectedVoice == null && voices.isNotEmpty()) {
            selectedVoice = voices.find { it.locale?.language == Locale.getDefault().language }
                ?: voices.first()
        }
    }

    Scaffold(modifier = Modifier.fillMaxSize()) { innerPadding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding)
                .padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(16.dp),
        ) {
            Text(
                text = stringResource(R.string.app_title),
                style = MaterialTheme.typography.headlineSmall,
            )

            Column(verticalArrangement = Arrangement.spacedBy(4.dp)) {
                Text(
                    text = stringResource(R.string.students_label),
                    style = MaterialTheme.typography.titleMedium,
                )
                Text(
                    text = stringResource(R.string.student_enrique_fong),
                    style = MaterialTheme.typography.bodyLarge,
                )
                Text(
                    text = stringResource(R.string.student_manuel_guillen),
                    style = MaterialTheme.typography.bodyLarge,
                )
            }

            OutlinedTextField(
                value = text,
                onValueChange = { text = it },
                modifier = Modifier
                    .fillMaxWidth()
                    .weight(1f),
                label = { Text(stringResource(R.string.text_hint)) },
                placeholder = { Text(stringResource(R.string.text_placeholder)) },
                minLines = 8,
                maxLines = 16,
            )

            ExposedDropdownMenuBox(
                expanded = voiceMenuExpanded,
                onExpandedChange = { voiceMenuExpanded = it },
                modifier = Modifier.fillMaxWidth(),
            ) {
                OutlinedTextField(
                    value = selectedVoice?.displayLabel()
                        ?: stringResource(R.string.voice_loading),
                    onValueChange = {},
                    readOnly = true,
                    enabled = isTtsReady && voices.isNotEmpty(),
                    label = { Text(stringResource(R.string.voice_label)) },
                    trailingIcon = {
                        ExposedDropdownMenuDefaults.TrailingIcon(expanded = voiceMenuExpanded)
                    },
                    modifier = Modifier
                        .menuAnchor(
                            type = ExposedDropdownMenuAnchorType.PrimaryNotEditable,
                            enabled = isTtsReady && voices.isNotEmpty(),
                        )
                        .fillMaxWidth(),
                )
                ExposedDropdownMenu(
                    expanded = voiceMenuExpanded,
                    onDismissRequest = { voiceMenuExpanded = false },
                ) {
                    voices.forEach { voice ->
                        DropdownMenuItem(
                            text = { Text(voice.displayLabel()) },
                            onClick = {
                                selectedVoice = voice
                                voiceMenuExpanded = false
                            },
                        )
                    }
                }
            }

            Button(
                onClick = {
                    val engine = tts ?: return@Button
                    val utterance = text.trim()
                    if (utterance.isEmpty()) return@Button

                    selectedVoice?.let { engine.voice = it }
                    engine.speak(utterance, TextToSpeech.QUEUE_FLUSH, null, "tts_utterance")
                },
                enabled = isTtsReady && text.isNotBlank(),
                modifier = Modifier.fillMaxWidth(),
            ) {
                Text(stringResource(R.string.play_button))
            }
        }
    }
}

private fun Voice.displayLabel(): String {
    val localeName = locale?.displayName ?: "Desconocido"
    val shortName = name.substringAfterLast("-", name)
    return "$localeName — $shortName"
}

@Preview(showBackground = true)
@Composable
fun TextToSpeechScreenPreview() {
    TexttospeechTheme {
        TextToSpeechScreen()
    }
}
