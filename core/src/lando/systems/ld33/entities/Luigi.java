package lando.systems.ld33.entities;

import com.badlogic.gdx.math.Rectangle;
import com.badlogic.gdx.math.Vector2;
import lando.systems.ld33.World;
import lando.systems.ld33.utils.Assets;

/**
 * Brian Ploeckelman created on 8/23/2015.
 */
public class Luigi extends ChanterBase {

    public Luigi(World w, Vector2 p) {
        super(w);
        bounds = new Rectangle(p.x, p.y, 2, 5);
        standingAnimation = walkingAnimation = jumpingAnimation = Assets.luigiAnimation;
    }

}
