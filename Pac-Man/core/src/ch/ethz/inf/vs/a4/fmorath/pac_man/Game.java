package ch.ethz.inf.vs.a4.fmorath.pac_man;

import com.badlogic.gdx.ApplicationAdapter;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.maps.tiled.TiledMap;
import com.badlogic.gdx.maps.tiled.TmxMapLoader;
import com.badlogic.gdx.math.Vector3;
import com.badlogic.gdx.utils.Array;
import com.badlogic.gdx.utils.viewport.FitViewport;
import com.badlogic.gdx.utils.viewport.Viewport;


import ch.ethz.inf.vs.a4.fmorath.pac_man.actions.EatCoinAction;
import ch.ethz.inf.vs.a4.fmorath.pac_man.actions.EatPlayerAction;
import ch.ethz.inf.vs.a4.fmorath.pac_man.actions.MovementAction;
import ch.ethz.inf.vs.a4.fmorath.pac_man.communication.CommunicationEntity;
import ch.ethz.inf.vs.a4.fmorath.pac_man.actions.Action;
import ch.ethz.inf.vs.a4.fmorath.pac_man.communication.ActionHandler;
import ch.ethz.inf.vs.a4.fmorath.pac_man.communication.Server;
import ch.ethz.inf.vs.a4.fmorath.pac_man.figures.Figure;
import ch.ethz.inf.vs.a4.fmorath.pac_man.figures.Ghost;
import ch.ethz.inf.vs.a4.fmorath.pac_man.figures.PacMan;


public class Game extends ApplicationAdapter implements ActionHandler {

	public CommunicationEntity communicator;
	public void setCommunicator(CommunicationEntity communicator){
		this.communicator = communicator;
	}
	public boolean isServer(){
		return communicator instanceof Server;
	}

    private static Game instance;
    public static Game getInstance() {
        return instance;
    }

	private int worldWidth;
	private int worldHeight;

	private TiledMap map;

	private OrthographicCamera camera;
	private Viewport viewport;

    private Array<Player> players = new Array<Player>();
    public Array<Player> getPlayers() {
        return players;
    }
    public void addPlayer(Player player) {
        players.add(player);
    }
    public int getNumPlayers() {
        return players.size;
    }
	public void removeAllPlayers(){
		players = new Array<Player>();
	}

    private int roundNumber = 0;
    private Round currentRound;
    private boolean hasEnded = false;
    public boolean hasEnded() {
        return hasEnded;
    }

	private int highScore = 0;
	public int getHighScore() {
		return highScore;
	}
	public void setHighScore(int highScore) {
		this.highScore = highScore;
	}

    public Game() {
        Game.instance = this;
    }


	@Override
	public void create () {
		map = new TmxMapLoader().load("map.tmx");
		worldWidth  = 4 * map.getProperties().get("width",  Integer.class);
		worldHeight = 4 * map.getProperties().get("height", Integer.class);

		camera = new OrthographicCamera();
		viewport = new FitViewport(worldWidth, worldHeight + 40, camera);
		this.communicator.setPlayerActionHandler(this);
		startRound();
	}

	@Override
	public void render() {
		Gdx.gl.glClearColor(0, 0, 0, 1);
		Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT);

		camera.position.set(new Vector3(worldWidth / 2, worldHeight / 2 + 4, 0));
		camera.update();

		currentRound.act(Gdx.graphics.getDeltaTime());
		currentRound.render(camera);
		currentRound.draw();
	}

	@Override
	public void dispose() {
		currentRound.dispose();
		map.dispose();
	}

    @Override
    public void resize(int width, int height) {
        viewport.update(width, height, true);
    }

    private void startRound() {
        currentRound = new Round(this, roundNumber, viewport, players, roundNumber == 0);
		roundNumber++;
    }

	public void endRound() {
		if (roundNumber == getNumPlayers())
            hasEnded = true;
        else {
            currentRound.dispose();
			Gdx.app.postRunnable(new Runnable() {
				@Override
				public void run() {
					startRound();
				}
			});
        }
	}

	@Override
	public void handleAction(Action action) {
        Player player = players.get(action.playerId);
        switch (action.type) {
            case Movement:
                player.getFigure().updatePositionAndDirection((MovementAction) action);
                break;
            case EatCoin:
                int index = ((EatCoinAction) action).eatenCoinIndex;
                currentRound.getCoins().get(index).collect(player);
                break;
            case EatPlayer:
                Figure figure = players.get(((EatPlayerAction) action).eatenPlayerId).getFigure();
                if (figure instanceof PacMan) {
                    ((PacMan) figure).onDeath();
                    currentRound.end(false);
                } else
                    ((Ghost) figure).onEaten();
                break;
        }
	}
}