package fri.uniza.semestralka1.generator

import java.util.*

/**
 * Generator of seeds for all generators used in this project.
 */
private val SEED_GENERATOR = Random()

/**
 * @return Next seed of [Long] type.
 */
fun nextSeed() = SEED_GENERATOR.nextLong()

/**
 * Class to define one interval of generation with given probability.
 */
data class IntervalProbability(
    val lowerLimit: Double = 0.0,
    val higherLimit: Double = 1.0,
    val probability: Double = 1.0
)

/**
 * Class to define one interval and its Random generator.
 */
data class IntervalRandom(
    val generator: Random,
    val lowerLimit: Double,
    val higherLimit: Double
)