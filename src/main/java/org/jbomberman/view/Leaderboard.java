package org.jbomberman.view;


import javafx.scene.control.Label;
import javafx.scene.control.ScrollPane;
import javafx.scene.layout.Pane;
import javafx.scene.layout.VBox;
import org.jbomberman.utils.SceneManager;
import org.json.JSONArray;
import org.json.JSONObject;

import java.io.FileReader;

public class Leaderboard{
    private final Pane leaderboardPane = SceneManager.getP("LEADERBOARD", false,false);;
    public Pane getLeaderboard() {
        return leaderboardPane;
    }
    private void createLeaderboard(){
    // Carica i dati dalla leaderboard (puoi mettere questo in una funzione a parte)
        JSONArray leaderboard = loadLeaderboardData();

        // Crea una VBox per organizzare i dati nella scena
        VBox vbox = new VBox();
        vbox.setSpacing(10);

        // Aggiungi un'intestazione
        Label headerLabel = new Label("Leaderboard");
        vbox.getChildren().add(headerLabel);

        // Itera attraverso i dati e crea una Label per ciascun giocatore
        for (int i = 0; i < leaderboard.length(); i++) {
            JSONObject player = leaderboard.getJSONObject(i);
            String nome = player.getString("nome");
            int punteggio = player.getInt("punteggio");

            Label playerLabel = new Label(nome + ": " + punteggio);
            vbox.getChildren().add(playerLabel);
        }

        // Crea un componente ScrollPane e imposta la VBox come contenuto
        ScrollPane leaderboardPane = new ScrollPane(vbox);
        leaderboardPane.setFitToWidth(true);
    }
    // Funzione per caricare i dati dalla leaderboard (puoi sostituirla con la tua logica di caricamento)
    private JSONArray loadLeaderboardData() {
        /*

        try {
            return new JSONArray(new FileReader(Leaderboard.class.getResourceAsStream("leaderboard.json")));
        } catch (Exception e) {
            e.printStackTrace();
            return new JSONArray(); // Ritorna un array vuoto in caso di errore
        }

         */
    return null;
    }
}
