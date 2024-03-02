package fri.uniza.semestralka1

import javafx.application.Application
import javafx.fxml.FXMLLoader
import javafx.scene.Scene
import javafx.stage.Stage
import kotlin.system.exitProcess

class Semestralka1MonteCarlo : Application() {
    override fun start(stage: Stage) {
        val fxmlLoader = FXMLLoader(Semestralka1MonteCarlo::class.java.getResource("gui.fxml"))
        val scene = Scene(fxmlLoader.load(), 1600.0, 800.0)
        stage.title = "Hello!"
        stage.scene = scene
        stage.show()

        stage.setOnCloseRequest {
            exitProcess(0)
        }
    }
}

fun main() {
    Application.launch(Semestralka1MonteCarlo::class.java)
}