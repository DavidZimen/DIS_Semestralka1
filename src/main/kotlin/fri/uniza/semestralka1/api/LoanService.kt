package fri.uniza.semestralka1.api

import fri.uniza.semestralka1.observer.Observer
import fri.uniza.semestralka1.simulation.MortgageMonteCarlo
import fri.uniza.semestralka1.simulation.StrategyType
import kotlinx.coroutines.coroutineScope
import kotlinx.coroutines.launch

class LoanService private constructor() {

    private val simulations = mapOf(
        StrategyType.A to MortgageMonteCarlo(StrategyType.A),
        StrategyType.B to MortgageMonteCarlo(StrategyType.B),
        StrategyType.C to MortgageMonteCarlo(StrategyType.C)
    )

    @Throws(IllegalStateException::class)
    fun setReplicationsCount(replicationsCount: Long) {
        simulations.forEach { (_, u) ->
            u.replicationsCount = replicationsCount
        }
    }

    @Throws(IllegalStateException::class)
    fun setMortgageValue(mortgageValue: Double) {
        simulations.forEach { (_, u) ->
            u.mortgageValue = mortgageValue
        }
    }

    suspend fun runSimulation(strategyType: StrategyType) = coroutineScope {
        launch {
            simulations[strategyType]?.runSimulation()
        }
    }

    fun stopSimulation() {
        simulations.forEach { (_, u) ->
            u.stopSimulation()
        }
    }

    fun subscribeStateChanges(strategyType: StrategyType, observer: Observer<Any>) {
        simulations[strategyType]?.state?.subscribe(strategyType.toString(), observer)
    }

    companion object {
        val instance: LoanService by lazy {
            LoanService()
        }
    }
}