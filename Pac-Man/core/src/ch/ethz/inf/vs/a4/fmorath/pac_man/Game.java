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
import com.badlogic.gdx.utils.viewport.FitViewport;
import com.badlogic.gdx.utils.viewport.Viewport;

import java.io.IOException;
import java.util.Arrays;

import ch.ethz.inf.vs.a4.fmorath.pac_man.communication.Client;
import ch.ethz.inf.vs.a4.fmorath.pac_man.communication.ExampleHandler;
import ch.ethz.inf.vs.a4.fmorath.pac_man.communication.PlayerAction;
import ch.ethz.inf.vs.a4.fmorath.pac_man.communication.Server;


public class Game extends ApplicationAdapter {

	private int screenWidth;
	private int screenHeight;
	private int worldWidth;
	private int worldHeight;

	private TiledMap map;
	private TiledMapRenderer mapRenderer;

	private OrthographicCamera camera;
	private Viewport viewport;
	private Round round;

	private int highscore = 0;

	public int getHighscore() {
		return highscore;
	}

	public void setHighscore(int highscore) {
		this.highscore = highscore;
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

		screenWidth = Gdx.graphics.getWidth();
		screenHeight = Gdx.graphics.getHeight();
		float aspectRatio = screenHeight / (float)screenWidth;

		map = new TmxMapLoader().load("map.tmx");
		worldWidth  = 4 * map.getProperties().get("width",  Integer.class);
		worldHeight = 4 * map.getProperties().get("height", Integer.class);

		mapRenderer = new OrthogonalTiledMapRenderer(map);

		camera = new OrthographicCamera();
		viewport = new FitViewport(worldWidth, worldHeight + 40, camera);

		Player[] players = new Player[5];
		Arrays.fill(players, new Player(this));
		round = new Round(this, viewport, map, players);
	}

	@Override
	public void render() {
		Gdx.gl.glClearColor(0, 0, 0, 1);
		Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT);

		camera.position.set(new Vector3(worldWidth / 2, worldHeight / 2 + 4, 0));
		camera.update();

		mapRenderer.setView(camera);
		mapRenderer.render();

//		round.getBatch().setProjectionMatrix(camera.combined);
		round.act(Gdx.graphics.getDeltaTime());
		round.draw();

//		WallCollisionDetection();

	}

	@Override
	public void dispose () {
		round.dispose();
		map.dispose();
	}

    @Override
    public void resize(int width, int height) {
        viewport.update(width, height, true);
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
//		int startXX = Math.round(startX);
//		int startYY = Math.round(startY);
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

		Server server = new Server(serverHandler);
		final Client client1 = new Client(client1Handler);
		final Client client2 = new Client(client2Handler);

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