package dev.foss.goldenpath.ui.settings

import android.Manifest
import android.widget.Toast
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.ScrollState
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import dev.foss.goldenpath.R
import dev.foss.goldenpath.display.highRefreshScroll
import dev.foss.goldenpath.ui.insets.bottomInsetPadding
import dev.foss.goldenpath.ui.theme.SpacingMd
import dev.foss.goldenpath.ui.theme.ThemeMode
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import org.astroalarm.astro.alarm.AstroAlarmScheduler
import org.astroalarm.astro.place.AstroPlace
import org.astroalarm.astro.place.AstroPlaceFinder
import org.astroalarm.astro.place.AstroPlaceStore
import org.astroalarm.math.MathPreferences
import org.astroalarm.tts.TtsPreferences
import org.astroalarm.ui.LocationCard
import org.astroalarm.ui.math.MathSettingsSection
import org.astroalarm.ui.tts.TtsVoicePicker

@OptIn(ExperimentalLayoutApi::class)
@Composable
fun SettingsScreen(
    themeMode: ThemeMode,
    onThemeModeSelect: (ThemeMode) -> Unit,
    saveCrashes: Boolean,
    onSaveCrashes: (Boolean) -> Unit,
    placeStore: AstroPlaceStore,
    onOpenAbout: () -> Unit,
    onOpenUrl: (String) -> Unit,
    scrollState: ScrollState = rememberScrollState(),
    modifier: Modifier = Modifier,
) {
    val context = LocalContext.current
    val scope = rememberCoroutineScope()
    val place by placeStore.place.collectAsState()

    var searchQuery by remember { mutableStateOf("") }
    var citySuggestions by remember { mutableStateOf<List<AstroPlace>>(emptyList()) }
    var isLocating by remember { mutableStateOf(false) }

    val ttsPrefs = remember { TtsPreferences(context) }
    val voice by ttsPrefs.voice.collectAsState()
    val mathPrefs = remember { MathPreferences(context) }

    fun triggerLocate() {
        scope.launch {
            isLocating = true
            val loc = withContext(Dispatchers.IO) {
                AstroPlaceFinder.resolveLocation(context)
            }
            isLocating = false
            if (loc != null) {
                placeStore.set(loc)
                AstroAlarmScheduler.rescheduleAll(context)
                Toast.makeText(context, context.getString(R.string.astro_toast_location_updated, loc.cityName), Toast.LENGTH_SHORT).show()
            } else {
                Toast.makeText(context, context.getString(R.string.astro_toast_location_failed), Toast.LENGTH_LONG).show()
            }
        }
    }

    val permissionLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.RequestMultiplePermissions()
    ) { perms ->
        val granted = perms[Manifest.permission.ACCESS_FINE_LOCATION] == true ||
            perms[Manifest.permission.ACCESS_COARSE_LOCATION] == true
        if (granted) {
            triggerLocate()
        } else {
            Toast.makeText(context, context.getString(R.string.astro_toast_location_permission), Toast.LENGTH_LONG).show()
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

    Column(
        modifier = modifier
            .highRefreshScroll()
            .verticalScroll(scrollState)
            .padding(SpacingMd),
        verticalArrangement = Arrangement.spacedBy(16.dp),
    ) {
        Text(
            text = stringResource(R.string.settings_title),
            style = MaterialTheme.typography.headlineSmall,
            fontWeight = FontWeight.Bold
        )

        LocationCard(
            place = place,
            searchQuery = searchQuery,
            onSearchQueryChange = { searchQuery = it },
            suggestions = citySuggestions,
            isLocating = isLocating,
            onSelectCity = { selected ->
                placeStore.set(selected)
                searchQuery = ""
                citySuggestions = emptyList()
                AstroAlarmScheduler.rescheduleAll(context)
            },
            onUseGps = {
                if (AstroPlaceFinder.hasLocationPermission(context)) {
                    triggerLocate()
                } else {
                    permissionLauncher.launch(
                        arrayOf(
                            Manifest.permission.ACCESS_FINE_LOCATION,
                            Manifest.permission.ACCESS_COARSE_LOCATION
                        )
                    )
                }
            }
        )

        HorizontalDivider()

        TtsVoicePicker(
            voice = voice,
            onVoiceChange = { newVoice ->
                ttsPrefs.setVoice(newVoice)
            }
        )

        HorizontalDivider()

        MathSettingsSection(mathPrefs = mathPrefs)

        HorizontalDivider()

        Card(
            modifier = Modifier.fillMaxWidth(),
            shape = RoundedCornerShape(12.dp),
            colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.7f))
        ) {
            Column(modifier = Modifier.padding(14.dp), verticalArrangement = Arrangement.spacedBy(8.dp)) {
                Text(
                    text = stringResource(R.string.openshouter_title),
                    style = MaterialTheme.typography.titleMedium,
                    fontWeight = FontWeight.Bold,
                    color = MaterialTheme.colorScheme.primary
                )
                Text(
                    text = stringResource(R.string.openshouter_desc),
                    style = MaterialTheme.typography.bodySmall,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
                FilledTonalButton(
                    onClick = { onOpenUrl("https://github.com/edwardlthompson/OpenShouter") },
                    contentPadding = PaddingValues(horizontal = 12.dp, vertical = 6.dp)
                ) {
                    Text(stringResource(R.string.openshouter_btn), fontSize = 13.sp)
                }
            }
        }

        HorizontalDivider()

        Text(
            text = stringResource(R.string.settings_theme_label),
            style = MaterialTheme.typography.titleMedium,
            fontWeight = FontWeight.SemiBold
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
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            Text(
                text = stringResource(R.string.settings_feedback_save_crashes),
                modifier = Modifier.weight(1f)
            )
            Switch(checked = saveCrashes, onCheckedChange = onSaveCrashes)
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
