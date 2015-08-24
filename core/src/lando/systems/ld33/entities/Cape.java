package lando.systems.ld33.entities;

import com.badlogic.gdx.math.Rectangle;
import com.badlogic.gdx.math.Vector2;
import lando.systems.ld33.World;
import lando.systems.ld33.utils.Assets;

/**
 * Created by dsgraham on 8/24/15.
 */
public class Cape extends EntityBase {
    public Cape(World w, Vector2 p) {
        super(w);
        bounds = new Rectangle(p.x, p.y, 1, 1);
        standingAnimation = jumpingAnimation = smashedAnimation = walkingAnimation = Assets.goombaCloakEmpty;
    }
}
