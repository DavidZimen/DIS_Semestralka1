package fri.uniza.semestralka1.ui

import fri.uniza.semestralka1.api.LoanService
import fri.uniza.semestralka1.general_utils.INTEGER_REGEX
import fri.uniza.semestralka1.simulation.StrategyType
import javafx.fxml.FXML
import javafx.fxml.Initializable
import javafx.scene.control.TextField
import javafx.scene.text.Text
import kotlinx.coroutines.*
import org.jfree.chart.ChartFactory
import org.jfree.chart.JFreeChart
import org.jfree.chart.axis.NumberAxis
import org.jfree.chart.fx.ChartViewer
import org.jfree.data.xy.XYSeries
import org.jfree.data.xy.XYSeriesCollection
import java.awt.Color
import java.net.URL
import java.util.*
import javax.swing.SwingUtilities


class GuiController : Initializable {

    private var seriesA = XYSeries("Strategia A")
    private var seriesB = XYSeries("Strategia B")
    private var seriesC = XYSeries("Strategia C")
    private val loanSimulationService = LoanService()

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
    private lateinit var bestStrategy: Text

    @FXML
    private lateinit var replications: TextField

    override fun initialize(p0: URL?, p1: ResourceBundle?) {
        initCharts()
        replications.allowOnlyInt()
    }

    @FXML
    fun onStart() {
        resetCharts()
        loanSimulationService.setReplicationsCount(replications.text.toReplicationsCount())

        GlobalScope.launch {
            loanSimulationService.runSimulation()
        }

        GlobalScope.launch {
            while (!loanSimulationService.running) {
                continue
            }

            while (loanSimulationService.running) {
                val stateA = loanSimulationService.checkForStateUpdates(StrategyType.A)
                val stateB = loanSimulationService.checkForStateUpdates(StrategyType.B)
                val stateC = loanSimulationService.checkForStateUpdates(StrategyType.C)
                if (stateA.replicationNumber > 10_000) {
                    SwingUtilities.invokeLater {
                        seriesA.add(stateA.replicationNumber, stateA.currentValue)
                        seriesB.add(stateB.replicationNumber, stateB.currentValue)
                        seriesC.add(stateC.replicationNumber, stateC.currentValue)
                        averageA.text = "Average: ${stateA.currentValue}"
                        averageB.text = "Average: ${stateB.currentValue}"
                        averageC.text = "Average: ${stateC.currentValue}"
                    }
                }
                delay(150)
            }
            bestStrategy.text = "Best strategy is ${loanSimulationService.result}"
        }
    }

    @FXML
    fun onStop() {
        loanSimulationService.stopSimulation()
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
        series.clear()
        dataset.addSeries(series)

        val chart = ChartFactory.createXYLineChart(
            "Monte Carlo - $strategy",
            "Počet replikácii",
            "Zaplatené [€]",
            dataset,

        )

        // Get the plot and set auto range for the range axis
        val yAxis = chart.xyPlot.rangeAxis as NumberAxis
        yAxis.autoRangeIncludesZero = false
        yAxis.isAutoRange = true

        chart.plot.backgroundPaint = Color.WHITE
        return chart
    }

    private fun String.toReplicationsCount(): Long {
        return if (isNullOrBlank()) {
            Long.MAX_VALUE
        } else {
            toLong()
        }
    }

    private fun TextField.allowOnlyInt() {
        textProperty().addListener { _, _, newValue ->
            if (!newValue.matches(INTEGER_REGEX)) {
                text = newValue.replace(Regex("[^0-9]"), "")
            }
        }
    }
}