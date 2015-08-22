package lando.systems.ld33.entities;

import com.badlogic.gdx.math.Rectangle;
import com.badlogic.gdx.math.Vector2;
import lando.systems.ld33.World;

/**
 * Created by dsgraham on 8/22/15.
 */
public class ItemEntity extends EntityBase {
    public ItemEntity(World w, Vector2 p) {
        super(w);
        bounds = new Rectangle(p.x, p.y, 1, 1);
        velocity.x = 4;
    }

    protected void hitHorizontal(){
        velocity.x = -velocity.x;
    }
}
