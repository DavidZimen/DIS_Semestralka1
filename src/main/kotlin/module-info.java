module fri.uniza.semestralka1 {
    requires javafx.controls;
    requires javafx.fxml;
    requires kotlin.stdlib;
    requires commons.math3;
    requires org.jfree.chart.fx;
    requires org.jfree.jfreechart;
    requires java.desktop;
    requires kotlinx.coroutines.core;


    opens fri.uniza.semestralka1 to javafx.fxml;
    opens fri.uniza.semestralka1.ui to javafx.fxml;
    exports fri.uniza.semestralka1;
}