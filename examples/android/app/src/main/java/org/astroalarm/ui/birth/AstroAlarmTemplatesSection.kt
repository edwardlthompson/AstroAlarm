package org.astroalarm.ui.birth

import android.widget.Toast
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.material3.Button
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import dev.foss.goldenpath.R
import org.astroalarm.astro.alarm.AstroAlarmScheduler
import org.astroalarm.astro.alarm.AstroAlarmStore
import org.astroalarm.astro.birth.AstroAlarmTemplates
import org.astroalarm.astro.birth.BirthProfile

@Composable
fun AstroAlarmTemplatesSection(
    profile: BirthProfile?,
    alarmStore: AstroAlarmStore,
) {
    val context = LocalContext.current
    Column(verticalArrangement = Arrangement.spacedBy(8.dp), modifier = Modifier.fillMaxWidth()) {
        Text(
            text = stringResource(R.string.astro_birth_templates),
            style = MaterialTheme.typography.titleMedium,
            fontWeight = FontWeight.SemiBold,
        )
        if (profile == null) {
            Text(stringResource(R.string.astro_natal_need_profile), color = MaterialTheme.colorScheme.onSurfaceVariant)
            return
        }
        AstroAlarmTemplates.forProfile(profile, context).forEach { template ->
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically,
            ) {
                Text(template.label, modifier = Modifier.weight(1f))
                Button(
                    onClick = {
                        alarmStore.save(template.alarm)
                        AstroAlarmScheduler.rescheduleAll(context)
                        Toast.makeText(context, context.getString(R.string.astro_birth_template_added), Toast.LENGTH_SHORT).show()
                    },
                    enabled = template.enabled,
                ) {
                    Text(stringResource(R.string.astro_birth_add_template))
                }
            }
        }
    }
}
