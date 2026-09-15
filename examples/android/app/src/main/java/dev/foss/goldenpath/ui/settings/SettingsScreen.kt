package dev.foss.goldenpath.ui.settings

import android.Manifest
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.ScrollState
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.ExperimentalLayoutApi
import androidx.compose.foundation.layout.FlowRow
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.Button
import androidx.compose.material3.FilterChip
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Switch
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import dev.foss.goldenpath.R
import dev.foss.goldenpath.display.highRefreshScroll
import dev.foss.goldenpath.ui.insets.bottomInsetPadding
import dev.foss.goldenpath.ui.theme.SpacingMd
import dev.foss.goldenpath.ui.theme.ThemeMode
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import org.astroalarm.astro.alarm.AstroAlarmScheduler
import org.astroalarm.astro.alarm.SunriseOfferStore
import org.astroalarm.astro.place.AstroPlace
import org.astroalarm.astro.place.AstroPlaceFinder
import org.astroalarm.astro.place.AstroPlaceStore
import org.astroalarm.math.MathPreferences
import org.astroalarm.tts.TtsPreferences
import org.astroalarm.ui.LocationCard
import org.astroalarm.ui.math.MathSettingsSection
import org.astroalarm.ui.onboard.PermissionNagDialog
import org.astroalarm.ui.tts.TtsVoicePicker
import org.astroalarm.widget.AppSnackbar

@OptIn(ExperimentalLayoutApi::class)
@Composable
fun SettingsScreen(
    themeMode: ThemeMode,
    onThemeModeSelect: (ThemeMode) -> Unit,
    saveCrashes: Boolean,
    onSaveCrashes: (Boolean) -> Unit,
    placeStore: AstroPlaceStore,
    onOpenAbout: () -> Unit,
    scrollState: ScrollState = rememberScrollState(),
    modifier: Modifier = Modifier,
) {
    val context = LocalContext.current
    val scope = rememberCoroutineScope()
    val place by placeStore.place.collectAsState()

    var searchQuery by remember { mutableStateOf("") }
    var citySuggestions by remember { mutableStateOf<List<AstroPlace>>(emptyList()) }
    var isLocating by remember { mutableStateOf(false) }
    var showOnboarding by remember { mutableStateOf(false) }
    var expandCitySearch by remember { mutableStateOf(0) }

    val ttsPrefs = remember { TtsPreferences(context) }
    val voice by ttsPrefs.voice.collectAsState()
    val mathPrefs = remember { MathPreferences(context) }
    val sunriseOfferStore = remember { SunriseOfferStore(context) }
    fun savePlace(next: AstroPlace) {
        val hadPlace = placeStore.get() != null
        placeStore.set(next)
        sunriseOfferStore.markPendingIfFirstCity(hadPlace)
        AstroAlarmScheduler.rescheduleAll(context)
    }

    fun triggerLocate() {
        scope.launch {
            isLocating = true
            val loc = withContext(Dispatchers.IO) {
                AstroPlaceFinder.resolveLocation(context)
            }
            isLocating = false
            if (loc != null) {
                savePlace(loc)
                AppSnackbar.emit(context.getString(R.string.astro_toast_location_updated, loc.cityName))
            } else {
                expandCitySearch++
                AppSnackbar.emit(context.getString(R.string.astro_toast_location_failed))
            }
        }
    }

    val permissionLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.RequestMultiplePermissions(),
    ) { perms ->
        val granted = perms[Manifest.permission.ACCESS_FINE_LOCATION] == true ||
            perms[Manifest.permission.ACCESS_COARSE_LOCATION] == true
        if (granted) {
            triggerLocate()
        } else {
            expandCitySearch++
            AppSnackbar.emit(context.getString(R.string.astro_toast_location_permission))
        }
    }

    LaunchedEffect(searchQuery) {
        if (searchQuery.trim().length >= 2) {
            citySuggestions = withContext(Dispatchers.IO) {
                AstroPlaceFinder.searchCities(context, searchQuery)
            }
        } else {
            citySuggestions = emptyList()
        }
    }

    if (showOnboarding) {
        PermissionNagDialog(onComplete = { showOnboarding = false })
    }

    Column(
        modifier = modifier
            .highRefreshScroll()
            .verticalScroll(scrollState)
            .padding(SpacingMd),
        verticalArrangement = Arrangement.spacedBy(16.dp),
    ) {
        LocationCard(
            place = place,
            searchQuery = searchQuery,
            onSearchQueryChange = { searchQuery = it },
            suggestions = citySuggestions,
            isLocating = isLocating,
            expandEpoch = expandCitySearch,
            onSelectCity = { selected ->
                savePlace(selected)
                searchQuery = ""
                citySuggestions = emptyList()
            },
            onUseGps = {
                if (AstroPlaceFinder.hasLocationPermission(context)) {
                    triggerLocate()
                } else {
                    permissionLauncher.launch(
                        arrayOf(
                            Manifest.permission.ACCESS_FINE_LOCATION,
                            Manifest.permission.ACCESS_COARSE_LOCATION,
                        ),
                    )
                }
            },
        )

        HorizontalDivider()

        TtsVoicePicker(
            voice = voice,
            onVoiceChange = { newVoice -> ttsPrefs.setVoice(newVoice) },
        )

        HorizontalDivider()

        MathSettingsSection(mathPrefs = mathPrefs)

        HorizontalDivider()

        Text(
            text = stringResource(R.string.settings_theme_label),
            style = MaterialTheme.typography.titleMedium,
            fontWeight = FontWeight.SemiBold,
        )
        FlowRow(horizontalArrangement = Arrangement.spacedBy(SpacingMd)) {
            ThemeMode.entries.forEach { mode ->
                FilterChip(
                    selected = themeMode == mode,
                    onClick = { onThemeModeSelect(mode) },
                    label = {
                        Text(
                            when (mode) {
                                ThemeMode.System -> stringResource(R.string.settings_theme_mode_system)
                                ThemeMode.Light -> stringResource(R.string.settings_theme_mode_light)
                                ThemeMode.Dark -> stringResource(R.string.settings_theme_mode_dark)
                            },
                        )
                    },
                )
            }
        }

        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
        ) {
            Text(
                text = stringResource(R.string.settings_feedback_save_crashes),
                modifier = Modifier.weight(1f),
            )
            Switch(checked = saveCrashes, onCheckedChange = onSaveCrashes)
        }

        HorizontalDivider()

        Button(
            onClick = { showOnboarding = true },
            modifier = Modifier.fillMaxWidth(),
        ) {
            Text(stringResource(R.string.onboard_settings_button))
        }

        Button(
            onClick = onOpenAbout,
            modifier = Modifier
                .fillMaxWidth()
                .bottomInsetPadding(),
        ) {
            Text(stringResource(R.string.settings_about_button))
        }
    }
}
