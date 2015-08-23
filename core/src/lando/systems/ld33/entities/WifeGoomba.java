package lando.systems.ld33.entities;

import com.badlogic.gdx.math.Rectangle;
import com.badlogic.gdx.math.Vector2;
import lando.systems.ld33.World;
import lando.systems.ld33.utils.Assets;

/**
 * Created by dsgraham on 8/23/15.
 */
public class WifeGoomba extends EntityBase {
    public WifeGoomba(World w, Vector2 p) {
        super(w);

        bounds = new Rectangle(p.x, p.y, 1, 1);
        standingAnimation = walkingAnimation = jumpingAnimation = Assets.goombaWifeAnimation;
    }

}
