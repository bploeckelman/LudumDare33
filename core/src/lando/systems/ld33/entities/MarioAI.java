package lando.systems.ld33.entities;

import com.badlogic.gdx.math.Rectangle;
import com.badlogic.gdx.math.Vector2;
import lando.systems.ld33.World;
import lando.systems.ld33.utils.Assets;

/**
 * Created by dsgraham on 8/22/15.
 */
public class MarioAI extends EntityBase {

    private int dir;
    private int segment;

    public MarioAI(World w, Vector2 pos) {
        super(w);
        bounds = new Rectangle(pos.x, pos.y, 1, 1);
        texture = Assets.testTexture;
        dir = 1;
        segment = 0;
    }

    public void jump(){
        if (!grounded) return;
        velocity.y = jumpVelocity;
    }

    public void update(float dt){
        super.update(dt);
        velocity.x = 8 * dir;
        switch(segment) {
            case 0:
                if (bounds.x > 25) {
                    jump();
                    segment++;
                }
                break;
            case 1:
                if (bounds.x > 31 && grounded) {
                    jump();
                    dir = -1;
                    segment++;
                }
                break;
            case 2:
                if (bounds.x < 27 && grounded){
                    bounds.x = 27;
                    segment++;
                    dir = 0;
                    jump();
                }
                break;
            case 3:
                if (grounded){
                    dir = 1;
                    if (bounds.x > 30){
                        jump();
                        segment++;
                    }
                }
        }

    }
}
