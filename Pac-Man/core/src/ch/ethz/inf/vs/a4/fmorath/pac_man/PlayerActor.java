package ch.ethz.inf.vs.a4.fmorath.pac_man;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.g2d.Animation;
import com.badlogic.gdx.graphics.g2d.Batch;
import com.badlogic.gdx.graphics.g2d.TextureAtlas;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.input.GestureDetector;
import com.badlogic.gdx.math.Rectangle;
import com.badlogic.gdx.math.Vector3;
import com.badlogic.gdx.scenes.scene2d.Actor;
import com.badlogic.gdx.scenes.scene2d.EventListener;
import com.badlogic.gdx.scenes.scene2d.InputEvent;
import com.badlogic.gdx.scenes.scene2d.utils.ActorGestureListener;

/**
 * Created by markus on 25.11.16.
 */

public class PlayerActor extends Actor{

    private TextureAtlas textureAtlas;
    private TextureRegion[] upFrames, downFrames, leftFrames, rightFrames;
    private Animation animUp, animDown, animLeft, animRight;
    private Animation currentAnimation;
    private MovementDirection currentDirection;
    private float elapsedTime;

    private float SPEED = 4.5f; // 11 = tiles per second in original pacman. 2 tiles = 2 world units. (4.5f)



    public PlayerActor(){

        // Set current direction to an initial value
        currentDirection = MovementDirection.NONE;
        elapsedTime = 0f;

        // Load textures
        textureAtlas = new TextureAtlas(Gdx.files.internal("sprites/spritesheet.atlas"));

        // Initialize animations
        rightFrames = new TextureRegion[2];
        rightFrames[0] = textureAtlas.findRegion("pacman_base");
        rightFrames[1] = textureAtlas.findRegion("pacman_right");

        leftFrames = new TextureRegion[2];
        leftFrames[0] = textureAtlas.findRegion("pacman_base");
        leftFrames[1] = textureAtlas.findRegion("pacman_left");

        upFrames = new TextureRegion[2];
        upFrames[0] = textureAtlas.findRegion("pacman_base");
        upFrames[1] = textureAtlas.findRegion("pacman_up");

        downFrames = new TextureRegion[2];
        downFrames[0] = textureAtlas.findRegion("pacman_base");
        downFrames[1] = textureAtlas.findRegion("pacman_down");

        // Set standard animations
        animUp    = new Animation(1/3f, upFrames);
        animDown  = new Animation(1/3f, downFrames);
        animLeft  = new Animation(1/3f, leftFrames);
        animRight = new Animation(1/3f, rightFrames);

        currentAnimation = animRight;

        // set Width and Heigt of the animation for later use
        this.setWidth (currentAnimation.getKeyFrame(elapsedTime, true).getRegionWidth());
        this.setHeight(currentAnimation.getKeyFrame(elapsedTime, true).getRegionHeight());

        addListener(new PlayerActorGestureListener());

        this.setPosition(0, 0);
    }


    // TODO: Implement map border and teleport through border correctly
    @Override
    public void act(float delta) {

//        System.out.println("## Loc:    X = " + this.getX() + "   Y = " + this.getY());


        // Move player coordinates according to direction and switch animation if needed

        // delta = time in seconds since the last frame.
        //this.setX((Math.abs(getX()) + delta*SPEED*16*getScaleX())%this.getStage().getCamera().viewportWidth);

        // change position depending on direction and time
        switch(currentDirection){
            case UP:
                this.setY((Math.abs(getY()) + delta*SPEED*16*getScaleY())%this.getStage().getCamera().viewportHeight);
                break;
            case DOWN:
                this.setY((Math.abs(getY()) - delta*SPEED*16*getScaleY())%this.getStage().getCamera().viewportHeight);
                break;
            case LEFT:
                this.setX((Math.abs(getX()) - delta*SPEED*16*getScaleX())%this.getStage().getCamera().viewportWidth);
                break;
            case RIGHT:
                this.setX((Math.abs(getX()) + delta*SPEED*16*getScaleX())%this.getStage().getCamera().viewportWidth);
                break;
            default:
                // Do nothing
                break;
        }

    }

    // This is a hack, but I didnt find another easy and compact way yet
    // TODO: Maybe remove this hack and implement it more correctly (e.g. in a personalized Stage class)
    @Override
    public Actor hit(float x, float y, boolean touchable) {
        return this;
        //return super.hit(x, y, touchable);
    }

    @Override
    public void draw(Batch batch, float parentAlpha) {
        elapsedTime += Gdx.graphics.getDeltaTime();
        batch.draw(currentAnimation.getKeyFrame(elapsedTime, true),
                getX(), getY(),
                this.getOriginX(), this.getOriginY(),
                this.getWidth(), getHeight(),
                this.getScaleX(), this.getScaleY(),
                this.getRotation());
    }


    // Returns the rectangle in world space (!)
    public Rectangle getRectangle(){
//        return new Rectangle(this.getX(), this.getY(), this.getWidth(), this.getHeight());
        Vector3 worldSpacePosition3 = this.getStage().getCamera().unproject(new Vector3(this.getX(), this.getY(), 0));
        return new Rectangle(worldSpacePosition3.x, worldSpacePosition3.y, this.getWidth(), this.getHeight());
    }

    public class PlayerActorGestureListener extends ActorGestureListener{

        @Override
        public void fling(InputEvent event, float velocityX, float velocityY, int button) {

            //System.out.println("### Fling event: x:" + velocityX + " y:" + velocityY);


            if(Math.abs(velocityX) >= Math.abs(velocityY)){
                // X dominated
                if (velocityX >= 0){
                    // Right fling
                    currentDirection = MovementDirection.RIGHT;
                }else{
                    // Left fling
                    currentDirection = MovementDirection.LEFT;
                }
            }else{
                // Y dominated
                if(velocityY >= 0){
                    //Down fling
                    currentDirection = MovementDirection.UP;
                }else{
                    // Up fling
                    currentDirection = MovementDirection.DOWN;
                }
            }

            // Switch current animation according to current direction
            switch (currentDirection){
                case UP:
                    currentAnimation = animUp;
                    break;
                case DOWN:
                    currentAnimation = animDown;
                    break;
                case LEFT:
                    currentAnimation = animLeft;
                    break;
                case RIGHT:
                    currentAnimation = animRight;
                    break;
                default:
                    // Dont change the current Animation
                    break;
            }

        }
    }
}
