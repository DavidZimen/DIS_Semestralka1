package fri.uniza.semestralka1.generator

import org.junit.jupiter.api.Assertions.*
import org.junit.jupiter.api.Test

class GeneratorTest {

    @Test
    fun shouldNotCreateGenerator() {
        var exception = assertThrows(IllegalStateException::class.java) {
            ContinuousEmpiricalGenerator(
                IntervalProbability(2.0, 3.0, 0.5),
                IntervalProbability(3.0, 4.0, 0.2)
            )
        }
        assertEquals(exception.message, GeneratorMessage.PROBABILITY_MSG)

        exception = assertThrows(IllegalStateException::class.java) {
            ContinuousEmpiricalGenerator(
                IntervalProbability(3.0, 2.0, 1.0)
            )
        }
        assertTrue(exception.message!!.contains(GeneratorMessage.INTERVAL_EDGES_MSG))

        exception = assertThrows(IllegalStateException::class.java) {
            ContinuousEmpiricalGenerator(
                IntervalProbability(2.0, 3.0, 0.5),
                IntervalProbability(4.0, 5.0, 0.5)
            )
        }
        assertEquals(exception.message, GeneratorMessage.OVERLAP_MSG)

        exception = assertThrows(IllegalStateException::class.java) {
            ContinuousEmpiricalGenerator(
                IntervalProbability(2.0, 3.0, 0.5),
                IntervalProbability(2.5, 5.0, 0.5)
            )
        }
        assertEquals(exception.message, GeneratorMessage.OVERLAP_MSG)
    }

    @Test
    fun shouldCreateGenerator() {
        // wrongly ordered
        assertDoesNotThrow {
            ContinuousEmpiricalGenerator(
                IntervalProbability(3.0, 4.0, 0.5),
                IntervalProbability(1.0, 2.0, 0.2),
                IntervalProbability(0.0, 1.0, 0.2),
                IntervalProbability(2.0, 3.0, 0.1)
            )
        }
    }
}