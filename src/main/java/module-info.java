module co.edu.uniquindio.poo.amazen {
    requires javafx.controls;
    requires javafx.fxml;
    requires javafx.base;
    requires javafx.graphics;
    requires static lombok;


    opens co.edu.uniquindio.poo.amazen to javafx.fxml;
    opens co.edu.uniquindio.poo.amazen.ViewController to javafx.fxml;
    opens co.edu.uniquindio.poo.amazen.Model to javafx.fxml;

    exports co.edu.uniquindio.poo.amazen ;
    exports co.edu.uniquindio.poo.amazen.ViewController ;
    exports co.edu.uniquindio.poo.amazen.Model ;
}