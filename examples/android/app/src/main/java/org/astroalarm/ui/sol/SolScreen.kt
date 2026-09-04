package org.astroalarm.ui.sol

import android.appwidget.AppWidgetManager
import android.content.ComponentName
import android.os.Build
import android.widget.Toast
import androidx.compose.foundation.Image
import androidx.compose.foundation.gestures.detectTapGestures
import androidx.compose.foundation.gestures.detectTransformGestures
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.AddCircle
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.asImageBitmap
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.semantics.contentDescription
import androidx.compose.ui.semantics.semantics
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import dev.foss.goldenpath.R
import kotlinx.coroutines.delay
import org.astroalarm.sol.PlanetBody
import org.astroalarm.sol.PlanetKepler
import org.astroalarm.astro.model.AstroAlarm
import org.astroalarm.astro.place.AstroPlace
import org.astroalarm.astro.settings.AstroDisplayPreferences
import org.astroalarm.ui.DiskChrome
import org.astroalarm.ui.OverlayToggleLine
import org.astroalarm.widget.ClockRenderSize
import org.astroalarm.widget.PlanetTextures
import org.astroalarm.widget.SolWidgetProvider
import java.time.Instant
import java.time.ZoneId
import java.time.format.DateTimeFormatter
import java.util.Locale

@Composable
fun SolScreen(
    place: AstroPlace? = null,
    alarms: List<AstroAlarm> = emptyList(),
    modifier: Modifier = Modifier,
) {
    val context = LocalContext.current
    val displayPrefs = remember { AstroDisplayPreferences(context) }
    val showEventTimes by displayPrefs.showEventTimesSol.collectAsState()
    var now by remember { mutableStateOf(Instant.now()) }
    var zoom by remember { mutableFloatStateOf(1f) }
    var selected by remember { mutableStateOf<PlanetBody?>(null) }
    val dark = isSystemInDarkTheme()
    val textures = remember {
        PlanetBody.entries.associateWith { PlanetTextures.get(context, it) }
    }
    LaunchedEffect(Unit) {
        while (true) {
            delay(60_000L)
            now = Instant.now()
        }
    }
    val scaleLabel = stringResource(R.string.sol_scale_au)
    val dateLine = DateTimeFormatter.ofPattern("MMM d yyyy", Locale.getDefault())
        .format(now.atZone(ZoneId.systemDefault()))
    Column(
        modifier = modifier.fillMaxSize().padding(horizontal = 16.dp, vertical = 6.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.spacedBy(8.dp)
    ) {
        BoxWithConstraints(Modifier.fillMaxWidth().weight(1f), contentAlignment = Alignment.TopCenter) {
            val side = minOf(maxWidth, (maxHeight - DiskChrome.Reserve).coerceAtLeast(0.dp))
            val sizePx = ClockRenderSize.fromMinDp(side.value.toInt().coerceAtLeast(80))
            val bmp = remember(now.epochSecond / 60, zoom, dark, sizePx, alarms, place, scaleLabel, showEventTimes) {
                SolRenderer.render(sizePx, now, zoom, dark, textures, alarms, place, scaleLabel, showEventTimes)
            }
            Column(
                Modifier.fillMaxWidth().align(Alignment.TopCenter),
                horizontalAlignment = Alignment.CenterHorizontally,
                verticalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                Image(
                    bitmap = bmp.asImageBitmap(),
                    contentDescription = stringResource(R.string.sol_cd),
                    modifier = Modifier
                        .size(side)
                        .pointerInput(sizePx, zoom, now) {
                            detectTapGestures { tap ->
                                val bx = tap.x * sizePx / size.width
                                val by = tap.y * sizePx / size.height
                                selected = SolRenderer.bodyAt(bx, by, sizePx, now, zoom)
                            }
                        }
                        .pointerInput(Unit) {
                            detectTransformGestures { _, _, z, _ ->
                                zoom = (zoom * z).coerceIn(0.05f, 8f)
                            }
                        }
                )
                Button(
                    onClick = { pinWidget(context) },
                    modifier = Modifier.fillMaxWidth().semantics {
                        contentDescription = context.getString(R.string.sol_pin_widget_cd)
                    },
                    shape = RoundedCornerShape(12.dp)
                ) {
                    Icon(Icons.Default.AddCircle, contentDescription = null)
                    Spacer(Modifier.width(8.dp))
                    Text(stringResource(R.string.sol_pin_widget))
                }
                Text(stringResource(R.string.sol_caption), fontSize = 12.sp, color = MaterialTheme.colorScheme.onSurfaceVariant)
            }
        }
        Text(dateLine, fontSize = 12.sp, color = MaterialTheme.colorScheme.onSurfaceVariant)
        Text(
            selected?.let { tapLine(context, it, now) } ?: stringResource(R.string.sol_hint),
            fontSize = 13.sp
        )
        Surface(
            modifier = Modifier.fillMaxWidth(),
            shape = RoundedCornerShape(12.dp),
            color = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.85f)
        ) {
            OverlayToggleLine(
                stringResource(R.string.astro_toggle_show_event_times),
                showEventTimes,
                { displayPrefs.setShowEventTimesSol(it) },
                modifier = Modifier.padding(horizontal = 12.dp, vertical = 2.dp),
            )
        }
    }
}

private fun tapLine(context: android.content.Context, body: PlanetBody, now: Instant): String {
    val st = PlanetKepler.state(body, now)
    val light = context.getString(R.string.sol_light_min, SolChrome.lightMin(st.au))
    val core = "${body.name} · ${"%.3f".format(st.au)} AU · ν ${st.nuDeg.toInt()}° · $light"
    if (body != PlanetBody.EARTH) return core
    val (peri, aph) = SolChrome.earthApsides(now)
    val df = DateTimeFormatter.ofPattern("MMM d", Locale.getDefault())
    val zone = ZoneId.systemDefault()
    val p = context.getString(R.string.sol_perihelion, df.format(peri.atZone(zone)))
    val a = context.getString(R.string.sol_aphelion, df.format(aph.atZone(zone)))
    return "$core · $p · $a"
}

private fun pinWidget(context: android.content.Context) {
    val mgr = context.getSystemService(AppWidgetManager::class.java)
    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O && mgr != null && mgr.isRequestPinAppWidgetSupported) {
        mgr.requestPinAppWidget(ComponentName(context, SolWidgetProvider::class.java), null, null)
        Toast.makeText(context, context.getString(R.string.astro_widget_pinned_success), Toast.LENGTH_SHORT).show()
    } else {
        Toast.makeText(context, context.getString(R.string.astro_widget_pin_manual_guide), Toast.LENGTH_LONG).show()
    }
}
