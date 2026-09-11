package org.astroalarm.astro.birth

import android.content.Context
import dev.foss.goldenpath.R
import org.astroalarm.astro.model.AlarmTarget
import org.astroalarm.astro.model.AstroAlarm
import org.astroalarm.astro.model.MercuryStationKind
import org.astroalarm.astro.model.NatalAspect
import java.util.UUID

data class AstroAlarmTemplate(
    val label: String,
    val alarm: AstroAlarm,
    val enabled: Boolean = true,
)

object AstroAlarmTemplates {
    fun forProfile(profile: BirthProfile, context: Context): List<AstroAlarmTemplate> {
        val id = profile.id
        val ascOk = profile.canComputeAscendant
        return listOf(
            AstroAlarmTemplate(
                label = context.getString(R.string.astro_natal_asc_conj_sun),
                enabled = ascOk,
                alarm = AstroAlarm(
                    id = UUID.randomUUID().toString(),
                    label = context.getString(R.string.astro_natal_asc_conj_sun),
                    target = AlarmTarget.NatalAscAspect(NatalBody.SUN, NatalAspect.Conjunction, id),
                ),
            ),
            AstroAlarmTemplate(
                label = context.getString(R.string.astro_natal_moon_return),
                alarm = AstroAlarm(
                    id = UUID.randomUUID().toString(),
                    label = context.getString(R.string.astro_natal_moon_return),
                    target = AlarmTarget.MoonReturn(id),
                ),
            ),
            AstroAlarmTemplate(
                label = context.getString(R.string.astro_natal_solar_return),
                alarm = AstroAlarm(
                    id = UUID.randomUUID().toString(),
                    label = context.getString(R.string.astro_natal_solar_return),
                    target = AlarmTarget.SolarReturn(id),
                ),
            ),
            AstroAlarmTemplate(
                label = context.getString(R.string.astro_natal_mercury_retro),
                alarm = AstroAlarm(
                    id = UUID.randomUUID().toString(),
                    label = context.getString(R.string.astro_natal_mercury_retro),
                    target = AlarmTarget.MercuryStation(MercuryStationKind.RetrogradeStart),
                ),
            ),
            AstroAlarmTemplate(
                label = context.getString(R.string.astro_natal_moon_rising_sign),
                enabled = ascOk,
                alarm = run {
                    val chart = BirthChartCalculator.compute(profile)
                    val sign = chart?.ascendant?.sign
                    AstroAlarm(
                        id = UUID.randomUUID().toString(),
                        label = context.getString(R.string.astro_natal_moon_rising_sign),
                        target = if (sign != null) {
                            AlarmTarget.MoonSignIngress(sign)
                        } else {
                            AlarmTarget.MoonReturn(id)
                        },
                        enabled = ascOk,
                    )
                },
            ),
            AstroAlarmTemplate(
                label = context.getString(R.string.astro_natal_compound_any_double),
                alarm = AstroAlarm(
                    id = UUID.randomUUID().toString(),
                    label = context.getString(R.string.astro_natal_compound_any_double),
                    target = AlarmTarget.NatalCompoundAny(2, id),
                ),
            ),
            AstroAlarmTemplate(
                label = context.getString(R.string.astro_natal_compound_any_triple),
                alarm = AstroAlarm(
                    id = UUID.randomUUID().toString(),
                    label = context.getString(R.string.astro_natal_compound_any_triple),
                    target = AlarmTarget.NatalCompoundAny(3, id),
                ),
            ),
            AstroAlarmTemplate(
                label = context.getString(R.string.astro_natal_compound_solar_moon),
                alarm = AstroAlarm(
                    id = UUID.randomUUID().toString(),
                    label = context.getString(R.string.astro_natal_compound_solar_moon),
                    target = AlarmTarget.NatalCompoundSpecific(
                        listOf(NatalStoryKind.SolarReturn.name, NatalStoryKind.MoonReturn.name),
                        id,
                    ),
                ),
            ),
        )
    }
}
