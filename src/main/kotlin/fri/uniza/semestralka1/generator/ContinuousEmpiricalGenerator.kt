package fri.uniza.semestralka1.generator

import fri.uniza.semestralka1.util.isABetweenBandC
import fri.uniza.semestralka1.util.isAEqualsToB
import fri.uniza.semestralka1.util.isAGreaterThanB
import fri.uniza.semestralka1.util.nextSeed
import java.util.Random
import kotlin.jvm.Throws

/**
 * Random generator for Continuous empirical distribution, supporting
 * more intervals of generating each with its own probability.
 * Implements [Generator] interface.
 * @author David Zimen
 */
class ContinuousEmpiricalGenerator : Generator {

    /**
     * Main generator of probabilities on <0; 1> interval, by which
     * specified generator will be chosen.
     */
    private val mainGenerator = Random(nextSeed())

    /**
     * Map where key is probability of generating from [IntervalRandom] value.
     * Used [LinkedHashMap] to ensure that keys are ordered as they were inserted.
     */
    private val generators = linkedMapOf<Double, IntervalRandom>()

    constructor() : this(IntervalProbability())

    constructor(vararg definition: IntervalProbability) {
        checkAndInitialize(definition.asList())
    }

    /**
     * Overrides [Generator.sample] method.
     * @return Pseudo-randomly generated number from Empirical continuous distribution
     *  abiding by the rules defined while creating the generator.
     */
    override fun sample(): Double {
        val p = mainGenerator.nextDouble()
        val intervalRandom = findIntervalRandom(p)
        return with(intervalRandom) {
            generator.nextDouble(lowerLimit, higherLimit)
        }
    }

    /**
     * Key for [generators] map is determined as last key
     * @return [IntervalRandom] from [generators] map based on provided [probability]
     */
    private fun findIntervalRandom(probability: Double): IntervalRandom {
        val key = generators.keys.findLast { key -> isAGreaterThanB(key, probability) } ?: 1.0
        return generators[key]!!
    }

    /**
     * Checks correct definitions of provided [intervals].
     * @throws IllegalStateException when cumulative probability is less than 1
     *  or when one of the [checkCorrectInterval], [checkOverlapping] throws it
     */
    @Throws(IllegalStateException::class)
    private fun checkAndInitialize(intervals: List<IntervalProbability>) {
        intervals.forEachIndexed { i, interval ->
            checkCorrectInterval(interval)
            if (i > 0) {
                checkOverlapping(intervals[i - 1], interval)
            }
            createIntervalRandom(interval)
        }

        if (!isAEqualsToB(generators.keys.last(), 1.0)) {
            throw IllegalStateException("Cumulative probability can not be lower than 1 !!!")
        }
    }

    /**
     * Checks if cumulative sum of probabilities for provided interval.
     * @throws IllegalStateException when cumulative sum is greater than 1
     */
    @Throws(IllegalStateException::class)
    private fun checkProbabilitySum(sum: Double, interval: IntervalProbability): Double {
        val probabilitySum = sum + interval.probability
        if (probabilitySum > 1) {
            throw IllegalStateException("Cumulative probability can not be higher than 1 !!!")
        }
        return probabilitySum
    }

    /**
     * Checks if lower limit of interval is less than its higher limit.
     * @throws IllegalStateException when interval limits are incorrectly defined
     */
    @Throws(IllegalStateException::class)
    private fun checkCorrectInterval(interval: IntervalProbability) {
        if (isAGreaterThanB(interval.lowerLimit, interval.higherLimit)) {
            throw IllegalStateException(
                "Lower interval limit cannot be greater than higher interval limit. For definition: " +
                    "Lower: ${interval.lowerLimit}, " +
                    "Higher: ${interval.higherLimit}, " +
                    "Probability: ${interval.probability}"
            )
        }
    }

    /**
     * Checks overlapping of 2 intervals.
     * @throws IllegalStateException when intervals overlap
     */
    @Throws(IllegalStateException::class)
    private fun checkOverlapping(previous: IntervalProbability, current: IntervalProbability) {
        if (isABetweenBandC(current.lowerLimit, previous.lowerLimit, previous.higherLimit)
            || isABetweenBandC(current.higherLimit, previous.lowerLimit, previous.higherLimit)) {
            throw IllegalStateException("Intervals for generation can not overlap !!!")
        }
    }

    /**
     * Creates new [Map.Entry] into [generators] from [interval].
     * Key is created as sum of last [Map.Entry.key] and [IntervalProbability.probability].
     * Random is created with seed generated from [seedGenerator].
     */
    private fun createIntervalRandom(interval: IntervalProbability) {
        with(generators) {
            val lastKey = if (generators.isEmpty()) 0.0 else keys.last()
            val newKey = lastKey + interval.probability
            put(
                newKey,
                IntervalRandom(
                    Random(nextSeed()),
                    interval.lowerLimit,
                    interval.higherLimit
                )
            )
        }
    }
}

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