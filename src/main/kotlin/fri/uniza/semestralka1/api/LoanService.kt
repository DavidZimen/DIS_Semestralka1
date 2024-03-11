package fri.uniza.semestralka1.api

import fri.uniza.semestralka1.observer.Observer
import fri.uniza.semestralka1.simulation.MortgageMonteCarlo
import fri.uniza.semestralka1.simulation.StrategyType
import kotlinx.coroutines.coroutineScope
import kotlinx.coroutines.launch


/**
 * Service for bridging between UI and backend.
 */
class LoanService private constructor() {

    /**
     * Map of [MortgageMonteCarlo] with respective [StrategyType].
     */
    private val simulations = mapOf(
        StrategyType.A to MortgageMonteCarlo(StrategyType.A),
        StrategyType.B to MortgageMonteCarlo(StrategyType.B),
        StrategyType.C to MortgageMonteCarlo(StrategyType.C)
    )

    /**
     * Sets [replicationsCount] to all simulations.
     */
    @Throws(IllegalStateException::class)
    fun setReplicationsCount(replicationsCount: Long) {
        simulations.forEach { (_, v) ->
            v.replicationsCount = replicationsCount
        }
    }

    /**
     * Sets [mortgageValue] to all simulation.
     */
    @Throws(IllegalStateException::class)
    fun setMortgageValue(mortgageValue: Double) {
        simulations.forEach { (_, v) ->
            v.mortgageValue = mortgageValue
        }
    }

    /**
     * Async function to start [MortgageMonteCarlo] for provided [strategyType].
     */
    suspend fun runSimulation(strategyType: StrategyType) = coroutineScope {
        launch {
            simulations[strategyType]?.runSimulation()
        }
    }

    /**
     * Stops all simulations.
     */
    fun stopSimulation() {
        simulations.forEach { (k, v) ->
            v.stopSimulation()
            v.state.removeObserver(k.toString())
        }
    }

    /**
     * Adds [observer] function to [MortgageMonteCarlo] of provided [strategyType].
     */
    fun subscribeStateChanges(strategyType: StrategyType, observer: Observer<Any>) {
        simulations[strategyType]?.state?.subscribe(strategyType.toString(), observer)
    }

    companion object {
        /**
         * Provides single instance of [LoanService] to whole application scope.
         */
        val instance: LoanService by lazy {
            LoanService()
        }
    }
}