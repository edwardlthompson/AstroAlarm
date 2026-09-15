package org.astroalarm.ui

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.SegmentedButton
import androidx.compose.material3.SegmentedButtonDefaults
import androidx.compose.material3.SingleChoiceSegmentedButtonRow
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import dev.foss.goldenpath.R
import org.astroalarm.astro.alarm.AstroAlarmStore
import org.astroalarm.astro.birth.BirthProfile
import org.astroalarm.astro.birth.BirthProfileStore
import org.astroalarm.astro.model.AstroAlarm
import org.astroalarm.astro.place.AstroPlace
import org.astroalarm.astro.settings.AstroNavPreferences
import org.astroalarm.astro.settings.DailyChip
import org.astroalarm.astro.settings.SkyChip
import org.astroalarm.ui.birth.BirthChartScreen
import org.astroalarm.ui.sol.SolScreen
import org.astroalarm.ui.solarterm.SolarTermScreen

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun DailyHubPage(
    place: AstroPlace?,
    alarms: List<AstroAlarm>,
    navPrefs: AstroNavPreferences,
) {
    val chip by navPrefs.dailyChip.collectAsState()
    Column(Modifier.fillMaxSize()) {
        SingleChoiceSegmentedButtonRow(
            modifier = Modifier.fillMaxWidth().padding(horizontal = 16.dp, vertical = 4.dp),
        ) {
            HubSegment.daily.forEachIndexed { i, option ->
                SegmentedButton(
                    selected = chip == option,
                    onClick = { navPrefs.setDailyChip(option) },
                    shape = SegmentedButtonDefaults.itemShape(i, HubSegment.daily.size),
                    label = {
                        Text(
                            stringResource(
                                if (option == DailyChip.TwoD) R.string.astro_tab_2d else R.string.astro_tab_3d,
                            ),
                        )
                    },
                )
            }
        }
        Box(Modifier.weight(1f).fillMaxSize()) {
            when (chip) {
                DailyChip.TwoD -> AstroClockScreen(place, alarms, Modifier.fillMaxSize())
                DailyChip.ThreeD -> Astro3DClockScreen(place, alarms, Modifier.fillMaxSize())
            }
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun SkyHubPage(
    place: AstroPlace?,
    alarms: List<AstroAlarm>,
    natalProfile: BirthProfile?,
    birthStore: BirthProfileStore,
    alarmStore: AstroAlarmStore,
    navPrefs: AstroNavPreferences,
) {
    val chip by navPrefs.skyChip.collectAsState()
    Column(Modifier.fillMaxSize()) {
        SingleChoiceSegmentedButtonRow(
            modifier = Modifier.fillMaxWidth().padding(horizontal = 16.dp, vertical = 4.dp),
        ) {
            HubSegment.sky.forEachIndexed { i, option ->
                SegmentedButton(
                    selected = chip == option,
                    onClick = { navPrefs.setSkyChip(option) },
                    shape = SegmentedButtonDefaults.itemShape(i, HubSegment.sky.size),
                    label = {
                        Text(
                            stringResource(
                                when (option) {
                                    SkyChip.Yearly -> R.string.astro_tab_yearly
                                    SkyChip.Sol -> R.string.astro_tab_sol
                                    SkyChip.Chart -> R.string.astro_tab_chart
                                },
                            ),
                        )
                    },
                )
            }
        }
        Box(Modifier.weight(1f).fillMaxSize()) {
            when (chip) {
                SkyChip.Yearly -> SolarTermScreen(place, alarms, Modifier.fillMaxSize())
                SkyChip.Sol -> SolScreen(place, alarms, natalProfile, Modifier.fillMaxSize())
                SkyChip.Chart -> BirthChartScreen(
                    birthStore = birthStore,
                    alarmStore = alarmStore,
                    modifier = Modifier.fillMaxSize(),
                )
            }
        }
    }
}
