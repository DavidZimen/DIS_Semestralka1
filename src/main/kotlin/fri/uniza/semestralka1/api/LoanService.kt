package fri.uniza.semestralka1.api

import fri.uniza.semestralka1.observer.Observer
import fri.uniza.semestralka1.simulation.MortgageMonteCarlo
import fri.uniza.semestralka1.simulation.SimulationState
import kotlinx.coroutines.coroutineScope
import kotlinx.coroutines.launch

class LoanService {

    private val monteCarlo: MortgageMonteCarlo = MortgageMonteCarlo()

    @Throws(IllegalStateException::class)
    fun setReplicationsCount(replicationsCount: Long) {
        monteCarlo.replicationsCount = replicationsCount
    }

    suspend fun runSimulation() = coroutineScope {
        launch {
            monteCarlo.runSimulation()
        }
    }

    fun stopSimulation() = monteCarlo.stopSimulation()

    fun subscribeStateChanges(observer: Observer<SimulationState>) {
        monteCarlo.state.subscribe(this::class.simpleName ?: "mortgage" , observer)
    }
}