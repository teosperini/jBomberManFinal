package org.jbomberman.utils;

import javafx.application.Platform;
import javafx.scene.control.Label;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.Pane;
import javafx.scene.paint.Color;
import javafx.scene.text.Font;
import javafx.scene.text.Text;

public class SceneManager {

    public static final int WIDTH = 594;
    public static final int HEIGHT = 420;
    private static final Font CUSTOM_FONT_SMALL = Font.loadFont(SceneManager.class.getResourceAsStream("/org/jbomberman/SfComicScriptBold-YXD2.ttf"), 30.0);
    public static Pane getSTPane(String stringa, int fontWidth) {
        Font customFont = Font.loadFont(SceneManager.class.getResourceAsStream("/org/jbomberman/SfComicScriptBold-YXD2.ttf"), fontWidth);
        Pane pane = new Pane();
        pane.setPrefWidth(WIDTH);
        pane.setPrefHeight(HEIGHT);
        pane.setStyle("-fx-background-color: rgba(0, 0, 0, 0.9);");

        Text text = new Text(stringa);
        text.setFont(customFont);
        text.setStyle("-fx-fill: white");

        text.setLayoutY(118);
        text.setLayoutX((WIDTH - text.getBoundsInLocal().getWidth()) / 2);

        pane.getChildren().add(text);
        return pane;
    }

    public static Pane getPane(String stringa, int fontWidth){
        Font customFont = Font.loadFont(SceneManager.class.getResourceAsStream("/org/jbomberman/SfComicScriptBold-YXD2.ttf"), fontWidth);
        Pane pane = getPaneImage();
        Text text = new Text(stringa);
        text.setFont(customFont);
        text.setStyle("-fx-fill: white");
        text.setStroke(Color.BLACK);
        text.setStrokeWidth(2);

        text.setLayoutY(118);
        text.setLayoutX((WIDTH - text.getBoundsInLocal().getWidth()) / 2);

        pane.getChildren().add(text);
        return pane;
    }



    public static Pane getTitlePane(String stringa, int fontWidth) {
        Font customFont = Font.loadFont(SceneManager.class.getResourceAsStream("/org/jbomberman/SfComicScriptBold-YXD2.ttf"), fontWidth);

        Pane pane = getPaneImage();

        pane.setStyle("-fx-background-color: rgba(0, 0, 0, 0.9);");

        Text text = new Text(stringa);
        text.setFont(customFont);
        text.setStyle("-fx-fill: darkcyan");
        text.setStroke(Color.BLACK);
        text.setStrokeWidth(2);

        text.setLayoutY(118);
        text.setLayoutX((WIDTH - text.getBoundsInLocal().getWidth()) / 2);

        pane.getChildren().add(text);
        return pane;
    }

    private static Pane getPaneImage() {
        ImageView imageView = new ImageView(new Image(SceneManager.class.getResourceAsStream("/org/jbomberman/sfondo_small.jpg")));;
        imageView.setFitHeight(HEIGHT);
        imageView.setFitWidth(WIDTH);
        Pane pane = new Pane(imageView);
        pane.setPrefWidth(WIDTH);
        pane.setPrefHeight(HEIGHT);
        return pane;
    }

    public static Label getButton(String text, int i, Color color) {
        Label clickableText = new Label(text);
        clickableText.setFont(CUSTOM_FONT_SMALL);
        //clickableText.setStyle("-fx-text-fill: white;");
        clickableText.setTextFill(color);



        clickableText.setOnMouseEntered(event ->{
            clickableText.setTextFill(Color.YELLOW);
        });
        clickableText.setOnMouseExited(event ->{
            clickableText.setTextFill(color);
        });

        Platform.runLater(() -> {
            double centerX = (double) WIDTH / 2;
            double centerY = (double) HEIGHT / 2;
            clickableText.setLayoutX(centerX - clickableText.getWidth() / 2);
            clickableText.setLayoutY(centerY - clickableText.getHeight() / 2 + (i *50));
        });

        return clickableText;
    }


    /**
     * Creates a back button for the given pane
     * @param pane
     * @return
     */
    public static Label backButton(Pane pane) {
        Label backButton = SceneManager.getButton("back", 3, Color.WHITE);
        backButton.setOnMouseClicked(event -> {
            pane.setVisible(false);
        });
        return backButton;
    }

    public static Label backButtonInternal(Pane current, Pane below){
        Label backButton = SceneManager.getButton("back", 3, Color.WHITE);
        backButton.setOnMouseClicked(event -> {
            current.setVisible(false);
            below.setVisible(true);
        });
        return backButton;
    }
}
