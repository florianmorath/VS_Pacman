package ch.ethz.inf.vs.a4.fmorath.pac_man.communication;

import com.badlogic.gdx.net.Socket;
import com.badlogic.gdx.utils.DataOutput;
import ch.ethz.inf.vs.a4.fmorath.pac_man.MovementDirection;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.io.OutputStream;

/**
 * Created by johannes on 22.11.16.
 */

/**
 * Class that provides the communication Protocol.
 */
abstract class GameCommunicator implements CommunicationConstants{
    /**
     * Send an user action to a player.
     * @param stream Output stream to the socket of the player
     * @param action The action to be sent
     * @throws IOException
     */
    public static void sendAction(DataOutputStream stream, PlayerAction action) throws IOException {
        if(stream == null || action.playerId < 0){
            throw new IllegalArgumentException();
        }
        stream.writeInt(action.playerId);
        stream.writeFloat(action.positionX);
        stream.writeFloat(action.positionY);
        stream.writeInt(action.newDirection.getValue());
    }

    /**
     * Wait for new action.
     * @param stream Input stream to the socket of the client/server that sends new actions.
     * @return The new action.
     * @throws IOException
     */
    public static PlayerAction receiveAction(DataInputStream stream) throws IOException {
        if(stream == null){
            throw new IllegalArgumentException();
        }
        int playerId = stream.readInt();
        float posX = stream.readFloat();
        float posY = stream.readFloat();
        MovementDirection newDirection = MovementDirection.createDirectionFromInt(stream.readInt());
        return new PlayerAction(playerId,posX,posY,newDirection);
    }

    /**
     * Notify clients that the game ends. Clients send the signal back to server to acknowledge the end and simplify clean-up for server.
     * @param stream Write the stop signal to this stream.
     * @throws IOException
     */
    public static void sendStopSignal(DataOutputStream stream) throws IOException {
        if(stream == null){
            throw new IllegalArgumentException();
        }
        stream.writeInt(-1);
        stream.writeFloat(0);
        stream.writeFloat(0);
        stream.writeInt(0);
    }

    /**
     * When all clients are connected to the game then the server sends this signal to each client to initiate the game.
     * @param stream Start signal is written to this stream.
     * @throws IOException
     */
    public static void sendStartSignal(DataOutputStream stream, int playerId, int numPlayers) throws IOException{
        if(stream == null){
            throw new IllegalArgumentException();
        }
        stream.writeByte(playerId);
        stream.writeByte(numPlayers);
    }

    /**
     * After connecting to the server, the clients have to wait for the server to start the game by sending the start signal.
     * @param stream Start signal will be written to this stream by server.
     * @throws IOException
     */
    public static int[] waitForStartSignal(DataInputStream stream) throws IOException{
        if(stream == null){
            throw new IllegalArgumentException();
        }
        int myId = stream.readByte();
        int numPlayers = stream.readByte();
        int[] res = {myId, numPlayers};
        return res;
    }
}
