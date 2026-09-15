package org.astroalarm.ui

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.hapticfeedback.HapticFeedbackType
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalHapticFeedback
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.semantics.Role
import androidx.compose.ui.semantics.contentDescription
import androidx.compose.ui.semantics.role
import androidx.compose.ui.semantics.semantics
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
import kotlinx.coroutines.delay
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
    val context = LocalContext.current
    val haptic = LocalHapticFeedback.current
    val reduceMotion = ReduceMotion.enabled(context)
    var showMathChallenge by remember { mutableStateOf(false) }
    var stopping by remember { mutableStateOf(false) }
    var stopScale by remember { mutableFloatStateOf(1f) }
    LaunchedEffect(stopping) {
        if (!stopping) return@LaunchedEffect
        haptic.performHapticFeedback(HapticFeedbackType.LongPress)
        if (LockscreenStop.animateScale(reduceMotion)) {
            stopScale = LockscreenStop.PRESSED_SCALE
            delay(LockscreenStop.SCALE_MS.toLong())
        }
        onStop()
    }

    val stopCd = stringResource(R.string.a11y_stop_alarm, alarm.label)
    val snoozeCd = stringResource(R.string.a11y_snooze_alarm, alarm.label)
    Surface(
        modifier = Modifier.fillMaxSize(),
        color = MaterialTheme.colorScheme.background
    ) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .statusBarsPadding()
                .navigationBarsPadding()
                .padding(24.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.SpaceBetween
        ) {
            Button(
                onClick = {
                    haptic.performHapticFeedback(HapticFeedbackType.Confirm)
                    if (alarm.mathUnlockEnabled) {
                        showMathChallenge = true
                    } else {
                        stopping = true
                    }
                },
                modifier = Modifier
                    .fillMaxWidth()
                    .height(56.dp)
                    .graphicsLayer { scaleX = stopScale; scaleY = stopScale }
                    .semantics {
                        role = Role.Button
                        contentDescription = stopCd
                    },
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

            Button(
                onClick = {
                    haptic.performHapticFeedback(HapticFeedbackType.Confirm)
                    onSnooze()
                },
                modifier = Modifier
                    .fillMaxWidth()
                    .height(56.dp)
                    .padding(bottom = 8.dp)
                    .semantics {
                        role = Role.Button
                        contentDescription = snoozeCd
                    },
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
