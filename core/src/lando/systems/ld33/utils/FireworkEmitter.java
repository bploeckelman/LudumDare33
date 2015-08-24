package lando.systems.ld33.utils;

import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.math.Vector2;

/**
 * Created by dsgraham on 8/24/15.
 */
public class FireworkEmitter {

    Vector2 pos;
    Vector2 vel;
    ParticleManager mgr;
    float emitterDelay;
    boolean dead;


    public FireworkEmitter(ParticleManager m, Vector2 p, Vector2 v){
        mgr = m;
        pos = p;
        vel = v;
        dead = false;
        emitterDelay = .3f;
    }

    public void update(float dt){

        vel.add(0, -2 * dt);
        pos.add(vel.x * dt, vel.y * dt);

        emitterDelay -= dt;
        if (emitterDelay <= 0) {
            emitterDelay = .01f;
            mgr.addParticle(pos.cpy(), new Vector2(), new Vector2(), new Color(1,1,0,1), new Color(1, 0, 0, .5f), .05f, .5f);
        }

        if (vel.y <= 0) {
            mgr.addFireworkExplotion(pos);
            dead = true;
        }


    }

}
