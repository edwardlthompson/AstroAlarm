package dev.foss.goldenpath.ui

import androidx.activity.compose.BackHandler
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.Info
import androidx.compose.material.icons.filled.Settings
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.SnackbarHostState
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalUriHandler
import androidx.compose.ui.res.stringResource
import dev.foss.goldenpath.R
import dev.foss.goldenpath.about.AppUpdates
import dev.foss.goldenpath.about.DonationsConfig
import dev.foss.goldenpath.ui.about.AboutScreen
import dev.foss.goldenpath.ui.about.LaunchPromptDialogs
import dev.foss.goldenpath.ui.components.GoldenPathScaffold
import dev.foss.goldenpath.ui.components.ThemeToggle
import dev.foss.goldenpath.ui.feedback.FeedbackScreen
import dev.foss.goldenpath.ui.settings.SettingsScreen
import dev.foss.goldenpath.ui.theme.ThemeMode
import org.astroalarm.astro.alarm.AstroAlarmStore
import org.astroalarm.astro.place.AstroPlaceStore
import org.astroalarm.ui.AstroScreen

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun GoldenPathScreen(
    snackbarHostState: SnackbarHostState,
    themeMode: ThemeMode,
    isOnline: Boolean,
    showAbout: Boolean,
    showSettings: Boolean,
    showFeedback: String?,
    saveCrashes: Boolean,
    releaseRepo: String,
    pendingStack: String?,
    appVersion: String,
    installedFormat: String,
    updateStatus: String,
    donations: DonationsConfig,
    canApplyUpdate: Boolean,
    launchPrompt: AppUpdates.LaunchPrompt?,
    onThemeToggle: () -> Unit,
    onThemeModeSelect: (ThemeMode) -> Unit,
    onAboutOpen: () -> Unit,
    onAboutClose: () -> Unit,
    onAboutOpenFromSettings: () -> Unit = onAboutOpen,
    onSettingsOpen: () -> Unit,
    onSettingsClose: () -> Unit,
    onSaveCrashes: (Boolean) -> Unit,
    onReportBug: () -> Unit,
    onRequestFeature: () -> Unit,
    onFeedbackClose: () -> Unit,
    onDonate: () -> Unit,
    onDonatePrompt: (Boolean) -> Unit,
    onUpdatePrompt: (Boolean) -> Unit,
    onApplyUpdate: () -> Unit,
) {
    val isSubScreen = showSettings || showAbout || showFeedback != null
    val settingsScrollState = rememberScrollState()

    BackHandler(enabled = isSubScreen) {
        when {
            showFeedback != null -> onFeedbackClose()
            showAbout -> onAboutClose()
            showSettings -> onSettingsClose()
        }
    }

    GoldenPathScaffold(
        snackbarHostState = snackbarHostState,
        topBar = {
            TopAppBar(
                title = {
                    Text(
                        when {
                            showFeedback != null -> stringResource(
                                if (showFeedback == "feature") R.string.feedback_feature_title else R.string.feedback_bug_title
                            )
                            showAbout -> stringResource(R.string.about_title)
                            showSettings -> stringResource(R.string.settings_title)
                            else -> stringResource(R.string.app_title)
                        }
                    )
                },
                navigationIcon = {
                    if (isSubScreen) {
                        IconButton(onClick = {
                            when {
                                showFeedback != null -> onFeedbackClose()
                                showAbout -> onAboutClose()
                                showSettings -> onSettingsClose()
                            }
                        }) {
                            Icon(
                                imageVector = Icons.AutoMirrored.Filled.ArrowBack,
                                contentDescription = stringResource(R.string.about_nav_back),
                            )
                        }
                    }
                },
                actions = {
                    if (!isSubScreen) {
                        IconButton(onClick = onSettingsOpen) {
                            Icon(
                                imageVector = Icons.Filled.Settings,
                                contentDescription = stringResource(R.string.settings_open),
                            )
                        }
                        IconButton(onClick = onAboutOpen) {
                            Icon(
                                imageVector = Icons.Filled.Info,
                                contentDescription = stringResource(R.string.about_open),
                            )
                        }
                    }
                    ThemeToggle(themeMode = themeMode, onToggle = onThemeToggle)
                },
            )
        },
    ) { innerPadding ->
        if (launchPrompt != null) {
            LaunchPromptDialogs(
                prompt = launchPrompt,
                onDonate = onDonatePrompt,
                onUpdate = onUpdatePrompt,
            )
        }
        when {
            showFeedback != null -> FeedbackScreen(
                kind = showFeedback,
                releaseRepo = releaseRepo,
                stack = pendingStack,
                onBack = onFeedbackClose,
                modifier = Modifier
                    .fillMaxSize()
                    .padding(innerPadding),
            )
            showSettings -> {
                val context = LocalContext.current
                val uriHandler = LocalUriHandler.current
                val placeStore = remember { AstroPlaceStore(context) }
                SettingsScreen(
                    themeMode = themeMode,
                    onThemeModeSelect = onThemeModeSelect,
                    saveCrashes = saveCrashes,
                    onSaveCrashes = onSaveCrashes,
                    placeStore = placeStore,
                    onOpenAbout = onAboutOpenFromSettings,
                    onOpenUrl = { url -> uriHandler.openUri(url) },
                    scrollState = settingsScrollState,
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(innerPadding),
                )
            }
            showAbout -> AboutScreen(
                version = appVersion,
                installedFormat = installedFormat,
                updateStatus = updateStatus,
                donations = donations,
                canApplyUpdate = canApplyUpdate,
                onApplyUpdate = onApplyUpdate,
                onReportBug = onReportBug,
                onRequestFeature = onRequestFeature,
                onBack = onAboutClose,
                modifier = Modifier
                    .fillMaxSize()
                    .padding(innerPadding),
            )
            else -> {
                val context = LocalContext.current
                val placeStore = remember { AstroPlaceStore(context) }
                val alarmStore = remember { AstroAlarmStore(context) }
                AstroScreen(
                    placeStore = placeStore,
                    alarmStore = alarmStore,
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(innerPadding),
                )
            }
        }
    }
}
