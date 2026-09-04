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
import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.AddCircle
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.asImageBitmap
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
import org.astroalarm.widget.Astro3DClockWidgetProvider
import org.astroalarm.widget.Astro3DRenderer
import org.astroalarm.widget.ClockParallax
import org.astroalarm.widget.ClockRenderSize
import org.astroalarm.widget.EarthTexture
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
    var now by remember { mutableStateOf(Instant.now()) }
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
            val sizePx = ClockRenderSize.fromMinDp(side.value.toInt().coerceAtLeast(80))
            val bitmap3D = remember(
                place, alarms, now.epochSecond, sizePx, showZodiac, showEventTimes,
                (tiltX / 2f).toInt(), (tiltY / 2f).toInt(), earth
            ) {
                Astro3DRenderer.render3D(
                    place = place,
                    alarms = alarms,
                    now = now,
                    size = sizePx,
                    showZodiac = showZodiac,
                    showEventTimes = showEventTimes,
                    parallaxX = tiltX,
                    parallaxY = tiltY,
                    earth = earth,
                )
            }
            Column(
                Modifier.fillMaxWidth().align(Alignment.TopCenter),
                horizontalAlignment = Alignment.CenterHorizontally,
                verticalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                Image(
                    bitmap = bitmap3D.asImageBitmap(),
                    contentDescription = stringResource(R.string.astro_widget_3d_desc),
                    modifier = Modifier.size(side)
                )
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
                    stringResource(R.string.astro_toggle_show_zodiac_3d),
                    showZodiac,
                    { displayPrefs.setShowZodiac3D(it) },
                )
                OverlayToggleLine(
                    stringResource(R.string.astro_toggle_show_event_times),
                    showEventTimes,
                    { displayPrefs.setShowEventTimes3D(it) },
                )
            }
        }
    }
}
