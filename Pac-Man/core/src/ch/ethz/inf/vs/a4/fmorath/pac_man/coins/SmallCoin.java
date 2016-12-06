package ch.ethz.inf.vs.a4.fmorath.pac_man.coins;

import com.badlogic.gdx.maps.tiled.TiledMapTileLayer;
import com.badlogic.gdx.math.Rectangle;
import com.badlogic.gdx.utils.Array;

import ch.ethz.inf.vs.a4.fmorath.pac_man.Player;

/**
 * Created by linus on 05.12.2016.
 */

public class SmallCoin extends Collectible {

    public SmallCoin(Array<Collectible> collectibles, TiledMapTileLayer layer, Rectangle rectangle, int posX, int posY) {
        super(collectibles, layer, rectangle, posX, posY);
    }

    @Override
    public void collect(Player player) {
        super.collect(player);
        player.increaseScore(10);
    }
}
