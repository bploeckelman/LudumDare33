package lando.systems.ld33.entities;

import com.badlogic.gdx.graphics.g2d.Animation;
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
    private boolean skipMushroom;
    private float delay;

    Animation animation;
    float stateTime;

    public MarioAI(World w, Vector2 pos, boolean skipMushroom) {
        super(w);
        this.skipMushroom = skipMushroom;
        bounds = new Rectangle(pos.x, pos.y, 1, 1);
        animation = Assets.marioSmallWalkAnimation;
        dir = 1;
        segment = 0;
    }

    public void jump(){
        if (!grounded) return;
        velocity.y = jumpVelocity;
    }

    protected void hitBlockFromBelow(ObjectBase obj){
        obj.hit();
    }

    public void update(float dt){
        super.update(dt);
        velocity.x = 8 * dir;

        stateTime += dt;
        keyframe = animation.getKeyFrame(stateTime);

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
                    if (skipMushroom) segment++;
                    segment++;
                    dir = 0;
                    jump();
                    delay = 1f;
                }
                break;
            case 3:
                if (grounded){
                    delay -=dt;
                    if (delay < 0) segment++;

                }
                break;
            case 4:
                if (grounded){
                    dir = 1;
                    if (bounds.x > 30){
                        jump();
                        segment++;
                    }
                }
                break;

        }
        velocity.x = 8 * dir;

        facesRight = dir > 0;
    }
}
