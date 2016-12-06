package ch.ethz.inf.vs.a4.fmorath.pac_man;

import ch.ethz.inf.vs.a4.fmorath.pac_man.figures.Figure;

/**
 * Created by linus on 07.12.2016.
 */

public class Player {

    private Game game;

    private Figure figure;
    public Figure getFigure() {
        return figure;
    }
    public void setFigure(Figure figure) {
        this.figure = figure;
    }

    private int score = 0;
    public int getScore() {
        return score;
    }
    public void resetScore(int score) {
        this.score = 0;
    }
    public void increaseScore(int amount) {
        score += amount;
        if (score > game.getHighscore())
            game.setHighscore(score);
    }

    public Player(Game game) {
        this.game = game;
    }
}
