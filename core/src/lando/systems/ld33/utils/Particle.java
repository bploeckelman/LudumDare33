package lando.systems.ld33.utils;

import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.utils.Pool;

/**
 * Created by dsgraham on 8/23/15.
 */
public class Particle implements Pool.Poolable{

    Vector2 pos;
    Vector2 vel;
    Vector2 accel;
    Color initalColor;
    Color finalColor;
    float scale;
    float timeToLive;
    float totalTTL;
    TextureRegion texture;

    public Particle(){
        pos = new Vector2();
        vel = new Vector2();
        accel = new Vector2();
        initalColor = new Color();
        finalColor = new Color();
        scale = .1f;
    }

    @Override
    public void reset() {

        timeToLive = -1;
    }

    public void init(Vector2 p ,Vector2 v, Vector2 a, Color iC, Color fC, float s, float t) {
        init(p, v, a, iC, fC, s, t, new TextureRegion(Assets.whiteTexture));

    }

    public void init(Vector2 p ,Vector2 v, Vector2 a, Color iC, Color fC, float s, float t, TextureRegion tex) {
        pos = p;
        vel = v;
        accel = a;
        initalColor = iC;
        finalColor = fC;
        scale = s;
        timeToLive = t;
        totalTTL = t;
        texture = tex;
    }

    public void update(float dt){
        timeToLive -= dt;
        vel.add(accel.x * dt, accel.y * dt);
        pos.add(vel.x * dt, vel.y * dt);
    }

    public void render(SpriteBatch batch){
        batch.setColor(finalColor.cpy().lerp(initalColor, timeToLive / totalTTL));
        batch.draw(texture, pos.x, pos.y, scale, scale);
        batch.setColor(Color.WHITE);
    }
}
