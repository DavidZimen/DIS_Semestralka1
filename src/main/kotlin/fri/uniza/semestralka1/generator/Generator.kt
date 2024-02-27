package fri.uniza.semestralka1.generator

/**
 * Abstract generator class providing one method for randomly generated number
 * @author David Zimen
 */
interface Generator {

    /**
     * Provides pseudo-randomly generated number by internal logic
     * of concrete implementation.
     * @return Randomly generated number of type [Double].
     */
    fun sample(): Double
}