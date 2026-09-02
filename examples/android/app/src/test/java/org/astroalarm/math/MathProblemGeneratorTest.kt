package org.astroalarm.math

import org.junit.Assert.assertNotNull
import org.junit.Assert.assertTrue
import org.junit.Test
import kotlin.random.Random

class MathProblemGeneratorTest {

    @Test
    fun generatesValidElementaryProblems() {
        repeat(50) {
            val problem = MathProblemGenerator.generate(MathDifficulty.ELEMENTARY, Random(it))
            assertNotNull(problem.expression)
            assertTrue(problem.expression.contains("+") || problem.expression.contains("-"))
            assertTrue(problem.answer >= 0)
            val nums = problem.expression.split(" + ", " - ").map { n -> n.trim().toInt() }
            assertTrue(nums.all { n -> n in 0..9 })
        }
    }

    @Test
    fun generatesValidEasyProblems() {
        repeat(50) {
            val problem = MathProblemGenerator.generate(MathDifficulty.EASY)
            assertNotNull(problem.expression)
            assertTrue(problem.expression.contains("+") || problem.expression.contains("-"))
            assertTrue(problem.answer >= 0)
        }
    }

    @Test
    fun generatesValidMediumProblems() {
        repeat(50) {
            val problem = MathProblemGenerator.generate(MathDifficulty.MEDIUM)
            assertNotNull(problem.expression)
            assertTrue(problem.expression.contains("+") || problem.expression.contains("-"))
        }
    }

    @Test
    fun generatesValidHardProblems() {
        repeat(50) {
            val problem = MathProblemGenerator.generate(MathDifficulty.HARD)
            assertNotNull(problem.expression)
            assertTrue(problem.expression.contains("×"))
        }
    }
}
