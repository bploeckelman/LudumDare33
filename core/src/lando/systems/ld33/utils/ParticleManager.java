package lando.systems.ld33.utils;


import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.math.MathUtils;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.utils.Array;
import com.badlogic.gdx.utils.Pool;
import lando.systems.ld33.entities.EntityBase;
import lando.systems.ld33.entities.mario.Mario;

import java.util.Iterator;

/**
 * Created by dsgraham on 8/23/15.
 */
public class ParticleManager {

    private final Array<Particle> activeParticles = new Array<Particle>();
    private final Array<FireworkEmitter> activeFireworks = new Array<FireworkEmitter>();

    private final Pool<Particle> particlePool = new Pool<Particle>() {
        @Override
        protected Particle newObject() {
            return new Particle();
        }
    };

    public ParticleManager(){

    }

    public void addLargeBlood(Vector2 pos){
        for (int i = 0; i < 500; i ++){
            Particle part = particlePool.obtain();
            float speed = 1 + MathUtils.random() * 4;
            float dir = MathUtils.random(45f, 135f);
            float x = pos.x + .3f + (MathUtils.random()/3f);
            part.init(new Vector2(x, pos.y), new Vector2(MathUtils.cosDeg(dir) * speed, MathUtils.sinDeg(dir) * speed ), new Vector2(0,-2f),
                    new Color(1,0,0,1), new Color(1,0,0,.1f), .02f + (MathUtils.random() / 20f), 2);
            activeParticles.add(part);
        }
    }

    public void addFirework(Vector2 pos, float angleDeg){
        angleDeg += MathUtils.random(-20, 20);
        Vector2 v = new Vector2(MathUtils.sinDeg(angleDeg) * 3, MathUtils.cos(angleDeg) * 3);
        activeFireworks.add(new FireworkEmitter(this, pos, new Vector2(MathUtils.random(-.8f,.8f), 3)));
    }

    public void addFireworkExplotion(Vector2 pos){
        Color c = new Color(MathUtils.random(), MathUtils.random(), MathUtils.random(), 1);
        for (int i = 0; i < 500; i ++){
            Particle part = particlePool.obtain();
            float speed = MathUtils.random() * 2f;
            float dir = MathUtils.random(360);
            part.init(pos.cpy(), new Vector2(MathUtils.cosDeg(dir) * speed, MathUtils.sinDeg(dir) * speed ), new Vector2(0,-1f),
                    c, new Color(c.r,c.g,c.b,.4f), .05f, 1);
            activeParticles.add(part);
        }
    }

    public void addParticle(Vector2 pos, Vector2 vel, Vector2 accel, Color iColor, Color fColor, float size, float ttl){
        Particle part = particlePool.obtain();
        part.init(pos, vel, accel, iColor, fColor, size, ttl);
        activeParticles.add(part);
    }

    public void addBlood(Vector2 pos){
        for (int i = 0; i < 50; i ++){
            Particle part = particlePool.obtain();
            float speed = MathUtils.random() * 2;
            float dir = MathUtils.random(360f);
            float x = pos.x + .3f + (MathUtils.random()/3f);
            part.init(new Vector2(x, pos.y), new Vector2(MathUtils.sinDeg(dir) * speed, MathUtils.cosDeg(dir) * speed ), new Vector2(0,-2f),
                    new Color(1,0,0,1), new Color(1,0,0,0), .05f, 2);
            activeParticles.add(part);
        }
    }

    public void update(float dt){
        Iterator<FireworkEmitter> iterator = activeFireworks.iterator();
        while (iterator.hasNext()) {
            FireworkEmitter emiter = iterator.next();
            emiter.update(dt);
            if (emiter.dead) {
                iterator.remove();
            }
        }

        int len = activeParticles.size;
        for (int i = len -1; i >= 0; i--){
            Particle part = activeParticles.get(i);
            part.update(dt);
            if (part.timeToLive <= 0){
                activeParticles.removeIndex(i);
                particlePool.free(part);
            }
        }
    }

    public void render(SpriteBatch batch){
        for (Particle part : activeParticles){
            part.render(batch);
        }
    }
}
