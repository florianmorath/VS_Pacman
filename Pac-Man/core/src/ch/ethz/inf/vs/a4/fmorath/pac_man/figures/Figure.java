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

import ch.ethz.inf.vs.a4.fmorath.pac_man.Player;
import ch.ethz.inf.vs.a4.fmorath.pac_man.Round;
import ch.ethz.inf.vs.a4.fmorath.pac_man.MovementDirection;
import ch.ethz.inf.vs.a4.fmorath.pac_man.communication.PlayerAction;

/**
 * Created by linus on 04.12.2016.
 */

public abstract class Figure extends Actor {

    private static final int CORNER_TOLERANCE = 2;
    protected static final float FRAME_DURATION = 0.05f;
    private static final float SPEED = 16 * 4.5f; // 11 tiles per second in original pacman. 2 tiles = 2 world units. (4.5f)
    protected float getSpeed() {
        return SPEED;
    }

    private Vector2 newPosition = null;
    protected MovementDirection currentDirection = MovementDirection.NONE;
    private MovementDirection newDirection = MovementDirection.NONE;
    protected float elapsedTime = 0f;

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

    public void setDirPos(MovementDirection newDirection,Vector2 position){
        synchronized (this){
            this.newDirection = newDirection;
            this.newPosition = position;
        }
    }

    public boolean positionChangeAvailable(){
        return newPosition != null && newDirection != MovementDirection.NONE;
    }

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
        updateFromReceivedData();
        tryToChangeDirection(delta);
        tryToMove(delta);
    }

    private void updateFromReceivedData(){
        Vector2 position= null;
        synchronized (this){
            if(positionChangeAvailable()){
                position = newPosition;
                newPosition = null;
            }
        }
        if(position != null)
            setPosition(position.x,position.y);
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

                if (direction.x != 0) {
                    if (Math.abs(wall.y + wall.height - position.y) < CORNER_TOLERANCE)
                        newPosition.y = wall.y + wall.height;
                    else if (Math.abs(position.y + getHeight() - wall.y) < CORNER_TOLERANCE)
                        newPosition.y = wall.y - getHeight();
                } else if (direction.y != 0) {
                    if (Math.abs(wall.x + wall.width - position.x) < CORNER_TOLERANCE)
                        newPosition.x = wall.x + wall.width;
                    else if (Math.abs(position.x + getWidth() - wall.x) < CORNER_TOLERANCE)
                        newPosition.x = wall.x - getWidth();
                }
            }
        }

        if ((noCollision || !newPosition.equals(position)) && canMoveToPosition(newPosition.add(direction.scl(distance)), newDirection)) {
            setPosition(newPosition.x, newPosition.y);
            currentDirection = newDirection;
            updateRepresentation();
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
            MovementDirection detectedDirection;
            if (Math.abs(velocityX) >= Math.abs(velocityY)) {
                // X dominated
                if (velocityX >= 0) {
                    // Right fling
                    detectedDirection = MovementDirection.RIGHT;
                } else {
                    // Left fling
                    detectedDirection = MovementDirection.LEFT;
                }
            } else {
                // Y dominated
                if (velocityY >= 0) {
                    //Down fling
                    detectedDirection = MovementDirection.DOWN;
                } else {
                    // Up fling
                    detectedDirection = MovementDirection.UP;
                }
            }
            PlayerAction action = new PlayerAction(player.getPlayerId(), getX(), getY(), detectedDirection);
            try {
                round.game.communicator.send(action);
            } catch (IOException e) {
                e.printStackTrace();
            }
            newDirection = detectedDirection;
            updateRepresentation();

            return true;
        }
    }
}
