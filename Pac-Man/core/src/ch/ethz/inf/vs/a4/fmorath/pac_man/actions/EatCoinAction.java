package ch.ethz.inf.vs.a4.fmorath.pac_man.actions;

import ch.ethz.inf.vs.a4.fmorath.pac_man.MovementDirection;

/**
 * Created by linus on 16.12.2016.
 */

public class EatCoinAction extends Action {
    public final int eatenCoinIndex;

    public EatCoinAction(int playerId, boolean sendResponseToRequestingClient, int eatenCoinIndex) {
        super(ActionType.EatCoin, playerId, sendResponseToRequestingClient);
        this.eatenCoinIndex = eatenCoinIndex;
    }
}
