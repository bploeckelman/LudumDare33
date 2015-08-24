package lando.systems.ld33.entities;

import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.graphics.g2d.Animation;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.math.Rectangle;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.utils.Array;
import lando.systems.ld33.World;
import lando.systems.ld33.entities.mapobjects.ObjectBase;
import lando.systems.ld33.utils.Assets;

/**
 * Created by dsgraham on 8/22/15.
 */
public class EntityBase {
    public static final float PIPEDELAY = 1.5f;

//    protected TextureRegion keyframe;
    protected Rectangle bounds;
    protected Vector2 velocity;
    protected boolean facesRight;
    protected boolean grounded;
    protected State state;
    protected float maxVelocity = 10;
    protected float jumpVelocity = 35;
    protected float damping = .8f;
    protected float gravity = -2f;
    protected World world;
    protected Array<Rectangle> tiles;
    public float moveDelay;
    public boolean dead;
    public Animation standingAnimation;
    public Animation walkingAnimation;
    public Animation jumpingAnimation;
    public Animation smashedAnimation;
    public float stateTime;
    public Thought thought;
    public boolean drawOnTop;


    enum State {
        Standing, Walking, Jumping, Smashed
    }


    public EntityBase (World w){
        tiles = new Array<Rectangle>();
        world = w;
        state = State.Standing;
//        keyframe = Assets.testTextureRegion;
        bounds = new Rectangle(3,0,1,1);
        velocity = new Vector2();
        dead = false;
        stateTime = 0;
        standingAnimation = Assets.goombaSmashedAnimation;
        walkingAnimation = Assets.goombaSmashedAnimation;
        jumpingAnimation = Assets.goombaSmashedAnimation;
        smashedAnimation = Assets.goombaSmashedAnimation;
        drawOnTop = false;
        world.gameEntities.add(this);

    }

    public Rectangle getBounds(){
        return bounds;
    }

    public void addThought(String text){
        thought = new Thought(text, bounds);
    }

    public void update(float dt){
        if (thought != null) {
            thought.update(dt);
            if (thought.timeToLive < 0) thought = null;
        }


        stateTime += dt;

        moveDelay -= dt;
        if (moveDelay > 0) return;

        if (dt == 0) return;


        // apply gravity if we are falling
        velocity.add(0, gravity);

        // clamp the velocity to the maximum, x-axis only
        velocity.x = Math.min(maxVelocity, Math.max(-maxVelocity, velocity.x));

        // Stop if gets slow enough
        if (Math.abs(velocity.x) < 1) {
            velocity.x = 0;
            state = State.Standing;
        } else {
            state = State.Walking;
        }
        if (!grounded) {
            state = State.Jumping;
        }

        // multiply by delta time so we know how far we go
        // in this frame
        velocity.scl(dt);

        // perform collision detection & response, on each axis, separately
        // if the koala is moving right, check the tiles to the right of it's
        // right bounding box edge, otherwise check the ones to the left
        Rectangle entityRect = world.rectPool.obtain();
        entityRect.set(bounds);
        int startX, startY, endX, endY;
        if (velocity.x > 0) {
            startX = endX = (int)(bounds.x + bounds.width + velocity.x);
        } else {
            startX = endX = (int)(bounds.x + velocity.x);
        }
        startY = (int)(bounds.y);
        endY = (int)(bounds.y + bounds.height);
        world.getTiles(startX, startY, endX, endY, tiles);
        entityRect.x += velocity.x;
        for (Rectangle tile : tiles) {
            if (entityRect.overlaps(tile)) {

                hitHorizontal();
                break;
            }
        }

        Array<ObjectBase> objTiles = world.getObjects();
        for (ObjectBase tile : objTiles) {
            if (entityRect.overlaps(tile.getBounds())) {

                hitHorizontal();
                break;
            }
        }
        entityRect.x = bounds.x;

        // if the koala is moving upwards, check the tiles to the top of it's
        // top bounding box edge, otherwise check the ones to the bottom
        grounded = false;
        boolean clearYVel = false;
        if (velocity.y > 0) {
            startY = endY = (int)(bounds.y + bounds.height + velocity.y);
        } else {
            startY = endY = (int)(bounds.y + velocity.y);
        }
        startX = (int)(bounds.x);
        endX = (int)(bounds.x + bounds.width);
        world.getTiles(startX, startY, endX, endY, tiles);
        entityRect.y += velocity.y;
        for (Rectangle tile : tiles) {
            if (entityRect.overlaps(tile)) {
                // we actually reset the koala y-position here
                // so it is just below/above the tile we collided with
                // this removes bouncing :)
                if (velocity.y > 0) {
                    bounds.y = tile.y - bounds.height;
                    // we hit a block jumping upwards, let's destroy it!
//                    TiledMapTileLayer layer = (TiledMapTileLayer)map.getLayers().get("walls");
//                    layer.setCell((int)tile.x, (int)tile.y, null);
                } else {
                    bounds.y = tile.y + tile.height;
                    // if we hit the ground, mark us as grounded so we can jump
                    grounded = true;
                }
                clearYVel = true;
                break;
            }
        }

        for (ObjectBase obj : objTiles){
            if (entityRect.overlaps(obj.getBounds())) {
                 if (velocity.y > 0){
                     bounds.y = obj.getBounds().y - bounds.height;
                     hitBlockFromBelow(obj);
                 } else {
                     bounds.y = obj.getBounds().y + obj.getBounds().height;
                     grounded = true;
                 }
                clearYVel = true;
                break;
            }

        }
        if (clearYVel){
            velocity.y = 0;
        }

        world.rectPool.free(entityRect);


        bounds.x += velocity.x;
        bounds.y += velocity.y;

        velocity.scl(1 / dt);

        if (bounds.x < -1 || bounds.x > world.foregroundLayer.getWidth() + 1 ||
                bounds.y < -1){
            dead = true;
        }


    }


    public void stomped(){

    }

   protected void hitHorizontal(){
       velocity.x = 0;
   }

    protected void hitBlockFromBelow(ObjectBase obj){

    }



    public void render(SpriteBatch batch){
        TextureRegion keyframe = null;
        switch(state){
            case Walking:
                keyframe = walkingAnimation.getKeyFrame(stateTime);
                break;
            case Standing:
                keyframe = standingAnimation.getKeyFrame(stateTime);
                break;
            case Jumping:
                keyframe = jumpingAnimation.getKeyFrame(stateTime);
                break;
            case Smashed:
                keyframe = smashedAnimation.getKeyFrame(stateTime);
                break;
        }


        if (facesRight) {
            batch.draw(keyframe, bounds.x, bounds.y, bounds.width, bounds.height);
        } else {
            batch.draw(keyframe, bounds.x + bounds.width, bounds.y, - bounds.width, bounds.height);
        }
    }

    public void renderUI(SpriteBatch batch, OrthographicCamera gameCam, OrthographicCamera uiCam){
        if (thought != null){
            thought.render(batch, gameCam, uiCam);
        }
    }
}
