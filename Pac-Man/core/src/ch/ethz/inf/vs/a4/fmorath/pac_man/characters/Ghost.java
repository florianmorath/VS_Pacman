package ch.ethz.inf.vs.a4.fmorath.pac_man.characters;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.g2d.Animation;
import com.badlogic.gdx.graphics.g2d.Batch;
import com.badlogic.gdx.graphics.g2d.Sprite;
import com.badlogic.gdx.graphics.g2d.TextureAtlas;
import com.badlogic.gdx.graphics.g2d.TextureRegion;

import ch.ethz.inf.vs.a4.fmorath.pac_man.Map;
import ch.ethz.inf.vs.a4.fmorath.pac_man.MovementDirection;

/**
 * Created by linus on 04.12.2016.
 */

public class Ghost extends Character {

    public static final Color BLINKY = new Color(255/255f, 95/255f,  95/255f,  1);
    public static final Color PINKY  = new Color(255/255f, 184/255f, 255/255f, 1);
    public static final Color INKY   = new Color(1/255f,   255/255f, 255/255f, 1);
    public static final Color CLYDE  = new Color(255/255f, 184/255f, 81/255f,  1);

    private Color color;
    private Sprite currentSprite;
    private Sprite spriteUp, spriteRight, spriteDown, spriteLeft;
    private Animation animation;

    @Override
    protected void initAnimations() {
        TextureRegion[] frames;
        TextureAtlas textureAtlas = new TextureAtlas(Gdx.files.internal("sprites/ghost.atlas"));

        frames = new TextureRegion[2];
        frames[0] = textureAtlas.findRegion("ghost_1");
        frames[1] = textureAtlas.findRegion("ghost_2");

        animation  = new Animation(FRAME_DURATION, frames);

        spriteUp = new Sprite(textureAtlas.findRegion("ghost_up"));
        spriteRight = new Sprite(textureAtlas.findRegion("ghost_right"));
        spriteDown = new Sprite(textureAtlas.findRegion("ghost_down"));
        spriteLeft = new Sprite(textureAtlas.findRegion("ghost_left"));

        currentSprite = spriteLeft;
    }

    @Override
    protected void updateRepresentation() {
        switch (currentDirection){
            case UP: currentSprite = spriteUp; break;
            case RIGHT: currentSprite = spriteRight; break;
            case DOWN: currentSprite = spriteDown; break;
            case LEFT: currentSprite = spriteLeft; break;
        }
    }

    public Ghost(Map map, int x, int y, Color color) {
        super(map, x, y);
        this.color = color;
        this.setWidth(currentSprite.getWidth());
        this.setHeight(currentSprite.getHeight());
        this.setPosition(x, y);
    }

    @Override
    public void draw(Batch batch, float parentAlpha) {
        super.draw(batch, parentAlpha);

        // Draw ghost base before eyes
        batch.setColor(color);
        batch.draw(animation.getKeyFrame(elapsedTime, true), getX(), getY());
        batch.setColor(Color.WHITE);

        // Draw eyes
        batch.draw(currentSprite, getX(), getY());
    }
}
