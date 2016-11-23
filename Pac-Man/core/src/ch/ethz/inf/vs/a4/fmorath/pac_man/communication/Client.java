package ch.ethz.inf.vs.a4.fmorath.pac_man.communication;


import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Net;
import com.badlogic.gdx.net.SocketHints;
import com.badlogic.gdx.net.Socket;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;

/**
 * Created by johannes on 22.11.16.
 */

/**
 * Client class for the Pac Man game communication protocol.
 */
public class Client implements CommunicationConstants{
    private PlayerActionHandler handler;
    private Socket socket;
    private DataInputStream in;
    private DataOutputStream out;
    private SendingQueue sendingQueue;

    /**
     * Constructor.
     * @param handler The handler that reacts to messages from the Network.
     */
    public Client(PlayerActionHandler handler){
        this.handler = handler;
        this.socket = null;
        this.sendingQueue = null;
    }

    /**
     * 1. Connect to the server.
     * 2. Start the sending queue thread that allows to send actions to the server.
     * 3. Start receiving thread that listens to actions from other players.
     * @param serverAddress Ip-Address or hostname of the server.
     * @throws IOException
     */
    public void connectAndStartGame(String serverAddress) throws IOException {
        connectToServer(serverAddress);
        startReceiveActionLoop();
        GameCommunicator.waitForStartSignal(in);
        handler.receivedStartSignal();

    }

    /**
     * Asynchronously send an action to the server.
     * @param action Action to be sent.
     */
    public void send(PlayerAction action){
        sendingQueue.send(action);
    }

    /**
     * Create a client socket.
     * Then create and start the sending queue thread.
     * @param serverAddress Ip-Address or hostname of the server.
     */
    private void connectToServer(String serverAddress) {
        SocketHints hints = new SocketHints();
        socket = Gdx.net.newClientSocket(Net.Protocol.TCP, serverAddress,SERVER_PORT, hints);
        in = new DataInputStream(socket.getInputStream());
        out = new DataOutputStream(socket.getOutputStream());
        sendingQueue = new SendingQueue(out);
        sendingQueue.startSendingLoop();
    }

    /**
     * Start thread that listens to actions sent by the server.
     * Notifies the PlayerActionHandler when receiving such a message.
     * Stops when receiving a stop signal from the server.
     */
    private void startReceiveActionLoop(){
        new Thread(new Runnable(){

            @Override
            public void run() {

                Gdx.app.log(LOGGING_TAG,"Started receive thread [Client].");
                DataInputStream dataIn = new DataInputStream(socket.getInputStream());
                try {
                    boolean stopped = false;
                    while(!stopped) {
                        PlayerAction action = GameCommunicator.receiveAction(dataIn);
                        if(action.playerId < 0){ //received stop signal
                            sendingQueue.stop();
                            GameCommunicator.sendStopSignal(out); //This helps the server to properly shut down its threads.
                            stopped = true;
                            handler.receivedStopSignal();
                        }else {
                            notifyHandler(action);
                        }
                    }
                } catch (IOException e) {
                    e.printStackTrace();
                    //TODO: Add proper exception handling
                }
                Gdx.app.log(LOGGING_TAG,"Stopping receive thread [Client].");

            }
        }).start();
    }

    /**
     * Method used to notify the Handler about received actions.
     * @param action The received action.
     */
    private void notifyHandler(PlayerAction action){
        this.handler.updatePlayerFigure(action);
    }

}
