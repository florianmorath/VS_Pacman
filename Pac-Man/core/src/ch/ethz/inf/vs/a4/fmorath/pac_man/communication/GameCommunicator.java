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
    public static class NameIdStart{
        public final String name;
        public final int id;
        public final boolean started;
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

    public static void sendPlayerNameAndId(DataOutputStream stream, String name, int playerId) throws IOException {
        if(stream == null){
            throw new IllegalArgumentException();
        }
        stream.writeBoolean(false);
        stream.writeUTF(name);
        stream.writeByte(playerId);
    }

    public static void sendPlayerName(DataOutputStream stream, String name) throws IOException{
        if(stream == null){
            throw new IllegalArgumentException();
        }
        stream.writeUTF(name);
    }

    public static String receivePlayerName(DataInputStream stream) throws IOException {
        if(stream == null){
            throw new IllegalArgumentException();
        }
        return stream.readUTF();
    }

    /*
    public static NameIdStart receivePlayerNameAndId(DataInputStream stream) throws IOException{
        if(stream == null){
            throw new IllegalArgumentException();
        }
        String name = stream.readUTF();
        int id = stream.readByte();
        return new NameIdStart(name, id);
    }*/


    public static NameIdStart waitForStartSignalAndNames(DataInputStream stream) throws IOException{
        if(stream == null){
            throw new IllegalArgumentException();
        }
        if (stream == null) {
            throw new IllegalArgumentException();
        }
        if (stream.readBoolean()) {
            return new NameIdStart(null, -1, true);
        }
        String name = stream.readUTF();
        int id = stream.readByte();
        return new NameIdStart(name, id, false);
    }

    public static void sendStartSignal(DataOutputStream stream) throws IOException {
        if(stream == null){
            throw new IllegalArgumentException();
        }
        stream.writeBoolean(true);
    }



    /**
     * TODO: Remove.
     * When all clients are connected to the game then the server sends this signal to each client to initiate the game.
     * @param stream Start signal is written to this stream.
     * @throws IOException
     */
    public static void sendStartSignal_old(DataOutputStream stream, int playerId, int numPlayers) throws IOException{
        if(stream == null){
            throw new IllegalArgumentException();
        }
        stream.writeByte(playerId);
        stream.writeByte(numPlayers);
    }

    /**
     * TODO: Remove.
     * After connecting to the server, the clients have to wait for the server to start the game by sending the start signal.
     * @param stream Start signal will be written to this stream by server.
     * @throws IOException
     */
    public static int[] waitForStartSignal_old(DataInputStream stream) throws IOException{
        if(stream == null){
            throw new IllegalArgumentException();
        }
        int myId = stream.readByte();
        int numPlayers = stream.readByte();
        int[] res = {myId, numPlayers};
        return res;
    }

}
