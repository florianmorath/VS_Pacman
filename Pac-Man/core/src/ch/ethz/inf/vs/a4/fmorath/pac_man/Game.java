package ch.ethz.inf.vs.a4.fmorath.pac_man;

import com.badlogic.gdx.ApplicationAdapter;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.maps.tiled.TiledMap;
import com.badlogic.gdx.maps.tiled.TiledMapRenderer;
import com.badlogic.gdx.maps.tiled.TmxMapLoader;
import com.badlogic.gdx.maps.tiled.renderers.OrthogonalTiledMapRenderer;
import com.badlogic.gdx.math.Vector3;
import com.badlogic.gdx.utils.Array;
import com.badlogic.gdx.utils.viewport.FitViewport;
import com.badlogic.gdx.utils.viewport.Viewport;

import java.io.IOException;

import ch.ethz.inf.vs.a4.fmorath.pac_man.communication.Client;
import ch.ethz.inf.vs.a4.fmorath.pac_man.communication.ExampleHandler;
import ch.ethz.inf.vs.a4.fmorath.pac_man.communication.PlayerAction;
import ch.ethz.inf.vs.a4.fmorath.pac_man.communication.Server;


public class Game extends ApplicationAdapter {

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

	/**
	 * TODO: Remove this method. Only for Demonstration purposes how to use the communication protocol.
	 * @throws IOException
	 * @throws InterruptedException
	 */
	public void testCommunication() throws IOException, InterruptedException {
		ExampleHandler serverHandler = new ExampleHandler("Server");
		ExampleHandler client1Handler = new ExampleHandler("Client1");
		ExampleHandler client2Handler = new ExampleHandler("Client2");

		Server server = new Server();
		server.setPlayerActionHandler(serverHandler);
		server.setStartSignalHandler(serverHandler);
		server.setStopSignalHandler(serverHandler);

		final Client client1 = new Client();
		server.setPlayerActionHandler(client1Handler);
		server.setStartSignalHandler(client1Handler);
		server.setStopSignalHandler(client1Handler);


		final Client client2 = new Client();
		server.setPlayerActionHandler(client2Handler);
		server.setStartSignalHandler(client2Handler);
		server.setStopSignalHandler(client2Handler);

		server.start();
		new Thread(new Runnable(){

			@Override
			public void run() {
				try {
					client1.connectAndStartGame("localhost");
				} catch (IOException e) {
					e.printStackTrace();
				}
			}
		}).start();
		new Thread(new Runnable(){

			@Override
			public void run() {
				try {
					client2.connectAndStartGame("localhost");
				} catch (IOException e) {
					e.printStackTrace();
				}
			}
		}).start();

		Thread.sleep(5000);
		server.startGame();
		Thread.sleep(3000);
		server.send(new PlayerAction(0, 0, 0, MovementDirection.UP));
		client1.send(new PlayerAction(2, 20, 0, MovementDirection.UP));
		client1.send(new PlayerAction(2, 21, 0, MovementDirection.UP));
		Thread.sleep(100);
		server.send(new PlayerAction(0, 1, 0, MovementDirection.UP));
		client1.send(new PlayerAction(1, 10, 0, MovementDirection.UP));
		server.send(new PlayerAction(0, 2, 0, MovementDirection.UP));
		Thread.sleep(20);
		client1.send(new PlayerAction(1, 11, 0, MovementDirection.UP));
		client1.send(new PlayerAction(1, 12, 0, MovementDirection.UP));
		client1.send(new PlayerAction(2, 22, 0, MovementDirection.UP));
		server.send(new PlayerAction(0, 3, 0, MovementDirection.UP));
		Thread.sleep(500);
		server.stop();
	}
}