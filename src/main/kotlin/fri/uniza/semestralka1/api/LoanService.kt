package fri.uniza.semestralka1.api

import fri.uniza.semestralka1.observer.Observer
import fri.uniza.semestralka1.simulation.MortgageMonteCarlo
import kotlinx.coroutines.coroutineScope
import kotlinx.coroutines.launch

class LoanService private constructor() {

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

    fun subscribeStateChanges(name: String, observer: Observer<Any>) {
        monteCarlo.state.subscribe(name , observer)
    }

    companion object {
        val instance: LoanService by lazy {
            LoanService()
        }
    }
}