package lando.systems.ld33.entities;

import com.badlogic.gdx.math.Rectangle;
import com.badlogic.gdx.math.Vector2;
import lando.systems.ld33.World;
import lando.systems.ld33.utils.Assets;

/**
 * Brian Ploeckelman created on 8/23/2015.
 */
public class KingHippo extends EntityBase {

    public KingHippo(World w, Vector2 p) {
        super(w);
        bounds = new Rectangle(p.x, p.y, 4, 8);
        standingAnimation = walkingAnimation = jumpingAnimation = Assets.kingHippoAnimation;
    }

}
