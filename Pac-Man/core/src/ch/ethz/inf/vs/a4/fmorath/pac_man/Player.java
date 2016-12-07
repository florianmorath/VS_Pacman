package ch.ethz.inf.vs.a4.fmorath.pac_man;

import ch.ethz.inf.vs.a4.fmorath.pac_man.figures.Figure;

/**
 * Created by linus on 07.12.2016.
 */

public class Player {

    private Game game;
    private String name;
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

    public Player(Game game, String name, boolean isLocalPlayer) {
        this.game = game;
        this.name = name;
        this.isLocalPlayer = isLocalPlayer;
    }

    @Override
    public String toString() {
        return name;
    }
}
