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
import com.badlogic.gdx.utils.Align;
import com.badlogic.gdx.utils.Array;
import com.badlogic.gdx.utils.viewport.Viewport;

import ch.ethz.inf.vs.a4.fmorath.pac_man.figures.Figure;
import ch.ethz.inf.vs.a4.fmorath.pac_man.figures.PacMan;
import ch.ethz.inf.vs.a4.fmorath.pac_man.figures.Ghost;
import ch.ethz.inf.vs.a4.fmorath.pac_man.coins.Coin;
import ch.ethz.inf.vs.a4.fmorath.pac_man.coins.LargeCoin;
import ch.ethz.inf.vs.a4.fmorath.pac_man.coins.SmallCoin;

/**
 * Created by markus on 25.11.16.
 */

public class Round extends Stage {

    private static final int LARGE_COIN_DURATION = 10;

    private final int backgroundLayerId = 0;
    private final int wallsLayerId = 1;
    private final int wallsCollisionLayerId = 2;
    private final int smallCoinsLayerId = 3;
    private final int smallCoinsCollisionLayerId = 4;
    private final int largeCoinsLayerId = 5;
    private final int largeCoinsCollisionLayerId = 6;

    private Game game;
    private int roundNumber;
    private Array<Player> players;
    private PacMan pacMan;
    private Figure[] figures;

    private TiledMap map;
    private BitmapFont font;

    private Array<Rectangle> walls = new Array<Rectangle>();
    public Array<Rectangle> getWalls() {
        return walls;
    }

    private Array<Coin> coins = new Array<Coin>();
    public Array<Coin> getCoins() {
        return coins;
    }

    private float largeCoinCountdown;
    private int exponent;

    public Round(Game game, int roundNumber, Viewport viewport, TiledMap map, Array<Player> players) {
        super(viewport);
        this.game = game;
        this.roundNumber = roundNumber;
        this.map = map;
        this.players = players;

        FreeTypeFontGenerator generator = new FreeTypeFontGenerator(Gdx.files.internal("fonts/font.ttf"));
        FreeTypeFontGenerator.FreeTypeFontParameter parameter = new FreeTypeFontGenerator.FreeTypeFontParameter();
        parameter.size = 8;
        font = generator.generateFont(parameter);
        generator.dispose();

        initWalls();
        initCoins();

        pacMan = new PacMan(this, 104, 52);
        figures = new Figure[]{
                pacMan,
                new Ghost (this, 104, 148, pacMan, Ghost.BLINKY),
                new Ghost (this, 104, 124, pacMan, Ghost.PINKY),
                new Ghost (this, 88, 124, pacMan, Ghost.INKY),
                new Ghost (this, 120, 124, pacMan, Ghost.CLYDE)
        };

        int i = roundNumber;
        for (Player player : players) {
            player.setFigure(figures[i]);
            addActor(figures[i]);
            i = (i + 1) % players.size;
        }
    }

    @Override
    public void act(float delta) {
        super.act(delta);
        if (largeCoinCountdown > 0)
            largeCoinCountdown -= delta;
        else
            setGhostsVulnerability(false);
    }

    @Override
    public void draw() {
        super.draw();
        Batch batch = getBatch();
        batch.begin();

        font.draw(batch, "SCORE",                                               4,   271, 0, Align.left,   false);
        font.draw(batch, Integer.toString(players.get(roundNumber).getScore()), 4,   262, 0, Align.left,   false);

        font.draw(batch, "HIGH SCORE",                                          113, 271, 0, Align.center, false);
        font.draw(batch, Integer.toString(game.getHighScore()),                 113, 262, 0, Align.center, false);

        if (largeCoinCountdown > 0)
            font.draw(batch, Integer.toString((int) largeCoinCountdown),        220, 262, 0, Align.right,  false);

        batch.end();
    }

    public void onLargeCoinCollected() {
        setGhostsVulnerability(true);
        largeCoinCountdown = LARGE_COIN_DURATION;
        exponent = 1;
    }

    private void setGhostsVulnerability(boolean value) {
        for (Figure figure : figures)
            if (figure instanceof Ghost)
                ((Ghost) figure).setVulnerable(value);
    }

    public void onGhostEaten() {
        pacMan.getPlayer().increaseScore((int) Math.pow(200, exponent++));
    }

    public void end() {
        game.endRound();
    }

    private void initWalls() {
        MapLayer wallLayer = map.getLayers().get(wallsCollisionLayerId);
        for (RectangleMapObject rectangleMapObject : wallLayer.getObjects().getByType(RectangleMapObject.class))
            walls.add(rectangleMapObject.getRectangle());
    }

    private void initCoins() {
        TiledMapTileLayer smallCoinsLayer = (TiledMapTileLayer) map.getLayers().get(smallCoinsLayerId);
        MapLayer smallCoinsCollisionLayer = map.getLayers().get(smallCoinsCollisionLayerId);
        for (RectangleMapObject rectangleMapObject : smallCoinsCollisionLayer.getObjects().getByType(RectangleMapObject.class)) {
            Rectangle rectangle = rectangleMapObject.getRectangle();
            coins.add(new SmallCoin(coins, smallCoinsLayer, rectangle));
        }

        TiledMapTileLayer largeCoinsLayer = (TiledMapTileLayer) map.getLayers().get(largeCoinsLayerId);
        MapLayer largeCoinsCollisionLayer = map.getLayers().get(largeCoinsCollisionLayerId);
        for (RectangleMapObject rectangleMapObject : largeCoinsCollisionLayer.getObjects().getByType(RectangleMapObject.class)) {
            Rectangle rectangle = rectangleMapObject.getRectangle();
            coins.add(new LargeCoin(coins, largeCoinsLayer, rectangle));
        }
    }
}
