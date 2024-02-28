package fri.uniza.semestralka1.generator

import fri.uniza.semestralka1.general_utils.*
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
     * Key for [generators] map is determined first key in ordered [Map.keys], that is greater than [probability].
     * @return [IntervalRandom] from [generators] map based on provided [probability]
     */
    private fun findIntervalRandom(probability: Double): IntervalRandom {
        var matchedKey = 1.0
        for (key in generators.keys) {
            if (isALessThanB(probability, key)) {
                matchedKey = key
                break
            }
        }
        return generators[matchedKey]!!
    }

    /**
     * Checks correct definitions of provided [intervals].
     * @throws IllegalStateException when cumulative probability is less than 1
     *  or when one of the [checkCorrectInterval], [checkOverlapping] throws it
     */
    @Throws(IllegalStateException::class)
    private fun checkAndInitialize(intervals: List<IntervalProbability>) {
        // sort for easier overlap checking
        val sortedIntervals = intervals.sortedWith(INTERVALS_SORTING)

        sortedIntervals.forEachIndexed { i, interval ->
            checkCorrectInterval(interval)
            if (i > 0) {
                checkOverlapping(sortedIntervals[i - 1], interval)
            }
            createIntervalRandom(interval)
        }

        if (!isAEqualsToB(generators.keys.last(), 1.0)) {
            throw IllegalStateException(GeneratorMessage.PROBABILITY_MSG)
        }
    }

    /**
     * Checks if lower limit of interval is less than its higher limit.
     * @throws IllegalStateException when interval limits are incorrectly defined
     */
    @Throws(IllegalStateException::class)
    private fun checkCorrectInterval(interval: IntervalProbability) {
        if (isAGreaterThanB(interval.lowerLimit, interval.higherLimit)) {
            throw IllegalStateException(
                "${GeneratorMessage.INTERVAL_EDGES_MSG} For definition: " +
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
        if (!isAEqualsToB(current.lowerLimit, previous.higherLimit)) {
            throw IllegalStateException(GeneratorMessage.OVERLAP_MSG)
        }
    }

    /**
     * Creates new [Map.Entry] into [generators] from [interval].
     * Key is created as sum of last [Map.Entry.key] and [IntervalProbability.probability].
     * Random is created with seed generated from [nextSeed].
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

    companion object {
        /**
         * Custom comparator for interval sorting.
         * Compares [Double] values with own function to provided precision.
         */
        private val INTERVALS_SORTING = Comparator<IntervalProbability> { o1, o2 ->
            isALessThanBComparator(o1.lowerLimit, o2.lowerLimit)
        }
    }
}
