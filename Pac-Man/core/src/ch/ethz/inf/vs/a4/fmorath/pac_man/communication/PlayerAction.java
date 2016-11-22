package com.mygdx.game.communication;

import com.mygdx.game.MovementDirection;

/**
 * Created by johannes on 22.11.16.
 */

public class PlayerAction {
    final int playerId;
    final float positionX;
    final float positionY;
    final MovementDirection newDirection;

    public PlayerAction(int playerId, float positionX, float positionY, MovementDirection newDirection){
        this.playerId = playerId;
        this.positionX = positionX;
        this.positionY = positionY;
        this.newDirection = newDirection;
    }
}
