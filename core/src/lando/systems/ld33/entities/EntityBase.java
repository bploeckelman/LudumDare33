package lando.systems.ld33.entities;

import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.math.Rectangle;
import com.badlogic.gdx.math.Vector2;

/**
 * Created by dsgraham on 8/22/15.
 */
public class EntityBase {

    protected Texture texture;
    protected Rectangle bounds;
    protected Vector2 velocity;
    protected Vector2 acceleration;


    public EntityBase (Texture tex, Rectangle r){
        texture = tex;
        bounds = r;
        velocity = new Vector2();
        acceleration = new Vector2();
    }

    public Rectangle getBounds(){
        return bounds;
    }

    public void update(float dt){
        acceleration.y -= 10;
        if (true) {// Should be some on ground in world
            acceleration.y = 0;

        }
        velocity.y += acceleration.y;
        velocity.x += acceleration.x;

        bounds.x += velocity.x * dt;
        bounds.y += velocity.y * dt;

        // TODO collide World and shift if you hit world

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
        batch.draw(texture, bounds.x, bounds.y, bounds.width, bounds.height);
    }
}
