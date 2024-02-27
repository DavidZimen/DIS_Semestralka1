package fri.uniza.semestralka1.util

import java.util.*

/**
 * Generator of seeds for all generators used in this project.
 */
private val SEED_GENERATOR = Random()

/**
 * @return Next seed of [Long] type.
 */
fun nextSeed() = SEED_GENERATOR.nextLong()