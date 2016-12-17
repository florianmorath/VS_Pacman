package ch.ethz.inf.vs.a4.fmorath.pac_man.actions;

/**
 * Created by johannes on 17.12.16.
 */

public class DisconnectPlayerAction extends Action {
    public DisconnectPlayerAction(int playerId) {
        super(ActionType.DisconnectPlayer, playerId, false);
    }
}
