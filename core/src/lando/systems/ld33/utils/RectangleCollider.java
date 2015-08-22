package lando.systems.ld33.utils;

import com.badlogic.gdx.math.Rectangle;

/**
 * Created by dsgraham on 8/22/15.
 */
public class RectangleCollider {


    /**
     * @param r1
     * @param r2
     * @return a Rectangle showing the intersection or null if they don't intersect
     */
    public static Rectangle findIntersection(Rectangle r1, Rectangle r2){
        if (r1.overlaps(r2)){
            Rectangle i = new Rectangle();
            i.x = Math.max(r1.x, r2.x);
            i.y = Math.max(r1.y, r2.y);
            i.width = Math.min(r1.x + r1.width, r2.x + r2.width) - i.x;
            i.height = Math.min(r1.y + r1.height, r2.y + r2.height) - i.y;

            return i;
        }
        return null;
    }
}
