package fri.uniza.semestralka1.ui

import fri.uniza.semestralka1.api.LoanService
import fri.uniza.semestralka1.general_utils.INTEGER_REGEX
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

        loanSimulationService.subscribeStateChanges { newState ->
            SwingUtilities.invokeLater {
                with(newState!!) {
                    seriesA.add(replicationNumber, currentAverage[StrategyType.A])
                    seriesB.add(replicationNumber, currentAverage[StrategyType.B])
                    seriesC.add(replicationNumber, currentAverage[StrategyType.C])
                    averageA.text = "Average: ${currentAverage[StrategyType.A]}"
                    averageB.text = "Average: ${currentAverage[StrategyType.B]}"
                    averageC.text = "Average: ${currentAverage[StrategyType.C]}"

                    if (bestStrategyType != null) {
                        bestStrategy.text = "Best strategy is $bestStrategyType"
                    }
                }
            }
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
        bestStrategy.text = ""
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