package org.jbomberman.view;
import javafx.application.Application;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.scene.Scene;
import javafx.scene.control.ComboBox;
import javafx.scene.control.TextField;
import javafx.scene.layout.StackPane;
import javafx.stage.Stage;

public class Prova extends Application {

        public static void main(String[] args) {
            launch(args);
        }

        @Override
        public void start(Stage primaryStage) {
            primaryStage.setTitle("Esempio Menu a Tendina");

            // Crea una lista di opzioni per il menu a tendina
            ObservableList<String> opzioni = FXCollections.observableArrayList(
                    "Opzione 1",
                    "Opzione 2",
                    "Opzione 3",
                    "Opzione 4"
            );

            // Crea il menu a tendina e imposta le opzioni
            ComboBox<String> menuATendina = new ComboBox<>(opzioni);
            menuATendina.setPromptText("Seleziona un'opzione"); // Testo di prompt
            menuATendina.setStyle("-fx-background-color: lightblue; -fx-font-size: 16px;");
            // Gestisci l'evento quando l'utente seleziona un'opzione
            menuATendina.setOnAction(e -> {
                String opzioneSelezionata = menuATendina.getValue();
                System.out.println("Opzione selezionata: " + opzioneSelezionata);
            });
            TextField campoDiTesto = new TextField();
            campoDiTesto.setLayoutY(10);
            campoDiTesto.setPromptText("Inserisci il tuo testo qui");

            //primaryStage.setTitle(campoDiTesto.getText());

            StackPane root = new StackPane();
            root.getChildren().addAll(menuATendina, campoDiTesto);

            primaryStage.setScene(new Scene(root, 300, 250));
            primaryStage.show();
        }
    }