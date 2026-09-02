package org.astroalarm.ui

import android.appwidget.AppWidgetManager
import android.content.ComponentName
import android.os.Build
import android.widget.Toast
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.pager.HorizontalPager
import androidx.compose.foundation.pager.rememberPagerState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.AddCircle
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalConfiguration
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.semantics.contentDescription
import androidx.compose.ui.semantics.semantics
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import dev.foss.goldenpath.R
import kotlinx.coroutines.launch
import org.astroalarm.astro.alarm.AstroAlarmScheduler
import org.astroalarm.astro.alarm.AstroAlarmStore
import org.astroalarm.astro.model.AlarmTarget
import org.astroalarm.astro.model.AstroAlarm
import org.astroalarm.astro.model.SolarEventType
import org.astroalarm.astro.place.AstroPlaceStore
import org.astroalarm.astro.settings.AstroDisplayPreferences
import org.astroalarm.widget.AstroUpcomingWidgetProvider
import org.astroalarm.ui.solarterm.SolarTermScreen
import org.astroalarm.ui.sol.SolScreen

@Composable
fun AstroScreen(
    placeStore: AstroPlaceStore,
    alarmStore: AstroAlarmStore,
    modifier: Modifier = Modifier
) {
    val context = LocalContext.current
    val displayPrefs = remember { AstroDisplayPreferences(context) }
    val viewMode by displayPrefs.alarmViewMode.collectAsState()
    val coroutineScope = rememberCoroutineScope()
    val place by placeStore.place.collectAsState()
    val alarms by alarmStore.alarms.collectAsState()

    var editingAlarm by remember { mutableStateOf<AstroAlarm?>(null) }
    var showAddDialogWithTarget by remember { mutableStateOf<AlarmTarget?>(null) }

    val pagerState = rememberPagerState(pageCount = { 5 })

    val hasLocation = place != null && place!!.isValid
    val defaultTarget = if (hasLocation) {
        AlarmTarget.Solar(SolarEventType.Sunrise, 0)
    } else {
        AlarmTarget.CustomClock(7, 0)
    }

    Scaffold(
        modifier = modifier.fillMaxSize(),
        contentWindowInsets = WindowInsets(0, 0, 0, 0),
        floatingActionButton = {
            if (pagerState.currentPage == 0) {
                FloatingActionButton(
                    onClick = { showAddDialogWithTarget = defaultTarget },
                    containerColor = MaterialTheme.colorScheme.primary,
                    contentColor = MaterialTheme.colorScheme.onPrimary
                ) {
                    Icon(Icons.Default.Add, contentDescription = stringResource(R.string.astro_cd_add_alarm))
                }
            }
        }
    ) { innerPadding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding)
        ) {
            val wideTabs = LocalConfiguration.current.screenWidthDp >= 400
            PrimaryTabRow(
                selectedTabIndex = pagerState.currentPage,
                modifier = Modifier.fillMaxWidth(),
            ) {
                Tab(
                    selected = pagerState.currentPage == 0,
                    onClick = { coroutineScope.launch { pagerState.animateScrollToPage(0) } },
                    text = { Text(stringResource(R.string.astro_tab_alarms), fontSize = 11.sp) }
                )
                Tab(
                    selected = pagerState.currentPage == 1,
                    onClick = { coroutineScope.launch { pagerState.animateScrollToPage(1) } },
                    modifier = Modifier.semantics {
                        contentDescription = "${context.getString(R.string.astro_tab_daily)} ${context.getString(R.string.astro_tab_2d)}"
                    },
                    text = {
                        if (wideTabs) {
                            Column(horizontalAlignment = Alignment.CenterHorizontally) {
                                Text(stringResource(R.string.astro_tab_daily), fontSize = 11.sp)
                                Text(stringResource(R.string.astro_tab_2d), fontSize = 11.sp)
                            }
                        } else {
                            Text(stringResource(R.string.astro_tab_2d), fontSize = 11.sp)
                        }
                    }
                )
                Tab(
                    selected = pagerState.currentPage == 2,
                    onClick = { coroutineScope.launch { pagerState.animateScrollToPage(2) } },
                    modifier = Modifier.semantics {
                        contentDescription = "${context.getString(R.string.astro_tab_daily)} ${context.getString(R.string.astro_tab_3d)}"
                    },
                    text = {
                        if (wideTabs) {
                            Column(horizontalAlignment = Alignment.CenterHorizontally) {
                                Text(stringResource(R.string.astro_tab_daily), fontSize = 11.sp)
                                Text(stringResource(R.string.astro_tab_3d), fontSize = 11.sp)
                            }
                        } else {
                            Text(stringResource(R.string.astro_tab_3d), fontSize = 11.sp)
                        }
                    }
                )
                Tab(
                    selected = pagerState.currentPage == 3,
                    onClick = { coroutineScope.launch { pagerState.animateScrollToPage(3) } },
                    text = { Text(stringResource(R.string.astro_tab_yearly), fontSize = 11.sp) }
                )
                Tab(
                    selected = pagerState.currentPage == 4,
                    onClick = { coroutineScope.launch { pagerState.animateScrollToPage(4) } },
                    text = { Text(stringResource(R.string.astro_tab_sol), fontSize = 11.sp) }
                )
            }

            HorizontalPager(
                state = pagerState,
                modifier = Modifier
                    .fillMaxWidth()
                    .weight(1f)
            ) { page ->
                when (page) {
                    0 -> {
                        Column(
                            modifier = Modifier
                                .fillMaxSize()
                                .padding(horizontal = 16.dp)
                        ) {
                            Button(
                                onClick = {
                                    val appWidgetManager = context.getSystemService(AppWidgetManager::class.java)
                                    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O && appWidgetManager != null && appWidgetManager.isRequestPinAppWidgetSupported) {
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
                                    .semantics {
                                        contentDescription = context.getString(R.string.astro_add_upcoming_widget_cd)
                                    },
                                shape = RoundedCornerShape(12.dp)
                            ) {
                                Icon(Icons.Default.AddCircle, contentDescription = null)
                                Spacer(modifier = Modifier.width(8.dp))
                                Text(stringResource(R.string.astro_add_upcoming_widget_btn))
                            }

                            LazyColumn(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .weight(1f),
                                verticalArrangement = Arrangement.spacedBy(10.dp)
                            ) {
                                if (alarms.isEmpty()) {
                                    item {
                                        EmptySectionNote(stringResource(R.string.astro_empty_all))
                                    }
                                } else if (viewMode == AlarmViewMode.NextDue) {
                                    renderNextDueAlarms(
                                        alarms = alarms,
                                        place = place,
                                        onToggle = { alarm, enabled ->
                                            alarmStore.toggle(alarm.id, enabled)
                                            AstroAlarmScheduler.rescheduleAll(context)
                                        },
                                        onEdit = { editingAlarm = it },
                                        onDelete = {
                                            alarmStore.delete(it.id)
                                            AstroAlarmScheduler.rescheduleAll(context)
                                        }
                                    )
                                } else {
                                    renderGroupedAlarms(
                                        alarms = alarms,
                                        place = place,
                                        onToggle = { alarm, enabled ->
                                            alarmStore.toggle(alarm.id, enabled)
                                            AstroAlarmScheduler.rescheduleAll(context)
                                        },
                                        onEdit = { editingAlarm = it },
                                        onDelete = {
                                            alarmStore.delete(it.id)
                                            AstroAlarmScheduler.rescheduleAll(context)
                                        }
                                    )
                                }

                                item { Spacer(modifier = Modifier.height(8.dp)) }
                            }

                            Row(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .padding(top = 2.dp, bottom = 72.dp),
                                horizontalArrangement = Arrangement.SpaceBetween,
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                                    FilterChip(
                                        selected = viewMode == AlarmViewMode.NextDue,
                                        onClick = { displayPrefs.setAlarmViewMode(AlarmViewMode.NextDue) },
                                        label = { Text(stringResource(R.string.astro_sort_next_due)) }
                                    )
                                    FilterChip(
                                        selected = viewMode == AlarmViewMode.Grouped,
                                        onClick = { displayPrefs.setAlarmViewMode(AlarmViewMode.Grouped) },
                                        label = { Text(stringResource(R.string.astro_sort_grouped)) }
                                    )
                                }
                                TextButton(
                                    onClick = { coroutineScope.launch { pagerState.animateScrollToPage(1) } },
                                    contentPadding = PaddingValues(horizontal = 4.dp, vertical = 2.dp)
                                ) {
                                    Text(stringResource(R.string.astro_swipe_hint), fontSize = 11.sp)
                                }
                            }
                        }
                    }
                    1 -> AstroClockScreen(place = place, alarms = alarms, modifier = Modifier.fillMaxSize())
                    2 -> Astro3DClockScreen(place = place, alarms = alarms, modifier = Modifier.fillMaxSize())
                    3 -> SolarTermScreen(place = place, alarms = alarms, modifier = Modifier.fillMaxSize())
                    else -> SolScreen(place = place, alarms = alarms, modifier = Modifier.fillMaxSize())
                }
            }
        }
    }

    if (showAddDialogWithTarget != null) {
        AstroEditDialog(
            initialAlarm = null,
            defaultTarget = showAddDialogWithTarget!!,
            place = place,
            onDismiss = { showAddDialogWithTarget = null },
            onSave = { newAlarm ->
                alarmStore.save(newAlarm)
                AstroAlarmScheduler.rescheduleAll(context)
                showAddDialogWithTarget = null
            }
        )
    }

    if (editingAlarm != null) {
        AstroEditDialog(
            initialAlarm = editingAlarm,
            defaultTarget = editingAlarm!!.target,
            place = place,
            onDismiss = { editingAlarm = null },
            onSave = { updated ->
                alarmStore.save(updated)
                AstroAlarmScheduler.rescheduleAll(context)
                editingAlarm = null
            }
        )
    }
}
