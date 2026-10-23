package org.astroalarm.ui.birth

import android.content.ClipData
import android.content.ClipboardManager
import android.content.Context
import android.widget.Toast
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Button
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Switch
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.semantics.contentDescription
import androidx.compose.ui.semantics.semantics
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import dev.foss.goldenpath.R
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import org.astroalarm.astro.birth.BirthProfile
import org.astroalarm.astro.birth.LocalLlmClient
import org.astroalarm.astro.birth.LocalLlmPrefs
import org.astroalarm.astro.birth.LocalLlmResult
import org.astroalarm.astro.birth.NatalAgentPrompt
import org.astroalarm.astro.birth.NatalChart

@Composable
fun BirthAgentSection(
    profile: BirthProfile?,
    chart: NatalChart?,
    onSaveInterpretation: (String) -> Unit,
    modifier: Modifier = Modifier,
) {
    val context = LocalContext.current
    val scope = rememberCoroutineScope()
    val llmPrefs = remember { LocalLlmPrefs(context) }
    val llmOn by llmPrefs.enabled.collectAsState()
    val llmBase by llmPrefs.baseUrl.collectAsState()
    var pasteText by remember(profile?.id, profile?.savedInterpretation) {
        mutableStateOf(profile?.savedInterpretation.orEmpty())
    }
    var llmBusy by remember { mutableStateOf(false) }
    val canCopy = NatalAgentPrompt.canCopy(profile, chart)
    val copyCd = stringResource(R.string.natal_copy_agent_cd)

    Column(modifier = modifier, verticalArrangement = Arrangement.spacedBy(8.dp)) {
        Text(stringResource(R.string.natal_agent_title), style = MaterialTheme.typography.titleMedium)
        Text(
            stringResource(R.string.natal_privacy_note),
            style = MaterialTheme.typography.bodySmall,
            fontSize = 12.sp,
        )
        Button(
            onClick = {
                val prompt = if (profile != null && chart != null) {
                    NatalAgentPrompt.build(profile, chart)
                } else {
                    null
                }
                if (prompt == null) {
                    Toast.makeText(context, R.string.natal_incomplete, Toast.LENGTH_SHORT).show()
                    return@Button
                }
                val ok = copyText(context, prompt)
                Toast.makeText(
                    context,
                    if (ok) R.string.natal_copy_ok else R.string.natal_copy_fail,
                    Toast.LENGTH_SHORT,
                ).show()
            },
            enabled = canCopy,
            modifier = Modifier
                .fillMaxWidth()
                .semantics { contentDescription = copyCd },
        ) {
            Text(stringResource(R.string.natal_copy_agent))
        }

        OutlinedTextField(
            value = pasteText,
            onValueChange = { pasteText = it },
            modifier = Modifier.fillMaxWidth(),
            label = { Text(stringResource(R.string.natal_paste_profile)) },
            placeholder = { Text(stringResource(R.string.natal_paste_hint)) },
            minLines = 3,
        )
        Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
            TextButton(
                onClick = {
                    onSaveInterpretation(pasteText)
                    Toast.makeText(context, R.string.natal_save_profile, Toast.LENGTH_SHORT).show()
                },
                enabled = profile != null,
            ) { Text(stringResource(R.string.natal_save_profile)) }
            TextButton(
                onClick = {
                    pasteText = ""
                    onSaveInterpretation("")
                },
                enabled = profile != null,
            ) { Text(stringResource(R.string.natal_clear_profile)) }
        }

        Row(verticalAlignment = Alignment.CenterVertically) {
            Text(stringResource(R.string.natal_local_llm_enable), modifier = Modifier.weight(1f))
            Switch(checked = llmOn, onCheckedChange = { llmPrefs.setEnabled(it) })
        }
        if (llmOn) {
            OutlinedTextField(
                value = llmBase,
                onValueChange = { llmPrefs.setBaseUrl(it) },
                modifier = Modifier.fillMaxWidth(),
                label = { Text(stringResource(R.string.natal_local_llm_url)) },
                singleLine = true,
            )
            Button(
                onClick = {
                    if (llmBusy || !canCopy || profile == null || chart == null) return@Button
                    val prompt = NatalAgentPrompt.build(profile, chart) ?: return@Button
                    llmBusy = true
                    scope.launch {
                        val result = withContext(Dispatchers.IO) {
                            LocalLlmClient().ask(llmBase, prompt)
                        }
                        llmBusy = false
                        when (result) {
                            is LocalLlmResult.Ok -> {
                                pasteText = result.text
                                onSaveInterpretation(result.text)
                                Toast.makeText(context, R.string.natal_local_llm_ok, Toast.LENGTH_SHORT).show()
                            }
                            is LocalLlmResult.Err -> {
                                val msg = when (result.reason) {
                                    "timeout" -> R.string.natal_local_llm_timeout
                                    else -> R.string.natal_local_llm_fail
                                }
                                Toast.makeText(context, msg, Toast.LENGTH_LONG).show()
                            }
                        }
                    }
                },
                enabled = canCopy && !llmBusy,
                modifier = Modifier.fillMaxWidth(),
            ) {
                Text(
                    if (llmBusy) stringResource(R.string.natal_local_llm_busy)
                    else stringResource(R.string.natal_local_llm),
                )
            }
        }

        if (profile?.savedInterpretation?.isNotBlank() == true) {
            Text(stringResource(R.string.natal_saved_profile), style = MaterialTheme.typography.titleSmall)
            Text(
                profile.savedInterpretation,
                style = MaterialTheme.typography.bodySmall,
                modifier = Modifier.padding(bottom = 8.dp),
            )
        }
    }
}

private fun copyText(context: Context, text: String): Boolean = runCatching {
    val cm = context.getSystemService(Context.CLIPBOARD_SERVICE) as ClipboardManager
    cm.setPrimaryClip(ClipData.newPlainText("natal_agent_prompt", text))
    true
}.getOrDefault(false)
