package ch.ethz.inf.vs.a4.fmorath.pac_man.communication;

/**
 * Created by johannes on 09.12.16.
 */

public interface StartSignalHandler {
    /**
     * This function will be called, when the start signal was received.
     * Note that it will also be called by the server, when it sends start signals to all other players.
     */
    void receivedStartSignal(int myPlayerId, int numPlayers);
}
