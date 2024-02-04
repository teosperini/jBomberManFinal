package org.jbomberman.utils;

import javafx.application.Platform;
import javafx.scene.Node;
import javafx.scene.control.Label;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.Pane;
import javafx.scene.paint.Color;
import javafx.scene.text.Font;
import javafx.scene.text.Text;
import org.jbomberman.controller.MainController;

public class SceneManager {

    public static final int SCALE_FACTOR = 35;
    public static final int WIDTH = SCALE_FACTOR* MainController.DX;
    public static final int HEIGHT = SCALE_FACTOR*MainController.DY;
    public static final Font CUSTOM_FONT = FontManager.SMALL;
    public static final Font CUSTOM_FONT_SMALL = Font.loadFont(SceneManager.class.getResourceAsStream("/org/jbomberman/SfComicScriptBold-YXD2.ttf"), SCALE_FACTOR-5);


    /**
     *
     * @param string the name of the main text
     * @param opacity set true if the pane needs to be used in game
     * @param main set true if the pane needs to be used as the main pane for the main menu
     * opacity and main can't be true at the same time
     * if neither opacity nor main is true, the pane is used for the main menu (except for the main screen
     * of the main menu)
     * @return
     */
    public static Pane getP(String string, boolean opacity, boolean main) {
        if (opacity && main) {
            System.out.println("you can't set opacity and main at the same time!!");
            return new Pane();
        }
        Font customFont;
        Pane pane;
        Text text = new Text(string);

        if (opacity) {
            pane = getPaneImage(true);
            pane.setStyle("-fx-background-color: rgba(0, 0, 0, 0.8);");

        }else {
            pane = getPaneImage(false);
        }

        if (main) {
            customFont = Font.loadFont(SceneManager.class.getResourceAsStream("/org/jbomberman/SfComicScriptBold-YXD2.ttf"), SCALE_FACTOR + 15);
            text.setFont(customFont);
            text.setStyle("-fx-fill: darkcyan");
        } else {
            customFont = Font.loadFont(SceneManager.class.getResourceAsStream("/org/jbomberman/SfComicScriptBold-YXD2.ttf"), SCALE_FACTOR + 5);
            text.setFont(customFont);
            text.setStyle("-fx-fill: white");
        }

        text.setStroke(Color.BLACK);
        text.setStrokeWidth(2);
        text.setLayoutY(118);
        text.setLayoutX((WIDTH - text.getBoundsInLocal().getWidth()) / 2);
        pane.getChildren().add(text);
        return pane;
    }

    private static Pane getPaneImage(boolean opacity) {
        //l'immagine la carico comunque, ma differenzio tra menu e gioco perchè nel gioco l'immagine la rendo invisibile
        ImageView imageView = new ImageView(new Image(SceneManager.class.getResourceAsStream("/org/jbomberman/sfondo_small.jpg")));
        imageView.setFitHeight(HEIGHT);
        imageView.setFitWidth(WIDTH);
        System.out.println("la larghezza è " + WIDTH);
        if (opacity) imageView.setOpacity(0);
        return new Pane(imageView);
    }

    public static Label getButton(String text, int i, Color color) {
        Text textNode = new Text(text);
        textNode.setFont(CUSTOM_FONT_SMALL);
        textNode.setFill(color);

        Label clickableText = new Label();
        clickableText.setGraphic(textNode);

        clickableText.setOnMouseEntered(event -> {
            textNode.setFill(Color.YELLOW);
        });

        clickableText.setOnMouseExited(event -> {
            textNode.setFill(color);
        });


        Platform.runLater(() -> {
            double textWidth = textNode.getLayoutBounds().getWidth();
            double textHeight = textNode.getLayoutBounds().getHeight();
            double centerX = (double) WIDTH / 2;
            double centerY = (double) HEIGHT / 2;
            clickableText.setLayoutX(centerX - textWidth / 2);
            clickableText.setLayoutY(centerY - textHeight / 2 + (i * SCALE_FACTOR));
        });

        return clickableText;

    }

    public static void changePane(Pane toHide, Pane toShow) {
        toHide.setVisible(false);
        toShow.setVisible(true);
        toShow.toFront();
        toShow.requestFocus();
    }

    public static Label getPoints(String string, Coordinate coordinate){
        Label text = new Label("+" + string);
        text.setFont(CUSTOM_FONT_SMALL);
        text.setLayoutX(((double)coordinate.x() * SCALE_FACTOR)-30);
        text.setLayoutY((((double)coordinate.y()) * SCALE_FACTOR)-10);
        return text;
    }


    public static ImageView createImageView(Coordinate c, Image image) {
        ImageView imageView = new ImageView(image);
        imageView.setLayoutX((double)c.x() * SCALE_FACTOR);
        imageView.setLayoutY((double)c.y() * SCALE_FACTOR);
        imageView.setFitHeight(SCALE_FACTOR);
        imageView.setFitWidth(SCALE_FACTOR);
        return imageView;
    }

    public static void setCentred(Node node){
        Platform.runLater(() -> {
            double textWidth = node.getLayoutBounds().getWidth();
            double textHeight = node.getLayoutBounds().getHeight();

            double centerX = (double) SceneManager.WIDTH / 2;
            double centerY = (double) SceneManager.HEIGHT / 2;
            node.setLayoutX(centerX - textWidth / 2);
            node.setLayoutY(centerY - textHeight / 2);
        });
    }
}