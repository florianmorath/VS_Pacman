package ch.ethz.inf.vs.a4.fmorath.pac_man.communication;

import ch.ethz.inf.vs.a4.fmorath.pac_man.MovementDirection;

/**
 * Created by johannes on 22.11.16.
 */

/**
 * Class that describes an action (change of direction) taken by the user.
 * Todo: make fields public or write getters/setters if this class should be used by other parts of the game.
 */
public class PlayerAction {
    private static final int EMPTY_PLAYER_ID = -1;

    public final int playerId;
    public final float positionX;
    public final float positionY;
    public final MovementDirection newDirection;
    public final int eatenPlayerId;
    public final int currentScore;

    /**
     * Constructor.
     * @param playerId Id of the player that took an action.
     * @param positionX X position where the player took the action
     * @param positionY Y position where the player took the action.
     * @param newDirection the new direction of the player.
     */
    public PlayerAction(int playerId, float positionX, float positionY, MovementDirection newDirection, int eatenPlayerId, int currentScore){
        this.playerId = playerId;
        this.positionX = positionX;
        this.positionY = positionY;
        this.newDirection = newDirection;
        this.eatenPlayerId = eatenPlayerId;
        this.currentScore = currentScore;
    }
    public PlayerAction(int playerId, float positionX, float positionY, MovementDirection direction, int currentScore){
        this(playerId, positionX, positionY, direction, EMPTY_PLAYER_ID, currentScore);
    }

    public PlayerAction(int playerId, float positionX, float positionY, MovementDirection direction){
        this(playerId, positionX, positionY, direction, 0);
    }


    public boolean hasEatenPlayer(){
        return eatenPlayerId != EMPTY_PLAYER_ID;
    }


}
