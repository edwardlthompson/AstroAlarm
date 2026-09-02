package org.astroalarm.ui.math

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import dev.foss.goldenpath.R
import org.astroalarm.math.MathDifficulty
import org.astroalarm.math.MathPreferences
import org.astroalarm.math.MathProblemGenerator

@OptIn(ExperimentalLayoutApi::class)
@Composable
fun MathSettingsSection(
    mathPrefs: MathPreferences,
    modifier: Modifier = Modifier
) {
    val difficulty by mathPrefs.difficulty.collectAsState()
    val problemCount by mathPrefs.problemCount.collectAsState()
    var previewProblem by remember(difficulty) {
        mutableStateOf(MathProblemGenerator.generate(difficulty))
    }
    var showAnswer by remember { mutableStateOf(false) }

    Card(
        modifier = modifier.fillMaxWidth(),
        shape = RoundedCornerShape(12.dp),
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.7f))
    ) {
        Column(
            modifier = Modifier.padding(14.dp),
            verticalArrangement = Arrangement.spacedBy(10.dp)
        ) {
            Text(
                text = stringResource(R.string.astro_math_settings_title),
                style = MaterialTheme.typography.titleMedium,
                fontWeight = FontWeight.Bold,
                color = MaterialTheme.colorScheme.primary
            )
            Text(
                text = stringResource(R.string.astro_math_settings_desc),
                style = MaterialTheme.typography.bodySmall,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )

            Text(
                text = stringResource(R.string.astro_math_difficulty_title),
                fontWeight = FontWeight.SemiBold,
                style = MaterialTheme.typography.bodyMedium
            )

            FlowRow(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(6.dp),
                verticalArrangement = Arrangement.spacedBy(6.dp)
            ) {
                MathDifficulty.entries.forEach { diff ->
                    val label = when (diff) {
                        MathDifficulty.ELEMENTARY -> stringResource(R.string.astro_math_diff_elementary)
                        MathDifficulty.EASY -> stringResource(R.string.astro_math_diff_easy)
                        MathDifficulty.MEDIUM -> stringResource(R.string.astro_math_diff_medium)
                        MathDifficulty.HARD -> stringResource(R.string.astro_math_diff_hard)
                    }
                    FilterChip(
                        selected = difficulty == diff,
                        onClick = {
                            mathPrefs.setDifficulty(diff)
                            showAnswer = false
                        },
                        label = { Text(label, fontSize = 12.sp) }
                    )
                }
            }

            Text(
                text = stringResource(R.string.astro_math_count_title),
                fontWeight = FontWeight.SemiBold,
                style = MaterialTheme.typography.bodyMedium
            )

            FlowRow(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(6.dp),
                verticalArrangement = Arrangement.spacedBy(6.dp)
            ) {
                listOf(1, 2, 3, 5).forEach { count ->
                    FilterChip(
                        selected = problemCount == count,
                        onClick = { mathPrefs.setProblemCount(count) },
                        label = { Text("$count", fontSize = 12.sp) }
                    )
                }
            }

            Card(
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(8.dp),
                colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface)
            ) {
                Column(
                    modifier = Modifier.padding(12.dp),
                    horizontalAlignment = Alignment.CenterHorizontally,
                    verticalArrangement = Arrangement.spacedBy(6.dp)
                ) {
                    Text(
                        text = stringResource(R.string.astro_math_preview_title),
                        style = MaterialTheme.typography.labelMedium,
                        color = MaterialTheme.colorScheme.primary
                    )
                    Text(
                        text = if (showAnswer) "${previewProblem.expression} = ${previewProblem.answer}" else "${previewProblem.expression} = ?",
                        fontSize = 20.sp,
                        fontWeight = FontWeight.Bold
                    )
                    Row(
                        horizontalArrangement = Arrangement.spacedBy(8.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        TextButton(
                            onClick = {
                                previewProblem = MathProblemGenerator.generate(difficulty)
                                showAnswer = false
                            }
                        ) {
                            Text(stringResource(R.string.astro_math_test_btn))
                        }
                        TextButton(
                            onClick = { showAnswer = !showAnswer }
                        ) {
                            Text(if (showAnswer) stringResource(R.string.astro_math_hide_answer) else stringResource(R.string.astro_math_show_answer))
                        }
                    }
                }
            }
        }
    }
}
