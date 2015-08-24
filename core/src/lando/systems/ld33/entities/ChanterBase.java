package lando.systems.ld33.entities;

import com.badlogic.gdx.math.Vector2;
import lando.systems.ld33.World;

/**
 * Created by dsgraham on 8/24/15.
 */
public class ChanterBase extends EntityBase {

    boolean chanting;

    public ChanterBase(World w) {
        super(w);
        chanting = true;
    }

    public void update(float dt){
        if (thought != null) {
            thought.update(dt);
            if (thought.timeToLive < 0) thought = null;
        }

        if (chanting) stateTime += dt;
        else stateTime = 0;
    }

    public void chant(boolean c){
        chanting = c;
    }
}
