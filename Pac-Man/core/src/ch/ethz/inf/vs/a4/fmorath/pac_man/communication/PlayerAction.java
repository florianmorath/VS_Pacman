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
    public final int playerId;
    public final float positionX;
    public final float positionY;
    public final MovementDirection newDirection;

    /**
     * Constructor.
     * @param playerId Id of the player that took an action.
     * @param positionX X position where the player took the action
     * @param positionY Y position where the player took the action.
     * @param newDirection the new direction of the player.
     */
    public PlayerAction(int playerId, float positionX, float positionY, MovementDirection newDirection){
        this.playerId = playerId;
        this.positionX = positionX;
        this.positionY = positionY;
        this.newDirection = newDirection;
    }
}
