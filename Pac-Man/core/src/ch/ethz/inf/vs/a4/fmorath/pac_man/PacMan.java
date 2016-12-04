package ch.ethz.inf.vs.a4.fmorath.pac_man;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.g2d.Animation;
import com.badlogic.gdx.graphics.g2d.Batch;
import com.badlogic.gdx.graphics.g2d.TextureAtlas;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.input.GestureDetector;
import com.badlogic.gdx.math.Intersector;
import com.badlogic.gdx.math.Rectangle;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.scenes.scene2d.Actor;

/**
 * Created by markus on 25.11.16.
 */

public class PacMan extends Actor {

    private Animation animation;
    private MovementDirection currentDirection = MovementDirection.NONE;
    private float elapsedTime = 0f;

    private static final float SPEED = 16 * 4.5f; // 11 tiles per second in original pacman. 2 tiles = 2 world units. (4.5f)
    private static final int CORNER_TOLERANCE = 8;

    public PacMan() {
        // Load textures
        TextureAtlas textureAtlas = new TextureAtlas(Gdx.files.internal("sprites/spritesheet.atlas"));

        // Initialize animations
        TextureRegion[] frames = new TextureRegion[textureAtlas.getRegions().size];
        for (int i = 0; i < frames.length; i++)
            frames[i] = textureAtlas.findRegion("pacman_" + i);

        // Set standard animation
        animation = new Animation(1/8f, frames);

        // set Width and Height of the animation for later use
        this.setWidth(16);
        this.setHeight(16);

        this.setOrigin(getWidth() / 2, getHeight() / 2);
        this.setPosition(104, 52);

        Gdx.input.setInputProcessor(new GestureDetector(new MovementGestureAdapter()));
    }

    @Override
    public void act(float delta) {
        // Change position according to direction and time
        float distance = SPEED * delta;
        Vector2 direction = currentDirection.getVector();
        Vector2 position = new Vector2(getX(), getY());
        position = position.add(direction.scl(distance));

        float viewportWidth = this.getStage().getCamera().viewportWidth;
        float viewportHeight = this.getStage().getCamera().viewportHeight;

        float halfWidth = getWidth() / 2;
        float halfHeight = getHeight() / 2;

        position.x = (position.x + halfWidth + viewportWidth) % viewportWidth - halfWidth;
        position.y = (position.y + halfHeight + viewportHeight) % viewportHeight - halfHeight;

        Rectangle player = new Rectangle(position.x, position.y, this.getWidth(), this.getHeight());
        for (Rectangle rect : Game.collisionRectangles) {
            if (Intersector.overlaps(rect, player)) {
                if (direction.x != 0) {
                    if (Math.abs(rect.y + rect.height - position.y) < CORNER_TOLERANCE)
                        position.y = rect.y + rect.height;
                    else if (Math.abs(position.y + getHeight() - rect.y) < CORNER_TOLERANCE)
                        position.y = rect.y - getHeight();
                    else {
                        if (currentDirection == MovementDirection.RIGHT)
                            position.x = rect.x - player.width;
                        else if (currentDirection == MovementDirection.LEFT)
                            position.x = rect.x + rect.width;
                        currentDirection = MovementDirection.NONE;
                    }
                } else if (direction.y != 0) {
                    if (Math.abs(rect.x + rect.width - position.x) < CORNER_TOLERANCE)
                        position.x = rect.x + rect.width;
                    else if (Math.abs(position.x + getWidth() - rect.x) < CORNER_TOLERANCE)
                        position.x = rect.x - getWidth();
                    else {
                        if (currentDirection == MovementDirection.UP)
                            position.y = rect.y - player.height;
                        else if (currentDirection == MovementDirection.DOWN)
                            position.y = rect.y + rect.height;
                        currentDirection = MovementDirection.NONE;
                    }
                }
            }
        }

        this.setPosition(position.x, position.y);
    }

    @Override
    public void draw(Batch batch, float parentAlpha) {
        if (currentDirection != MovementDirection.NONE)
            elapsedTime += Gdx.graphics.getDeltaTime();
        batch.draw(animation.getKeyFrame(elapsedTime, true),
                getX(), getY(),
                this.getOriginX(), this.getOriginY(),
                this.getWidth(), getHeight(),
                this.getScaleX(), this.getScaleY(),
                this.getRotation());
    }

    private class MovementGestureAdapter extends GestureDetector.GestureAdapter {

        @Override
        public boolean fling(float velocityX, float velocityY, int button) {
            //System.out.println("### Fling event: x:" + velocityX + " y:" + velocityY);

            if (Math.abs(velocityX) >= Math.abs(velocityY)) {
                // X dominated
                if (velocityX >= 0) {
                    // Right fling
                    currentDirection = MovementDirection.RIGHT;
                } else {
                    // Left fling
                    currentDirection = MovementDirection.LEFT;
                }
            } else {
                // Y dominated
                if (velocityY >= 0) {
                    //Down fling
                    currentDirection = MovementDirection.DOWN;
                } else {
                    // Up fling
                    currentDirection = MovementDirection.UP;
                }
            }

            setRotation(currentDirection.getValue() * 90);

            return true;
        }
    }
}
