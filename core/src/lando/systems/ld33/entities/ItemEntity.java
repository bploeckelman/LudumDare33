package lando.systems.ld33.entities;

import com.badlogic.gdx.math.Rectangle;
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
    public static final float ITEMDELAY = .5f;


    public ItemEntity(World w, float px, float py) {
        super(w);
        bounds = new Rectangle(px, py, 1, 1);
    }

    protected void hitHorizontal() {}

}
