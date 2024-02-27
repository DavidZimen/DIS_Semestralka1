module fri.uniza.semestralka1 {
    requires javafx.controls;
    requires javafx.fxml;
    requires kotlin.stdlib;
    requires commons.math3;


    opens fri.uniza.semestralka1 to javafx.fxml;
    opens fri.uniza.semestralka1.ui to javafx.fxml;
    exports fri.uniza.semestralka1;
}