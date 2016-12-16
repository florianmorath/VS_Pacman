package ch.ethz.inf.vs.a4.fmorath.pac_man.coins;

import com.badlogic.gdx.maps.tiled.TiledMapTileLayer;
import com.badlogic.gdx.math.Rectangle;
import com.badlogic.gdx.utils.Array;

import ch.ethz.inf.vs.a4.fmorath.pac_man.Game;
import ch.ethz.inf.vs.a4.fmorath.pac_man.Player;
import ch.ethz.inf.vs.a4.fmorath.pac_man.Round;
import ch.ethz.inf.vs.a4.fmorath.pac_man.actions.EatCoinAction;

/**
 * Created by linus on 05.12.2016.
 */

public class SmallCoin extends Coin {

    public SmallCoin(Round round, Array<Coin> collectibles, TiledMapTileLayer layer, Rectangle rectangle) {
        super(round, collectibles, layer, rectangle);
    }

    @Override
    public void collect(Player player) {
        super.collect(player);
        player.increaseScore(10);
        round.playWaka();
    }
}
