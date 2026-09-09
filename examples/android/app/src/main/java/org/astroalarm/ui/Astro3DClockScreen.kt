package org.astroalarm.ui

import android.appwidget.AppWidgetManager
import android.content.ComponentName
import android.content.Context
import android.hardware.Sensor
import android.hardware.SensorEvent
import android.hardware.SensorEventListener
import android.hardware.SensorManager
import android.os.Build
import android.widget.Toast
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.gestures.detectTransformGestures
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.AddCircle
import androidx.compose.material3.*
import androidx.compose.runtime.*
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
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import dev.foss.goldenpath.R
import org.astroalarm.astro.model.AstroAlarm
import org.astroalarm.astro.place.AstroPlace
import org.astroalarm.astro.settings.AstroDisplayPreferences
import org.astroalarm.astro.zodiac.ZodiacCalculator
import org.astroalarm.ui.solarterm.WheelZoomPan
import org.astroalarm.ui.solarterm.WheelZoomPanMath
import org.astroalarm.widget.Astro3DClockWidgetProvider
import org.astroalarm.widget.Astro3DRenderer
import org.astroalarm.widget.ClockParallax
import org.astroalarm.widget.EarthTexture
import org.astroalarm.widget.MoonTexture
import java.time.Instant

@Composable
fun Astro3DClockScreen(
    place: AstroPlace?,
    alarms: List<AstroAlarm>,
    modifier: Modifier = Modifier
) {
    val context = LocalContext.current
    val displayPrefs = remember { AstroDisplayPreferences(context) }
    val showZodiac by displayPrefs.showZodiac3D.collectAsState()
    val showEventTimes by displayPrefs.showEventTimes3D.collectAsState()
    val earth = remember { EarthTexture.get(context) }
    val moon = remember { MoonTexture.get(context) }
    var now by remember { mutableStateOf(Instant.now()) }
    var viewport by remember { mutableStateOf(WheelZoomPan()) }
    val viewportLatest = rememberUpdatedState(viewport)
    var tiltX by remember { mutableFloatStateOf(0f) }
    var tiltY by remember { mutableFloatStateOf(0f) }

    DisposableEffect(Unit) {
        val sensorManager = context.getSystemService(Context.SENSOR_SERVICE) as? SensorManager
        val accelerometer = sensorManager?.getDefaultSensor(Sensor.TYPE_ACCELEROMETER)
        val listener = object : SensorEventListener {
            override fun onSensorChanged(event: SensorEvent?) {
                if (event != null) {
                    val (px, py) = ClockParallax.fromAccelerometer(event.values[0], event.values[2])
                    tiltX = px
                    tiltY = py
                }
            }
            override fun onAccuracyChanged(sensor: Sensor?, accuracy: Int) {}
        }
        if (sensorManager != null && accelerometer != null) {
            sensorManager.registerListener(listener, accelerometer, SensorManager.SENSOR_DELAY_UI)
        }
        onDispose { sensorManager?.unregisterListener(listener) }
    }

    LaunchedEffect(Unit) {
        while (true) {
            kotlinx.coroutines.delay(1000L)
            now = Instant.now()
        }
    }

    val sunLon = ZodiacCalculator.sunLongitudeAt(now)
    val middaySign = ZodiacCalculator.overheadMiddayZodiac(now)

    Column(
        modifier = modifier.fillMaxSize().padding(horizontal = 16.dp, vertical = 6.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.spacedBy(8.dp)
    ) {
        BoxWithConstraints(
            modifier = Modifier.fillMaxWidth().weight(1f),
            contentAlignment = Alignment.TopCenter
        ) {
            val side = minOf(maxWidth, (maxHeight - DiskChrome.Reserve).coerceAtLeast(0.dp))
            Column(
                Modifier.fillMaxWidth().align(Alignment.TopCenter),
                horizontalAlignment = Alignment.CenterHorizontally,
                verticalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                Canvas(
                    modifier = Modifier
                        .size(side)
                        .clipToBounds()
                        .pointerInput(Unit) {
                            detectTransformGestures { centroid, pan, zoom, _ ->
                                viewport = WheelZoomPanMath.apply(
                                    viewportLatest.value,
                                    centroid.x, centroid.y, pan.x, pan.y, zoom,
                                    size.width.toFloat(), size.height.toFloat(),
                                )
                            }
                        }
                        .semantics { contentDescription = context.getString(R.string.astro_widget_3d_desc) }
                ) {
                    val px = size.width.toInt().coerceAtLeast(1)
                    drawIntoCanvas { gc ->
                        val native = gc.nativeCanvas
                        native.save()
                        WheelZoomPanMath.concat(native, viewport, size.width)
                        Astro3DRenderer.draw(
                            native, place, alarms, now, px, showZodiac, showEventTimes, tiltX, tiltY, earth, moon,
                        )
                        native.restore()
                    }
                }
                Button(
                    onClick = {
                        val mgr = context.getSystemService(AppWidgetManager::class.java)
                        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O && mgr != null && mgr.isRequestPinAppWidgetSupported) {
                            mgr.requestPinAppWidget(ComponentName(context, Astro3DClockWidgetProvider::class.java), null, null)
                            Toast.makeText(context, context.getString(R.string.astro_widget_pinned_success), Toast.LENGTH_SHORT).show()
                        } else {
                            Toast.makeText(context, context.getString(R.string.astro_widget_pin_manual_guide), Toast.LENGTH_LONG).show()
                        }
                    },
                    modifier = Modifier.fillMaxWidth().semantics {
                        contentDescription = context.getString(R.string.astro_add_3d_widget_cd)
                    },
                    shape = RoundedCornerShape(12.dp)
                ) {
                    Icon(Icons.Default.AddCircle, contentDescription = null)
                    Spacer(modifier = Modifier.width(8.dp))
                    Text(stringResource(R.string.astro_add_3d_widget_btn))
                }
                Text(
                    text = place?.let {
                        String.format(java.util.Locale.getDefault(), "🌐 %.2f°, %.2f°   %s %s   ✨ %.1f°", it.latitude, it.longitude, middaySign.symbol, middaySign.englishName, sunLon)
                    } ?: String.format(java.util.Locale.getDefault(), "%s %s   ✨ %.1f°", middaySign.symbol, middaySign.englishName, sunLon),
                    fontSize = 13.sp,
                    fontWeight = FontWeight.Medium,
                    maxLines = 2
                )
            }
        }

        Surface(
            modifier = Modifier.fillMaxWidth(),
            shape = RoundedCornerShape(12.dp),
            color = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.85f)
        ) {
            Column(Modifier.padding(horizontal = 12.dp, vertical = 2.dp)) {
                OverlayToggleLine(
                    stringResource(R.string.astro_toggle_show_event_times),
                    showEventTimes,
                    { displayPrefs.setShowEventTimes3D(it) },
                )
                OverlayToggleLine(
                    stringResource(R.string.astro_toggle_show_zodiac_3d),
                    showZodiac,
                    { displayPrefs.setShowZodiac3D(it) },
                )
            }
        }
    }
}
