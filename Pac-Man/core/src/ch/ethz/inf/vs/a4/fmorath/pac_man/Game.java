package ch.ethz.inf.vs.a4.fmorath.pac_man;

import com.badlogic.gdx.ApplicationAdapter;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.maps.MapLayer;
import com.badlogic.gdx.maps.MapObjects;
import com.badlogic.gdx.maps.objects.RectangleMapObject;
import com.badlogic.gdx.maps.objects.TextureMapObject;
import com.badlogic.gdx.maps.tiled.TiledMap;
import com.badlogic.gdx.maps.tiled.TiledMapRenderer;
import com.badlogic.gdx.maps.tiled.TiledMapTileLayer;
import com.badlogic.gdx.maps.tiled.TmxMapLoader;
import com.badlogic.gdx.maps.tiled.renderers.OrthogonalTiledMapRenderer;
import com.badlogic.gdx.math.Intersector;
import com.badlogic.gdx.math.Rectangle;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.math.Vector3;
import com.badlogic.gdx.scenes.scene2d.Stage;
import com.badlogic.gdx.utils.Array;
import com.badlogic.gdx.utils.Pool;
import com.badlogic.gdx.utils.Scaling;
import com.badlogic.gdx.utils.viewport.ExtendViewport;
import com.badlogic.gdx.utils.viewport.FillViewport;
import com.badlogic.gdx.utils.viewport.FitViewport;
import com.badlogic.gdx.utils.viewport.ScalingViewport;
import com.badlogic.gdx.utils.viewport.ScreenViewport;
import com.badlogic.gdx.utils.viewport.StretchViewport;
import com.badlogic.gdx.utils.viewport.Viewport;

import org.w3c.dom.css.Rect;

import java.io.IOException;

import ch.ethz.inf.vs.a4.fmorath.pac_man.communication.Client;
import ch.ethz.inf.vs.a4.fmorath.pac_man.communication.ExampleHandler;
import ch.ethz.inf.vs.a4.fmorath.pac_man.communication.PlayerAction;
import ch.ethz.inf.vs.a4.fmorath.pac_man.communication.Server;


public class Game extends ApplicationAdapter {

	int screenWidth;
	int screenHeight;
	float scale;
	Stage stage;
	PlayerActor pacmanActor;

	TiledMap map;
	TiledMapRenderer tiledMapRenderer;
	public static Array<Rectangle> collisionRectangles = new Array<Rectangle>();

	int worldWidth;
	int worldHeight;

	OrthographicCamera camera;
	Viewport viewport;

	int collisionLayerId = 2;

	private Pool<Rectangle> rectPool = new Pool<Rectangle>() {
		@Override
		protected Rectangle newObject () {
			return new Rectangle();
		}
	};

	private Array<Rectangle> tiles = new Array<Rectangle>();

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

		// Initialize map, mapRenderer and the collision layer
		map = new TmxMapLoader().load("pacmanMap.tmx");
		worldWidth  = map.getProperties().get("width",  Integer.class);
		worldHeight = map.getProperties().get("height", Integer.class);

		tiledMapRenderer = new OrthogonalTiledMapRenderer(map);
		initCollisionRectangles();

		// Initialize camera and viewport
		camera = new OrthographicCamera();
		viewport = new FitViewport(4 * worldWidth, 4 * worldHeight, camera);

		// Initialize stage
		stage = new Stage(viewport);

		// Initialize PacMan Actor
		pacmanActor = new PlayerActor();
//		pacmanActor.setPosition(camera.viewportWidth/2 - (pacmanActor.getWidth()/2)*pacmanActor.getScaleX(),
//				camera.viewportHeight/2 - (pacmanActor.getHeight()/2)*pacmanActor.getScaleY());

		stage.addActor(pacmanActor);

		Gdx.input.setInputProcessor(stage);
	}

	@Override
	public void render () {
		Gdx.gl.glClearColor(1, 0, 0, 1);
		Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT);

		Gdx.gl.glViewport(0, 0, screenWidth, screenHeight);

		camera.update();

		tiledMapRenderer.setView(camera);
		tiledMapRenderer.render();

//		stage.getBatch().setProjectionMatrix(camera.combined);
		stage.act(Gdx.graphics.getDeltaTime());
		stage.draw();

//		WallCollisionDetection();

	}

	@Override
	public void dispose () {
		stage.dispose();
		map.dispose();
	}

	// Function to extract rectangles from the collision layer
	private void initCollisionRectangles() {
		MapLayer collisionLayer = map.getLayers().get(collisionLayerId);
		for (RectangleMapObject rectangleObject : collisionLayer.getObjects().getByType(RectangleMapObject.class))
			collisionRectangles.add(rectangleObject.getRectangle());
	}



//	private void WallCollisionDetection() {
//
//		getTiles(pacmanActor.getX(), pacmanActor.getY(), pacmanActor.getX()+pacmanActor.getWidth(), pacmanActor.getY()+pacmanActor.getHeight(), tiles);
//
//		for(Rectangle tile: tiles) {
//			Gdx.app.log("tile", "true");
//
//			if (Intersector.overlaps(tile, new Rectangle(pacmanActor.getX(),pacmanActor.getY(),pacmanActor.getWidth(),pacmanActor.getHeight()))) {
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