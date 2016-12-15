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
abstract class GameCommunicator{

    /**
     * Class used for the receive function that has to listen for new players and the start signal.
     */
    public static class NameIdStart{
        public final String name;
        public final int id;
        public final boolean started;

        /**
         * Constructor
         * @param name Name of a new Player.
         * @param id Id of the new Player.
         * @param started True if start signal was received, then the other two fields are meaningless.
         */
        public NameIdStart(String name, int id, boolean started){
            this.name = name;
            this.id = id;
            this.started = started;
        }
    }



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
        stream.writeInt(action.eatenPlayerId);
        stream.writeInt(action.currentScore);
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
        int eatenPlayerId = stream.readInt();
        int currentScore = stream.readInt();
        return new PlayerAction(playerId,posX,posY,newDirection,eatenPlayerId, currentScore);
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
     * This will be used by the server to send joining players (name and id) to all the clients.
     * @param stream The ouput stream from the connection
     * @param name Name of new Player
     * @param playerId Id of new Player
     * @throws IOException
     */
    public static void sendPlayerNameAndId(DataOutputStream stream, String name, int playerId) throws IOException {
        if(stream == null){
            throw new IllegalArgumentException();
        }
        stream.writeBoolean(false);
        stream.writeUTF(name);
        stream.writeByte(playerId);
    }

    /**
     * A new client will send his name to the server.
     * @param stream Connection to the server
     * @param name The name of the new player
     * @throws IOException
     */
    public static void sendPlayerName(DataOutputStream stream, String name) throws IOException{
        if(stream == null){
            throw new IllegalArgumentException();
        }
        stream.writeUTF(name);
    }

    /**
     * Server uses this function to listen to new clients.
     * @param stream
     * @return
     * @throws IOException
     */
    public static String receivePlayerName(DataInputStream stream) throws IOException {
        if(stream == null){
            throw new IllegalArgumentException();
        }
        return stream.readUTF();
    }


    /**
     * Clients will use this methods to wait for incoming information from the client before the game starts.
     * This can either be new players, or the start signal. In order to combine these possibilities the class NameIdStart was add for the output
     * @param stream connection to the server
     * @return Either the start signal, or a new user.
     * @throws IOException
     */
    public static NameIdStart waitForStartSignalAndNames(DataInputStream stream) throws IOException{
        if(stream == null){
            throw new IllegalArgumentException();
        }
        if (stream.readByte() == 1) {
            return new NameIdStart(null, -1, true);
        }
        String name = stream.readUTF();
        int id = stream.readByte();
        return new NameIdStart(name, id, false);
    }

    /**
     * Used by the Server to send start signal to all clients and start the game.
     * @param stream connection to a client
     * @throws IOException
     */
    public static void sendStartSignal(DataOutputStream stream) throws IOException {
        if(stream == null){
            throw new IllegalArgumentException();
        }
        stream.writeByte(1);
    }


}
