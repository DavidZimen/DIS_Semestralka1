package fri.uniza.semestralka1.generator

import fri.uniza.semestralka1.general_utils.isALessOrEqualsToB
import java.util.Random

/**
 * Random generator for Continuous uniform distribution.
 * Possible to provide [lowerLimit] and [higherLimit] for own interval of generation.
 * Implements [Generator] interface.
 * @author David Zimen
 */
class ContinuousUniformGenerator : Generator {

    /**
     * Lower limit of the generation interval. Is inclusive.
     */
    private val lowerLimit: Double

    /**
     * Higher limit of the generation interval. Is exclusive.
     */
    private val higherLimit: Double

    /**
     * Generator of random numbers.
     */
    private val generator = Random(nextSeed())

    /**
     * Creates new instance of [ContinuousUniformGenerator] with.
     * Attribute [lowerLimit] is set to 0 and [higherLimit] is set to 1.
     */
    constructor(): this(0.0, 1.0)

    /**
     * Creates a new instance of [ContinuousUniformGenerator] based on [lowerLimit] and [higherLimit] parameters.
     * @throws IllegalStateException when [lowerLimit] >= [higherLimit]
     */
    @Throws(IllegalStateException::class)
    constructor(lowerLimit: Double, higherLimit: Double) {
        if (isALessOrEqualsToB(lowerLimit, higherLimit)) {
            throw IllegalStateException(GeneratorMessage.INTERVAL_EDGES_MSG)
        }
        this.lowerLimit = lowerLimit
        this.higherLimit = higherLimit
    }

    /**
     * Overrides [Generator.sample] method.
     * @return Pseudo-randomly generated number with Continuous uniform distribution
     *  from <[lowerLimit]; [higherLimit]) interval.
     */
    override fun sample() = generator.nextDouble(lowerLimit, higherLimit)
}