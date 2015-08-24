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

        //Lets keep it simple, only do smart things on the ground
        if (!grounded || moveDelay > 0) return;

        // It's a pit
        int startX = (int)(bounds.x +.5f);
        int endX = (int)(bounds.x + .5f + dir);
        int startY = (int)bounds.y -1;
        int endY = startY + 2;
        world.getTiles(startX, startY, endX, endY, tiles);
        if (tiles.size == 0) {
                dir *= -1;
        }
    }
}
