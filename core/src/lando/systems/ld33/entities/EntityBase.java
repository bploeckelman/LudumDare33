package lando.systems.ld33.entities;

import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.math.Rectangle;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.utils.Array;
import lando.systems.ld33.World;

/**
 * Created by dsgraham on 8/22/15.
 */
public class EntityBase {

    protected Texture texture;
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

    enum State {
        Standing, Walking, Jumping
    }


    public EntityBase (World w, Texture tex, Rectangle r){
        tiles = new Array<Rectangle>();
        world = w;
        state = State.Standing;
        texture = tex;
        bounds = r;
        velocity = new Vector2();
    }

    public Rectangle getBounds(){
        return bounds;
    }

    public void update(float dt){

        if (dt == 0) return;


        // apply gravity if we are falling
        velocity.add(0, gravity);

        // clamp the velocity to the maximum, x-axis only
        velocity.x = Math.min(maxVelocity, Math.max(-maxVelocity, velocity.x));

        // Stop if gets slow enough
        if (Math.abs(velocity.x) < 1) {
            velocity.x = 0;
            if (grounded) state = State.Standing;
        }

        // multiply by delta time so we know how far we go
        // in this frame
        velocity.scl(dt);

        // perform collision detection & response, on each axis, separately
        // if the koala is moving right, check the tiles to the right of it's
        // right bounding box edge, otherwise check the ones to the left
        Rectangle koalaRect = world.rectPool.obtain();
        koalaRect.set(bounds);
        int startX, startY, endX, endY;
        if (velocity.x > 0) {
            startX = endX = (int)(bounds.x + bounds.width + velocity.x);
        } else {
            startX = endX = (int)(bounds.x + velocity.x);
        }
        startY = (int)(bounds.y);
        endY = (int)(bounds.y + bounds.height);
        world.getTiles(startX, startY, endX, endY, tiles);
        koalaRect.x += velocity.x;
        for (Rectangle tile : tiles) {
            if (koalaRect.overlaps(tile)) {
                velocity.x = 0;
                break;
            }
        }
        koalaRect.x = bounds.x;

        // if the koala is moving upwards, check the tiles to the top of it's
        // top bounding box edge, otherwise check the ones to the bottom
        grounded = false;
        if (velocity.y > 0) {
            startY = endY = (int)(bounds.y + bounds.height + velocity.y);
        } else {
            startY = endY = (int)(bounds.y + velocity.y);
        }
        startX = (int)(bounds.x);
        endX = (int)(bounds.x + bounds.width);
        world.getTiles(startX, startY, endX, endY, tiles);
        koalaRect.y += velocity.y;
        for (Rectangle tile : tiles) {
            if (koalaRect.overlaps(tile)) {
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
                velocity.y = 0;
                break;
            }
        }

        world.rectPool.free(koalaRect);


        bounds.x += velocity.x;
        bounds.y += velocity.y;

        velocity.scl(1 / dt);

        // TODO collide World and shift if you hit world


        velocity.x *= damping;
    }


    /**
     *
     * Called when the entitiy collides with the world
     *
     * @param rect Rectangle of intersection
     */
    protected void collisionHandler(Rectangle rect){

    }

    public void render(SpriteBatch batch){

        // TODO make this some sort of animation?
        if (facesRight) {
            batch.draw(texture, bounds.x, bounds.y, bounds.width, bounds.height);
        } else {
            batch.draw(texture, bounds.x + bounds.width, bounds.y, - bounds.width, bounds.height);
        }
    }
}
