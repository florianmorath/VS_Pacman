package ch.ethz.inf.vs.a4.fmorath.pac_man.communication;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;
import java.net.SocketTimeoutException;
import java.util.ArrayList;
import java.util.List;

import ch.ethz.inf.vs.a4.fmorath.pac_man.MovementDirection;
import ch.ethz.inf.vs.a4.fmorath.pac_man.actions.Action;
import ch.ethz.inf.vs.a4.fmorath.pac_man.actions.ActionType;
import ch.ethz.inf.vs.a4.fmorath.pac_man.actions.DisconnectPlayerAction;

/**
 * Created by johannes on 22.11.16.
 */

/**
 * Server for the Pac Man game communication protocol.
 */
public class Server extends CommunicationEntity{
    private final List<Socket> clients;
    private final List<String> playerNames;
    private final List<SendingQueue> sendingQueues;
    private boolean gameStarted;
    private boolean gameStopped;

    /**
     * Constructor.
     */
    public Server(int port, String myName){
        super(port);
        gameStarted = false;
        gameStopped = false;
        clients = new ArrayList<Socket>();
        playerNames = new ArrayList<String>();
        playerNames.add(myName);
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
        new Thread(new Runnable(){

            @Override
            public void run() {
                try {
                    waitForNewClients();
                    if(!gameStopped) {
                        onGameStarts();
                    }
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
        for(SendingQueue queue: sendingQueues){
            queue.stop();
        }
        for(Socket s: clients){
            try {
                if(!gameStarted){
                    GameCommunicator.sendPlayerDisconnected(new DataOutputStream(s.getOutputStream()), 0);
                }
                else {
                    GameCommunicator.sendStopSignal(new DataOutputStream(s.getOutputStream()));
                }
                notifyStopHandler();
            } catch (IOException e) {
                e.printStackTrace(); //Todo: add proper exception handling.
            }
        }
        gameStarted = true;
        gameStopped = true;
    }

    /**
     * Initiate the start of the game by setting the "gameStarted" flag.
     * Note that the game may not start immediately but only when the server socket times out.
     * That means, that you should not start sending messages after calling this method.
     * Instead use the "receivedStartSignal" of the ActionHandler interface.
     */
    public void startGame(){
        gameStarted = true;
    }

    /**
     * This function can be used to send an action taken by the local player to all the other players.
     * @param action The action taken by local player
     * @throws IOException
     */
    @Override
    public void send(Action action) throws IOException {
        notifyHandler(action);
        sendActionToClients(action);
    }

    /**
     * Game starts.
     * 1. Start listening to all sockets.
     * 2. Start sending queue threads.
     * 3. Send start signal to all clients.
     * 4. Notify local user about start of the game.
     */
    private void onGameStarts() throws IOException {

        startSendingLoops();
        try {
            sendStartSignalToAllClients();
            notifyStartHandlerStart();
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

        notifyHandlerNewPlayer(playerNames.get(0), 0, true);
        ServerSocket serverSocket = new ServerSocket(getPort());
        serverSocket.setSoTimeout(1000);
        serverSocket.setReuseAddress(true);

        // Loop until game starts.
        while(!gameStarted){
            try {
                Socket socket = serverSocket.accept();
                clients.add(socket);
                getAndDistributeNewClientsName(socket);
            }catch (SocketTimeoutException ex){
                //do nothing because the timeout is expected.
            }catch(IOException ex){
                ex.printStackTrace();
            }
        }

        serverSocket.close();
    }

    private void getAndDistributeNewClientsName(final Socket socket) throws IOException {
        new Thread(new Runnable() {
            @Override
            public void run() {
                DataOutputStream out = null;
                try {
                    out = new DataOutputStream(socket.getOutputStream());
                    DataInputStream in = new DataInputStream(socket.getInputStream());
                    int newId;
                    String newName = GameCommunicator.receivePlayerName(in);
                    synchronized (this) {
                        newId = playerNames.size();
                        GameCommunicator.sendPlayerNameAndId(out, newName, newId);
                        for (int i = 0; i < playerNames.size(); ++i){
                            GameCommunicator.sendPlayerNameAndId(out, playerNames.get(i), i);
                        }
                        for(Socket c: clients){
                            GameCommunicator.sendPlayerNameAndId(new DataOutputStream(c.getOutputStream()), newName, newId);
                        }
                        playerNames.add(newName);
                    }
                    notifyHandlerNewPlayer(newName, newId, false);
                    receiveAndDistributeCommandsFrom(socket);
                } catch (IOException e) {
                    e.printStackTrace();
                }

            }
        }).start();

    }


    /**
     * Starts a new Thread that waits for incoming player actions from the specified socket.
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
                    boolean disconnected = false;
                    dataIn = new DataInputStream(socket.getInputStream());
                    while(!gameStopped && !disconnected) {
                        Action action = GameCommunicator.receiveAction(dataIn);
                        if (action.type != ActionType.DisconnectPlayer) {
                            sendActionToClients(action);
                            notifyHandler(action);
                        }else{
                            disconnected = true;
                            onPlayerDisconnected(action.playerId);
                        }
                    }
                    socket.close();
                } catch (IOException e) {
                    //Lost connection to client
                    e.printStackTrace();
                }

            }
        }).start();
    }

    private void onPlayerDisconnected(int id) {
        if (!gameStopped) {
            Socket socket;
            synchronized (this) {
                socket = clients.remove(id - 1);
                playerNames.remove(id);
                for (int i = 0; i < clients.size(); ++i) {
                    try {
                        GameCommunicator.sendPlayerDisconnected(new DataOutputStream(clients.get(i).getOutputStream()), id);
                    } catch (Exception ex) {
                        ex.printStackTrace();
                    }
                }
            }
            try {
                GameCommunicator.sendStartSignal(new DataOutputStream(socket.getOutputStream()));
            } catch (IOException ex) {
                ex.printStackTrace();
            }


            notifyPlayerLeft(id);
        }
    }

    /**
     * Sends action to all the clients (except for the client who sent the action).
     * @param action Action to be sent to all clients.
     * @throws IOException
     */
    private void sendActionToClients(Action action) throws IOException {
        for (int i = 0; i < sendingQueues.size(); i++) {
            if (i + 1 != action.playerId || action.sendResponseToRequestingClient)
                sendingQueues.get(i).send(action);
        }
    }

    /**
     * Start sending queue threads for all the clients.
     */
    private void startSendingLoops() throws IOException {
        for (Socket s : clients) {
            SendingQueue queue = new SendingQueue(new DataOutputStream(s.getOutputStream()));
            queue.startSendingLoop();
            sendingQueues.add(queue);
        }
    }


    /**
     * Method to send the start signal to all Clients.
     * @throws IOException
     */
    private void sendStartSignalToAllClients() throws IOException {
        for(int i=0; i < clients.size(); ++i){
            Socket s = clients.get(i);
            GameCommunicator.sendStartSignal(new DataOutputStream(s.getOutputStream()));
        }
    }

}
