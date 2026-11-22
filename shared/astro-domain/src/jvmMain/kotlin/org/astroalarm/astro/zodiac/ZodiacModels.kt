package org.astroalarm.astro.zodiac

enum class ZodiacSign(
    val englishName: String,
    val symbol: String,
    val startLongitudeDeg: Double
) {
    Aries("Aries", "♈", 0.0),
    Taurus("Taurus", "♉", 30.0),
    Gemini("Gemini", "♊", 60.0),
    Cancer("Cancer", "♋", 90.0),
    Leo("Leo", "♌", 120.0),
    Virgo("Virgo", "♍", 150.0),
    Libra("Libra", "♎", 180.0),
    Scorpio("Scorpio", "♏", 210.0),
    Sagittarius("Sagittarius", "♐", 240.0),
    Capricorn("Capricorn", "♑", 270.0),
    Aquarius("Aquarius", "♒", 300.0),
    Pisces("Pisces", "♓", 330.0);

    companion object {
        fun fromEclipticLongitude(deg: Double): ZodiacSign {
            var norm = deg % 360.0
            if (norm < 0.0) norm += 360.0
            val index = (norm / 30.0).toInt().coerceIn(0, 11)
            return entries[index]
        }
    }
}

enum class ZodiacPoint(
    val englishName: String,
    val degreeOffset: Double
) {
    Beginning("Beginning (0° Cusp)", 0.0),
    Middle("Middle (15° Peak)", 15.0),
    End("End (30° Transition)", 30.0)
}
