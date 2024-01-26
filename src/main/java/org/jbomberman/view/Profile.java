package org.jbomberman.view;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.scene.control.ComboBox;
import javafx.scene.control.TextField;
import javafx.scene.control.TextFormatter;
import org.jbomberman.controller.MainController;
import org.jbomberman.utils.Coordinate;
import org.jbomberman.utils.SceneManager;
import javafx.application.Platform;
import javafx.geometry.Pos;
import javafx.scene.control.Label;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.input.KeyCode;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Pane;
import javafx.scene.paint.Color;

import java.util.List;

import static org.jbomberman.utils.SceneManager.*;
import static org.jbomberman.utils.SceneManager.SCALE_FACTOR;

public class Profile {
    private final MainController controller = MainController.getInstance();
    private String nickname;
    private List<Color> avatarList = List.of(Color.RED);
    private Color avatar;
    private int gamesPlayed;
    private int gamesWon;
    private int gamesLost;
    private final Pane mainProfile = SceneManager.getP("PROFILE", false,false);;
    private Pane chooser;

    public Profile() {
        //default settings
        nickname = "Guest";
        avatar = avatarList.get(0);
        //createChooser();
        //createProfileWindow();
    }

    private void createProfileWindow() {
        /*
        mainProfile.setOnMouseClicked(event -> mainProfile.requestFocus());

        TextField textField = new TextField();
        mainProfile.getChildren().addAll(textField);

        Platform.runLater(() -> {
            double textWidth = textField.getLayoutBounds().getWidth();
            double textHeight = textField.getLayoutBounds().getHeight();

            double centerX = (double) SceneManager.WIDTH / 2;
            double centerY = (double) SceneManager.HEIGHT / 2;
            textField.setLayoutX(centerX - textWidth / 2);
            textField.setLayoutY(centerY - textHeight / 2);
        });

        int maxLength = 8;

        TextFormatter<String> textFormatter = new TextFormatter<>(change -> {
            if (change.isAdded() && change.getControlNewText().length() > maxLength) {
                return null; // Ignora il cambiamento se supera il limite
            }
            return change;
        });

        textField.setTextFormatter(textFormatter);

        String string = "nickname";
        textField.setPromptText(string);

        if (textField.isFocused()) {
            textField.setPromptText("");
        }
        else {
            textField.setPromptText(string);
        }

        textField.setOnKeyPressed(keyEvent -> {
            ImageView imageView = new ImageView(new Image(Profile.class.getResourceAsStream("definitive/ok.png")));
            imageView.setLayoutX((double) SceneManager.WIDTH /3);
            imageView.setLayoutY((double) SceneManager.HEIGHT /2 - (double) SCALE_FACTOR /2);
            imageView.setFitHeight(SCALE_FACTOR);
            imageView.setFitWidth(SCALE_FACTOR);
            if (keyEvent.getCode().equals(KeyCode.ENTER)){
                nickname = textField.getText();
                textField.clear();
                mainProfile.requestFocus();
                mainProfile.getChildren().add(imageView);
                controller.setNick(nickname);
            }
        });

         */
    }

    private void createChooser(){
        /*
        chooser = SceneManager.getP("choose your avatar",false,false);
        HBox hbox = new HBox();
        hbox.setAlignment(Pos.CENTER);
        hbox.setStyle("-fx-background-color: rgba(0, 0, 0, 0.0)");
        ImageView imageView1 = new ImageView(new Image("/org/jbomberman/EndermanFace.png"));
        ImageView imageView2 = new ImageView(new Image("/org/jbomberman/view/definitive/steve.png"));
        imageView1.setLayoutX(100);
        imageView2.setLayoutX(110);
        imageView1.setFitHeight(50);
        imageView1.setFitWidth(50);
        imageView2.setFitHeight(50);
        imageView2.setFitWidth(50);
        imageView1.setOnMouseEntered(event -> {
            imageView1.setOpacity(0.5);
        });
        imageView1.setOnMouseExited(event -> {
            imageView1.setOpacity(1);
        });
        imageView2.setOnMouseEntered(event -> {
            imageView2.setOpacity(0.5);
        });
        imageView2.setOnMouseExited(event -> {
            imageView2.setOpacity(1);
        });

        ImageView[] images = {imageView1, imageView2};
        for (ImageView imageView : images) {
            imageView.setOnMouseClicked(event -> {
                SceneManager.changePane(chooser, mainProfile);
            });
        }


        chooser.setOnKeyPressed(keyEvent -> {
            if (keyEvent.getCode() == KeyCode.ESCAPE) {
                SceneManager.changePane(chooser, mainProfile);
            }
        });
        Label back = SceneManager.backButtonInternal(chooser, mainProfile);

        hbox.getChildren().addAll(imageView1, imageView2);
        chooser.getChildren().addAll(hbox,back);
        chooser.setVisible(false);
        Platform.runLater(() -> {
            hbox.setLayoutX((chooser.getWidth() - hbox.getWidth()) / 2);
            hbox.setLayoutY((chooser.getHeight() - hbox.getHeight()) / 2);
        });

         */
    }

    public Pane getProfile() {
        return mainProfile;
    }
}