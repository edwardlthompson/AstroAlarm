package org.astroalarm.math

import kotlin.random.Random

enum class MathDifficulty {
    EASY,
    MEDIUM,
    HARD,
    GENIUS
}

data class MathProblem(
    val expression: String,
    val answer: Int
)

object MathProblemGenerator {
    fun generate(difficulty: MathDifficulty): MathProblem {
        return when (difficulty) {
            MathDifficulty.EASY -> {
                val a = Random.nextInt(4, 25)
                val b = Random.nextInt(3, 20)
                if (Random.nextBoolean()) {
                    MathProblem("$a + $b", a + b)
                } else {
                    val maxVal = maxOf(a, b)
                    val minVal = minOf(a, b)
                    MathProblem("$maxVal - $minVal", maxVal - minVal)
                }
            }
            MathDifficulty.MEDIUM -> {
                val a = Random.nextInt(18, 75)
                val b = Random.nextInt(15, 65)
                if (Random.nextBoolean()) {
                    MathProblem("$a + $b", a + b)
                } else {
                    val maxVal = maxOf(a, b)
                    val minVal = minOf(a, b)
                    MathProblem("$maxVal - $minVal", maxVal - minVal)
                }
            }
            MathDifficulty.HARD -> {
                val a = Random.nextInt(6, 15)
                val b = Random.nextInt(4, 12)
                val c = Random.nextInt(10, 40)
                val product = a * b
                if (Random.nextBoolean()) {
                    MathProblem("$a × $b + $c", product + c)
                } else {
                    MathProblem("$a × $b - $c", product - c)
                }
            }
            MathDifficulty.GENIUS -> {
                val a = Random.nextInt(11, 22)
                val b = Random.nextInt(8, 16)
                val c = Random.nextInt(25, 80)
                val product = a * b
                if (Random.nextBoolean()) {
                    MathProblem("($a × $b) - $c", product - c)
                } else {
                    val d = Random.nextInt(12, 35)
                    MathProblem("($a + $d) × $b", (a + d) * b)
                }
            }
        }
    }
}
