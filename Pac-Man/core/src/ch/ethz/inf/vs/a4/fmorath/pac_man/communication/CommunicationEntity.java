package ch.ethz.inf.vs.a4.fmorath.pac_man.communication;

/**
 * Created by johannes on 09.12.16.
 */

/**
 * A communication node (either Server or client) that notifies its handlers about communication events.
 */
public class CommunicationEntity {

    private PlayerActionHandler actionHandler;
    private StartSignalHandler startSignalHandler;
    private StopSignalHandler stopSignalHandler;

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
    public void setPlayerActionHandler(PlayerActionHandler handler){
        this.actionHandler = handler;
    }


    /**
     * Method used to notify handler about received action.
     * @param action The action received from the network.
     */
    protected void notifyHandler(PlayerAction action){
        this.actionHandler.updatePlayerFigure(action);
    }

    /**
     * Call the appropriate function to handle start signal
     */
    protected void notifyStopHandler()
    {
        if(stopSignalHandler != null)
            stopSignalHandler.receivedStopSignal();
    }

    /**
     * notify start signal handler that game started.
     */
    protected void notifyStartHandler(){
        if(startSignalHandler != null)
            startSignalHandler.receivedStartSignal();
    }
}
