package fri.uniza.semestralka1.simulation.core

/**
 * Abstract core of the Monte Carlo simulation type.
 * @author David Zimen
 */
open class MonteCarloCore(replicationsCount: Long) : SimulationCore() {

    /**
     * Number of replications to be done in 1 simulation run.
     * Base value is 1 000 replications.
     */
    protected var replicationsCount = replicationsCount
        private set

    protected var replicationsExecuted = 0L
        private set

    /**
     * Indication if simulation is stopped by user.
     */
    protected var stopSimulation = false
        private set


    /**
     * Method to execute before all replications begin.
     */
    protected open fun beforeReplication() {}

    /**
     * Method to execute before every single replication.
     */
    protected open fun beforeReplications() {}

    /**
     * Logic of the replication.
     */
    protected open fun replication() {}

    /**
     * Method to execute after every single replication.
     */
    protected open fun afterReplication() {}

    /**
     * Method to execute after all replications are finished.
     */
    protected open fun afterReplications() {}

    /**
     * Runs Monte Carlo simulation with users implementation of abstract methods.
     * Overrides [SimulationCore.runSimulation].
     */
    final override fun runSimulation() {
        beforeReplications()
        for (i in 0 until replicationsCount) {
            beforeReplication()
            replication()
            replicationsExecuted++
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
    final override fun stopSimulation() {
        stopSimulation = true
    }
}