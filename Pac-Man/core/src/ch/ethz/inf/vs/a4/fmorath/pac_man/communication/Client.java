package ch.ethz.inf.vs.a4.fmorath.pac_man.communication;


import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.net.Socket;

import ch.ethz.inf.vs.a4.fmorath.pac_man.actions.Action;
import ch.ethz.inf.vs.a4.fmorath.pac_man.actions.ActionType;
import ch.ethz.inf.vs.a4.fmorath.pac_man.actions.DisconnectPlayerAction;


/**
 * Created by johannes on 22.11.16.
 */

/**
 * Client class for the Pac Man game communication protocol.
 */
public class Client extends CommunicationEntity {
    private Socket socket;
    private DataInputStream in;
    private DataOutputStream out;
    private SendingQueue sendingQueue;
    private int localId = -1;
    private String myName;
    private boolean stopped = false;

    /**
     * Constructor.
     */
    public Client(int port, String myName){
        super(port);
        this.socket = null;
        this.sendingQueue = null;
        this.myName = myName;
    }

    /**
     * 1. Connect to the server.
     * 2. Send Player Name
     * 3. Wait for all the players that connect to the game and for start signal
     * 4. Start receiving thread that listens to actions from other players.
     * @param serverAddress Ip-Address or hostname of the server.
     * @throws IOException
     */
    public void connectAndStartGame(String serverAddress) throws IOException {
        connectToServer(serverAddress);
        GameCommunicator.sendPlayerName(out, myName);
        boolean started;
        do {
            GameCommunicator.NameIdStart newPlayer = GameCommunicator.waitForStartSignalAndNames(in);
            started = newPlayer.started;
            if(!started && !stopped){
                if(localId < 0){
                    localId = newPlayer.id;
                }else {
                    if(newPlayer.disconnect){
                        if(newPlayer.id == 0) { //server disconnects
                            stopped = true;
                            started = true;
                        }
                        notifyPlayerLeft(newPlayer.id);
                        if(newPlayer.id < localId){
                            localId--;
                        }
                    }else {
                        notifyHandlerNewPlayer(newPlayer.name, newPlayer.id, newPlayer.id == localId);
                    }
                }
            }
        }while(!started);

        if(!stopped) {

            startReceiveActionLoop();
            notifyStartHandlerStart();
        }

    }

    /**
     * Asynchronously send an action to the server.
     * @param action Action to be sent.
     */
    @Override
    public void send(Action action) throws IOException {
        if (!action.sendResponseToRequestingClient)
            notifyHandler(action);
        sendingQueue.send(action);
    }

    /**
     * Create a client socket.
     * Then create and start the sending queue thread.
     * @param serverAddress Ip-Address or hostname of the server.
     */
    private void connectToServer(String serverAddress) throws IOException {
        int port = getPort();
        socket = new Socket(serverAddress, port);
        in = new DataInputStream(socket.getInputStream());
        out = new DataOutputStream(socket.getOutputStream());
        sendingQueue = new SendingQueue(out);
        sendingQueue.startSendingLoop();
    }

    /**
     * Start thread that listens to actions sent by the server.
     * Notifies the ActionHandler when receiving such a message.
     * Stops when receiving a stop signal from the server.
     */
    private void startReceiveActionLoop(){
        new Thread(new Runnable(){

            @Override
            public void run() {

                DataInputStream dataIn;
                try {
                    dataIn = new DataInputStream(socket.getInputStream());
                    boolean stopped = false;
                    while(!stopped) {
                        Action action = GameCommunicator.receiveAction(dataIn);
                        if(action.type == ActionType.DisconnectPlayer){ //received stop signal
                            if(action.playerId == localId) {
                                GameCommunicator.sendStopSignal(out); //This helps the server to properly shut down its threads.
                                stop();
                                notifyStopHandler();
                            }else{
                                notifyPlayerLeft(action.playerId);
                            }
                        } else {
                            notifyHandler(action);
                        }
                    }
                } catch (IOException e) {
                    e.printStackTrace();
                }

            }
        }).start();
    }

    @Override
    public void stop() throws IOException {
        if(localId > -1)
            GameCommunicator.sendAction(out, new DisconnectPlayerAction(localId));
        sendingQueue.stop();
        stopped = true;
    }


}
