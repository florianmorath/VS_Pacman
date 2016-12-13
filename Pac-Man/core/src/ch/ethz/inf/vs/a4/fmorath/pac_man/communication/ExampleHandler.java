package ch.ethz.inf.vs.a4.fmorath.pac_man.communication;

import com.badlogic.gdx.Gdx;


/**
 * Created by johannes on 22.11.16.
 */

/**
 * TODO: remove this class later. Only serves as example how to implement a PlayerActionHandler. Of course instead of printing the actions, they should be used to update the game state.
 */
public class ExampleHandler implements PlayerActionHandler, StartSignalHandler, StopSignalHandler {
    private final String tag;

    public ExampleHandler(String tag){
        this.tag = "ExampleHandler::"+tag;
    }

    @Override
    public void updatePlayerFigure(PlayerAction action) {
        Gdx.app.log(tag, "Received action"+ action.newDirection +" from player " + action.playerId + " at position [" + action.positionX + ":" + action.positionY+ "].");
    }

    @Override
    public void receivedStartSignal() {
        Gdx.app.log(tag, "Received start signal");
    }

    @Override
    public void receivedStopSignal() {
        Gdx.app.log(tag, "Received stop signal");
    }
}
