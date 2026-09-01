package org.astroalarm.solarterm

/** 24 Solar Terms (二十四节气) at 15° steps of apparent geocentric ecliptic longitude. */
enum class SolarTerm(
    val index: Int,
    val longitudeDeg: Double,
    val typicalMonth: Int,
    val hans: String,
    val hant: String,
    val pinyin: String,
    val glyph: String,
    val season: SolarSeason,
) {
    LICHUN(1, 315.0, 2, "立春", "立春", "Lìchūn", "🌱", SolarSeason.SPRING),
    YUSHUI(2, 330.0, 2, "雨水", "雨水", "Yǔshuǐ", "💧", SolarSeason.SPRING),
    JINGZHE(3, 345.0, 3, "惊蛰", "驚蟄", "Jīngzhé", "🐛", SolarSeason.SPRING),
    CHUNFEN(4, 0.0, 3, "春分", "春分", "Chūnfēn", "☀️", SolarSeason.SPRING),
    QINGMING(5, 15.0, 4, "清明", "清明", "Qīngmíng", "🌸", SolarSeason.SPRING),
    GUYU(6, 30.0, 4, "谷雨", "穀雨", "Gǔyǔ", "🌾", SolarSeason.SPRING),
    LIXIA(7, 45.0, 5, "立夏", "立夏", "Lìxià", "🌞", SolarSeason.SUMMER),
    XIAOMAN(8, 60.0, 5, "小满", "小滿", "Xiǎomǎn", "🌿", SolarSeason.SUMMER),
    MANGZHONG(9, 75.0, 6, "芒种", "芒種", "Mángzhòng", "🌾", SolarSeason.SUMMER),
    XIAZHI(10, 90.0, 6, "夏至", "夏至", "Xiàzhì", "🔆", SolarSeason.SUMMER),
    XIAOSHU(11, 105.0, 7, "小暑", "小暑", "Xiǎoshǔ", "🔥", SolarSeason.SUMMER),
    DASHU(12, 120.0, 7, "大暑", "大暑", "Dàshǔ", "🌡️", SolarSeason.SUMMER),
    LIQIU(13, 135.0, 8, "立秋", "立秋", "Lìqiū", "🍂", SolarSeason.AUTUMN),
    CHUSHU(14, 150.0, 8, "处暑", "處暑", "Chǔshǔ", "🌬️", SolarSeason.AUTUMN),
    BAILU(15, 165.0, 9, "白露", "白露", "Báilù", "💧", SolarSeason.AUTUMN),
    QIUFEN(16, 180.0, 9, "秋分", "秋分", "Qiūfēn", "⚖️", SolarSeason.AUTUMN),
    HANLU(17, 195.0, 10, "寒露", "寒露", "Hánlù", "🍁", SolarSeason.AUTUMN),
    SHUANGJIANG(18, 210.0, 10, "霜降", "霜降", "Shuāngjiàng", "❄️", SolarSeason.AUTUMN),
    LIDONG(19, 225.0, 11, "立冬", "立冬", "Lìdōng", "🌨️", SolarSeason.WINTER),
    XIAOXUE(20, 240.0, 11, "小雪", "小雪", "Xiǎoxuě", "❄️", SolarSeason.WINTER),
    DAXUE(21, 255.0, 12, "大雪", "大雪", "Dàxuě", "⛄", SolarSeason.WINTER),
    DONGZHI(22, 270.0, 12, "冬至", "冬至", "Dōngzhì", "🌑", SolarSeason.WINTER),
    XIAOHAN(23, 285.0, 1, "小寒", "小寒", "Xiǎohán", "🧊", SolarSeason.WINTER),
    DAHAN(24, 300.0, 1, "大寒", "大寒", "Dàhán", "🥶", SolarSeason.WINTER);

    fun hanzi(traditional: Boolean): String = if (traditional) hant else hans

    fun next(): SolarTerm = entries[(ordinal + 1) % entries.size]

    /** Names/colors only: southern “local seasons” shift by half a year. Longitudes stay astronomical. */
    fun localAlias(localSeasons: Boolean, southern: Boolean): SolarTerm =
        if (localSeasons && southern) entries[(ordinal + 12) % entries.size] else this

    companion object {
        fun containing(longitudeDeg: Double): SolarTerm {
            val lon = wrap360(longitudeDeg)
            return entries.first { wrap360(lon - it.longitudeDeg) < 15.0 }
        }
    }
}

enum class SolarSeason { SPRING, SUMMER, AUTUMN, WINTER }

internal fun wrap360(deg: Double): Double {
    var d = deg % 360.0
    if (d < 0.0) d += 360.0
    return d
}
