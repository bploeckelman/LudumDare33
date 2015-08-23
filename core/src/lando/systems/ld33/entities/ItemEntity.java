package lando.systems.ld33.entities;

import aurelienribon.tweenengine.Tween;
import aurelienribon.tweenengine.equations.Linear;
import com.badlogic.gdx.math.Rectangle;
import com.badlogic.gdx.math.Vector2;
import lando.systems.ld33.LudumDare33;
import lando.systems.ld33.World;
import lando.systems.ld33.accessors.RectangleAccessor;

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
        Tween.to(bounds, RectangleAccessor.Y, ITEMDELAY)
                .target(py + 1.1f)
                .ease(Linear.INOUT)
                .start(LudumDare33.tween);
        moveDelay = ITEMDELAY+ .1f;
        velocity.x = 4;
    }

    protected void hitHorizontal(){
        velocity.x = -velocity.x;
    }
}
