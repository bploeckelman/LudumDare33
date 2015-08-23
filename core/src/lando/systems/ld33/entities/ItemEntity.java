package lando.systems.ld33.entities;

import com.badlogic.gdx.math.Rectangle;
import com.badlogic.gdx.math.Vector2;
import lando.systems.ld33.World;

/**
 * Created by dsgraham on 8/22/15.
 */
public abstract class ItemEntity extends EntityBase {

    public enum ItemType {
        COIN,
        MUSHROOM,
        FIREFLOWER,
        STAR;
        // ...

        public static ItemType getType(String string) {
            if      (string.equalsIgnoreCase("coin"))       return COIN;
            else if (string.equalsIgnoreCase("mushroom"))   return MUSHROOM;
            else if (string.equalsIgnoreCase("fireflower")) return FIREFLOWER;
            else if (string.equalsIgnoreCase("star"))       return STAR;
            else return null;
        }
    }

    public ItemType type;

    public ItemEntity(World w, Vector2 p) {
        super(w);
        bounds = new Rectangle(p.x, p.y, 1, 1);
        velocity.x = 4;
    }

    public ItemEntity(World w, float px, float py) {
        super(w);
        bounds = new Rectangle(px, py, 1, 1);
        velocity.x = 4;
    }

    protected void hitHorizontal(){
        velocity.x = -velocity.x;
    }
}
