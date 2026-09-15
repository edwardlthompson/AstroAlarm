package org.astroalarm.ui

import android.content.Context
import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.FilterChip
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.semantics.contentDescription
import androidx.compose.ui.semantics.semantics
import androidx.compose.ui.unit.dp
import dev.foss.goldenpath.R
import org.astroalarm.astro.alarm.AstroAlarmScheduler
import org.astroalarm.astro.alarm.AstroAlarmStore
import org.astroalarm.astro.model.AstroAlarm
import org.astroalarm.astro.place.AstroPlace
import org.astroalarm.astro.settings.AstroDisplayPreferences
import org.astroalarm.widget.AstroUpcomingWidgetProvider
import org.astroalarm.widget.WidgetPin

@Composable
fun AstroAlarmsPage(
    context: Context,
    alarms: List<AstroAlarm>,
    place: AstroPlace?,
    viewMode: AlarmViewMode,
    displayPrefs: AstroDisplayPreferences,
    alarmStore: AstroAlarmStore,
    onEdit: (AstroAlarm) -> Unit,
) {
    var pendingDelete by remember { mutableStateOf<AstroAlarm?>(null) }
    fun persistDelete(alarm: AstroAlarm) {
        alarmStore.delete(alarm.id)
        AstroAlarmScheduler.rescheduleAll(context)
    }
    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(horizontal = 16.dp)
    ) {
        LazyColumn(
            modifier = Modifier.fillMaxWidth().weight(1f),
            verticalArrangement = Arrangement.spacedBy(10.dp),
        ) {
            item { AlarmPermissionBanner(context) }
            if (alarms.isEmpty()) {
                item {
                    Column(
                        modifier = Modifier.fillMaxWidth().padding(top = 24.dp),
                        horizontalAlignment = Alignment.CenterHorizontally,
                    ) {
                        Image(
                            painter = painterResource(EmptyAlarmsMark.drawableId),
                            contentDescription = null,
                            modifier = Modifier.size(EmptyAlarmsMark.SIZE_DP.dp),
                        )
                        EmptySectionNote(stringResource(R.string.astro_empty_all))
                    }
                }
            } else if (viewMode == AlarmViewMode.NextDue) {
                renderNextDueAlarms(
                    alarms = alarms,
                    place = place,
                    onToggle = { alarm, enabled ->
                        alarmStore.toggle(alarm.id, enabled)
                        AstroAlarmScheduler.rescheduleAll(context)
                    },
                    onEdit = onEdit,
                    onDelete = { pendingDelete = it },
                )
            } else {
                renderGroupedAlarms(
                    alarms = alarms,
                    place = place,
                    onToggle = { alarm, enabled ->
                        alarmStore.toggle(alarm.id, enabled)
                        AstroAlarmScheduler.rescheduleAll(context)
                    },
                    onEdit = onEdit,
                    onDelete = { pendingDelete = it },
                )
            }
            item { androidx.compose.foundation.layout.Spacer(modifier = Modifier.height(8.dp)) }
        }

        Row(
            modifier = Modifier.fillMaxWidth().padding(top = 2.dp, bottom = 8.dp),
            horizontalArrangement = Arrangement.spacedBy(8.dp),
            verticalAlignment = Alignment.CenterVertically,
        ) {
            FilterChip(
                selected = viewMode == AlarmViewMode.NextDue,
                onClick = { displayPrefs.setAlarmViewMode(AlarmViewMode.NextDue) },
                label = { Text(stringResource(R.string.astro_sort_next_due)) },
            )
            FilterChip(
                selected = viewMode == AlarmViewMode.Grouped,
                onClick = { displayPrefs.setAlarmViewMode(AlarmViewMode.Grouped) },
                label = { Text(stringResource(R.string.astro_sort_grouped)) },
            )
        }
        TextButton(
            onClick = { WidgetPin.request(context, AstroUpcomingWidgetProvider::class.java) },
            modifier = Modifier
                .fillMaxWidth()
                .padding(bottom = 64.dp)
                .semantics { contentDescription = context.getString(R.string.astro_add_upcoming_widget_cd) },
        ) {
            Text(stringResource(R.string.astro_add_upcoming_widget_btn))
        }
    }
    pendingDelete?.let { alarm ->
        AlertDialog(
            onDismissRequest = { pendingDelete = null },
            title = { Text(stringResource(R.string.astro_delete_confirm_title)) },
            text = { Text(stringResource(R.string.astro_delete_confirm_body)) },
            confirmButton = {
                TextButton(onClick = {
                    persistDelete(alarm)
                    pendingDelete = null
                }) {
                    Text(stringResource(R.string.astro_action_delete))
                }
            },
            dismissButton = {
                TextButton(onClick = { pendingDelete = null }) {
                    Text(stringResource(R.string.astro_action_cancel))
                }
            },
        )
    }
}
