package ch.ethz.inf.vs.a4.fmorath.pac_man.coins;

import com.badlogic.gdx.maps.tiled.TiledMapTileLayer;
import com.badlogic.gdx.math.Rectangle;

import ch.ethz.inf.vs.a4.fmorath.pac_man.characters.PacMan;

/**
 * Created by linus on 05.12.2016.
 */

public class SmallCoin extends Collectible {

    public SmallCoin(TiledMapTileLayer layer, Rectangle rectangle, int posX, int posY) {
        super(layer, rectangle, posX, posY);
    }

    @Override
    public void collect(PacMan pacMan) {
        super.collect(pacMan);
    }
}
