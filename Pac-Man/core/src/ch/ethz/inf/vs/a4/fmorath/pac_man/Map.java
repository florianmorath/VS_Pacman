package ch.ethz.inf.vs.a4.fmorath.pac_man;

import com.badlogic.gdx.maps.MapLayer;
import com.badlogic.gdx.maps.objects.RectangleMapObject;
import com.badlogic.gdx.maps.tiled.TiledMap;
import com.badlogic.gdx.maps.tiled.TiledMapTileLayer;
import com.badlogic.gdx.math.Rectangle;
import com.badlogic.gdx.scenes.scene2d.Stage;
import com.badlogic.gdx.utils.Array;
import com.badlogic.gdx.utils.viewport.Viewport;

import ch.ethz.inf.vs.a4.fmorath.pac_man.characters.PacMan;
import ch.ethz.inf.vs.a4.fmorath.pac_man.characters.Ghost;
import ch.ethz.inf.vs.a4.fmorath.pac_man.coins.Collectible;
import ch.ethz.inf.vs.a4.fmorath.pac_man.coins.SmallCoin;

/**
 * Created by markus on 25.11.16.
 */

public class Map extends Stage {

    private final int backgroundLayerId = 0;
    private final int wallsLayerId = 1;
    private final int wallsCollisionLayerId = 2;
    private final int smallCoinsLayerId = 3;
    private final int smallCoinsCollisionLayerId = 4;
    private final int largeCoinsLayerId = 5;
    private final int largeCoinsCollisionLayerId = 6;

    private TiledMap tiledMap;
    private Array<Rectangle> walls = new Array<Rectangle>();
    private Array<Collectible> collectibles = new Array<Collectible>();

    public Array<Rectangle> getWalls() {
        return walls;
    }

    public Array<Collectible> getCollectibles() {
        return collectibles;
    }

    public Map(Viewport viewport, TiledMap tiledMap) {
        super(viewport);
        this.tiledMap = tiledMap;

        initWalls();
        initCollectibles();

        Ghost blinky = new Ghost(this, 104, 148, Ghost.BLINKY);
        Ghost pinky = new Ghost(this, 104, 124, Ghost.PINKY);
        Ghost inky = new Ghost(this, 88, 124, Ghost.INKY);
        Ghost clyde = new Ghost(this, 120, 124, Ghost.CLYDE);
        PacMan pacMan = new PacMan(this, 104, 52);

        this.addActor(blinky);
        this.addActor(pinky);
        this.addActor(inky);
        this.addActor(clyde);
        this.addActor(pacMan);
    }

    private void initWalls() {
        MapLayer wallLayer = tiledMap.getLayers().get(wallsCollisionLayerId);
        for (RectangleMapObject rectangleMapObject : wallLayer.getObjects().getByType(RectangleMapObject.class))
            walls.add(rectangleMapObject.getRectangle());
    }

    private void initCollectibles() {
        TiledMapTileLayer smallCoinsLayer = (TiledMapTileLayer) tiledMap.getLayers().get(smallCoinsLayerId);
        MapLayer smallCoinsCollisionLayer = tiledMap.getLayers().get(smallCoinsCollisionLayerId);
        for (RectangleMapObject rectangleMapObject : smallCoinsCollisionLayer.getObjects().getByType(RectangleMapObject.class)) {
            Rectangle rectangle = rectangleMapObject.getRectangle();
            collectibles.add(new SmallCoin(smallCoinsLayer, rectangle, (int) rectangle.getX() / 4, (int) rectangle.getY() / 4));
        }

        TiledMapTileLayer largeCoinsLayer = (TiledMapTileLayer) tiledMap.getLayers().get(largeCoinsLayerId);
        MapLayer largeCoinsCollisionLayer = tiledMap.getLayers().get(largeCoinsCollisionLayerId);
        for (RectangleMapObject rectangleMapObject : largeCoinsCollisionLayer.getObjects().getByType(RectangleMapObject.class)) {
            Rectangle rectangle = rectangleMapObject.getRectangle();
            collectibles.add(new SmallCoin(largeCoinsLayer, rectangle, (int) rectangle.getX() / 4, (int) rectangle.getY() / 4));
        }
    }
}
