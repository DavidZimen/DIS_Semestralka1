package fri.uniza.semestralka1.ui

import fri.uniza.semestralka1.simulation.LoanMonteCarlo
import javafx.fxml.FXML
import javafx.fxml.Initializable
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

    private val series = XYSeries("Random data")
    private val monteCarlo = LoanMonteCarlo(5_000_000)

    @FXML
    private lateinit var chartViewer: ChartViewer

    @FXML
    fun startSimulation() {
        monteCarlo.runSimulation()
    }

    override fun initialize(p0: URL?, p1: ResourceBundle?) {
        chartViewer.chart = createChart("A")
        chartViewer.isVisible = true
        val runner = Runnable {
            monteCarlo.strategyAState.listen("guiListener") {
                state -> run {
                    SwingUtilities.invokeLater { series.add(state.replicationNumber, state.currentValue) }
                }
            }
        }
        Thread(runner).start()
    }

    private fun createChart(strategy: String): JFreeChart {
        val dataset = XYSeriesCollection()
        dataset.addSeries(series)

        val chart = ChartFactory.createXYLineChart(
            "Monte Carlo - $strategy",
            "Počet replikácii",
            "Zaplatené [€]",
            dataset
        )

        // Get the plot and set auto range for the range axis
        val rangeAxis = chart.xyPlot.rangeAxis as NumberAxis
        rangeAxis.autoRangeIncludesZero = false
        rangeAxis.isAutoRange = true

        chart.plot.backgroundPaint = Color.WHITE
        return chart
    }
}