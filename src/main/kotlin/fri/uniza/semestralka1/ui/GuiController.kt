package fri.uniza.semestralka1.ui

import fri.uniza.semestralka1.api.LoanService
import fri.uniza.semestralka1.simulation.StrategyType
import javafx.fxml.FXML
import javafx.fxml.Initializable
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
    fun startSimulation() {
        initCharts()

        GlobalScope.launch {
            loanSimulationService.runSimulation()
        }

        while (!loanSimulationService.running) {
            print("")
        }

        GlobalScope.launch {
            while (loanSimulationService.running) {
                val stateA = loanSimulationService.checkForStateUpdates(StrategyType.A)
                val stateB = loanSimulationService.checkForStateUpdates(StrategyType.B)
                val stateC = loanSimulationService.checkForStateUpdates(StrategyType.C)
                if (stateA.replicationNumber > 250) {
                    SwingUtilities.invokeLater {
                        seriesA.add(stateA.replicationNumber, stateA.currentValue)
                        seriesB.add(stateB.replicationNumber, stateB.currentValue)
                        seriesC.add(stateC.replicationNumber, stateC.currentValue)
                        averageA.text = "Average: ${stateA.currentValue}"
                        averageB.text = "Average: ${stateB.currentValue}"
                        averageC.text = "Average: ${stateC.currentValue}"
                    }
                }
                delay(200)
            }
        }
    }

    override fun initialize(p0: URL?, p1: ResourceBundle?) {
        loanSimulationService.setReplicationsCount(1_000_000)
        initCharts()
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
        val rangeAxis = chart.xyPlot.rangeAxis as NumberAxis
        rangeAxis.autoRangeIncludesZero = false
        rangeAxis.isAutoRange = true

        chart.plot.backgroundPaint = Color.WHITE
        return chart
    }
}