module com.jbomberman.jbomberman {
    requires javafx.controls;
    requires javafx.fxml;
    requires javafx.media;
    requires org.json;

    opens org.jbomberman to javafx.fxml;
    exports org.jbomberman;
}