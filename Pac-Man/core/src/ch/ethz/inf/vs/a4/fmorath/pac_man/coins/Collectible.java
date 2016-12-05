package ch.ethz.inf.vs.a4.fmorath.pac_man.coins;

import com.badlogic.gdx.maps.MapLayer;
import com.badlogic.gdx.maps.objects.RectangleMapObject;
import com.badlogic.gdx.maps.tiled.TiledMap;
import com.badlogic.gdx.maps.tiled.TiledMapTileLayer;
import com.badlogic.gdx.math.Rectangle;
import com.badlogic.gdx.utils.Array;

import java.lang.reflect.InvocationTargetException;

import ch.ethz.inf.vs.a4.fmorath.pac_man.characters.PacMan;

/**
 * Created by linus on 05.12.2016.
 */

public abstract class Collectible {

    private TiledMapTileLayer.Cell cell1, cell2, cell3, cell4;
    protected Rectangle rectangle;

    public Collectible(TiledMapTileLayer layer, Rectangle rectangle, int posX, int posY) {
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
    
    public void collect(PacMan pacMan) {
        cell1.setTile(null);
        cell2.setTile(null);
        cell3.setTile(null);
        cell4.setTile(null);
    }
}
