package ch.ethz.inf.vs.a4.fmorath.pac_man.actions;

import ch.ethz.inf.vs.a4.fmorath.pac_man.MovementDirection;
import ch.ethz.inf.vs.a4.fmorath.pac_man.actions.ActionType;

/**
 * Created by johannes on 22.11.16.
 */

public class Action {
    public final ActionType type;
    public final int playerId;
    public final boolean sendResponseToRequestingClient;

    public Action(ActionType type, int playerId, boolean sendResponseToRequestingClient) {
        this.type = type;
        this.playerId = playerId;
        this.sendResponseToRequestingClient = sendResponseToRequestingClient;
    }
}
