package ch.ethz.inf.vs.a4.fmorath.pac_man;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.g2d.Animation;
import com.badlogic.gdx.graphics.g2d.Batch;
import com.badlogic.gdx.graphics.g2d.TextureAtlas;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.input.GestureDetector;
import com.badlogic.gdx.math.Intersector;
import com.badlogic.gdx.math.Rectangle;
import com.badlogic.gdx.scenes.scene2d.Actor;

/**
 * Created by markus on 25.11.16.
 */

public class Player extends Actor{

    private Animation animation;
    private MovementDirection currentDirection;
    private float elapsedTime = 0f;

    private float SPEED = 16 * 4.5f; // 11 tiles per second in original pacman. 2 tiles = 2 world units. (4.5f)

    public Player() {
        // Set current direction to an initial value
        currentDirection = MovementDirection.NONE;

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

        float posX = getX();
        float posY = getY();

        switch(currentDirection){
            case RIGHT:
                posX += delta * SPEED * getScaleX();
                break;
            case LEFT:
                posX -= delta * SPEED * getScaleX();
                break;
            case UP:
                posY += delta * SPEED * getScaleY();
                break;
            case DOWN:
                posY -= delta * SPEED * getScaleY();
                break;
            default: break;
        }

        float viewportWidth = this.getStage().getCamera().viewportWidth;
        float viewportHeight = this.getStage().getCamera().viewportHeight;

        posX = (posX + viewportWidth) % viewportWidth;
        posY = (posY + viewportHeight) % viewportHeight;

        Rectangle player = new Rectangle(posX, posY, this.getWidth(), this.getHeight());
        for (Rectangle rect : Game.collisionRectangles) {
            if (Intersector.overlaps(rect, player)) {
                switch(currentDirection){
                    case RIGHT:
                        posX = rect.x - player.width;
                        break;
                    case LEFT:
                        posX = rect.x + rect.width;
                        break;
                    case UP:
                        posY = rect.y - player.height;
                        break;
                    case DOWN:
                        posY = rect.y + rect.height;
                        break;
                    default: break;
                }
                currentDirection = MovementDirection.NONE;
            }
        }

        this.setPosition(posX, posY);
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
