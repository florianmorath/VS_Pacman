package ch.ethz.inf.vs.a4.fmorath.pac_man.communication;


//import java.net.*;
import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.net.Socket;

/**
 * Created by johannes on 22.11.16.
 */

/**
 * Client class for the Pac Man game communication protocol.
 */
public class Client extends CommunicationEntity implements CommunicationConstants{
    private Socket socket;
    private DataInputStream in;
    private DataOutputStream out;
    private SendingQueue sendingQueue;

    /**
     * Constructor.
     */
    public Client(){
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
        notifyStartHandler();

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
        //SocketHints hints = new SocketHints();
        try {
            socket = new Socket(serverAddress, SERVER_PORT);//Gdx.net.newClientSocket(Net.Protocol.TCP, serverAddress,SERVER_PORT, hints);
            in = new DataInputStream(socket.getInputStream());
            out = new DataOutputStream(socket.getOutputStream());
            sendingQueue = new SendingQueue(out);
            sendingQueue.startSendingLoop();
        }catch(IOException ex){
            //Todo: Exception handling.
            ex.printStackTrace();
        }
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

                DataInputStream dataIn;
                try {
                    dataIn = new DataInputStream(socket.getInputStream());
                    boolean stopped = false;
                    while(!stopped) {
                        PlayerAction action = GameCommunicator.receiveAction(dataIn);
                        if(action.playerId < 0){ //received stop signal
                            sendingQueue.stop();
                            GameCommunicator.sendStopSignal(out); //This helps the server to properly shut down its threads.
                            stopped = true;
                            notifyStopHandler();
                        }else {
                            notifyHandler(action);
                        }
                    }
                } catch (IOException e) {
                    e.printStackTrace();
                    //TODO: Add proper exception handling
                }
                //Gdx.app.log(LOGGING_TAG,"Stopping receive thread [Client].");

            }
        }).start();
    }


}
