package ch.ethz.inf.vs.a4.fmorath.pac_man;

import java.util.Comparator;

import ch.ethz.inf.vs.a4.fmorath.pac_man.figures.Figure;

/**
 * Created by linus on 07.12.2016.
 */

public class Player implements Comparable<Player> {

    private int playerId;
    public int getPlayerId(){
        return playerId;
    }
    private Game game;
    public String name;
    private boolean isLocalPlayer;
    public boolean isLocalPlayer() {
        return isLocalPlayer;
    }

    private Figure figure;
    public Figure getFigure() {
        return figure;
    }
    public void setFigure(Figure figure) {
        this.figure = figure;
        figure.setPlayer(this);
    }



    private int score = 0;
    public int getScore() {
        return score;
    }
    public void increaseScore(int amount) {
        score += amount;
        if (score > game.getHighScore())
            game.setHighScore(score);
    }

    public Player(Game game, String name, boolean isLocalPlayer, int playerId) {
        this.game = game;
        this.name = name;
        this.isLocalPlayer = isLocalPlayer;
        this.playerId = playerId;
    }

    @Override
    public String toString() {
        return name;
    }

    @Override
    public int compareTo(Player other) {
        if (score> other.score)
            return -1;
        else if (score < other.score)
            return 1;
        else
            return 0;
    }
}
