package org.astroalarm.ui.birth

import android.appwidget.AppWidgetManager
import android.content.ComponentName
import android.os.Build
import android.widget.Toast
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.gestures.detectTapGestures
import androidx.compose.foundation.gestures.detectTransformGestures
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.BoxWithConstraints
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.AddCircle
import androidx.compose.material3.Button
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberUpdatedState
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clipToBounds
import androidx.compose.ui.graphics.drawscope.drawIntoCanvas
import androidx.compose.ui.graphics.nativeCanvas
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.semantics.contentDescription
import androidx.compose.ui.semantics.semantics
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import dev.foss.goldenpath.R
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import org.astroalarm.astro.birth.BirthProfile
import org.astroalarm.astro.birth.NatalAspectMath
import org.astroalarm.astro.birth.NatalBody
import org.astroalarm.astro.birth.NatalChart
import org.astroalarm.astro.birth.NatalCompoundNext
import org.astroalarm.astro.birth.NatalEventLinks
import org.astroalarm.astro.birth.NatalSkySnapshot
import org.astroalarm.astro.birth.NatalStoryKind
import org.astroalarm.astro.birth.NatalWheelHit
import org.astroalarm.astro.birth.NatalWheelHitTest
import org.astroalarm.astro.birth.NatalWheelRenderer
import org.astroalarm.ui.AstroMenuDropdown
import org.astroalarm.ui.DiskChrome
import org.astroalarm.ui.OverlayToggleLine
import org.astroalarm.ui.solarterm.WheelZoomPan
import org.astroalarm.ui.solarterm.WheelZoomPanMath
import org.astroalarm.widget.NatalChartWidgetProvider
import java.time.Instant
import java.time.ZoneId
import java.time.format.DateTimeFormatter
import java.util.Locale

@Composable
fun NatalWheelSection(
    profile: BirthProfile?,
    chart: NatalChart?,
    profiles: List<BirthProfile> = emptyList(),
    onSelectProfile: (String) -> Unit = {},
    onOpenDetails: () -> Unit = {},
    modifier: Modifier = Modifier,
) {
    val context = LocalContext.current
    val dark = isSystemInDarkTheme()
    var showSun by remember { mutableStateOf(true) }
    var showMoon by remember { mutableStateOf(true) }
    var showMercury by remember { mutableStateOf(true) }
    var focus by remember { mutableStateOf<NatalBody?>(null) }
    var explain by remember { mutableStateOf<NatalExplainCopy?>(null) }
    var viewport by remember { mutableStateOf(WheelZoomPan()) }
    val viewportLatest = rememberUpdatedState(viewport)
    val sky = remember { NatalSkySnapshot.now() }
    val events = remember(chart, sky, showSun, showMoon, showMercury) {
        if (chart == null) emptyList()
        else NatalEventLinks.active(chart, sky).filter {
            when (it.kind) {
                NatalStoryKind.AscSun, NatalStoryKind.SolarReturn -> showSun
                NatalStoryKind.AscMoon, NatalStoryKind.MoonReturn, NatalStoryKind.MoonRisingSign -> showMoon
                NatalStoryKind.AscMercury, NatalStoryKind.MercuryStation -> showMercury
            }
        }
    }
    var doubleLine by remember { mutableStateOf("") }
    var tripleLine by remember { mutableStateOf("") }
    val scanning = stringResource(R.string.astro_natal_compound_scanning)
    val noDouble = stringResource(R.string.astro_natal_no_double)
    val noTriple = stringResource(R.string.astro_natal_no_triple)
    val doubleFmt = stringResource(R.string.astro_natal_next_double)
    val tripleFmt = stringResource(R.string.astro_natal_next_triple)
    LaunchedEffect(chart) {
        if (chart == null) {
            doubleLine = ""
            tripleLine = ""
            return@LaunchedEffect
        }
        doubleLine = scanning
        tripleLine = scanning
        withContext(Dispatchers.Default) {
            val now = Instant.now()
            val zone = ZoneId.systemDefault()
            val fmt = DateTimeFormatter.ofPattern("EEE HH:mm", Locale.getDefault())
            val d = NatalCompoundNext.nextAny(chart, 2, now)
            val t = NatalCompoundNext.nextAny(chart, 3, now)
            doubleLine = d?.let {
                String.format(doubleFmt, NatalCompoundNext.labelKinds(it.kinds), fmt.format(it.at.atZone(zone)))
            } ?: noDouble
            tripleLine = t?.let {
                String.format(tripleFmt, NatalCompoundNext.labelKinds(it.kinds), fmt.format(it.at.atZone(zone)))
            } ?: noTriple
        }
    }
    val focusCaption = if (chart != null && focus != null) {
        NatalAspectMath.links(chart)
            .filter { it.a == focus || it.b == focus }
            .joinToString(" · ") { "${it.aspect.name} ${if (it.a == focus) it.b.name else it.a.name}" }
            .ifBlank { focus!!.name }
    } else {
        null
    }
    explain?.let { copy ->
        NatalGlyphExplainSheet(
            title = copy.title,
            body = copy.body,
            onDismiss = { explain = null },
        )
    }
    Column(
        modifier = modifier.fillMaxSize(),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.spacedBy(8.dp),
    ) {
        BoxWithConstraints(Modifier.fillMaxWidth().weight(1f), contentAlignment = Alignment.TopCenter) {
            val side = minOf(maxWidth, (maxHeight - DiskChrome.Reserve).coerceAtLeast(0.dp))
            Column(
                Modifier.fillMaxWidth().align(Alignment.TopCenter),
                horizontalAlignment = Alignment.CenterHorizontally,
                verticalArrangement = Arrangement.spacedBy(8.dp),
            ) {
                Canvas(
                    modifier = Modifier
                        .size(side)
                        .clipToBounds()
                        .pointerInput(Unit) {
                            detectTransformGestures { centroid, pan, zoom, _ ->
                                viewport = WheelZoomPanMath.apply(
                                    viewportLatest.value,
                                    centroid.x, centroid.y,
                                    pan.x, pan.y, zoom,
                                    size.width.toFloat(), size.height.toFloat(),
                                )
                            }
                        }
                        .pointerInput(chart, showSun, showMoon, showMercury) {
                            detectTapGestures { tap ->
                                if (chart == null) return@detectTapGestures
                                val (lx, ly) = WheelZoomPanMath.contentPoint(
                                    tap.x, tap.y, viewportLatest.value,
                                    size.width.toFloat(), size.height.toFloat(),
                                )
                                val hit = NatalWheelHitTest.at(
                                    lx, ly, size.width.coerceAtLeast(1), chart, sky,
                                    showSun, showMoon, showMercury,
                                )
                                if (hit != null) {
                                    if (hit is NatalWheelHit.NatalPlanet) focus = hit.body
                                    explain = NatalGlyphExplain.of(hit, context)
                                }
                            }
                        }
                        .semantics { contentDescription = context.getString(R.string.astro_natal_wheel_cd) },
                ) {
                    val px = size.width.toInt().coerceAtLeast(1)
                    drawIntoCanvas { gc ->
                        val native = gc.nativeCanvas
                        native.drawColor(if (dark) 0xFF121212.toInt() else 0xFFF5F5F5.toInt())
                        if (chart != null) {
                            native.save()
                            WheelZoomPanMath.concat(native, viewport, size.width)
                            NatalWheelRenderer.draw(
                                canvas = native,
                                size = px,
                                chart = chart,
                                sky = sky,
                                eventLinks = events,
                                showSun = showSun,
                                showMoon = showMoon,
                                showMercury = showMercury,
                                focusBody = focus,
                                dark = dark,
                            )
                            native.restore()
                        }
                    }
                }
                Text(
                    stringResource(R.string.astro_explain_tap_hint),
                    fontSize = 12.sp,
                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                )
                Button(
                    onClick = { pinNatalWidget(context) },
                    modifier = Modifier.fillMaxWidth().semantics {
                        contentDescription = context.getString(R.string.astro_natal_pin_widget_cd)
                    },
                    shape = RoundedCornerShape(12.dp),
                ) {
                    Icon(Icons.Default.AddCircle, contentDescription = null)
                    Spacer(Modifier.width(8.dp))
                    Text(stringResource(R.string.astro_natal_pin_widget))
                }
                if (chart == null) {
                    Text(
                        stringResource(R.string.astro_birth_empty),
                        fontSize = 13.sp,
                        color = MaterialTheme.colorScheme.onSurfaceVariant,
                    )
                } else {
                    if (focusCaption != null) Text(focusCaption, fontSize = 13.sp)
                    Text(doubleLine, fontSize = 12.sp, color = MaterialTheme.colorScheme.onSurfaceVariant)
                    Text(tripleLine, fontSize = 12.sp, color = MaterialTheme.colorScheme.onSurfaceVariant)
                }
            }
        }
        if (profiles.isNotEmpty()) {
            AstroMenuDropdown(
                label = stringResource(R.string.astro_birth_title),
                selectedText = profile?.label?.ifBlank { profile.id.take(6) } ?: "",
                options = profiles.map { p -> p.id to p.label.ifBlank { p.id.take(6) } },
                onSelect = onSelectProfile,
            )
        }
        OutlinedButton(
            onClick = onOpenDetails,
            modifier = Modifier.fillMaxWidth(),
            shape = RoundedCornerShape(12.dp),
        ) {
            Text(stringResource(R.string.astro_birth_details))
        }
        if (chart != null) {
            Surface(
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(12.dp),
                color = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.85f),
            ) {
                Column {
                    OverlayToggleLine("☉ Live Sun", showSun, onCheckedChange = { showSun = it })
                    OverlayToggleLine("☽ Live Moon", showMoon, onCheckedChange = { showMoon = it })
                    OverlayToggleLine("☿ Live Mercury", showMercury, onCheckedChange = { showMercury = it })
                }
            }
        }
    }
}

private fun pinNatalWidget(context: android.content.Context) {
    val mgr = context.getSystemService(AppWidgetManager::class.java)
    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O && mgr != null && mgr.isRequestPinAppWidgetSupported) {
        mgr.requestPinAppWidget(ComponentName(context, NatalChartWidgetProvider::class.java), null, null)
        Toast.makeText(context, context.getString(R.string.astro_widget_pinned_success), Toast.LENGTH_SHORT).show()
    } else {
        Toast.makeText(context, context.getString(R.string.astro_widget_pin_manual_guide), Toast.LENGTH_LONG).show()
    }
}
