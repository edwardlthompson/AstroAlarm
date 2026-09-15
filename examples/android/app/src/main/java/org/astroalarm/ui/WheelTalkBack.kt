package org.astroalarm.ui

import androidx.compose.ui.Modifier
import androidx.compose.ui.semantics.CustomAccessibilityAction
import androidx.compose.ui.semantics.contentDescription
import androidx.compose.ui.semantics.customActions
import androidx.compose.ui.semantics.semantics

fun Modifier.wheelTalkBack(
    description: String,
    nextLabel: String,
    resetLabel: String,
    shareLabel: String,
    onNext: () -> Boolean,
    onReset: () -> Boolean,
    onShare: () -> Boolean,
): Modifier = semantics {
    contentDescription = description
    customActions = listOf(
        CustomAccessibilityAction(nextLabel, onNext),
        CustomAccessibilityAction(resetLabel, onReset),
        CustomAccessibilityAction(shareLabel, onShare),
    )
}
