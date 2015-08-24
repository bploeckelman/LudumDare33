package lando.systems.ld33.entities;

import com.badlogic.gdx.math.MathUtils;
import com.badlogic.gdx.math.Vector2;
import lando.systems.ld33.World;

/**
 * Created by dsgraham on 8/23/15.
 */
public class MarioDumb extends Mario {
    public MarioDumb(World w, Vector2 pos) {
        super(w, pos);
    }

    public void update(float dt){
        super.update(dt);

        if (MathUtils.random(1000) < 2 && grounded)
            jump();


    }
}
