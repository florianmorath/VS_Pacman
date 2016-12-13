package ch.ethz.inf.vs.a4.fmorath.pac_man.coins;

import com.badlogic.gdx.maps.tiled.TiledMapTileLayer;
import com.badlogic.gdx.math.Rectangle;
import com.badlogic.gdx.utils.Array;

import ch.ethz.inf.vs.a4.fmorath.pac_man.Player;
import ch.ethz.inf.vs.a4.fmorath.pac_man.figures.PacMan;

/**
 * Created by linus on 05.12.2016.
 */

public class LargeCoin extends Coin {

    public LargeCoin(Array<Coin> collectibles, TiledMapTileLayer layer, Rectangle rectangle) {
        super(collectibles, layer, rectangle);
    }

    @Override
    public void collect(Player player) {
        super.collect(player);
        player.increaseScore(50);
        if (player.getFigure() instanceof PacMan)
            ((PacMan) player.getFigure()).onLargeCoinCollected();
    }
}
