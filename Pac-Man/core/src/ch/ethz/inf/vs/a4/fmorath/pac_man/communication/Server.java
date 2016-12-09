package ch.ethz.inf.vs.a4.fmorath.pac_man.communication;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by johannes on 22.11.16.
 */

/**
 * Server for the Pac Man game communication protocol.
 */
public class Server extends CommunicationEntity implements CommunicationConstants{
    private final List<Socket> clients;
    private final List<SendingQueue> sendingQueues;
    private boolean gameStarted;
    private boolean gameStopped;

    /**
     * Constructor.
     */
    public Server(){
        gameStarted = false;
        gameStopped = false;
        clients = new ArrayList<Socket>();
        this.sendingQueues = new ArrayList<SendingQueue>();
    }


    /**
     * Asynchronously start server: Start a new Thread that waits for the clients to connect, until the "gameStarted" flag is set.
     * Then the thread initiates the start of the game.
     */
    public void start(){
        if(gameStopped){
            throw new RuntimeException("Cannot start server, after it was stopped.");
        }
        //Gdx.app.log(LOGGING_TAG,"Start server.");
        new Thread(new Runnable(){

            @Override
            public void run() {
                try {
                    waitForNewClients();
                    onGameStarts();
                } catch (IOException e) {
                    //connection problems.
                    e.printStackTrace();
                }
            }
        }).start();
    }

    /**
     * Initiate server shutdown by setting the "gameStopped" flag, stopping all the SendingQueues and sending the stop signal to all clients.
     */
    public void stop(){
        gameStarted = true;
        gameStopped = true;
        for(SendingQueue queue: sendingQueues){
            queue.stop();
        }
        for(Socket s: clients){
            try {
                GameCommunicator.sendStopSignal(new DataOutputStream(s.getOutputStream()));
                notifyStopHandler();
            } catch (IOException e) {
                e.printStackTrace(); //Todo: add proper exception handling.
            }
        }
    }

    /**
     * Initiate the start of the game by setting the "gameStarted" flag.
     * Note that the game may not start immediately but only when the server socket times out.
     * That means, that you should not start sending messages after calling this method.
     * Instead use the "receivedStartSignal" of the PlayerActionHandler interface.
     */
    public void startGame(){
        gameStarted = true;
    }


    /**
     * This function can be used to send an action taken by the local player to all the other players.
     * @param action The action taken by local player
     * @throws IOException
     */
    public void send(PlayerAction action) throws IOException {
        sendActionToAllClients(action, null);
    }


    /**
     * Game starts.
     * 1. Start listening to all sockets.
     * 2. Start sending queue threads.
     * 3. Send start signal to all clients.
     * 4. Notify local user about start of the game.
     */
    private void onGameStarts() throws IOException {

        //Gdx.app.log(LOGGING_TAG,"Start game.");
        startReceivingLoops();
        startSendingLoops();
        try {
            sendStartSignalToAllClients();
            notifyStartHandler(0,clients.size()+1);
        } catch (IOException e) {
            e.printStackTrace(); //Todo: add proper exception handling.
        }
    }


    /**
     * This method implements the server functionality before the game starts.
     * I.e. the server waits (in a separate thread) for new clients to connect.
     * It uses timeouts to check if the local player initiated the game start.
     */
    private void waitForNewClients() throws IOException {

        ServerSocket serverSocket = new ServerSocket(SERVER_PORT);
        serverSocket.setSoTimeout(3000);

        // Loop until game starts.
        while(!gameStarted){
            // Create a socket
            //Gdx.app.log(LOGGING_TAG, "Waiting for connections.");
            try {
                Socket socket = serverSocket.accept();
                clients.add(socket);
                //Gdx.app.log(LOGGING_TAG, "Got connection from " + socket.getRemoteAddress());
            }catch(IOException ex){
                //do nothing because the timeout is expected.
            }
        }

        serverSocket.close();
    }


    /**
     * Starts a new Thread that waits for imcoming player actions from the specified socket.
     * All incoming actions will be forwarded to all other players and the local user will get a notification.
     * The thread terminates when it receives the stop signal from the client.
     * @param socket Socket of the client.
     */
    private void receiveAndDistributeCommandsFrom(final Socket socket){
        new Thread(new Runnable(){

            @Override
            public void run() {
                DataInputStream dataIn;
                try {
                    dataIn = new DataInputStream(socket.getInputStream());
                    while(!gameStopped) {
                        PlayerAction action = GameCommunicator.receiveAction(dataIn);
                        if(action.playerId >= 0) {
                            sendActionToAllClients(action, socket);
                            notifyHandler(action);
                        }
                    }
                    socket.close();
                } catch (IOException e) {
                    //Lost connection to client
                    e.printStackTrace();
                }
                //socket.dispose();
                //Gdx.app.log(LOGGING_TAG,"Stopping ReceiveAndDistribute thread and closing socket.");

            }
        }).start();
    }

    /**
     * Sends action to all the clients (except for the client who sent the action).
     * @param action Action to be sent to all clients.
     * @param receivedFrom Socket of the client that sent the action.
     * @throws IOException
     */
    private void sendActionToAllClients(PlayerAction action, Socket receivedFrom) throws IOException {
        for(SendingQueue queue: sendingQueues){
            queue.send(action); //Todo: exclude the socket that sent the action.

        }
    }


    /**
     * Start sending queue threads for all the clients.
     */
    private void startSendingLoops() throws IOException {
        for(Socket s: clients){
            SendingQueue queue = new SendingQueue(new DataOutputStream(s.getOutputStream()));
            queue.startSendingLoop();
            sendingQueues.add(queue);
        }
    }

    /**
     * Start receiving thread for every client.
     */
    private void startReceivingLoops(){
        for(Socket s: clients){
            receiveAndDistributeCommandsFrom(s);
        }
    }

    /**
     * Method to send the start signal to all Clients.
     * Todo: Parallelize the code. Maybe move this part to SendingQueue.
     * @throws IOException
     */
    private void sendStartSignalToAllClients() throws IOException {
        for(int i=0;i<clients.size();++i){
            Socket s = clients.get(i);
            GameCommunicator.sendStartSignal(new DataOutputStream(s.getOutputStream()),i+1,clients.size()+1);
        }
    }

}
