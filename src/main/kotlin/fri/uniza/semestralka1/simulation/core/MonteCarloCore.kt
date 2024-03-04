package fri.uniza.semestralka1.simulation.core

/**
 * Abstract core of the Monte Carlo simulation type.
 * @author David Zimen
 */
open class MonteCarloCore() : SimulationCore() {

    /**
     * Number of replications to be done in 1 simulation run.
     * Base value is 1 000 replications.
     */
    var replicationsCount = Long.MAX_VALUE
        set(value) {
            if (simulationRunning) {
                throw IllegalStateException("Simulation is running. Cannot set new replications count !!!")
            }
            field = value
        }

    var replicationsExecuted = 0L
        private set

    /**
     * Indication if simulation is stopped by user.
     */
    var simulationRunning = false
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
        simulationRunning = true
        replicationsExecuted = 0
        for (i in 0 until replicationsCount) {
            beforeReplication()
            replication()
            replicationsExecuted++
            afterReplication()
            if (!simulationRunning) {
                break
            }
        }
        afterReplications()
        simulationRunning = false
    }

    /**
     * Stops Monte Carlo simulation.
     * Overrides [SimulationCore.stopSimulation].
     */
    final override fun stopSimulation() {
        simulationRunning = false
    }
}