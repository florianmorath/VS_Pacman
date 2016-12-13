package ch.ethz.inf.vs.a4.fmorath.pac_man.coins;

import com.badlogic.gdx.maps.tiled.TiledMapTileLayer;
import com.badlogic.gdx.math.Rectangle;
import com.badlogic.gdx.utils.Array;

import ch.ethz.inf.vs.a4.fmorath.pac_man.Player;
import ch.ethz.inf.vs.a4.fmorath.pac_man.figures.Figure;
import ch.ethz.inf.vs.a4.fmorath.pac_man.figures.PacMan;

/**
 * Created by linus on 05.12.2016.
 */

public abstract class Collectible {

    private Array<Collectible> collectibles;
    private TiledMapTileLayer.Cell cell1, cell2, cell3, cell4;
    protected Rectangle rectangle;

    public Collectible(Array<Collectible> collectibles, TiledMapTileLayer layer, Rectangle rectangle, int posX, int posY) {
        this.collectibles = collectibles;
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
        collectibles.removeValue(this, true);
    }
}
