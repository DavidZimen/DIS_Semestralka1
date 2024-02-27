package fri.uniza.semestralka1.generator

import java.util.Random

/**
 * Random generator for Discrete uniform distribution.
 * Possible to provide [minValue] and [maxValue] for own interval of generation.
 * Implements [Generator] interface.
 * @author David Zimen
 */
class DiscreteUniformGenerator(minValue: Int, maxValue: Int) : Generator {

    /**
     * Lowest possible generated number.
     */
    private val minValue: Int

    /**
     * Highest possible generated number.
     */
    private val maxValue: Int

    /**
     * Generator of random numbers.
     */
    private val generator = Random(nextSeed())

    init {
        if (minValue >= maxValue) {
            throw IllegalStateException("Parameter minValue can not be greater than parameter maxValue !!!")
        }
        this.minValue = minValue
        this.maxValue = maxValue + 1
    }

    /**
     * Overrides [Generator.sample] method.
     * @return Pseudo-randomly generated [Int] from <[minValue]; [maxValue]> interval converted to [Double].
     */
    override fun sample() = generator.nextInt(minValue, maxValue).toDouble()
}