package ch.ethz.inf.vs.a4.fmorath.pac_man.actions;

/**
 * Created by linus on 16.12.2016.
 */

public class EatPlayerAction extends Action {
    public final int eatenPlayerId;

    public EatPlayerAction(int playerId, boolean sendResponseToRequestingClient, int eatenPlayerId) {
        super(ActionType.EatPlayer, playerId, sendResponseToRequestingClient);
        this.eatenPlayerId = eatenPlayerId;
    }
}
