package ch.ethz.inf.vs.a4.fmorath.pac_man.communication;


/**
 * Created by johannes on 22.11.16.
 */

import ch.ethz.inf.vs.a4.fmorath.pac_man.actions.Action;

/**
 * An interface that is used by server and clients to call the appropriate functions after receiving information from other players.
 * This interface must be implemented by all classes that need to react when receiving information from the network.
 */
public interface ActionHandler {
    /**
     * This function is called when a new player action was received.
     * @param action The received player action.
     */
    void handleAction(Action action);
}
