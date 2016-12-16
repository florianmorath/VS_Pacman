package ch.ethz.inf.vs.a4.fmorath.pac_man.communication;


/**
 * Created by johannes on 22.11.16.
 */

import ch.ethz.inf.vs.a4.fmorath.pac_man.actions.Action;

/**
 * An interface that is used by server and clients to call the appropriate functions after receiving information from other players.
 * This interface must be implemented by all classes that need to react when receiving information from the network.
 * Todo: WARNING: With the current implementation of the communication classes, it is possible, that these functions are called concurrently by more than one thread (when the server receives actions from several players "at the same time". Depending on the implementations of the interface, this might be a problem or not. If it is, it remains to be decided if we change the implementation of the interface (the classes that use the communication classes) or if we change the communication classes.
 */
public interface ActionHandler {
    /**
     * This function is called when a new player action was received.
     * @param action The received player action.
     */
    void handleAction(Action action);
}