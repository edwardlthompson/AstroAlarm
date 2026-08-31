package org.astroalarm.math

import org.junit.Assert.assertNotNull
import org.junit.Assert.assertTrue
import org.junit.Test

class MathProblemGeneratorTest {

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

    @Test
    fun generatesValidGeniusProblems() {
        repeat(50) {
            val problem = MathProblemGenerator.generate(MathDifficulty.GENIUS)
            assertNotNull(problem.expression)
            assertTrue(problem.expression.contains("(") && problem.expression.contains(")"))
        }
    }
}
