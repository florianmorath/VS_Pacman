package ch.ethz.inf.vs.a4.fmorath.pac_man.figures;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.g2d.Animation;
import com.badlogic.gdx.graphics.g2d.Batch;
import com.badlogic.gdx.graphics.g2d.Sprite;
import com.badlogic.gdx.graphics.g2d.TextureAtlas;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.math.Intersector;
import com.badlogic.gdx.math.Rectangle;

import org.w3c.dom.css.Rect;

import ch.ethz.inf.vs.a4.fmorath.pac_man.Player;
import ch.ethz.inf.vs.a4.fmorath.pac_man.Round;

/**
 * Created by linus on 04.12.2016.
 */

public class Ghost extends Figure {

    public static final Color BLINKY = new Color(255/255f, 0/255f,   0/255f,   1);
    public static final Color PINKY  = new Color(255/255f, 184/255f, 222/255f, 1);
    public static final Color INKY   = new Color(0/255f,   255/255f, 222/255f, 1);
    public static final Color CLYDE  = new Color(255/255f, 184/255f, 71/255f,  1);

    private PacMan pacMan;
    private Color color;
    private Sprite currentSprite;
    private Sprite spriteUp, spriteRight, spriteDown, spriteLeft;
    private Animation animation, blueAnimation;

    private boolean isVulnerable;
    public void setVulnerable(boolean isVulnerable) {
        this.isVulnerable = isVulnerable;
    }

    @Override
    protected float getSpeed() {
        return super.getSpeed() / (isVulnerable ? 2 : 1);
    }

    @Override
    protected void initAnimations() {
        TextureRegion[] frames, blueframes;
        TextureAtlas textureAtlas = new TextureAtlas(Gdx.files.internal("sprites/ghost.atlas"));

        frames = new TextureRegion[2];
        frames[0] = textureAtlas.findRegion("ghost_1");
        frames[1] = textureAtlas.findRegion("ghost_2");
        animation = new Animation(FRAME_DURATION, frames);

        blueframes = new TextureRegion[2];
        blueframes[0] = textureAtlas.findRegion("ghost_blue_1");
        blueframes[1] = textureAtlas.findRegion("ghost_blue_2");
        blueAnimation = new Animation(FRAME_DURATION, blueframes);

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

    public Ghost(Round round, int x, int y, PacMan pacMan, Color color) {
        super(round, x, y);
        this.pacMan = pacMan;
        this.color = color;
        this.setWidth(currentSprite.getWidth());
        this.setHeight(currentSprite.getHeight());
        this.setPosition(x, y);
        this.isVulnerable = false;
    }

    @Override
    public void act(float delta) {
        super.act(delta);
        if (intersects(pacMan)) {
            if (isVulnerable) {
                round.onGhostEaten();
                setPosition(104, 124);
                isVulnerable = false;
            } else {
                pacMan.onDeath();
                round.end(false);
            }
        }
    }

    private boolean intersects(PacMan pacMan) {
        Rectangle r1 = getRectangle();
        Rectangle r2 = pacMan.getRectangle();
        Rectangle intersection = new Rectangle();
        intersection.x = Math.max(r1.x, r2.x);
        intersection.setWidth(Math.min(r1.x + r1.width, r2.x + r2.width) - intersection.x);
        intersection.y = Math.max(r1.y, r2.y);
        intersection.setHeight(Math.min(r1.y + r1.height, r2.y + r2.height) - intersection.y);
        return intersection.width >= r2.width / 2 && intersection.height >= r2.height / 2;
    }

    @Override
    public void draw(Batch batch, float parentAlpha) {
        super.draw(batch, parentAlpha);
        if (!isVulnerable) {
            // Draw ghost base before eyes
            batch.setColor(color);
            batch.draw(animation.getKeyFrame(elapsedTime, true), getX(), getY());
            batch.setColor(Color.WHITE);

            // Draw eyes
            batch.draw(currentSprite, getX(), getY());
        } else
            batch.draw(blueAnimation.getKeyFrame(elapsedTime, true), getX(), getY());
    }
}
