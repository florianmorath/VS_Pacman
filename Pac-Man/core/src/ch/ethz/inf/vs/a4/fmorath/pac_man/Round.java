package ch.ethz.inf.vs.a4.fmorath.pac_man;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.g2d.Batch;
import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.graphics.g2d.freetype.FreeTypeFontGenerator;
import com.badlogic.gdx.maps.MapLayer;
import com.badlogic.gdx.maps.objects.RectangleMapObject;
import com.badlogic.gdx.maps.tiled.TiledMap;
import com.badlogic.gdx.maps.tiled.TiledMapTileLayer;
import com.badlogic.gdx.math.Rectangle;
import com.badlogic.gdx.scenes.scene2d.Stage;
import com.badlogic.gdx.utils.Array;
import com.badlogic.gdx.utils.viewport.Viewport;

import ch.ethz.inf.vs.a4.fmorath.pac_man.figures.Figure;
import ch.ethz.inf.vs.a4.fmorath.pac_man.figures.PacMan;
import ch.ethz.inf.vs.a4.fmorath.pac_man.figures.Ghost;
import ch.ethz.inf.vs.a4.fmorath.pac_man.coins.Collectible;
import ch.ethz.inf.vs.a4.fmorath.pac_man.coins.LargeCoin;
import ch.ethz.inf.vs.a4.fmorath.pac_man.coins.SmallCoin;

/**
 * Created by markus on 25.11.16.
 */

public class Round extends Stage {

    private final int backgroundLayerId = 0;
    private final int wallsLayerId = 1;
    private final int wallsCollisionLayerId = 2;
    private final int smallCoinsLayerId = 3;
    private final int smallCoinsCollisionLayerId = 4;
    private final int largeCoinsLayerId = 5;
    private final int largeCoinsCollisionLayerId = 6;

    private Game game;
    private Player[] players;

    private TiledMap map;
    private Array<Rectangle> walls = new Array<Rectangle>();
    private Array<Collectible> collectibles = new Array<Collectible>();

    private BitmapFont font;

    public Array<Rectangle> getWalls() {
        return walls;
    }

    public Array<Collectible> getCollectibles() {
        return collectibles;
    }

    public Round(Game game, Viewport viewport, TiledMap map, Player... players) {
        super(viewport);
        this.game = game;
        this.map = map;
        this.players = players;

        FreeTypeFontGenerator generator = new FreeTypeFontGenerator(Gdx.files.internal("fonts/font.ttf"));
        FreeTypeFontGenerator.FreeTypeFontParameter parameter = new FreeTypeFontGenerator.FreeTypeFontParameter();
        parameter.size = 8;
        font = generator.generateFont(parameter);
        generator.dispose();

        initWalls();
        initCollectibles();

        // TODO: Set figures for players based on round number
        Figure[] figures = new Figure[5];
        figures[0] = new Ghost (players[1], this, 104, 148, Ghost.BLINKY);
        figures[1] = new Ghost (players[2], this, 104, 124, Ghost.PINKY);
        figures[2] = new Ghost (players[3], this, 88,  124, Ghost.INKY);
        figures[3] = new Ghost (players[4], this, 120, 124, Ghost.CLYDE);
        figures[4] = new PacMan(players[0], this, 104, 52);

        for (int i = 0; i < players.length; i++) {
            players[i].setFigure(figures[i]);
            this.addActor(figures[i]);
        }
    }

    @Override
    public void draw() {
        super.draw();
        Batch batch = getBatch();
        batch.begin();
        font.draw(batch, "HIGH SCORE",                            113, 271, 0, 1, false);
        font.draw(batch, Integer.toString(players[0].getScore()), 57,  262, 0, 2, false);
        font.draw(batch, Integer.toString(game.getHighscore()),   137, 262, 0, 2, false);
        batch.end();
    }

    private void initWalls() {
        MapLayer wallLayer = map.getLayers().get(wallsCollisionLayerId);
        for (RectangleMapObject rectangleMapObject : wallLayer.getObjects().getByType(RectangleMapObject.class))
            walls.add(rectangleMapObject.getRectangle());
    }

    private void initCollectibles() {
        TiledMapTileLayer smallCoinsLayer = (TiledMapTileLayer) map.getLayers().get(smallCoinsLayerId);
        MapLayer smallCoinsCollisionLayer = map.getLayers().get(smallCoinsCollisionLayerId);
        int index = 0;
        for (RectangleMapObject rectangleMapObject : smallCoinsCollisionLayer.getObjects().getByType(RectangleMapObject.class)) {
            Rectangle rectangle = rectangleMapObject.getRectangle();
            collectibles.add(new SmallCoin(collectibles, smallCoinsLayer, rectangle, (int) rectangle.getX() / 4, (int) rectangle.getY() / 4));
        }

        TiledMapTileLayer largeCoinsLayer = (TiledMapTileLayer) map.getLayers().get(largeCoinsLayerId);
        MapLayer largeCoinsCollisionLayer = map.getLayers().get(largeCoinsCollisionLayerId);
        for (RectangleMapObject rectangleMapObject : largeCoinsCollisionLayer.getObjects().getByType(RectangleMapObject.class)) {
            Rectangle rectangle = rectangleMapObject.getRectangle();
            collectibles.add(new LargeCoin(collectibles, largeCoinsLayer, rectangle, (int) rectangle.getX() / 4, (int) rectangle.getY() / 4));
        }
    }
}
