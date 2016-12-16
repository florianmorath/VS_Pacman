package ch.ethz.inf.vs.a4.fmorath.pac_man.actions;

import ch.ethz.inf.vs.a4.fmorath.pac_man.MovementDirection;

/**
 * Created by linus on 16.12.2016.
 */

public class MovementAction extends Action {
    public final float positionX;
    public final float positionY;
    public final MovementDirection newDirection;

    public MovementAction(int playerId, boolean sendResponseToRequestingClient, float positionX, float positionY, MovementDirection newDirection) {
        super(ActionType.Movement, playerId, sendResponseToRequestingClient);
        this.positionX = positionX;
        this.positionY = positionY;
        this.newDirection = newDirection;
    }
}
