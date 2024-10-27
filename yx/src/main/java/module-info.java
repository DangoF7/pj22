module com.example.dem {
    requires javafx.controls;
    requires javafx.fxml;
    requires javafx.media;
    requires java.logging;
    requires javafx.graphics;


    opens com.example.yx to javafx.fxml;
    exports com.example.yx;
}