package org.astroalarm.ui

import android.appwidget.AppWidgetManager
import android.content.ComponentName
import android.content.Context
import android.os.Build
import android.widget.Toast
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.AddCircle
import androidx.compose.material3.Button
import androidx.compose.material3.FilterChip
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.semantics.contentDescription
import androidx.compose.ui.semantics.semantics
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import dev.foss.goldenpath.R
import org.astroalarm.astro.alarm.AstroAlarmScheduler
import org.astroalarm.astro.alarm.AstroAlarmStore
import org.astroalarm.astro.model.AstroAlarm
import org.astroalarm.astro.place.AstroPlace
import org.astroalarm.astro.settings.AstroDisplayPreferences
import org.astroalarm.widget.AstroUpcomingWidgetProvider

@Composable
fun AstroAlarmsPage(
    context: Context,
    alarms: List<AstroAlarm>,
    place: AstroPlace?,
    viewMode: AlarmViewMode,
    displayPrefs: AstroDisplayPreferences,
    alarmStore: AstroAlarmStore,
    onEdit: (AstroAlarm) -> Unit,
    onSwipeHint: () -> Unit,
) {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(horizontal = 16.dp)
    ) {
        Button(
            onClick = {
                val appWidgetManager = context.getSystemService(AppWidgetManager::class.java)
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O &&
                    appWidgetManager != null &&
                    appWidgetManager.isRequestPinAppWidgetSupported
                ) {
                    val provider = ComponentName(context, AstroUpcomingWidgetProvider::class.java)
                    appWidgetManager.requestPinAppWidget(provider, null, null)
                    Toast.makeText(context, context.getString(R.string.astro_widget_pinned_success), Toast.LENGTH_SHORT).show()
                } else {
                    Toast.makeText(context, context.getString(R.string.astro_widget_pin_manual_guide), Toast.LENGTH_LONG).show()
                }
            },
            modifier = Modifier
                .fillMaxWidth()
                .padding(top = 8.dp, bottom = 4.dp)
                .semantics { contentDescription = context.getString(R.string.astro_add_upcoming_widget_cd) },
            shape = RoundedCornerShape(12.dp),
        ) {
            Icon(Icons.Default.AddCircle, contentDescription = null)
            Spacer(modifier = Modifier.width(8.dp))
            Text(stringResource(R.string.astro_add_upcoming_widget_btn))
        }

        LazyColumn(
            modifier = Modifier.fillMaxWidth().weight(1f),
            verticalArrangement = Arrangement.spacedBy(10.dp),
        ) {
            if (alarms.isEmpty()) {
                item { EmptySectionNote(stringResource(R.string.astro_empty_all)) }
            } else if (viewMode == AlarmViewMode.NextDue) {
                renderNextDueAlarms(
                    alarms = alarms,
                    place = place,
                    onToggle = { alarm, enabled ->
                        alarmStore.toggle(alarm.id, enabled)
                        AstroAlarmScheduler.rescheduleAll(context)
                    },
                    onEdit = onEdit,
                    onDelete = {
                        alarmStore.delete(it.id)
                        AstroAlarmScheduler.rescheduleAll(context)
                    },
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
                    onDelete = {
                        alarmStore.delete(it.id)
                        AstroAlarmScheduler.rescheduleAll(context)
                    },
                )
            }
            item { Spacer(modifier = Modifier.height(8.dp)) }
        }

        Row(
            modifier = Modifier.fillMaxWidth().padding(top = 2.dp, bottom = 72.dp),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically,
        ) {
            Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
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
            TextButton(onClick = onSwipeHint, contentPadding = PaddingValues(horizontal = 4.dp, vertical = 2.dp)) {
                Text(stringResource(R.string.astro_swipe_hint), fontSize = 11.sp)
            }
        }
    }
}
