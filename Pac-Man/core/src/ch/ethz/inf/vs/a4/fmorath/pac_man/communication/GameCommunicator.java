package ch.ethz.inf.vs.a4.fmorath.pac_man.communication;

import ch.ethz.inf.vs.a4.fmorath.pac_man.MovementDirection;
import ch.ethz.inf.vs.a4.fmorath.pac_man.actions.Action;
import ch.ethz.inf.vs.a4.fmorath.pac_man.actions.ActionType;
import ch.ethz.inf.vs.a4.fmorath.pac_man.actions.DisconnectPlayerAction;
import ch.ethz.inf.vs.a4.fmorath.pac_man.actions.EatCoinAction;
import ch.ethz.inf.vs.a4.fmorath.pac_man.actions.EatPlayerAction;
import ch.ethz.inf.vs.a4.fmorath.pac_man.actions.MovementAction;
import ch.ethz.inf.vs.a4.fmorath.pac_man.actions.StopGameAction;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;

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
        public final boolean disconnect;

        /**
         * Constructor
         * @param name Name of a new Player.
         * @param id Id of the new Player.
         * @param started True if start signal was received, then the other two fields are meaningless.
         */
        public NameIdStart(String name, int id, boolean started, boolean disconnect){
            this.name = name;
            this.id = id;
            this.started = started;
            this.disconnect = disconnect;
        }
    }

    /**
     * Send an user action to a player.
     * @param stream Output stream to the socket of the player
     * @param action The action to be sent
     * @throws IOException
     */
    public static void sendAction(DataOutputStream stream, Action action) throws IOException {
        if(stream == null){
            throw new IllegalArgumentException();
        }

        stream.writeInt(action.type.getValue());
        stream.writeInt(action.playerId);
        stream.writeBoolean(action.sendResponseToRequestingClient);

        switch (action.type) {
            case Movement:
                MovementAction movementAction = (MovementAction) action;
                stream.writeFloat(movementAction.positionX);
                stream.writeFloat(movementAction.positionY);
                stream.writeInt(movementAction.newDirection.getValue());
                break;
            case EatCoin:
                EatCoinAction eatCoinAction = (EatCoinAction) action;
                stream.writeInt(eatCoinAction.eatenCoinIndex);
                break;
            case EatPlayer:
                EatPlayerAction eatPlayerAction = (EatPlayerAction) action;
                stream.writeInt(eatPlayerAction.eatenPlayerId);
                break;
            case DisconnectPlayer:
                break;
        }
    }


    /**
     * Wait for new action.
     * @param stream Input stream to the socket of the client/server that sends new actions.
     * @return The new action.
     * @throws IOException
     */
    public static Action receiveAction(DataInputStream stream) throws IOException {
        if(stream == null){
            throw new IllegalArgumentException();
        }

        ActionType actionType = ActionType.getType(stream.readInt());
        int playerId = stream.readInt();
        boolean sendResponseToRequestingClient = stream.readBoolean();

        switch (actionType) {
            case Movement:
                float posX = stream.readFloat();
                float posY = stream.readFloat();
                MovementDirection newDirection = MovementDirection.getDirection(stream.readInt());
                return new MovementAction(playerId, sendResponseToRequestingClient, posX, posY, newDirection);
            case EatCoin:
                int eatenCoinIndex = stream.readInt();
                return new EatCoinAction(playerId, sendResponseToRequestingClient, eatenCoinIndex);
            case EatPlayer:
                int eatenPlayerId = stream.readInt();
                return new EatPlayerAction(playerId, sendResponseToRequestingClient, eatenPlayerId);
            case DisconnectPlayer:
                return new DisconnectPlayerAction(playerId);
            case StopGame:
                return new StopGameAction();
            default:
                return null;
        }
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
        sendAction(stream, new StopGameAction());
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
        stream.writeBoolean(false);
    }

    public static void sendPlayerDisconnected(DataOutputStream stream, int playerId) throws IOException {
        if(stream == null){
            throw new IllegalArgumentException();
        }
        stream.writeBoolean(false);
        stream.writeUTF("");
        stream.writeByte(playerId);
        stream.writeBoolean(true);
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
        if (stream.readBoolean()) {
            return new NameIdStart(null, -1, true, false);
        }
        String name = stream.readUTF();
        int id = stream.readByte();
        boolean disconnect = stream.readBoolean();
        return new NameIdStart(name, id, false, disconnect);
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
        stream.writeBoolean(true);
    }


}
