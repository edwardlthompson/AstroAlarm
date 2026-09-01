package org.astroalarm.ui.solarterm

import androidx.compose.foundation.Canvas
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.ModalBottomSheet
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.semantics.contentDescription
import androidx.compose.ui.semantics.semantics
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import dev.foss.goldenpath.R
import org.astroalarm.solarterm.SolarTerm
import java.time.Instant
import java.time.ZoneId
import java.time.ZonedDateTime

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun SolarTermDetailSheet(
    term: SolarTerm,
    english: String,
    hanzi: String,
    whenLocal: String,
    description: String,
    instant: Instant,
    zone: ZoneId,
    onDismiss: () -> Unit,
) {
    val doy = ZonedDateTime.ofInstant(instant, zone).dayOfYear
    val talk = "$hanzi ${term.pinyin}, $english, $whenLocal"
    ModalBottomSheet(onDismissRequest = onDismiss) {
        androidx.compose.foundation.layout.Column(
            modifier = Modifier
                .fillMaxWidth()
                .verticalScroll(rememberScrollState())
                .padding(horizontal = 20.dp, vertical = 8.dp)
                .semantics { contentDescription = talk }
        ) {
            Text("$hanzi  ${term.glyph}", style = MaterialTheme.typography.headlineSmall)
            Text(term.pinyin, style = MaterialTheme.typography.titleMedium, color = MaterialTheme.colorScheme.primary)
            Text(english, style = MaterialTheme.typography.titleMedium)
            Text(
                stringResource(R.string.solar_term_longitude, term.longitudeDeg),
                style = MaterialTheme.typography.bodySmall,
                modifier = Modifier.padding(top = 8.dp),
            )
            Text(stringResource(R.string.solar_term_local_datetime), fontSize = 12.sp, modifier = Modifier.padding(top = 8.dp))
            Text(whenLocal, style = MaterialTheme.typography.bodyLarge)
            Text(stringResource(R.string.solar_term_phenology), fontSize = 12.sp, modifier = Modifier.padding(top = 12.dp))
            Text(description, style = MaterialTheme.typography.bodyMedium)
            Text(stringResource(R.string.solar_term_timeline), fontSize = 12.sp, modifier = Modifier.padding(top = 16.dp))
            YearDayBar(dayOfYear = doy)
            androidx.compose.foundation.layout.Spacer(Modifier.height(24.dp))
        }
    }
}

@Composable
private fun YearDayBar(dayOfYear: Int, modifier: Modifier = Modifier) {
    val scheme = MaterialTheme.colorScheme
    Canvas(modifier.fillMaxWidth().height(18.dp).padding(vertical = 4.dp)) {
        val y = size.height / 2f
        drawLine(scheme.outlineVariant, Offset(0f, y), Offset(size.width, y), size.height * 0.35f, StrokeCap.Round)
        val x = size.width * ((dayOfYear - 1) / 365f).coerceIn(0f, 1f)
        drawCircle(Color(0xFFE53935), radius = size.height * 0.42f, center = Offset(x, y))
    }
}
