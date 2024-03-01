package fri.uniza.semestralka1.simulation.core

/**
 * Simulation core with important methods [runSimulation], [stopSimulation], [resumeSimulation] and [pauseSimulation].
 * All methods are open, so their implementation is not mandatory, but highly recommended.
 * @author David Zimen
 */
open class SimulationCore {

    /**
     * Runs the simulation with provided configuration.
     */
    open fun runSimulation() {}

    /**
     * Stops simulation, without possibility to resume.
     */
    open fun stopSimulation() {}

    /**
     * Pauses simulation at given point, with possibility to resume..
     */
    open fun pauseSimulation() {}

    /**
     * Resumes simulation to the point when it was stopped.
     */
    open fun resumeSimulation() {}
}