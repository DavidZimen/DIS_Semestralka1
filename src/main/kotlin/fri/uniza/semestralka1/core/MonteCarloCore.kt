package fri.uniza.semestralka1.core

/**
 * Abstract core of the Monte Carlo simulation type.
 * @author David Zimen
 */
abstract class MonteCarloCore {

    /**
     * Number of replications to be done in 1 simulation run.
     * Base value is 1 000 replications.
     */
    var replicationsCount: Int = 1_000
        protected set

    /**
     * Method to execute before all replications begin.
     */
    abstract fun beforeReplication()

    /**
     * Method to execute before every single replication.
     */
    abstract fun beforeReplications()

    /**
     * Logic of the replication.
     */
    abstract fun replication()

    /**
     * Method to execute after every single replication.
     */
    abstract fun afterReplication()

    /**
     * Method to execute after all replications are finished.
     */
    abstract fun afterReplications()

    /**
     * Runs Monte Carlo simulation with users implementation of abstract methods.
     */
    fun runSimulation() {
        beforeReplications()
        for (i in 0 until replicationsCount) {
            beforeReplication()
            replication()
            afterReplication()
        }
        afterReplications()
    }
}