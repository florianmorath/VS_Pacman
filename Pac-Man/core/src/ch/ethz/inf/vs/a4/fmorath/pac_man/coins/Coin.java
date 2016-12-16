package ch.ethz.inf.vs.a4.fmorath.pac_man.coins;

import com.badlogic.gdx.maps.tiled.TiledMapTileLayer;
import com.badlogic.gdx.math.Rectangle;
import com.badlogic.gdx.utils.Array;

import ch.ethz.inf.vs.a4.fmorath.pac_man.Game;
import ch.ethz.inf.vs.a4.fmorath.pac_man.Player;
import ch.ethz.inf.vs.a4.fmorath.pac_man.Round;
import ch.ethz.inf.vs.a4.fmorath.pac_man.actions.EatCoinAction;
import ch.ethz.inf.vs.a4.fmorath.pac_man.figures.PacMan;

/**
 * Created by linus on 05.12.2016.
 */

public abstract class Coin {

    protected Round round;
    protected Array<Coin> coins;
    private TiledMapTileLayer.Cell cell1, cell2, cell3, cell4;
    protected Rectangle rectangle;

    public Coin(Round round, Array<Coin> coins, TiledMapTileLayer layer, Rectangle rectangle) {
        this.round = round;
        this.coins = coins;

        int posX = (int) rectangle.getX() / 4;
        int posY = (int) rectangle.getY() / 4;
        this.cell1 = layer.getCell(posX, posY);
        this.cell2 = layer.getCell(posX+1, posY);
        this.cell3 = layer.getCell(posX, posY+1);
        this.cell4 = layer.getCell(posX+1, posY+1);
        this.rectangle = rectangle;
    }

    public boolean intersects(PacMan pacMan) {
        Rectangle other = pacMan.getRectangle();
        return  other.getX() <= rectangle.getX() &&
                other.getY() <= rectangle.getY() &&
                other.getX() + other.width  >= rectangle.getX() + rectangle.width &&
                other.getY() + other.height >= rectangle.getY() + rectangle.height;
    }
    
    public void collect(Player player) {
        cell1.setTile(null);
        cell2.setTile(null);
        cell3.setTile(null);
        cell4.setTile(null);
        coins.removeValue(this, true);
        if (coins.size == 0)
            round.end(true);
    }
}
