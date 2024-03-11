package fri.uniza.semestralka1.ui

import fri.uniza.semestralka1.api.LoanService
import fri.uniza.semestralka1.general_utils.DOUBLE_REGEX
import fri.uniza.semestralka1.general_utils.INTEGER_REGEX
import fri.uniza.semestralka1.simulation.SimulationState
import fri.uniza.semestralka1.simulation.Strategy
import fri.uniza.semestralka1.simulation.StrategyType
import javafx.fxml.FXML
import javafx.fxml.Initializable
import javafx.scene.control.TextField
import javafx.scene.text.Text
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch
import org.jfree.chart.ChartFactory
import org.jfree.chart.JFreeChart
import org.jfree.chart.axis.NumberAxis
import org.jfree.chart.fx.ChartViewer
import org.jfree.chart.plot.XYPlot
import org.jfree.data.xy.XYSeries
import org.jfree.data.xy.XYSeriesCollection
import java.awt.BasicStroke
import java.awt.Color
import java.net.URL
import java.util.*
import javax.swing.SwingUtilities


class GuiController : Initializable {

    private var seriesA = XYSeries("Strategy A")
    private var seriesB = XYSeries("Strategy B")
    private var seriesC = XYSeries("Strategy C")
    private val loanSimulationService = LoanService.instance

    @FXML
    private lateinit var chartA: ChartViewer

    @FXML
    private lateinit var chartB: ChartViewer

    @FXML
    private lateinit var chartC: ChartViewer

    @FXML
    private lateinit var averageA: Text

    @FXML
    private lateinit var averageB: Text

    @FXML
    private lateinit var averageC: Text

    @FXML
    private lateinit var replications: TextField

    @FXML
    private lateinit var mortgageValue: TextField

    override fun initialize(p0: URL?, p1: ResourceBundle?) {
        initCharts()
        replications.allowOnlyInt()
        replications.allowOnlyDouble()
    }

    @FXML
    fun onStart() {
        resetCharts()
        loanSimulationService.setReplicationsCount(replications.text.toReplicationsCount())
        loanSimulationService.setMortgageValue(mortgageValue.text.toMortgageValue())

        GlobalScope.launch {
            loanSimulationService.runSimulation(StrategyType.A)
        }

        GlobalScope.launch {
            loanSimulationService.runSimulation(StrategyType.B)
        }

        GlobalScope.launch {
            loanSimulationService.runSimulation(StrategyType.C)
        }

        loanSimulationService.subscribeStateChanges(StrategyType.A) { newState ->
            SwingUtilities.invokeLater {
                with(newState!! as SimulationState) {
                    seriesA.add(replicationNumber, currentAverage)
                    averageA.text = "Average: $currentAverage"

                }
            }
        }

        loanSimulationService.subscribeStateChanges(StrategyType.B) { newState ->
            SwingUtilities.invokeLater {
                with(newState!! as SimulationState) {
                    seriesB.add(replicationNumber, currentAverage)
                    averageB.text = "Average: $currentAverage"
                }
            }
        }

        loanSimulationService.subscribeStateChanges(StrategyType.C) { newState ->
            SwingUtilities.invokeLater {
                with(newState!! as SimulationState) {
                    seriesC.add(replicationNumber, currentAverage)
                    averageC.text = "Average: $currentAverage"
                }
            }
        }
    }

    @FXML
    fun onStop() {
        loanSimulationService.stopSimulation()
    }

    @FXML
    fun onCut() {
        val removeLastIndex = (seriesA.itemCount * ITEMS_CUT_PERCENTAGE).toInt() - 1
        seriesA.delete(0, removeLastIndex)
        seriesB.delete(0, removeLastIndex)
        seriesC.delete(0, removeLastIndex)
    }

    private fun resetCharts() {
        seriesA.clear()
        seriesB.clear()
        seriesC.clear()
    }

    private fun initCharts() {
        chartA.chart = createChart(StrategyType.A)
        chartA.isVisible = true
        chartB.chart = createChart(StrategyType.B)
        chartB.isVisible = true
        chartC.chart = createChart(StrategyType.C)
        chartC.isVisible = true
    }

    private fun createChart(strategy: StrategyType): JFreeChart {
        val dataset = XYSeriesCollection()
        val series = when(strategy) {
            StrategyType.A -> seriesA
            StrategyType.B -> seriesB
            StrategyType.C -> seriesC
        }
        val color = when(strategy) {
            StrategyType.A -> Color.RED
            StrategyType.B -> Color.BLUE
            StrategyType.C -> Color.GREEN
        }
        series.clear()
        dataset.addSeries(series)

        val chart = ChartFactory.createXYLineChart(
            "Monte Carlo - $strategy",
            "Replication",
            "Paid [€]",
            dataset,

        )

        //auto range for the y-axis
        val yAxis = chart.xyPlot.rangeAxis as NumberAxis
        yAxis.autoRangeIncludesZero = false
        yAxis.isAutoRange = true

        with(chart.plot as XYPlot) {
            backgroundPaint = Color.WHITE
            renderer.setSeriesStroke(0, BasicStroke(1.5f))
            renderer.setSeriesPaint(0, color)
        }
        return chart
    }

    private fun String.toReplicationsCount(): Long {
        return if (isNullOrBlank()) {
            Long.MAX_VALUE
        } else {
            toLong()
        }
    }

    private fun String.toMortgageValue(): Double {
        return if (isNullOrBlank()) {
            Strategy.INITIAL_MORTGAGE_VALUE
        } else {
            toDouble()
        }
    }

    private fun TextField.allowOnlyInt() {
        textProperty().addListener { _, _, newValue ->
            if (!newValue.matches(INTEGER_REGEX)) {
                text = newValue.replace(Regex("[^0-9]"), "")
            }
        }
    }

    fun TextField.allowOnlyDouble() {
        textProperty().addListener { _, _, newValue ->
            if (!newValue.matches(DOUBLE_REGEX)) {
                text = newValue.replace(Regex("[^0-9.]"), "")
            }
        }
    }

    companion object {
        private const val ITEMS_CUT_PERCENTAGE = 0.1
    }
}