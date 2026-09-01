package org.astroalarm.ui.solarterm

import android.content.res.Resources
import dev.foss.goldenpath.R
import org.astroalarm.solarterm.SolarTerm

object SolarTermCopy {
    private val NAMES = intArrayOf(
        R.string.solar_term_lichun, R.string.solar_term_yushui, R.string.solar_term_jingzhe,
        R.string.solar_term_chunfen, R.string.solar_term_qingming, R.string.solar_term_guyu,
        R.string.solar_term_lixia, R.string.solar_term_xiaoman, R.string.solar_term_mangzhong,
        R.string.solar_term_xiazhi, R.string.solar_term_xiaoshu, R.string.solar_term_dashu,
        R.string.solar_term_liqiu, R.string.solar_term_chushu, R.string.solar_term_bailu,
        R.string.solar_term_qiufen, R.string.solar_term_hanlu, R.string.solar_term_shuangjiang,
        R.string.solar_term_lidong, R.string.solar_term_xiaoxue, R.string.solar_term_daxue,
        R.string.solar_term_dongzhi, R.string.solar_term_xiaohan, R.string.solar_term_dahan,
    )
    private val DESCS = intArrayOf(
        R.string.solar_term_desc_lichun, R.string.solar_term_desc_yushui, R.string.solar_term_desc_jingzhe,
        R.string.solar_term_desc_chunfen, R.string.solar_term_desc_qingming, R.string.solar_term_desc_guyu,
        R.string.solar_term_desc_lixia, R.string.solar_term_desc_xiaoman, R.string.solar_term_desc_mangzhong,
        R.string.solar_term_desc_xiazhi, R.string.solar_term_desc_xiaoshu, R.string.solar_term_desc_dashu,
        R.string.solar_term_desc_liqiu, R.string.solar_term_desc_chushu, R.string.solar_term_desc_bailu,
        R.string.solar_term_desc_qiufen, R.string.solar_term_desc_hanlu, R.string.solar_term_desc_shuangjiang,
        R.string.solar_term_desc_lidong, R.string.solar_term_desc_xiaoxue, R.string.solar_term_desc_daxue,
        R.string.solar_term_desc_dongzhi, R.string.solar_term_desc_xiaohan, R.string.solar_term_desc_dahan,
    )

    fun nameRes(term: SolarTerm): Int = NAMES[term.ordinal]
    fun descRes(term: SolarTerm): Int = DESCS[term.ordinal]
    fun name(res: Resources, term: SolarTerm): String = res.getString(nameRes(term))
    fun desc(res: Resources, term: SolarTerm): String = res.getString(descRes(term))
}
