package org.jbomberman.view;

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

public class Profile {
    private String nickname;
    private String avatar;
    private int gamesPlayed;
    private int gamesWon;
    private int gamesLost;
    private int level;
    private Pane mainProfile = SceneManager.getPane("PROFILE", 40);;
    private Pane chooser;

    public Profile() {
        nickname = "Guest";
        avatar = "Avatar";
        createChooser();
        createProfileWindow();
    }

    private void createProfileWindow() {
        mainProfile.setOnKeyPressed(keyEvent -> {
            if (keyEvent.getCode() == KeyCode.ESCAPE) {
                mainProfile.setVisible(false);
            }
        });
        Label chooseProfile = SceneManager.getButton("choose profile", 1, Color.WHITE);
        chooseProfile.setOnMouseClicked(event -> {
            chooser.setVisible(true);
        });
        mainProfile.getChildren().addAll(SceneManager.backButton(mainProfile),chooseProfile ,chooser);
    }

    private void createChooser(){
        chooser = SceneManager.getPane("choose your avatar",40);
        HBox hbox = new HBox();
        hbox.setAlignment(Pos.CENTER);
        hbox.setStyle("-fx-background-color: rgba(0, 0, 0, 0.0)");
        ImageView imageView1 = new ImageView(new Image("/org/jbomberman/EndermanFace.png"));
        ImageView imageView2 = new ImageView(new Image("/org/jbomberman/view/definitive/steve.png"));
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
                chooser.setVisible(false);
                mainProfile.setVisible(false);
            });
        }


        chooser.setOnKeyPressed(keyEvent -> {
            if (keyEvent.getCode() == KeyCode.ESCAPE) {
                chooser.setVisible(false);
                mainProfile.setVisible(true);
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
    }

    public Pane getProfile() {
        return mainProfile;
    }
}