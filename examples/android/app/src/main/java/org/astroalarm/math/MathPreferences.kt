package org.astroalarm.math

import android.content.Context
import android.content.SharedPreferences
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow

class MathPreferences(context: Context) {
    private val prefs: SharedPreferences =
        context.applicationContext.getSharedPreferences("astro_math_prefs", Context.MODE_PRIVATE)

    private val _difficulty = MutableStateFlow(loadDifficulty())
    val difficulty: StateFlow<MathDifficulty> = _difficulty.asStateFlow()

    private val _problemCount = MutableStateFlow(loadProblemCount())
    val problemCount: StateFlow<Int> = _problemCount.asStateFlow()

    fun getDifficulty(): MathDifficulty = _difficulty.value

    fun setDifficulty(diff: MathDifficulty) {
        prefs.edit().putString(KEY_DIFFICULTY, diff.name).commit()
        _difficulty.value = diff
    }

    fun getProblemCount(): Int = _problemCount.value

    fun setProblemCount(count: Int) {
        val clamped = count.coerceIn(1, 5)
        prefs.edit().putInt(KEY_PROBLEM_COUNT, clamped).commit()
        _problemCount.value = clamped
    }

    private fun loadDifficulty(): MathDifficulty {
        val name = prefs.getString(KEY_DIFFICULTY, MathDifficulty.MEDIUM.name)
        if (name == "GENIUS") return MathDifficulty.HARD
        return runCatching { MathDifficulty.valueOf(name ?: MathDifficulty.MEDIUM.name) }
            .getOrDefault(MathDifficulty.MEDIUM)
    }

    private fun loadProblemCount(): Int {
        return prefs.getInt(KEY_PROBLEM_COUNT, 1).coerceIn(1, 5)
    }

    companion object {
        private const val KEY_DIFFICULTY = "math_difficulty"
        private const val KEY_PROBLEM_COUNT = "math_problem_count"
    }
}
