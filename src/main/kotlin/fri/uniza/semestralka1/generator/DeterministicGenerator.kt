package fri.uniza.semestralka1.generator

class DeterministicGenerator(private val value: Double) : Generator {

    /**
     * Overrides [Generator.sample] method.
     * @return Single [value] provided while creating instance.
     */
    override fun sample() = value
}