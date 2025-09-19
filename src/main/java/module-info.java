module co.edu.uniquindio.poo.amazen {
    requires javafx.controls;
    requires javafx.fxml;


    opens co.edu.uniquindio.poo.amazen to javafx.fxml;
    exports co.edu.uniquindio.poo.amazen;
}