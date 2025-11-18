module co.edu.uniquindio.poo.amazen {
    // JavaFX
    requires javafx.controls;
    requires javafx.fxml;
    requires javafx.graphics;
    requires javafx.base;

    // Lombok solo en compilación
    requires static lombok;

    opens co.edu.uniquindio.poo.amazen to javafx.fxml;
    opens co.edu.uniquindio.poo.amazen.ViewController to javafx.fxml;
    opens co.edu.uniquindio.poo.amazen.Model to javafx.fxml;
    opens co.edu.uniquindio.poo.amazen.Model.Strategy to javafx.fxml;
    opens co.edu.uniquindio.poo.amazen.Model.Persona to javafx.fxml;

    // Si usas AWT/Swing en algún sitio (opcional)
    requires java.desktop;
    requires jdk.jsobject;
    requires javafx.web;

    // Exports si otras capas usan estas APIs
    exports co.edu.uniquindio.poo.amazen;
    exports co.edu.uniquindio.poo.amazen.ViewController;
    exports co.edu.uniquindio.poo.amazen.Model;
    exports co.edu.uniquindio.poo.amazen.Model.Persona;
    exports co.edu.uniquindio.poo.amazen.Model.Strategy;
}