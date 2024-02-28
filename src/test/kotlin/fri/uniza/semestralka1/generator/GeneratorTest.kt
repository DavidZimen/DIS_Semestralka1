package fri.uniza.semestralka1.generator

import fri.uniza.semestralka1.general_utils.writeToCsv
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

    @Test
    fun generateNumbersToFile() {
        // initialize generator
        val generator = ContinuousEmpiricalGenerator(
            IntervalProbability(0.1, 0.3, 0.1),
            IntervalProbability(0.3, 0.8, 0.5),
            IntervalProbability(0.8, 1.2, 0.15),
            IntervalProbability(1.2, 2.5, 0.1),
            IntervalProbability(2.5, 3.8, 0.1),
            IntervalProbability(3.8, 4.8, 0.05)
        )

        // generate data
        val replications = 1_000_000
        val data = mutableListOf<Double>()
        for (i in 0 until replications) {
            data.add(generator.sample())
        }

        // write data to file
        writeToCsv("continuous_empirical_distribution.csv", data)
    }
}