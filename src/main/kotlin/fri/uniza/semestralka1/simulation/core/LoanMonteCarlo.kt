package fri.uniza.semestralka1.simulation.core

import fri.uniza.semestralka1.generator.*
import kotlin.math.pow

class LoanMonteCarlo(replicationsCount: Long) : MonteCarloCore(replicationsCount) {

    /**
     * Map of generators as defined in the assignment.
     * Key is starting year of the generator.
     * Value must implement [Generator] interface.
     */
    private val generators = linkedMapOf<Int, Generator>()

    private val strategyA = Strategy(StrategyType.A)
    private val strategyB = Strategy(StrategyType.B)
    private val strategyC = Strategy(StrategyType.C)

    override fun beforeReplications() {
        initializeGenerators()
    }

    override fun beforeReplication() {
        strategyA.prepareForReplication()
        strategyB.prepareForReplication()
        strategyC.prepareForReplication()
    }

    override fun replication() {
        calculateStrategy(StrategyType.A)
        calculateStrategy(StrategyType.B)
        calculateStrategy(StrategyType.C)
    }

    override fun afterReplication() {
        strategyA.evaluateReplication(replicationsExecuted)
        strategyB.evaluateReplication(replicationsExecuted)
        strategyC.evaluateReplication(replicationsExecuted)
    }

    override fun afterReplications() {
        var best = if (strategyA.paidAverage < strategyB.paidAverage) strategyA else strategyB
        best = if (best.paidAverage < strategyC.paidAverage) best else strategyC
        println("Best strategy is ${best.type} with average paid ${best.paidAverage}€")
        println("${strategyA.type}: ${strategyA.paidAverage}€")
        println("${strategyB.type}: ${strategyB.paidAverage}€")
        println("${strategyC.type}: ${strategyC.paidAverage}€")
    }

    private fun calculateStrategy(type: StrategyType) {
        val strategy = when(type) {
            StrategyType.A -> strategyA
            StrategyType.B -> strategyB
            StrategyType.C -> strategyC
            else -> return
        }

        val years = type.years
        var leftToPay = INITIAL_MORTGAGE_VALUE
        var yearInterest: Double
        var monthlyPayment: Double

        // always operates with 2 values - also necessary to pair last() with LAST_YEAR
        for ((year, nextYear) in years.zipWithNext() + (years.last() to LAST_YEAR)) {
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
    val type: StrategyType,
) {
    var paidReplication: Double = Double.MAX_VALUE
    var paidAverage: Double = Double.MAX_VALUE
    private var paidOverall: Double = 0.0

    fun prepareForReplication() {
        paidReplication = 0.0
    }

    fun evaluateReplication(replicationsCompleted: Long) {
        paidOverall += paidReplication
        paidAverage = paidOverall / replicationsCompleted
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
    val years: List<Int>
        get() = when(this) {
            A -> listOf(LoanMonteCarlo.FIRST_YEAR, 2029, 2032, 2033)
            B -> listOf(LoanMonteCarlo.FIRST_YEAR, 2027, 2030, 2033)
            C -> listOf(LoanMonteCarlo.FIRST_YEAR, 2027, 2028, 2033)
        }
}
