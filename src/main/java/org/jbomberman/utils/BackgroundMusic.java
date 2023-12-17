package org.jbomberman.utils;

import javafx.scene.media.AudioClip;
import javafx.scene.media.Media;
import javafx.scene.media.MediaPlayer;

public class BackgroundMusic {
    private static final String PATH = System.getProperty("user.dir");
    private static final String GAMESOUNDTRACK = BackgroundMusic.class.getResource("UndertaleOST.mp3").toExternalForm();
    private static final AudioClip GAMEBOMB = new AudioClip(BackgroundMusic.class.getResource("tnt_exp.mp3").toExternalForm());
    private static final AudioClip SUCCESS = new AudioClip(BackgroundMusic.class.getResource("success.mp3").toExternalForm());

    private static MediaPlayer mediaPlayer;


    public static void playMusic(){
        // Carica il file audio da riprodurre
        Media media = new Media(GAMESOUNDTRACK);
        System.out.println("source: " + GAMEBOMB.getSource());
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
        mediaPlayer.setVolume(20);
    }

    public static void playSuccess() {
        SUCCESS.play();
    }
}