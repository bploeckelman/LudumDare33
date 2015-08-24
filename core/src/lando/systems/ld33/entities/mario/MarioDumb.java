package lando.systems.ld33.entities.mario;

import com.badlogic.gdx.math.MathUtils;
import com.badlogic.gdx.math.Vector2;
import lando.systems.ld33.World;

/**
 * Created by dsgraham on 8/23/15.
 */
public class MarioDumb extends Mario {

    boolean isJumper;

    public MarioDumb(World w, Vector2 pos) {
        this(w, pos, false);
    }

    public MarioDumb(World w, Vector2 pos, boolean large) {
        super(w, pos);
        isJumper = MathUtils.random(100) < 25;
        if(large) {
            growBig();
        }
    }

    public void update(float dt){
        super.update(dt);

        if (isJumper && MathUtils.random(1000) < 2 && grounded) {
            jump();
        }
        if (MathUtils.random(1000) < 10 && grounded) {
            dir *= -1;
        }
    }
}
