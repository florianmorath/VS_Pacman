package ch.ethz.inf.vs.a4.fmorath.pac_man.figures;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.g2d.Batch;
import com.badlogic.gdx.input.GestureDetector;
import com.badlogic.gdx.math.Intersector;
import com.badlogic.gdx.math.Rectangle;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.scenes.scene2d.Actor;
import com.badlogic.gdx.utils.Array;

import java.io.IOException;
import java.util.ArrayDeque;
import java.util.Queue;

import ch.ethz.inf.vs.a4.fmorath.pac_man.Game;
import ch.ethz.inf.vs.a4.fmorath.pac_man.Player;
import ch.ethz.inf.vs.a4.fmorath.pac_man.Round;
import ch.ethz.inf.vs.a4.fmorath.pac_man.MovementDirection;
import ch.ethz.inf.vs.a4.fmorath.pac_man.actions.MovementAction;

/**
 * Created by linus on 04.12.2016.
 */

public abstract class Figure extends Actor {

    protected static final float FRAME_DURATION = 0.05f;
    private static final float SPEED = 16 * 4.5f; // 11 tiles per second in original pacman. 2 tiles = 2 world units. (4.5f)
    protected float getSpeed() {
        return SPEED;
    }

    protected MovementDirection currentDirection = MovementDirection.NONE;
    private MovementDirection newDirection = MovementDirection.NONE;

    protected float elapsedTime = 0f;

    private ArrayDeque<PosDir> catchUpQueue = new ArrayDeque<PosDir>();
    private class PosDir {
        public Vector2 position;
        public MovementDirection direction;
        public PosDir(Vector2 position, MovementDirection direction) {
            this.position = position;
            this.direction = direction;
        }
    }

    protected Player player;
    public Player getPlayer() {
        return player;
    }
    public void setPlayer(Player player) {
        this.player = player;
        if (player.isLocalPlayer())
            Gdx.input.setInputProcessor(new GestureDetector(new MovementGestureAdapter()));
    }

    protected Round round;
    protected Array<Rectangle> getWalls() {
        return round.getWalls();
    }


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
        if (!catchUpQueue.isEmpty())
            catchUp(delta);
        else {
            tryToChangeDirection(delta);
            tryToMove(delta);
        }
    }

    private void catchUp(float delta) {
        Vector2 targetPosition = catchUpQueue.getFirst().position;

        float distance = 2 * getSpeed() * delta;
        Vector2 position = new Vector2(getX(), getY());
        Vector2 direction = (targetPosition.cpy().sub(position)).nor();

        currentDirection = MovementDirection.getDirection(direction);
        updateRepresentation();

        position.add(direction.scl(distance));
        if ((targetPosition.dst(position) <= distance)) {
            setPosition(targetPosition.x, targetPosition.y);
            currentDirection = catchUpQueue.remove().direction;
            updateRepresentation();
        } else
            setPosition(position.x, position.y);
    }

    private void tryToChangeDirection(float delta) {
        if (currentDirection == newDirection || newDirection == MovementDirection.NONE)
            return;

        float distance = getSpeed() * delta;
        Vector2 direction = newDirection.getVector();
        Vector2 position = new Vector2(getX(), getY());
        position = position.add(direction.scl(distance));

        boolean noCollision = true;
        Vector2 newPosition = new Vector2(position);
        Rectangle player = new Rectangle(position.x, position.y, this.getWidth(), this.getHeight());
        for (Rectangle wall : getWalls()) {
            if (Intersector.overlaps(wall, player)) {
                noCollision = false;

                float maxDistance = delta * getSpeed();
                if (direction.x != 0) {
                    if (Math.abs(wall.y + wall.height - position.y) <= maxDistance)
                        newPosition.y = wall.y + wall.height;
                    else if (Math.abs(position.y + getHeight() - wall.y) <= maxDistance)
                        newPosition.y = wall.y - getHeight();
                } else if (direction.y != 0) {
                    if (Math.abs(wall.x + wall.width - position.x) <= maxDistance)
                        newPosition.x = wall.x + wall.width;
                    else if (Math.abs(position.x + getWidth() - wall.x) <= maxDistance)
                        newPosition.x = wall.x - getWidth();
                }
            }
        }

        if ((noCollision || !newPosition.equals(position)) && canMoveToPosition(newPosition.add(direction.scl(distance)), newDirection)) {
            MovementAction action = new MovementAction(getPlayer().getPlayerId(), false, newPosition.x, newPosition.y, newDirection);
            try {
                Game.getInstance().communicator.send(action);
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

    public void updatePositionAndDirection(MovementAction action) {
        synchronized (this) {
            Vector2 position = new Vector2(action.positionX, action.positionY);
            Vector2 lastPosition = !catchUpQueue.isEmpty() ? catchUpQueue.getLast().position : new Vector2(getX(), getY());
            if (player.isLocalPlayer() || (lastPosition.cpy().dst(position) > round.getWidth() / 2)) {
                if (!player.isLocalPlayer())
                    catchUpQueue.clear();
                setPosition(position.x, position.y);
                currentDirection = action.newDirection;
                updateRepresentation();
            } else
                 catchUpQueue.add(new PosDir(new Vector2(action.positionX, action.positionY), action.newDirection));
        }
    }

    private void tryToMove(float delta) {
        float distance = getSpeed() * delta;
        Vector2 direction = currentDirection.getVector();
        Vector2 position = new Vector2(getX(), getY());
        position = position.add(direction.scl(distance));

        if (canMoveToPosition(position, currentDirection)) {
            float viewportWidth = getStage().getCamera().viewportWidth;
            float viewportHeight = getStage().getCamera().viewportHeight;

            float halfWidth = getWidth() / 2;
            float halfHeight = getHeight() / 2;

            position.x = (position.x + halfWidth + viewportWidth) % viewportWidth - halfWidth;
            position.y = (position.y + halfHeight + viewportHeight) % viewportHeight - halfHeight;

            this.setPosition(position.x, position.y);
        } else {
            newDirection = MovementDirection.NONE;
            currentDirection = MovementDirection.NONE;
        }
    }

    private boolean canMoveToPosition(Vector2 position, MovementDirection direction) {
        Rectangle figure = new Rectangle(position.x, position.y, this.getWidth(), this.getHeight());
        for (Rectangle wall : getWalls()) {
            if (Intersector.overlaps(wall, figure)) {
                switch (direction) {
                    case RIGHT: position.x = wall.x - figure.width;  break;
                    case LEFT:  position.x = wall.x + wall.width;    break;
                    case UP:    position.y = wall.y - figure.height; break;
                    case DOWN:  position.y = wall.y + wall.height;   break;
                }
                return false;
            }
        }
        return true;
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
                    newDirection = MovementDirection.RIGHT;
                } else {
                    // Left fling
                    newDirection = MovementDirection.LEFT;
                }
            } else {
                // Y dominated
                if (velocityY >= 0) {
                    //Down fling
                    newDirection = MovementDirection.DOWN;
                } else {
                    // Up fling
                    newDirection = MovementDirection.UP;
                }
            }

            return true;
        }
    }
}
