package org.astroalarm.ui

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.pager.HorizontalPager
import androidx.compose.foundation.pager.rememberPagerState
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import dev.foss.goldenpath.R
import kotlinx.coroutines.launch
import org.astroalarm.astro.alarm.AstroAlarmScheduler
import org.astroalarm.astro.alarm.AstroAlarmStore
import org.astroalarm.astro.alarm.SunriseOffer
import org.astroalarm.astro.alarm.SunriseOfferStore
import org.astroalarm.astro.birth.BirthProfileStore
import org.astroalarm.astro.model.AlarmTarget
import org.astroalarm.astro.model.AstroAlarm
import org.astroalarm.astro.model.SolarEventType
import org.astroalarm.astro.place.AstroPlaceStore
import org.astroalarm.astro.settings.AstroDisplayPreferences
import org.astroalarm.astro.settings.AstroNavPreferences

@Composable
fun AstroScreen(
    placeStore: AstroPlaceStore,
    alarmStore: AstroAlarmStore,
    birthStore: BirthProfileStore,
    modifier: Modifier = Modifier,
) {
    val context = LocalContext.current
    val displayPrefs = remember { AstroDisplayPreferences(context) }
    val navPrefs = remember { AstroNavPreferences(context) }
    val viewMode by displayPrefs.alarmViewMode.collectAsState()
    val coroutineScope = rememberCoroutineScope()
    val place by placeStore.place.collectAsState()
    val alarms by alarmStore.alarms.collectAsState()
    val birthProfiles by birthStore.profiles.collectAsState()
    val activeBirth = birthProfiles.firstOrNull { it.active } ?: birthProfiles.firstOrNull()
    var editingAlarm by remember { mutableStateOf<AstroAlarm?>(null) }
    var showAddDialogWithTarget by remember { mutableStateOf<AlarmTarget?>(null) }
    val pagerState = rememberPagerState(pageCount = { 3 })
    val sunriseOfferStore = remember { SunriseOfferStore(context) }
    var showSunriseOffer by remember { mutableStateOf(false) }
    var sunriseOfferHm by remember { mutableStateOf("") }
    val reduceMotion = ReduceMotion.enabled(context)
    LaunchedEffect(place, alarms) {
        if (!sunriseOfferStore.consumePending()) return@LaunchedEffect
        val p = place
        if (!SunriseOffer.shouldShow(p, alarms, sunriseOfferStore.dismissed(), pending = true)) {
            if (SunriseOffer.hasSunriseAlarm(alarms) || p == null) sunriseOfferStore.markDone()
            return@LaunchedEffect
        }
        val fire = SunriseOffer.nextFire(p!!) ?: return@LaunchedEffect
        sunriseOfferHm = SunriseOffer.formatHm(fire, p.zone)
        showSunriseOffer = true
    }
    fun goTo(page: Int) {
        coroutineScope.launch {
            if (reduceMotion) pagerState.scrollToPage(page) else pagerState.animateScrollToPage(page)
        }
    }
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
            androidx.compose.animation.AnimatedVisibility(
                visible = pagerState.currentPage == 0,
                enter = UxMotion.fabEnter(reduceMotion),
                exit = UxMotion.fabExit(reduceMotion),
            ) {
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
            PrimaryTabRow(
                selectedTabIndex = pagerState.currentPage,
                modifier = Modifier.fillMaxWidth(),
            ) {
                Tab(
                    selected = pagerState.currentPage == 0,
                    onClick = { goTo(0) },
                    text = { Text(stringResource(R.string.astro_tab_alarms)) },
                )
                Tab(
                    selected = pagerState.currentPage == 1,
                    onClick = { goTo(1) },
                    text = { Text(stringResource(R.string.astro_tab_daily)) },
                )
                Tab(
                    selected = pagerState.currentPage == 2,
                    onClick = { goTo(2) },
                    text = { Text(stringResource(R.string.astro_tab_sky)) },
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
                    )
                    1 -> DailyHubPage(place = place, alarms = alarms, navPrefs = navPrefs)
                    else -> SkyHubPage(
                        place = place,
                        alarms = alarms,
                        natalProfile = activeBirth,
                        birthStore = birthStore,
                        alarmStore = alarmStore,
                        navPrefs = navPrefs,
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
    if (showSunriseOffer) {
        SunriseOfferDialog(
            timeHm = sunriseOfferHm,
            onAdd = {
                val label = AstroEventLabels.solarLabel(context.resources, SolarEventType.Sunrise)
                alarmStore.save(SunriseOffer.alarm(label))
                AstroAlarmScheduler.rescheduleAll(context)
                sunriseOfferStore.markDone()
                showSunriseOffer = false
            },
            onDismiss = {
                sunriseOfferStore.markDone()
                showSunriseOffer = false
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
