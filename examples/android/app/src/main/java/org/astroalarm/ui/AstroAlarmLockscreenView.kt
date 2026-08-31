package org.astroalarm.ui

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.window.Dialog
import dev.foss.goldenpath.R
import org.astroalarm.astro.model.AstroAlarm
import org.astroalarm.math.MathPreferences
import org.astroalarm.math.MathProblemGenerator
import java.time.LocalTime
import java.time.format.DateTimeFormatter

@Composable
fun AstroAlarmLockscreenView(
    alarm: AstroAlarm,
    onSnooze: () -> Unit,
    onStop: () -> Unit
) {
    val currentTime = remember {
        LocalTime.now().format(DateTimeFormatter.ofPattern("HH:mm"))
    }
    var showMathChallenge by remember { mutableStateOf(false) }

    Surface(
        modifier = Modifier.fillMaxSize(),
        color = MaterialTheme.colorScheme.background
    ) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(24.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.SpaceBetween
        ) {
            Spacer(modifier = Modifier.height(32.dp))

            Column(horizontalAlignment = Alignment.CenterHorizontally) {
                Text(
                    text = currentTime,
                    fontSize = 64.sp,
                    fontWeight = FontWeight.Bold,
                    color = MaterialTheme.colorScheme.onBackground
                )
                Spacer(modifier = Modifier.height(16.dp))
                Text(
                    text = alarm.label,
                    fontSize = 24.sp,
                    fontWeight = FontWeight.Medium,
                    textAlign = TextAlign.Center,
                    color = MaterialTheme.colorScheme.primary
                )
            }

            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(bottom = 32.dp),
                verticalArrangement = Arrangement.spacedBy(16.dp)
            ) {
                Button(
                    onClick = onSnooze,
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(56.dp),
                    colors = ButtonDefaults.buttonColors(
                        containerColor = MaterialTheme.colorScheme.secondaryContainer,
                        contentColor = MaterialTheme.colorScheme.onSecondaryContainer
                    ),
                    shape = RoundedCornerShape(16.dp)
                ) {
                    Text(
                        text = stringResource(R.string.astro_action_snooze, alarm.snoozeMinutes),
                        fontSize = 18.sp,
                        fontWeight = FontWeight.SemiBold
                    )
                }

                Button(
                    onClick = {
                        if (alarm.mathUnlockEnabled) {
                            showMathChallenge = true
                        } else {
                            onStop()
                        }
                    },
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(56.dp),
                    colors = ButtonDefaults.buttonColors(
                        containerColor = MaterialTheme.colorScheme.error,
                        contentColor = MaterialTheme.colorScheme.onError
                    ),
                    shape = RoundedCornerShape(16.dp)
                ) {
                    Text(
                        text = stringResource(R.string.astro_action_stop),
                        fontSize = 18.sp,
                        fontWeight = FontWeight.Bold
                    )
                }
            }
        }
    }

    if (showMathChallenge) {
        MathChallengeDialog(
            onDismiss = { showMathChallenge = false },
            onSolved = {
                showMathChallenge = false
                onStop()
            }
        )
    }
}

@Composable
private fun MathChallengeDialog(
    onDismiss: () -> Unit,
    onSolved: () -> Unit
) {
    val context = LocalContext.current
    val mathPrefs = remember { MathPreferences(context) }
    val difficulty = mathPrefs.getDifficulty()
    val totalProblems = mathPrefs.getProblemCount()

    var currentProblemIndex by remember { mutableIntStateOf(1) }
    var currentProblem by remember { mutableStateOf(MathProblemGenerator.generate(difficulty)) }
    var userAnswer by remember { mutableStateOf("") }
    var isError by remember { mutableStateOf(false) }

    fun checkAndAdvance() {
        if (userAnswer.trim().toIntOrNull() == currentProblem.answer) {
            if (currentProblemIndex >= totalProblems) {
                onSolved()
            } else {
                currentProblemIndex += 1
                currentProblem = MathProblemGenerator.generate(difficulty)
                userAnswer = ""
                isError = false
            }
        } else {
            isError = true
            userAnswer = ""
            currentProblem = MathProblemGenerator.generate(difficulty)
        }
    }

    Dialog(onDismissRequest = onDismiss) {
        Card(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp),
            shape = RoundedCornerShape(20.dp),
            colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface)
        ) {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(24.dp),
                horizontalAlignment = Alignment.CenterHorizontally,
                verticalArrangement = Arrangement.spacedBy(16.dp)
            ) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(
                        text = stringResource(R.string.astro_math_dialog_title),
                        style = MaterialTheme.typography.titleLarge,
                        fontWeight = FontWeight.Bold
                    )
                    if (totalProblems > 1) {
                        Badge {
                            Text(stringResource(R.string.astro_math_progress, currentProblemIndex, totalProblems))
                        }
                    }
                }

                Text(
                    text = "${currentProblem.expression} = ?",
                    fontSize = 32.sp,
                    fontWeight = FontWeight.ExtraBold,
                    color = MaterialTheme.colorScheme.primary
                )

                OutlinedTextField(
                    value = userAnswer,
                    onValueChange = {
                        userAnswer = it.filter { char -> char.isDigit() || char == '-' }
                        isError = false
                    },
                    label = { Text(stringResource(R.string.astro_math_hint)) },
                    isError = isError,
                    keyboardOptions = KeyboardOptions(
                        keyboardType = KeyboardType.Number,
                        imeAction = ImeAction.Done
                    ),
                    keyboardActions = KeyboardActions(onDone = { checkAndAdvance() }),
                    singleLine = true,
                    modifier = Modifier.fillMaxWidth()
                )

                if (isError) {
                    Text(
                        text = stringResource(R.string.astro_math_incorrect),
                        color = MaterialTheme.colorScheme.error,
                        style = MaterialTheme.typography.bodySmall
                    )
                }

                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.End,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    TextButton(onClick = onDismiss) {
                        Text(stringResource(R.string.astro_action_cancel))
                    }
                    Spacer(modifier = Modifier.width(8.dp))
                    Button(onClick = { checkAndAdvance() }) {
                        Text(stringResource(R.string.astro_math_submit))
                    }
                }
            }
        }
    }
}
