package org.astroalarm.math

import kotlin.random.Random

enum class MathDifficulty {
    ELEMENTARY,
    EASY,
    MEDIUM,
    HARD,
}

data class MathProblem(
    val expression: String,
    val answer: Int
)

object MathProblemGenerator {
    fun generate(difficulty: MathDifficulty, rng: Random = Random.Default): MathProblem {
        return when (difficulty) {
            MathDifficulty.ELEMENTARY -> {
                val a = rng.nextInt(0, 10)
                val b = rng.nextInt(0, 10)
                if (rng.nextBoolean()) {
                    MathProblem("$a + $b", a + b)
                } else {
                    val maxVal = maxOf(a, b)
                    val minVal = minOf(a, b)
                    MathProblem("$maxVal - $minVal", maxVal - minVal)
                }
            }
            MathDifficulty.EASY -> {
                val a = rng.nextInt(4, 25)
                val b = rng.nextInt(3, 20)
                if (rng.nextBoolean()) {
                    MathProblem("$a + $b", a + b)
                } else {
                    val maxVal = maxOf(a, b)
                    val minVal = minOf(a, b)
                    MathProblem("$maxVal - $minVal", maxVal - minVal)
                }
            }
            MathDifficulty.MEDIUM -> {
                val a = rng.nextInt(18, 75)
                val b = rng.nextInt(15, 65)
                if (rng.nextBoolean()) {
                    MathProblem("$a + $b", a + b)
                } else {
                    val maxVal = maxOf(a, b)
                    val minVal = minOf(a, b)
                    MathProblem("$maxVal - $minVal", maxVal - minVal)
                }
            }
            MathDifficulty.HARD -> {
                val a = rng.nextInt(6, 15)
                val b = rng.nextInt(4, 12)
                val c = rng.nextInt(10, 40)
                val product = a * b
                if (rng.nextBoolean()) {
                    MathProblem("$a × $b + $c", product + c)
                } else {
                    MathProblem("$a × $b - $c", product - c)
                }
            }
        }
    }
}
