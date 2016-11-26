package com.mygdx.game;

import com.badlogic.gdx.ApplicationAdapter;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.InputProcessor;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.Sprite;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.maps.MapObjects;
import com.badlogic.gdx.maps.objects.RectangleMapObject;
import com.badlogic.gdx.maps.tiled.TiledMap;
import com.badlogic.gdx.maps.tiled.TiledMapRenderer;
import com.badlogic.gdx.maps.tiled.TiledMapTileLayer;
import com.badlogic.gdx.maps.tiled.TmxMapLoader;
import com.badlogic.gdx.maps.tiled.renderers.OrthogonalTiledMapRenderer;
import com.badlogic.gdx.math.Intersector;
import com.badlogic.gdx.math.Rectangle;
import com.badlogic.gdx.scenes.scene2d.Stage;
import com.badlogic.gdx.utils.Array;
import com.badlogic.gdx.utils.Pool;
import com.badlogic.gdx.utils.Scaling;
import com.badlogic.gdx.utils.viewport.FillViewport;
import com.badlogic.gdx.utils.viewport.ScalingViewport;
import com.badlogic.gdx.utils.viewport.StretchViewport;
import com.badlogic.gdx.utils.viewport.Viewport;


public class MyGdxGame extends ApplicationAdapter{

	int screenWidth;
	int screenHeight;
	Stage stage;
	PlayerActor pacmanActor;

	TiledMap map;
	TiledMapRenderer tiledMapRenderer;

	int worldWidth;
	int worldHeight;
	float aspectRatio;

	OrthographicCamera camera;
	Viewport viewport;

	private Pool<Rectangle> rectPool = new Pool<Rectangle>() {
		@Override
		protected Rectangle newObject () {
			return new Rectangle();
		}
	};

	private Array<Rectangle> tiles = new Array<Rectangle>();

	@Override
	public void create () {
		screenWidth = Gdx.graphics.getWidth();
		screenHeight = Gdx.graphics.getHeight();
		aspectRatio = (float)screenHeight/(float)screenWidth;

		// Initialize map and mapRenderer
		map = new TmxMapLoader().load("pacmanMap.tmx");
		tiledMapRenderer = new OrthogonalTiledMapRenderer(map, 4.82f);

		worldWidth  = map.getProperties().get("width",  Integer.class);
		worldHeight = map.getProperties().get("height", Integer.class);


		// Create camera and viewport
		camera = new OrthographicCamera(worldWidth, worldWidth*aspectRatio);
		camera.setToOrtho(false, Gdx.graphics.getWidth() / 4, Gdx.graphics.getHeight() / 4);
		//viewport = new StretchViewport(screenWidth, screenHeight, camera);
		viewport = new ScalingViewport(Scaling.fill, screenWidth, screenHeight, camera);
		viewport.apply();
		camera.position.set(worldWidth/2, worldHeight/2, 0);

		// Initialize stage
		stage = new Stage(viewport);

		// Initialize PacMan Actor
		pacmanActor = new PlayerActor();
		pacmanActor.setScale(4f);
		pacmanActor.setPosition(camera.viewportWidth/2 - (pacmanActor.getWidth()/2)*pacmanActor.getScaleX(),
				camera.viewportHeight/2 - (pacmanActor.getHeight()/2)*pacmanActor.getScaleY());

		stage.addActor(pacmanActor);

		Gdx.input.setInputProcessor(stage);
	}

	@Override
	public void render () {
		Gdx.gl.glClearColor(1, 0, 0, 1);
		Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT);

		camera.update();

		tiledMapRenderer.setView(camera);
		tiledMapRenderer.render();

		stage.getBatch().setProjectionMatrix(camera.combined);
		stage.act(Gdx.graphics.getDeltaTime());
		stage.draw();

		WallCollisionDetection();

	}
	
	@Override
	public void dispose () {
		stage.dispose();
		map.dispose();
	}

	@Override
	public void resize(int width, int height) {
		viewport.update(width, height);
		camera.position.set(camera.viewportWidth/2, camera.viewportHeight/2, 0);
	}



	private void WallCollisionDetection() {

		getTiles(pacmanActor.getX(), pacmanActor.getY(), pacmanActor.getX()+pacmanActor.getWidth(), pacmanActor.getY()+pacmanActor.getHeight(), tiles);

		for(Rectangle tile: tiles) {
			Gdx.app.log("tile", "true");

			if (Intersector.overlaps(tile, new Rectangle(pacmanActor.getX(),pacmanActor.getY(),pacmanActor.getWidth(),pacmanActor.getHeight()))) {
				// collision happened
				Gdx.app.log("collision", "true");

			}
		}

	}

	private void getTiles(float startX, float startY, float endX, float endY, Array<Rectangle> tiles) {
		
		int startXX = Math.round(startX);
		int startYY = Math.round(startY);
		TiledMapTileLayer layer = (TiledMapTileLayer)map.getLayers().get("Walls Layer");

		rectPool.freeAll(tiles);
		tiles.clear();
		for(int y =  startYY; y <= endY; y++) {
			for(int x = startXX; x <= endX; x++) {
				TiledMapTileLayer.Cell cell = layer.getCell(x, y);

				if(cell != null) {
					Gdx.app.log("cellFound", "true");

					Rectangle rect = rectPool.obtain();
					rect.set(x, y, 1, 1);
					tiles.add(rect);

				}
			}
		}
	}


}
