package com.mygdx.game.communication;

import com.badlogic.gdx.net.Socket;
import com.badlogic.gdx.utils.DataOutput;
import com.mygdx.game.MovementDirection;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.io.OutputStream;

/**
 * Created by johannes on 22.11.16.
 */

public abstract class GameCommunicator implements CommunicationConstants{
    public static void sendAction(DataOutputStream stream, PlayerAction action) throws IOException {
        stream.writeInt(action.playerId);
        stream.writeFloat(action.positionX);
        stream.writeFloat(action.positionY);
        stream.writeInt(action.newDirection.getValue());
    }

    public static PlayerAction receiveAction(DataInputStream stream) throws IOException {
        int playerId = stream.readInt();
        float posX = stream.readFloat();
        float posY = stream.readFloat();
        MovementDirection newDirection = MovementDirection.createDirectionFromInt(stream.readInt());
        return new PlayerAction(playerId,posX,posY,newDirection);
    }
}
