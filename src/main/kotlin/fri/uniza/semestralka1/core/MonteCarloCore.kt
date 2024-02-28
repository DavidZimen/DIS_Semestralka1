package fri.uniza.semestralka1.core

/**
 * Abstract core of the Monte Carlo simulation type.
 * @author David Zimen
 */
open class MonteCarloCore() : SimulationCore() {

    /**
     * Number of replications to be done in 1 simulation run.
     * Base value is 1 000 replications.
     */
    private var replicationsCount = Int.MAX_VALUE

    /**
     * Indication if simulation is stopped by user.
     */
    private var stopSimulation = false

    constructor(replicationsCount: Int) : this() {
        this.replicationsCount = replicationsCount
    }

    /**
     * Method to execute before all replications begin.
     */
    open fun beforeReplication() {}

    /**
     * Method to execute before every single replication.
     */
    open fun beforeReplications() {}

    /**
     * Logic of the replication.
     */
    open fun replication() {}

    /**
     * Method to execute after every single replication.
     */
    open fun afterReplication() {}

    /**
     * Method to execute after all replications are finished.
     */
    open fun afterReplications() {}

    /**
     * Runs Monte Carlo simulation with users implementation of abstract methods.
     * Overrides [SimulationCore.runSimulation].
     */
    override fun runSimulation() {
        beforeReplications()
        for (i in 0 until replicationsCount) {
            beforeReplication()
            replication()
            afterReplication()
            if (stopSimulation) {
                break
            }
        }
        afterReplications()
    }

    /**
     * Stops Monte Carlo simulation.
     * Overrides [SimulationCore.stopSimulation].
     */
    override fun stopSimulation() {
        stopSimulation = true
    }
}