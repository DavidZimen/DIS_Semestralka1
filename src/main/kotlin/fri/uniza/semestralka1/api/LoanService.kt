package fri.uniza.semestralka1.api

import fri.uniza.semestralka1.simulation.LoanMonteCarlo
import fri.uniza.semestralka1.simulation.Strategy
import fri.uniza.semestralka1.simulation.StrategyState
import fri.uniza.semestralka1.simulation.StrategyType
import kotlinx.coroutines.coroutineScope
import kotlinx.coroutines.launch

class LoanService {

    private val monteCarlo: LoanMonteCarlo = LoanMonteCarlo()

    val running: Boolean
        get() = monteCarlo.simulationRunning

    val result: StrategyType
        get() = monteCarlo.bestStrategy!!

    @Throws(IllegalStateException::class)
    fun setReplicationsCount(replicationsCount: Long) {
        monteCarlo.replicationsCount = replicationsCount
    }

    suspend fun runSimulation() = coroutineScope {
        launch { monteCarlo.runSimulation()}
    }

    fun stopSimulation() = monteCarlo.stopSimulation()

    fun checkForStateUpdates(strategyType: StrategyType) = monteCarlo.getStateForType(strategyType)
}