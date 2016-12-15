package ch.ethz.inf.vs.a4.fmorath.pac_man;

import com.badlogic.gdx.ApplicationAdapter;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.maps.tiled.TiledMap;
import com.badlogic.gdx.maps.tiled.TmxMapLoader;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.math.Vector3;
import com.badlogic.gdx.utils.Array;
import com.badlogic.gdx.utils.viewport.FitViewport;
import com.badlogic.gdx.utils.viewport.Viewport;


import java.io.IOException;

import ch.ethz.inf.vs.a4.fmorath.pac_man.communication.Client;
import ch.ethz.inf.vs.a4.fmorath.pac_man.communication.CommunicationEntity;
import ch.ethz.inf.vs.a4.fmorath.pac_man.communication.PlayerAction;
import ch.ethz.inf.vs.a4.fmorath.pac_man.communication.PlayerActionHandler;
import ch.ethz.inf.vs.a4.fmorath.pac_man.communication.Server;
import ch.ethz.inf.vs.a4.fmorath.pac_man.figures.Ghost;
import ch.ethz.inf.vs.a4.fmorath.pac_man.figures.PacMan;


public class Game extends ApplicationAdapter implements PlayerActionHandler{


	private boolean isServer = false;
	public CommunicationEntity communicator;
	public void setCommunicator(Server server){
		communicator = server;
		isServer = true;
	}
	public void setCommunicator(Client client){
		this.communicator = client;

	}

	public boolean isServer(){
		return isServer;
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


	public void broadcastCollision(int playerIdEats, int playerIdEaten){
		if(!isServer){
			throw new RuntimeException("only server can broadcast collisions");
		}
		Server server = (Server) communicator;
		try {
			server.sendCollisionToAllClients(playerIdEats, playerIdEaten);
		}catch(IOException ex){
			ex.printStackTrace();
		}
	}

	public void broadcastPacManWon(){
		int pacManId = -1;
		for(int i = 0; i<players.size; ++i){
			if(players.get(i).isPacMan()){
				pacManId = players.get(i).getPlayerId();
			}
		}
		broadcastCollision(pacManId,pacManId);
	}


	@Override
	public void updatePlayerFigure(PlayerAction action) {
		if(action.hasEatenPlayer()){
			Player eaten = players.get(action.eatenPlayerId);
			if(eaten.isPacMan()){
				if(action.playerId == action.eatenPlayerId){
					// pac man collected all coins
					currentRound.end(true);
				}else {
					PacMan pacman = (PacMan) eaten.getFigure();
					pacman.onDeath();
					currentRound.end(false);
				}
			}else{
				Ghost eatenGhost = (Ghost) eaten.getFigure();
				eatenGhost.onEaten();
			}
		}else {
			Player p = players.get(action.playerId);
			if(!p.isLocalPlayer()){
				while(p.getFigure().positionChangeAvailable()){}
				p.getFigure().setDirPos(action.newDirection, new Vector2(action.positionX, action.positionY));
			}
		}

	}
}