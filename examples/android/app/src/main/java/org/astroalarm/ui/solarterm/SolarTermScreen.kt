package org.astroalarm.ui.solarterm

import android.appwidget.AppWidgetManager
import android.content.ComponentName
import android.os.Build
import android.widget.Toast
import androidx.compose.animation.AnimatedContent
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
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.semantics.contentDescription
import androidx.compose.ui.semantics.semantics
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import dev.foss.goldenpath.R
import kotlinx.coroutines.delay
import org.astroalarm.astro.place.AstroPlace
import org.astroalarm.astro.settings.AstroDisplayPreferences
import org.astroalarm.solarterm.SolarTerm
import org.astroalarm.widget.EarthTexture
import org.astroalarm.widget.SolarTermWidgetProvider
import java.time.Instant

@Composable
fun SolarTermScreen(place: AstroPlace?, modifier: Modifier = Modifier) {
    val context = LocalContext.current
    val prefs = remember { AstroDisplayPreferences(context) }
    val traditional by prefs.solarTermTraditional.collectAsState()
    val localSeasons by prefs.solarTermLocalSeasons.collectAsState()
    var now by remember { mutableStateOf(Instant.now()) }
    var mode3d by remember { mutableStateOf(false) }
    var yaw by remember { mutableFloatStateOf(0f) }
    var scale by remember { mutableFloatStateOf(1f) }
    var selected by remember { mutableStateOf<SolarTerm?>(null) }
    val dark = isSystemInDarkTheme()
    val earth = remember { EarthTexture.get(context) }

    LaunchedEffect(Unit) {
        while (true) {
            delay(30_000L)
            now = Instant.now()
        }
    }
    LaunchedEffect(mode3d) {
        if (!mode3d) return@LaunchedEffect
        while (true) {
            delay(40L)
            yaw = (yaw + 0.25f) % 360f
        }
    }

    val (snap, req) = remember(place, now.epochSecond / 60, traditional, localSeasons, dark) {
        SolarTermDrawFactory.request(context.resources, place, now, traditional, localSeasons, dark, false)
    }
    val talk = stringResource(R.string.solar_term_cd_wheel) + ". " +
        SolarTermFormat.nextGlance(context.resources, snap.next, SolarTermFormat.zoneOf(place), traditional, SolarTermFormat.southern(place), localSeasons)

    Column(
        modifier = modifier.fillMaxSize().padding(horizontal = 12.dp, vertical = 6.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.spacedBy(6.dp)
    ) {
        Text(req.locationLabel, fontSize = 12.sp, color = MaterialTheme.colorScheme.onSurfaceVariant)
        Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
            FilterChip(selected = !mode3d, onClick = { mode3d = false }, label = { Text(stringResource(R.string.solar_term_view_2d)) })
            FilterChip(selected = mode3d, onClick = { mode3d = true }, label = { Text(stringResource(R.string.solar_term_view_3d)) })
        }
        BoxWithConstraints(Modifier.fillMaxWidth().weight(1f), contentAlignment = Alignment.Center) {
            val side = minOf(maxWidth, maxHeight)
            val sizePx = org.astroalarm.widget.ClockRenderSize.fromMinDp(side.value.toInt().coerceAtLeast(80))
            AnimatedContent(targetState = mode3d, label = "solar-term-mode") { three ->
                val bmp = remember(req, sizePx, three, yaw.toInt() / 2, earth) {
                    runCatching {
                        if (three) SolarTerm3DRenderer.render(req, sizePx, yaw, earth, place?.latitude ?: 40.0, place?.longitude ?: 0.0)
                        else SolarTermWheelRenderer.render(req, sizePx)
                    }.getOrElse { SolarTermWheelRenderer.render(req, sizePx) }
                }
                Image(
                    bitmap = bmp.asImageBitmap(),
                    contentDescription = talk,
                    modifier = Modifier
                        .size(side)
                        .graphicsLayer { scaleX = scale; scaleY = scale }
                        .semantics { contentDescription = talk }
                        .pointerInput(sizePx, three, yaw) {
                            detectTapGestures { tap ->
                                val bx = tap.x * sizePx / size.width
                                val by = tap.y * sizePx / size.height
                                val idx = if (three) SolarTerm3DRenderer.markerAt(bx, by, sizePx, yaw)
                                else SolarTermWheelRenderer.sectorAt(bx, by, sizePx)
                                if (idx != null) selected = SolarTerm.entries[idx]
                            }
                        }
                        .pointerInput(three) {
                            detectTransformGestures { _, pan, zoom, _ ->
                                if (three) yaw = (yaw + pan.x * 0.2f) % 360f
                                else scale = (scale * zoom).coerceIn(1f, 2.4f)
                            }
                        }
                )
            }
        }
        Button(
            onClick = { pinWidget(context) },
            modifier = Modifier.fillMaxWidth(),
            shape = RoundedCornerShape(12.dp)
        ) {
            Icon(Icons.Default.AddCircle, contentDescription = null)
            Spacer(Modifier.width(8.dp))
            Text(stringResource(R.string.solar_term_pin_widget))
        }
    }

    selected?.let { term ->
        val shown = term.localAlias(localSeasons, SolarTermFormat.southern(place))
        val occ = snap.year.occurrences.firstOrNull { it.term == term } ?: snap.current
        SolarTermDetailSheet(
            term = shown,
            english = SolarTermCopy.name(context.resources, shown),
            hanzi = shown.hanzi(traditional),
            whenLocal = SolarTermFormat.localStamp(occ.utc, SolarTermFormat.zoneOf(place)),
            description = SolarTermCopy.desc(context.resources, shown),
            instant = occ.utc,
            zone = SolarTermFormat.zoneOf(place),
            onDismiss = { selected = null },
        )
    }
}

private fun pinWidget(context: android.content.Context) {
    val mgr = context.getSystemService(AppWidgetManager::class.java)
    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O && mgr != null && mgr.isRequestPinAppWidgetSupported) {
        mgr.requestPinAppWidget(ComponentName(context, SolarTermWidgetProvider::class.java), null, null)
        Toast.makeText(context, context.getString(R.string.astro_widget_pinned_success), Toast.LENGTH_SHORT).show()
    } else {
        Toast.makeText(context, context.getString(R.string.astro_widget_pin_manual_guide), Toast.LENGTH_LONG).show()
    }
}
