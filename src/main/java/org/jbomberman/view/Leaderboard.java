package org.jbomberman.view;

import javafx.application.Platform;
import javafx.geometry.Pos;
import javafx.scene.control.Label;
import javafx.scene.control.ScrollPane;
import javafx.scene.layout.Pane;
import javafx.scene.layout.StackPane;
import javafx.scene.layout.VBox;
import org.jbomberman.controller.MainController;
import org.jbomberman.utils.SceneManager;

import java.util.Map;

public class Leaderboard {

    private final MainController controller = MainController.getInstance();
    private final Pane leaderboardPane = SceneManager.getP("LEADERBOARD", false, false);

    public Leaderboard() {
        ScrollPane scrollPane = new ScrollPane();
        scrollPane.setPrefSize(150, 150);

        VBox contentPane = new VBox(2); // 10 è lo spazio tra le label

        contentPane.setAlignment(Pos.CENTER);

        for (int i = 1; i <= 10; i++) {
            Label label = new Label("Label " + i);
            label.setStyle("-fx-text-fill: white;");
            contentPane.getChildren().add(label);
        }
        scrollPane.setStyle("-fx-control-inner-background: transparent;");
        contentPane.setStyle("-fx-background-color: transparent;");
        scrollPane.setId("mainScrollPane");
        scrollPane.getStylesheets().add("org/jbomberman/view/scrollPane.css");

        scrollPane.setContent(contentPane);

        Platform.runLater(() -> {
                    double paneWidth = scrollPane.getLayoutBounds().getWidth();
                    double paneHeight = scrollPane.getLayoutBounds().getHeight();

                    double centerX = (double) SceneManager.WIDTH / 2;
                    double centerY = (double) SceneManager.HEIGHT / 2;
                    scrollPane.setLayoutX(centerX - paneWidth / 2);
                    scrollPane.setLayoutY(centerY - paneHeight / 2);
                });

        /*
        Map<String, Integer> leaderboard = controller.loadLeaderboard();

        VBox vbox = new VBox();
        vbox.setAlignment(Pos.CENTER); // Imposta l'allineamento al centro
        vbox.setSpacing(10);

        leaderboard.keySet().forEach(name -> {
            int points = leaderboard.get(name);
            Label player = new Label(name + ": " + points);
            player.setFont(SceneManager.CUSTOM_FONT_SMALL);
            vbox.getChildren().add(player);
        });

        ScrollPane scrollPane = new ScrollPane(vbox);
        scrollPane.setFitToWidth(true);

        vbox.setStyle("-fx-background-color: rgba(0, 0, 0, 0);");
        scrollPane.setStyle("-fx-background-color: rgba(0, 0, 0, 0);");

             */

        leaderboardPane.getChildren().add(scrollPane);
    }

    public Pane getLeaderboardPane() {
        return leaderboardPane;
    }
}
