package org.astroalarm.ui

import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import dev.foss.goldenpath.R
import org.astroalarm.astro.place.AstroPlace
import org.astroalarm.astro.sky.TonightSky
import org.astroalarm.astro.sky.TonightSkyEvent
import java.time.Instant
import java.time.format.DateTimeFormatter
import java.time.format.FormatStyle

@Composable
fun TonightSkyCard(
    place: AstroPlace?,
    modifier: Modifier = Modifier,
    now: Instant = Instant.now(),
) {
    val event = remember(place, now) { place?.let { TonightSky.next(it, now) } }
    Card(
        modifier = modifier.fillMaxWidth().padding(vertical = 4.dp),
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceVariant),
    ) {
        Text(
            stringResource(R.string.astro_tonight_sky_title),
            style = MaterialTheme.typography.titleMedium,
            modifier = Modifier.padding(start = 16.dp, end = 16.dp, top = 16.dp),
        )
        if (place == null || !place.isValid || event == null) {
            Text(
                stringResource(R.string.astro_tonight_sky_need_place),
                style = MaterialTheme.typography.bodyMedium,
                modifier = Modifier.padding(start = 16.dp, end = 16.dp, top = 4.dp, bottom = 16.dp),
            )
        } else {
            val zone = place.zone
            val time = DateTimeFormatter.ofLocalizedTime(FormatStyle.SHORT)
                .withZone(zone)
                .format(event.at)
            Text(
                stringResource(eventTitleRes(event.kind), time),
                style = MaterialTheme.typography.bodyLarge,
                modifier = Modifier.padding(start = 16.dp, end = 16.dp, top = 4.dp),
            )
            Text(
                stringResource(contextRes(event.contextKey)),
                style = MaterialTheme.typography.bodySmall,
                color = MaterialTheme.colorScheme.onSurfaceVariant,
                modifier = Modifier.padding(start = 16.dp, end = 16.dp, top = 2.dp, bottom = 16.dp),
            )
        }
    }
}

private fun eventTitleRes(kind: TonightSkyEvent.Kind): Int = when (kind) {
    TonightSkyEvent.Kind.Sunrise -> R.string.astro_tonight_sky_sunrise
    TonightSkyEvent.Kind.Sunset -> R.string.astro_tonight_sky_sunset
    TonightSkyEvent.Kind.Moonrise -> R.string.astro_tonight_sky_moonrise
    TonightSkyEvent.Kind.Moonset -> R.string.astro_tonight_sky_moonset
}

private fun contextRes(key: String): Int = when (key) {
    "ctx_sunrise_today" -> R.string.astro_tonight_ctx_sunrise_today
    "ctx_sunrise_tomorrow" -> R.string.astro_tonight_ctx_sunrise_tomorrow
    "ctx_sunrise_later" -> R.string.astro_tonight_ctx_sunrise_later
    "ctx_sunset_today" -> R.string.astro_tonight_ctx_sunset_today
    "ctx_sunset_tomorrow" -> R.string.astro_tonight_ctx_sunset_tomorrow
    "ctx_sunset_later" -> R.string.astro_tonight_ctx_sunset_later
    "ctx_moonrise_today" -> R.string.astro_tonight_ctx_moonrise_today
    "ctx_moonrise_tomorrow" -> R.string.astro_tonight_ctx_moonrise_tomorrow
    "ctx_moonrise_later" -> R.string.astro_tonight_ctx_moonrise_later
    "ctx_moonset_today" -> R.string.astro_tonight_ctx_moonset_today
    "ctx_moonset_tomorrow" -> R.string.astro_tonight_ctx_moonset_tomorrow
    else -> R.string.astro_tonight_ctx_moonset_later
}
