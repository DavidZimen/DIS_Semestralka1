package fri.uniza.semestralka1

import fri.uniza.semestralka1.simulation.core.LoanMonteCarlo
import javafx.application.Application
import javafx.fxml.FXMLLoader
import javafx.scene.Scene
import javafx.stage.Stage

class Semestralka1MonteCarlo : Application() {
    override fun start(stage: Stage) {
        val fxmlLoader = FXMLLoader(Semestralka1MonteCarlo::class.java.getResource("hello-view.fxml"))
        val scene = Scene(fxmlLoader.load(), 320.0, 240.0)
        stage.title = "Hello!"
        stage.scene = scene
        stage.show()
    }
}

fun main() {
//    Application.launch(Semestralka1MonteCarlo::class.java)
    LoanMonteCarlo(5_000_000)
}