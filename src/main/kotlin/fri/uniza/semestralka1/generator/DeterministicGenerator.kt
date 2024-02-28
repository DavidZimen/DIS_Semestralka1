package fri.uniza.semestralka1.generator

/**
 * Class for deterministic generator that always return specified value.
 * For easier integration with Monte carlo generators.
 * @author David Zimen
 */
class DeterministicGenerator(private val value: Double) : Generator {

    /**
     * Overrides [Generator.sample] method.
     * @return Single [value] provided while creating instance.
     */
    override fun sample() = value
}