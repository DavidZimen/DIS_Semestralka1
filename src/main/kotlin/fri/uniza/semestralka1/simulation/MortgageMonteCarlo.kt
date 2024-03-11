package fri.uniza.semestralka1.simulation

import fri.uniza.semestralka1.generator.*
import fri.uniza.semestralka1.simulation.core.MonteCarloCore
import kotlin.math.pow

/**
 * Monte carlo specific logic to assignment.
 */
class MortgageMonteCarlo(private val strategyType: StrategyType) : MonteCarloCore() {

    /**
     * Initial value of the mortgage for calculations.
     */
    var mortgageValue = Strategy.INITIAL_MORTGAGE_VALUE
        set(value) {
            if (simulationRunning) {
                throw IllegalStateException("Simulation is running. Cannot set new mortgage value !!!")
            }
            field = value
        }

    /**
     * Holds data for calculations.
     */
    private lateinit var strategy: Strategy

    /**
     * Map of generators as defined in the assignment.
     * Key is starting year of the generator.
     * Value must implement [Generator] interface.
     */
    private val generators = linkedMapOf<Int, Generator>()

    constructor(strategy: StrategyType, replicationsCount: Long) : this(strategy) {
        this.replicationsCount = replicationsCount
    }

    // OVERRIDE FUNCTIONS
    override fun beforeReplications() {
        initializeStrategy()
        initializeGenerators()
    }

    override fun beforeReplication() {
        strategy.prepareForReplication()
    }

    override fun replication() {
        calculateStrategy()
    }

    override fun afterReplication() {
        strategy.evaluateReplication(replicationsExecuted)
        if (replicationsExecuted > FIRST_EMIT_AFTER && replicationsExecuted.mod(EMIT_STATE_AFTER) == 0) {
            updateState()
        }
    }

    override fun afterReplications() {
        updateState()
    }

    // PRIVATE FUNCTIONS
    /**
     * Calculates value of [StrategyType] for one replication.
     */
    private fun calculateStrategy() {
        var leftToPay = mortgageValue
        var yearInterest: Double
        var monthlyPayment: Double

        // always operates with 2 values - also necessary to pair last() with LAST_YEAR
        for ((year, nextYear) in strategyType.fixationYears.zipWithNext() + (strategyType.fixationYears.last() to LAST_YEAR)) {
            yearInterest = getGenerator(year).sample()
            monthlyPayment = calculateMonthlyPayment(leftToPay, yearInterest, LAST_YEAR - year)
            leftToPay = calculateMoneyLeftToPay(leftToPay, yearInterest, LAST_YEAR - year, nextYear - year)
            strategy.paidReplication += monthlyPayment * (nextYear - year) * 12
        }
    }

    /**
     * Calculates monthly payment for the mortgage.
     * @param leftToPay Money that are remaining to be paid.
     * @param yearInterest Generated interest in given year.
     * @param yearsToPay Year left to pay.
     */
    private fun calculateMonthlyPayment(
        leftToPay: Double,
        yearInterest: Double,
        yearsToPay: Int
    ): Double {
        val smth = transformYearInterest(yearInterest, yearsToPay)
        val numerator = leftToPay * yearInterest.toPercentage().monthly() * smth
        val denominator = smth - 1
        return numerator / denominator
    }

    /**
     * Calculates how much money need to paid to finish the mortgage.
     * @param leftToPay Money that were remaining to be paid
     * @param yearInterest Interest rate for [leftToPay]
     * @param yearsToPay How many years are remaining for paying [leftToPay] money
     * @param paidYears How manu years will be paid [leftToPay] with given [yearInterest]
     */
    private fun calculateMoneyLeftToPay(
        leftToPay: Double,
        yearInterest: Double,
        yearsToPay: Int,
        paidYears: Int
    ): Double {
        val smthToPay = transformYearInterest(yearInterest, yearsToPay)
        val smthPaid = transformYearInterest(yearInterest, paidYears)
        val numerator =  smthToPay - smthPaid
        val denominator = smthToPay - 1
        return leftToPay * (numerator / denominator)
    }

    /**
     * @return [Generator] for provided [year].
     */
    private fun getGenerator(year: Int): Generator {
        val updatedYear = if (year.mod(2) == 1) year - 1 else year
        return generators[updatedYear] ?: generators[2032]!!
    }

    /**
     * Initializes [generators] with specified parameters.
     */
    private fun initializeGenerators() {
        generators[2024] = DiscreteUniformGenerator(1, 4)
        generators[2026] = ContinuousUniformGenerator(0.3, 5.0)
        generators[2028] = ContinuousEmpiricalGenerator(
            IntervalProbability(0.1, 0.3, 0.1),
            IntervalProbability(0.3, 0.8, 0.35),
            IntervalProbability(0.8, 1.2, 0.2),
            IntervalProbability(1.2, 2.5, 0.15),
            IntervalProbability(2.5, 3.8, 0.15),
            IntervalProbability(3.8, 4.8, 0.05)
        )
        generators[2030] = DeterministicGenerator(1.3)
        generators[2032] = ContinuousUniformGenerator(0.9, 2.2)
    }

    /**
     * Initializes [strategy] with new value of [Strategy].
     */
    private fun initializeStrategy() {
        strategy = Strategy()
    }

    /**
     * Send notification about current state of the simulation to all observers of [state].
     */
    private fun updateState() {
        state.next(SimulationState(replicationsExecuted, strategy.paidAverage))
    }

    /**
     * Calculates partial formula from whole mortgage calculation formula.
     */
    private fun transformYearInterest(yearInterest: Double, yearsToPay: Int): Double {
        return (1 + yearInterest.toPercentage().monthly()).pow(12.0 * yearsToPay)
    }

    /**
     * Divides number by 12.
     */
    private fun Double.monthly() = this / 12

    /**
     * Divides number by 100.
     */
    private fun Double.toPercentage() = this / 100

    companion object {
        const val FIRST_YEAR = 2024
        const val LAST_YEAR = 2034
        const val FIRST_EMIT_AFTER = 1_000_000
        const val EMIT_STATE_AFTER = 25_000
    }
}

/**
 * Class for keeping track of strategy state during simulation.
 */
class Strategy {
    var paidReplication = INITIAL_MORTGAGE_VALUE
    var paidAverage = INITIAL_MORTGAGE_VALUE
    private var paidOverall = 0.0

    fun prepareForReplication() {
        paidReplication = 0.0
    }

    fun evaluateReplication(replicationsCompleted: Long) {
        paidOverall += paidReplication
        paidAverage = paidOverall / replicationsCompleted
    }

    companion object {
        const val INITIAL_MORTGAGE_VALUE = 100_000.0
    }
}

/**
 * Holds current state of the [MortgageMonteCarlo] simulation.
 */
data class SimulationState(
    val replicationNumber: Long,
    val currentAverage: Double,
)

/**
 * Type of the strategy as specified in assignment.
 */
enum class StrategyType {
    A,
    B,
    C;

    /**
     * Fixation years for strategies.
     */
    val fixationYears: List<Int>
        get() = when(this) {
            A -> listOf(MortgageMonteCarlo.FIRST_YEAR, 2029, 2032, 2033)
            B -> listOf(MortgageMonteCarlo.FIRST_YEAR, 2027, 2030, 2033)
            C -> listOf(MortgageMonteCarlo.FIRST_YEAR, 2027, 2028, 2033)
        }
}
