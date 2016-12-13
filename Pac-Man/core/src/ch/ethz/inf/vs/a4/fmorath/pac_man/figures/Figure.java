package ch.ethz.inf.vs.a4.fmorath.pac_man.figures;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.g2d.Batch;
import com.badlogic.gdx.input.GestureDetector;
import com.badlogic.gdx.math.Intersector;
import com.badlogic.gdx.math.Rectangle;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.scenes.scene2d.Actor;

import ch.ethz.inf.vs.a4.fmorath.pac_man.Player;
import ch.ethz.inf.vs.a4.fmorath.pac_man.Round;
import ch.ethz.inf.vs.a4.fmorath.pac_man.MovementDirection;
import ch.ethz.inf.vs.a4.fmorath.pac_man.coins.Collectible;

/**
 * Created by linus on 04.12.2016.
 */

public abstract class Figure extends Actor {

    protected static final int CORNER_TOLERANCE = 5;
    protected static final float FRAME_DURATION = 0.05f;
    private static final float SPEED = 16 * 4.5f; // 11 tiles per second in original pacman. 2 tiles = 2 world units. (4.5f)

    protected MovementDirection currentDirection = MovementDirection.NONE;
    protected float elapsedTime = 0f;

    protected Player player;
    public void setPlayer(Player player) {
        this.player = player;
        if (player.isLocalPlayer())
            Gdx.input.setInputProcessor(new GestureDetector(new MovementGestureAdapter()));
    }

    protected Round round;

    protected abstract void initAnimations();
    protected abstract void updateRepresentation();

    public Rectangle getRectangle() {
        return new Rectangle(getX(), getY(), getWidth(), getHeight());
    }

    public Figure(Round round, int x, int y) {
        this.round = round;
        this.setPosition(x, y);
        initAnimations();
    }

    @Override
    public void act(float delta) {
        move(delta);
    }
    private void move(float delta) {
        float distance = SPEED * delta;
        Vector2 direction = currentDirection.getVector();
        Vector2 position = new Vector2(getX(), getY());
        position = position.add(direction.scl(distance));

        float viewportWidth = getStage().getCamera().viewportWidth;
        float viewportHeight = getStage().getCamera().viewportHeight;

        float halfWidth = getWidth() / 2;
        float halfHeight = getHeight() / 2;

        position.x = (position.x + halfWidth + viewportWidth) % viewportWidth - halfWidth;
        position.y = (position.y + halfHeight + viewportHeight) % viewportHeight - halfHeight;

        Rectangle player = new Rectangle(position.x, position.y, this.getWidth(), this.getHeight());
        for (Rectangle wall : round.getWalls()) {
            if (Intersector.overlaps(wall, player)) {
                if (direction.x != 0) {
                    if (Math.abs(wall.y + wall.height - position.y) < CORNER_TOLERANCE)
                        position.y = wall.y + wall.height;
                    else if (Math.abs(position.y + getHeight() - wall.y) < CORNER_TOLERANCE)
                        position.y = wall.y - getHeight();
                    else {
                        if (currentDirection == MovementDirection.RIGHT)
                            position.x = wall.x - player.width;
                        else if (currentDirection == MovementDirection.LEFT)
                            position.x = wall.x + wall.width;
                        currentDirection = MovementDirection.NONE;
                    }
                } else if (direction.y != 0) {
                    if (Math.abs(wall.x + wall.width - position.x) < CORNER_TOLERANCE)
                        position.x = wall.x + wall.width;
                    else if (Math.abs(position.x + getWidth() - wall.x) < CORNER_TOLERANCE)
                        position.x = wall.x - getWidth();
                    else {
                        if (currentDirection == MovementDirection.UP)
                            position.y = wall.y - player.height;
                        else if (currentDirection == MovementDirection.DOWN)
                            position.y = wall.y + wall.height;
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
    }

    private class MovementGestureAdapter extends GestureDetector.GestureAdapter {
        @Override
        public boolean fling(float velocityX, float velocityY, int button) {
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
            updateRepresentation();

            return true;
        }
    }
}
