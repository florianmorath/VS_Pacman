package ch.ethz.inf.vs.a4.fmorath.pac_man.figures;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.g2d.Animation;
import com.badlogic.gdx.graphics.g2d.Batch;
import com.badlogic.gdx.graphics.g2d.TextureAtlas;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.math.Rectangle;
import com.badlogic.gdx.utils.Array;

import ch.ethz.inf.vs.a4.fmorath.pac_man.*;
import ch.ethz.inf.vs.a4.fmorath.pac_man.coins.Collectible;

/**
 * Created by markus on 25.11.16.
 */

public class PacMan extends Figure {

    private Animation currentAnimation;
    private Animation animUp, animRight, animDown, animLeft;
    private Array<Rectangle> walls;

    @Override
    protected void initAnimations() {
        TextureRegion[] upFrames, rightFrames, downFrames, leftFrames;
        TextureAtlas textureAtlas = new TextureAtlas(Gdx.files.internal("sprites/pac_man.atlas"));

        upFrames = new TextureRegion[3];
        upFrames[0] = textureAtlas.findRegion("pac_man_base");
        upFrames[1] = textureAtlas.findRegion("pac_man_up_1");
        upFrames[2] = textureAtlas.findRegion("pac_man_up_2");

        rightFrames = new TextureRegion[3];
        rightFrames[0] = textureAtlas.findRegion("pac_man_base");
        rightFrames[1] = textureAtlas.findRegion("pac_man_right_1");
        rightFrames[2] = textureAtlas.findRegion("pac_man_right_2");

        downFrames = new TextureRegion[3];
        downFrames[0] = textureAtlas.findRegion("pac_man_base");
        downFrames[1] = textureAtlas.findRegion("pac_man_down_1");
        downFrames[2] = textureAtlas.findRegion("pac_man_down_2");

        leftFrames = new TextureRegion[3];
        leftFrames[0] = textureAtlas.findRegion("pac_man_base");
        leftFrames[1] = textureAtlas.findRegion("pac_man_left_1");
        leftFrames[2] = textureAtlas.findRegion("pac_man_left_2");

        animUp    = new Animation(FRAME_DURATION, upFrames);
        animRight = new Animation(FRAME_DURATION, rightFrames);
        animDown  = new Animation(FRAME_DURATION, downFrames);
        animLeft  = new Animation(FRAME_DURATION, leftFrames);

        currentAnimation = animLeft;
    }

    @Override
    protected void updateRepresentation() {
        switch (currentDirection){
            case UP: currentAnimation = animUp; break;
            case RIGHT: currentAnimation = animRight; break;
            case DOWN: currentAnimation = animDown; break;
            case LEFT: currentAnimation = animLeft; break;
        }
    }

    public PacMan(Round round, int x, int y) {
        super(round, x, y);
        this.setWidth(currentAnimation.getKeyFrame(0).getRegionWidth());
        this.setHeight(currentAnimation.getKeyFrame(0).getRegionHeight());

        walls = new Array<Rectangle>(round.getWalls());
        walls.add(new Rectangle(104, 144, 16, 4));
    }

    @Override
    public void act(float delta) {
        move(delta, walls);
        for (Collectible collectible : round.getCollectibles())
            if (collectible.intersects(this))
                collectible.collect(player);
    }

    @Override
    public void draw(Batch batch, float parentAlpha) {
        super.draw(batch, parentAlpha);
        batch.draw(currentAnimation.getKeyFrame(elapsedTime, true), getX(), getY());
    }
}
