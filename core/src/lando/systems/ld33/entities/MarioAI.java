package lando.systems.ld33.entities;

import com.badlogic.gdx.graphics.g2d.Animation;
import com.badlogic.gdx.math.Intersector;
import com.badlogic.gdx.math.Rectangle;
import com.badlogic.gdx.math.Vector2;
import lando.systems.ld33.World;
import lando.systems.ld33.utils.Assets;

/**
 * Created by dsgraham on 8/22/15.
 */
public class MarioAI extends Mario {


    private int segment;
    float delay;

    public MarioAI(World w, Vector2 pos) {
        super(w, pos);
        segment = 0;
    }

    public void update(float dt){
        super.update(dt);

        switch (world.phase){
            case First:
                handlePhase1(dt);
                break;
        }


    }

    public void handlePhase1(float dt){
        switch(segment) {
            case 0:
                if (bounds.x > 20 && bounds.x < 21) {
                    jump();
                }
                if (bounds.x > 25.5f) {
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
                    //delay = 1f;
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
    }
}
