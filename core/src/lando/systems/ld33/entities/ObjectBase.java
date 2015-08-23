package lando.systems.ld33.entities;

import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.math.Rectangle;
import lando.systems.ld33.World;
import lando.systems.ld33.utils.Assets;
import org.w3c.dom.css.Rect;

/**
 * Brian Ploeckelman created on 8/22/2015.
 */
public abstract class ObjectBase {

    World         world;
    TextureRegion keyframe;
    Rectangle     bounds;

    public ObjectBase(World world, Rectangle bounds) {
        this.world = world;
        this.keyframe = new TextureRegion(Assets.testTexture);
        this.bounds = bounds;
    }

    public abstract void update(float delta);

    public abstract void hit();

    public void render(SpriteBatch batch) {
        batch.draw(keyframe, bounds.x, bounds.y, bounds.width, bounds.height);
    }

    public Rectangle getBounds() { return bounds; }
    public TextureRegion getKeyframe() { return keyframe; }

}
