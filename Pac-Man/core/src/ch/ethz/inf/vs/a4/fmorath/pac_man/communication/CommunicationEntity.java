package ch.ethz.inf.vs.a4.fmorath.pac_man.communication;

/**
 * Created by johannes on 09.12.16.
 */

import java.io.IOException;

import ch.ethz.inf.vs.a4.fmorath.pac_man.actions.Action;

/**
 * A communication node (either Server or client) that notifies its handlers about communication events.
 */
abstract public class CommunicationEntity {

    private int communicationPort;

    private ActionHandler actionHandler;
    private StartSignalHandler startSignalHandler;
    private StopSignalHandler stopSignalHandler;

    public CommunicationEntity(int communicationPort){
        this.communicationPort = communicationPort;
    }

    /**
     * Get the communication port
     * @return
     */
    public int getPort(){
        return communicationPort;
    }

    /**
     * Setter for start signal handler
     * @param handler
     */
    public void setStartSignalHandler(StartSignalHandler handler){
        this.startSignalHandler = handler;
    }

    /**
     * Setter for stop signal handler
     * @param handler
     */
    public void setStopSignalHandler(StopSignalHandler handler){
        this.stopSignalHandler = handler;
    }

    /**
     * Setter for player action handler
     * @param handler
     */
    public void setPlayerActionHandler(ActionHandler handler){
        this.actionHandler = handler;
    }

    /**
     * Method used to notify handler about received action.
     * @param action The action received from the network.
     */
    protected void notifyHandler(Action action){
        this.actionHandler.handleAction(action);
    }

    /**
     * Call the appropriate function to handleAction start signal
     */
    protected void notifyStopHandler(){
        if(stopSignalHandler != null)
            stopSignalHandler.receivedStopSignal();
    }

    /**
     * notify start signal handler that game started.
     */
    protected void notifyStartHandlerStart(){
        if(startSignalHandler != null)
            startSignalHandler.receivedStartSignal();
    }

    protected void notifyHandlerNewPlayer(String name, int id, boolean isLocalPlayer){
        startSignalHandler.receivedNewPlayer(name, id, isLocalPlayer);
    }

    protected void notifyPlayerLeft(int id){
        startSignalHandler.receivedPlayerDisconnected(id);
    }

    public abstract void send(Action action) throws IOException;

    public abstract void stop() throws  IOException;
}
