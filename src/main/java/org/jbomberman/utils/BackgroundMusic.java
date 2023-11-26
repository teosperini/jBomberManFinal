package org.jbomberman.utils;

import javafx.scene.media.AudioClip;
import javafx.scene.media.Media;
import javafx.scene.media.MediaPlayer;

import java.io.InputStream;
import java.nio.file.Paths;
public class BackgroundMusic {
    private static final String PATH = System.getProperty("user.dir");
    private static final InputStream GAMESOUNDTRACK = (BackgroundMusic.class.getResourceAsStream("org/jbomberman/utils/tnt_exp.mp3"));
    private static final AudioClip GAMEBOMB = new AudioClip(BackgroundMusic.class.getResource("utils/nuke.mp3").toExternalForm());


    private static MediaPlayer mediaPlayer;


    public static void playMusic(){
        // Carica il file audio da riprodurre
        System.out.println();
        Media media = new Media(GAMESOUNDTRACK.toString());
        mediaPlayer =  new MediaPlayer(media);
        mediaPlayer.setCycleCount(MediaPlayer.INDEFINITE); // Riproduce la musica in modo continuo
        mediaPlayer.play();
    }

    public static void stopMusic(){
        mediaPlayer.stop();
    }

    public static void playBomb(){
        GAMEBOMB.play();
    }

    //capire bene come funziona
    public static void setVolume(){
        mediaPlayer.setVolume(50);
    }
}