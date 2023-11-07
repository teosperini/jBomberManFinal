module com.jbomberman.jbomberman {
    requires javafx.controls;
    requires javafx.fxml;
    requires javafx.media;

    opens org.jbomberman to javafx.fxml;
    exports org.jbomberman;
}