package fri.uniza.semestralka1.simulation

import fri.uniza.semestralka1.generator.*
import fri.uniza.semestralka1.simulation.core.MonteCarloCore
import kotlin.math.pow

class LoanMonteCarlo() : MonteCarloCore() {

    /**
     * Best strategy after finishing the
     */
    var bestStrategy: Strategy? = null
        private set

    /**
     * Observable for strategy A state during simulation.
     */
    private val strategyAState = StrategyState()

    /**
     * Observable for strategy B state during simulation.
     */
    private val strategyBState = StrategyState()

    /**
     * Observable for strategy C state during simulation.
     */
    private val strategyCState = StrategyState()

    /**
     * Strategy A.
     */
    private val strategyA = Strategy(StrategyType.A)

    /**
     * Strategy B.
     */
    private val strategyB = Strategy(StrategyType.B)

    /**
     * Strategy C.
     */
    private val strategyC = Strategy(StrategyType.C)

    /**
     * Map of generators as defined in the assignment.
     * Key is starting year of the generator.
     * Value must implement [Generator] interface.
     */
    private val generators = linkedMapOf<Int, Generator>()

    constructor(replicationsCount: Long) : this() {
        this.replicationsCount = replicationsCount
    }

    //PUBLIC FUNCTIONS
    fun getStrategyState(type: StrategyType): StrategyState {
        return when(type) {
            StrategyType.A -> strategyAState
            StrategyType.B -> strategyBState
            StrategyType.C -> strategyCState
        }
    }

    // OVERRIDE FUNCTIONS
    override fun beforeReplications() {
        initializeGenerators()
        resetStatistics()
    }

    override fun beforeReplication() {
        StrategyType.values().forEach {
            it.getStrategy().prepareForReplication()
        }
    }

    override fun replication() {
        StrategyType.values().forEach {
            it.calculateStrategy()
        }
    }

    override fun afterReplication() {
        StrategyType.values().forEach {
            it.getStrategy().evaluateReplication(replicationsExecuted)
            it.updateStrategyState()
        }
    }

    override fun afterReplications() {
        bestStrategy = if (strategyA.paidAverage < strategyB.paidAverage) strategyA else strategyB
        bestStrategy = if (bestStrategy!!.paidAverage < strategyC.paidAverage) bestStrategy else strategyC
    }

    /**
     * Calculates value of [StrategyType] for one replication.
     */
    private fun StrategyType.calculateStrategy() {
        val strategy = getStrategy()

        var leftToPay = INITIAL_MORTGAGE_VALUE
        var yearInterest: Double
        var monthlyPayment: Double

        // always operates with 2 values - also necessary to pair last() with LAST_YEAR
        for ((year, nextYear) in fixationYears.zipWithNext() + (fixationYears.last() to LAST_YEAR)) {
            yearInterest = getGenerator(year).sample()
            monthlyPayment = calculateMonthlyPayment(leftToPay, yearInterest, LAST_YEAR - year)
            leftToPay = calculateMoneyLeftToPay(leftToPay, yearInterest, LAST_YEAR - year, nextYear - year)
            strategy.paidReplication += monthlyPayment * (nextYear - year) * 12
        }
    }

    // PRIVATE FUNCTIONS
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

    private fun resetStatistics() {
        bestStrategy = null
        StrategyType.values().forEach {
            it.getStrategy().resetForReplications()
        }
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
     * Updates current state of [StrategyType] with calculated values.
     */
    private fun StrategyType.updateStrategyState() {
        val strategy = getStrategy()
        val strategyState = when (this) {
            StrategyType.A -> strategyAState
            StrategyType.B -> strategyBState
            StrategyType.C -> strategyCState
        }
        with(strategyState) {
            replicationNumber = replicationsExecuted
            currentValue = strategy.paidAverage
        }
    }

    /**
     * @return Correct [Strategy] for [StrategyType]
     */
    private fun StrategyType.getStrategy() = when (this) {
        StrategyType.A -> strategyA
        StrategyType.B -> strategyB
        StrategyType.C -> strategyC
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
        const val INITIAL_MORTGAGE_VALUE = 100_000.0
        const val FIRST_YEAR = 2024
        const val LAST_YEAR = 2034
    }
}

/**
 * Class for keeping track of strategy state during simulation.
 */
class Strategy(
    type: StrategyType
) {
    val type: StrategyType = type
    var paidReplication: Double = LoanMonteCarlo.INITIAL_MORTGAGE_VALUE
    var paidAverage: Double = LoanMonteCarlo.INITIAL_MORTGAGE_VALUE
    private var paidOverall: Double = 0.0

    fun prepareForReplication() {
        paidReplication = 0.0
    }

    fun resetForReplications() {
        paidOverall = 0.0
        paidReplication = LoanMonteCarlo.INITIAL_MORTGAGE_VALUE
        paidAverage = LoanMonteCarlo.INITIAL_MORTGAGE_VALUE
    }

    fun evaluateReplication(replicationsCompleted: Long) {
        paidOverall += paidReplication
        paidAverage = paidOverall / replicationsCompleted
    }
}

class StrategyState(
    var replicationNumber: Long = 0L,
    var currentValue: Double = 0.0
) {
    override fun toString(): String {
        return "State - replication: $replicationNumber, paid: $currentValue"
    }
}

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
            A -> listOf(LoanMonteCarlo.FIRST_YEAR, 2029, 2032, 2033)
            B -> listOf(LoanMonteCarlo.FIRST_YEAR, 2027, 2030, 2033)
            C -> listOf(LoanMonteCarlo.FIRST_YEAR, 2027, 2028, 2033)
        }
}
