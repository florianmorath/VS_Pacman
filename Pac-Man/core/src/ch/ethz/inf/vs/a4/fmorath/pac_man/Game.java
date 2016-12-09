package ch.ethz.inf.vs.a4.fmorath.pac_man;

import com.badlogic.gdx.ApplicationAdapter;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.maps.tiled.TiledMap;
import com.badlogic.gdx.maps.tiled.TiledMapRenderer;
import com.badlogic.gdx.maps.tiled.TmxMapLoader;
import com.badlogic.gdx.maps.tiled.renderers.OrthogonalTiledMapRenderer;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.math.Vector3;
import com.badlogic.gdx.utils.Array;
import com.badlogic.gdx.utils.viewport.FitViewport;
import com.badlogic.gdx.utils.viewport.Viewport;


import ch.ethz.inf.vs.a4.fmorath.pac_man.communication.Client;
import ch.ethz.inf.vs.a4.fmorath.pac_man.communication.CommunicationEntity;
import ch.ethz.inf.vs.a4.fmorath.pac_man.communication.PlayerAction;
import ch.ethz.inf.vs.a4.fmorath.pac_man.communication.PlayerActionHandler;
import ch.ethz.inf.vs.a4.fmorath.pac_man.communication.Server;


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

	public boolean getIsServer(){
		return isServer;
	}

    private static Game instance;
    public static Game getInstance() {
        return instance;
    }

	private int worldWidth;
	private int worldHeight;

	private TiledMap map;
	private TiledMapRenderer mapRenderer;

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
		/*try {
			testCommunication();
		} catch (IOException e) {
			e.printStackTrace();
		} catch (InterruptedException e) {
			e.printStackTrace();
		}*/

		map = new TmxMapLoader().load("map.tmx");
		worldWidth  = 4 * map.getProperties().get("width",  Integer.class);
		worldHeight = 4 * map.getProperties().get("height", Integer.class);

		mapRenderer = new OrthogonalTiledMapRenderer(map);

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

		mapRenderer.setView(camera);
		mapRenderer.render();

		currentRound.act(Gdx.graphics.getDeltaTime());
		currentRound.draw();

//		WallCollisionDetection();
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
        currentRound = new Round(this, roundNumber++, viewport, map, players);
    }

	public void endRound() {
		if (roundNumber == getNumPlayers())
            hasEnded = true;
        else
            startRound();
	}

//	private void WallCollisionDetection() {
//
//		getTiles(pacMan.getX(), pacMan.getY(), pacMan.getX()+pacMan.getWidth(), pacMan.getY()+pacMan.getHeight(), tiles);
//
//		for(Rectangle tile: tiles) {
//			Gdx.app.log("tile", "true");
//
//			if (Intersector.overlaps(tile, new Rectangle(pacMan.getX(),pacMan.getY(),pacMan.getWidth(),pacMan.getHeight()))) {
//				// collision happened
//				Gdx.app.log("collision", "true");
//
//				// Isnt it easier to use System.out?
//				System.out.println("### Collision detected");
//
//			}
//		}

//	}

//	private void getTiles(float startX, float startY, float endX, float endY, Array<Rectangle> tiles) {
//
//		int startXX = Math.currentRound(startX);
//		int startYY = Math.currentRound(startY);
//		TiledMapTileLayer layer = (TiledMapTileLayer)map.getLayers().get("Walls Layer");
//
//		rectPool.freeAll(tiles);
//		tiles.clear();
//		for(int y =  startYY; y <= endY; y++) {
//			for(int x = startXX; x <= endX; x++) {
//				TiledMapTileLayer.Cell cell = layer.getCell(x, y);
//
//				if(cell != null) {
//					Gdx.app.log("cellFound", "true");
//
//					Rectangle rect = rectPool.obtain();
//					rect.set(x, y, 1, 1);
//					tiles.add(rect);
//
//				}
//			}
//		}
//	}


	@Override
	public void updatePlayerFigure(PlayerAction action) {
		for(Player p: players){
			if(!p.isLocalPlayer() && p.getPlayerId() == action.playerId){
				while(p.getFigure().positionChangeAvailable()){}
				p.getFigure().setDirPos(action.newDirection, new Vector2(action.positionX,action.positionY));
			}
		}

	}
}