package org.astroalarm.ui

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.pager.HorizontalPager
import androidx.compose.foundation.pager.rememberPagerState
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalConfiguration
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.semantics.contentDescription
import androidx.compose.ui.semantics.semantics
import androidx.compose.ui.unit.sp
import dev.foss.goldenpath.R
import kotlinx.coroutines.launch
import org.astroalarm.astro.alarm.AstroAlarmScheduler
import org.astroalarm.astro.alarm.AstroAlarmStore
import org.astroalarm.astro.birth.BirthProfileStore
import org.astroalarm.astro.model.AlarmTarget
import org.astroalarm.astro.model.AstroAlarm
import org.astroalarm.astro.model.SolarEventType
import org.astroalarm.astro.place.AstroPlaceStore
import org.astroalarm.astro.settings.AstroDisplayPreferences
import org.astroalarm.ui.birth.BirthChartScreen
import org.astroalarm.ui.sol.SolScreen
import org.astroalarm.ui.solarterm.SolarTermScreen

@Composable
fun AstroScreen(
    placeStore: AstroPlaceStore,
    alarmStore: AstroAlarmStore,
    birthStore: BirthProfileStore,
    modifier: Modifier = Modifier,
) {
    val context = LocalContext.current
    val displayPrefs = remember { AstroDisplayPreferences(context) }
    val viewMode by displayPrefs.alarmViewMode.collectAsState()
    val coroutineScope = rememberCoroutineScope()
    val place by placeStore.place.collectAsState()
    val alarms by alarmStore.alarms.collectAsState()
    val birthProfiles by birthStore.profiles.collectAsState()
    val activeBirth = birthProfiles.firstOrNull { it.active } ?: birthProfiles.firstOrNull()
    var editingAlarm by remember { mutableStateOf<AstroAlarm?>(null) }
    var showAddDialogWithTarget by remember { mutableStateOf<AlarmTarget?>(null) }
    val pagerState = rememberPagerState(pageCount = { 6 })
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
                    contentColor = MaterialTheme.colorScheme.onPrimary,
                ) {
                    Icon(Icons.Default.Add, contentDescription = stringResource(R.string.astro_cd_add_alarm))
                }
            }
        },
    ) { innerPadding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding),
        ) {
            val wideTabs = LocalConfiguration.current.screenWidthDp >= 400
            PrimaryTabRow(
                selectedTabIndex = pagerState.currentPage,
                modifier = Modifier.fillMaxWidth(),
            ) {
                Tab(
                    selected = pagerState.currentPage == 0,
                    onClick = { coroutineScope.launch { pagerState.animateScrollToPage(0) } },
                    text = { Text(stringResource(R.string.astro_tab_alarms), fontSize = 11.sp) },
                )
                Tab(
                    selected = pagerState.currentPage == 1,
                    onClick = { coroutineScope.launch { pagerState.animateScrollToPage(1) } },
                    modifier = Modifier.semantics {
                        contentDescription =
                            "${context.getString(R.string.astro_tab_daily)} ${context.getString(R.string.astro_tab_2d)}"
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
                    },
                )
                Tab(
                    selected = pagerState.currentPage == 2,
                    onClick = { coroutineScope.launch { pagerState.animateScrollToPage(2) } },
                    modifier = Modifier.semantics {
                        contentDescription =
                            "${context.getString(R.string.astro_tab_daily)} ${context.getString(R.string.astro_tab_3d)}"
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
                    },
                )
                Tab(
                    selected = pagerState.currentPage == 3,
                    onClick = { coroutineScope.launch { pagerState.animateScrollToPage(3) } },
                    text = { Text(stringResource(R.string.astro_tab_yearly), fontSize = 11.sp) },
                )
                Tab(
                    selected = pagerState.currentPage == 4,
                    onClick = { coroutineScope.launch { pagerState.animateScrollToPage(4) } },
                    text = { Text(stringResource(R.string.astro_tab_sol), fontSize = 11.sp) },
                )
                Tab(
                    selected = pagerState.currentPage == 5,
                    onClick = { coroutineScope.launch { pagerState.animateScrollToPage(5) } },
                    text = { Text(stringResource(R.string.astro_tab_chart), fontSize = 11.sp) },
                )
            }
            HorizontalPager(
                state = pagerState,
                modifier = Modifier
                    .fillMaxWidth()
                    .weight(1f),
            ) { page ->
                when (page) {
                    0 -> AstroAlarmsPage(
                        context = context,
                        alarms = alarms,
                        place = place,
                        viewMode = viewMode,
                        displayPrefs = displayPrefs,
                        alarmStore = alarmStore,
                        onEdit = { editingAlarm = it },
                        onSwipeHint = { coroutineScope.launch { pagerState.animateScrollToPage(1) } },
                    )
                    1 -> AstroClockScreen(place = place, alarms = alarms, modifier = Modifier.fillMaxSize())
                    2 -> Astro3DClockScreen(place = place, alarms = alarms, modifier = Modifier.fillMaxSize())
                    3 -> SolarTermScreen(place = place, alarms = alarms, modifier = Modifier.fillMaxSize())
                    4 -> SolScreen(
                        place = place,
                        alarms = alarms,
                        natalProfile = activeBirth,
                        modifier = Modifier.fillMaxSize(),
                    )
                    else -> BirthChartScreen(
                        birthStore = birthStore,
                        alarmStore = alarmStore,
                        modifier = Modifier.fillMaxSize(),
                    )
                }
            }
        }
    }

    if (showAddDialogWithTarget != null) {
        AstroEditDialog(
            initialAlarm = null,
            defaultTarget = showAddDialogWithTarget!!,
            place = place,
            existingAlarms = alarms,
            natalProfileId = activeBirth?.id,
            natalAscOk = activeBirth?.canComputeAscendant == true,
            onDismiss = { showAddDialogWithTarget = null },
            onSave = {
                alarmStore.save(it)
                AstroAlarmScheduler.rescheduleAll(context)
                showAddDialogWithTarget = null
            },
        )
    }
    if (editingAlarm != null) {
        AstroEditDialog(
            initialAlarm = editingAlarm,
            defaultTarget = editingAlarm!!.target,
            place = place,
            existingAlarms = alarms,
            natalProfileId = activeBirth?.id,
            natalAscOk = activeBirth?.canComputeAscendant == true,
            onDismiss = { editingAlarm = null },
            onSave = {
                alarmStore.save(it)
                AstroAlarmScheduler.rescheduleAll(context)
                editingAlarm = null
            },
        )
    }
}
